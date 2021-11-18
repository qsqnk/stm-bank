package softwareTransactionalMemory.transaction

import softwareTransactionalMemory.transactionVariable.TxVar

interface TxScope {

    fun <T> TxVar<T>.read(): T

    fun <T> TxVar<T>.write(value: T): T

}