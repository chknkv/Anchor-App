package com.chknkv.corepasscode.presentation.enterpasscode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.corepasscode.domain.BiometricAuthenticator
import com.chknkv.corepasscode.domain.PasscodeRepository
import com.chknkv.corepasscode.domain.hashPasscode
import com.chknkv.corepasscode.models.domain.BiometricContext
import com.chknkv.corepasscode.models.domain.BiometricResult
import com.chknkv.corepasscode.models.presentation.enterpasscode.EnterPasscodeUiAction
import com.chknkv.corepasscode.models.presentation.enterpasscode.EnterPasscodeUiEvent
import com.chknkv.corepasscode.models.presentation.enterpasscode.EnterPasscodeUiResult
import com.chknkv.corepasscode.models.presentation.enterpasscode.EnterPasscodeUiState
import com.chknkv.corepasscode.presentation.PASSCODE_LENGTH
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
 * ViewModel для управления экраном ввода пароля (Passcode).
 * 
 * Поддерживает два сценария:
 * 1. Обычный вход в приложение.
 * 2. Верификация текущего пароля перед его сменой.
 * 
 * Также управляет запуском биометрической аутентификации, если она доступна и включена.
 * 
 * @property repository Репозиторий для проверки хэша пароля и настроек биометрии.
 * @property biometricAuthenticator Платформенный сервис аутентификации.
 */
class EnterPasscodeViewModel(
    private val repository: PasscodeRepository,
    private val biometricAuthenticator: BiometricAuthenticator,
) : ViewModel() {

    private val _uiState = MutableStateFlow<EnterPasscodeUiState>(EnterPasscodeUiState.Init)
    val uiState: StateFlow<EnterPasscodeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<EnterPasscodeUiEvent>()
    val uiEvent: SharedFlow<EnterPasscodeUiEvent> = _uiEvent.asSharedFlow()

    private val _actionFlow = MutableSharedFlow<EnterPasscodeUiAction>(extraBufferCapacity = 64)

    private var biometricContext: BiometricContext? = null
    private var biometricStrings: BiometricStrings? = null
    private var biometricJob: Job? = null

    private var isScreenInitialized = false

    /**
     * Инициализирует экран и параметры биометрии.
     * 
     * @param isChangeFlow true, если требуется подтверждение перед сменой пароля.
     * @param biometricContext Платформенные зависимости для биометрии.
     * @param biometryTitle Заголовок системного окна биометрии.
     * @param biometryReason Описание причины запроса биометрии.
     * @param biometryCancel Текст кнопки отмены.
     */
    fun initScreen(
        isChangeFlow: Boolean,
        biometricContext: BiometricContext,
        biometryTitle: String,
        biometryReason: String,
        biometryCancel: String,
    ) {
        if (isScreenInitialized) return
        isScreenInitialized = true
        this.biometricContext = biometricContext
        this.biometricStrings = BiometricStrings(
            title = biometryTitle,
            reason = biometryReason,
            cancel = biometryCancel,
        )
        _uiState.value = EnterPasscodeUiState.Successful(
            EnterPasscodeUiResult(
                isChangeFlow = isChangeFlow,
                biometricType = biometricAuthenticator.availableType(),
                isBiometricAvailable = biometricAuthenticator.isAvailable() && repository.isBiometricEnabled,
            )
        )
        subscribeToActions()
    }

    /**
     * Отправляет действие пользователя во ViewModel.
     */
    fun emitAction(action: EnterPasscodeUiAction) {
        viewModelScope.launch { _actionFlow.emit(action) }
    }

    /**
     * Запускает коллектор [_actionFlow].
     * `.onStart` гарантирует, что [EnterPasscodeUiAction.Init] будет первым обработанным событием.
     */
    private fun subscribeToActions() {
        viewModelScope.launch {
            _actionFlow
                .onStart { emit(EnterPasscodeUiAction.Init) }
                .collect { action ->
                    when (action) {
                        is EnterPasscodeUiAction.Init -> onInit()
                        is EnterPasscodeUiAction.NumberClick -> onDigit(action.digit)
                        is EnterPasscodeUiAction.DeleteClick -> onDelete()
                        is EnterPasscodeUiAction.ShowForgotAlert -> updateResult { it.copy(isForgotAlertVisible = true) }
                        is EnterPasscodeUiAction.HideForgotAlert -> updateResult { it.copy(isForgotAlertVisible = false) }
                        is EnterPasscodeUiAction.ForgotPasscode -> {
                            updateResult { it.copy(isForgotAlertVisible = false) }
                            repository.clearPasscode()
                            _uiEvent.emit(EnterPasscodeUiEvent.ForgotPasscodeRequested)
                        }
                        is EnterPasscodeUiAction.TryBiometric -> tryBiometric()
                    }
                }
        }
    }

    private fun onInit() {
        if (successfulResult()?.isBiometricAvailable == true) {
            viewModelScope.launch {
                delay(AUTO_BIOMETRIC_DELAY_MS)
                tryBiometric()
            }
        }
    }

    private suspend fun onDigit(digit: Int) {
        val current = successfulResult() ?: return
        if (current.enteredDigits.size >= PASSCODE_LENGTH) return
        val next = current.enteredDigits + digit
        updateResult { it.copy(enteredDigits = next, isError = false) }
        if (next.size == PASSCODE_LENGTH) {
            delay(AUTO_CHECK_DELAY_MS)
            verify(next.joinToString(""))
        }
    }

    private fun onDelete() {
        val current = successfulResult() ?: return
        if (current.enteredDigits.isEmpty()) return
        updateResult { it.copy(enteredDigits = current.enteredDigits.dropLast(1), isError = false) }
    }

    private suspend fun verify(passcode: String) {
        val savedHash = repository.getPasscodeHash()
        val enteredHash = hashPasscode(passcode)
        if (savedHash != null && savedHash == enteredHash) {
            _uiEvent.emit(EnterPasscodeUiEvent.EnterSuccess)
        } else {
            _uiEvent.emit(EnterPasscodeUiEvent.InvalidPasscode)
            updateResult {
                it.copy(enteredDigits = emptyList(), isError = true, shakeTrigger = it.shakeTrigger + 1)
            }
        }
    }

    private fun tryBiometric() {
        val context = biometricContext ?: return
        val strings = biometricStrings ?: return
        if (biometricJob?.isActive == true) return
        if (!biometricAuthenticator.isAvailable() || !repository.isBiometricEnabled) return

        biometricJob = viewModelScope.launch {
            val result = biometricAuthenticator.authenticate(
                context = context,
                title = strings.title,
                subtitle = strings.reason,
                cancelButtonText = strings.cancel,
            )
            when (result) {
                BiometricResult.Success -> _uiEvent.emit(EnterPasscodeUiEvent.EnterSuccess)
                BiometricResult.Cancelled -> Unit
                is BiometricResult.Error -> Unit
            }
        }
    }

    private fun successfulResult(): EnterPasscodeUiResult? =
        (_uiState.value as? EnterPasscodeUiState.Successful)?.result

    private fun updateResult(transform: (EnterPasscodeUiResult) -> EnterPasscodeUiResult) {
        val current = _uiState.value as? EnterPasscodeUiState.Successful ?: return
        _uiState.value = current.copy(result = transform(current.result))
    }

    private data class BiometricStrings(
        val title: String,
        val reason: String,
        val cancel: String,
    )

    private companion object {
        const val AUTO_CHECK_DELAY_MS = 200L
        const val AUTO_BIOMETRIC_DELAY_MS = 300L
    }
}
