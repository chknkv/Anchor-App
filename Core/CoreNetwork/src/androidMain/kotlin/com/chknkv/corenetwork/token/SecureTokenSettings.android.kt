package com.chknkv.corenetwork.token

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.chknkv.coreutils.appContext
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

private const val ENCRYPTED_PREFS_FILE = "anchor_secure_tokens"

/**
 * Android actual: возвращает [Settings] на основе [EncryptedSharedPreferences].
 *
 * Ключевой материал управляется Android Keystore через [MasterKey] (AES256-GCM).
 * Требует, чтобы [appContext] был инициализирован в `Application.onCreate()`.
 *
 * @return Защищённое хранилище с AES256-SIV (ключи) и AES256-GCM (значения).
 */
actual fun createSecureTokenSettings(): Settings {
    val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val encryptedPrefs = EncryptedSharedPreferences.create(
        appContext,
        ENCRYPTED_PREFS_FILE,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    return SharedPreferencesSettings(encryptedPrefs)
}
