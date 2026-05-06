package com.chknkv.feature.welcome.models.data

import kotlinx.serialization.Serializable

/**
 * Ответ на запрос верификации OTP-кода.
 *
 * @param accessToken  JWT access-токен для аутентификации запросов.
 * @param refreshToken JWT refresh-токен для обновления access-токена.
 * @param isFirstAuthorized true — пользователь впервые авторизовался (регистрация);
 *   false — возвращающийся пользователь.
 */
@Serializable
internal data class OtpVerifyResponse(
    val accessToken: String,
    val refreshToken: String,
    val isFirstAuthorized: Boolean,
)
