package softwareTransactionalMemory

import softwareTransactionalMemory.transaction.AbortException
import softwareTransactionalMemory.transaction.Transaction
import softwareTransactionalMemory.transaction.TxScope

fun <T> atomic(block: TxScope.() -> T): T {
    while (true) {
        val tx = Transaction()
        try {
            val result = tx.block()

            if (tx.commit()) return result
            else tx.abort()
        } catch (e: AbortException) {
            tx.abort()
        }
    }
}
