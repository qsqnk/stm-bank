package bank.api

sealed interface BankTransaction

data class Withdraw(
    val username: Username,
    val amount: Long
) : BankTransaction

data class TopUp(
    val username: Username,
    val amount: Long
) : BankTransaction

data class Transfer(
    val from: Username,
    val to: Username,
    val amount: Long
) : BankTransaction

data class GetBalance(
    val username: Username
): BankTransaction