package bank.model

import bank.api.Password
import bank.api.Username
import softwareTransactionalMemory.transactionVariable.TxVar

data class Account(
    val username: Username,
    val encryptedPassword: Password,
    val balance: TxVar<Long>
)
