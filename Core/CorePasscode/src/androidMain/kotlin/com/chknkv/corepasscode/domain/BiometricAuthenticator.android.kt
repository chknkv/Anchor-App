package com.chknkv.corepasscode.domain

import android.content.Context
import android.content.pm.PackageManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.chknkv.corepasscode.models.domain.BiometricContext
import com.chknkv.corepasscode.models.domain.BiometricResult
import com.chknkv.corepasscode.models.domain.BiometricType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * Android-реализация [BiometricAuthenticator] поверх `androidx.biometric`.
 *
 * @param appContext Application context для проверки наличия фичи и опроса [BiometricManager].
 */
actual class BiometricAuthenticator(
    private val appContext: Context
) {

    actual fun availableType(): BiometricType {
        val pm = appContext.packageManager
        return when {
            pm.hasSystemFeature(PackageManager.FEATURE_FACE) -> BiometricType.FACE_ANDROID
            pm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) -> BiometricType.TOUCH_ID
            pm.hasSystemFeature(PackageManager.FEATURE_IRIS) -> BiometricType.FACE_ANDROID
            else -> BiometricType.NONE
        }
    }

    actual fun isAvailable(): Boolean {
        if (availableType() == BiometricType.NONE) return false
        val manager = BiometricManager.from(appContext)
        val status = manager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        return status == BiometricManager.BIOMETRIC_SUCCESS
    }

    actual suspend fun authenticate(
        context: BiometricContext,
        title: String,
        subtitle: String,
        cancelButtonText: String,
    ): BiometricResult = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val activity = context.activity
            val executor = ContextCompat.getMainExecutor(activity)

            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (continuation.isActive) continuation.resume(BiometricResult.Success)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (!continuation.isActive) return
                    val isCancel = errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                            errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                            errorCode == BiometricPrompt.ERROR_CANCELED
                    val result = if (isCancel) BiometricResult.Cancelled
                    else BiometricResult.Error(errString.toString())
                    continuation.resume(result)
                }

                override fun onAuthenticationFailed() {
                    /* Отдельный неудачный скан — диалог сам отобразит ошибку. */
                }
            }

            val prompt = BiometricPrompt(activity, executor, callback)
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText(cancelButtonText)
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.BIOMETRIC_WEAK
                )
                .setConfirmationRequired(false)
                .build()

            continuation.invokeOnCancellation {
                runCatching { prompt.cancelAuthentication() }
            }

            prompt.authenticate(promptInfo)
        }
    }
}
