package com.chknkv.corepasscode.models.presentation.createpasscode

/**
 * Действия пользователя на экране создания passcode.
 */
sealed interface CreatePasscodeUiAction {

    /** Инициализация экрана (сброс состояния, подтягивание флагов). */
    data object Init : CreatePasscodeUiAction

    /**
     * Пользователь нажал цифровую клавишу.
     *
     * @param digit Введённая цифра 0..9.
     */
    data class NumberClick(val digit: Int) : CreatePasscodeUiAction

    /** Пользователь нажал backspace. */
    data object DeleteClick : CreatePasscodeUiAction

    /** Пользователь подтвердил пропуск создания passcode (из алерта). */
    data object Skip : CreatePasscodeUiAction

    /** Пользователь нажал кнопку "Пропустить" — показать алерт. */
    data object ShowSkipAlert : CreatePasscodeUiAction

    /** Пользователь отменил алерт пропуска. */
    data object DismissSkipAlert : CreatePasscodeUiAction
}
