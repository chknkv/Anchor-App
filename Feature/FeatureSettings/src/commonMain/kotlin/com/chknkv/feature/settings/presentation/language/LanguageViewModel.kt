package com.chknkv.feature.settings.presentation.language

import androidx.lifecycle.ViewModel
import com.chknkv.coreutils.AppLanguage
import com.chknkv.feature.settings.domain.SettingsInteractor
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel для экрана выбора языка.
 * 
 * @property interactor Интерактор для обновления настроек языка.
 */
internal class LanguageViewModel(
    private val interactor: SettingsInteractor,
) : ViewModel() {

    val language: StateFlow<AppLanguage> = interactor.language

    fun setLanguage(language: AppLanguage) {
        interactor.setLanguage(language)
    }
}
