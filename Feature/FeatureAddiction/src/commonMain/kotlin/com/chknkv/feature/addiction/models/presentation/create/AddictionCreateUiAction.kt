package com.chknkv.feature.addiction.models.presentation.create

import com.chknkv.feature.addiction.models.presentation.AddictionCategoryUi
import com.chknkv.feature.addiction.models.presentation.AddictionGradientUi
import com.chknkv.feature.addiction.models.presentation.AddictionIconUi

/**
 * Интенты экрана создания привычки.
 */
internal sealed interface AddictionCreateUiAction {
    /** Инициализировать экран. Эмитится автоматически через [kotlinx.coroutines.flow.onStart]. */
    data object Init : AddictionCreateUiAction

    /** Изменить текст названия привычки. @param value Новое значение поля. */
    data class ChangeTitle(val value: String) : AddictionCreateUiAction

    /** Изменить текст описания привычки. @param value Новое значение поля. */
    data class ChangeDescription(val value: String) : AddictionCreateUiAction

    /** Выбрать иконку. @param icon Иконка. */
    data class SelectIcon(val icon: AddictionIconUi) : AddictionCreateUiAction

    /** Выбрать цвет-градиент. @param gradient Градиент. */
    data class SelectGradient(val gradient: AddictionGradientUi) : AddictionCreateUiAction

    /** Выбрать (или снять) категорию. @param category Категория. */
    data class SelectCategory(val category: AddictionCategoryUi) : AddictionCreateUiAction

    /**
     * Отправить форму создания привычки.
     * @param emptyTitleError Локализованный текст ошибки при пустом названии.
     * @param emptyCategoryError Локализованный текст ошибки при не выбранной категории.
     */
    data class Submit(val emptyTitleError: String, val emptyCategoryError: String) : AddictionCreateUiAction

    /** Вернуться назад. */
    data object NavigateBack : AddictionCreateUiAction
}
