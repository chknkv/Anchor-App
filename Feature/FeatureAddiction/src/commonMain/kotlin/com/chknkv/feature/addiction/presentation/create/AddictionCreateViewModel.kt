package com.chknkv.feature.addiction.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.feature.addiction.domain.interactor.AddictionInteractor
import com.chknkv.feature.addiction.models.domain.AddictionCreate
import com.chknkv.feature.addiction.models.presentation.AddictionCategoryUi
import com.chknkv.feature.addiction.models.presentation.AddictionGradientUi
import com.chknkv.feature.addiction.models.presentation.AddictionIconUi
import com.chknkv.feature.addiction.models.presentation.ErrorMessageUiResult
import com.chknkv.feature.addiction.models.presentation.create.AddictionCreateUiAction
import com.chknkv.feature.addiction.models.presentation.create.AddictionCreateUiEvent
import com.chknkv.feature.addiction.models.presentation.create.AddictionCreateUiResult
import com.chknkv.feature.addiction.models.presentation.create.AddictionCreateUiState
import com.chknkv.feature.addiction.presentation.AVAILABLE_GRADIENTS
import com.chknkv.feature.addiction.presentation.AVAILABLE_ICONS
import com.chknkv.feature.addiction.presentation.toDomain
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * ViewModel экрана создания новой привычки.
 *
 * Управляет состоянием формы создания: ввод названия, описания, выбор иконки,
 * градиента и категории. Валидирует поля перед отправкой и сообщает об успехе
 * через [uiEvent].
 *
 * @param interactor Интерактор для создания привычки через доменный слой.
 */
internal class AddictionCreateViewModel(
    private val interactor: AddictionInteractor,
) : ViewModel() {

    private val _actionFlow = MutableSharedFlow<AddictionCreateUiAction>(extraBufferCapacity = 64)

    private val _uiState = MutableStateFlow<AddictionCreateUiState>(AddictionCreateUiState.Init)
    val uiState: StateFlow<AddictionCreateUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddictionCreateUiEvent>(extraBufferCapacity = 16)
    val uiEvent: SharedFlow<AddictionCreateUiEvent> = _uiEvent.asSharedFlow()

    private val submitExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)

        val current = successfulResult ?: return@CoroutineExceptionHandler
        _uiState.value = AddictionCreateUiState.Successful(
            current.copy(isLoading = false, isError = ErrorMessageUiResult(isNetworkError = true))
        )
    }

    private var isScreenInitialized = false

    /** Инициализирует экран и запускает подписку на события. Повторный вызов игнорируется. */
    fun initScreen() {
        if (isScreenInitialized) return
        isScreenInitialized = true
        subscribeToActions()
    }

    /** Отправляет действие пользователя в поток обработки. */
    fun emitAction(action: AddictionCreateUiAction) {
        viewModelScope.launch { _actionFlow.emit(action) }
    }

    /** Подписывается на поток действий и распределяет их по обработчикам. */
    private fun subscribeToActions() {
        viewModelScope.launch {
            _actionFlow
                .onStart { emit(AddictionCreateUiAction.Init) }
                .collect { action ->
                    when (action) {
                        is AddictionCreateUiAction.Init -> handleInit()
                        is AddictionCreateUiAction.ChangeTitle -> handleChangeTitle(action.value)
                        is AddictionCreateUiAction.ChangeDescription -> handleChangeDescription(action.value)
                        is AddictionCreateUiAction.SelectIcon -> handleSelectIcon(action.icon)
                        is AddictionCreateUiAction.SelectGradient -> handleSelectGradient(action.gradient)
                        is AddictionCreateUiAction.SelectCategory -> handleSelectCategory(action.category)
                        is AddictionCreateUiAction.Submit -> handleSubmit(
                            action.emptyTitleError,
                            action.emptyCategoryError,
                        )
                        is AddictionCreateUiAction.NavigateBack -> Unit
                    }
                }
        }
    }

    /** Инициализирует форму значениями по умолчанию. */
    private fun handleInit() {
        val initialResult = AddictionCreateUiResult(
            selectedIcon = AVAILABLE_ICONS.first(),
            selectedGradient = AVAILABLE_GRADIENTS.first(),
            availableIcons = AVAILABLE_ICONS,
            availableGradients = AVAILABLE_GRADIENTS,
            availableCategories = AddictionCategoryUi.entries,
        )
        _uiState.value = AddictionCreateUiState.Successful(initialResult)
    }

    /** Обрабатывает изменение поля названия привычки. */
    private fun handleChangeTitle(value: String) {
        val result = successfulResult ?: return
        updateResult(result.copy(title = value, isError = null))
    }

    /** Обрабатывает изменение поля описания привычки. */
    private fun handleChangeDescription(value: String) {
        val result = successfulResult ?: return
        updateResult(result.copy(description = value, isError = null))
    }

    /** Обрабатывает выбор иконки. */
    private fun handleSelectIcon(icon: AddictionIconUi) {
        val result = successfulResult ?: return
        updateResult(result.copy(selectedIcon = icon, isError = null))
    }

    /** Обрабатывает выбор градиента. */
    private fun handleSelectGradient(gradient: AddictionGradientUi) {
        val result = successfulResult ?: return
        updateResult(result.copy(selectedGradient = gradient, isError = null))
    }

    /** Обрабатывает выбор (или снятие) категории. */
    private fun handleSelectCategory(category: AddictionCategoryUi) {
        val result = successfulResult ?: return
        val newCategory = if (result.selectedCategory == category) null else category
        updateResult(result.copy(selectedCategory = newCategory, isError = null))
    }

    /** Валидирует форму и отправляет запрос на создание привычки. */
    private fun handleSubmit(emptyTitleError: String, emptyCategoryError: String) {
        val result = successfulResult ?: return
        if (result.title.isBlank()) {
            updateResult(result.copy(isError = ErrorMessageUiResult(message = emptyTitleError)))
            return
        }
        if (result.selectedCategory == null) {
            updateResult(result.copy(isError = ErrorMessageUiResult(message = emptyCategoryError)))
            return
        }
        updateResult(result.copy(isLoading = true, isError = null))
        viewModelScope.launch(submitExceptionHandler) {
            interactor.createNewClientAddiction(
                AddictionCreate(
                    name = result.title,
                    description = result.description,
                    iconKey = result.selectedIcon.toDomain(),
                    gradientKey = result.selectedGradient.toDomain(),
                    categoryKey = result.selectedCategory.toDomain(),
                )
            )
            _uiEvent.emit(AddictionCreateUiEvent.OnCreated)
        }
    }

    /** Возвращает текущий успешный результат или null, если состояние не Successful. */
    private val successfulResult: AddictionCreateUiResult?
        get() = (_uiState.value as? AddictionCreateUiState.Successful)?.result

    /** Обновляет состояние экрана успешным результатом. */
    private fun updateResult(result: AddictionCreateUiResult) {
        _uiState.value = AddictionCreateUiState.Successful(result)
    }

    companion object {
        private const val TAG = "AddictionCreateViewModel"
    }
}
