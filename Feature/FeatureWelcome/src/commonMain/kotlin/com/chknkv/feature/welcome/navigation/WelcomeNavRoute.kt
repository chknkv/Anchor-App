package com.chknkv.feature.welcome.navigation

import kotlinx.serialization.Serializable

/**
 * Маршруты навигации внутри флоу приветствия (онбординга).
 */
@Serializable
internal sealed interface WelcomeNavRoute {

    /** Экран авторизации (ввод Email и OTP). */
    @Serializable
    data object Authorization : WelcomeNavRoute

    /**
     * Экран управления паролем (создание или ввод).
     * @param isCreation true — если требуется создание нового пароля, false — ввод существующего.
     * @param isFirstAuthorized true — первый вход (после passcode → HabitSelection),
     *   false — возвращающийся пользователь (после passcode → onFinished).
     */
    @Serializable
    data class Passcode(val isCreation: Boolean, val isFirstAuthorized: Boolean = true) : WelcomeNavRoute

    /** Экран выбора первичных привычек. */
    @Serializable
    data object HabitSelection : WelcomeNavRoute
}
