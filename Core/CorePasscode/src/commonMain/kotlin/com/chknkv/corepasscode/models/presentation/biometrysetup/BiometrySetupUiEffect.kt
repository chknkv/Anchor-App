package com.chknkv.corepasscode.models.presentation.biometrysetup

/**
 * Одноразовые события экрана подключения биометрии.
 */
sealed interface BiometrySetupUiEvent {

    /** Биометрия успешно подключена — флоу завершается. */
    data object BiometricEnabled : BiometrySetupUiEvent

    /**
     * Подключение биометрии не удалось — пользователь остаётся на экране.
     *
     * @param reason Причина сбоя для логирования.
     */
    data class BiometricFailed(val reason: String) : BiometrySetupUiEvent

    /** Экран завершён (биометрия подключена или пропущена). */
    data object SetupFinished : BiometrySetupUiEvent
}
