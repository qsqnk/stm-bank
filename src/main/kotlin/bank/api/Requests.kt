package bank.api

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val login: String,
    val password: String
)

@Serializable
data class AmountRequest(
    val amount: Long
)

@Serializable
data class TransferRequest(
    val to: Username,
    val amount: Long
)