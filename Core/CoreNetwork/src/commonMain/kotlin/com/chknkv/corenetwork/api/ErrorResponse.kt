package com.chknkv.corenetwork.api

import kotlinx.serialization.Serializable

/**
 * Тело ошибки, которое сервер возвращает при HTTP 4xx/5xx.
 *
 * @param code Машиночитаемый код ошибки (например, `"INVALID_OTP"`).
 * @param message Человекочитаемое описание ошибки (не отображается пользователю напрямую).
 */
@Serializable
data class ErrorResponse(val code: String, val message: String)
