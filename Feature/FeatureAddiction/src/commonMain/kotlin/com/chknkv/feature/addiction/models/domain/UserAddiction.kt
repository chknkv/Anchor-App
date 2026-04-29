package com.chknkv.feature.addiction.models.domain

/**
 * Доменная модель привычки пользователя.
 *
 * @property id Уникальный идентификатор.
 * @property name Название привычки.
 * @property category Категория.
 * @property iconKey Ключ иконки.
 * @property gradient Ключ градиента.
 * @property controlDays Количество дней под контролем.
 * @property description Описание привычки.
 * @property completedDates Множество дат выполнения (ISO).
 * @property canIncrementToday Доступно ли выполнение сегодня.
 * @property nextIncrementAvailableInSeconds Секунд до следующей отметки.
 */
internal data class UserAddiction(
    val id: Int,
    val name: String,
    val category: AddictionCategory,
    val iconKey: String,
    val gradient: String,
    val controlDays: Int,
    val description: String = "",
    val completedDates: Set<String> = emptySet(),
    val canIncrementToday: Boolean = true,
    val nextIncrementAvailableInSeconds: Int = 0,
)