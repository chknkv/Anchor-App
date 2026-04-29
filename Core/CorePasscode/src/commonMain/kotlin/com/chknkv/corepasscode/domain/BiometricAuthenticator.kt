package com.chknkv.corepasscode.domain

import com.chknkv.corepasscode.models.domain.BiometricContext
import com.chknkv.corepasscode.models.domain.BiometricResult
import com.chknkv.corepasscode.models.domain.BiometricType

/**
 * Интерфейс доступа к системным средствам биометрической аутентификации.
 * 
 * Предоставляет методы для проверки доступности биометрии и запуска процесса сканирования.
 */
expect class BiometricAuthenticator {

    /**
     * Возвращает тип биометрии, доступной на текущем устройстве.
     * @return [BiometricType.NONE] если биометрия недоступна.
     */
    fun availableType(): BiometricType

    /**
     * Проверяет, поддерживается ли и настроена ли биометрия в системе.
     */
    fun isAvailable(): Boolean

    /**
     * Запускает процесс биометрической аутентификации.
     * 
     * @param context Платформенный контекст (см. [BiometricContext]).
     * @param title Заголовок системного диалога.
     * @param subtitle Текст описания для чего требуется аутентификация.
     * @param cancelButtonText Текст кнопки отмены.
     * @return Результат аутентификации [BiometricResult].
     */
    suspend fun authenticate(
        context: BiometricContext,
        title: String,
        subtitle: String,
        cancelButtonText: String,
    ): BiometricResult
}
