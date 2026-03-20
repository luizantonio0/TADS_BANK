require('dotenv').config();
const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();
const PORT = 3000;

const services = {
    ms_auth: "http://auth-ms:8055",
    ms_cliente: "http://localhost:8080"
};

const publicRoutes = ['/ms_auth/auth/login', '/ms_auth/auth/validate'];

app.use(async (req, res, next) => {

    console.log(req.path)

    if (publicRoutes.some(route => req.path.startsWith(route))) {
        return next();
    }

    if (req.path.startsWith('/ms_auth')) {
        return next();
    }

    try {
        const token = req.headers['authorization'];
        if (!token) return res.status(401).json({ error: "Token ausente." });

        const authResp = await fetch(`${services.ms_auth}/auth/validate`, {
            method: 'GET',
            headers: {
                'Authorization': token,
            }
        });

        if (!authResp.ok) {
            return res.status(401).json({ error: "Faça login novamente." });
        }
        
        next();
    } catch (err) {
        console.error("Erro no Gateway:", err.message);
        res.status(500).json({ error: "Serviço de autenticação indisponível." });
    }
});

app.use('/ms_auth', createProxyMiddleware({
    target: services.ms_auth,
    changeOrigin: true,
    pathRewrite: { '^/ms_auth': '' }
}));

app.use('/ms_cliente', createProxyMiddleware({
    target: services.ms_cliente,
    changeOrigin: true,
    pathRewrite: { '^/ms_cliente': '' }
}));


app.listen(PORT, () => {
    console.log(`API Gateway rodando em http://localhost:${PORT}`);
});