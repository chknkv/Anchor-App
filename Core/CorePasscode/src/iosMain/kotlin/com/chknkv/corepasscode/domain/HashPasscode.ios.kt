package com.chknkv.corepasscode.domain

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.refTo
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH

/**
 * iOS-реализация SHA-256 поверх CommonCrypto.
 */
@OptIn(ExperimentalForeignApi::class)
actual fun hashPasscode(passcode: String): String {
    val bytes = passcode.encodeToByteArray()
    return memScoped {
        val digest = allocArray<platform.posix.uint8_tVar>(CC_SHA256_DIGEST_LENGTH)
        CC_SHA256(bytes.refTo(0), bytes.size.toUInt(), digest)
        val resultBytes = digest.readBytes(CC_SHA256_DIGEST_LENGTH)
        resultBytes.joinToString(separator = "") { byte ->
            ((byte.toInt() and 0xFF) + 0x100).toString(16).substring(1)
        }
    }
}
