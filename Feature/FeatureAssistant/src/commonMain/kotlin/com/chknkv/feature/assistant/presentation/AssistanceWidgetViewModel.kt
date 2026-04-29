package com.chknkv.feature.assistant.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.feature.assistant.domain.interactor.AssistanceInteractor
import com.chknkv.feature.assistant.models.presentation.AssistanceWidgetUiAction
import com.chknkv.feature.assistant.models.presentation.AssistanceWidgetUiResult
import com.chknkv.feature.assistant.models.presentation.AssistanceWidgetUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * ViewModel виджета помощи.
 *
 * Отвечает за загрузку мотивационной цитаты и управление состоянием [AssistanceWidgetUiState],
 * включая видимость BottomSheet с детальной цитатой.
 *
 * @property interactor Интерактор для получения данных виджета.
 */
internal class AssistanceWidgetViewModel(
    private val interactor: AssistanceInteractor,
) : ViewModel() {

    /** Обработчик необработанных исключений: логирует и переводит UI в [AssistanceWidgetUiState.Error]. */
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        _uiState.value = AssistanceWidgetUiState.Error(throwable.message)
    }

    /** Входящий поток действий; буферизует до 64 элементов без блокировки эмиттера. */
    private val _actionFlow = MutableSharedFlow<AssistanceWidgetUiAction>(extraBufferCapacity = 64)

    private val _uiState = MutableStateFlow<AssistanceWidgetUiState>(AssistanceWidgetUiState.Loading)

    /** Публичный поток состояния для наблюдения из Compose. */
    val uiState: StateFlow<AssistanceWidgetUiState> = _uiState.asStateFlow()

    /** Guard против повторной инициализации при рекомпозиции. */
    private var isInitialized = false

    /**
     * Запускает подписку на действия и начальную загрузку данных.
     * Повторные вызовы — no-op.
     */
    fun initWidget() {
        if (isInitialized) return
        isInitialized = true
        subscribeToActions()
    }

    /**
     * Передаёт действие пользователя в поток обработки.
     *
     * @param action Действие, инициированное пользователем.
     */
    fun emitAction(action: AssistanceWidgetUiAction) {
        viewModelScope.launch { _actionFlow.emit(action) }
    }

    /**
     * Подписывается на [_actionFlow] и маршрутизирует каждое действие.
     * [AssistanceWidgetUiAction.Init] гарантированно эмитится первым через [onStart].
     */
    private fun subscribeToActions() {
        viewModelScope.launch {
            _actionFlow
                .onStart { emit(AssistanceWidgetUiAction.Init) }
                .collect { action ->
                    when (action) {
                        AssistanceWidgetUiAction.Init,
                        AssistanceWidgetUiAction.Refresh -> handleLoadData()
                        AssistanceWidgetUiAction.ShowQuoteSheet -> handleShowQuoteSheet()
                        AssistanceWidgetUiAction.HideQuoteSheet -> handleHideQuoteSheet()
                    }
                }
        }
    }

    private val successfulResult: AssistanceWidgetUiResult?
        get() = (_uiState.value as? AssistanceWidgetUiState.Successful)?.result

    private fun handleLoadData() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.value = AssistanceWidgetUiState.Loading
            loadData()
        }
    }

    private fun handleShowQuoteSheet() {
        val current = successfulResult ?: return
        _uiState.value = AssistanceWidgetUiState.Successful(current.copy(isQuoteSheetVisible = true))
    }

    private fun handleHideQuoteSheet() {
        val current = successfulResult ?: return
        _uiState.value = AssistanceWidgetUiState.Successful(current.copy(isQuoteSheetVisible = false))
    }

    private suspend fun loadData() {
        val quote = interactor.getMotivationalQuote()
        _uiState.value = AssistanceWidgetUiState.Successful(
            AssistanceWidgetUiResult(
                quoteText = quote.text,
                quoteDetailText = quote.detailText,
            )
        )
    }

    companion object {
        private const val TAG = "AssistanceWidgetViewModel"
    }
}


//private const val WEEKS_COUNT = 16
//private const val DAYS_IN_WEEK = 7
//
//private val CELL_GAP = 5.dp