import { services } from "../routes.js";

export async function handleConsultaCliente(res, claims, cpf) {

    let config = {
        method: 'GET',
        headers: {
            'X-User-Id': claims.cpf,
            'X-User-Profile': claims.profile
        }
    }

    if(!!cpf && claims.profile == "CLIENTE" && cpf != claims.cpf) return res.status(403).json({ error: "Você não tem permissão para isso." });

    console.log(1)

    const clienteResp = await fetch(services.clientes + `/clientes/${cpf}`, config)
    if (!clienteResp.ok) return res.status(clienteResp.status).json(clienteResp.body);
    const cliente = await clienteResp.json();
console.log(2)
    const contaResp = await fetch(services.contas + `/contas/cpf/${cpf}`, config)
    if (!contaResp.ok) {
        const error = await contaResp.json().catch(() => ({ error: "Erro ao buscar conta" }));
        return res.status(contaResp.status).json(error);
    }
    const conta = await contaResp.json();
console.log(3)
    const gerenteResp = await fetch(services.gerentes + `/gerentes/${cliente.gerente}`, config)
    if (gerenteResp.ok) {
        const gerente = await gerenteResp.json();
        cliente.gerente_nome = gerente.nome;
        cliente.gerente_email = gerente.email;
    }
    console.log(4)

    cliente.conta = conta.conta;
    cliente.saldo = conta.saldo;
    cliente.limite = conta.limite;

    return res.status(200).json(cliente);

}