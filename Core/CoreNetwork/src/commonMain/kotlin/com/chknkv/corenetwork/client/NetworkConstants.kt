package com.chknkv.corenetwork.client

/**
 * Общие константы сетевого слоя: таймауты и служебные эндпоинты.
 */
internal object NetworkConstants {

    /** Максимальное время ожидания полного ответа, мс. */
    const val REQUEST_TIMEOUT_MS = 30_000L

    /** Максимальное время установки соединения, мс. */
    const val CONNECT_TIMEOUT_MS = 10_000L

    /** Максимальное время ожидания данных на сокете, мс. */
    const val SOCKET_TIMEOUT_MS = 30_000L

    /** Relative path эндпоинта для обновления JWT-токенов. */
    const val REFRESH_ENDPOINT = "auth/refresh"
}
