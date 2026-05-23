export interface Cliente {
  cpf: string;
  nome: string;
  email: string;
  salario: number;
  endereco: string;
  cidade: string;
  estado: string;
  cep?: string;
  CEP?: string;
  conta?: number | string;
  telefone?: string;
  cpf_gerente?: string;
  gerente?: string;
  saldo?: number;
  limite?: number;
  gerente_nome?: string;
  gerente_email?: string;
}
