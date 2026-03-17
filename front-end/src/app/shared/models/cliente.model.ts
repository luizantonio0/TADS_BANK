export interface Cliente {
  cpf: string;
  nome: string;
  email: string;
  salario: number;
  endereco: string;
  cidade: string;
  estado: string;

  conta?: number;
  telefone?: string;
  saldo?: number;
  limite?: number;
  gerente_nome?: string;
  gerente_email?: string;
}
