package bank.rest

import bank.api.*
import bank.model.Account
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import softwareTransactionalMemory.transactionVariable.TxVar
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class CredentialsRequest(
    val login: String,
    val password: String
)

@Serializable
data class AmountRequest(
    val amount: Long
)

@Serializable
data class TransferRequest(
    val to: Username,
    val amount: Long
)

fun Routing.registerRoute(db: ConcurrentHashMap<Username, Account>) {
    post("/register") {
        val (username, password) = call.receive<CredentialsRequest>()
        if (!db.containsKey(username)) {
            db[username] = Account(
                username,
                password,
                TxVar(0L)
            )
            call.respondText { "User $username has been registred!" }
        }
    }
}

fun Route.bankRoutes(bank: BankApi) {
    get("bank/balance") {
        val username = getUsername() ?: return@get
        val transaction = GetBalance(
            username = username
        )
        bank.processTransaction(transaction).also { call.respondText(it) }
    }

    post("bank/withdraw") {
        val (amount) = call.receive<AmountRequest>()
        val username = getUsername() ?: return@post
        val transaction = Withdraw(
            username = username,
            amount = amount
        )
        bank.processTransaction(transaction).also { call.respondText(it) }
    }

    post("bank/topup") {
        val (amount) = call.receive<AmountRequest>()
        val username = getUsername() ?: return@post
        val transaction = TopUp(
            username = username,
            amount = amount
        )
        bank.processTransaction(transaction).also { call.respondText(it) }
    }

    post("bank/transfer") {
        val (to, amount) = call.receive<TransferRequest>()
        val username = getUsername() ?: return@post
        val transaction = Transfer(
            from = username,
            to = to,
            amount = amount
        )
        bank.processTransaction(transaction).also { call.respondText(it) }
    }
}

fun PipelineContext<*, ApplicationCall>.getUsername() = call.principal<UserIdPrincipal>()?.name
