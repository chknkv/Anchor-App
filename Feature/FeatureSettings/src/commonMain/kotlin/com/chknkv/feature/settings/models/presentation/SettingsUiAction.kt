package com.chknkv.feature.settings.models.presentation

/**
 * Интенты для экрана настроек.
 */
sealed interface SettingsUiAction

/**
 * Навигационные интенты экрана настроек.
 */
sealed interface SettingsNavigationAction : SettingsUiAction {

    /** Открыть настройки оформления. */
    data object OpenThemeSettings : SettingsNavigationAction

    /** Открыть настройки языка. */
    data object OpenLanguageSettings : SettingsNavigationAction

    /** Открыть раздел конфиденциальности (смена passcode). */
    data object OpenPrivacySettings : SettingsNavigationAction

    /** Вернуться на предыдущий экран. */
    data object NavigateBack : SettingsNavigationAction

    /** Выйти из аккаунта. */
    data object Logout : SettingsNavigationAction
}

/**
 * Интенты управления диалогом подтверждения выхода.
 */
sealed interface SettingsLogoutAction : SettingsUiAction {

    /** Показать подтверждение выхода. */
    data object ShowLogoutConfirm : SettingsLogoutAction

    /** Скрыть подтверждение выхода. */
    data object HideLogoutConfirm : SettingsLogoutAction
}
