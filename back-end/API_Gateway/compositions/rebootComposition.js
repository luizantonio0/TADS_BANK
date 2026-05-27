import { services } from "../routes.js";

export async function handleReboot(res) {

  const contasResp = await fetch(services.contas + "/contas/reboot")
  if (!contasResp.ok) return res.status(contasResp.status).json(contasResp.body);

  const gerentesResp = await fetch(services.gerentes + "/gerentes/reboot")
  if (!gerentesResp.ok) return res.status(gerentesResp.status).json(gerentesResp.body);

  const clientesResp = await fetch(services.clientes + "/clientes/reboot")
  if (!clientesResp.ok) return res.status(clientesResp.status).json(clientesResp.body);

  const authResp = await fetch(services.auth + "/auth/reboot")
  if (!authResp.ok) return res.status(authResp.status).json(authResp.body);

  return res.status(200).json("Banco de dados criado conforme especificação");

}