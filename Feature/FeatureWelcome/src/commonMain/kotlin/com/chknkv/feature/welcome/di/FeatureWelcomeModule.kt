package com.chknkv.feature.welcome.di

import com.chknkv.feature.addiction.di.featureAddictionModule
import com.chknkv.feature.welcome.data.mapper.AuthorizationApiMapper
import com.chknkv.feature.welcome.data.mapper.AuthorizationApiMapperImpl
import com.chknkv.feature.welcome.data.repository.AuthorizationRepositoryImpl
import com.chknkv.feature.welcome.domain.interactor.AuthorizationInteractor
import com.chknkv.feature.welcome.domain.interactor.AuthorizationInteractorImpl
import com.chknkv.feature.welcome.data.repository.AuthorizationRepository
import com.chknkv.feature.welcome.presentation.WelcomeViewModel
import com.chknkv.feature.welcome.presentation.authorization.AuthorizationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DI-модуль для фичи приветствия (авторизация и онбординг).
 */
val featureWelcomeModule = module {
    includes(featureAddictionModule)

    factory<AuthorizationApiMapper> { AuthorizationApiMapperImpl(apiClient = get()) }
    single<AuthorizationRepository> {
        AuthorizationRepositoryImpl(apiMapper = get(), tokenStorage = get())
    }
    factory<AuthorizationInteractor> { AuthorizationInteractorImpl(repository = get()) }

    viewModel { WelcomeViewModel(get(), get()) }
    viewModel { AuthorizationViewModel(get(), get()) }
}
