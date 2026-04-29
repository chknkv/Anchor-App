package com.chknkv.feature.addiction.data.mapper

import com.chknkv.feature.addiction.models.data.AddictionAllGroupResponse
import com.chknkv.feature.addiction.models.data.AddictionCreateRequest
import com.chknkv.feature.addiction.models.data.AddictionDetailResponse
import com.chknkv.feature.addiction.models.data.AddictionSelectedRequest
import com.chknkv.feature.addiction.models.data.AddictionSelectionGroupResponse
import com.chknkv.feature.addiction.models.data.AddictionUpdateRequest

/**
 * Интерфейс сетевого маппера для работы с API привычек.
 *
 */
internal interface AddictionApiMapper {

    /**
     * Возвращает список групп привычек для экрана выбора при онбординге.
     *
     * `GET /`
     *
     * @return Список с группами привычек для экрана выбора при онбординге.
     */
    suspend fun getAddictionGroupsForSelection(): List<AddictionSelectionGroupResponse>

    /**
     * Сохраняет выбранные клиентом привычки при онбординге.
     *
     * `POST /`
     *
     * @param request Список ID выбранных привычек.
     */
    suspend fun saveSelectedAddictions(request: AddictionSelectedRequest)

    /**
     * Возвращает привычки клиента, сгруппированные по категории.
     *
     * `GET /`
     *
     * @return Список со всеми группами и привычками, подключенные у клиента.
     */
    suspend fun getAllClientAddictions(): List<AddictionAllGroupResponse>

    /**
     * Создаёт новую привычку клиента.
     *
     * `POST /`
     *
     * @param request Новая, созданная клиентом, привычка.
     */
    suspend fun createNewClientAddiction(request: AddictionCreateRequest)

    /**
     * Возвращает полные данные одной привычки клиента по идентификатору.
     *
     * `GET /`
     *
     * @param id Идентификатор привычки.
     * @return Детальная информация о привычке клиента.
     */
    suspend fun getClientDetailsAddiction(id: Int): AddictionDetailResponse

    /**
     * Увеличивает счётчик контрольных дней привычки на 1.
     *
     * `POST /`
     *
     * @param id Идентификатор привычки.
     */
    suspend fun incrementAddictionControlDays(id: Int)

    /**
     * Обновляет параметры существующей привычки.
     *
     * `PATCH /`
     *
     * @param id Идентификатор привычки.
     * @param request Тело запроса с обновлёнными данными.
     */
    suspend fun updateClientAddiction(id: Int, request: AddictionUpdateRequest)

    /**
     * Удаляет привычку клиента по идентификатору.
     *
     * `DELETE /`
     *
     * @param id Идентификатор привычки.
     */
    suspend fun deleteClientAddiction(id: Int)
}
