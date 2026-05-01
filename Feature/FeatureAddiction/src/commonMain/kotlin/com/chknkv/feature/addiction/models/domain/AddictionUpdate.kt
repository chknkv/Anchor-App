package com.chknkv.feature.addiction.models.domain

import com.chknkv.feature.addiction.models.domain.base.AddictionCategory
import com.chknkv.feature.addiction.models.domain.base.AddictionGradient
import com.chknkv.feature.addiction.models.domain.base.AddictionIcon

/**
 * Модель данных для обновления существующей привычки.
 *
 * @property id Уникальный идентификатор привычки.
 * @property name Новое название привычки.
 * @property description Новое описание привычки.
 * @property iconKey Новая иконка привычки.
 * @property gradientKey Новый градиент оформления привычки.
 * @property categoryKey Новая категория привычки.
 */
internal data class AddictionUpdate(
    val id: Int,
    val name: String,
    val description: String,
    val iconKey: AddictionIcon,
    val gradientKey: AddictionGradient,
    val categoryKey: AddictionCategory,
)