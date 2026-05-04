package com.chknkv.corenetwork.token

/**
 * Публичный контракт для сохранения JWT-токенов после успешной авторизации.
 *
 * Предоставляется Feature-модулям через DI, скрывая полный [TokenRepository]
 * (чтение, очистка — детали реализации сетевого слоя).
 */
interface TokenStorage {

    /**
     * Сохраняет пару токенов в защищённом хранилище.
     *
     * @param accessToken  JWT access-токен.
     * @param refreshToken JWT refresh-токен.
     */
    suspend fun saveTokens(accessToken: String, refreshToken: String)
}
