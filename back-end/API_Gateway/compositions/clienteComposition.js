import { services } from "../routes.js";

export async function handleConsultaCliente(res, claims, cpf) {
    console.log(cpf)
    let config = {
        method: 'GET',
        headers: {
            'X-User-Id': claims.cpf,
            'X-User-Profile': claims.profile
        }
    }

    if(claims.profile == "CLIENTE" && cpf != claims.cpf) return res.status(403).json({ error: "Você não tem permissão para isso." });

    const clienteResp = await fetch(services.clientes + `/clientes/${cpf}`, config)
    if (!clienteResp.ok) return res.status(clienteResp.status).json(clienteResp.body);
    const cliente = await clienteResp.json();
 
    const contaResp = await fetch(services.contas + `/contas/cliente/${cpf}`, config)
    if (!contaResp.ok) return res.status(contaResp.status).json(contaResp.body);
    const conta = (await contaResp.json());

    const gerenteResp = await fetch(services.gerentes + `/gerentes/${cliente.gerente}`, config)
    if (!gerenteResp.ok) return res.status(gerenteResp.status).json(gerenteResp.body);
    const gerente = (await gerenteResp.json());

    cliente.saldo = conta.saldo;
    cliente.limite = conta.limite;
    cliente.conta = conta.conta;
    cliente.gerente_nome = gerente.nome;
    cliente.gerente_email = gerente.email;

    return res.status(200).json(cliente);

}