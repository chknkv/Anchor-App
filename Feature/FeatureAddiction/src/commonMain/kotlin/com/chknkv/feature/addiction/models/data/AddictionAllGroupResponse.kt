package com.chknkv.feature.addiction.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Ответ сервера — группа привычек пользователя по категории для экрана всех привычек.
 *
 * Используется для эндпоинта `GET /user/addictions/grouped`.
 *
 * @param categoryKey Строковый ключ категории (например, `"lifestyle"`).
 * @param addictions Список привычек пользователя в данной категории.
 */
@Serializable
internal data class AddictionAllGroupResponse(
    @SerialName("category_key") val categoryKey: String,
    @SerialName("addictions")   val addictions: List<AddictionDetailResponse>,
)
