package com.chknkv.feature.welcome.data.repository

import com.chknkv.corenetwork.api.NetworkException
import com.chknkv.corenetwork.requireBody
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
        apiMapper.sendOtp(email).requireBody().sessionId

    override suspend fun verifyOtp(sessionId: String, otp: String): Boolean {
        val body = try {
            apiMapper.verifyOtp(sessionId, otp).requireBody()
        } catch (e: NetworkException.HttpError) {
            throw when (e.code) {
                400 -> OtpException.InvalidOtp
                410 -> OtpException.SessionExpired
                else -> e
            }
        }
        tokenStorage.saveTokens(
            accessToken = body.accessToken,
            refreshToken = body.refreshToken,
        )
        return body.isFirstAuthorized
    }

    override suspend fun resendOtp(sessionId: String) {
        try {
            apiMapper.resendOtp(sessionId)
        } catch (e: NetworkException.HttpError) {
            throw when (e.code) {
                410 -> OtpException.SessionExpired
                else -> e
            }
        }
    }
}
