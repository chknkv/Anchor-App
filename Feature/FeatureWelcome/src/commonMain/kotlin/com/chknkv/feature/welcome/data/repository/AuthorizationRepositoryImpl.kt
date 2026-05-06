package com.chknkv.feature.welcome.data.repository

import com.chknkv.corenetwork.api.NetworkException
import com.chknkv.corenetwork.token.TokenStorage
import com.chknkv.feature.welcome.data.mapper.AuthorizationApiMapper
import com.chknkv.feature.welcome.domain.OtpException

/**
 * Реализация [AuthorizationRepository].
 *
 * @param apiMapper Сетевой маппер OTP-авторизации.
 * @param tokenStorage Хранилище JWT-токенов для сохранения после успешной верификации.
 */
internal class AuthorizationRepositoryImpl(
    private val apiMapper: AuthorizationApiMapper,
    private val tokenStorage: TokenStorage,
) : AuthorizationRepository {

    override suspend fun sendOtp(email: String): String =
        apiMapper.sendOtp(email).sessionId

    override suspend fun verifyOtp(sessionId: String, otp: String): Boolean {
        val response = try {
            apiMapper.verifyOtp(sessionId, otp)
        } catch (e: NetworkException.BadRequest) {
            throw OtpException.InvalidOtp
        }
        tokenStorage.saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
        )
        return response.isFirstAuthorized
    }

    override suspend fun resendOtp(sessionId: String) {
        apiMapper.resendOtp(sessionId)
    }
}
