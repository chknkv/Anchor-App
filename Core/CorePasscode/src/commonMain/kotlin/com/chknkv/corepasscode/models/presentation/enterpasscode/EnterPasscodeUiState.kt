package com.chknkv.corepasscode.models.presentation.enterpasscode

/**
 * Состояние экрана ввода passcode.
 */
sealed interface EnterPasscodeUiState {

    /** Начальное состояние до вызова [EnterPasscodeViewModel.initScreen]. */
    data object Init : EnterPasscodeUiState

    /**
     * Данные готовы к отображению.
     *
     * @param result Результирующие данные для отрисовки UI.
     */
    data class Successful(val result: EnterPasscodeUiResult) : EnterPasscodeUiState
}
