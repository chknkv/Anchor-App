package com.chknkv.corenetwork.client

import io.ktor.client.plugins.logging.LogLevel
import kotlinx.serialization.json.Json

/**
 * Уровень логирования Ktor, зависящий от типа сборки.
 *
 * Android debug  → [LogLevel.BODY] (тела запросов/ответов редактируются).
 * Android release → [LogLevel.NONE].
 * iOS (любая)    → [LogLevel.NONE].
 */
expect val ktorLogLevel: LogLevel

/**
 * Конфигурация JSON-сериализатора для ContentNegotiation плагина.
 *
 * Android debug  → `isLenient = true` (удобство при разработке).
 * Android release → `isLenient = false` (строгий парсинг).
 * iOS            → `isLenient = false` всегда.
 */
expect val jsonConfig: Json
