package softwareTransactionalMemory.transactionVariable

import softwareTransactionalMemory.transaction.*

internal class TxVarState<T>(
    private val owner: Transaction,
    private val oldValue: T,
    private val newValue: T
) {
    internal fun valueIn(tx: Transaction, onActive: (Transaction) -> Unit): Any? = when (owner) {
        tx -> newValue
        else -> when (owner.status) {
            TxStatus.COMMITTED -> newValue
            TxStatus.ABORTED -> oldValue
            TxStatus.ACTIVE -> TxStatus.ACTIVE.also { onActive(owner) }
        }
    }
}
