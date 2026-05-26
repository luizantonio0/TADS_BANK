import express from 'express';
import { createProxyMiddleware } from 'http-proxy-middleware';
import { match } from 'path-to-regexp';
import { services, routes } from './routes.js';
import cors from 'cors';

import { handleConsultaClientesGerente } from './compositions/gerenteClientesConsultaComposition.js';
import { handleGerenteDashboard } from './compositions/gerenteDashboardComposition.js';
import { handleMelhoresClientes } from './compositions/melhoresClientesComposition.js';
import { handleConsultaCliente } from './compositions/clienteComposition.js';

const app = express();
const PORT = 3000;

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
            return res.status(401).json("Você não tem permissão para performar essa ação.");
        }
    }

    if(targetRoute.name == "buscarGerentes" && req.query.numero == "dashboard") {
        return await handleGerenteDashboard(res, claims);
    }

    if(targetRoute.name == "buscarClientes" && req.query.filtro == "melhores_clientes") {
        return await handleMelhoresClientes(res, claims);
    }

    if(targetRoute.name == "buscarClientes" && !req.query.filtro) {
        return await handleConsultaClientesGerente(res, claims);
    }

    if(targetRoute.name == "buscarCliente") {
        let cpf = parsedRoute.params.cpf;
        return await handleConsultaCliente(res, claims, cpf);
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
            }
        }
    })(req, res, next);
});

app.listen(PORT, () => {
    console.log(`API Gateway rodando em http://localhost:${PORT}`);
});
