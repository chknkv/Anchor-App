package com.chknkv.corepasscode.models.presentation.enterpasscode

/**
 * Одноразовые события экрана ввода passcode.
 */
sealed interface EnterPasscodeUiEvent {

    /** Введённый passcode не совпал с сохранённым хэшем — экран запускает shake-анимацию. */
    data object InvalidPasscode : EnterPasscodeUiEvent

    /** Пользователь успешно прошёл верификацию (паролем или биометрией). */
    data object EnterSuccess : EnterPasscodeUiEvent

    /** Пользователь подтвердил сброс passcode — флоу вызывает [onForgotPasscode]. */
    data object ForgotPasscodeRequested : EnterPasscodeUiEvent
}
