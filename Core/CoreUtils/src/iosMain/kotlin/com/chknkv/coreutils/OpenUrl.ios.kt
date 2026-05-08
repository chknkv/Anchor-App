package com.chknkv.coreutils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * Реализация [openUrl] для iOS.
 *
 * @param url Строка URL для открытия.
 */
actual fun openUrl(url: String) {
    NSURL.URLWithString(url)?.let { nsUrl ->
        UIApplication.sharedApplication.openURL(nsUrl, emptyMap<Any?, Any>(), null)
    }
}
