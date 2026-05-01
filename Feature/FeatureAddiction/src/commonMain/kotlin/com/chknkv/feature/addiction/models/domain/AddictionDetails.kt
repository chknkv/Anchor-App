package com.chknkv.feature.addiction.models.domain

import com.chknkv.feature.addiction.models.domain.base.AddictionCategory
import com.chknkv.feature.addiction.models.domain.base.AddictionGradient
import com.chknkv.feature.addiction.models.domain.base.AddictionIcon

/**
 * Доменная модель привычки пользователя.
 *
 * @property id Уникальный идентификатор.
 * @property name Название привычки.
 * @property category Категория.
 * @property iconKey Иконка привычки.
 * @property gradient Градиент оформления карточки привычки.
 * @property controlDays Количество дней под контролем.
 * @property description Описание привычки; пустая строка, если не задано.
 * @property completedDates Множество дат выполнения (ISO).
 * @property canIncrementToday Доступно ли выполнение сегодня.
 * @property nextIncrementAvailableInSeconds Секунд до следующей отметки.
 */
internal data class AddictionDetails(
    val id: Int,
    val name: String,
    val category: AddictionCategory,
    val iconKey: AddictionIcon,
    val gradient: AddictionGradient,
    val controlDays: Int,
    val description: String = "",
    val completedDates: Set<String> = emptySet(),
    val canIncrementToday: Boolean = true,
    val nextIncrementAvailableInSeconds: Int = 0,
)