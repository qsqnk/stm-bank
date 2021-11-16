package bank.api

interface BankApi {

    fun processTransaction(transaction: BankTransaction): TransactionResult

}
