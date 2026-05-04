package com.chknkv.feature.welcome.domain.interactor

import com.chknkv.feature.welcome.data.repository.AuthorizationRepository

/**
 * Реализация [AuthorizationInteractor].
 *
 * @param repository Репозиторий OTP-авторизации.
 */
internal class AuthorizationInteractorImpl(
    private val repository: AuthorizationRepository,
) : AuthorizationInteractor {

    override suspend fun sendOtp(email: String): String = repository.sendOtp(email)

    override suspend fun verifyOtp(sessionId: String, otp: String): Boolean =
        repository.verifyOtp(sessionId, otp)

    override suspend fun resendOtp(sessionId: String) = repository.resendOtp(sessionId)

    override fun handleTermsClicked() {
        // TODO: Открыть экран или URL с условиями использования.
    }
}
