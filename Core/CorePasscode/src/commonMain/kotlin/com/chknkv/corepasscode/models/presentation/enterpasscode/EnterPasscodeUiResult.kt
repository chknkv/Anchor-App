package com.chknkv.corepasscode.models.presentation.enterpasscode

import com.chknkv.corepasscode.models.domain.BiometricType

/**
 * Состояние экрана ввода passcode.
 *
 * @param enteredDigits Введённые пользователем цифры.
 * @param isError Режим подсветки ошибки на индикаторе.
 * @param isForgotAlertVisible Показан ли AlertAction "Забыли passcode?"
 * @param isBiometricAvailable Доступна ли биометрия на устройстве и включена пользователем.
 * @param biometricType Тип биометрии для отрисовки иконки (Face/Touch).
 * @param isChangeFlow `true` — режим верификации перед сменой (влияет на текст экрана).
 * @param shakeTrigger Счётчик ошибок для shake-анимации.
 */
data class EnterPasscodeUiResult(
    val enteredDigits: List<Int> = emptyList(),
    val isError: Boolean = false,
    val isForgotAlertVisible: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val biometricType: BiometricType = BiometricType.NONE,
    val isChangeFlow: Boolean = false,
    val shakeTrigger: Int = 0,
)
