export interface Gerente {
  cpf: string;
  nome: string;
  email: string;
  tipo: string;
  senha?: string;
}

export interface GerenteResponse {
  gerente: Gerente;
  clientes?: any[];
  saldo_positivo?: number;
  saldo_negativo?: number;
}
