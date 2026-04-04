export interface ContaResponse {
    cliente: string,
    conta: string,
    saldo: number;
}

export interface Movimentacao {
    data: string,
    tipo: string,
    origem: string,
    destino: string,
    valor: number;
}

export interface ExtratoResponse {
    conta: string,
    saldo: number,
    movimentacoes: Movimentacao[];
}

export interface OperacaoContaResponse {
    valor: number;
}