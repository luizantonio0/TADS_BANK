import { services } from "../routes.js";

export async function handleGerenteDashboard(res, claims) {

    let config = {
        method: 'GET',
        headers: {
            'X-User-Id': claims.cpf,
            'X-User-Profile': claims.profile
        }
    }

    const gerentesResp = await fetch(services.gerentes + "/gerentes", config)
    if (!gerentesResp.ok) return res.status(gerentesResp.status).json(gerentesResp.body);
    const gerentes = await gerentesResp.json();

    const gerenteCpfs = gerentes.map(x => x.cpf);

    const contasResp = await fetch(services.contas + `/contas/relation?gerentes=${gerenteCpfs.join(",")}`, config)
    if (!contasResp.ok) return res.status(contasResp.status).json(contasResp.body);
    const contas = await contasResp.json();

    const clientesResp = await fetch(services.clientes + `/clientes/relation?gerentes=${gerenteCpfs.join(",")}`, config)
    if (!clientesResp.ok) return res.status(clientesResp.status).json(clientesResp.body);
    const clientes = await clientesResp.json();

    const saldosResp = await fetch(services.contas + `/contas/saldos?gerentes=${gerenteCpfs.join(",")}`, config)
    if (!saldosResp.ok) return res.status(saldosResp.status).json(saldosResp.body);
    const saldos = await saldosResp.json();

    let response = []

    for (var gerente of gerentes) {
        let clientesGerente = clientes[gerente.cpf];
        for (var cliente of clientesGerente) {
            cliente.limite = contas[cliente.cpf].limite;
            cliente.saldo = contas[cliente.cpf].saldo;
        }
        let saldoGerentes = saldos[gerente.cpf];
        response.push({
            "gerente": gerente,
            "clientes": clientesGerente,
            "saldo_positivo": saldoGerentes.saldo_positivo,
            "saldo_negativo": saldoGerentes.saldo_negativo,
        })
    }

    return res.status(200).json(response);

}

