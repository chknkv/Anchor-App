package com.chknkv.feature.assistant.models.presentation

/**
 * Данные, полученные от [AssistanceInteractor] и готовые к отображению.
 *
 * @param quoteText Краткий текст цитаты для карточки карусели.
 * @param quoteDetailText Расширенный текст, открывающийся в BottomSheet.
 * @param isQuoteSheetVisible Флаг видимости BottomSheet с развёрнутой цитатой.
 */
internal data class AssistanceWidgetUiResult(
    val quoteText: String,
    val quoteDetailText: String,
    val isQuoteSheetVisible: Boolean = false,
)
