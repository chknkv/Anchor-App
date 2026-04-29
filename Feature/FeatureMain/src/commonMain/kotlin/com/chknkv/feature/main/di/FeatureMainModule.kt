package com.chknkv.feature.main.di

import com.chknkv.feature.addiction.di.featureAddictionModule
import com.chknkv.feature.assistant.di.featureAssistantModule
import com.chknkv.feature.settings.di.featureSettingsModule
import org.koin.dsl.module

/**
 * DI-модуль для основного флоу приложения.
 */
val featureMainModule = module {
    includes(
        featureSettingsModule,
        featureAddictionModule,
        featureAssistantModule
    )
}
