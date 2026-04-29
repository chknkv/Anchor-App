package com.chknkv.feature.settings.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chknkv.feature.settings.domain.SettingsInteractor
import com.chknkv.feature.settings.models.presentation.MainSettingsUiState
import com.chknkv.feature.settings.models.presentation.SettingsLogoutAction
import com.chknkv.feature.settings.models.presentation.SettingsNavigationAction
import com.chknkv.feature.settings.models.presentation.SettingsUiAction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel для главного экрана настроек.
 * 
 * Наблюдает за изменениями темы и языка через [SettingsInteractor] и обновляет 
 * состояние экрана при их изменении. Также обрабатывает действия навигации и выхода.
 * 
 * @property interactor Интерактор для работы с настройками приложения.
 */
internal class MainSettingsViewModel(
    private val interactor: SettingsInteractor,
) : ViewModel() {

    private var isLogoutConfirmVisible = false

    private val _uiState = MutableStateFlow<MainSettingsUiState>(
        MainSettingsUiState.Successful(interactor.buildSettingsResult(false))
    )
    val uiState: StateFlow<MainSettingsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SettingsNavigationAction>(extraBufferCapacity = 16)
    val uiEvent: SharedFlow<SettingsNavigationAction> = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(interactor.theme, interactor.language) { _, _ -> Unit }
                .collect { refreshState() }
        }
    }

    fun onAction(action: SettingsUiAction) {
        when (action) {
            SettingsLogoutAction.ShowLogoutConfirm -> {
                isLogoutConfirmVisible = true
                refreshState()
            }
            SettingsLogoutAction.HideLogoutConfirm -> {
                isLogoutConfirmVisible = false
                refreshState()
            }
            is SettingsNavigationAction -> viewModelScope.launch { _uiEvent.emit(action) }
        }
    }

    private fun refreshState() {
        _uiState.value = MainSettingsUiState.Successful(interactor.buildSettingsResult(isLogoutConfirmVisible))
    }
}
