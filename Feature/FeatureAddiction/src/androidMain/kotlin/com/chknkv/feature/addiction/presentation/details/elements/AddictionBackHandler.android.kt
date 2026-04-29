package com.chknkv.feature.addiction.presentation.details.elements

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.chknkv.designsystem.screen.BackHandler as DesignSystemBackHandler

@Composable
internal actual fun AddictionBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)

    DesignSystemBackHandler(enabled = enabled, onBack = onBack)
}
