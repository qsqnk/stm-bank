package bank.model

import bank.api.*
import softwareTransactionalMemory.atomic
import java.util.concurrent.ConcurrentHashMap

class BankService(private val db: ConcurrentHashMap<Username, Account>) : BankApi {

    override fun process(transaction: BankTransaction): TransactionResult =
        when (transaction) {
            is GetBalance -> processGetBalance(transaction)
            is Withdraw -> processWithdraw(transaction)
            is TopUp -> processTopUp(transaction)
            is Transfer -> processTransfer(transaction)
        }


    private fun processGetBalance(transaction: GetBalance): TransactionResult {
        val accountBalanceVar = db[transaction.username]?.balance
            ?: return TransactionResult(
                status = TransactionStatus.UNSUCCESSFUL,
                message = unknownUser(transaction.username)
            )

        val balance = atomic { accountBalanceVar.read() }

        return TransactionResult(
            status = TransactionStatus.SUCCESSFUL,
            message = successfulTransaction(balance)
        )
    }

    private fun processWithdraw(transaction: Withdraw): TransactionResult {
        val accountBalanceVar = db[transaction.username]?.balance
            ?: return TransactionResult(
                status = TransactionStatus.UNSUCCESSFUL,
                message = unknownUser(transaction.username)
            )

        if (transaction.amount <= 0) return TransactionResult(
            status = TransactionStatus.UNSUCCESSFUL,
            message = invalidAmount(transaction.amount)
        )

        var successful = false

        atomic {
            val balance = accountBalanceVar.read()
            if (balance >= transaction.amount) {
                accountBalanceVar.write(balance - transaction.amount)
                successful = true
            }
        }

        val balance = atomic { accountBalanceVar.read() }

        return TransactionResult(
            status = if (successful) TransactionStatus.SUCCESSFUL
            else TransactionStatus.UNSUCCESSFUL,
            message = if (successful) successfulTransaction(balance)
            else notEnoughMoney(transaction.username)
        )
    }

    private fun processTopUp(transaction: TopUp): TransactionResult {
        val accountBalanceVar = db[transaction.username]?.balance
            ?: return TransactionResult(
                status = TransactionStatus.UNSUCCESSFUL,
                message = unknownUser(transaction.username)
            )

        if (transaction.amount <= 0) return TransactionResult(
            status = TransactionStatus.UNSUCCESSFUL,
            message = invalidAmount(transaction.amount)
        )

        atomic {
            val balance = accountBalanceVar.read()
            accountBalanceVar.write(balance + transaction.amount)
        }

        val balance = atomic { accountBalanceVar.read() }

        return TransactionResult(
            status = TransactionStatus.SUCCESSFUL,
            message = successfulTransaction(balance)
        )
    }

    private fun processTransfer(transaction: Transfer): TransactionResult {
        val senderBalanceVar = db[transaction.from]?.balance
            ?: return TransactionResult(
                status = TransactionStatus.UNSUCCESSFUL,
                message = unknownUser(transaction.from)
            )

        val receiverBalanceVar = db[transaction.to]?.balance
            ?: return TransactionResult(
                status = TransactionStatus.UNSUCCESSFUL,
                message = unknownUser(transaction.to)
            )

        if (transaction.amount <= 0) return TransactionResult(
            status = TransactionStatus.UNSUCCESSFUL,
            message = invalidAmount(transaction.amount)
        )

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

        return TransactionResult(
            status = if (successful) TransactionStatus.SUCCESSFUL
            else TransactionStatus.UNSUCCESSFUL,
            message = if (successful) successfulTransaction(senderBalance)
            else notEnoughMoney(transaction.from)
        )
    }

    companion object {
        /**
         * Helper functions to generate transaction message
         *
         */
        fun successfulTransaction(balance: Long) = "Your balance is $balance"
        fun unknownUser(username: Username) = "Unknown user $username!"
        fun notEnoughMoney(username: Username) = "Not enough money for this transaction, $username!"
        fun invalidAmount(amount: Long) = "Invalid amount for transaction: $amount!"
    }
}