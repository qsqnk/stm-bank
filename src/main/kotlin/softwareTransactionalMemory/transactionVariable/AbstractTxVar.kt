package softwareTransactionalMemory.transactionVariable

import softwareTransactionalMemory.transaction.AbstractTransaction

abstract class AbstractTxVar<T> {

    internal abstract fun readIn(tx: AbstractTransaction): T

    internal abstract fun writeIn(tx: AbstractTransaction, value: T): T

}