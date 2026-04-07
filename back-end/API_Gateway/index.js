//require('dotenv').config();
const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const cors = require('cors');
const app = express();
const PORT = 3000;

app.use(cors({
    origin: 'http://localhost:4200',
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization']
}));



// Original
// const services = {
//     ms_auth: "http://auth-ms:8055",
// };


//Mockado
const services = {
    ms_auth: "http://localhost:3001",
    clientes: "http://localhost:3001",
    contas: "http://localhost:3001",
    gerentes: "http://localhost:3001"
};

/*

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
*/

app.use(createProxyMiddleware({
    pathFilter: '/clientes',
    target: services.clientes,
    changeOrigin: true
}));

app.use(createProxyMiddleware({
    pathFilter: '/logout',
    target: services.ms_auth,
    changeOrigin: true
}));

app.use(createProxyMiddleware({
    pathFilter: '/gerentes',
    target: services.gerentes,
    changeOrigin: true
}));

app.use(createProxyMiddleware({
    pathFilter: '/contas',
    target: services.contas,
    changeOrigin: true
}));

app.use(createProxyMiddleware({
    pathFilter: '/login',
    target: services.ms_auth,
    changeOrigin: true
}));

// app.use(createProxyMiddleware({
//     pathFilter: '/ms_auth',
//     target: services.ms_auth,
//     changeOrigin: true,
//     pathRewrite: { '^/ms_auth': '' }
// }));


app.listen(PORT, () => {
    console.log(`API Gateway rodando em http://localhost:${PORT}`);
});