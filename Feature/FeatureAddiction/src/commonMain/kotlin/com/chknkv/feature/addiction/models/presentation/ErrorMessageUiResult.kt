package com.chknkv.feature.addiction.models.presentation

import androidx.compose.runtime.Immutable

/**
 * Модель данных для отображения ошибок в интерфейсе.
 *
 * @property isNetworkError Флаг, указывающий на наличие сетевой ошибки (например, отсутствие интернета).
 * @property message Текст сообщения об ошибке для отображения пользователю.
 */
@Immutable
internal data class ErrorMessageUiResult(
    val isNetworkError: Boolean = false,
    val message: String? = null
)