export interface ContaResponse {
    cliente: string,
    conta: string,
    saldo: number;
}

export interface Movimentacao {
    data_hora: string,
    tipo: string,
    conta_origem: string,
    conta_destino: string | null,
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