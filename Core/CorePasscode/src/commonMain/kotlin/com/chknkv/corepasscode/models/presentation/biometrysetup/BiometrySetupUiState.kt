package com.chknkv.corepasscode.models.presentation.biometrysetup

/**
 * Состояние экрана подключения биометрии.
 */
sealed interface BiometrySetupUiState {

    /** Начальное состояние до вызова [BiometrySetupViewModel.initScreen]. */
    data object Init : BiometrySetupUiState

    /**
     * Данные готовы к отображению.
     *
     * @param result Результирующие данные для отрисовки UI.
     */
    data class Successful(val result: BiometrySetupUiResult) : BiometrySetupUiState
}
