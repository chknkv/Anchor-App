package com.chknkv.designsystem.passcode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.AudioToolbox.AudioServicesPlaySystemSound

@Composable
actual fun rememberKeyClickPlayer(): () -> Unit {
    return remember { { AudioServicesPlaySystemSound(1104u) } }
}
