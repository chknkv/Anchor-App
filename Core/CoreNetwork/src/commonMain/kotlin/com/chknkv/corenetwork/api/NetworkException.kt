package com.chknkv.corenetwork.api

/**
 * Иерархия доменных сетевых ошибок.
 *
 * Feature-модули перехватывают [NetworkException] и никогда не работают напрямую
 * с Ktor-типами (например, [io.ktor.client.plugins.ResponseException]).
 *
 * @param message Описание ошибки (не содержит PII).
 * @param cause   Исходное исключение или `null`.
 */
sealed class NetworkException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {

    /** Сервер вернул HTTP 400. */
    data class BadRequest(val error: ErrorResponse?) : NetworkException("HTTP 400")

    /** Сервер вернул HTTP 401 после попытки обновить токен. */
    data object Unauthorized : NetworkException("Unauthorized")

    /** Сервер вернул HTTP 403. */
    data class Forbidden(val error: ErrorResponse?) : NetworkException("HTTP 403")

    /** Сервер вернул HTTP 404. */
    data class NotFound(val error: ErrorResponse?) : NetworkException("HTTP 404")

    /** Сервер вернул HTTP 409. */
    data class Conflict(val error: ErrorResponse?) : NetworkException("HTTP 409")

    /** Сервер вернул HTTP 5xx. */
    data class ServerError(val code: Int, val error: ErrorResponse?) : NetworkException("HTTP $code")

    /**
     * Сервер вернул иной HTTP-код ошибки (не покрытый выше).
     *
     * @param code  HTTP-статус.
     * @param error Тело ошибки от сервера или `null`.
     */
    data class HttpError(val code: Int, val error: ErrorResponse?) : NetworkException("HTTP $code")

    /** Сетевое соединение недоступно (нет интернета или хост недостижим). */
    data object NoConnection : NetworkException("No internet connection")

    /**
     * Неожиданная ошибка, не попавшая в другие категории.
     * В [message] используется только имя класса исключения — PII не утекает.
     *
     * @param cause Исходное исключение.
     */
    class Unknown(cause: Throwable) : NetworkException(
        message = "Network error: ${cause::class.simpleName}",
        cause = cause,
    )
}
