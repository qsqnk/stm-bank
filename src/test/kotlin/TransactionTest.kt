import org.junit.jupiter.api.Test
import softwareTransactionalMemory.transaction.Transaction
import softwareTransactionalMemory.transaction.TxStatus
import softwareTransactionalMemory.transactionVariable.TxVar
import kotlin.concurrent.thread
import kotlin.test.assertEquals

class TransactionTest {

    /**
     * If tx1 want to interact with txVar and there are
     * no other transactions that want to interact with txVar
     * tx1 will always be committed
     *
     */
    @Test
    fun aloneTx() {
        val tx = Transaction()
        val txVar = TxVar(0)

        with(tx) {
            txVar.write(1)
            commit()
        }

        assertEquals(TxStatus.COMMITTED, tx.status)
    }

    /**
     * If the first transaction interacts with the variable txVar
     * then comes new transaction that also want to interact with txVar
     * old transaction aborts
     */
    @Test
    fun oldTxAborts() {
        val tx1 = Transaction()
        val tx2 = Transaction()
        val txVar = TxVar(0)

        thread {
            with(tx1) {
                txVar.write(1)
                Thread.sleep(3000)
            }
        }.join()

        thread {
            with(tx2) {
                txVar.write(2)
                assertEquals(TxStatus.ABORTED, tx1.status)
            }
        }.join()
    }
}