package com.chknkv.feature.addiction.domain.interactor

import com.chknkv.feature.addiction.models.domain.AddictionCreate
import com.chknkv.feature.addiction.models.domain.AddictionsSelectionGroups
import com.chknkv.feature.addiction.models.domain.AddictionAllGroups
import com.chknkv.feature.addiction.models.domain.AddictionDetails
import com.chknkv.feature.addiction.models.domain.AddictionUpdate
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
    suspend fun getAddictionGroupsForSelection(): List<AddictionsSelectionGroups>

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
    suspend fun getAllClientAddictions(): List<AddictionAllGroups>

    /**
     * Создаёт новую пользовательскую привычку.
     */
    suspend fun createNewClientAddiction(request: AddictionCreate)

    /**
     * Возвращает привычку пользователя по идентификатору.
     */
    suspend fun getClientDetailsAddiction(id: Int): AddictionDetails

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
