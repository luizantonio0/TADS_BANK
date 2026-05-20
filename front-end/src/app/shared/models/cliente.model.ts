export interface Cliente {
  cpf: string;
  nome: string;
  email: string;
  salario: number;
  endereco: string;
  cidade: string;
  estado: string;
  CEP?: string;
  conta?: number;
  telefone?: string;
  cpf_gerente?: string;
  saldo?: number;
  limite?: number;
  gerente_nome?: string;
  gerente_email?: string;
}
