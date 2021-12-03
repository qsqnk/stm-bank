package bank.api

interface BankApi {

    fun process(transaction: BankTransaction): TransactionResult

}
