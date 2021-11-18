package bank.api

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionStatus {
    SUCCESSFUL,
    UNSUCCESSFUL
}

@Serializable
data class TransactionResult(
    val status: TransactionStatus,
    val message: String
)
