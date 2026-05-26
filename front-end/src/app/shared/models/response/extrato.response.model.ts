export interface ExtratoResponse {
    conta: string,
    saldo: number,
    movimentacoes: Movimentacao[],
    saldo_consolidade: any
}

export interface Movimentacao {
    data_hora: string,
    operacao: string,
    conta_origem: string,
    conta_destino: string,
    valor: number
}