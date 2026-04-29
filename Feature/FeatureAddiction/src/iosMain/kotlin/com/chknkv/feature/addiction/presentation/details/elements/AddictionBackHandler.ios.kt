package com.chknkv.feature.addiction.presentation.details.elements

import androidx.compose.runtime.Composable
import com.chknkv.designsystem.screen.BackHandler

@Composable
internal actual fun AddictionBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}
