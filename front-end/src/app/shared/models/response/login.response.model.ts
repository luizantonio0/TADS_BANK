export interface LoginResponseModel {
  access_token: string;
  token_type: string;
  tipo: string;
  usuario: {
    nome: string;
    cpf: string;
    email: string;
  };
}
