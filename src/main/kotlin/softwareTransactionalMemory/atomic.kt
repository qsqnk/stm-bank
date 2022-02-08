package softwareTransactionalMemory

import softwareTransactionalMemory.transaction.AbortException
import softwareTransactionalMemory.transaction.Transaction
import softwareTransactionalMemory.transaction.TxScope

fun <T> atomic(block: TxScope.() -> T): T {
    while (true) {
        with(Transaction()) {
            try {
                val result = block()
                if (commit()) return result
                else abort()
            } catch (e: AbortException) {
                abort()
            }
        }
    }
}
