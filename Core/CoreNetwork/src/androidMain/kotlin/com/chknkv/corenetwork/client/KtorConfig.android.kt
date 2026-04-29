package com.chknkv.corenetwork.client

import io.ktor.client.plugins.logging.LogLevel
import kotlinx.serialization.json.Json

/**
 * Android actual: уровень логирования Ktor.
 *
 * Библиотечный модуль не имеет доступа к BuildConfig приложения, поэтому
 * используется безопасный по умолчанию [LogLevel.NONE] для обоих типов сборки.
 * Ktor-логи при необходимости включаются через Napier на уровне приложения.
 */
actual val ktorLogLevel: LogLevel = LogLevel.NONE

/**
 * Android actual: конфигурация JSON-сериализатора.
 *
 * Строгий парсинг (`isLenient = false`) для продакшн-безопасности.
 */
actual val jsonConfig: Json = Json {
    ignoreUnknownKeys = true
    isLenient = false
}
