package com.chknkv.anchor

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import com.chknkv.anchor.di.sharedModule
import com.chknkv.anchor.root.AnchorApp
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.UIKit.UIViewController

/**
 * Создаёт основной [UIViewController] приложения для платформы iOS.
 *
 * Инициализирует Koin с app-level зависимостями, аналогично `AnchorApplication.onCreate()` на Android:
 * - `anchorBaseUrl` — базовый URL API, читается через [resolveBaseUrl].
 *
 * После регистрации app-модуля подключает [sharedModule] с feature- и core-зависимостями.
 */
@Suppress("FunctionName")
fun AnchorMainViewController(): UIViewController = ComposeUIViewController(configure = {
    onFocusBehavior = OnFocusBehavior.DoNothing
    startKoin {
        modules(
            module { single<String>(named("anchorBaseUrl")) { resolveBaseUrl() } },
            sharedModule,
        )
    }
}) {
    AnchorApp()
}


/**
 * Читает базовый URL API из `Info.plist` (ключ `BASE_URL`).
 *
 * Значение заполняется на этапе сборки Xcode из `Config.xcconfig`.
 * Если ключ отсутствует — бросает [IllegalStateException] с инструкцией по исправлению.
 *
 * @return Базовый URL API.
 */
private fun resolveBaseUrl(): String =
    NSBundle.mainBundle.infoDictionary
        ?.get("BASE_URL") as? String
        ?: error(
            "BASE_URL не задан в Info.plist. " +
                    "Добавьте ключ BASE_URL в Config.xcconfig и пропишите его в Info.plist."
        )