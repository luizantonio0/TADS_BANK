import { services } from "../routes.js";

export async function handleCriarGerente(req, res, claims) {

    let config = {
        method: 'GET',
        headers: {
            'X-User-Id': claims.cpf,
            'X-User-Profile': claims.profile
        }
    }

    if(claims.profile != "ADMINISTRADOR") return res.status(403).json({ error: "Você não tem permissão para isso." });

    const menoresSaldosResp = await fetch(services.contas + `/contas/relation/saldoNegativo`, config)
    if (!extratoResp.ok) return res.status(extratoResp.status).json(extratoResp.body);
    const saldosNegativos = await extratoResp.json();

    req.body.saldos_negativos = saldosNegativos;

    return res.status(200).json(clientes);

}