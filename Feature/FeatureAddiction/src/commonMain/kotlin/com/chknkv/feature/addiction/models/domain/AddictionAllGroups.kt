package com.chknkv.feature.addiction.models.domain

import com.chknkv.feature.addiction.models.domain.base.AddictionCategory
import com.chknkv.feature.addiction.models.domain.base.AddictionGradient
import com.chknkv.feature.addiction.models.domain.base.AddictionIcon

/**
 * Доменная модель группы привычек пользователя по категории.
 *
 * @property category Категория группы.
 * @property addictions Список привычек пользователя в этой категории.
 * @property isCreateNewAvailable Разрешено ли создание новой привычки.
 */
internal data class AddictionAllGroups(
    val category: AddictionCategory,
    val addictions: List<AddictionAllGroup>,
    val isCreateNewAvailable: Boolean = false,
)

/**
 * Одна привычка пользователя в категории.
 *
 * @param id Уникальный идентификатор привычки.
 * @param name Название привычки.
 * @param icon Иконка привычки, используемая для отображения в ячейке списка.
 * @param gradient Градиент фона иконки привычки.
 * @param controlDays Количество дней под контролем.
 */
internal data class AddictionAllGroup(
    val id: Int,
    val name: String,
    val icon: AddictionIcon,
    val gradient: AddictionGradient,
    val controlDays: Int,
)
