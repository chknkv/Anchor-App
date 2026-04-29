package com.chknkv.corenetwork.token

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val KEY_ACCESS_TOKEN = "corenetwork_access_token"
private const val KEY_REFRESH_TOKEN = "corenetwork_refresh_token"

/**
 * Реализация [TokenRepository] на основе platform-secure [Settings].
 *
 * Android: настройки хранятся в EncryptedSharedPreferences.
 * iOS: настройки хранятся в Keychain через multiplatform-settings-keychain.
 *
 * [Mutex] сериализует операции чтения/записи, предотвращая race condition
 * между конкурентным обновлением токенов и вызовом [clearTokens] при logout.
 *
 * ВНИМАНИЕ: [Mutex] не реентрантен. Методы этого класса нельзя вызывать
 * из лямбд, уже выполняющихся внутри другого `withLock` этого же экземпляра —
 * это приведёт к deadlock.
 *
 * @param settings Платформенное защищённое хранилище, созданное через [createSecureTokenSettings].
 */
internal class TokenRepositoryImpl(
    private val settings: Settings,
) : TokenRepository {

    private val mutex = Mutex()

    override suspend fun getAccessToken(): String? = mutex.withLock {
        settings.getStringOrNull(KEY_ACCESS_TOKEN)
    }

    override suspend fun getRefreshToken(): String? = mutex.withLock {
        settings.getStringOrNull(KEY_REFRESH_TOKEN)
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) = mutex.withLock {
        settings[KEY_ACCESS_TOKEN] = accessToken
        settings[KEY_REFRESH_TOKEN] = refreshToken
    }

    override suspend fun clearTokens() = mutex.withLock {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }
}
