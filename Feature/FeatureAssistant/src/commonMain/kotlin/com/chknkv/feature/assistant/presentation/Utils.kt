package com.chknkv.feature.assistant.presentation

import com.chknkv.feature.assistant.models.domain.MotivationalQuote
import com.chknkv.feature.assistant.models.presentation.QuoteUiResult

// region AssistanceWidget

/** Конвертирует domain-model [MotivationalQuote] в UI-model [QuoteUiResult]. */
internal fun MotivationalQuote.toQuoteUiResult(): QuoteUiResult = QuoteUiResult(
    quoteText = text,
    quoteDetailText = detailText,
)

// endregion
