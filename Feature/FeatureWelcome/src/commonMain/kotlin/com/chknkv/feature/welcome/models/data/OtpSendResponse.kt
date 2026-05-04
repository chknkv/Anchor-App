package com.chknkv.feature.welcome.models.data

import com.chknkv.corenetwork.NetworkEntity
import kotlinx.serialization.Serializable

/** Конверт ответа на запрос отправки OTP-кода. */
@Serializable
internal class OtpSendResponse : NetworkEntity<OtpSendBody>()

/**
 * Тело успешного ответа на запрос отправки OTP.
 *
 * @property sessionId Идентификатор сессии, необходимый для верификации OTP и повторной отправки.
 */
@Serializable
internal class OtpSendBody(val sessionId: String)
