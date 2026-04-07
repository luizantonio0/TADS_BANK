const fs = require('fs');
const path = require('path');
const express = require('express');
const cors = require('cors');
const app = express();
const porta = 3001;

app.use(cors({
    origin: '*',
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization']
}));

let dados;

try {
    const dadosBrutos = fs.readFileSync(path.join(__dirname, 'mock.json'), 'utf8');
    dados = JSON.parse(dadosBrutos);
} catch (erro) {
    console.error("Erro ao ler o arquivo:", erro);
}



app.get('/clientes', (req, res) => {
    return res.status(200).json(
        dados.GET.clientes
    )
})
app.get('/clientes/:cpf', (req, res) => {
    return res.status(200).json(
        dados.GET.clientes
    )
})

app.post('/clientes', (req, res) => {
    return res.status(200).json(
        dados.POST.clientes.default
    )
})
app.post('/clientes/:cpf/aprovar', (req, res) => {
    return res.status(200).json(
        dados.POST.clientes.aprovar
    )
})

app.post('/clientes/:cpf/rejeitar', (req, res) => {
    return res.status(200).json(
        dados.POST.clientes.rejeitar
    )
})

app.put('/clientes', (req, res) => {
    return res.status(200).json(
        dados.PUT.clientes
    )
})

// =============================== CONTAS =================================

app.get('/contas/:numero', (req, res) => {
    return res.status(200).json(
        dados.GET.contas.default
    )
})
app.get('/contas/:numero/saldo', (req, res) => {
    return res.status(200).json(
        dados.GET.contas.default
    )
})
app.get('/contas/:numero/extrato', (req, res) => {
    return res.status(200).json(
        dados.GET.contas.extrato
    )
})

app.post('/contas/:numero/depositar', (req, res) => {
    return res.status(200).json(
        dados.POST.contas.depositar
    )
})

app.post('/contas/:numero/sacar', (req, res) => {
    return res.status(200).json(
        dados.POST.contas.sacar
    )
})

app.post('/contas/:numero/transferir', (req, res) => {
    return res.status(200).json(
        dados.POST.contas.transferir
    )
})

// ========================= GERENTE =========================

app.get('/gerentes', (req, res) => {
    return res.status(200).json(
        dados.GET.gerentes
    )
})
app.get('/gerentes/:cpf', (req, res) => {
    return res.status(200).json(
        dados.GET.gerentes
    )
})

app.post('/gerentes', (req, res) => {
    return res.status(200).json(
        dados.POST.gerentes
    )
})
app.put('/gerentes/:cpf', (req, res) => {
    return res.status(200).json(
        dados.PUT.gerentes
    )
})

app.delete('/gerentes/:cpf', (req, res) => {
    return res.status(200).json(
        dados.DELETE.gerentes
    )
})

// ========================= LOGIN =======================================

app.post('/login', (req, res) => {
    return res.status(200).json(
        dados.POST.login
    )
})

app.post('/logout', (req, res) => {
    return res.status(200).json(
        dados.POST.logout
    )
})


app.listen(porta, () => console.log(`Servidor rodando em http://localhost:${porta}`));
