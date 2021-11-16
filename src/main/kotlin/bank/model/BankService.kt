package bank.model

import bank.api.*
import softwareTransactionalMemory.atomic
import java.util.concurrent.ConcurrentHashMap

class BankService(private val db: ConcurrentHashMap<Username, Account>) : BankApi {

    override fun processTransaction(transaction: BankTransaction): TransactionResult {
        when (transaction) {

            is GetBalance -> {
                val accountBalanceVar = db[transaction.username]?.balance ?: return unknownUser(transaction.username)

                val balance = atomic { accountBalanceVar.read() }
                return successfulTransaction(balance)
            }

            is Withdraw -> {
                val accountBalanceVar = db[transaction.username]?.balance ?: return unknownUser(transaction.username)
                if (transaction.amount <= 0) return invalidAmount(transaction.amount)
                var successful = false
                atomic {
                    val balance = accountBalanceVar.read()
                    if (balance >= transaction.amount) {
                        accountBalanceVar.write(balance - transaction.amount)
                        successful = true
                    }
                }
                val balance = atomic { accountBalanceVar.read() }
                return if (successful) successfulTransaction(balance)
                else notEnoughMoney(transaction.username)
            }

            is TopUp -> {
                val accountBalanceVar = db[transaction.username]?.balance ?: return unknownUser(transaction.username)
                if (transaction.amount <= 0) return invalidAmount(transaction.amount)
                atomic {
                    val balance = accountBalanceVar.read()
                    accountBalanceVar.write(balance + transaction.amount)
                }
                val balance = atomic { accountBalanceVar.read() }
                return successfulTransaction(balance)
            }

            is Transfer -> {
                val senderBalanceVar = db[transaction.from]?.balance ?: return unknownUser(transaction.from)
                val receiverBalanceVar = db[transaction.to]?.balance ?: return unknownUser(transaction.to)
                if (transaction.amount <= 0) return invalidAmount(transaction.amount)
                var successful = false
                atomic {
                    val senderBalance = senderBalanceVar.read()
                    val receiverBalance = receiverBalanceVar.read()
                    if (senderBalance >= transaction.amount) {
                        senderBalanceVar.write(senderBalance - transaction.amount)
                        receiverBalanceVar.write(receiverBalance + transaction.amount)
                        successful = true
                    }
                }
                val senderBalance = atomic { senderBalanceVar.read() }
                return if (successful) successfulTransaction(senderBalance)
                else notEnoughMoney(transaction.from)
            }
        }
    }

    companion object {
        fun successfulTransaction(balance: Long) = "Successful! Your balance is $balance"

        fun unknownUser(username: Username) = "Unknown user, $username!"

        fun notEnoughMoney(username: Username) = "Not enough money for this transaction, $username!"

        fun invalidAmount(amount: Long) = "Invalid amount for transaction: $amount!"
    }
}