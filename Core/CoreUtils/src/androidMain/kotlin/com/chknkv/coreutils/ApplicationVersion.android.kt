package com.chknkv.coreutils

import android.content.Context
import android.content.pm.PackageManager

/**
 * Глобальный контекст приложения для доступа к PackageManager.
 * Должен быть инициализирован в Application.onCreate().
 */
lateinit var appContext: Context

/**
 * Реализация получения версии приложения для Android.
 * Читает versionName из манифеста через [PackageManager].
 */
actual fun getAppVersion(): String {
    return try {
        val pInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
        pInfo.versionName ?: "unknown"
    } catch (_: PackageManager.NameNotFoundException) {
        "unknown"
    }
}