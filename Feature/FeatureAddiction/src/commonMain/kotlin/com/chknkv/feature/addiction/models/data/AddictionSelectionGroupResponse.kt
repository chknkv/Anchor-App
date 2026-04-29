package com.chknkv.feature.addiction.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Ответ сервера — группа привычек для экрана выбора при онбординге.
 *
 * Используется для эндпоинта `GET /addiction-groups`.
 *
 * @param categoryKey Строковый ключ категории (например, `"lifestyle"`).
 * @param addictions Список привычек в группе.
 */
@Serializable
internal data class AddictionSelectionGroupResponse(
    @SerialName("category_key") val categoryKey: String,
    @SerialName("addictions")   val addictions: List<AddictionSelectionItemResponse>,
)

/**
 * Элемент группы привычек для экрана выбора.
 *
 * @param id Уникальный идентификатор привычки (используется при сохранении выбора).
 * @param name Название привычки.
 * @param iconKey Строковый ключ иконки.
 * @param category Строковый ключ категории.
 */
@Serializable
internal data class AddictionSelectionItemResponse(
    @SerialName("id")       val id: Int,
    @SerialName("name")     val name: String,
    @SerialName("icon_key") val iconKey: String,
    @SerialName("category") val category: String,
)
