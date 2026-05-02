package com.chknkv.feature.assistant.models.data

import com.chknkv.corenetwork.NetworkEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Ответ для эндпоинта `GET /assistant/motivational-quote`.
 */
@Serializable
internal class MotivationalQuoteResponse : NetworkEntity<MotivationalQuoteBody>()

/**
 * Тело ответа с мотивационной цитатой.
 *
 * @param text Краткий текст цитаты, отображаемый на карточке-карусели.
 * @param detailText Расширенный текст с дополнительным мотивационным содержанием,
 *   показываемый в BottomSheet при нажатии на карточку.
 */
@Serializable
internal data class MotivationalQuoteBody(
    @SerialName("text") val text: String,
    @SerialName("detail_text") val detailText: String,
)
