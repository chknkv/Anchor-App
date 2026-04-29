package com.chknkv.feature.settings.di

import com.chknkv.feature.settings.domain.SettingsInteractor
import com.chknkv.feature.settings.domain.SettingsInteractorImpl
import com.chknkv.feature.settings.presentation.appearance.AppearanceViewModel
import com.chknkv.feature.settings.presentation.language.LanguageViewModel
import com.chknkv.feature.settings.presentation.main.MainSettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DI-модуль для фичи настроек.
 */
val featureSettingsModule = module {
    single<SettingsInteractor> { SettingsInteractorImpl(get()) }

    viewModel { MainSettingsViewModel(get()) }
    viewModel { AppearanceViewModel(get()) }
    viewModel { LanguageViewModel(get()) }
}
