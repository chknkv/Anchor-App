package com.chknkv.feature.addiction.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Тело запроса на создание новой привычки пользователя.
 *
 * @param name Название привычки.
 * @param description Описание привычки (может быть пустым).
 * @param iconKey Строковый ключ иконки.
 * @param gradientKey Строковый ключ градиента.
 * @param category Строковый ключ категории (например, `"lifestyle"`).
 */
@Serializable
internal data class AddictionCreateRequest(
    @SerialName("name")         val name: String,
    @SerialName("description")  val description: String,
    @SerialName("icon_key")     val iconKey: String,
    @SerialName("gradient_key") val gradientKey: String,
    @SerialName("category")     val category: String,
)
