package com.chknkv.coreutils

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

/**
 * Реализация получения системного языка для iOS.
 * Использует [NSLocale.currentLocale.languageCode].
 */
actual fun getSystemLanguageCode(): String {
    return NSLocale.currentLocale.languageCode
}

/**
 * Реализация обновления локали для iOS.
 * Записывает выбранный язык в "AppleLanguages" через [NSUserDefaults].
 * Изменения вступают в силу после перезапуска приложения (особенность iOS).
 */
actual fun updateSystemLocale(languageCode: String) {
    NSUserDefaults.standardUserDefaults.setObject(listOf(languageCode), "AppleLanguages")
    NSUserDefaults.standardUserDefaults.synchronize()
}

/**
 * Получает предпочтительный язык из системных настроек iOS.
 */
actual fun getPlatformAppLanguageCode(): String? {
    val languages = NSLocale.preferredLanguages
    if (languages.isEmpty()) return null
    val firstLanguage = languages[0] as String
    
    return firstLanguage.split("-")[0]
}
