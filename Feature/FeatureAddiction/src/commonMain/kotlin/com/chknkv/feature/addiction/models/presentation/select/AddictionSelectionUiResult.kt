package com.chknkv.feature.addiction.models.presentation.select

import com.chknkv.feature.addiction.models.presentation.all.AddictionCategoryUi

/**
 * Состояние содержимого экрана выбора привычек (используется в [AddictionSelectionUiState.Successful]).
 *
 * @param groups Список групп привычек для отображения.
 * @param selectedIds Множество идентификаторов выбранных привычек.
 * @param maxSelectable Максимально допустимое число выбранных привычек.
 * @param isSaving Флаг: идёт ли сохранение выбора на сервер.
 */
internal data class AddictionSelectionUiResult(
    val groups: List<AddictionGroupUi> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val maxSelectable: Int = MAX_SELECTABLE,
    val isSaving: Boolean = false,
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
 * @param category Категория группы — используется для заголовка и идентификации.
 * @param addictions Список привычек в группе.
 */
internal data class AddictionGroupUi(
    val category: AddictionCategoryUi,
    val addictions: List<AddictionUi>,
)

/**
 * UI-модель одной привычки для экрана выбора.
 *
 * @param id Уникальный идентификатор привычки (используется при сохранении выбора).
 * @param name Название привычки.
 */
internal data class AddictionUi(
    val id: Int,
    val name: String,
)
