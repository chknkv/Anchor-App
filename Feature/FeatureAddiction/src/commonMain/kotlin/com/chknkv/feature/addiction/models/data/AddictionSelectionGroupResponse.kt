package com.chknkv.feature.addiction.models.data

import com.chknkv.corenetwork.NetworkEntity
import com.chknkv.feature.addiction.models.data.base.AddictionCategoryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Ответ для эндпоинта `GET /addictions/default-groups`.
 */
@Serializable
internal class AddictionSelectionGroupsResponse : NetworkEntity<AddictionSelectionGroupsBody>()

/**
 * Body модель с группами привычек для экрана выбора при онбординге.
 *
 * @param items Список групп привычек для экрана выбора при онбординге.
 */
@Serializable
internal data class AddictionSelectionGroupsBody(
    @SerialName("items") val items: List<AddictionSelectionGroups>,
)

/**
 * Одна группа привычек для экрана выбора при онбординге.
 *
 * @param categoryKey Ключ категории из API; неизвестное значение вызывает ошибку десериализации.
 * @param addictions Список привычек в группе.
 */
@Serializable
internal data class AddictionSelectionGroups(
    @SerialName("category_key") val categoryKey: AddictionCategoryKey,
    @SerialName("addictions")   val addictions: List<AddictionSelectionItem>,
)

/**
 * Один элемент привычки для экрана выбора при онбординге.
 *
 * @param id Уникальный идентификатор привычки (используется при сохранении выбора).
 * @param name Название привычки.
 */
@Serializable
internal data class AddictionSelectionItem(
    @SerialName("id")           val id: Int,
    @SerialName("name")         val name: String,
)

/**
 * Тело запроса на сохранение выбранных привычек при онбординге.
 *
 * Используется для эндпоинта `POST /user/addictions/select`.
 *
 * @param ids Список идентификаторов выбранных привычек.
 */
@Serializable
internal data class AddictionSelectedRequest(
    @SerialName("ids") val ids: List<Int>,
)
