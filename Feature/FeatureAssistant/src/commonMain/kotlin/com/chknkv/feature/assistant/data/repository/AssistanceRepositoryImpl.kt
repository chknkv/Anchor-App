package com.chknkv.feature.assistant.data.repository

import com.chknkv.feature.assistant.data.converter.toDomain
import com.chknkv.feature.assistant.data.mapper.AssistanceApiMapper
import com.chknkv.feature.assistant.models.domain.MotivationalQuote

/**
 * Реализация [AssistanceRepository].
 *
 * @param apiMapper Сетевой маппер для взаимодействия с API виджета помощи.
 */
internal class AssistanceRepositoryImpl(
    private val apiMapper: AssistanceApiMapper,
) : AssistanceRepository {

    override suspend fun getMotivationalQuote(): MotivationalQuote =
        apiMapper.getMotivationalQuote().toDomain()
}
