package com.chknkv.feature.addiction.models.presentation.create

/**
 * Одноразовые события (side effects) экрана создания привычки.
 */
internal sealed interface AddictionCreateUiEvent {

    /** Привычка успешно создана — нужно закрыть экран. */
    data object OnCreated : AddictionCreateUiEvent
}
