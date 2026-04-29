package com.chknkv.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import com.chknkv.coreutils.AppLanguage
import java.util.Locale
import androidx.compose.ui.platform.LocalResources

actual object LocalAppLocale {

    private var default: Locale? = null

    @Composable
    actual infix fun provides(language: AppLanguage): ProvidedValue<*> {
        val configuration = LocalConfiguration.current

        if (default == null) {
            default = Locale.getDefault()
        }

        val new = when (language) {
            AppLanguage.RUSSIAN -> Locale("ru")
            AppLanguage.ENGLISH -> Locale("en")
        }

        Locale.setDefault(new)
        configuration.setLocale(new)
        val resources = LocalResources.current

        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return LocalConfiguration.provides(configuration)
    }
}
