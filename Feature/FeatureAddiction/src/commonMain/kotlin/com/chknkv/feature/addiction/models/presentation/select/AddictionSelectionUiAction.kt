package com.chknkv.feature.addiction.models.presentation.select

/**
 * Интенты экрана выбора привычек.
 */
internal sealed interface AddictionSelectionUiAction {

    /** Инициализировать экран. */
    data object Init : AddictionSelectionUiAction

    /**
     * Пользователь переключил выбор привычки.
     *
     * @param addictionId Идентификатор привычки.
     */
    data class OnAddictionToggled(val addictionId: Int) : AddictionSelectionUiAction

    /** Пользователь нажал «Пропустить». */
    data object OnSkipClicked : AddictionSelectionUiAction

    /** Пользователь нажал «Далее» (активна при наличии хотя бы одной выбранной привычки). */
    data object OnNextClicked : AddictionSelectionUiAction
}