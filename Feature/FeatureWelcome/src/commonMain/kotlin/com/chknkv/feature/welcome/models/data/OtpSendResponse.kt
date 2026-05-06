package com.chknkv.feature.welcome.models.data

import kotlinx.serialization.Serializable

/**
 * Ответ на запрос отправки OTP-кода.
 *
 * @param sessionId Идентификатор сессии, необходимый для верификации OTP и повторной отправки.
 */
@Serializable
internal data class OtpSendResponse(val sessionId: String)
