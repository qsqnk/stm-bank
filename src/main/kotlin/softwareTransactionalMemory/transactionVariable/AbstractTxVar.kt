package softwareTransactionalMemory.transactionVariable

import softwareTransactionalMemory.transaction.AbstractTransaction

abstract class AbstractTxVar<T> {

    abstract fun readIn(tx: AbstractTransaction): T

    abstract fun writeIn(tx: AbstractTransaction, value: T): T

}