package com.chknkv.corepasscode.models.domain

/**
 * Платформозависимый контекст для биометрической аутентификации.
 * 
 * Данный класс-маркер позволяет передавать необходимые платформенные зависимости 
 * (например, FragmentActivity на Android) в доменный слой, сохраняя common-интерфейс.
 */
expect class BiometricContext
