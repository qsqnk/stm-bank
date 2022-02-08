package bank.utils

import bank.api.Password
import java.security.MessageDigest

fun Password.encrypted(): String = with(MessageDigest.getInstance("SHA-512")) {
    update(toByteArray())
    digest().decodeToString()
}