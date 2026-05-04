package com.chknkv.feature.welcome.models.data

import com.chknkv.corenetwork.NetworkEntity
import kotlinx.serialization.Serializable

/** Конверт ответа на запрос верификации OTP-кода. */
@Serializable
internal class OtpVerifyResponse : NetworkEntity<OtpVerifyBody>()

/**
 * Тело успешного ответа на верификацию OTP.
 *
 * @property accessToken  JWT access-токен для аутентификации запросов.
 * @property refreshToken JWT refresh-токен для обновления access-токена.
 * @property isFirstAuthorized true — пользователь впервые авторизовался (регистрация);
 *   false — возвращающийся пользователь.
 */
@Serializable
internal class OtpVerifyBody(
    val accessToken: String,
    val refreshToken: String,
    val isFirstAuthorized: Boolean,
)
