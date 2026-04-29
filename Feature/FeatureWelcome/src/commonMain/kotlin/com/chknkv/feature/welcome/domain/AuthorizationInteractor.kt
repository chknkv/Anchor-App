package com.chknkv.feature.welcome.domain

import kotlinx.coroutines.delay

/**
 * Интерактор для бизнес-логики экрана авторизации.
 * Отвечает за валидацию email, выполнение OTP-запроса и обработку Terms of Service.
 */
interface AuthorizationInteractor {

    /**
     * Обрабатывает нажатие на кнопку "Get OTP".
     * Выполняет сетевой запрос для получения OTP-кода.
     *
     * @param email почта пользователя
     * @return true - если почта успешно отправлена, false - произошла проблема
     */
    suspend fun handleGetOtp(email: String): Boolean

    /**
     * Проверяет введенный OTP-код.
     *
     * @param otp введенный код
     * @return true - если код верный, false - иначе
     */
    suspend fun checkOtp(otp: String): Boolean

    /**
     * Обрабатывает нажатие на ссылку Terms of Service.
     */
    fun handleTermsClicked()

    /**
     * Повторно отправляет OTP-код.
     */
    suspend fun resendOtp(email: String)
}

/**
 * Реализация [AuthorizationInteractor].
 */
class AuthorizationInteractorImpl : AuthorizationInteractor {

    override suspend fun handleGetOtp(email: String): Boolean {
        delay(3000L)
        return email == "chekunkov.test@yandex.ru"
    }

    override suspend fun checkOtp(otp: String): Boolean {
        delay(3000L)
        return otp == "55087"
    }

    override fun handleTermsClicked() {
        // TODO: Открыть экран или URL с условиями использования.
    }

    override suspend fun resendOtp(email: String) {
        // TODO: Повторная отправка кода
    }
}
