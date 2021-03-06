import bank.api.Username
import bank.rest.installBasicAuth
import bank.model.Account
import bank.model.BankService
import bank.rest.signUp
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import java.util.concurrent.ConcurrentHashMap
import bank.rest.bank

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val db = ConcurrentHashMap<Username, Account>()
    val bank = BankService(db)

    installBasicAuth(db)
    install(ContentNegotiation) { json() }

    routing {
        signUp(db)
        authenticate {
            bank(bank)
        }
    }
}