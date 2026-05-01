package com.chknkv.feature.addiction.models.presentation.select

import androidx.compose.runtime.Immutable
import com.chknkv.feature.addiction.models.presentation.AddictionCategoryUi

/**
 * Состояние содержимого экрана выбора привычек (используется в [AddictionSelectionUiState.Successful]).
 *
 * @property groups Список групп привычек для отображения.
 * @property selectedIds Множество идентификаторов выбранных привычек.
 * @property maxSelectable Максимально допустимое число выбранных привычек.
 * @property isSaving Флаг: идёт ли сохранение выбора на сервер.
 * @property isFailed Флаг: произошла ли ошибка при отправке данных на сервер.
 */
@Immutable
internal data class AddictionSelectionUiResult(
    val groups: List<AddictionGroupUi> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val maxSelectable: Int = MAX_SELECTABLE,
    val isSaving: Boolean = false,
    val isFailed: Boolean = false
) {
    /** Количество выбранных привычек. */
    val selectedCount: Int get() = selectedIds.size

    /** Достигнут ли лимит выбранных привычек. */
    val isLimitReached: Boolean get() = selectedCount >= maxSelectable

    /** Должна ли кнопка показывать «Далее» (а не «Пропустить»). */
    val isNextMode: Boolean get() = selectedCount > 0

    companion object {
        const val MAX_SELECTABLE = 3
    }
}

/**
 * UI-модель группы привычек для экрана выбора.
 *
 * Группа идентифицируется категорией (enum), а не числовым ID.
 * Заголовок разрешается в composable через `stringResource(category.toTitleStringResource())`.
 *
 * @property category Категория группы — используется для заголовка и идентификации.
 * @property addictions Список привычек в группе.
 */
@Immutable
internal data class AddictionGroupUi(
    val category: AddictionCategoryUi,
    val addictions: List<AddictionUi>,
)

/**
 * UI-модель одной привычки для экрана выбора.
 *
 * @property id Уникальный идентификатор привычки (используется при сохранении выбора).
 * @property name Название привычки.
 */
@Immutable
internal data class AddictionUi(
    val id: Int,
    val name: String,
)
