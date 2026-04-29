package com.chknkv.corepasscode.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.chknkv.corepasscode.models.domain.BiometricContext

@Composable
actual fun rememberBiometricContext(): BiometricContext = remember { BiometricContext() }
