package com.chknkv.feature.welcome.domain.interactor

import com.chknkv.coreutils.openUrl
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

    override fun handleTermsClicked() = openUrl(TERMS_OF_USE_LINK)

    override fun handlePrivacyPolicyClicked() = openUrl(PRIVACY_POLICY_LINK)

    companion object {
        private const val TERMS_OF_USE_LINK = "https://github.com/chknkv/Anchor-App/docs/TERMS_OF_USE"
        private const val PRIVACY_POLICY_LINK = "https://github.com/chknkv/Anchor-App/docs/PRIVACY_POLICY"
    }
}
