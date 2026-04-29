package com.chknkv.feature.settings.presentation.appearance

import androidx.lifecycle.ViewModel
import com.chknkv.coreutils.AppTheme
import com.chknkv.feature.settings.domain.SettingsInteractor
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel для экрана выбора темы оформления.
 * 
 * @property interactor Интерактор для обновления настроек темы.
 */
internal class AppearanceViewModel(
    private val interactor: SettingsInteractor,
) : ViewModel() {

    val theme: StateFlow<AppTheme> = interactor.theme

    fun setTheme(theme: AppTheme) {
        interactor.setTheme(theme)
    }
}
