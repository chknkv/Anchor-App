package com.chknkv.corenetwork.mock

import com.chknkv.corenetwork.client.jsonConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay

/**
 * Создаёт [HttpClient] на основе [MockEngine].
 *
 * Перехватывает все запросы и отдаёт JSON через [resolver] —
 * без реального сетевого соединения и без Auth-плагина.
 *
 * Используется только в [coreMockNetworkModule] во время разработки,
 * пока бэкенд недоступен.
 *
 * @param baseUrl  Базовый URL, передаётся через Koin (аналогично реальному клиенту).
 * @param resolver Suspend-функция, возвращающая JSON-строку по пути и методу запроса.
 *                 Путь передаётся без ведущего слэша и без базового префикса URL.
 */
internal fun createMockHttpClient(
    baseUrl: String,
    resolver: suspend (path: String, method: HttpMethod) -> String,
): HttpClient {
    val baseEncodedPath = Url(baseUrl).encodedPath.trimEnd('/')

    return HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                val path = request.url.encodedPath
                    .removePrefix(baseEncodedPath)
                    .trimStart('/')

                delay(MOCK_RESPONSE_DELAY_MS)
                val responseJson = resolver(path, request.method)

                respond(
                    content = responseJson,
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString(),
                    ),
                )
            }
        }

        install(ContentNegotiation) {
            json(jsonConfig)
        }

        defaultRequest {
            url(baseUrl)
        }
    }
}

/** Искусственная задержка мок-ответов в миллисекундах — имитирует сетевую латентность. */
private const val MOCK_RESPONSE_DELAY_MS = 1800L