package com.chknkv.feature.addiction.di

import com.chknkv.corenetwork.api.ApiClient
import com.chknkv.feature.addiction.data.mapper.AddictionApiMapper
import com.chknkv.feature.addiction.data.mapper.AddictionApiMapperImpl
import com.chknkv.feature.addiction.data.repository.AddictionRepository
import com.chknkv.feature.addiction.data.repository.AddictionRepositoryImpl
import com.chknkv.feature.addiction.domain.interactor.AddictionInteractor
import com.chknkv.feature.addiction.domain.interactor.AddictionInteractorImpl
import com.chknkv.feature.addiction.presentation.all.AddictionAllViewModel
import com.chknkv.feature.addiction.presentation.create.AddictionCreateViewModel
import com.chknkv.feature.addiction.presentation.details.AddictionDetailsViewModel
import com.chknkv.feature.addiction.presentation.select.AddictionSelectionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * DI-модуль для фичи выбора и отображения привычек.
 */
val featureAddictionModule = module {
    single<AddictionApiMapper> { AddictionApiMapperImpl(get<ApiClient>()) }
    single<AddictionRepository> { AddictionRepositoryImpl(get()) }
    factory<AddictionInteractor> { AddictionInteractorImpl(get()) }
    viewModel { AddictionSelectionViewModel(get()) }
    viewModel { AddictionAllViewModel(get()) }
    viewModel { AddictionCreateViewModel(get()) }
    viewModel { params -> AddictionDetailsViewModel(params.get(), get()) }
}
