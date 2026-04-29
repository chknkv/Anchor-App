package com.chknkv.corenetwork.client

import com.chknkv.corenetwork.token.TokenRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable

/**
 * Создаёт платформенный [HttpClient] с полной конфигурацией для Anchor App.
 *
 * Android actual → движок OkHttp.
 * iOS actual     → движок Darwin.
 *
 * Base URL передаётся явно: Android читает его из BuildConfig через Koin,
 * iOS — из NSBundle.mainBundle через Koin. Это разделяет ответственность:
 * секрет URL принадлежит app-модулю, а не библиотеке CoreNetwork.
 *
 * @param tokenRepository Репозиторий для чтения и сохранения JWT-токенов.
 * @param baseUrl         Базовый URL API (например, `https://api.anchor.app/v1/`).
 * @return Готовый к использованию [HttpClient].
 */
expect fun createAnchorHttpClient(tokenRepository: TokenRepository, baseUrl: String): HttpClient

/**
 * Применяет единую конфигурацию ко всем платформенным движкам.
 *
 * Устанавливает плагины: ContentNegotiation, Logging (с редакцией токенов),
 * HttpTimeout, Auth (bearer с автоматическим refresh) и defaultRequest.
 *
 * @param tokenRepository Репозиторий для загрузки и обновления токенов.
 * @param baseUrl         Базовый URL API (например, `https://api.anchor.app/v1/`).
 */
internal fun <T : HttpClientEngineConfig> HttpClientConfig<T>.applyAnchorConfig(
    tokenRepository: TokenRepository,
    baseUrl: String,
) {
    install(ContentNegotiation) {
        json(jsonConfig)
    }

    // LogLevel.BODY и ALL запрещены: тело ответа /auth/refresh содержит JWT-токены.
    // Редакция Bearer-заголовка покрывает только заголовки; тело при BODY-логировании не редактируется.
    require(ktorLogLevel != LogLevel.BODY && ktorLogLevel != LogLevel.ALL) {
        "LogLevel.BODY / ALL запрещены: тело ответа содержит JWT-токены. Используйте LogLevel.HEADERS или NONE."
    }

    install(Logging) {
        logger = object : Logger {
            private val tokenPattern = Regex("""Bearer\s+[A-Za-z0-9\-._~+/]+=*""")
            override fun log(message: String) =
                Napier.v(message.replace(tokenPattern, "Bearer [REDACTED]"), tag = "Ktor")
        }
        level = ktorLogLevel
    }

    install(HttpTimeout) {
        requestTimeoutMillis = NetworkConstants.REQUEST_TIMEOUT_MS
        connectTimeoutMillis = NetworkConstants.CONNECT_TIMEOUT_MS
        socketTimeoutMillis = NetworkConstants.SOCKET_TIMEOUT_MS
    }

    install(Auth) {
        // Ktor 3.x: bearer {} — стандартный способ настройки JWT Bearer-аутентификации.
        // RefreshTokensParams предоставляет client, response, oldTokens как ресивер.
        // Внутренний Mutex Ktor-плагина сериализует конкурентные refresh-вызовы.
        // Кэшируем хост один раз — Url(baseUrl) не создаётся на каждый запрос.
        val anchorHost = Url(baseUrl).host

        bearer {
            loadTokens {
                val access = tokenRepository.getAccessToken() ?: return@loadTokens null
                val refresh = tokenRepository.getRefreshToken() ?: return@loadTokens null
                BearerTokens(accessToken = access, refreshToken = refresh)
            }

            refreshTokens {
                val refreshToken = tokenRepository.getRefreshToken() ?: run {
                    tokenRepository.clearTokens()
                    return@refreshTokens null
                }

                // client — ресивер RefreshTokensParams (HttpClient без Auth-плагина).
                // markAsRefreshTokenRequest() предотвращает рекурсивный refresh.
                // Ktor 3.x с expectSuccess=true бросает ResponseException для не-2xx ответов,
                // поэтому ручная проверка статуса не нужна — всё обрабатывается в catch.
                return@refreshTokens try {
                    val response = client.post("$baseUrl${NetworkConstants.REFRESH_ENDPOINT}") {
                        markAsRefreshTokenRequest()
                        contentType(ContentType.Application.Json)
                        setBody(RefreshRequest(refreshToken))
                    }
                    val tokens = response.body<RefreshResponse>()
                    tokenRepository.saveTokens(tokens.accessToken, tokens.refreshToken)
                    BearerTokens(
                        accessToken = tokens.accessToken,
                        refreshToken = tokens.refreshToken,
                    )
                } catch (e: ResponseException) {
                    val status = e.response.status
                    if (status == HttpStatusCode.Unauthorized ||
                        status == HttpStatusCode.Forbidden ||
                        status == HttpStatusCode.BadRequest
                    ) {
                        // Явный отказ сервера — токен отозван или недействителен.
                        tokenRepository.clearTokens()
                    }
                    // Транзиентные ошибки (5xx, 429) — токены сохраняем, попробуем позже.
                    null
                } catch (e: Exception) {
                    // Сетевая ошибка — сохраняем токены, попробуем позже.
                    null
                }
            }

            // Токен прикрепляется только к запросам на хост Anchor API.
            sendWithoutRequest { request ->
                request.url.host == anchorHost
            }
        }
    }

    defaultRequest {
        url(baseUrl)
    }
}

/**
 * DTO для тела запроса обновления токена.
 * Не data class — исключает автогенерацию [toString] с содержимым токена.
 */
@Serializable
private class RefreshRequest(val refreshToken: String)

/**
 * DTO для тела ответа на запрос обновления токена.
 * Не data class — исключает автогенерацию [toString] с содержимым токенов.
 */
@Serializable
private class RefreshResponse(val accessToken: String, val refreshToken: String)
