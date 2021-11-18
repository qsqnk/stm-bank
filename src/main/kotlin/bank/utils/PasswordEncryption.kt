package bank.utils

import bank.api.Password
import java.security.MessageDigest

fun Password.encrypted(): String {
    val algo = MessageDigest.getInstance("SHA-512")
    algo.update(toByteArray())
    return algo.digest().decodeToString()
}