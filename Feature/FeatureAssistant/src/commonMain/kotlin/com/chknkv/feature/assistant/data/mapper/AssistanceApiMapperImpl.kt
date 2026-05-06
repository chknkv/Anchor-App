package com.chknkv.feature.assistant.data.mapper

import com.chknkv.corenetwork.api.ApiClient
import com.chknkv.feature.assistant.models.data.MotivationalQuoteResponse
import io.ktor.http.HttpMethod

/**
 * Реализация [AssistanceApiMapper].
 *
 * @param apiClient DSL-клиент из CoreNetwork.
 */
internal class AssistanceApiMapperImpl(
    private val apiClient: ApiClient,
) : AssistanceApiMapper {

    override suspend fun getMotivationalQuote(): MotivationalQuoteResponse =
        apiClient.execute<MotivationalQuoteResponse> {
            endpoint = GET_MOTIVATIONAL_QUOTE_ENDPOINT
            method = HttpMethod.Get
        }

    companion object {
        private const val GET_MOTIVATIONAL_QUOTE_ENDPOINT = "assistant/motivational-quote"
    }
}
