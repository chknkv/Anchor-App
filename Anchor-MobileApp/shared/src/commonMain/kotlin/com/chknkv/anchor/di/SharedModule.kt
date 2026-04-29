package com.chknkv.anchor.di

import com.chknkv.anchor.root.AnchorViewModel
import com.chknkv.corepasscode.di.corePasscodeModule
import com.chknkv.corenetwork.di.coreNetworkModule
import com.chknkv.coreutils.AppIdentifier
import com.chknkv.coreutils.coreUtilsModule
import com.chknkv.feature.main.di.featureMainModule
import com.chknkv.feature.welcome.di.featureWelcomeModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Общий DI-модуль приложения.
 * 
 * Объединяет в себе модули всех слоев (Core, Feature) и регистрирует 
 * корневую ViewModel приложения.
 */
val sharedModule = module {
    includes(
        coreUtilsModule(AppIdentifier.ANCHOR),
        corePasscodeModule,
        coreNetworkModule,
        featureMainModule,
        featureWelcomeModule,
    )

    viewModel { AnchorViewModel(get(), get()) }
}

/**
 * Инициализирует Koin для всех платформ.
 * 
 * @param appModule Платформозависимый модуль (например, с контекстом Android).
 */
fun initKoin(appModule: Module = module {}) = startKoin {
    modules(appModule, sharedModule)
}
