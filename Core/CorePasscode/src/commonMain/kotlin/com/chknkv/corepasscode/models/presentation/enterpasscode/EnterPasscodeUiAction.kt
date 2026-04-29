package com.chknkv.corepasscode.models.presentation.enterpasscode

/**
 * Действия пользователя на экране ввода passcode (вход или верификация).
 */
sealed interface EnterPasscodeUiAction {

    /** Инициализация экрана. Запускает авто-триггер биометрии, если включена. */
    data object Init : EnterPasscodeUiAction

    /**
     * Нажата цифровая клавиша.
     *
     * @param digit Введённая цифра 0..9.
     */
    data class NumberClick(val digit: Int) : EnterPasscodeUiAction

    /** Нажат backspace. */
    data object DeleteClick : EnterPasscodeUiAction

    /** Пользователь подтвердил сброс passcode. */
    data object ForgotPasscode : EnterPasscodeUiAction

    /** Показать алерт "Забыли passcode?" */
    data object ShowForgotAlert : EnterPasscodeUiAction

    /** Скрыть алерт "Забыли passcode?" */
    data object HideForgotAlert : EnterPasscodeUiAction

    /** Пользователь нажал иконку биометрии (или авто-триггер). */
    data object TryBiometric : EnterPasscodeUiAction
}
