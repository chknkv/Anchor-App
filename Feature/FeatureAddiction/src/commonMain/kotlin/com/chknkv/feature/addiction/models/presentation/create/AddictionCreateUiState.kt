package com.chknkv.feature.addiction.models.presentation.create

/**
 * Состояния экрана создания привычки.
 */
internal sealed interface AddictionCreateUiState {
    /** Начальное состояние до инициализации. */
    data object Init : AddictionCreateUiState
    /** Экран загружается (зарезервировано — при инициализации не используется). */
    data object Loading : AddictionCreateUiState
    /** Форма готова к работе. @param result Текущее состояние формы. */
    data class Successful(val result: AddictionCreateUiResult) : AddictionCreateUiState
    /** Фатальная ошибка при инициализации. @param message Текст ошибки. */
    data class Error(val message: String? = null) : AddictionCreateUiState
}
