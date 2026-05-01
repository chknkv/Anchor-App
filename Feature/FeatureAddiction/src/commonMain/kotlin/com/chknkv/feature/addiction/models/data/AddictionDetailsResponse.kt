package com.chknkv.feature.addiction.models.data

import com.chknkv.corenetwork.NetworkEntity
import com.chknkv.feature.addiction.models.data.base.AddictionCategoryKey
import com.chknkv.feature.addiction.models.data.base.AddictionGradientKey
import com.chknkv.feature.addiction.models.data.base.AddictionIconKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Ответ для эндпоинта `GET /client/addictions/details/{id}`.
 */
@Serializable
internal class AddictionDetailsResponse : NetworkEntity<AddictionDetailsBody>()

/**
 * Body модель с полными данными привычки пользователя.
 *
 * @param id Уникальный идентификатор привычки.
 * @param name Название привычки.
 * @param categoryKey Строковый ключ категории (например, `"lifestyle"`).
 * @param iconKey Ключ иконки из API; неизвестное значение вызывает ошибку десериализации.
 * @param gradientKey Ключ градиента из API; неизвестное значение вызывает ошибку десериализации.
 * @param controlDays Количество дней под контролем.
 * @param description Описание привычки.
 * @param completedDates Список дат выполнения в формате ISO (например, `"2026-04-29"`).
 * @param canIncrementToday Доступна ли отметка выполнения сегодня.
 * @param nextIncrementAvailableInSeconds Секунд до следующей доступной отметки.
 */
@Serializable
internal data class AddictionDetailsBody(
    @SerialName("id")                                   val id: Int,
    @SerialName("name")                                 val name: String,
    @SerialName("category_key")                         val categoryKey: AddictionCategoryKey,
    @SerialName("icon_key")                             val iconKey: AddictionIconKey,
    @SerialName("gradient_key")                         val gradientKey: AddictionGradientKey,
    @SerialName("control_days")                         val controlDays: Int,
    @SerialName("description")                          val description: String? = null,
    @SerialName("completed_dates")                      val completedDates: List<String> = emptyList(),
    @SerialName("can_increment_today")                  val canIncrementToday: Boolean,
    @SerialName("next_increment_available_in_seconds")  val nextIncrementAvailableInSeconds: Long,
)
