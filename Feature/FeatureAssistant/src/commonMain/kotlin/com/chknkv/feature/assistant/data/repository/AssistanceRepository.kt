package com.chknkv.feature.assistant.data.repository

import com.chknkv.corenetwork.api.NetworkException
import com.chknkv.feature.assistant.models.domain.MotivationalQuote

/**
 * Источник данных для виджета помощи.
 *
 * Реализации могут читать данные из сети, локальной БД или хардкоженных заглушек.
 */
internal interface AssistanceRepository {

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
