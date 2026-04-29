package com.chknkv.feature.main.navigation

import kotlinx.serialization.Serializable

/**
 * Маршруты навигации внутри основного флоу приложения.
 */
@Serializable
internal sealed interface MainNavRoute {

    /** Главный экран приложения. */
    @Serializable
    data object Main : MainNavRoute

    /** Экран создания новой привычки. */
    @Serializable
    data object AddictionCreate : MainNavRoute

    /** Экран просмотра информации о привычке. */
    @Serializable
    data class AddictionDetails(val addictionId: Int) : MainNavRoute
}
