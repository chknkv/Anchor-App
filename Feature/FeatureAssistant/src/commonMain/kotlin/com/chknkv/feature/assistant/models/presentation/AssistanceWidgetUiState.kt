package com.chknkv.feature.assistant.models.presentation

/**
 * Состояние виджета помощи.
 *
 * - [Loading] — данные загружаются; отображается shimmer-скелетон.
 * - [Successful] — данные получены; отображается карусель карточек.
 * - [Error] — произошла ошибка; виджет не отображается (Unit).
 */
internal sealed interface AssistanceWidgetUiState {

    data object Loading : AssistanceWidgetUiState

    data class Successful(val result: AssistanceWidgetUiResult) : AssistanceWidgetUiState

    data class Error(val message: String? = null) : AssistanceWidgetUiState
}
