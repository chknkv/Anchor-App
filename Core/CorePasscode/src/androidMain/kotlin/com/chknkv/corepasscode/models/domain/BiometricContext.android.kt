package com.chknkv.corepasscode.models.domain

import androidx.fragment.app.FragmentActivity

/**
 * Android-реализация: оборачивает [FragmentActivity], необходимый для
 * [androidx.biometric.BiometricPrompt].
 *
 * @property activity Активность, показывающая системный диалог биометрии.
 */
actual class BiometricContext(val activity: FragmentActivity)
