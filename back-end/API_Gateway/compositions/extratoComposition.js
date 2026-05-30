import { services } from "../routes.js";

export async function handleExtratoFull(res, claims, conta, queryParams) {

    let config = {
        method: 'GET',
        headers: {
            'X-User-Id': claims.cpf,
            'X-User-Profile': claims.profile
        }
    }

    const { inicio, fim } = queryParams;

    const extratoResp = await fetch(services.contas + `/contas/${conta}/extrato?include=all${inicio ? "&inicio=" + inicio : ""}${fim ? "&fim=" + fim : ""}`, config)
    if (!extratoResp.ok) return res.status(extratoResp.status).json(extratoResp.body);
    const extrato = await extratoResp.json();
 
    const contasExtrato = []
    for(let mov of extrato.movimentacoes) {
        if(mov.tipo == "TRANSFERENCIA") {
            contasExtrato.push(mov.conta_origem);
            contasExtrato.push(mov.conta_destino);
        }
    }

    const contasResp = await fetch(services.contas + `/contas?filtro=contas&contas=${contasExtrato.join(",")}`, config)
    if (!contasResp.ok) return res.status(contasResp.status).json(contasResp.body);
    const contas = await contasResp.json();

    const mapContas = contas.reduce((map, conta) => {
        map.set(conta.conta, conta); 
        return map;
    }, new Map());

    const cpfs = contas.map(x => x.cpf);

    const clientesResp = await fetch(services.clientes + `/clientes/nomes?filtro=${cpfs.join(",")}`, config)
    if (!clientesResp.ok) return res.status(clientesResp.status).json(clientesResp.body);
    const clientes = await clientesResp.json();

    for(let mov of extrato.movimentacoes) {
        if(mov.tipo == "TRANSFERENCIA") {
            var contaOrigem = mapContas.get(mov.conta_origem);
            var contaDestino = mapContas.get(mov.conta_destino);
            mov.nome_destino = clientes[contaDestino.cpf];
            mov.nome_origem = clientes[contaOrigem.cpf];
        }
    }
    
    return res.status(200).json(extrato);

}