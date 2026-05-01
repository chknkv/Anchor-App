package com.chknkv.feature.addiction.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.feature.addiction.domain.interactor.AddictionInteractor
import com.chknkv.feature.addiction.models.domain.AddictionUpdate
import com.chknkv.feature.addiction.models.presentation.AddictionCategoryUi
import com.chknkv.feature.addiction.models.presentation.AddictionGradientUi
import com.chknkv.feature.addiction.models.presentation.AddictionIconUi
import com.chknkv.feature.addiction.models.presentation.ErrorMessageUiResult
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.Init
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.NavigateBack
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.SwitchToEditMode
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.IncrementDays
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.ChangeDeleteConfirmationVisibility
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.ChangeTitle
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.ChangeDescription
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.SelectIcon
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.SelectGradient
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.SelectCategory
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.SubmitEdit
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction.DeleteHabit
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiEvent
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiResult
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiState
import com.chknkv.feature.addiction.models.presentation.details.DetailsMode
import com.chknkv.feature.addiction.presentation.AVAILABLE_GRADIENTS
import com.chknkv.feature.addiction.presentation.AVAILABLE_ICONS
import com.chknkv.feature.addiction.presentation.toDomain
import com.chknkv.feature.addiction.presentation.toUi
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * ViewModel экрана деталей привычки.
 *
 * Отвечает за загрузку данных о привычке, управление режимами просмотра и редактирования,
 * а также за выполнение действий (отметка дня, обновление, удаление).
 *
 * @property addictionId Идентификатор выбранной привычки.
 * @property interactor Интерактор для работы с данными.
 */
