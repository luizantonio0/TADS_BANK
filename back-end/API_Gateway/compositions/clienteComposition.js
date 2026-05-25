import { services } from "../routes.js";

export async function handleConsultaCliente(res, claims, cpf) {

    let config = {
        method: 'GET',
        headers: {
            'X-User-Id': claims.cpf,
            'X-User-Profile': claims.profile
        }
    }

    if(claims.profile == "CLIENTE" && cpf != claims.cpf) return res.status(403).json({ error: "Você não tem permissão para isso." });

    const clienteResp = await fetch(services.clientes + `/clientes/${claims.cpf}`, config)
    if (!clienteResp.ok) return res.status(clienteResp.status).json(clienteResp.body);
    const clientes = await clienteResp.json();

    const contaResp = await fetch(services.contas + `/contas/cliente/${claims.cpf}`, config)
    if (!contasResp.ok) return res.status(contasResp.status).json(contasResp.body);
    const contas = (await contasResp.json());

    for(var cliente of clientes) {
        cliente.saldo = contas[cliente.cpf].saldo;
        cliente.limite = contas[cliente.cpf].limite;
    }

    return res.status(200).json(clientes);

}