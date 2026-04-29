package com.chknkv.feature.addiction.data.repository

import com.chknkv.feature.addiction.data.mapper.AddictionApiMapper
import com.chknkv.feature.addiction.domain.converter.toAddictionSelectedRequest
import com.chknkv.feature.addiction.domain.converter.toDomain
import com.chknkv.feature.addiction.domain.converter.toRequest
import com.chknkv.feature.addiction.models.domain.create.AddictionCreate
import com.chknkv.feature.addiction.models.domain.select.AddictionGroup
import com.chknkv.feature.addiction.models.domain.UserAddiction
import com.chknkv.feature.addiction.models.domain.UserAddictionGroup
import com.chknkv.feature.addiction.models.domain.update.AddictionUpdate
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

    override suspend fun getAddictionGroupsForSelection(): List<AddictionGroup> =
        apiMapper.getAddictionGroupsForSelection().map { it.toDomain() }

    override suspend fun saveSelectedAddictions(ids: Set<Int>) {
        apiMapper.saveSelectedAddictions(ids.toAddictionSelectedRequest())
        _updates.emit(Unit)
    }

    override suspend fun getAllClientAddictions(): List<UserAddictionGroup> =
        apiMapper.getAllClientAddictions().map { it.toDomain() }

    override suspend fun createNewClientAddiction(request: AddictionCreate) {
        apiMapper.createNewClientAddiction(request.toRequest())
        _updates.emit(Unit)
    }

    override suspend fun getClientDetailsAddiction(id: Int): UserAddiction =
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
