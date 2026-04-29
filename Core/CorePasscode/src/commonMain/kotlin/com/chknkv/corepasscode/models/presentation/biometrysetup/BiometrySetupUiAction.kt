package com.chknkv.corepasscode.models.presentation.biometrysetup

/**
 * Действия пользователя на экране подключения биометрии.
 */
sealed interface BiometrySetupUiAction {

    /** Инициализация экрана. */
    data object Init : BiometrySetupUiAction

    /** Пользователь нажал "Включить" — запуск системного prompt. */
    data object Enable : BiometrySetupUiAction

    /** Пользователь нажал "Пропустить". */
    data object Skip : BiometrySetupUiAction
}
