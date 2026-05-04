package com.chknkv.corenetwork.token

/**
 * Контракт для чтения и записи JWT-токенов в защищённом хранилище платформы.
 *
 * Реализация обязана обеспечивать атомарность операций чтения/записи.
 * Расширяет [TokenStorage] — Feature-модули работают только через [TokenStorage],
 * не имея доступа к методам чтения и очистки.
 */
interface TokenRepository : TokenStorage {

    /**
     * Возвращает сохранённый access-токен.
     *
     * @return Access-токен или `null`, если отсутствует.
     */
    suspend fun getAccessToken(): String?

    /**
     * Возвращает сохранённый refresh-токен.
     *
     * @return Refresh-токен или `null`, если отсутствует.
     */
    suspend fun getRefreshToken(): String?

    /**
     * Сохраняет оба токена.
     *
     * @param accessToken  Новый JWT access-токен.
     * @param refreshToken Новый JWT refresh-токен.
     */
    override suspend fun saveTokens(accessToken: String, refreshToken: String)

    /**
     * Удаляет оба токена из хранилища (например, при выходе из аккаунта).
     */
    suspend fun clearTokens()
}
