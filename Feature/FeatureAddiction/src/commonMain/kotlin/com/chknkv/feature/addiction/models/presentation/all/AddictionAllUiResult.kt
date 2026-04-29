package com.chknkv.feature.addiction.models.presentation.all

import androidx.compose.ui.graphics.Brush
import org.jetbrains.compose.resources.DrawableResource

/**
 * Результат успешной загрузки экрана всех привычек.
 *
 * @param groups Список групп привычек, сгруппированных по категории.
 */
internal data class AddictionAllUiResult(
    val groups: List<UserAddictionGroupUi>,
)

/**
 * Группа привычек пользователя по категории для отображения на экране всех привычек.
 *
 * @param category Категория группы.
 * @param addictions Список привычек в этой категории.
 */
internal data class UserAddictionGroupUi(
    val category: AddictionCategoryUi,
    val addictions: List<UserAddictionUi>,
)

/**
 * UI-модель привычки пользователя для отображения в списке.
 *
 * @param id Уникальный идентификатор привычки.
 * @param name Название привычки.
 * @param category Категория — используется для фильтрации и отображения заголовка.
 * @param iconRes Ресурс иконки привычки.
 * @param iconGradient Градиентный фон иконки привычки.
 * @param controlDays Количество дней под контролем.
 */
internal data class UserAddictionUi(
    val id: Int,
    val name: String,
    val category: AddictionCategoryUi,
    val iconRes: DrawableResource,
    val iconGradient: Brush,
    val controlDays: Int,
)
