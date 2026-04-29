package com.chknkv.feature.addiction.models.presentation.create

import com.chknkv.feature.addiction.models.presentation.all.AddictionCategoryUi

/**
 * Интенты экрана создания привычки.
 * Передаются от UI в [AddictionCreateViewModel] через [AddictionCreateViewModel.emitAction].
 */
internal sealed interface AddictionCreateUiAction {
    /** Инициализировать экран. Эмитится автоматически через [kotlinx.coroutines.flow.onStart]. */
    data object Init : AddictionCreateUiAction
    /** Изменить текст названия привычки. @param value Новое значение поля. */
    data class ChangeTitle(val value: String) : AddictionCreateUiAction
    /** Изменить текст описания привычки. @param value Новое значение поля. */
    data class ChangeDescription(val value: String) : AddictionCreateUiAction
    /** Выбрать иконку. @param iconKey Ключ иконки. */
    data class SelectIcon(val iconKey: String) : AddictionCreateUiAction
    /** Выбрать цвет-градиент. @param gradientKey Ключ градиента. */
    data class SelectGradient(val gradientKey: String) : AddictionCreateUiAction
    /** Выбрать (или снять) категорию. @param category Категория. */
    data class SelectCategory(val category: AddictionCategoryUi) : AddictionCreateUiAction
    /**
     * Отправить форму создания привычки.
     * @param emptyTitleError Локализованный текст ошибки при пустом названии.
     * @param emptyCategoryError Локализованный текст ошибки при не выбранной категории.
     */
    data class Submit(val emptyTitleError: String, val emptyCategoryError: String) : AddictionCreateUiAction
    /** Вернуться назад. Обрабатывается через лямбду [AddictionCreateScreen.onBack]. */
    data object NavigateBack : AddictionCreateUiAction
}
