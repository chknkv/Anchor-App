package com.chknkv.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.chknkv.coreutils.AppLanguage
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

actual object LocalAppLocale {

    private val default = NSLocale.preferredLanguages.first() as String
    private val InternalLocalAppLocale = staticCompositionLocalOf { default }

    @Composable
    actual infix fun provides(language: AppLanguage): ProvidedValue<*> {
        val new = when (language) {
            AppLanguage.RUSSIAN -> "ru"
            AppLanguage.ENGLISH -> "en"
        }

        NSUserDefaults.standardUserDefaults.setObject(listOf(new), "AppleLanguages")
        
        return InternalLocalAppLocale.provides(new)
    }
}
