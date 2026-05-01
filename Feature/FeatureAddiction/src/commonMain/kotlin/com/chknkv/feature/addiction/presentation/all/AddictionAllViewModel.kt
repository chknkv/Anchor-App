package com.chknkv.feature.addiction.presentation.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.feature.addiction.domain.interactor.AddictionInteractor
import com.chknkv.feature.addiction.models.presentation.all.AddictionAllUiAction
import com.chknkv.feature.addiction.models.presentation.all.AddictionAllUiState
import com.chknkv.feature.addiction.presentation.toUiResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * ViewModel экрана всех привычек пользователя.
 *
 * Отвечает за загрузку списка привычек, управление фильтром по категории
 * и обработку обновления данных.
 *
 * @property interactor Интерактор для получения данных о привычках пользователя.
 */
internal class AddictionAllViewModel(
    private val interactor: AddictionInteractor,
) : ViewModel() {

    /** Обработчик необработанных исключений из корутин: логирует ошибку и переводит UI в состояние [AddictionAllUiState.Error]. */
    private val addictionAllCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        _uiState.value = AddictionAllUiState.Error(throwable.message)
    }

    /** Входящий поток действий пользователя; буферизует до 64 элементов. */
    private val _actionFlow = MutableSharedFlow<AddictionAllUiAction>(extraBufferCapacity = 64)

    /** Изменяемое состояние экрана. Доступно снаружи через [uiState]. */
    private val _uiState = MutableStateFlow<AddictionAllUiState>(AddictionAllUiState.Init)

    /** Публичный поток состояния экрана для наблюдения из Compose. */
    val uiState: StateFlow<AddictionAllUiState> = _uiState.asStateFlow()

    /** Флаг однократной инициализации экрана; предотвращает повторный запуск при рекомпозиции. */
    private var isScreenInitialized = false

    /**
     * Инициализирует экран: запускает подписку на действия и начальную загрузку данных.
     * Повторные вызовы — no-op.
     */
    fun initScreen() {
        if (isScreenInitialized) return
        isScreenInitialized = true
        subscribeToActions()
        subscribeToUpdates()
    }

    /**
     * Подписывается на поток обновлений из интерактора.
     * При получении события инициирует обновление данных.
     */
    private fun subscribeToUpdates() {
        viewModelScope.launch {
            interactor.updates.collectLatest { handleRefresh() }
        }
    }

    /**
     * Передаёт действие пользователя в поток обработки.
     *
     * @param action Действие, инициированное пользователем или системой.
     */
    fun emitAction(action: AddictionAllUiAction) {
        viewModelScope.launch { _actionFlow.emit(action) }
    }

    /**
     * Подписывается на [_actionFlow] и маршрутизирует каждое действие в соответствующий handle-метод.
     * Инициирует начальную загрузку через [onStart].
     */
    private fun subscribeToActions() {
        viewModelScope.launch {
            _actionFlow
                .onStart { emit(AddictionAllUiAction.Init) }
                .collect { action ->
                    when (action) {
                        is AddictionAllUiAction.Init -> handleInit()
                        is AddictionAllUiAction.Refresh -> handleRefresh()
                    }
                }
        }
    }

    /** Переводит экран в состояние загрузки и запускает первичное получение данных. */
    private fun handleInit() = loadScreen()

    /** Переводит экран в состояние загрузки и повторно запрашивает данные. */
    private fun handleRefresh() = loadScreen()

    /**
     * Переводит экран в состояние загрузки и выполняет загрузку данных.
     * Используется как при первичной инициализации, так и при обновлении.
     */
    private fun loadScreen() {
        viewModelScope.launch(addictionAllCoroutineExceptionHandler) {
            _uiState.value = AddictionAllUiState.Loading
            loadData()
        }
    }

    /**
     * Загружает список привычек через интерактор и переводит экран в [AddictionAllUiState.Successful]
     * или [AddictionAllUiState.Empty] в зависимости от результата.
     */
    private suspend fun loadData() {
        val addictions = interactor.getAllClientAddictions()
        _uiState.value = if (addictions.isEmpty()) {
            AddictionAllUiState.Empty
        } else {
            AddictionAllUiState.Successful(addictions.toUiResult())
        }
    }

    companion object {
        private const val TAG = "AddictionAllViewModel"
    }
}
