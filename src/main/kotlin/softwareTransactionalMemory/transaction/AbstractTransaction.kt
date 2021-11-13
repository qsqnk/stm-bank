package softwareTransactionalMemory.transaction

import kotlinx.atomicfu.AtomicRef

abstract class AbstractTransaction{

    internal abstract val status: TxStatus

    internal abstract fun commit(): Boolean

    internal abstract fun abort(): Boolean

}
