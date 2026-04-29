package com.chknkv.feature.assistant.models.presentation

/**
 * Действия пользователя или системы, обрабатываемые [AssistanceWidgetViewModel].
 */
internal sealed interface AssistanceWidgetUiAction {

    /** Первичная инициализация виджета: запрашивает данные впервые. */
    data object Init : AssistanceWidgetUiAction

    /** Принудительное обновление данных виджета. */
    data object Refresh : AssistanceWidgetUiAction

    /** Открыть шторку с подробной цитатой. */
    data object ShowQuoteSheet : AssistanceWidgetUiAction

    /** Закрыть шторку с подробной цитатой. */
    data object HideQuoteSheet : AssistanceWidgetUiAction
}
