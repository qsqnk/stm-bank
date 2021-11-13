package softwareTransactionalMemory.transaction

import kotlinx.atomicfu.atomic
import softwareTransactionalMemory.transactionVariable.TxVar

class Transaction : TxScope {
    private val _status = atomic(TxStatus.ACTIVE)

    internal val status
        get() = _status.value

    internal fun commit() =
        _status.compareAndSet(TxStatus.ACTIVE, TxStatus.COMMITTED)

    internal fun abort() =
        _status.compareAndSet(TxStatus.ACTIVE, TxStatus.ABORTED)

    override fun <T> TxVar<T>.read(): T =
        openIn(this@Transaction, update = { it })

    override fun <T> TxVar<T>.write(value: T): T =
        openIn(this@Transaction, update = { value })

}