package com.chknkv.feature.welcome.data.mapper

import com.chknkv.corenetwork.NetworkEntity
import com.chknkv.corenetwork.api.ApiClient
import com.chknkv.corenetwork.isSuccessfulExecute
import com.chknkv.feature.welcome.models.data.OtpResendRequest
import com.chknkv.feature.welcome.models.data.OtpSendRequest
import com.chknkv.feature.welcome.models.data.OtpSendResponse
import com.chknkv.feature.welcome.models.data.OtpVerifyRequest
import com.chknkv.feature.welcome.models.data.OtpVerifyResponse
import io.ktor.http.HttpMethod

/**
 * Реализация [AuthorizationApiMapper].
 *
 * @param apiClient DSL-клиент из CoreNetwork.
 */
internal class AuthorizationApiMapperImpl(
    private val apiClient: ApiClient,
) : AuthorizationApiMapper {

    override suspend fun sendOtp(email: String): OtpSendResponse =
        apiClient.request<OtpSendResponse> {
            endpoint = ENDPOINT_OTP_SEND
            method = HttpMethod.Post
            body = OtpSendRequest(email = email)
        }

    override suspend fun verifyOtp(sessionId: String, otp: String): OtpVerifyResponse =
        apiClient.request<OtpVerifyResponse> {
            endpoint = ENDPOINT_OTP_VERIFY
            method = HttpMethod.Post
            body = OtpVerifyRequest(sessionId = sessionId, otp = otp)
        }

    override suspend fun resendOtp(sessionId: String) {
        apiClient.request<NetworkEntity<Unit>> {
            endpoint = ENDPOINT_OTP_RESEND
            method = HttpMethod.Post
            body = OtpResendRequest(sessionId = sessionId)
        }.isSuccessfulExecute()
    }

    private companion object {
        const val ENDPOINT_OTP_SEND = "auth/otp/send"
        const val ENDPOINT_OTP_VERIFY = "auth/otp/verify"
        const val ENDPOINT_OTP_RESEND = "auth/otp/resend"
    }
}
