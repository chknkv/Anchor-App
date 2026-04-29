package com.chknkv.designsystem.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Интерфейс для перехвата события "Назад".
 */
interface BackInterceptor {
    val isEnabled: Boolean
    fun onBack()
}

/**
 * Реализация интерцептора, позволяющая динамически менять состояние.
 */
class BackInterceptorScope : BackInterceptor {
    override var isEnabled by mutableStateOf(false)
    var onBackAction by mutableStateOf<(() -> Unit)?>(null)

    override fun onBack() {
        onBackAction?.invoke()
    }
}

val LocalBackInterceptor = staticCompositionLocalOf<BackInterceptor> {
    object : BackInterceptor {
        override val isEnabled = false
        override fun onBack() {}
    }
}

/**
 * Компонент для регистрации перехватчика в текущей области композиции.
 */
@Composable
fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    val interceptor = LocalBackInterceptor.current as? BackInterceptorScope ?: return

    DisposableEffect(enabled, onBack) {
        if (enabled) {
            interceptor.isEnabled = true
            interceptor.onBackAction = onBack
        }
        onDispose {
            if (enabled) {
                interceptor.isEnabled = false
                interceptor.onBackAction = null
            }
        }
    }
}
