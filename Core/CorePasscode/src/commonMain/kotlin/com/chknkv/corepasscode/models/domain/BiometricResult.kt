package com.chknkv.corepasscode.models.domain

/**
 * Результат запроса биометрической аутентификации.
 */
sealed interface BiometricResult {

    /** Пользователь прошёл аутентификацию. */
    data object Success : BiometricResult

    /** Пользователь отменил аутентификацию (кнопка отмены или back). */
    data object Cancelled : BiometricResult

    /**
     * Произошла ошибка при аутентификации.
     *
     * @param reason Описание причины (передаётся в UI или лог).
     */
    data class Error(val reason: String) : BiometricResult
}
