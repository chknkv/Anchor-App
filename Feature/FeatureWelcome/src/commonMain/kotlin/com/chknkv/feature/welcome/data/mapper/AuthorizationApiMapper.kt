package com.chknkv.feature.welcome.data.mapper

import com.chknkv.feature.welcome.models.data.OtpSendResponse
import com.chknkv.feature.welcome.models.data.OtpVerifyResponse

/**
 * Сетевой маппер для OTP-авторизации по email.
 *
 * Отвечает исключительно за выполнение HTTP-запросов и возврат сырых data-моделей.
 * Бизнес-логика и маппинг ошибок — ответственность [com.chknkv.feature.welcome.data.repository.AuthorizationRepository].
 */
internal interface AuthorizationApiMapper {

    /**
     * Отправляет OTP-код на указанный email.
     *
     * `POST auth/otp/send`
     *
     * @param email Адрес электронной почты пользователя.
     * @return Ответ сервера с идентификатором сессии.
     * @throws com.chknkv.corenetwork.api.NetworkException При сетевой или серверной ошибке.
     */
    suspend fun sendOtp(email: String): OtpSendResponse

    /**
     * Верифицирует введённый OTP-код.
     *
     * `POST auth/otp/verify`
     *
     * @param sessionId Идентификатор сессии, полученный в [sendOtp].
     * @param otp Код из письма (5 символов).
     * @return Ответ сервера с токенами и флагом первой авторизации.
     * @throws com.chknkv.corenetwork.api.NetworkException При сетевой или серверной ошибке.
     */
    suspend fun verifyOtp(sessionId: String, otp: String): OtpVerifyResponse

    /**
     * Повторно отправляет OTP-код для текущей сессии.
     *
     * `POST auth/otp/resend`
     *
     * @param sessionId Идентификатор активной сессии.
     * @throws com.chknkv.corenetwork.api.NetworkException При сетевой или серверной ошибке.
     */
    suspend fun resendOtp(sessionId: String)
}
