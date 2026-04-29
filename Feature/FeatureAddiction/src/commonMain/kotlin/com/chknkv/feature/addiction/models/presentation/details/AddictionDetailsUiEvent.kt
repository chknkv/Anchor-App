package com.chknkv.feature.addiction.models.presentation.details

/**
 * Одноразовые события (Side Effects) экрана деталей привычки.
 */
internal sealed interface AddictionDetailsUiEvent {

    /** Сигнализирует об успешном удалении привычки. */
    data object HabitDeleted : AddictionDetailsUiEvent

    /** Сигнализирует о необходимости закрыть экран. */
    data object NavigateBack : AddictionDetailsUiEvent
}
