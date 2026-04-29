package com.chknkv.feature.addiction.domain.interactor

import com.chknkv.feature.addiction.models.domain.create.AddictionCreate
import com.chknkv.feature.addiction.models.domain.select.AddictionGroup
import com.chknkv.feature.addiction.models.domain.UserAddiction
import com.chknkv.feature.addiction.models.domain.UserAddictionGroup
import com.chknkv.feature.addiction.models.domain.update.AddictionUpdate
import kotlinx.coroutines.flow.SharedFlow

/**
 * Контракт доменного слоя для работы с привычками.
 */
internal interface AddictionInteractor {

    /** Поток событий обновления данных (создание, удаление, изменение). */
    val updates: SharedFlow<Unit>

    /**
     * Возвращает список групп привычек для выбора на экране "Выбор привычек".
     */
    suspend fun getAddictionGroupsForSelection(): List<AddictionGroup>

    /**
     * Сохраняет идентификаторы выбранных пользователем привычек на экране "Выбор привычек".
     *
     * @param ids Множество идентификаторов выбранных привычек.
     */
    suspend fun saveSelectedAddictions(ids: Set<Int>)

    /**
     * Возвращает список привычек клиента, сгруппированных по категории.
     * Используется для отображения в функции "Все привычки" на Главном Экране.
     */
    suspend fun getAllClientAddictions(): List<UserAddictionGroup>

    /**
     * Создаёт новую пользовательскую привычку.
     */
    suspend fun createNewClientAddiction(request: AddictionCreate)

    /**
     * Возвращает привычку пользователя по идентификатору.
     */
    suspend fun getClientDetailsAddiction(id: Int): UserAddiction

    /**
     * Увеличивает счётчик контрольных дней на 1. Ограничение 1 раз/24 ч — на бэке.
     */
    suspend fun incrementAddictionControlDays(id: Int)

    /**
     * Обновляет параметры существующей привычки.
     */
    suspend fun updateClientAddiction(request: AddictionUpdate)

    /**
     * Удаляет привычку пользователя по идентификатору.
     *
     * @param id Идентификатор привычки.
     */
    suspend fun deleteClientAddiction(id: Int)
}
