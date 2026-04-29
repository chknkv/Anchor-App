package com.chknkv.feature.addiction.models.domain.select

import com.chknkv.feature.addiction.models.domain.AddictionCategory

/**
 * Доменная модель отдельной привычки для экрана выбора.
 *
 * @param id Уникальный идентификатор привычки.
 * @param name Название привычки.
 */
internal data class Addiction(
    val id: Int,
    val name: String,
)

/**
 * Доменная модель группы привычек.
 *
 * Группа идентифицируется категорией, а не числовым ID.
 * Заголовок группы хранится в строковых ресурсах и разрешается в presentation-слое.
 *
 * @param category Категория, к которой относится группа.
 * @param addictions Список привычек в группе.
 */
internal data class AddictionGroup(
    val category: AddictionCategory,
    val addictions: List<Addiction>,
)
