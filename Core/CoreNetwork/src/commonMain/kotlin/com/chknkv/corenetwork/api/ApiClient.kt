package com.chknkv.corenetwork.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException

/**
 * Чистый фасад над [HttpClient], экспонируемый Feature-модулям.
 *
 * Feature-код никогда не импортирует Ktor напрямую — весь сетевой доступ
 * проходит через этот класс и [ApiRequestBuilder] DSL.
 *
 * @param httpClient Сконфигурированный Ktor-клиент из CoreNetwork DI.
 *   Помечен `@PublishedApi internal` — необходим для доступа внутри `inline fun request`.
 *   Не предназначен для прямого использования в Feature-модулях.
 */
class ApiClient(@PublishedApi internal val httpClient: HttpClient) {

    /**
     * Выполняет типизированный HTTP-запрос и десериализует тело ответа.
     *
     * Все сетевые ошибки маппируются в [NetworkException].
     * [CancellationException] пробрасывается без обёртки — cooperative cancellation сохраняется.
     *
     * @param T     Тип ожидаемого тела ответа (должен быть `@Serializable`).
     * @param block Лямбда конфигурации запроса через [ApiRequestBuilder] DSL.
     * @return Десериализованный объект типа [T].
     * @throws NetworkException При любой сетевой ошибке.
     * @throws CancellationException При отмене родительской корутины.
     */
    suspend inline fun <reified T> request(block: ApiRequestBuilder.() -> Unit): T {
        val req = ApiRequestBuilder().apply(block)
        return try {
            httpClient.request(req.endpoint) {
                method = req.method
                req.queryParams.forEach { (key, value) ->
                    if (value != null) url.parameters.append(key, value.toString())
                }
                req.body?.let {
                    contentType(ContentType.Application.Json)
                    setBody(it)
                }
            }.body()
        } catch (e: CancellationException) {
            // Cooperative cancellation — не оборачиваем в NetworkException.
            throw e
        } catch (e: ResponseException) {
            when (e.response.status) {
                HttpStatusCode.Unauthorized -> throw NetworkException.Unauthorized
                else -> throw NetworkException.HttpError(
                    code = e.response.status.value,
                    description = e.response.status.description,
                )
            }
        } catch (e: ConnectTimeoutException) {
            throw NetworkException.NoConnection
        } catch (e: SocketTimeoutException) {
            throw NetworkException.NoConnection
        } catch (e: NetworkException) {
            throw e
        } catch (e: Exception) {
            // UnresolvedAddressException не экспортирован в commonMain —
            // проверяем по цепочке cause для кросс-платформенного покрытия.
            if (e.cause?.let { it::class.simpleName == "UnresolvedAddressException" } == true) {
                throw NetworkException.NoConnection
            }
            throw NetworkException.Unknown(e)
        }
    }
}
