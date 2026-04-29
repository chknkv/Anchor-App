package com.chknkv.corepasscode.models.domain

/**
 * Тип поддерживаемой биометрии на устройстве.
 */
enum class BiometricType {
    /** Распознавание лица (iOS Face ID). */
    FACE_ID,

    /** Скан лица (Android Face Unlock / Face Authentication). */
    FACE_ANDROID,

    /** Отпечаток пальца (Touch ID / Fingerprint). */
    TOUCH_ID,

    /** Биометрия недоступна или не настроена. */
    NONE
}