internal class AddictionDetailsViewModel(
    private val addictionId: Int,
    private val interactor: AddictionInteractor,
) : ViewModel() {

    private val _actionFlow = MutableSharedFlow<AddictionDetailsUiAction>(extraBufferCapacity = 64)

    /** Поток состояния экрана. */
    private val _uiState = MutableStateFlow<AddictionDetailsUiState>(AddictionDetailsUiState.Init)
    val uiState: StateFlow<AddictionDetailsUiState> = _uiState.asStateFlow()

    /** Поток одноразовых событий (навигация, удаление). */
    private val _uiEvent = MutableSharedFlow<AddictionDetailsUiEvent>(extraBufferCapacity = 16)
    val uiEvent: SharedFlow<AddictionDetailsUiEvent> = _uiEvent.asSharedFlow()

    /** Обработчик ошибок инициализации. */
    private val initExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        _uiState.value = AddictionDetailsUiState.Error(throwable.message)
    }

    /** Обработчик ошибок при сохранении изменений. */
    private val submitExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        val current = successfulResult ?: return@CoroutineExceptionHandler
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(
                isLoading = false,
                isError = ErrorMessageUiResult(isNetworkError = true)
            )
        )
    }

    /** Обработчик ошибок при отметке выполнения. */
    private val incrementExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        val current = successfulResult ?: return@CoroutineExceptionHandler
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(
                isLoading = false,
                isError = ErrorMessageUiResult(isNetworkError = true)
            )
        )
    }

    /** Обработчик ошибок при удалении привычки. */
    private val deleteExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        val current = successfulResult ?: return@CoroutineExceptionHandler
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(
                isLoading = false,
                isError = ErrorMessageUiResult(isNetworkError = true)
            )
        )
    }

    private var isScreenInitialized = false
    private var countdownJob: Job? = null

    /** Инициализирует экран и запускает подписку на действия. */
    fun initScreen() {
        if (isScreenInitialized) return
        isScreenInitialized = true
        subscribeToActions()
    }

    /** Эмитит действие пользователя в поток обработки. */
    fun emitAction(action: AddictionDetailsUiAction) {
        viewModelScope.launch { _actionFlow.emit(action) }
    }

    /** Подписывается на действия и распределяет их по обработчикам. */
    private fun subscribeToActions() {
        viewModelScope.launch {
            _actionFlow
                .onStart { emit(Init) }
                .collect { action ->
                    when (action) {
                        is Init                                 -> handleInit()
                        is NavigateBack                         -> handleNavigateBack()
                        is SwitchToEditMode                     -> handleSwitchToEditMode()
                        is IncrementDays                        -> handleIncrementDays()
                        is DeleteHabit                          -> handleDeleteHabit()
                        is ChangeDeleteConfirmationVisibility   -> handleChangeDeleteConfirmationVisibility(action.isVisible)
                        is ChangeTitle                          -> handleChangeTitle(action.value)
                        is ChangeDescription                    -> handleChangeDescription(action.value)
                        is SelectIcon                           -> handleSelectIcon(action.icon)
                        is SelectGradient                       -> handleSelectGradient(action.gradient)
                        is SelectCategory                       -> handleSelectCategory(action.category)
                        is SubmitEdit                           -> handleSubmitEdit(action.emptyTitleError, action.emptyCategoryError)
                    }
                }
        }
    }

    private fun handleInit() {
        viewModelScope.launch(initExceptionHandler) {
            _uiState.value = AddictionDetailsUiState.Loading
            val addiction = interactor.getClientDetailsAddiction(addictionId)
            val result = AddictionDetailsUiResult(
                addictionId = addiction.id,
                mode = DetailsMode.ViewMode,
                title = addiction.name,
                category = addiction.category.toUi(),
                icon = addiction.iconKey.toUi(),
                gradient = addiction.gradient.toUi(),
                description = addiction.description,
                controlDays = addiction.controlDays,
                editTitle = addiction.name,
                editDescription = addiction.description,
                editIcon = addiction.iconKey.toUi(),
                editGradient = addiction.gradient.toUi(),
                editCategory = addiction.category.toUi(),
                availableIcons = AVAILABLE_ICONS,
                availableGradients = AVAILABLE_GRADIENTS,
                completedDates = addiction.completedDates,
                canIncrementToday = addiction.canIncrementToday,
                nextIncrementSeconds = addiction.nextIncrementAvailableInSeconds,
            )
            _uiState.value = AddictionDetailsUiState.Successful(result)
            startCountdownIfNeeded()
        }
    }

    private fun startCountdownIfNeeded() {
        val current = successfulResult ?: return
        if (current.canIncrementToday || current.nextIncrementSeconds <= 0) return
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val updated = successfulResult ?: break
                val newSeconds = (updated.nextIncrementSeconds - 1).coerceAtLeast(0)
                _uiState.value = AddictionDetailsUiState.Successful(
                    updated.copy(
                        nextIncrementSeconds = newSeconds,
                        canIncrementToday = newSeconds == 0,
                    )
                )
                if (newSeconds == 0) break
            }
        }
    }

    private fun handleSwitchToEditMode() {
        val current = successfulResult ?: return
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(mode = DetailsMode.EditMode, isError = null)
        )
    }

    private fun handleNavigateBack() {
        val current = successfulResult ?: run {
            viewModelScope.launch { _uiEvent.emit(AddictionDetailsUiEvent.NavigateBack) }
            return
        }

        if (current.mode is DetailsMode.EditMode) {
            _uiState.value = AddictionDetailsUiState.Successful(
                current.copy(mode = DetailsMode.ViewMode, isError = null)
            )
        } else {
            viewModelScope.launch { _uiEvent.emit(AddictionDetailsUiEvent.NavigateBack) }
        }
    }

    private fun handleChangeTitle(value: String) {
        val current = successfulResult ?: return
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(editTitle = value, isError = null)
        )
    }

    private fun handleChangeDescription(value: String) {
        val current = successfulResult ?: return
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(editDescription = value, isError = null)
        )
    }

    private fun handleSelectIcon(icon: AddictionIconUi) {
        val current = successfulResult ?: return
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(editIcon = icon, isError = null)
        )
    }

    private fun handleSelectGradient(gradient: AddictionGradientUi) {
        val current = successfulResult ?: return
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(editGradient = gradient, isError = null)
        )
    }

    private fun handleSelectCategory(category: AddictionCategoryUi) {
        val current = successfulResult ?: return
        val newCategory = if (current.editCategory == category) null else category
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(editCategory = newCategory, isError = null)
        )
    }

    private fun handleSubmitEdit(emptyTitleError: String, emptyCategoryError: String) {
        val current = successfulResult ?: return
        if (current.editTitle.isBlank()) {
            _uiState.value = AddictionDetailsUiState.Successful(
                current.copy(isError = ErrorMessageUiResult(message = emptyTitleError))
            )
            return
        }
        if (current.editCategory == null) {
            _uiState.value = AddictionDetailsUiState.Successful(
                current.copy(isError = ErrorMessageUiResult(message = emptyCategoryError))
            )
            return
        }
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(isLoading = true, isError = null)
        )
        viewModelScope.launch(submitExceptionHandler) {
            interactor.updateClientAddiction(
                AddictionUpdate(
                    id = current.addictionId,
                    name = current.editTitle,
                    description = current.editDescription,
                    iconKey = current.editIcon.toDomain(),
                    gradientKey = current.editGradient.toDomain(),
                    categoryKey = current.editCategory.toDomain(),
                )
            )
            countdownJob?.cancel()
            countdownJob = null
            val addiction = interactor.getClientDetailsAddiction(addictionId)
            val result = AddictionDetailsUiResult(
                addictionId = addiction.id,
                mode = DetailsMode.ViewMode,
                title = addiction.name,
                category = addiction.category.toUi(),
                icon = addiction.iconKey.toUi(),
                gradient = addiction.gradient.toUi(),
                description = addiction.description,
                controlDays = addiction.controlDays,
                editTitle = addiction.name,
                editDescription = addiction.description,
                editIcon = addiction.iconKey.toUi(),
                editGradient = addiction.gradient.toUi(),
                editCategory = addiction.category.toUi(),
                availableIcons = AVAILABLE_ICONS,
                availableGradients = AVAILABLE_GRADIENTS,
                completedDates = addiction.completedDates,
                canIncrementToday = addiction.canIncrementToday,
                nextIncrementSeconds = addiction.nextIncrementAvailableInSeconds,
            )
            _uiState.value = AddictionDetailsUiState.Successful(result)
            startCountdownIfNeeded()
        }
    }

    private fun handleIncrementDays() {
        val current = successfulResult ?: return
        if (current.isLoading) return
        _uiState.value = AddictionDetailsUiState.Successful(current.copy(isLoading = true))
        viewModelScope.launch(incrementExceptionHandler) {
            interactor.incrementAddictionControlDays(current.addictionId)
            val updated = successfulResult ?: return@launch
            _uiState.value = AddictionDetailsUiState.Successful(
                updated.copy(
                    controlDays = updated.controlDays + 1,
                    canIncrementToday = false,
                    nextIncrementSeconds = SECONDS_IN_DAY,
                    isLoading = false,
                )
            )
            startCountdownIfNeeded()
        }
    }

    private fun handleChangeDeleteConfirmationVisibility(isVisible: Boolean) {
        val current = successfulResult ?: return
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(isDeleteConfirmationVisible = isVisible)
        )
    }

    private fun handleDeleteHabit() {
        val current = successfulResult ?: return
        if (current.isLoading) return
        _uiState.value = AddictionDetailsUiState.Successful(current.copy(
            isDeleteConfirmationVisible = false,
            isLoading = true,
        ))
        viewModelScope.launch(deleteExceptionHandler) {
            interactor.deleteClientAddiction(current.addictionId)
            _uiEvent.emit(AddictionDetailsUiEvent.HabitDeleted)
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        countdownJob = null
    }

    private val successfulResult: AddictionDetailsUiResult?
        get() = (_uiState.value as? AddictionDetailsUiState.Successful)?.result

    companion object {
        private const val TAG = "AddictionDetailsViewModel"
        private const val SECONDS_IN_DAY = 86400
    }
}
