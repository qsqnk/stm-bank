package bank.api

import bank.model.BankTransaction

interface BankApi {

    fun process(transaction: BankTransaction): TransactionResult

}
