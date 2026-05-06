package com.chknkv.corenetwork.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException

/**
 * Чистый фасад над [HttpClient], экспонируемый Feature-модулям.
 *
 * Feature-код никогда не импортирует Ktor напрямую — весь сетевой доступ
 * проходит через этот класс и [ApiRequestBuilder] DSL.
 *
 * @param httpClient Сконфигурированный Ktor-клиент из CoreNetwork DI.
 *   Помечен `@PublishedApi internal` — необходим для доступа внутри `inline fun`.
 *   Не предназначен для прямого использования в Feature-модулях.
 */
class ApiClient(@PublishedApi internal val httpClient: HttpClient) {

    /**
     * Выполняет HTTP-запрос.
     *
     * Типовой аргумент [T] должен всегда указываться явно на call-site:
     * - `request<MyResponse> { ... }` — десериализует тело ответа (200/201).
     * - `request<Unit> { ... }` — игнорирует тело (204 No Content).
     *
     * Все сетевые ошибки маппируются в [NetworkException].
     * [CancellationException] пробрасывается без обёртки.
     *
     * @param T     Тип ожидаемого ответа. Передай [Unit] для эндпоинтов с 204.
     * @param block Лямбда конфигурации запроса через [ApiRequestBuilder] DSL.
     * @return Десериализованный объект типа [T], либо [Unit].
     * @throws NetworkException При любой сетевой ошибке.
     */
    suspend inline fun <reified T : Any> execute(block: ApiRequestBuilder.() -> Unit): T {
        val req = ApiRequestBuilder().apply(block)
        return try {
            val response = httpClient.request(req.endpoint) {
                method = req.method
                req.queryParams.forEach { (key, value) ->
                    if (value != null) url.parameters.append(key, value.toString())
                }
                req.body?.let {
                    contentType(ContentType.Application.Json)
                    setBody(it)
                }
            }
            if (T::class == Unit::class) Unit as T else response.body()
        } catch (e: CancellationException) {
            throw e
        } catch (e: ResponseException) {
            throw mapResponseException(e)
        } catch (e: ConnectTimeoutException) {
            throw NetworkException.NoConnection
        } catch (e: SocketTimeoutException) {
            throw NetworkException.NoConnection
        } catch (e: NetworkException) {
            throw e
        } catch (e: Exception) {
            if (e.cause?.let { it::class.simpleName == "UnresolvedAddressException" } == true) {
                throw NetworkException.NoConnection
            }
            throw NetworkException.Unknown(e)
        }
    }

    @PublishedApi
    internal suspend fun mapResponseException(e: ResponseException): NetworkException {
        val error = runCatching { e.response.body<ErrorResponse>() }.getOrNull()
        return when (val code = e.response.status.value) {
            400 -> NetworkException.BadRequest(error)
            401 -> NetworkException.Unauthorized
            403 -> NetworkException.Forbidden(error)
            404 -> NetworkException.NotFound(error)
            409 -> NetworkException.Conflict(error)
            in 500..599 -> NetworkException.ServerError(code, error)
            else -> NetworkException.HttpError(code, error)
        }
    }
}
