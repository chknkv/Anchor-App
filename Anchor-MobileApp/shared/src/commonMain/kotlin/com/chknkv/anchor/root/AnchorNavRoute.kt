package com.chknkv.anchor.root

import kotlinx.serialization.Serializable

/**
 * Глобальные маршруты навигации приложения.
 */
@Serializable
sealed interface AnchorNavRoute {

    /** Маршрут к флоу приветствия (авторизация, онбординг). */
    @Serializable
    data object Welcome : AnchorNavRoute

    /** Маршрут к основному рабочему флоу приложения. */
    @Serializable
    data object Main : AnchorNavRoute
}
