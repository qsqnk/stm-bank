import bank.api.*
import bank.model.Account
import bank.model.BankService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import softwareTransactionalMemory.transactionVariable.TxVar
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.assertEquals

class BankServiceTest {
    private lateinit var db: ConcurrentHashMap<Username, Account>
    private lateinit var bank: BankApi

    @BeforeEach
    fun initBankService() {
        val db = ConcurrentHashMap<Username, Account>().apply {
        set("user1", Account(
            "user1",
            "12345",
            TxVar(0)
        ))
        set("user2", Account(
            "user2",
            "12345",
            TxVar(0)
        ))
    }
        bank = BankService(db)
    }

    @Test
    fun successfulTopTup() {
        val tx = TopUp("user1", 100)
        val txRes = bank.process(tx)
        assertEquals(
            TransactionResult(TransactionStatus.SUCCESSFUL, "Your balance is 100"),
            txRes
        )
    }

    @Test
    fun successfulWithDraw() {
        bank.process(TopUp("user1", 100))
        val tx = Withdraw("user1", 30)
        val txRes = bank.process(tx)
        assertEquals(
            TransactionResult(TransactionStatus.SUCCESSFUL, "Your balance is 70"),
            txRes
        )
    }

    @Test
    fun successfulTransfer() {
        bank.process(TopUp("user1", 100))
        bank.process(TopUp("user2", 0))
        bank.process(Transfer("user1", "user2", 30))

        val balanceUser1TxRes = bank.process(GetBalance("user1"))
        val balanceUser2TxRes = bank.process(GetBalance("user2"))

        assertEquals(
            TransactionResult(TransactionStatus.SUCCESSFUL, "Your balance is 70"),
            balanceUser1TxRes
        )

        assertEquals(
            TransactionResult(TransactionStatus.SUCCESSFUL, "Your balance is 30"),
            balanceUser2TxRes
        )
    }

    @Test
    fun unknownSenderTest() {
        val tx = Transfer("user5", "user1", 10)
        val txRes = bank.process(tx)
        assertEquals(
            TransactionResult(TransactionStatus.UNSUCCESSFUL, "Unknown user user5!"),
            txRes
        )
    }

    @Test
    fun unknownReceiverTest() {
        val tx = Transfer("user1", "user5", 10)
        val txRes = bank.process(tx)
        assertEquals(
            TransactionResult(TransactionStatus.UNSUCCESSFUL, "Unknown user user5!"),
            txRes
        )
    }
}
