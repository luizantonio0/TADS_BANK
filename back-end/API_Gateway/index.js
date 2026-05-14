//require('dotenv').config();
const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const { match } = require('path-to-regexp');
const { services, routeMappings } = require("./routes")
const cors = require('cors');
const app = express();
const PORT = 3000;

app.use(cors({
    origin: 'http://localhost:4200',
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization']
}));

app.use(async (req, res, next) => {
    let targetRoute = null;

    for (const pattern in routeMappings) {
        const checker = match(pattern, { decode: decodeURIComponent });
        const result = checker(req.path);

        if (result) {
            targetRoute = routeMappings[pattern];
            break;
        }
    }

    if (!targetRoute) {
        return res.status(404).json({ error: "Rota não definida" });
    }

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
        if(authResp.status < 200 || authResp >= 300) {
            return res.status(authResp.status).json("Algo deu errado. Tente novamente mais tarde.");
        }
        const claims = await response.json();
        if(targetRoute.profiles !== "*" && !targetRoute.toUpperCase().split(",").includes(claims.profile.toUpperCase())) {
            return res.status(401).json("Você não tem permissão para performar essa ação.");
        }
    }

    return createProxyMiddleware({
        target: targetRoute.target,
        changeOrigin: true,
        pathRewrite: (path, req) => {
            if (path === '/login' || path === '/logout') return `/auth${path}`;
            return path;
        },
        onProxyReq: (proxyReq, req, res) => {
            if (req.body && Object.keys(req.body).length) {
                const bodyData = JSON.stringify(req.body);
                proxyReq.setHeader('Content-Type', 'application/json');
                proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData));
                proxyReq.write(bodyData);
            }
        }
    })(req, res, next);
});

app.listen(PORT, () => {
    console.log(`API Gateway rodando em http://localhost:${PORT}`);
});