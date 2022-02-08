package softwareTransactionalMemory.transactionVariable

import kotlinx.atomicfu.*
import softwareTransactionalMemory.transaction.*

class TxVar<T>(initialValue: T) {
    private val state = atomic(TxVarState(initialTx, initialValue, initialValue))

    @Suppress("UNCHECKED_CAST")
    internal fun openIn(tx: Transaction, update: (T) -> T): T {
        while (true) {
            val currentState = state.value
            val valueInTx = currentState.valueIn(tx, onActive = Transaction::abort)

            if (valueInTx == TxStatus.ACTIVE) continue

            val updatedValue = update(valueInTx as T)
            val updatedState = TxVarState(tx, valueInTx, updatedValue)

            if (state.compareAndSet(currentState, updatedState)) {
                if (tx.status == TxStatus.ABORTED) throw AbortException()
                return updatedValue
            }
        }
    }

    companion object {
        private val initialTx = Transaction().apply { commit() }
    }
}