package com.chknkv.corepasscode.models.presentation.createpasscode

/**
 * Одноразовые события экрана создания passcode.
 */
sealed interface CreatePasscodeUiEvent {

    /** Повтор passcode не совпал с первичным вводом — экран запускает shake-анимацию. */
    data object PasscodesDoNotMatch : CreatePasscodeUiEvent

    /** Passcode успешно создан и сохранён — флоу переходит к следующему шагу. */
    data object PasscodeCreated : CreatePasscodeUiEvent

    /** Пользователь подтвердил пропуск создания passcode. */
    data object SkipRequested : CreatePasscodeUiEvent
}
