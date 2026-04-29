package com.chknkv.designsystem.passcode

import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberKeyClickPlayer(): () -> Unit {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    return remember { { audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, -1f) } }
}
