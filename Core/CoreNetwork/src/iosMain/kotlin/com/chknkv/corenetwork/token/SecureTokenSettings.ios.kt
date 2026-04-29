package com.chknkv.corenetwork.token

import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings

private const val KEYCHAIN_SERVICE = "com.chknkv.anchor.tokens"

/**
 * iOS actual: возвращает [Settings] на основе iOS Keychain.
 *
 * Элементы хранятся с атрибутом `kSecAttrAccessibleWhenUnlockedThisDeviceOnly`:
 * недоступны при заблокированном устройстве и не синхронизируются в iCloud.
 *
 * @return [KeychainSettings] с изолированным service-именем [KEYCHAIN_SERVICE].
 */
actual fun createSecureTokenSettings(): Settings =
    KeychainSettings(service = KEYCHAIN_SERVICE)
