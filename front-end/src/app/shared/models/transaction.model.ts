export interface Transaction {
    uuid: string
    origin?: {
        account_number: string
        account_name: string
    },
    destination?: {
        account_number: string
        account_name: string
    }
    type: 'withdraw' | 'deposit' | 'transfer'
    amount: number
    date: string
}

export interface TransactionGroup {
    day: string
    balance: number
    transactions: Transaction[]
}