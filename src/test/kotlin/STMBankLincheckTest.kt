import org.jetbrains.kotlinx.lincheck.LinChecker
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.annotations.Param
import org.jetbrains.kotlinx.lincheck.annotations.Validate
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingCTest
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.junit.jupiter.api.Test
import softwareTransactionalMemory.atomic
import softwareTransactionalMemory.transactionVariable.TxVar
import kotlin.random.Random

@ModelCheckingCTest
class STMBankLincheckTest {
    private val balances = IntArray(USERS) { (Random.nextInt(0, 1000)) }
    private val txVarBalances = Array(USERS) { i -> TxVar(balances[i]) }

    @Test
    fun runTest() = ModelCheckingOptions().iterations(20).check(this::class.java)

    @Operation
    fun makeTransfer(
        @Param(gen = IntGen::class, conf = "0:${USERS - 1}") from: Int,
        @Param(gen = IntGen::class, conf = "0:${USERS - 1}") to: Int,
        @Param(gen = IntGen::class, conf = "0:1000") amount: Int
    ) {
        transferAtomic(from, to, amount)
        transfer(from, to, amount)
    }

    @Validate
    fun validate() {
        val valid = balances.withIndex().all { (i, balance) -> balance == atomic { txVarBalances[i].read() } }
        if (!valid) throw IllegalStateException("Balances are not equal!")
    }

    private fun transferAtomic(from: Int, to: Int, amount: Int) = atomic {
        if (from == to) return@atomic
        val balanceFrom = txVarBalances[from].read()
        val balanceTo = txVarBalances[to].read()
        txVarBalances[from].write(balanceFrom - amount)
        txVarBalances[to].write(balanceTo + amount)
    }

    private fun transfer(from: Int, to: Int, amount: Int) {
        if (from == to) return
        balances[from] -= amount
        balances[to] += amount
    }

    companion object {
        const val USERS = 50
    }
}