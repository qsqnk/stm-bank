package softwareTransactionalMemory.transaction

import kotlinx.atomicfu.atomic
import softwareTransactionalMemory.transactionVariable.AbstractTxVar

class Transaction : TxScope, AbstractTransaction() {
    private val _status = atomic(TxStatus.ACTIVE)

    override val status
        get() = _status.value

    override fun commit() =
        _status.compareAndSet(TxStatus.ACTIVE, TxStatus.COMMITTED)

    override fun abort() =
        _status.compareAndSet(TxStatus.ACTIVE, TxStatus.ABORTED)

    override fun <T> AbstractTxVar<T>.read(): T =
        readIn(this@Transaction)

    override fun <T> AbstractTxVar<T>.write(value: T): T =
        writeIn(this@Transaction, value)

}
