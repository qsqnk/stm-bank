package bank.rest

import bank.api.*
import bank.model.Account
import bank.utils.encrypted
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.Serializable
import softwareTransactionalMemory.transactionVariable.TxVar
import java.util.concurrent.ConcurrentHashMap

fun Routing.signUp(db: ConcurrentHashMap<Username, Account>) {
    post("/signup") {
        val (username, password) = call.receive<SignUpRequest>().also { println(it) }
        if (!db.containsKey(username)) {
            db[username] = Account(
                username,
                password.encrypted(),
                TxVar(0L)
            )
            call.respondText { "User $username has been signed up!" }
        }
    }
}

fun Route.bank(bank: BankApi) {
    get("bank/balance") {
        val username = getUsername() ?: return@get
        val transaction = GetBalance(
            username = username
        )
        bank.processTransaction(transaction).also { call.respond(it) }
    }

    post("bank/withdraw") {
        val (amount) = call.receive<AmountRequest>()
        val username = getUsername() ?: return@post
        val transaction = Withdraw(
            username = username,
            amount = amount
        )
        bank.processTransaction(transaction).also { call.respond(it) }
    }

    post("bank/topup") {
        val (amount) = call.receive<AmountRequest>()
        val username = getUsername() ?: return@post
        val transaction = TopUp(
            username = username,
            amount = amount
        )
        bank.processTransaction(transaction).also { call.respond(it) }
    }

    post("bank/transfer") {
        val (to, amount) = call.receive<TransferRequest>()
        val username = getUsername() ?: return@post
        val transaction = Transfer(
            from = username,
            to = to,
            amount = amount
        )
        bank.processTransaction(transaction).also { call.respond(it) }
    }
}

fun PipelineContext<*, ApplicationCall>.getUsername() = call.principal<UserIdPrincipal>()?.name
