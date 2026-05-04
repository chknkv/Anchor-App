package com.chknkv.feature.welcome.models.data

import kotlinx.serialization.Serializable

/** Тело запроса на повторную отправку OTP-кода. */
@Serializable
internal class OtpResendRequest(val sessionId: String)
