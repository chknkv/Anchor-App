package com.chknkv.corenetwork.mock

import com.chknkv.corenetwork.api.ApiClient
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin-модуль мок-сетевого слоя.
 *
 * Отличия от реального модуля:
 * - Не регистрирует [com.chknkv.corenetwork.token.TokenRepository] — авторизация не нужна для мокка.
 * - [HttpClient] использует [io.ktor.client.engine.mock.MockEngine] вместо OkHttp/Darwin.
 * - JSON-ответы читаются из `composeResources/files/mock/`.
 * - Реальные сетевые запросы не выполняются.
 *
 * Возврат к реальному API: вернуть `includes(coreNetworkModule)` в SharedModule.
 */
val coreMockNetworkModule = module {
    single<HttpClient>(named("anchorHttpClient")) {
        createMockHttpClient(
            baseUrl = get(named("anchorBaseUrl")),
            resolver = MockApiResponses::resolve,
        )
    }
    single {
        ApiClient(get(named("anchorHttpClient")))
    }
}
