package com.chknkv.feature.addiction.models.data

import com.chknkv.feature.addiction.models.data.base.AddictionCategoryKey
import com.chknkv.feature.addiction.models.data.base.AddictionGradientKey
import com.chknkv.feature.addiction.models.data.base.AddictionIconKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Тело запроса на создание новой привычки пользователя.
 *
 * @param name Название привычки.
 * @param description Описание привычки (может быть пустым).
 * @param iconKey Ключ иконки из [AddictionIconKey].
 * @param gradientKey Ключ градиента из [AddictionGradientKey].
 * @param category Строковый ключ категории (например, `"lifestyle"`).
 */
@Serializable
internal data class AddictionCreateRequest(
    @SerialName("name")         val name: String,
    @SerialName("description")  val description: String,
    @SerialName("icon_key")     val iconKey: AddictionIconKey,
    @SerialName("gradient_key") val gradientKey: AddictionGradientKey,
    @SerialName("category_key") val categoryKey: AddictionCategoryKey,
)
