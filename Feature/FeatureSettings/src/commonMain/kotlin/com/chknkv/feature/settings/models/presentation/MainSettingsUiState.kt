package com.chknkv.feature.settings.models.presentation

/**
 * Состояние экрана главного списка настроек.
 */
sealed interface MainSettingsUiState {

    /**
     * Данные доступны — список настроек отображается.
     *
     * @param result Результирующее состояние для отрисовки UI.
     */
    data class Successful(val result: SettingsUiResult) : MainSettingsUiState
}
