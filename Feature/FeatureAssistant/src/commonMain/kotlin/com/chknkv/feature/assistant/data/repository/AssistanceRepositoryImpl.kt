package com.chknkv.feature.assistant.data.repository

import com.chknkv.feature.assistant.models.domain.MotivationalQuote
import kotlinx.coroutines.delay

/**
 * Реализация [AssistanceRepository].
 */
internal class AssistanceRepositoryImpl : AssistanceRepository {

    override suspend fun getMotivationalQuote(): MotivationalQuote {
        delay(3500)
        return MotivationalQuote(
            text = "Ты справишься — один шаг за раз.",
            detailText = "Каждый момент — это возможность сделать правильный выбор. " +
                "Ты сильнее, чем думаешь.",
        )
    }
}
