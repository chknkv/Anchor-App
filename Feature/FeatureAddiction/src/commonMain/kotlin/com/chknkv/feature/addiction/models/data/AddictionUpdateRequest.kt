package com.chknkv.feature.addiction.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Тело запроса на обновление существующей привычки пользователя.
 *
 * @param name Новое название привычки.
 * @param description Новое описание привычки.
 * @param iconKey Новый строковый ключ иконки.
 * @param gradientKey Новый строковый ключ градиента.
 * @param category Новый строковый ключ категории.
 */
@Serializable
internal data class AddictionUpdateRequest(
    @SerialName("name")         val name: String,
    @SerialName("description")  val description: String,
    @SerialName("icon_key")     val iconKey: String,
    @SerialName("gradient_key") val gradientKey: String,
    @SerialName("category")     val category: String,
)
