package com.chknkv.feature.assistant.models.presentation

import androidx.compose.runtime.Immutable

/**
 * Состояние виджета помощи.
 *
 * - [Loading] — данные загружаются; отображается shimmer-скелетон.
 * - [Successful] — данные получены; отображается карусель из двух карточек («Цитата» + «Тревожная кнопка»).
 */
internal sealed interface AssistanceWidgetUiState {

    @Immutable
    data object Loading : AssistanceWidgetUiState

    @Immutable
    data class Successful(val result: AssistanceWidgetUiResult) : AssistanceWidgetUiState

}
