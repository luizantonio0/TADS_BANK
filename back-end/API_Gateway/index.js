import express from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import { match } from 'path-to-regexp';
import { services, routes } from './routes.js';
import cors from 'cors';

import { handleConsultaClientesGerente } from './compositions/gerenteClientesConsultaComposition.js';
import { handleGerenteDashboard } from './compositions/gerenteDashboardComposition.js';
import { handleMelhoresClientes } from './compositions/melhoresClientesComposition.js';
import { handleConsultaCliente } from './compositions/clienteComposition.js';
import { handleExtratoFull } from './compositions/extratoComposition.js';
import { handleReboot } from './compositions/rebootComposition.js';
import { handleRelatorioCliente } from './compositions/clienteRelatorioComposition.js';
import { handleAtualizarCliente } from './compositions/atualizarClienteComposition.js';

const app = express();
const PORT = 3000;

app.use(express.json());

app.use(cors({
    origin: 'http://localhost:4200',
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization']
}));

app.use(async (req, res, next) => {
    if (req.method === 'OPTIONS') {
        return res.sendStatus(204);
    }

    let targetRoute = null;
    let parsedRoute = null;

    for (const route of routes) {
        if(req.method !== route.method) {
            continue;
        }
        let checker = match(route.path, { decode: decodeURIComponent });
        parsedRoute = checker(req.path);

        if (parsedRoute) {
            targetRoute = route;
            break;
        }
    }

    if (!targetRoute || !parsedRoute) {
        return res.status(404).json({ error: "Rota não definida" });
    }

    let claims;
    if (!targetRoute.public) {
        const authHeader = req.headers['authorization'];
        if (!authHeader) {
           return res.status(401).json({ error: "Faça login para continuar!" });
        }
        const authResp = await fetch(services.auth + "/auth/validate", {
            method: 'POST',
            headers: {
                'Authorization': authHeader,
            }
        });
        if(authResp.status < 200 || authResp.status >= 300) {
            return res.status(authResp.status).json({ error: "Faça login para continuar." });
        }

        claims = await authResp.json();
        if(claims.error) {
            return res.status(authResp.status).json({error: claims.error})
        }
        if(targetRoute.profiles !== "*" && !targetRoute.profiles.toUpperCase().split(",").includes(claims.profile.toUpperCase())) {
            return res.status(403).json("Você não tem permissão para performar essa ação.");
        }
    }

    console.log(targetRoute.name + ":" + JSON.stringify(req.query) + ":" + (claims ? claims.cpf : ''))

    if(targetRoute.name == "reboot") {
        return await handleReboot(res);
    }

    if(targetRoute.name == "criarGerente") {
        if(claims.profile != "ADMINISTRADOR") return res.status(403).json({ error: "Você não tem permissão para isso." });

        const menoresSaldosResp = await fetch(services.contas + `/contas/relation/saldoPositivo`, {
            method: 'GET',
            headers: {
                'X-User-Id': claims.cpf,
                'X-User-Profile': claims.profile
            }
        })
        if (!menoresSaldosResp.ok) return res.status(menoresSaldosResp.status).json(menoresSaldosResp.body);
        const saldosPositivos = await menoresSaldosResp.json();
        req.body.saldos_positivos = saldosPositivos;
    }

    if(targetRoute.name == "atualizarCliente") {
        let cpf = parsedRoute.params.cpf;
        return handleAtualizarCliente(req, res, claims, cpf);
    }

    if(targetRoute.name == "buscarGerentes" && req.query.filtro == "dashboard") {
        return await handleGerenteDashboard(res, claims);
    }

    if(targetRoute.name == "buscarClientes" && req.query.filtro == "melhores_clientes") {
        return await handleMelhoresClientes(res, claims);
    }

    if(targetRoute.name == "buscarClientes" && req.query.filtro == "adm_relatorio_clientes") {
        return await handleRelatorioCliente(res, claims);
    }

    if(targetRoute.name == "buscarClientes" && !req.query.filtro) {
        return await handleConsultaClientesGerente(req, res, claims);
    }

    if(targetRoute.name == "buscarCliente" && !req.query.include) {
        let cpf = parsedRoute.params.cpf;
        return await handleConsultaCliente(res, claims, cpf);
    }

    if(targetRoute.name == "extrato" && req.query.include == "all") {
        let conta = parsedRoute.params.conta;
        return await handleExtratoFull(res, claims, conta, req.query);
    }

    return createProxyMiddleware({
        target: targetRoute.target,
        changeOrigin: true,
        pathRewrite: (path) => {
            if (path === '/login' || path === '/logout') return `/auth${path}`;
            return path;
        },
        on: {
            proxyReq: (proxyReq, req, res) => {
                if (claims) {
                    proxyReq.setHeader("X-User-Id", claims.cpf);
                    proxyReq.setHeader("X-User-Profile", claims.profile);
                }
                if (req.body && ['POST', 'PUT', 'PATCH'].includes(req.method)) {
                    const bodyData = JSON.stringify(req.body);
                    proxyReq.setHeader('Content-Type', 'application/json');
                    proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
                    proxyReq.write(bodyData);
                }
            }
        }
    })(req, res, next);
});

app.listen(PORT, () => {
    console.log(`API Gateway rodando em http://localhost:${PORT}`);
});
