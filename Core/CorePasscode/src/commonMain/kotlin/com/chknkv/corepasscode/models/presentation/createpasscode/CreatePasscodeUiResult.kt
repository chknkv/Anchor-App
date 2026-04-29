package com.chknkv.corepasscode.models.presentation.createpasscode

/**
 * Состояние экрана создания passcode.
 *
 * @param enteredDigits Введённые пользователем цифры (0..5 на двух шагах).
 * @param isConfirming `true` — шаг подтверждения (повтор passcode).
 * @param isSkipAlertVisible Показан ли AlertAction пропуска.
 * @param isSkipAvailable Доступна ли кнопка "Пропустить" (скрыта в режиме Change).
 * @param shakeTrigger Счётчик ошибок; инкремент запускает shake-анимацию индикатора.
 */
data class CreatePasscodeUiResult(
    val enteredDigits: List<Int> = emptyList(),
    val isConfirming: Boolean = false,
    val isSkipAlertVisible: Boolean = false,
    val isSkipAvailable: Boolean = true,
    val shakeTrigger: Int = 0,
)
