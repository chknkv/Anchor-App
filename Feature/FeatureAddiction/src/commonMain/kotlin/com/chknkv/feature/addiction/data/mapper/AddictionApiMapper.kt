package com.chknkv.feature.addiction.data.mapper

import com.chknkv.feature.addiction.models.data.AddictionAllGroupsResponse
import com.chknkv.feature.addiction.models.data.AddictionCreateRequest
import com.chknkv.feature.addiction.models.data.AddictionDetailsResponse
import com.chknkv.feature.addiction.models.data.AddictionSelectedRequest
import com.chknkv.feature.addiction.models.data.AddictionSelectionGroupsResponse
import com.chknkv.feature.addiction.models.data.AddictionUpdateRequest

/**
 * Интерфейс сетевого маппера для работы с API привычек.
 */
internal interface AddictionApiMapper {

    /**
     * Возвращает ответ с группами привычек для экрана выбора при онбординге.
     *
     * `GET /addictions/default-selection-groups`
     */
    suspend fun getAddictionGroupsForSelection(): AddictionSelectionGroupsResponse

    /**
     * Сохраняет выбранные клиентом привычки при онбординге.
     *
     * `POST /addictions/save-selected`
     */
    suspend fun saveSelectedAddictions(request: AddictionSelectedRequest)

    /**
     * Возвращает ответ с привычками клиента, сгруппированными по категории.
     *
     * `GET /client/addictions/all`
     */
    suspend fun getAllClientAddictions(): AddictionAllGroupsResponse

    /**
     * Создаёт новую привычку клиента.
     *
     * `POST /client/addictions/create`
     */
    suspend fun createNewClientAddiction(request: AddictionCreateRequest)

    /**
     * Возвращает ответ с полными данными одной привычки клиента по идентификатору.
     *
     * `GET /client/addictions/details/{id}`
     */
    suspend fun getClientDetailsAddiction(id: Int): AddictionDetailsResponse

    /**
     * Увеличивает счётчик контрольных дней привычки на 1.
     *
     * `POST /client/addictions/increment/{id}`
     */
    suspend fun incrementAddictionControlDays(id: Int)

    /**
     * Обновляет параметры существующей привычки.
     *
     * `PATCH /client/addictions/update/{id}`
     */
    suspend fun updateClientAddiction(id: Int, request: AddictionUpdateRequest)

    /**
     * Удаляет привычку клиента по идентификатору.
     *
     * `DELETE /client/addictions/delete/{id}`
     */
    suspend fun deleteClientAddiction(id: Int)
}
