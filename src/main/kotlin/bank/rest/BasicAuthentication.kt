package bank.rest

import bank.api.Username
import bank.model.Account
import io.ktor.application.*
import io.ktor.auth.*
import java.util.concurrent.ConcurrentHashMap

fun Application.installBasicAuth(db: ConcurrentHashMap<Username, Account>) {
    install(Authentication) {
        basic {
            validate { authData ->
                when (authData.password) {
                    db[authData.name]?.password -> UserIdPrincipal(authData.name)
                    else -> null
                }
            }
        }
    }
}