package com.chknkv.feature.addiction.models.domain

import com.chknkv.feature.addiction.models.domain.base.AddictionCategory
import com.chknkv.feature.addiction.models.domain.base.AddictionGradient
import com.chknkv.feature.addiction.models.domain.base.AddictionIcon

/**
 * Запрос на создание новой пользовательской привычки.
 *
 * Все поля передаются на бэкенд как есть — конвертация Compose-типов происходит
 * исключительно в presentation-слое.
 *
 * @param name Название привычки (не пустое, проверяется в ViewModel).
 * @param description Описание привычки (может быть пустым).
 * @param iconKey Иконка выбранной привычки.
 * @param gradientKey Градиент оформления привычки.
 * @param categoryKey Категория привычки.
 */
internal data class AddictionCreate(
    val name: String,
    val description: String,
    val iconKey: AddictionIcon,
    val gradientKey: AddictionGradient,
    val categoryKey: AddictionCategory,
)