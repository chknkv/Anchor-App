package com.chknkv.feature.addiction.data.repository

import com.chknkv.feature.addiction.data.mapper.AddictionApiMapper
import com.chknkv.feature.addiction.data.converter.toDomain
import com.chknkv.feature.addiction.domain.converter.toRequest
import com.chknkv.feature.addiction.models.data.AddictionSelectedRequest
import com.chknkv.feature.addiction.models.domain.AddictionCreate
import com.chknkv.feature.addiction.models.domain.AddictionsSelectionGroups
import com.chknkv.feature.addiction.models.domain.AddictionAllGroups
import com.chknkv.feature.addiction.models.domain.AddictionDetails
import com.chknkv.feature.addiction.models.domain.AddictionUpdate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Реализация [AddictionRepository].
 *
 * @param apiMapper Сетевой маппер для взаимодействия с API привычек.
 */
internal class AddictionRepositoryImpl(
    private val apiMapper: AddictionApiMapper,
) : AddictionRepository {

    private val _updates = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val updates: SharedFlow<Unit> = _updates.asSharedFlow()

    override suspend fun getAddictionGroupsForSelection(): List<AddictionsSelectionGroups> =
        apiMapper.getAddictionGroupsForSelection().toDomain()

    override suspend fun saveSelectedAddictions(ids: Set<Int>) {
        apiMapper.saveSelectedAddictions(AddictionSelectedRequest(ids.toList()))
        _updates.emit(Unit)
    }

    override suspend fun getAllClientAddictions(): List<AddictionAllGroups> =
        apiMapper.getAllClientAddictions().toDomain()

    override suspend fun createNewClientAddiction(request: AddictionCreate) {
        apiMapper.createNewClientAddiction(request.toRequest())
        _updates.emit(Unit)
    }

    override suspend fun getClientDetailsAddiction(id: Int): AddictionDetails =
        apiMapper.getClientDetailsAddiction(id).toDomain()

    override suspend fun incrementAddictionControlDays(id: Int) {
        apiMapper.incrementAddictionControlDays(id)
        _updates.emit(Unit)
    }

    override suspend fun updateClientAddiction(request: AddictionUpdate) {
        apiMapper.updateClientAddiction(id = request.id, request = request.toRequest())
        _updates.emit(Unit)
    }

    override suspend fun deleteClientAddiction(id: Int) {
        apiMapper.deleteClientAddiction(id)
        _updates.emit(Unit)
    }
}
