package com.chknkv.anchor.android

import android.app.Application
import android.content.Context
import com.chknkv.anchor.di.initKoin
import com.chknkv.coreutils.appContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Базовый класс [Application] для Android-части приложения Anchor.
 * Отвечает за инициализацию Dependency Injection (Koin) и установку глобального контекста.
 */
class AnchorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        initKoin(
            appModule = module {
                single<Context> { this@AnchorApplication }
                single<String>(named("anchorBaseUrl")) { BuildConfig.BASE_URL }
            }
        )
    }
}
