import softwareTransactionalMemory.atomic
import softwareTransactionalMemory.transactionVariable.TxVar
import kotlin.concurrent.thread
import kotlin.contracts.contract

class AtomicTest {
    val a = TxVar(1)
    val b = TxVar(2)

    fun moveAtoB() {
        atomic {
            val t = a.read()
            b.write(t)
        }
    }

    fun moveBtoA() {
        atomic {
            val t = b.read()
            a.write(t)
        }
    }

    fun check(): Boolean {
        val threads = listOf(
            thread(start = false) { moveAtoB() },
            thread(start = false) { moveBtoA() }
        )

        return atomic { a.read() == b.read() }
    }
}

fun main() {

    val counter = TxVar(0)


    println(atomic { counter.read() })

}


