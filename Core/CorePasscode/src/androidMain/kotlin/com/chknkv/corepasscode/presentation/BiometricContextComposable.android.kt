package com.chknkv.corepasscode.presentation

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.chknkv.corepasscode.models.domain.BiometricContext

@Composable
actual fun rememberBiometricContext(): BiometricContext {
    val context = LocalContext.current
    return remember(context) {
        val activity = context.findFragmentActivity()
            ?: error(
                "PasscodeFlow requires host Activity to extend FragmentActivity " +
                        "(e.g. AppCompatActivity). Current: ${context::class.simpleName}"
            )
        BiometricContext(activity)
    }
}

private tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}
