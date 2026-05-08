package com.chknkv.coreutils

import android.content.Intent
import androidx.core.net.toUri

/**
 * Реализация [openUrl] для Android.
 *
 * @param url Строка URL для открытия.
 */
actual fun openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}
