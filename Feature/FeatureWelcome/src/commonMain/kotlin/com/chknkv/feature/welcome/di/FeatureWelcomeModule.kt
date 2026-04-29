package com.chknkv.feature.welcome.di

import com.chknkv.feature.addiction.di.featureAddictionModule
import com.chknkv.feature.welcome.domain.AuthorizationInteractor
import com.chknkv.feature.welcome.domain.AuthorizationInteractorImpl
import com.chknkv.feature.welcome.presentation.WelcomeViewModel
import com.chknkv.feature.welcome.presentation.authorization.AuthorizationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DI-модуль для фичи приветствия (авторизация и онбординг).
 */
val featureWelcomeModule = module {
    includes(featureAddictionModule)

    factory<AuthorizationInteractor> { AuthorizationInteractorImpl() }

    viewModel { WelcomeViewModel(get(), get()) }
    viewModel { AuthorizationViewModel(get(), get()) }
}
