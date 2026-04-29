package com.chknkv.coreutils

import org.koin.dsl.module

/**
 * DI-модуль для базовых утилит приложения.
 * Регистрирует сервис настроек [AppSettings] как синглтон.
 * 
 * @param appIdentifier Идентификатор приложения для правильной изоляции данных в настройках.
 */
fun coreUtilsModule(appIdentifier: AppIdentifier) = module {
    single<AppSettings> { AppSettingsImpl(appIdentifier) }
}