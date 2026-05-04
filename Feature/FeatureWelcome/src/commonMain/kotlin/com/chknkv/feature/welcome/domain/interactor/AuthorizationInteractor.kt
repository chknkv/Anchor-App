package com.chknkv.feature.welcome.domain.interactor

import com.chknkv.feature.welcome.data.repository.AuthorizationRepository

/**
 * Интерактор для бизнес-логики OTP-авторизации по email.
 *
 * Делегирует сетевые операции в [AuthorizationRepository].
 * Обработка ошибок ([com.chknkv.feature.welcome.domain.OtpException]) — ответственность ViewModel.
 */
internal interface AuthorizationInteractor {

    /**
     * Отправляет OTP-код на указанный email.
     *
     * @param email Адрес электронной почты пользователя.
     * @return Идентификатор сессии для последующей верификации.
     * @throws com.chknkv.corenetwork.api.NetworkException При сетевой ошибке.
     */
    suspend fun sendOtp(email: String): String

    /**
     * Верифицирует введённый OTP-код.
     *
     * @param sessionId Идентификатор сессии, полученный в [sendOtp].
     * @param otp Код из письма.
     * @return `true` если пользователь авторизуется впервые, `false` для возвращающегося.
     * @throws com.chknkv.feature.welcome.domain.OtpException.InvalidOtp Код неверен.
     * @throws com.chknkv.feature.welcome.domain.OtpException.SessionExpired Сессия истекла.
     * @throws com.chknkv.corenetwork.api.NetworkException При иных сетевых ошибках.
     */
    suspend fun verifyOtp(sessionId: String, otp: String): Boolean

    /**
     * Повторно отправляет OTP-код для текущей сессии.
     *
     * @param sessionId Идентификатор сессии.
     * @throws com.chknkv.feature.welcome.domain.OtpException.SessionExpired Сессия истекла.
     * @throws com.chknkv.corenetwork.api.NetworkException При иных сетевых ошибках.
     */
    suspend fun resendOtp(sessionId: String)

    /**
     * Обрабатывает нажатие на ссылку Terms of Service.
     */
    fun handleTermsClicked()
}