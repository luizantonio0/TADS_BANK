import { Movimentacao } from "./conta.model"

export interface GrupoMovimentacao {
    dia: string
    saldo: number
    movimentacoes: Movimentacao[]
}