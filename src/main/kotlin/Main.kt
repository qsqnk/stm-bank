import softwareTransactionalMemory.atomic
import softwareTransactionalMemory.transactionVariable.TxVar
import kotlin.concurrent.thread

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

    fun start() {
        val threads = listOf(
            thread(start = false) { moveAtoB() },
            thread(start = false) { moveBtoA() }
        )

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        atomic {
            println(a.read())
            println(b.read())
        }
    }
}

fun main() {

    AtomicTest().start()


}