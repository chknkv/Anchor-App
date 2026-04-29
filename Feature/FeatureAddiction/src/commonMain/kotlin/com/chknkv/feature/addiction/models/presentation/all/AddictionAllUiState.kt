package com.chknkv.feature.addiction.models.presentation.all

/**
 * Состояния экрана всех привычек пользователя.
 */
internal sealed interface AddictionAllUiState {

    /** Начальное состояние. */
    data object Init : AddictionAllUiState

    /** Экран загружает данные — отображается полноэкранный индикатор. */
    data object Loading : AddictionAllUiState

    /**
     * Данные успешно загружены.
     *
     * @param result Результат с привычками, категориями и текущим фильтром.
     */
    data class Successful(val result: AddictionAllUiResult) : AddictionAllUiState

    /**
     * Произошла ошибка при загрузке данных.
     *
     * @param message Текст ошибки для отображения (может быть null).
     */
    data class Error(val message: String? = null) : AddictionAllUiState

    /** Пользователь ещё не добавил ни одной привычки — список пуст. */
    data object Empty : AddictionAllUiState
}
