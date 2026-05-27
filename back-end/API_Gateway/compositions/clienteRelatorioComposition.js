import { services } from "../routes.js";

export async function handleRelatorioCliente(res, claims) {

    let config = {
        method: 'GET',
        headers: {
            'X-User-Id': claims.cpf,
            'X-User-Profile': claims.profile
        }
    }

    if(claims.profile != "ADMINISTRADOR") return res.status(403).json({ error: "Você não tem permissão para isso." });

    const clienteResp = await fetch(services.clientes + `/clientes`, config)
    if (!clienteResp.ok) return res.status(clienteResp.status).json(clienteResp.body);
    const clientes = await clienteResp.json()

    const contaResp = await fetch(services.contas + `/contas`, config)
    if (!contaResp.ok) {
        const error = await contaResp.json().catch(() => ({ error: "Erro ao buscar conta" }));
        return res.status(contaResp.status).json(error);
    }
    const contas = (await contaResp.json()).reduce((map, conta) => {
        map.set(conta.cpf, conta); 
        return map;
    }, new Map());

    const gerentesResp = await fetch(services.gerentes + `/gerentes`, config)
    if (!gerentesResp.ok) {
        const error = await gerentesResp.json().catch(() => ({ error: "Erro ao buscar conta" }));
        return res.status(gerentesResp.status).json(error);
    }

    const gerentes = (await gerentesResp.json()).reduce((map, gerente) => {
        map.set(gerente.cpf, gerente); 
        return map;
    }, new Map());

    for(let cliente of clientes) {
        let conta = contas.get(cliente.cpf);
        let gerente = gerentes.get(cliente.gerente);
        cliente.conta = !conta ? '' : conta.conta;
        cliente.saldo = !conta ? 0 : conta.saldo;
        cliente.limite = !conta ? 0 : conta.limite;
        cliente.gerente_nome = gerente.nome;
        cliente.gerente_email = gerente.email;
    }
    
    return res.status(200).json(clientes);

}