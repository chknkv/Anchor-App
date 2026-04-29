package com.chknkv.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import com.chknkv.coreutils.AppLanguage

/**
 * CompositionLocal для управления текущей локалью приложения внутри Compose.
 * 
 * Позволяет динамически изменять язык интерфейса без перезапуска приложения.
 */
expect object LocalAppLocale {

    /**
     * Предоставляет значение для CompositionLocalProvider.
     * 
     * @param language Выбранный язык приложения.
     */
    @Composable
    infix fun provides(language: AppLanguage): ProvidedValue<*>
}
