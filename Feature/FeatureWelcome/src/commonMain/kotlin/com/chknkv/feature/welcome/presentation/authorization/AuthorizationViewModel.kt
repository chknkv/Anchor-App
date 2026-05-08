package com.chknkv.feature.welcome.presentation.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.coreutils.AppSettings
import com.chknkv.designsystem.otp.PinInputState
import com.chknkv.feature.welcome.domain.interactor.AuthorizationInteractor
import com.chknkv.feature.welcome.domain.OtpException
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
 * и верификации введённого кода.
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

    private var sessionId: String = ""

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

                    is AuthorizationUiAction.OnPrivacyPolicyClicked -> interactor.handlePrivacyPolicyClicked()

                    is AuthorizationUiAction.OnSheetVisibilityChange -> {
                        _uiResult.value = _uiResult.value.copy(
                            otp = if (action.isVisible) OtpUiResult(isSheetVisible = true)
                            else _uiResult.value.otp.copy(isSheetVisible = false)
                        )
                        if (action.isVisible) startTimer() else stopTimer()
                    }

                    is AuthorizationUiAction.OnPinChange ->
                        viewModelScope.launch { onPinChange(action.pinCode) }

                    is AuthorizationUiAction.OnResendOtpClicked ->
                        viewModelScope.launch { onResendOtp() }
                }
            }
        }
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                handleTimerTick()
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
        _uiResult.value = _uiResult.value.copy(
            otp = _uiResult.value.otp.copy(timerValue = 60, isResendAvailable = false)
        )
        startTimer()

        try {
            interactor.resendOtp(sessionId)
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = "resendOtp failed: ${e.message}", throwable = e)
        }
    }

    private fun handleEmailChanged(email: String): AuthorizationUiResult {
        sessionId = ""
        return _uiResult.value.copy(
            email = email,
            isGetOtpEnabled = isEmailValid(email),
            isError = false,
        )
    }

    private suspend fun onGetOtp() {
        if (_uiResult.value.isLoading) return
        _uiResult.value = _uiResult.value.copy(isLoading = true, isError = false)
        sessionId = interactor.sendOtp(_uiResult.value.email)
        _uiResult.value = _uiResult.value.copy(
            isLoading = false,
            otp = OtpUiResult(isSheetVisible = true)
        )
        startTimer()
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
        try {
            val isFirstAuthorized = interactor.verifyOtp(sessionId, pinCode)
            appSettings.setAuthorized(true)
            _uiResult.value = _uiResult.value.copy(
                otp = _uiResult.value.otp.copy(isSheetVisible = false)
            )
            val event = if (isFirstAuthorized) {
                AuthorizationUiEvent.OnAuthorizedNewUser
            } else {
                AuthorizationUiEvent.OnAuthorizedReturningUser
            }
            _uiEvent.emit(event)
        } catch (e: OtpException.InvalidOtp) {
            Napier.e(tag = TAG, message = e.message ?: "Unknown error", throwable = e)
            _uiResult.value = _uiResult.value.copy(
                otp = _uiResult.value.otp.copy(pinState = PinInputState.Error)
            )
        } catch (e: Exception) {
            Napier.e(tag = TAG, message = e.message ?: "Unknown error", throwable = e)
            _uiResult.value = _uiResult.value.copy(
                otp = _uiResult.value.otp.copy(pinState = PinInputState.Error)
            )
        }
    }

    companion object {
        private const val TAG = "AuthorizationViewModel"
    }
}
