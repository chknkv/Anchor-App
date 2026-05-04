package com.chknkv.feature.welcome.presentation

/**
 * Регулярное выражение для проверки валидности email-адреса.
 * Покрывает стандартный формат: local-part@domain.tld
 */
private val EMAIL_REGEX = Regex(
    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
)

/**
 * Проверяет, является ли строка валидным email-адресом.
 *
 * @param email Строка для проверки.
 * @return `true`, если строка соответствует формату email.
 */
internal fun isEmailValid(email: String): Boolean {
    return email.isNotBlank() && EMAIL_REGEX.matches(email.trim())
}
