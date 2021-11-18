package bank.rest

import bank.api.Username
import bank.model.Account
import bank.utils.encrypted
import io.ktor.application.*
import io.ktor.auth.*
import java.util.concurrent.ConcurrentHashMap

fun Application.installBasicAuth(db: ConcurrentHashMap<Username, Account>) {
    install(Authentication) {
        basic {
            validate { authData ->
                when (authData.password.encrypted()) {
                    db[authData.name]?.encryptedPassword -> UserIdPrincipal(authData.name)
                    else -> null
                }
            }
        }
    }
}