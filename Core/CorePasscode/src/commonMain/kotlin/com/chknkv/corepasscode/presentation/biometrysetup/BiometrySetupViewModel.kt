package com.chknkv.corepasscode.presentation.biometrysetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.corepasscode.domain.BiometricAuthenticator
import com.chknkv.corepasscode.domain.PasscodeRepository
import com.chknkv.corepasscode.models.domain.BiometricContext
import com.chknkv.corepasscode.models.domain.BiometricResult
import com.chknkv.corepasscode.models.presentation.biometrysetup.BiometrySetupUiAction
import com.chknkv.corepasscode.models.presentation.biometrysetup.BiometrySetupUiEvent
import com.chknkv.corepasscode.models.presentation.biometrysetup.BiometrySetupUiResult
import com.chknkv.corepasscode.models.presentation.biometrysetup.BiometrySetupUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * ViewModel для управления экраном настройки биометрии после создания пароля.
 * 
 * Позволяет пользователю включить Face ID / Touch ID / Fingerprint или пропустить этот шаг.
 * 
 * @property repository Репозиторий для сохранения флага доступности биометрии.
 * @property authenticator Сервис для проверки биометрии.
 */
class BiometrySetupViewModel(
    private val repository: PasscodeRepository,
    private val authenticator: BiometricAuthenticator,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BiometrySetupUiState>(BiometrySetupUiState.Init)
    val uiState: StateFlow<BiometrySetupUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<BiometrySetupUiEvent>()
    val uiEvent: SharedFlow<BiometrySetupUiEvent> = _uiEvent.asSharedFlow()

    private val _actionFlow = MutableSharedFlow<BiometrySetupUiAction>(extraBufferCapacity = 32)

    private var biometricContext: BiometricContext? = null
    private var biometricStrings: BiometricStrings? = null
    private var enableJob: Job? = null

    private var isScreenInitialized = false

    /**
     * Инициализирует экран и параметры биометрии.
     * 
     * @param biometricContext Платформенные зависимости для биометрии.
     * @param biometryTitle Заголовок системного окна биометрии.
     * @param biometryReason Описание причины запроса биометрии.
     * @param biometryCancel Текст кнопки отмены.
     */
    fun initScreen(
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
        _uiState.value = BiometrySetupUiState.Successful(
            BiometrySetupUiResult(biometricType = authenticator.availableType())
        )
        subscribeToActions()
    }

    /**
     * Отправляет действие пользователя во ViewModel.
     */
    fun emitAction(action: BiometrySetupUiAction) {
        viewModelScope.launch { _actionFlow.emit(action) }
    }

    /**
     * Запускает коллектор [_actionFlow].
     * `.onStart` гарантирует, что [BiometrySetupUiAction.Init] будет первым обработанным событием.
     */
    private fun subscribeToActions() {
        viewModelScope.launch {
            _actionFlow
                .onStart { emit(BiometrySetupUiAction.Init) }
                .collect { action ->
                    when (action) {
                        is BiometrySetupUiAction.Init -> Unit
                        is BiometrySetupUiAction.Enable -> enable()
                        is BiometrySetupUiAction.Skip -> {
                            repository.isBiometricEnabled = false
                            _uiEvent.emit(BiometrySetupUiEvent.SetupFinished)
                        }
                    }
                }
        }
    }

    private fun enable() {
        val context = biometricContext ?: return
        val strings = biometricStrings ?: return
        if (enableJob?.isActive == true) return

        enableJob = viewModelScope.launch {
            updateResult { it.copy(isLoading = true) }
            val result = authenticator.authenticate(
                context = context,
                title = strings.title,
                subtitle = strings.reason,
                cancelButtonText = strings.cancel,
            )
            updateResult { it.copy(isLoading = false) }
            when (result) {
                BiometricResult.Success -> {
                    repository.isBiometricEnabled = true
                    _uiEvent.emit(BiometrySetupUiEvent.BiometricEnabled)
                    _uiEvent.emit(BiometrySetupUiEvent.SetupFinished)
                }
                BiometricResult.Cancelled -> Unit
                is BiometricResult.Error -> _uiEvent.emit(BiometrySetupUiEvent.BiometricFailed(result.reason))
            }
        }
    }

    private fun updateResult(transform: (BiometrySetupUiResult) -> BiometrySetupUiResult) {
        val current = _uiState.value as? BiometrySetupUiState.Successful ?: return
        _uiState.value = current.copy(result = transform(current.result))
    }

    private data class BiometricStrings(
        val title: String,
        val reason: String,
        val cancel: String,
    )
}
