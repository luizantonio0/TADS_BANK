import { services } from "../routes.js";
import { handleConsultaCliente } from "./clienteComposition.js";

export async function handleAtualizarCliente(req, res, claims, cpf) {

  let headers = {
      'X-User-Id': claims.cpf,
      'X-User-Profile': claims.profile,
      'Content-Type': 'application/json'
  };
  const requestBody = typeof res.body === 'string' ? req.body : JSON.stringify(req.body);

  const clienteResp = await fetch(services.clientes + `/clientes/${cpf}`, {
      method: 'PUT',
      headers: headers,
      body: requestBody
  });

  if (!clienteResp.ok) {
      const errorData = await clienteResp.json().catch(() => ({ error: "Erro desconhecido" }));
      return res.status(clienteResp.status).json(errorData);
  }
  
  return handleConsultaCliente(res, claims, cpf);
}