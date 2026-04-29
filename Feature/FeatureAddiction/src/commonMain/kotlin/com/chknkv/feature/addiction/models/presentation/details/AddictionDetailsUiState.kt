package com.chknkv.feature.addiction.models.presentation.details

/**
 * Состояния экрана с информацией о привычке.
 */
internal sealed interface AddictionDetailsUiState {
    /** Начальное состояние до загрузки. */
    data object Init : AddictionDetailsUiState
    
    /** Состояние загрузки данных. */
    data object Loading : AddictionDetailsUiState
    
    /** 
     * Успешное состояние с данными. 
     * 
     * @property result Данные для отображения.
     */
    data class Successful(val result: AddictionDetailsUiResult) : AddictionDetailsUiState
    
    /** 
     * Состояние ошибки. 
     * 
     * @property message Локализованное сообщение об ошибке.
     */
    data class Error(val message: String? = null) : AddictionDetailsUiState
}
