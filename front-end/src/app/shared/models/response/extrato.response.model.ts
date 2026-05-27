export interface ExtratoResponse {
    conta: string,
    saldo: number,
    movimentacoes: Movimentacao[],
    saldo_consolidade: any
}

export interface Movimentacao {
    data: string,
    operacao: string,
    origem: string,
    destino: string,
    valor: number
}