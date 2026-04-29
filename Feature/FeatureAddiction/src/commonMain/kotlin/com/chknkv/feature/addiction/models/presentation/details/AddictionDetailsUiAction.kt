package com.chknkv.feature.addiction.models.presentation.details

import com.chknkv.feature.addiction.models.presentation.all.AddictionCategoryUi

/**
 * Интенты (действия пользователя) экрана деталей привычки.
 */
internal sealed interface AddictionDetailsUiAction {
    /** Инициализация экрана. */
    data object Init : AddictionDetailsUiAction
    
    /** Возврат назад. */
    data object NavigateBack : AddictionDetailsUiAction
    
    /** Переход в режим редактирования. */
    data object SwitchToEditMode : AddictionDetailsUiAction
    
    /** Изменение временного заголовка. */
    data class ChangeTitle(val value: String) : AddictionDetailsUiAction
    
    /** Изменение временного описания. */
    data class ChangeDescription(val value: String) : AddictionDetailsUiAction
    
    /** Выбор иконки. */
    data class SelectIcon(val iconKey: String) : AddictionDetailsUiAction
    
    /** Выбор градиента. */
    data class SelectGradient(val gradientKey: String) : AddictionDetailsUiAction
    
    /** Выбор категории. */
    data class SelectCategory(val category: AddictionCategoryUi) : AddictionDetailsUiAction
    
    /** 
     * Сохранение изменений. 
     * 
     * @property emptyTitleError Текст ошибки, если название пустое.
     * @property emptyCategoryError Текст ошибки, если категория не выбрана.
     */
    data class SubmitEdit(
        val emptyTitleError: String,
        val emptyCategoryError: String,
    ) : AddictionDetailsUiAction
    
    /** Увеличение счетчика дней. */
    data object IncrementDays : AddictionDetailsUiAction

    /** Запрос на удаление привычки пользователя. */
    data object DeleteHabit : AddictionDetailsUiAction
}
