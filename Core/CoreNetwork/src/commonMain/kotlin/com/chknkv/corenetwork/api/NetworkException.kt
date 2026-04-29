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

    /**
     * Сервер вернул HTTP-ошибку.
     *
     * Объявлен как `data class` для структурного равенства при тестировании.
     * Не содержит PII, поэтому автогенерация [toString] безопасна.
     *
     * Поле названо [description], а не `message`, чтобы не затенять [Throwable.message].
     *
     * @param code        HTTP-статус (например, 400, 404, 500).
     * @param description Краткое описание из статусной строки ответа.
     */
    data class HttpError(val code: Int, val description: String) : NetworkException("HTTP $code: $description")

    /**
     * Сервер вернул HTTP 401 после попытки обновить токен.
     * Вызывающий код должен перенаправить пользователя на экран входа.
     */
    data object Unauthorized : NetworkException("Unauthorized")

    /**
     * Сетевое соединение недоступно (нет интернета или хост недостижим).
     */
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
