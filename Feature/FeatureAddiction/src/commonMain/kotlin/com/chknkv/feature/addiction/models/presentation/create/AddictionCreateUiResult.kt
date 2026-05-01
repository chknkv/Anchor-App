package com.chknkv.feature.addiction.models.presentation.create

import androidx.compose.runtime.Immutable
import com.chknkv.feature.addiction.models.presentation.AddictionCategoryUi
import com.chknkv.feature.addiction.models.presentation.AddictionGradientUi
import com.chknkv.feature.addiction.models.presentation.AddictionIconUi
import com.chknkv.feature.addiction.models.presentation.ErrorMessageUiResult

/**
 * Состояние содержимого формы создания привычки.
 **
 * @property title Текущее значение поля «Название».
 * @property description Текущее значение поля «Описание».
 * @property selectedIcon Выбранная иконка.
 * @property selectedGradient Выбранный градиент.
 * @property selectedCategory Выбранная категория; null — категория не выбрана.
 * @property availableIcons Список доступных иконок для пикера.
 * @property availableGradients Список доступных градиентов для пикера.
 * @property availableCategories Список категорий, доступных для выбора.
 * @property isLoading true — идёт отправка запроса на создание.
 * @property isError Модель с информацией об ошибке.
 */
@Immutable
internal data class AddictionCreateUiResult(
    val title: String = "",
    val description: String = "",
    val selectedIcon: AddictionIconUi,
    val selectedGradient: AddictionGradientUi,
    val selectedCategory: AddictionCategoryUi? = null,
    val availableIcons: List<AddictionIconUi>,
    val availableGradients: List<AddictionGradientUi>,
    val availableCategories: List<AddictionCategoryUi> = AddictionCategoryUi.entries,
    val isLoading: Boolean = false,
    val isError: ErrorMessageUiResult? = null,
)
