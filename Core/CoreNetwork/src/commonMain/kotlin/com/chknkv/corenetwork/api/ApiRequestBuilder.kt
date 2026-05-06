package com.chknkv.corenetwork.api

import io.ktor.http.HttpMethod

/**
 * DSL-билдер для формирования HTTP-запроса через [ApiClient.execute].
 *
 * Пример использования:
 * ```kotlin
 * val result: MyDto = apiClient.request {
 *     endpoint = "user/addictions"
 *     method   = HttpMethod.Get
 *     query("page" to 1, "size" to 20)
 * }
 * ```
 */
class ApiRequestBuilder {

    /** Relative path, добавляемый к baseUrl клиента (без ведущего слэша). */
    var endpoint: String = ""

    /** HTTP-метод запроса. По умолчанию GET. */
    var method: HttpMethod = HttpMethod.Get

    /**
     * Тело запроса. Должно быть `@Serializable`, если метод поддерживает тело.
     * Content-Type всегда `application/json`.
     */
    var body: Any? = null

    /** Накопленные query-параметры. */
    @PublishedApi internal val queryParams: MutableList<Pair<String, Any?>> = mutableListOf()

    /**
     * Добавляет один или несколько query-параметров. Значения `null` игнорируются.
     *
     * @param params Пары ключ–значение для добавления в URL.
     */
    fun query(vararg params: Pair<String, Any?>) {
        queryParams += params
    }
}
