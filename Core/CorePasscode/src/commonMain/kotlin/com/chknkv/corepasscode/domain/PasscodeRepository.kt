package com.chknkv.corepasscode.domain

import com.russhwolf.settings.Settings

/**
 * Репозиторий для управления хранением данных безопасности (пароль, биометрия).
 * 
 * В целях безопасности оперирует только хэш-значениями пароля (SHA-256). 
 * Сырой пароль никогда не сохраняется в памяти устройства.
 */
interface PasscodeRepository {

    /**
     * Сохраняет хэш пароля.
     * @param hash Строковое представление хэша. Если null — пароль удаляется.
     */
    fun savePasscodeHash(hash: String?)

    /**
     * Возвращает сохраненный хэш пароля.
     * @return Хэш или null, если пароль не установлен.
     */
    fun getPasscodeHash(): String?

    /**
     * Определяет, разрешил ли пользователь использование биометрии для входа.
     */
    var isBiometricEnabled: Boolean

    /**
     * Проверяет, установлен ли пароль на данный момент.
     */
    fun hasPasscode(): Boolean

    /**
     * Сбрасывает все настройки безопасности (удаляет пароль и отключает биометрию).
     */
    fun clearPasscode()
}

/**
 * Реализация [PasscodeRepository] на базе [Settings].
 * 
 * @param settings Экземпляр настроек для персистентного хранения.
 */
class PasscodeRepositoryImpl(
    private val settings: Settings
) : PasscodeRepository {

    override fun savePasscodeHash(hash: String?) {
        if (hash == null) settings.remove(KEY_PASSCODE_HASH)
        else settings.putString(KEY_PASSCODE_HASH, hash)
    }

    override fun getPasscodeHash(): String? =
        settings.getStringOrNull(KEY_PASSCODE_HASH)

    override var isBiometricEnabled: Boolean
        get() = settings.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = settings.putBoolean(KEY_BIOMETRIC_ENABLED, value)

    override fun hasPasscode(): Boolean = getPasscodeHash() != null

    override fun clearPasscode() {
        settings.remove(KEY_PASSCODE_HASH)
        settings.remove(KEY_BIOMETRIC_ENABLED)
    }

    private companion object {
        const val KEY_PASSCODE_HASH = "anchor_app_passcode_hash"
        const val KEY_BIOMETRIC_ENABLED = "anchor_app_passcode_biometric_enabled"
    }
}
