package com.chknkv.corepasscode.presentation

import androidx.compose.runtime.Composable
import com.chknkv.corepasscode.models.domain.BiometricContext

/**
 * Возвращает платформозависимый контекст [BiometricContext] для биометрической аутентификации.
 * 
 * На Android возвращает обертку над FragmentActivity.
 */
@Composable
expect fun rememberBiometricContext(): BiometricContext
