import { services } from "../routes.js";

export async function handleConsultaClientesGerente(res, claims) {

    let config = {
        method: 'GET',
        headers: {
            'X-User-Id': claims.cpf,
            'X-User-Profile': claims.profile
        }
    }

    const clientesResp = await fetch(services.clientes + `/clientes/gerente/${claims.cpf}`, config)
    if (!clientesResp.ok) return res.status(clientesResp.status).json(clientesResp.body);
    const clientes = await clientesResp.json();

    const contasResp = await fetch(services.contas + `/contas/relation?gerentes=${claims.cpf}`, config)
    if (!contasResp.ok) return res.status(contasResp.status).json(contasResp.body);
    const contas = (await contasResp.json());

    for(var cliente of clientes) {
        cliente.saldo = contas[cliente.cpf].saldo;
        cliente.limite = contas[cliente.cpf].limite;
    }

    return res.status(200).json(clientes);

}