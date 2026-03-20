require('dotenv').config();
const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();
const PORT = 3000;

const services = {
    ms_auth: "http://auth-ms:8055",
    ms_cliente: "http://localhost:8080"
};

app.use(async (req, res, next) => {
    if (req.path.startsWith('/ms_auth')) {
        return next();
    }
    try {
        const token = req.headers['authorization'];
        const authResp = await fetch(`${services.ms_auth}/auth/validate`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token,
            }
        });
        if (!authResp.ok) {
            res.status(401).json({
                error: "Faça login novamente."
            });
            return;
        }
        return next();
    } catch (err) {
        console.error(err);
        res.status(500).json({
            error: "Algo deu errado. Tente novamente mais tarde."
        });
    }
});

app.use('/ms_cliente', createProxyMiddleware({
    target: services.ms_cliente,
    changeOrigin: true,
    pathRewrite: { '^/ms_cliente': '' }
}));

app.use('/ms_auth', createProxyMiddleware({
    target: services.ms_auth,
    changeOrigin: true,
    pathRewrite: { '^/ms_auth': '' }
}));


app.listen(PORT, () => {
    console.log(`API Gateway rodando em http://gateway:${PORT}`);
});