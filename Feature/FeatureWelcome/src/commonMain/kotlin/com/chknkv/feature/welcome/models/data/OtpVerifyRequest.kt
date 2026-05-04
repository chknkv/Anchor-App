package com.chknkv.feature.welcome.models.data

import kotlinx.serialization.Serializable

/** Тело запроса на верификацию OTP-кода. */
@Serializable
internal class OtpVerifyRequest(val sessionId: String, val otp: String)
