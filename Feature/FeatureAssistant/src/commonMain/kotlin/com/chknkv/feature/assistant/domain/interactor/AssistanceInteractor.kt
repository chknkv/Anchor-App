package com.chknkv.feature.assistant.domain.interactor

import com.chknkv.corenetwork.api.NetworkException
import com.chknkv.feature.assistant.models.domain.MotivationalQuote

/**
 * Бизнес-логика виджета помощи.
 *
 * Является единственной точкой входа для ViewModel в domain-слой.
 */
internal interface AssistanceInteractor {

    /**
     * Возвращает мотивационную цитату для отображения в виджете.
     *
     * @return Доменная модель [MotivationalQuote] с текстом цитаты.
     * @throws NetworkException.Unauthorized При истёкшем / невалидном токене.
     * @throws NetworkException.NoConnection При отсутствии сети.
     * @throws NetworkException.HttpError При ошибке на стороне сервера.
     */
    suspend fun getMotivationalQuote(): MotivationalQuote
}
