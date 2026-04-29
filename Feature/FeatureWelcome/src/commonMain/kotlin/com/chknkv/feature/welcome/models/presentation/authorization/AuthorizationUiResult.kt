package com.chknkv.feature.welcome.models.presentation.authorization

import com.chknkv.designsystem.otp.PinInputState

/**
 * Результирующее состояние экрана авторизации.
 * Содержит все данные, необходимые для отрисовки UI.
 *
 * @param email Текущее значение поля ввода email.
 * @param isGetOtpEnabled Активна ли кнопка "Get OTP" (email прошёл валидацию).
 * @param isLoading Показывать ли индикатор загрузки поверх экрана.
 * @param isError Произошла ли ошибка при запросе OTP.
 * @param otp Состояние OTP-элемента.
 */
data class AuthorizationUiResult(
    val email: String = "",
    val isGetOtpEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val otp: OtpUiResult = OtpUiResult()
)

/**
 * Состояние OTP-элемента.
 *
 * @param pinCode Текущий введенный код.
 * @param pinState Состояние ввода (Input, Loading, Error).
 * @param isSheetVisible Видимость шторки с вводом кода.
 * @param timerValue Значение таймера обратного отсчета (в секундах).
 * @param isResendAvailable Доступна ли кнопка повторной отправки кода.
 */
data class OtpUiResult(
    val pinCode: String = "",
    val pinState: PinInputState = PinInputState.Input,
    val isSheetVisible: Boolean = false,
    val timerValue: Int = 60,
    val isResendAvailable: Boolean = false
)
