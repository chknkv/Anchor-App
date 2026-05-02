package com.chknkv.feature.assistant.data.mapper

import com.chknkv.feature.assistant.models.data.MotivationalQuoteBody

/**
 * Сетевой маппер для работы с API виджетами помощи.
 */
internal interface AssistanceApiMapper {

    /**
     * Возвращает мотивационную цитату для отображения в виджете.
     *
     * `GET /assistant/motivational-quote`
     */
    suspend fun getMotivationalQuote(): MotivationalQuoteBody
}
