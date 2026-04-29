package com.chknkv.corepasscode.domain

import java.security.MessageDigest

/**
 * Android-реализация SHA-256 поверх `java.security.MessageDigest`.
 */
actual fun hashPasscode(passcode: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = digest.digest(passcode.encodeToByteArray())
    return bytes.joinToString(separator = "") { byte ->
        ((byte.toInt() and 0xFF) + 0x100).toString(16).substring(1)
    }
}
