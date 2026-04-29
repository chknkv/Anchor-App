package com.chknkv.coreutils

import platform.Foundation.NSBundle

/**
 * Реализация получения версии приложения для iOS.
 * Читает "CFBundleShortVersionString" из основного [NSBundle].
 */
actual fun getAppVersion(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
        ?: "unknown"
}