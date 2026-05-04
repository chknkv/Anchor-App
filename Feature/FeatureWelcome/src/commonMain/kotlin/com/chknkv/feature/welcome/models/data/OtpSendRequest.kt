package com.chknkv.feature.welcome.models.data

import kotlinx.serialization.Serializable

/** Тело запроса на отправку OTP-кода. */
@Serializable
internal class OtpSendRequest(val email: String)
