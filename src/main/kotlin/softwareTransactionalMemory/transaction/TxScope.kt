package softwareTransactionalMemory.transaction

import softwareTransactionalMemory.transactionVariable.AbstractTxVar

interface TxScope {

    fun <T> AbstractTxVar<T>.read(): T

    fun <T> AbstractTxVar<T>.write(value: T): T

}