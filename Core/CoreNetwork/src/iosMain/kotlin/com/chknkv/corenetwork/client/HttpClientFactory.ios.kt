package com.chknkv.corenetwork.client

import com.chknkv.corenetwork.token.TokenRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSBundle

/**
 * iOS actual: Ktor-клиент с движком Darwin (URLSession).
 *
 * Base URL передаётся явно из Koin. iOS-приложение регистрирует его как
 * `single<String>(named("anchorBaseUrl")) { NSBundle.mainBundle.baseUrl }`,
 * где `baseUrl` читается из `Info.plist` (ключ BASE_URL из Config.xcconfig).
 *
 * Если параметр не был зарегистрирован в Koin, при инициализации передаётся
 * значение из NSBundle напрямую как fallback.
 *
 * @param tokenRepository Репозиторий JWT-токенов для Auth-плагина.
 * @param baseUrl         Базовый URL API.
 * @return Сконфигурированный [HttpClient].
 */
actual fun createAnchorHttpClient(tokenRepository: TokenRepository, baseUrl: String): HttpClient =
    HttpClient(Darwin) {
        applyAnchorConfig(tokenRepository, baseUrl)
    }

/**
 * Читает BASE_URL из Info.plist (заполняется из Config.xcconfig при сборке Xcode).
 * Используется при регистрации `anchorBaseUrl` в Koin из iOS-приложения.
 */
internal val iosBaseUrl: String
    get() = NSBundle.mainBundle.infoDictionary
        ?.get("BASE_URL") as? String
        ?: error("BASE_URL не задан в Info.plist. Проверьте Config.xcconfig.")
