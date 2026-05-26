import { services } from "../routes.js";

export async function handleMelhoresClientes(res, claims) {

    let config = {
        method: 'GET',
        headers: {
            'X-User-Id': claims.cpf,
            'X-User-Profile': claims.profile
        }
    }

    const contasResp = await fetch(services.contas + "/contas?filtro=melhores_clientes", config)
    if (!contasResp.ok) return res.status(contasResp.status).json(contasResp.body);
    const contas = await contasResp.json();

    let response = []

    for (var conta of contas) {
        const clienteResp = await fetch(services.clientes + `/clientes/${conta.cpf}`, config)
        if (!clienteResp.ok) return res.status(clienteResp.status).json(clienteResp.body);
        const cliente = await clienteResp.json();
        
        cliente.saldo = conta.saldo;
        cliente.limite = conta.limite;

        response.push(cliente)
    }

    return res.status(200).json(response);

}

