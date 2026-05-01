package com.chknkv.feature.addiction.presentation.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.feature.addiction.domain.interactor.AddictionInteractor
import com.chknkv.feature.addiction.models.presentation.select.AddictionSelectionUiAction
import com.chknkv.feature.addiction.models.presentation.select.AddictionSelectionUiEvent
import com.chknkv.feature.addiction.models.presentation.select.AddictionSelectionUiResult
import com.chknkv.feature.addiction.models.presentation.select.AddictionSelectionUiState
import com.chknkv.feature.addiction.presentation.toUi
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
 * ViewModel для управления логикой выбора привычек.
 * 
 * Отвечает за загрузку списка групп привычек, обработку выбора пользователя 
 * (с учетом лимита) и сохранение результатов.
 * 
 * @property interactor Интерактор для работы с данными о привычках.
 */
internal class AddictionSelectionViewModel(
    private val interactor: AddictionInteractor,
) : ViewModel() {

    /** Обработчик необработанных исключений из корутин: логирует ошибку и переводит UI в [AddictionSelectionUiState.Error]. */
    private val addictionSelectionCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        _uiState.value = AddictionSelectionUiState.Error()
    }

    /** Входящий поток действий пользователя; буферизует до 64 элементов. */
    private val _actionFlow = MutableSharedFlow<AddictionSelectionUiAction>(extraBufferCapacity = 64)

    /** Изменяемое состояние экрана. Доступно снаружи через [uiState]. */
    private val _uiState = MutableStateFlow<AddictionSelectionUiState>(AddictionSelectionUiState.Init)
    val uiState: StateFlow<AddictionSelectionUiState> = _uiState.asStateFlow()

    /** Поток одноразовых событий (хаптик при превышении лимита, завершение выбора). */
    private val _uiEvent = MutableSharedFlow<AddictionSelectionUiEvent>(extraBufferCapacity = 16)
    val uiEvent: SharedFlow<AddictionSelectionUiEvent> = _uiEvent.asSharedFlow()

    /** Флаг инициализации экрана. */
    private var isScreenInitialized = false

    /**
     * Инициализирует экран и запускает подписку на события.
     * Повторный вызов игнорируется.
     */
    fun initScreen() {
        if (isScreenInitialized) return
        isScreenInitialized = true
        subscribeToActions()
    }

    /**
     * Отправляет действие пользователя в поток обработки.
     * 
     * @param action Действие для обработки.
     */
    fun emitAction(action: AddictionSelectionUiAction) {
        viewModelScope.launch { _actionFlow.emit(action) }
    }

    /**
     * Подписывается на поток действий и распределяет их по обработчикам.
     */
    private fun subscribeToActions() {
        viewModelScope.launch {
            _actionFlow
                .onStart { emit(AddictionSelectionUiAction.Init) }
                .collect { action ->
                    when (action) {
                        is AddictionSelectionUiAction.Init -> handleInit()
                        is AddictionSelectionUiAction.OnAddictionToggled -> handleToggle(action.addictionId)
                        is AddictionSelectionUiAction.OnSkipClicked -> _uiEvent.emit(AddictionSelectionUiEvent.OnFinished)
                        is AddictionSelectionUiAction.OnNextClicked -> handleNext()
                    }
                }
        }
    }

    /**
     * Обрабатывает начальную загрузку данных.
     */
    private fun handleInit() {
        viewModelScope.launch(addictionSelectionCoroutineExceptionHandler) {
            _uiState.value = AddictionSelectionUiState.Loading
            val groups = interactor.getAddictionGroupsForSelection()
            _uiState.value = AddictionSelectionUiState.Successful(
                AddictionSelectionUiResult(groups = groups.map { it.toUi() })
            )
        }
    }

    /**
     * Обрабатывает выбор или отмену выбора привычки.
     *
     * @param id Идентификатор привычки.
     */
    private suspend fun handleToggle(id: Int) {
        val current = successfulResult ?: return
        val isSelected = id in current.selectedIds
        if (!isSelected && current.isLimitReached) {
            _uiEvent.emit(AddictionSelectionUiEvent.OnSelectionLimitReached)
            return
        }
        val updated = if (isSelected) current.selectedIds - id else current.selectedIds + id
        updateResult(current.copy(selectedIds = updated))
    }

    /**
     * Обрабатывает переход к следующему шагу после выбора привычек.
     * Запускает процесс сохранения. При ошибке сбрасывает [AddictionSelectionUiResult.isSaving]
     * и выставляет [AddictionSelectionUiResult.isFailed] в `true`, не переводя экран в Error-state.
     */
    private fun handleNext() {
        val current = successfulResult ?: return
        if (current.isSaving || current.selectedIds.isEmpty()) return

        val saveExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
            val latest = successfulResult ?: return@CoroutineExceptionHandler
            updateResult(latest.copy(isSaving = false, isFailed = true))
        }

        viewModelScope.launch(saveExceptionHandler) {
            updateResult(current.copy(isSaving = true, isFailed = false))
            interactor.saveSelectedAddictions(current.selectedIds)
            _uiEvent.emit(AddictionSelectionUiEvent.OnFinished)
        }
    }

    /**
     * Возвращает текущий успешный результат или null.
     */
    private val successfulResult: AddictionSelectionUiResult?
        get() = (_uiState.value as? AddictionSelectionUiState.Successful)?.result

    /**
     * Обновляет состояние экрана успешным результатом.
     * 
     * @param result Новый результат для отображения.
     */
    private fun updateResult(result: AddictionSelectionUiResult) {
        _uiState.value = AddictionSelectionUiState.Successful(result)
    }

    companion object {
        private const val TAG = "AddictionSelectionViewModel"
    }
}
