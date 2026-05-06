package com.chknkv.feature.assistant.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Ответ для эндпоинта `GET /assistant/motivational-quote`.
 *
 * @param text Краткий текст цитаты, отображаемый на карточке-карусели.
 * @param detailText Расширенный текст с дополнительным мотивационным содержанием,
 *   показываемый в BottomSheet при нажатии на карточку.
 */
@Serializable
internal data class MotivationalQuoteResponse(
    @SerialName("text") val text: String,
    @SerialName("detail_text") val detailText: String,
)
