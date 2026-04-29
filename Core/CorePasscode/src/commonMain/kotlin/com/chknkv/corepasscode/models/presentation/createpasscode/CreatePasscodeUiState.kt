package com.chknkv.corepasscode.models.presentation.createpasscode

/**
 * Состояние экрана создания passcode.
 */
sealed interface CreatePasscodeUiState {

    /** Начальное состояние. */
    data object Init : CreatePasscodeUiState

    /**
     * Данные готовы к отображению.
     *
     * @param result Результирующие данные для отрисовки UI.
     */
    data class Successful(val result: CreatePasscodeUiResult) : CreatePasscodeUiState
}
