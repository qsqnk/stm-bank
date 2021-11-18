package bank.utils

import bank.api.Password
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

fun Password.encrypted(): String {
    val spec = PBEKeySpec(toCharArray())
    val encAlgo = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    return encAlgo.generateSecret(spec).encoded.toString()
}