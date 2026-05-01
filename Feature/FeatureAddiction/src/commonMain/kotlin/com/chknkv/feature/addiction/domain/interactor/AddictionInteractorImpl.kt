package com.chknkv.feature.addiction.domain.interactor

import com.chknkv.feature.addiction.data.repository.AddictionRepository
import com.chknkv.feature.addiction.models.domain.AddictionCreate
import com.chknkv.feature.addiction.models.domain.AddictionsSelectionGroups
import com.chknkv.feature.addiction.models.domain.AddictionAllGroups
import com.chknkv.feature.addiction.models.domain.AddictionDetails
import com.chknkv.feature.addiction.models.domain.AddictionUpdate
import kotlinx.coroutines.flow.SharedFlow

/**
 * Реализация [AddictionInteractor].
 *
 * @param repository Источник данных о привычках.
 */
internal class AddictionInteractorImpl(
    private val repository: AddictionRepository,
) : AddictionInteractor {

    override val updates: SharedFlow<Unit> = repository.updates

    override suspend fun getAddictionGroupsForSelection(): List<AddictionsSelectionGroups> = repository.getAddictionGroupsForSelection()

    override suspend fun saveSelectedAddictions(ids: Set<Int>) = repository.saveSelectedAddictions(ids)

    override suspend fun getAllClientAddictions(): List<AddictionAllGroups> = repository.getAllClientAddictions()

    override suspend fun createNewClientAddiction(request: AddictionCreate) = repository.createNewClientAddiction(request)

    override suspend fun getClientDetailsAddiction(id: Int): AddictionDetails = repository.getClientDetailsAddiction(id)

    override suspend fun incrementAddictionControlDays(id: Int) = repository.incrementAddictionControlDays(id)

    override suspend fun updateClientAddiction(request: AddictionUpdate) = repository.updateClientAddiction(request)

    override suspend fun deleteClientAddiction(id: Int) = repository.deleteClientAddiction(id)
}
