package com.chknkv.coreutils

import com.russhwolf.settings.Settings

/**
 * Перечисление доступных тем оформления приложения.
 */
enum class AppTheme {

    /** Следовать системным настройкам устройства. */
    SYSTEM,

    /** Принудительно светлая тема. */
    LIGHT,

    /** Принудительно тёмная тема. */
    DARK
}

/**
 * Идентификаторы приложений для изоляции настроек в общем хранилище.
 * Позволяет использовать один и тот же модуль настроек в разных продуктах.
 */
enum class AppIdentifier {

    ANCHOR
}

/**
 * Вспомогательный объект для персистентного хранения выбранной темы оформления.
 */
object ApplicationTheme {
    private val settings: Settings by lazy { Settings() }

    private fun getThemeKey(appIdentifier: AppIdentifier): String {
        return "${appIdentifier.name}_APPLICATION_THEME"
    }

    /**
     * Сохраняет выбранную тему оформления.
     * @param appIdentifier Идентификатор приложения.
     * @param theme Тема оформления для сохранения.
     */
    fun setTheme(appIdentifier: AppIdentifier, theme: AppTheme) {
        settings.putString(getThemeKey(appIdentifier), theme.name)
    }

    /**
     * Возвращает сохраненную тему оформления.
     * @param appIdentifier Идентификатор приложения.
     * @return Сохраненная [AppTheme] или [AppTheme.SYSTEM] по умолчанию.
     */
    fun getTheme(appIdentifier: AppIdentifier): AppTheme {
        val themeName = settings.getString(getThemeKey(appIdentifier), AppTheme.SYSTEM.name)
        return try {
            AppTheme.valueOf(themeName)
        } catch (_: Exception) {
            AppTheme.SYSTEM
        }
    }
}
