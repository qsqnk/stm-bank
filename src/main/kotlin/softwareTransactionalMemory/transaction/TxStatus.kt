package softwareTransactionalMemory.transaction

class AbortException : Exception()

internal enum class TxStatus {
    ACTIVE,
    COMMITTED,
    ABORTED
}