package com.chknkv.feature.assistant.domain.interactor

import com.chknkv.feature.assistant.models.domain.MotivationalQuote

/**
 * Бизнес-логика виджета помощи.
 *
 * Является единственной точкой входа для ViewModel в domain-слой.
 */
internal interface AssistanceInteractor {

    /**
     * Возвращает мотивационную цитату для отображения в виджете.
     */
    suspend fun getMotivationalQuote(): MotivationalQuote
}
