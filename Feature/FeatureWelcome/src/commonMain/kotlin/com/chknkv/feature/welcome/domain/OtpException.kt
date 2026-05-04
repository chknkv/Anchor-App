package com.chknkv.feature.welcome.domain

/**
 * Доменные ошибки OTP-авторизации.
 */
internal sealed class OtpException : Exception() {

    /** Введённый OTP-код неверен (HTTP 400). */
    data object InvalidOtp : OtpException()

    /** Сессия истекла, необходимо начать авторизацию заново (HTTP 410). */
    data object SessionExpired : OtpException()
}
