package com.chknkv.coreutils

/**
 * Возвращает строковое представление версии приложения (например, "1.0.0").
 * Реализация зависит от платформы: на Android читается из PackageManager, 
 * на iOS — из Info.plist (CFBundleShortVersionString).
 */
expect fun getAppVersion(): String
