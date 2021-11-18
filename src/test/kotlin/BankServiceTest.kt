import bank.api.*
import bank.model.Account
import bank.model.BankService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import softwareTransactionalMemory.transaction.Transaction
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
        set("user3", Account(
            "user3",
            "12345",
            TxVar(0)
        ))
    }
        bank = BankService(db)
    }

    @Test
    fun unknownSenderTest() {
        val tx = Transfer("user5", "user1", 10)
        val txRes = bank.processTransaction(tx)
        println(txRes)
        assertEquals(
            TransactionResult(TransactionStatus.UNSUCCESSFUL, "Unknown user user5!"),
            txRes
        )
    }

    @Test
    fun unknownReceiverTest() {
        val tx = Transfer("user1", "user5", 10)
        val txRes = bank.processTransaction(tx)
        println(txRes)
        assertEquals(
            TransactionResult(TransactionStatus.UNSUCCESSFUL, "Unknown user user5!"),
            txRes
        )
    }





}