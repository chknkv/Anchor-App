package com.chknkv.feature.addiction.models.presentation.details

import androidx.compose.runtime.Immutable
import com.chknkv.feature.addiction.models.presentation.AddictionCategoryUi
import com.chknkv.feature.addiction.models.presentation.AddictionGradientUi
import com.chknkv.feature.addiction.models.presentation.AddictionIconUi
import com.chknkv.feature.addiction.models.presentation.ErrorMessageUiResult

/**
 * Состояние содержимого экрана деталей привычки.
 *
 * @property addictionId Уникальный идентификатор привычки.
 * @property mode Текущий режим (просмотр/редактирование).
 * @property title Название привычки (в режиме просмотра).
 * @property category Категория привычки — заголовок разрешается composable через `stringResource(category.toTitleStringResource())`.
 * @property icon Иконка привычки.
 * @property gradient Градиент привычки.
 * @property description Описание привычки.
 * @property controlDays Количество дней под контролем.
 * @property editTitle Временное название (в режиме редактирования).
 * @property editDescription Временное описание (в режиме редактирования).
 * @property editIcon Временная иконка (в режиме редактирования).
 * @property editGradient Временный градиент (в режиме редактирования).
 * @property editCategory Временная категория (в режиме редактирования); null — не выбрана.
 * @property availableIcons Список всех доступных иконок для выбора.
 * @property availableGradients Список всех доступных градиентов для выбора.
 * @property completedDates Множество дат выполнения привычки (ISO-строки).
 * @property canIncrementToday Можно ли отметить выполнение сегодня.
 * @property nextIncrementSeconds Секунд до следующей доступной отметки.
 * @property isLoading true — идёт сетевая операция (сохранение, удаление).
 * @property isError Модель с информацией об ошибке.
 */
@Immutable
internal data class AddictionDetailsUiResult(
    val addictionId: Int,
    val mode: DetailsMode = DetailsMode.ViewMode,
    val title: String = "",
    val category: AddictionCategoryUi = AddictionCategoryUi.OTHER,
    val icon: AddictionIconUi = AddictionIconUi.LIFESTYLE,
    val gradient: AddictionGradientUi = AddictionGradientUi.GRAY,
    val description: String = "",
    val controlDays: Int = 0,
    val editTitle: String = "",
    val editDescription: String = "",
    val editIcon: AddictionIconUi = AddictionIconUi.LIFESTYLE,
    val editGradient: AddictionGradientUi = AddictionGradientUi.GRAY,
    val editCategory: AddictionCategoryUi? = null,
    val availableIcons: List<AddictionIconUi> = emptyList(),
    val availableGradients: List<AddictionGradientUi> = emptyList(),
    val completedDates: Set<String> = emptySet(),
    val canIncrementToday: Boolean = true,
    val nextIncrementSeconds: Int = 0,
    val isDeleteConfirmationVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isError: ErrorMessageUiResult? = null,
)
