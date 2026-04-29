package com.chknkv.feature.addiction.models.presentation.select

/**
 * Одноразовые события экрана выбора привычек (side effects).
 */
internal sealed interface AddictionSelectionUiEvent {

    /** Пользователь попытался выбрать привычку сверх лимита — нужен тактильный отклик. */
    data object OnSelectionLimitReached : AddictionSelectionUiEvent

    /** Экран завершён (пропуск или успешное сохранение). */
    data object OnFinished : AddictionSelectionUiEvent
}