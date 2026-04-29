package com.chknkv.corepasscode.presentation.createpasscode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.corepasscode.domain.PasscodeRepository
import com.chknkv.corepasscode.domain.hashPasscode
import com.chknkv.corepasscode.models.presentation.createpasscode.CreatePasscodeUiAction
import com.chknkv.corepasscode.models.presentation.createpasscode.CreatePasscodeUiEvent
import com.chknkv.corepasscode.models.presentation.createpasscode.CreatePasscodeUiResult
import com.chknkv.corepasscode.models.presentation.createpasscode.CreatePasscodeUiState
import com.chknkv.corepasscode.presentation.PASSCODE_LENGTH
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
 * ViewModel для управления экраном создания пароля (Passcode).
 * 
 * Реализует двухэтапный процесс:
 * 1. Первичный ввод пароля.
 * 2. Повторный ввод для подтверждения.
 * 
 * @property repository Репозиторий для сохранения хэшированного пароля.
 */
class CreatePasscodeViewModel(
    private val repository: PasscodeRepository,
) : ViewModel() {

    /**
     * Поток состояний экрана. UI подписывается на него для отображения данных.
     */
    private val _uiState = MutableStateFlow<CreatePasscodeUiState>(CreatePasscodeUiState.Init)
    val uiState: StateFlow<CreatePasscodeUiState> = _uiState.asStateFlow()

    /**
     * Поток разовых событий (эффектов), таких как навигация или показ уведомлений.
     */
    private val _uiEvent = MutableSharedFlow<CreatePasscodeUiEvent>()
    val uiEvent: SharedFlow<CreatePasscodeUiEvent> = _uiEvent.asSharedFlow()

    /**
     * Внутренний поток действий пользователя для асинхронной обработки.
     */
    private val _actionFlow = MutableSharedFlow<CreatePasscodeUiAction>(extraBufferCapacity = 64)

    private var firstPasscode: String? = null

    private var isScreenInitialized = false

    /**
     * Инициализирует начальное состояние экрана.
     * @param isChangeFlow true, если экран открыт во флоу смены пароля (скрывает кнопку пропуска).
     */
    fun initScreen(isChangeFlow: Boolean) {
        if (isScreenInitialized) return
        isScreenInitialized = true
        _uiState.value = CreatePasscodeUiState.Successful(
            CreatePasscodeUiResult(isSkipAvailable = !isChangeFlow)
        )
        subscribeToActions()
    }

    /**
     * Отправляет действие пользователя на обработку во ViewModel.
     * @param action Действие (например, клик по кнопке).
     */
    fun emitAction(action: CreatePasscodeUiAction) {
        viewModelScope.launch { _actionFlow.emit(action) }
    }

    /**
     * Запускает коллектор [_actionFlow].
     * `.onStart` гарантирует, что [CreatePasscodeUiAction.Init] будет первым обработанным событием.
     */
    private fun subscribeToActions() {
        viewModelScope.launch {
            _actionFlow
                .onStart { emit(CreatePasscodeUiAction.Init) }
                .collect { action ->
                    when (action) {
                        is CreatePasscodeUiAction.Init -> resetToStep1()
                        is CreatePasscodeUiAction.NumberClick -> onDigit(action.digit)
                        is CreatePasscodeUiAction.DeleteClick -> onDelete()
                        is CreatePasscodeUiAction.ShowSkipAlert -> updateResult { it.copy(isSkipAlertVisible = true) }
                        is CreatePasscodeUiAction.DismissSkipAlert -> updateResult { it.copy(isSkipAlertVisible = false) }
                        is CreatePasscodeUiAction.Skip -> {
                            updateResult { it.copy(isSkipAlertVisible = false) }
                            _uiEvent.emit(CreatePasscodeUiEvent.SkipRequested)
                        }
                    }
                }
        }
    }

    private suspend fun onDigit(digit: Int) {
        val current = successfulResult() ?: return
        if (current.enteredDigits.size >= PASSCODE_LENGTH) return
        val next = current.enteredDigits + digit
        updateResult { it.copy(enteredDigits = next) }
        if (next.size == PASSCODE_LENGTH) {
            delay(AUTO_SUBMIT_DELAY_MS)
            handleFullInput(next.joinToString(""))
        }
    }

    private fun onDelete() {
        val current = successfulResult() ?: return
        if (current.enteredDigits.isEmpty()) return
        updateResult { it.copy(enteredDigits = current.enteredDigits.dropLast(1)) }
    }

    private suspend fun handleFullInput(passcode: String) {
        val firstCandidate = firstPasscode
        if (firstCandidate == null) {
            firstPasscode = passcode
            updateResult { it.copy(enteredDigits = emptyList(), isConfirming = true) }
        } else {
            if (firstCandidate == passcode) {
                repository.savePasscodeHash(hashPasscode(passcode))
                firstPasscode = null
                _uiEvent.emit(CreatePasscodeUiEvent.PasscodeCreated)
            } else {
                _uiEvent.emit(CreatePasscodeUiEvent.PasscodesDoNotMatch)
                updateResult { it.copy(shakeTrigger = it.shakeTrigger + 1) }
                delay(SHAKE_DURATION_MS)
                resetToStep1()
            }
        }
    }

    private fun resetToStep1() {
        firstPasscode = null
        updateResult { it.copy(enteredDigits = emptyList(), isConfirming = false, isSkipAlertVisible = false) }
    }

    private fun successfulResult(): CreatePasscodeUiResult? =
        (_uiState.value as? CreatePasscodeUiState.Successful)?.result

    private fun updateResult(transform: (CreatePasscodeUiResult) -> CreatePasscodeUiResult) {
        val current = _uiState.value as? CreatePasscodeUiState.Successful ?: return
        _uiState.value = current.copy(result = transform(current.result))
    }

    private companion object {
        const val AUTO_SUBMIT_DELAY_MS = 150L
        const val SHAKE_DURATION_MS = 400L
    }
}
