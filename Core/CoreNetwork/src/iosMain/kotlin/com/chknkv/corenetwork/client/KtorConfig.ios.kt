package com.chknkv.corenetwork.client

import io.ktor.client.plugins.logging.LogLevel
import kotlinx.serialization.json.Json

/**
 * iOS actual: уровень логирования Ktor.
 *
 * Всегда [LogLevel.NONE] — тела запросов никогда не логируются на iOS.
 */
actual val ktorLogLevel: LogLevel = LogLevel.NONE

/**
 * iOS actual: конфигурация JSON-сериализатора.
 *
 * Строгий парсинг (`isLenient = false`) на всех типах iOS-сборок.
 */
actual val jsonConfig: Json = Json {
    ignoreUnknownKeys = true
    isLenient = false
}
