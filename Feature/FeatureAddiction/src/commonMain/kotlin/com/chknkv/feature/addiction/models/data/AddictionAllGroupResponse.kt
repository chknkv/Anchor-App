package com.chknkv.feature.addiction.models.data

import com.chknkv.corenetwork.NetworkEntity
import com.chknkv.feature.addiction.models.data.base.AddictionCategoryKey
import com.chknkv.feature.addiction.models.data.base.AddictionGradientKey
import com.chknkv.feature.addiction.models.data.base.AddictionIconKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Ответ для эндпоинта `GET /client/addictions/all`.
 */
@Serializable
internal class AddictionAllGroupsResponse : NetworkEntity<AddictionAllGroupsBody>()

/**
 * Body модель со списком групп привычек пользователя, сгруппированных по категории.
 *
 * @param isCreateNewAvailable Разрешено ли создание новой привычки; регулируется на бэкенде.
 * @param items Список групп привычек пользователя, сгруппированных по категории.
 */
@Serializable
internal data class AddictionAllGroupsBody(
    @SerialName("is_create_new_available")  val isCreateNewAvailable: Boolean = false,
    @SerialName("items")                    val items: List<AddictionsAllGroup>? = null,
)

/**
 * Одна группа привычек пользователя по категории.
 *
 * @param categoryKey Ключ категории из API; неизвестное значение вызывает ошибку десериализации.
 * @param addictions Список привычек пользователя в данной категории.
 */
@Serializable
internal data class AddictionsAllGroup(
    @SerialName("category_key") val categoryKey: AddictionCategoryKey,
    @SerialName("addictions")   val addictions: List<AddictionAllInGroup>,
)

/**
 * Одна привычка пользователя в категории.
 *
 * @param id Уникальный идентификатор привычки.
 * @param name Название привычки.
 * @param iconKey Ключ иконки из API; неизвестное значение вызывает ошибку десериализации.
 * @param gradientKey Ключ градиента из API; неизвестное значение вызывает ошибку десериализации.
 * @param controlDays Количество дней под контролем.
 */
@Serializable
internal data class AddictionAllInGroup(
    @SerialName("id")           val id: Int,
    @SerialName("name")         val name: String,
    @SerialName("icon_key")     val iconKey: AddictionIconKey,
    @SerialName("gradient")     val gradientKey: AddictionGradientKey,
    @SerialName("control_days") val controlDays: Int,
)
