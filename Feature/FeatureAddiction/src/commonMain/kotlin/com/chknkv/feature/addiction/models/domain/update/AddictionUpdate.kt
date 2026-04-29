package com.chknkv.feature.addiction.models.domain.update

import com.chknkv.feature.addiction.models.domain.AddictionCategory

/**
 * Модель данных для обновления существующей привычки.
 *
 * @property id Уникальный идентификатор привычки.
 * @property name Новое название привычки.
 * @property description Новое описание привычки.
 * @property iconKey Новый строковый ключ иконки.
 * @property gradientKey Новый строковый ключ градиента.
 * @property category Новая категория привычки.
 */
internal data class AddictionUpdate(
    val id: Int,
    val name: String,
    val description: String,
    val iconKey: String,
    val gradientKey: String,
    val category: AddictionCategory,
)
