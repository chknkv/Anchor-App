package com.chknkv.feature.addiction.models.presentation.details

import androidx.compose.runtime.Immutable
import com.chknkv.feature.addiction.models.presentation.all.AddictionCategoryUi

/**
 * Состояние содержимого экрана деталей привычки.
 *
 * @property addictionId Уникальный идентификатор привычки.
 * @property mode Текущий режим (просмотр/редактирование).
 * @property title Название привычки (в режиме просмотра).
 * @property category Категория привычки — заголовок разрешается composable через `stringResource(category.toTitleStringResource())`.
 * @property iconKey Ключ текущей иконки.
 * @property gradientKey Ключ текущего градиента.
 * @property description Описание привычки.
 * @property controlDays Количество дней под контролем.
 * @property editTitle Временное название (в режиме редактирования).
 * @property editDescription Временное описание (в режиме редактирования).
 * @property editIconKey Временный ключ иконки (в режиме редактирования).
 * @property editGradientKey Временный ключ градиента (в режиме редактирования).
 * @property editCategory Временная категория (в режиме редактирования); null — не выбрана.
 * @property availableIconKeys Список всех доступных иконок для выбора.
 * @property availableGradientKeys Список всех доступных градиентов для выбора.
 * @property completedDates Множество дат выполнения привычки (ISO-строки).
 * @property canIncrementToday Можно ли отметить выполнение сегодня.
 * @property nextIncrementSeconds Секунд до следующей доступной отметки.
 * @property isLoading true — идёт сетевая операция (сохранение, удаление).
 * @property errorMessage Текст ошибки при выполнении операций; null — ошибок нет.
 */
@Immutable
internal data class AddictionDetailsUiResult(
    val addictionId: Int,
    val mode: DetailsMode = DetailsMode.ViewMode,
    val title: String = "",
    val category: AddictionCategoryUi = AddictionCategoryUi.OTHER,
    val iconKey: String = "",
    val gradientKey: String = "",
    val description: String = "",
    val controlDays: Int = 0,
    val editTitle: String = "",
    val editDescription: String = "",
    val editIconKey: String = "",
    val editGradientKey: String = "",
    val editCategory: AddictionCategoryUi? = null,
    val availableIconKeys: List<String> = emptyList(),
    val availableGradientKeys: List<String> = emptyList(),
    val completedDates: Set<String> = emptySet(),
    val canIncrementToday: Boolean = true,
    val nextIncrementSeconds: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
