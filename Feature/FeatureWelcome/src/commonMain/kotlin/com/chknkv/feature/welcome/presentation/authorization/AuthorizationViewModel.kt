package com.chknkv.feature.welcome.presentation.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.coreutils.AppSettings
import com.chknkv.designsystem.otp.PinInputState
import com.chknkv.feature.welcome.domain.AuthorizationInteractor
import com.chknkv.feature.welcome.models.presentation.authorization.AuthorizationUiAction
import com.chknkv.feature.welcome.models.presentation.authorization.AuthorizationUiEvent
import com.chknkv.feature.welcome.models.presentation.authorization.AuthorizationUiResult
import com.chknkv.feature.welcome.models.presentation.authorization.OtpUiResult
import com.chknkv.feature.welcome.presentation.isEmailValid
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
import kotlinx.coroutines.launch

/**
 * ViewModel для управления процессом авторизации.
 * 
 * Реализует логику ввода email, запроса OTP-кода, управления таймером повторной отправки 
 * и верификации введенного кода.
 * 
 * @property interactor Интерактор для выполнения сетевых запросов авторизации.
 * @property appSettings Настройки приложения для сохранения состояния авторизации.
 */
internal class AuthorizationViewModel(
    private val interactor: AuthorizationInteractor,
    private val appSettings: AppSettings,
) : ViewModel() {

    private val authorizationCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        _uiResult.value = _uiResult.value.copy(isLoading = false, isError = true)
    }

    private val actions = MutableSharedFlow<AuthorizationUiAction>(extraBufferCapacity = 64)

    private val _uiResult = MutableStateFlow(AuthorizationUiResult())
    val uiResult: StateFlow<AuthorizationUiResult> = _uiResult.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AuthorizationUiEvent>(extraBufferCapacity = 16)
    val uiEvent: SharedFlow<AuthorizationUiEvent> = _uiEvent.asSharedFlow()

    private var timerJob: Job? = null

    init {
        observeActions()
    }

    fun emitAction(action: AuthorizationUiAction) {
        actions.tryEmit(action)
    }

    private fun observeActions() {
        viewModelScope.launch {
            actions.collect { action ->
                when (action) {
                    is AuthorizationUiAction.OnEmailChanged ->
                        _uiResult.value = handleEmailChanged(action.email)
                    is AuthorizationUiAction.OnGetOtpClicked ->
                        viewModelScope.launch(authorizationCoroutineExceptionHandler) { onGetOtp() }
                    is AuthorizationUiAction.OnTermsClicked -> interactor.handleTermsClicked()
                    is AuthorizationUiAction.OnAuthorizedClicked ->
                        viewModelScope.launch { _uiEvent.emit(AuthorizationUiEvent.OnAuthorized) }
                    is AuthorizationUiAction.OnSheetVisibilityChange -> {
                        _uiResult.value = _uiResult.value.copy(
                            otp = if (action.isVisible) OtpUiResult(isSheetVisible = true)
                            else _uiResult.value.otp.copy(isSheetVisible = false)
                        )
                        if (action.isVisible) startTimer() else stopTimer()
                    }
                    is AuthorizationUiAction.OnPinChange ->
                        viewModelScope.launch(authorizationCoroutineExceptionHandler) { onPinChange(action.pinCode) }
                    is AuthorizationUiAction.OnResendOtpClicked ->
                        viewModelScope.launch(authorizationCoroutineExceptionHandler) { onResendOtp() }
                    is AuthorizationUiAction.OnTimerTick -> handleTimerTick()
                }
            }
        }
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                emitAction(AuthorizationUiAction.OnTimerTick)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun handleTimerTick() {
        val currentOtp = _uiResult.value.otp
        if (currentOtp.timerValue > 0) {
            val newValue = currentOtp.timerValue - 1
            _uiResult.value = _uiResult.value.copy(
                otp = currentOtp.copy(
                    timerValue = newValue,
                    isResendAvailable = newValue == 0
                )
            )
        } else {
            stopTimer()
        }
    }

    private suspend fun onResendOtp() {
        interactor.resendOtp(_uiResult.value.email)
        _uiResult.value = _uiResult.value.copy(
            otp = _uiResult.value.otp.copy(
                timerValue = 60,
                isResendAvailable = false
            )
        )
        startTimer()
    }

    private fun handleEmailChanged(email: String): AuthorizationUiResult = _uiResult.value.copy(
        email = email,
        isGetOtpEnabled = isEmailValid(email),
        isError = false
    )

    private suspend fun onGetOtp() {
        _uiResult.value = _uiResult.value.copy(isLoading = true, isError = false)
        val emailOtpResult = interactor.handleGetOtp(_uiResult.value.email)
        _uiResult.value = _uiResult.value.copy(
            isLoading = false,
            isError = !emailOtpResult,
            otp = OtpUiResult(isSheetVisible = emailOtpResult)
        )
        if (emailOtpResult) startTimer()
    }

    private suspend fun onPinChange(pinCode: String) {
        _uiResult.value = _uiResult.value.copy(
            otp = _uiResult.value.otp.copy(
                pinCode = pinCode,
                pinState = PinInputState.Input
            )
        )
        if (pinCode.length == 5) {
            onCheckOtp(pinCode)
        }
    }

    private suspend fun onCheckOtp(pinCode: String) {
        _uiResult.value = _uiResult.value.copy(
            otp = _uiResult.value.otp.copy(pinState = PinInputState.Loading)
        )
        val isSuccess = interactor.checkOtp(pinCode)
        if (isSuccess) {
            appSettings.setAuthorized(true)
            _uiResult.value = _uiResult.value.copy(
                otp = _uiResult.value.otp.copy(isSheetVisible = false)
            )
            _uiEvent.emit(AuthorizationUiEvent.OnAuthorized)
        } else {
            _uiResult.value = _uiResult.value.copy(
                otp = _uiResult.value.otp.copy(pinState = PinInputState.Error)
            )
        }
    }

    companion object {
        private const val TAG = "AuthorizationViewModel"
    }
}
