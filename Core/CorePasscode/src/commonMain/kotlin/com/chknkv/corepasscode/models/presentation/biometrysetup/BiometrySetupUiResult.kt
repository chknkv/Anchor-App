package com.chknkv.corepasscode.models.presentation.biometrysetup

import com.chknkv.corepasscode.models.domain.BiometricType

/**
 * Состояние экрана подключения биометрии.
 *
 * @param biometricType Тип биометрии устройства.
 * @param isLoading Идёт ли запрос prompt.
 */
data class BiometrySetupUiResult(
    val biometricType: BiometricType = BiometricType.NONE,
    val isLoading: Boolean = false,
)
