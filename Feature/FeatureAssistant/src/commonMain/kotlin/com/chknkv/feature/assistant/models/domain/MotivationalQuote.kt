package com.chknkv.feature.assistant.models.domain

/**
 * Domain-модель мотивационной цитаты.
 *
 * @param text Краткий текст цитаты, отображаемый на карточке-карусели.
 * @param detailText Расширенный текст с дополнительным мотивационным содержанием,
 *   показываемый в BottomSheet при нажатии на карточку.
 */
internal data class MotivationalQuote(
    val text: String,
    val detailText: String,
)
