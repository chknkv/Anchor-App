package com.chknkv.feature.assistant.di

import com.chknkv.corenetwork.api.ApiClient
import com.chknkv.feature.assistant.data.mapper.AssistanceApiMapper
import com.chknkv.feature.assistant.data.mapper.AssistanceApiMapperImpl
import com.chknkv.feature.assistant.data.repository.AssistanceRepository
import com.chknkv.feature.assistant.data.repository.AssistanceRepositoryImpl
import com.chknkv.feature.assistant.domain.interactor.AssistanceInteractor
import com.chknkv.feature.assistant.domain.interactor.AssistanceInteractorImpl
import com.chknkv.feature.assistant.presentation.AssistanceWidgetViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin DI-модуль фичи виджета помощи.
 *
 * Регистрирует:
 * - [AssistanceApiMapper] как `single` — единственный экземпляр сетевого маппера.
 * - [AssistanceRepository] как `single` — future-proof для состояния кэша / БД.
 * - [AssistanceInteractor] как `factory` — без состояния.
 * - [AssistanceWidgetViewModel] — жизненный цикл управляется [androidx.lifecycle.ViewModelStore].
 *
 * Подключение: добавить `includes(featureAssistantModule)` в `featureMainModule`.
 */
val featureAssistantModule = module {
    single<AssistanceApiMapper> { AssistanceApiMapperImpl(get<ApiClient>()) }
    single<AssistanceRepository> { AssistanceRepositoryImpl(get()) }
    factory<AssistanceInteractor> { AssistanceInteractorImpl(get()) }
    viewModel { AssistanceWidgetViewModel(get()) }
}
