import org.jetbrains.kotlinx.lincheck.LinChecker
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.annotations.Param
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingCTest
import org.junit.jupiter.api.Test
import softwareTransactionalMemory.atomic
import softwareTransactionalMemory.transactionVariable.TxVar

@ModelCheckingCTest
class STMModelTest {

    private val balances = Array(2) { TxVar(1000) }

    @Test
    fun runTest() = LinChecker.check(this::class.java)

    @Operation
    fun topUp(
        @Param(gen = IntGen::class, conf = "0:1") id: Int,
        @Param(gen = IntGen::class, conf = "0:100") amount: Int
    ) = atomic {
        val old = balances[id].read()
        balances[id].write(old + amount)
    }

    @Operation
    fun widthDraw(
        @Param(gen = IntGen::class, conf = "0:1") id: Int,
        @Param(gen = IntGen::class, conf = "0:100") amount: Int
    ) = topUp(id, -amount)

    @Operation
    fun transfer(
        @Param(gen = IntGen::class, conf = "0:1") from: Int,
        @Param(gen = IntGen::class, conf = "0:100") amount: Int
    ) {
        val to = from xor 1
        atomic {
            val fromB = balances[from].read()
            val toB = balances[to].read()
            balances[from].write(fromB - amount)
            balances[to].write(toB + amount)
        }
    }
}