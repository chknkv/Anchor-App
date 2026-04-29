package com.chknkv.feature.assistant.data.repository

import com.chknkv.feature.assistant.models.domain.MotivationalQuote

/**
 * Источник данных для виджета помощи.
 *
 * Реализации могут читать данные из сети, локальной БД или хардкоженных заглушек.
 */
internal interface AssistanceRepository {

    /**
     * Возвращает мотивационную цитату для отображения в виджете.
     */
    suspend fun getMotivationalQuote(): MotivationalQuote
}
