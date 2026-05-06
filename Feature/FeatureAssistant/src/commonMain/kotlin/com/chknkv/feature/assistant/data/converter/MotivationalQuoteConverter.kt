package com.chknkv.feature.assistant.data.converter

import com.chknkv.feature.assistant.models.data.MotivationalQuoteResponse
import com.chknkv.feature.assistant.models.domain.MotivationalQuote

/**
 * Конвертирует [MotivationalQuoteResponse] в domain-model [MotivationalQuote].
 */
internal fun MotivationalQuoteResponse.toDomain(): MotivationalQuote = MotivationalQuote(
    text = text,
    detailText = detailText,
)
