package com.chknkv.feature.addiction.models.domain.create

import com.chknkv.feature.addiction.models.domain.AddictionCategory

/**
 * Запрос на создание новой пользовательской привычки.
 *
 * Все поля передаются на бэкенд как есть — конвертация Compose-типов происходит
 * исключительно в presentation-слое.
 *
 * @param name Название привычки (не пустое, проверяется в ViewModel).
 * @param description Описание привычки (может быть пустым).
 * @param iconKey Строковый ключ выбранной иконки (например, `"ic_habit_sport"`).
 * @param gradientKey Строковый ключ выбранного градиента (например, `"orange"`).
 * @param category Категория привычки.
 */
internal data class AddictionCreate(
    val name: String,
    val description: String,
    val iconKey: String,
    val gradientKey: String,
    val category: AddictionCategory,
)
