package com.chknkv.feature.welcome.models.presentation.authorization

/**
 * Разовые события (эффекты) экрана авторизации.
 */
sealed interface AuthorizationUiEvent {

    /** Авторизация завершена, пользователь входит впервые. */
    data object OnAuthorizedNewUser : AuthorizationUiEvent

    /** Авторизация завершена, пользователь возвращается. */
    data object OnAuthorizedReturningUser : AuthorizationUiEvent
}
