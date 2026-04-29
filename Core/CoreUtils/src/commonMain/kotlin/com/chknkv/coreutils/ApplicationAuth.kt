package com.chknkv.coreutils

import com.russhwolf.settings.Settings

/**
 * Вспомогательный объект для управления состоянием авторизации на уровне платформы.
 * Использует [Settings] для персистентного хранения флага авторизации.
 */
object ApplicationAuth {
    private val settings: Settings by lazy { Settings() }

    private fun getAuthKey(appIdentifier: AppIdentifier): String {
        return "${appIdentifier.name}_IS_AUTHORIZED"
    }

    /**
     * Сохраняет состояние авторизации пользователя.
     * @param appIdentifier Идентификатор приложения для изоляции данных.
     * @param isAuthorized true, если пользователь успешно авторизован.
     */
    fun setAuthorized(appIdentifier: AppIdentifier, isAuthorized: Boolean) {
        settings.putBoolean(getAuthKey(appIdentifier), isAuthorized)
    }

    /**
     * Проверяет, авторизован ли пользователь.
     * @param appIdentifier Идентификатор приложения.
     * @return true, если флаг авторизации установлен в true, иначе false.
     */
    fun isAuthorized(appIdentifier: AppIdentifier): Boolean {
        return settings.getBoolean(getAuthKey(appIdentifier), false)
    }
}
