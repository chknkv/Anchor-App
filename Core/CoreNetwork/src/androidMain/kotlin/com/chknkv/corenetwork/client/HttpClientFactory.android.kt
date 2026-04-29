package com.chknkv.corenetwork.client

import com.chknkv.corenetwork.token.TokenRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Android actual: Ktor-клиент с движком OkHttp.
 *
 * Base URL передаётся явно из Koin (зарегистрирован в `AnchorApplication.onCreate()`
 * как `single<String>(named("anchorBaseUrl")) { BuildConfig.BASE_URL }`).
 *
 * @param tokenRepository Репозиторий JWT-токенов для Auth-плагина.
 * @param baseUrl         Базовый URL API из BuildConfig.
 * @return Сконфигурированный [HttpClient].
 */
actual fun createAnchorHttpClient(tokenRepository: TokenRepository, baseUrl: String): HttpClient =
    HttpClient(OkHttp) {
        applyAnchorConfig(tokenRepository, baseUrl)
    }
