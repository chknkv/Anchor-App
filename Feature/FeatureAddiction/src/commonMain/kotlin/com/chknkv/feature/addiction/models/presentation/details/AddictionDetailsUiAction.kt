package com.chknkv.feature.addiction.models.presentation.details

import com.chknkv.feature.addiction.models.presentation.AddictionCategoryUi
import com.chknkv.feature.addiction.models.presentation.AddictionGradientUi
import com.chknkv.feature.addiction.models.presentation.AddictionIconUi

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
    data class SelectIcon(val icon: AddictionIconUi) : AddictionDetailsUiAction

    /** Выбор градиента. */
    data class SelectGradient(val gradient: AddictionGradientUi) : AddictionDetailsUiAction
    
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

    /** Переключение видимости шторки подтверждения удаления. */
    data class ChangeDeleteConfirmationVisibility(val isVisible: Boolean) : AddictionDetailsUiAction

    /** Запрос на удаление привычки пользователя. */
    data object DeleteHabit : AddictionDetailsUiAction
}
