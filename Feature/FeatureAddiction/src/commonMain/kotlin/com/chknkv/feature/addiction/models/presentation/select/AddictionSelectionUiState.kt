package com.chknkv.feature.addiction.models.presentation.select

/**
 * Состояния экрана выбора привычек.
 */
internal sealed interface AddictionSelectionUiState {

    /** Начальное состояние до старта загрузки. */
    data object Init : AddictionSelectionUiState

    /** Идёт загрузка списка групп привычек. */
    data object Loading : AddictionSelectionUiState

    /**
     * Данные успешно загружены.
     *
     * @param result Состояние содержимого экрана.
     */
    data class Successful(val result: AddictionSelectionUiResult) : AddictionSelectionUiState

    /**
     * Ошибка загрузки данных.
     *
     * @param message Опциональное сообщение об ошибке.
     */
    data class Error(val message: String? = null) : AddictionSelectionUiState
}