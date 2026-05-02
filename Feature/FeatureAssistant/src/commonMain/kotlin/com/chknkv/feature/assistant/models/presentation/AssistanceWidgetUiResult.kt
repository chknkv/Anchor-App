package com.chknkv.feature.assistant.models.presentation

import androidx.compose.runtime.Immutable

/**
 * Результирующее состояние виджета помощи.
 *
 * @param quote Данные цитаты, или `null` если цитата недоступна (ошибка сети).
 *   При `null` карточка цитаты скрыта; карточка «Тревожная кнопка» отображается всегда.
 */
@Immutable
internal data class AssistanceWidgetUiResult(
    val quote: QuoteUiResult? = null,
)

/**
 * UI-модель для виджета "Мотивирующая цитата"
 *
 * @param quoteText Краткий текст цитаты для карточки карусели.
 * @param quoteDetailText Расширенный текст, открывающийся в BottomSheet.
 * @param isQuoteSheetVisible Флаг видимости BottomSheet с развёрнутой цитатой.
 */
@Immutable
internal data class QuoteUiResult(
    val quoteText: String,
    val quoteDetailText: String,
    val isQuoteSheetVisible: Boolean = false,
)

