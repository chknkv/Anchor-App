package com.chknkv.coreutils

import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Реализация получения системного языка для Android.
 * Использует [Resources.getSystem] для доступа к текущей конфигурации.
 */
actual fun getSystemLanguageCode(): String {
    return Resources.getSystem().configuration.locales[0].language
}

/**
 * Реализация обновления локали для Android.
 * Использует [AppCompatDelegate.setApplicationLocales] для поддержки Per-app language 
 * и автоматического обновления ресурсов. Требует использования AppCompat-темы.
 */
actual fun updateSystemLocale(languageCode: String) {
    val appLocales = LocaleListCompat.forLanguageTags(languageCode)
    AppCompatDelegate.setApplicationLocales(appLocales)
}

/**
 * Получает код языка, установленный через настройки приложения в Android 13+.
 */
actual fun getPlatformAppLanguageCode(): String? {
    val locales = AppCompatDelegate.getApplicationLocales()
    if (locales.isEmpty) return null
    return locales[0]?.language
}
