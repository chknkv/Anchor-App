package com.chknkv.feature.welcome.data.repository

/**
 * Репозиторий OTP-авторизации по email.
 */
internal interface AuthorizationRepository {

    /**
     * Отправляет OTP-код на указанный email.
     *
     * @param email Адрес электронной почты пользователя.
     * @return Идентификатор сессии для последующей верификации.
     */
    suspend fun sendOtp(email: String): String

    /**
     * Верифицирует введённый OTP-код.
     *
     * После успешной верификации токены сохраняются в защищённое хранилище.
     *
     * @param sessionId Идентификатор сессии, полученный в [sendOtp].
     * @param otp Код из письма (5 символов).
     */
    suspend fun verifyOtp(sessionId: String, otp: String): Boolean

    /**
     * Повторно отправляет OTP-код для текущей сессии.
     *
     * @param sessionId Идентификатор сессии.
     */
    suspend fun resendOtp(sessionId: String)
}