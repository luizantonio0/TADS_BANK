const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();
const PORT = 3000;

// Configuração dos endpoints dos seus microsserviços
const services = {
    ms_cliente: 'http://localhost:8080',
};

// Middleware de autenticação
app.use((req, res, next) => {
    const token = req.headers['authorization'];
    if (!token) {
        return res.status(401).json({ message: 'Token não fornecido!' });
    }
    next();
});

// 2. Roteamento para Microsserviço de Usuários
app.use('/ms_cliente', createProxyMiddleware({
    target: services.ms_cliente,
    changeOrigin: true,
    // Remove o / ususarios
    pathRewrite: { '^/ms_cliente': '' }
}));


app.listen(PORT, () => {
    console.log(`API Gateway rodando em http://localhost:${PORT}`);
});