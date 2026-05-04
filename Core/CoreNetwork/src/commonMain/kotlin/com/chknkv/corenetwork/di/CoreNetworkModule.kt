package com.chknkv.corenetwork.di

import com.chknkv.corenetwork.api.ApiClient
import com.chknkv.corenetwork.client.createAnchorHttpClient
import com.chknkv.corenetwork.token.TokenRepository
import com.chknkv.corenetwork.token.TokenRepositoryImpl
import com.chknkv.corenetwork.token.TokenStorage
import com.chknkv.corenetwork.token.createSecureTokenSettings
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin-модуль сетевого слоя CoreNetwork.
 *
 * Подключить в SharedModule:
 * ```kotlin
 * includes(coreNetworkModule, ...)
 * ```
 *
 * Регистрирует:
 * - [TokenRepository] — защищённое хранилище JWT-токенов.
 * - `named("anchorHttpClient")` [HttpClient] — Ktor-клиент с Auth, Logging, ContentNegotiation.
 *   Именованный квалификатор предотвращает конфликт с другими [HttpClient]-биндингами в проекте.
 * - [ApiClient] — DSL-фасад для Feature-модулей.
 *
 * Требует: `single<String>(named("anchorBaseUrl"))` зарегистрированный в app-модуле
 * (Android: `BuildConfig.BASE_URL`, iOS: `NSBundle.mainBundle` INFO.plist).
 */
val coreNetworkModule = module {
    single<TokenRepository> {
        TokenRepositoryImpl(createSecureTokenSettings())
    }
    single<TokenStorage> { get<TokenRepository>() }
    single<HttpClient>(named("anchorHttpClient")) {
        createAnchorHttpClient(get(), get(named("anchorBaseUrl")))
    }
    single {
        ApiClient(get(named("anchorHttpClient")))
    }
}
