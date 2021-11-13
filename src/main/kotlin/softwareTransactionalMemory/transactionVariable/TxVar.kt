package softwareTransactionalMemory.transactionVariable

import kotlinx.atomicfu.*
import softwareTransactionalMemory.transaction.*
import softwareTransactionalMemory.transaction.TxStatus.*

class TxVar<T>(initialValue: T) {
    private val state = atomic(TxVarState(owner = rootTx,
                                          oldValue = initialValue,
                                          newValue = initialValue))

    internal fun openIn(tx: Transaction, update: (T) -> T): T {
        while (true) {
            val currentState = state.value
            val valueInTx = currentState.valueIn(tx, onActive = Transaction::abort)

            if (valueInTx === ACTIVE) continue

            val updatedValue = update(valueInTx as T)
            val updatedState = TxVarState(tx, valueInTx, updatedValue)

            if (state.compareAndSet(currentState, updatedState)) {
                if (tx.status == ABORTED) throw AbortException()
                return updatedValue
            }
        }
    }

    companion object {
        private val rootTx = Transaction().apply { commit() }
    }
}