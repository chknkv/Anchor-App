package com.chknkv.corepasscode.domain

import com.chknkv.corepasscode.models.domain.BiometricContext
import com.chknkv.corepasscode.models.domain.BiometricResult
import com.chknkv.corepasscode.models.domain.BiometricType
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.NSError
import platform.LocalAuthentication.LABiometryTypeFaceID
import platform.LocalAuthentication.LABiometryTypeTouchID
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAErrorAppCancel
import platform.LocalAuthentication.LAErrorSystemCancel
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume

/**
 * iOS-реализация [BiometricAuthenticator] поверх LocalAuthentication.
 */
actual class BiometricAuthenticator {

    @OptIn(ExperimentalForeignApi::class)
    actual fun availableType(): BiometricType = memScoped {
        val context = LAContext()
        val error = alloc<ObjCObjectVar<NSError?>>()
        val canEvaluate = context.canEvaluatePolicy(
            LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            error.ptr
        )
        if (!canEvaluate) return BiometricType.NONE
        when (context.biometryType) {
            LABiometryTypeFaceID -> BiometricType.FACE_ID
            LABiometryTypeTouchID -> BiometricType.TOUCH_ID
            else -> BiometricType.NONE
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun isAvailable(): Boolean = memScoped {
        val context = LAContext()
        val error = alloc<ObjCObjectVar<NSError?>>()
        context.canEvaluatePolicy(
            LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            error.ptr
        )
    }

    actual suspend fun authenticate(
        context: BiometricContext,
        title: String,
        subtitle: String,
        cancelButtonText: String,
    ): BiometricResult = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val laContext = LAContext().apply {
                localizedCancelTitle = cancelButtonText
            }

            laContext.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = subtitle.ifBlank { title },
                reply = { success, error ->
                    if (!continuation.isActive) return@evaluatePolicy
                    val result: BiometricResult = when {
                        success -> BiometricResult.Success
                        error != null && isCancellation(error.code) -> BiometricResult.Cancelled
                        error != null -> BiometricResult.Error(error.localizedDescription)
                        else -> BiometricResult.Error("unknown")
                    }
                    continuation.resume(result)
                }
            )

            continuation.invokeOnCancellation {
                runCatching { laContext.invalidate() }
            }
        }
    }

    private fun isCancellation(code: Long): Boolean =
        code == LAErrorUserCancel || code == LAErrorAppCancel || code == LAErrorSystemCancel
}
