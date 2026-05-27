export interface ContaResponse {
    cliente: string,
    conta: string,
    saldo: number;
}

export interface Movimentacao {
    data: string,
    tipo: string,
    origem: string,
    destino: string | null,
    valor: number;
    nome_origem?: string,
    nome_destino: string;
}

export interface ExtratoResponse {
    conta: string,
    saldo: number,
    movimentacoes: Movimentacao[],
    saldos_consolidados: Record<string, number>
}

export interface OperacaoContaResponse {
    valor: number;
}