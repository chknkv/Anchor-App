package com.chknkv.feature.addiction.models.domain

import com.chknkv.feature.addiction.models.domain.base.AddictionCategory

/**
 * Доменная модель группы привычек.
 *
 * @param category Категория, к которой относится группа.
 * @param addictions Список привычек в группе.
 */
internal data class AddictionsSelectionGroups(
    val category: AddictionCategory,
    val addictions: List<AddictionSelectionInGroup>,
)

/**
 * Доменная модель отдельной привычки для экрана выбора.
 *
 * @param id Уникальный идентификатор привычки.
 * @param name Название привычки.
 */
internal data class AddictionSelectionInGroup(
    val id: Int,
    val name: String,
)