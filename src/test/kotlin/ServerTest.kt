import bank.api.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import org.junit.jupiter.api.Test
import java.util.*
import com.google.gson.Gson

class ServerTest {

    @Test
    fun signUpTest() = withServer {
        with(signUp("qsqnk", "12345")) {
            assertEquals("User qsqnk has been signed up!", response.content)
        }
    }

    @Test
    fun singUpSignedUpUserTest() = withServer {
        signUp("qsqnk", "12345")
        with(signUp("qsqnk", "12345")) {
            assertNull(response.content)
        }
    }

    @Test
    fun invalidCredentialsTest() = withServer {
        signUp("qsqnk", "12345")
        with(handleRequest(HttpMethod.Get, "bank/balance") {
            addJsonTypeHeader()
            addAuthHeader("qsqnk", "1234")
        }) {
            assertNull(response.content)
        }
    }

    @Test
    fun balanceTest() = withServer {
        signUp("qsqnk", "12345")
        with(handleRequest(HttpMethod.Get, "bank/balance") {
            addJsonTypeHeader()
            addAuthHeader("qsqnk", "12345")
        }) {
            assertEquals(
                TransactionResult(TransactionStatus.SUCCESSFUL, "Your balance is 0").toJson(),
                response.content
            )
        }
    }

    @Test
    fun unsuccessfulWithdrawTest() = withServer {
        signUp("qsqnk", "12345")
        with(handleRequest(HttpMethod.Post, "bank/withdraw") {
            addJsonTypeHeader()
            addAuthHeader("qsqnk", "12345")
            setBody(AmountRequest(100).toJson())
        }) {
            assertEquals(
                TransactionResult(TransactionStatus.UNSUCCESSFUL, "Not enough money for this transaction, qsqnk!").toJson(),
                response.content
            )
        }

    }

    /**
     * TopUp 100
     *
     * Withdraw 30
     *
     * Balance should be 70
     */
    @Test
    fun withdrawTest() = withServer {
        signUp("qsqnk", "12345")

        handleRequest(HttpMethod.Post, "bank/topup") {
            addJsonTypeHeader()
            addAuthHeader("qsqnk", "12345")
            setBody(AmountRequest(100).toJson())
        }

        handleRequest(HttpMethod.Post, "bank/withdraw") {
            addJsonTypeHeader()
            addAuthHeader("qsqnk", "12345")
            setBody(AmountRequest(30).toJson())
        }

        with(handleRequest(HttpMethod.Get, "bank/balance") {
            addJsonTypeHeader()
            addAuthHeader("qsqnk", "12345")
        }) {
            assertEquals(
                TransactionResult(TransactionStatus.SUCCESSFUL, "Your balance is 70").toJson(),
                response.content
            )
        }
    }

    /**
     * Register user1, user2
     *
     * user1 tops up his balance by 100
     *
     * user1 transfers 50 to user2
     *
     * balances of user1, user2 should be equal to 50
     */
    @Test
    fun transferTest() = withServer {
        signUp("user1", "12345")
        signUp("user2", "12345")

        handleRequest(HttpMethod.Post, "bank/topup") {
            addJsonTypeHeader()
            addAuthHeader("user1", "12345")
            setBody(AmountRequest(100).toJson())
        }

        handleRequest(HttpMethod.Post, "bank/transfer") {
            addJsonTypeHeader()
            addAuthHeader("user1", "12345")
            setBody(TransferRequest("user2", 50).toJson())
        }

        with(handleRequest(HttpMethod.Get, "bank/balance") {
            addJsonTypeHeader()
            addAuthHeader("user1", "12345")
        }) {
            assertEquals(
                TransactionResult(TransactionStatus.SUCCESSFUL, "Your balance is 50").toJson(),
                response.content
            )
        }

        with(handleRequest(HttpMethod.Get, "bank/balance") {
            addJsonTypeHeader()
            addAuthHeader("user2", "12345")
        }) {
            assertEquals(
                TransactionResult(TransactionStatus.SUCCESSFUL, "Your balance is 50").toJson(),
                response.content
            )
        }
    }

    private fun Any.toJson() = Gson().toJson(this)

    private fun TestApplicationEngine.signUp(username: Username, password: Password) =
        handleRequest(HttpMethod.Post, "/signup") {
            addJsonTypeHeader()
            setBody(SignUpRequest(username, password).toJson())
        }

    private fun TestApplicationRequest.addJsonTypeHeader() = addHeader("Content-Type", "application/json")

    private fun TestApplicationRequest.addAuthHeader(username: Username, password: Password) {
        addHeader(
            "Authorization",
            "Basic ${Base64.getEncoder().encodeToString("$username:$password".toByteArray())}"
        )
    }

    private fun withServer(block: TestApplicationEngine.() -> Unit) {
        withTestApplication(Application::module, block)
    }
}