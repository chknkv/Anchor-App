package com.chknkv.feature.settings.navigation

import kotlinx.serialization.Serializable

/**
 * Маршруты навигации внутри фичи настроек.
 */
@Serializable
internal sealed interface SettingsNavRoute {

    /** Главный экран настроек. */
    @Serializable
    data object Main : SettingsNavRoute

    /** Экран выбора темы оформления. */
    @Serializable
    data object Appearance : SettingsNavRoute

    /** Экран выбора языка. */
    @Serializable
    data object Language : SettingsNavRoute

    /** Флоу управления паролем. */
    @Serializable
    data object PasscodeFlow : SettingsNavRoute
}
