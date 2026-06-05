export interface Gerente {
  cpf: string;
  nome: string;
  email: string;
  tipo: string;
  telefone: string;
  senha?: string;
}

export interface GerenteDashboardResponse {
  gerente: Gerente;
  clientes?: any[];
  saldo_positivo?: number;
  saldo_negativo?: number;
}
