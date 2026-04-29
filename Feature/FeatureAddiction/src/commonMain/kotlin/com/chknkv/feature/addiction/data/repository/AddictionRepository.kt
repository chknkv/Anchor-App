package com.chknkv.feature.addiction.data.repository

import com.chknkv.feature.addiction.models.domain.create.AddictionCreate
import com.chknkv.feature.addiction.models.domain.select.AddictionGroup
import com.chknkv.feature.addiction.models.domain.UserAddiction
import com.chknkv.feature.addiction.models.domain.UserAddictionGroup
import com.chknkv.feature.addiction.models.domain.update.AddictionUpdate
import kotlinx.coroutines.flow.SharedFlow

/**
 * Интерфейс репозитория для работы со справочником привычек.
 *
 * Предоставляет доступ к группам привычек, привычкам пользователя
 * и позволяет сохранять выбор пользователя.
 */
internal interface AddictionRepository {

    /**
     * Поток событий обновления данных.
     * Эмитит Unit при любом изменении (создание, удаление, обновление).
     */
    val updates: SharedFlow<Unit>

    /**
     * Возвращает список всех доступных групп привычек.
     */
    suspend fun getAddictionGroupsForSelection(): List<AddictionGroup>

    /**
     * Сохраняет выбранные пользователем привычки в локальном или удаленном хранилище.
     *
     * @param ids Множество идентификаторов выбранных привычек (ID).
     */
    suspend fun saveSelectedAddictions(ids: Set<Int>)

    /**
     * Возвращает список привычек текущего пользователя, сгруппированных по категории.
     */
    suspend fun getAllClientAddictions(): List<UserAddictionGroup>

    /**
     * Создаёт новую пользовательскую привычку.
     *
     * @param request Данные для создания привычки.
     */
    suspend fun createNewClientAddiction(request: AddictionCreate)

    /**
     * Возвращает одну привычку пользователя по идентификатору.
     *
     * @param id Идентификатор привычки.
     * @throws NoSuchElementException если привычка с таким id не найдена.
     */
    suspend fun getClientDetailsAddiction(id: Int): UserAddiction

    /**
     * Увеличивает счётчик контрольных дней на 1.
     * Ограничение «не более 1 раза в 24 часа» контролируется бэкендом.
     *
     * @param id Идентификатор привычки.
     */
    suspend fun incrementAddictionControlDays(id: Int)

    /**
     * Обновляет параметры существующей привычки.
     *
     * @param request Данные для обновления.
     */
    suspend fun updateClientAddiction(request: AddictionUpdate)

    /**
     * Удаляет привычку пользователя по идентификатору.
     *
     * @param id Идентификатор привычки.
     */
    suspend fun deleteClientAddiction(id: Int)
}
