package softwareTransactionalMemory.transactionVariable

import softwareTransactionalMemory.transaction.Transaction
import softwareTransactionalMemory.transaction.TxStatus

internal class TxVarState<T>(
    internal val owner: Transaction,
    internal val oldValue: T,
    internal val newValue: T
) {
    internal fun valueIn(tx: Transaction, onActive: (Transaction) -> Unit): Any? = when (owner) {
        tx -> newValue
        else -> when (owner.status) {
            TxStatus.COMMITTED -> newValue
            TxStatus.ABORTED -> oldValue
            TxStatus.ACTIVE -> onActive(owner).let { TxStatus.ACTIVE }
        }
    }
}
