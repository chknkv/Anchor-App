package com.chknkv.feature.assistant.domain.interactor

import com.chknkv.feature.assistant.data.repository.AssistanceRepository
import com.chknkv.feature.assistant.models.domain.MotivationalQuote

/**
 * Реализация [AssistanceInteractor].
 *
 * На данном этапе не содержит дополнительной бизнес-логики.
 * При необходимости здесь размещается трансформация, кэширование или валидация.
 */
internal class AssistanceInteractorImpl(
    private val repository: AssistanceRepository,
) : AssistanceInteractor {

    override suspend fun getMotivationalQuote(): MotivationalQuote {
        return repository.getMotivationalQuote()
    }
}
