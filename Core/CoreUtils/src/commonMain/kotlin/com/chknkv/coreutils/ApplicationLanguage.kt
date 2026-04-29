package com.chknkv.coreutils

import com.russhwolf.settings.Settings

/**
 * Возвращает текущий код языка системы (например, "ru", "en").
 * Реализация зависит от платформы.
 */
expect fun getSystemLanguageCode(): String

/**
 * Обновляет конфигурацию локали системы для текущего контекста приложения.
 * Используется для динамической смены языка без перезагрузки всей системы.
 * @param languageCode ISO-код языка (например, "ru").
 */
expect fun updateSystemLocale(languageCode: String)

/**
 * Возвращает код языка, явно заданный для этого приложения в системных настройках платформы.
 * Актуально для Android 13+ (Per-app language settings).
 * @return Код языка или null, если используются системные настройки.
 */
expect fun getPlatformAppLanguageCode(): String?

/**
 * Поддерживаемые языки в приложении.
 */
enum class AppLanguage {
    /** Русский язык. */
    RUSSIAN,
    /** Английский язык. */
    ENGLISH
}

/**
 * Вспомогательный объект для управления языковыми настройками на уровне платформы.
 * Обеспечивает сохранение выбора пользователя и определение языка по умолчанию.
 */
object ApplicationLanguage {
    private val settings: Settings by lazy { Settings() }

    private fun getLanguageKey(appIdentifier: AppIdentifier): String {
        return "${appIdentifier.name}_APPLICATION_LANGUAGE"
    }

    /**
     * Сохраняет выбранный язык и применяет его в системе.
     * @param appIdentifier Идентификатор приложения для изоляции ключа в настройках.
     * @param language Выбранный язык.
     */
    fun setLanguage(appIdentifier: AppIdentifier, language: AppLanguage) {
        settings.putString(getLanguageKey(appIdentifier), language.name)
        
        val code = when (language) {
            AppLanguage.RUSSIAN -> "ru"
            AppLanguage.ENGLISH -> "en"
        }
        updateSystemLocale(code)
    }

    /**
     * Возвращает текущий язык приложения.
     * Приоритет выбора:
     * 1. Настройки платформы (Per-app settings).
     * 2. Сохраненное значение в приложении.
     * 3. Системный язык устройства.
     * 
     * @param appIdentifier Идентификатор приложения.
     * @return Текущий активный [AppLanguage].
     */
    fun getLanguage(appIdentifier: AppIdentifier): AppLanguage {
        val platformLang = getPlatformAppLanguageCode()
        if (platformLang != null) {
            return if (platformLang.startsWith("ru")) AppLanguage.RUSSIAN else AppLanguage.ENGLISH
        }

        val savedLanguageName = settings.getStringOrNull(getLanguageKey(appIdentifier))
        if (savedLanguageName != null) {
            return try {
                AppLanguage.valueOf(savedLanguageName)
            } catch (_: Exception) {
                resolveDefaultLanguage()
            }
        }

        return resolveDefaultLanguage()
    }

    private fun resolveDefaultLanguage(): AppLanguage {
        val systemCode = getSystemLanguageCode()
        return if (systemCode.startsWith("ru")) AppLanguage.RUSSIAN else AppLanguage.ENGLISH
    }
}
