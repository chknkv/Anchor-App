package com.chknkv.feature.assistant.data.converter

import com.chknkv.feature.assistant.models.data.MotivationalQuoteBody
import com.chknkv.feature.assistant.models.domain.MotivationalQuote

/**
 * Конвертирует data-model [MotivationalQuoteBody] в domain-model [MotivationalQuote].
 */
internal fun MotivationalQuoteBody.toDomain(): MotivationalQuote = MotivationalQuote(
    text = text,
    detailText = detailText,
)
