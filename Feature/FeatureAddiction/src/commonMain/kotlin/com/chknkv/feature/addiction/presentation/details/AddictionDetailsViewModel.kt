package com.chknkv.feature.addiction.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.feature.addiction.domain.interactor.AddictionInteractor
import com.chknkv.feature.addiction.models.domain.update.AddictionUpdate
import com.chknkv.feature.addiction.models.presentation.all.AddictionCategoryUi
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiEvent
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiResult
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiState
import com.chknkv.feature.addiction.models.presentation.details.DetailsMode
import com.chknkv.feature.addiction.presentation.GRADIENT_BLACK
import com.chknkv.feature.addiction.presentation.GRADIENT_BLUE
import com.chknkv.feature.addiction.presentation.GRADIENT_DARK_ORANGE
import com.chknkv.feature.addiction.presentation.GRADIENT_DARK_RED
import com.chknkv.feature.addiction.presentation.GRADIENT_GRAY
import com.chknkv.feature.addiction.presentation.GRADIENT_GREEN
import com.chknkv.feature.addiction.presentation.GRADIENT_INDIGO
import com.chknkv.feature.addiction.presentation.GRADIENT_ORANGE
import com.chknkv.feature.addiction.presentation.GRADIENT_PINK
import com.chknkv.feature.addiction.presentation.GRADIENT_PURPLE
import com.chknkv.feature.addiction.presentation.GRADIENT_RED
import com.chknkv.feature.addiction.presentation.ICON_HABIT_FINANCE
import com.chknkv.feature.addiction.presentation.ICON_HABIT_HEALTH
import com.chknkv.feature.addiction.presentation.ICON_HABIT_LIFESTYLE
import com.chknkv.feature.addiction.presentation.ICON_HABIT_PLACEHOLDER
import com.chknkv.feature.addiction.presentation.ICON_HABIT_PRODUCTIVITY
import com.chknkv.feature.addiction.presentation.ICON_HABIT_RELATIONSHIPS
import com.chknkv.feature.addiction.presentation.ICON_HABIT_SPORT
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
            current.copy(isLoading = false, errorMessage = throwable.message)
        )
    }

    /** Обработчик ошибок при отметке выполнения. */
    private val incrementExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        val current = successfulResult ?: return@CoroutineExceptionHandler
        _uiState.value = AddictionDetailsUiState.Successful(current.copy(isLoading = false))
    }

    /** Обработчик ошибок при удалении привычки. */
    private val deleteExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        val current = successfulResult ?: return@CoroutineExceptionHandler
        _uiState.value = AddictionDetailsUiState.Successful(current.copy(isLoading = false))
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
                .onStart { emit(AddictionDetailsUiAction.Init) }
                .collect { action ->
                    when (action) {
                        AddictionDetailsUiAction.Init             -> handleInit()
                        AddictionDetailsUiAction.NavigateBack     -> handleNavigateBack()
                        AddictionDetailsUiAction.SwitchToEditMode -> handleSwitchToEditMode()
                        AddictionDetailsUiAction.IncrementDays    -> handleIncrementDays()
                        AddictionDetailsUiAction.DeleteHabit      -> handleDeleteHabit()
                        is AddictionDetailsUiAction.ChangeTitle       -> handleChangeTitle(action.value)
                        is AddictionDetailsUiAction.ChangeDescription -> handleChangeDescription(action.value)
                        is AddictionDetailsUiAction.SelectIcon        -> handleSelectIcon(action.iconKey)
                        is AddictionDetailsUiAction.SelectGradient    -> handleSelectGradient(action.gradientKey)
                        is AddictionDetailsUiAction.SelectCategory    -> handleSelectCategory(action.category)
                        is AddictionDetailsUiAction.SubmitEdit        -> handleSubmitEdit(
                            action.emptyTitleError, action.emptyCategoryError,
                        )
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
                iconKey = addiction.iconKey,
                gradientKey = addiction.gradient,
                description = addiction.description,
                controlDays = addiction.controlDays,
                editTitle = addiction.name,
                editDescription = addiction.description,
                editIconKey = addiction.iconKey,
                editGradientKey = addiction.gradient,
                editCategory = addiction.category.toUi(),
                availableIconKeys = AVAILABLE_ICON_KEYS,
                availableGradientKeys = AVAILABLE_GRADIENT_KEYS,
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
            current.copy(mode = DetailsMode.EditMode, errorMessage = null)
        )
    }

    private fun handleNavigateBack() {
        val current = successfulResult ?: run {
            viewModelScope.launch { _uiEvent.emit(AddictionDetailsUiEvent.NavigateBack) }
            return
        }

        if (current.mode is DetailsMode.EditMode) {
            _uiState.value = AddictionDetailsUiState.Successful(
                current.copy(mode = DetailsMode.ViewMode, errorMessage = null)
            )
        } else {
            viewModelScope.launch { _uiEvent.emit(AddictionDetailsUiEvent.NavigateBack) }
        }
    }

    private fun handleChangeTitle(value: String) {
        val current = successfulResult ?: return
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(editTitle = value, errorMessage = null)
        )
    }

    private fun handleChangeDescription(value: String) {
        val current = successfulResult ?: return
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(editDescription = value, errorMessage = null)
        )
    }

    private fun handleSelectIcon(iconKey: String) {
        val current = successfulResult ?: return
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(editIconKey = iconKey, errorMessage = null)
        )
    }

    private fun handleSelectGradient(gradientKey: String) {
        val current = successfulResult ?: return
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(editGradientKey = gradientKey, errorMessage = null)
        )
    }

    private fun handleSelectCategory(category: AddictionCategoryUi) {
        val current = successfulResult ?: return
        val newCategory = if (current.editCategory == category) null else category
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(editCategory = newCategory, errorMessage = null)
        )
    }

    private fun handleSubmitEdit(emptyTitleError: String, emptyCategoryError: String) {
        val current = successfulResult ?: return
        if (current.editTitle.isBlank()) {
            _uiState.value = AddictionDetailsUiState.Successful(
                current.copy(errorMessage = emptyTitleError)
            )
            return
        }
        if (current.editCategory == null) {
            _uiState.value = AddictionDetailsUiState.Successful(
                current.copy(errorMessage = emptyCategoryError)
            )
            return
        }
        _uiState.value = AddictionDetailsUiState.Successful(
            current.copy(isLoading = true, errorMessage = null)
        )
        viewModelScope.launch(submitExceptionHandler) {
            interactor.updateClientAddiction(
                AddictionUpdate(
                    id = current.addictionId,
                    name = current.editTitle,
                    description = current.editDescription,
                    iconKey = current.editIconKey,
                    gradientKey = current.editGradientKey,
                    category = current.editCategory.toDomain(),
                )
            )
            handleInit()
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
                    isLoading = false,
                )
            )
        }
    }

    private fun handleDeleteHabit() {
        val current = successfulResult ?: return
        if (current.isLoading) return
        _uiState.value = AddictionDetailsUiState.Successful(current.copy(isLoading = true))
        viewModelScope.launch(deleteExceptionHandler) {
            interactor.deleteClientAddiction(current.addictionId)
            _uiEvent.emit(AddictionDetailsUiEvent.HabitDeleted)
        }
    }

    private val successfulResult: AddictionDetailsUiResult?
        get() = (_uiState.value as? AddictionDetailsUiState.Successful)?.result

    companion object {
        private const val TAG = "AddictionDetailsViewModel"

        private val AVAILABLE_ICON_KEYS: List<String>
            get() = listOf(
                ICON_HABIT_PLACEHOLDER, ICON_HABIT_LIFESTYLE, ICON_HABIT_HEALTH,
                ICON_HABIT_SPORT, ICON_HABIT_PRODUCTIVITY, ICON_HABIT_FINANCE,
                ICON_HABIT_RELATIONSHIPS,
            )

        private val AVAILABLE_GRADIENT_KEYS: List<String>
            get() = listOf(
                GRADIENT_GRAY, GRADIENT_GREEN, GRADIENT_BLUE, GRADIENT_INDIGO,
                GRADIENT_PURPLE, GRADIENT_PINK, GRADIENT_RED, GRADIENT_ORANGE,
                GRADIENT_DARK_ORANGE, GRADIENT_DARK_RED, GRADIENT_BLACK,
            )
    }
}
