package com.chknkv.corenetwork

import kotlinx.serialization.Serializable

/**
 * Универсальный конверт сетевого ответа Anchor API.
 *
 * Все эндпоинты возвращают JSON следующей формы:
 * ```json
 * { "success": true, "body": { ... } }
 * { "success": false, "message": "Описание ошибки" }
 * ```
 *
 * @param T Тип полезной нагрузки, вложенной в поле [body].
 * @param success Признак успешного выполнения запроса на стороне сервера.
 * @param body Полезная нагрузка ответа; присутствует только при [success] == `true`
 *   и для эндпоинтов, возвращающих данные. Равен `null` для void-эндпоинтов или при ошибке.
 * @param message Человекочитаемое сообщение об ошибке от сервера;
 *   присутствует только при [success] == `false`.
 * @param timeout Строковый признак таймаута на стороне сервера;
 *   присутствует только при [success] == `false` и причине таймаута.
 */
@Serializable
open class NetworkEntity<T>(
    val success: Boolean = true,
    val body: T? = null,
    val message: String? = null,
    val timeout: String? = null,
)

/**
 * Извлекает тело ответа из конверта [NetworkEntity].
 *
 * @throws IllegalStateException Если [NetworkEntity.success] == `false` (возвращает [NetworkEntity.message] или [NetworkEntity.timeout])
 *   или если [NetworkEntity.body] равен `null` при успешном ответе.
 * @return Ненулевое тело ответа типа [T].
 */
fun <T> NetworkEntity<T>.requireBody(): T {
    if (!success) error(message ?: timeout ?: "Unknown server error")
    return body ?: error("NetworkEntity: success=true but body is null")
}

/**
 * Проверяет успешность выполнения void-запроса (эндпоинт не возвращает тела).
 *
 * Используется вместо `if (!entity.success) error(...)` для эндпоинтов,
 * возвращающих [NetworkEntity] без полезной нагрузки.
 *
 * @throws IllegalStateException Если [NetworkEntity.success] == `false`;
 *   сообщение берётся из [NetworkEntity.message] или [NetworkEntity.timeout].
 */
fun NetworkEntity<*>.isSuccessfulExecute() {
    if (!success) error(message ?: timeout ?: "Unknown server error")
}
