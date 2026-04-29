package com.chknkv.feature.addiction.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Ответ сервера с полными данными привычки пользователя.
 *
 * Используется как элемент [AddictionAllGroupResponse.addictions]
 * и как ответ `GET /user/addictions/{id}`.
 *
 * @param id Уникальный идентификатор привычки.
 * @param name Название привычки.
 * @param category Строковый ключ категории (например, `"lifestyle"`).
 * @param iconKey Строковый ключ иконки (например, `"ic_habit_sport"`).
 * @param gradient Строковый ключ градиента (например, `"orange"`).
 * @param controlDays Количество дней под контролем.
 * @param description Описание привычки.
 * @param completedDates Список дат выполнения в формате ISO (например, `"2026-04-29"`).
 * @param canIncrementToday Доступна ли отметка выполнения сегодня.
 * @param nextIncrementAvailableInSeconds Секунд до следующей доступной отметки.
 */
@Serializable
internal data class AddictionDetailResponse(
    @SerialName("id")                                  val id: Int,
    @SerialName("name")                                val name: String,
    @SerialName("category")                            val category: String,
    @SerialName("icon_key")                            val iconKey: String,
    @SerialName("gradient")                            val gradient: String,
    @SerialName("control_days")                        val controlDays: Int,
    @SerialName("description")                         val description: String = "",
    @SerialName("completed_dates")                     val completedDates: List<String> = emptyList(),
    @SerialName("can_increment_today")                 val canIncrementToday: Boolean = true,
    @SerialName("next_increment_available_in_seconds") val nextIncrementAvailableInSeconds: Int = 0,
)
