package com.chknkv.corenetwork.token

import com.russhwolf.settings.Settings

/**
 * Создаёт platform-secure экземпляр [Settings] для хранения JWT-токенов.
 *
 * Android actual → EncryptedSharedPreferences (androidx.security-crypto, AES256-GCM).
 * iOS actual     → Keychain (multiplatform-settings KeychainSettings,
 *                  `kSecAttrAccessibleWhenUnlockedThisDeviceOnly`).
 *
 * @return Защищённое хранилище, готовое к использованию в [TokenRepositoryImpl].
 */
expect fun createSecureTokenSettings(): Settings
