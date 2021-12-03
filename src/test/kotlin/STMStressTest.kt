import org.junit.jupiter.api.Test
import softwareTransactionalMemory.atomic
import softwareTransactionalMemory.transactionVariable.TxVar
import kotlin.random.Random
import kotlin.test.assertTrue


class STMStressTest {
    private val balances = IntArray(USERS) { 1000 }
    private val txVarBalances = Array(USERS) { TxVar(1000) }

    /**
     * Checks that after some random transfers
     * balances represented by intArray and array of txVars are equal
     *
     */
    @Test
    fun `All transactions passed`() {
        val queries = List(100000) { List(3) { Random.nextInt(0, USERS) } }

        queries.forEach { (from, to, amount) -> transfer(from, to, amount) }
        queries.parallelStream().forEach { (from, to, amount) -> transferAtomic(from, to, amount) }

        assertTrue {
            balances.withIndex().all { (i, balance) -> balance == atomic { txVarBalances[i].read() } }
        }
    }

    private fun transferAtomic(i: Int, j: Int, amount: Int) = atomic {
        if (i == j) return@atomic
        val balanceI = txVarBalances[i].read()
        val balanceJ = txVarBalances[j].read()
        txVarBalances[i].write(balanceI - amount)
        txVarBalances[j].write(balanceJ + amount)
    }

    private fun transfer(i: Int, j: Int, amount: Int) {
        if (i == j) return
        balances[i] -= amount
        balances[j] += amount
    }

    companion object {
        const val USERS = 1000
    }
}