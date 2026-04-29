package com.chknkv.corepasscode.navigation

import kotlinx.serialization.Serializable

/**
 * Внутренние маршруты навигации флоу passcode.
 * Используются только внутри [PasscodeFlow] — не являются частью публичного API модуля.
 */
@Serializable
internal sealed interface PasscodeNavRoute {

    /** Экран создания passcode (первичный или при смене). */
    @Serializable
    data class Create(val isChange: Boolean) : PasscodeNavRoute

    /** Экран ввода существующего passcode. */
    @Serializable
    data object Enter : PasscodeNavRoute

    /** Экран предложения подключить биометрию. */
    @Serializable
    data object Biometry : PasscodeNavRoute
}