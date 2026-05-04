package com.chknkv.feature.addiction.models.data

import com.chknkv.feature.addiction.models.data.base.AddictionCategoryKey
import com.chknkv.feature.addiction.models.data.base.AddictionGradientKey
import com.chknkv.feature.addiction.models.data.base.AddictionIconKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Тело запроса на обновление существующей привычки пользователя.
 *
 * @param name Новое название привычки.
 * @param description Новое описание привычки.
 * @param iconKey Новый ключ иконки из [AddictionIconKey].
 * @param gradientKey Новый ключ градиента из [AddictionGradientKey].
 * @param category Новый строковый ключ категории.
 */
@Serializable
internal data class AddictionUpdateRequest(
    @SerialName("name")         val name: String,
    @SerialName("description")  val description: String,
    @SerialName("icon_key")     val iconKey: AddictionIconKey,
    @SerialName("gradient_key") val gradientKey: AddictionGradientKey,
    @SerialName("category_key") val categoryKey: AddictionCategoryKey,
)
