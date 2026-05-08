package com.chknkv.feature.welcome.models.presentation.authorization

/**
 * Sealed-интерфейс действий пользователя на экране авторизации.
 * Используется для передачи интентов из UI-слоя в компонент.
 */
sealed interface AuthorizationUiAction {

    /**
     * Пользователь изменил текст в поле email.
     *
     * @param email Новое значение текстового поля.
     */
    data class OnEmailChanged(val email: String) : AuthorizationUiAction

    /**
     * Пользователь нажал кнопку "Get OTP".
     */
    data object OnGetOtpClicked : AuthorizationUiAction

    /**
     * Пользователь нажал на ссылку Terms of Service.
     */
    data object OnTermsClicked : AuthorizationUiAction

    /**
     * Пользователь нажал на ссылку Privacy Policy.
     */
    data object OnPrivacyPolicyClicked : AuthorizationUiAction

    /**
     * Пользователь изменил состояние видимости шторки OTP.
     */
    data class OnSheetVisibilityChange(val isVisible: Boolean) : AuthorizationUiAction

    /**
     * Пользователь изменил вводимый OTP код.
     */
    data class OnPinChange(val pinCode: String) : AuthorizationUiAction

    /**
     * Пользователь нажал "Отправить код повторно".
     */
    data object OnResendOtpClicked : AuthorizationUiAction
}
