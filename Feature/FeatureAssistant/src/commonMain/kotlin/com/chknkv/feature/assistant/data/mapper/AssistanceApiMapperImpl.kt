package com.chknkv.feature.assistant.data.mapper

import com.chknkv.corenetwork.api.ApiClient
import com.chknkv.corenetwork.requireBody
import com.chknkv.feature.assistant.models.data.MotivationalQuoteBody
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

    override suspend fun getMotivationalQuote(): MotivationalQuoteBody =
        apiClient.request<MotivationalQuoteResponse> {
            endpoint = GET_MOTIVATIONAL_QUOTE_ENDPOINT
            method = HttpMethod.Get
        }.requireBody()

    companion object {
        private const val GET_MOTIVATIONAL_QUOTE_ENDPOINT = "assistant/motivational-quote"
    }
}
