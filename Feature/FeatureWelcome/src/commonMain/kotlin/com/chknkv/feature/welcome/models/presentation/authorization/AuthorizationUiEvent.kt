package com.chknkv.feature.welcome.models.presentation.authorization

/**
 * Разовые события (эффекты) экрана авторизации.
 */
sealed interface AuthorizationUiEvent {

    /** Событие успешного завершения авторизации. */
    data object OnAuthorized : AuthorizationUiEvent
}
