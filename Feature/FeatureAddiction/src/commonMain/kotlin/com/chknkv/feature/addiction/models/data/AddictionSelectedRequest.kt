package com.chknkv.feature.addiction.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Тело запроса на сохранение выбранных привычек при онбординге.
 *
 * Используется для эндпоинта `POST /user/addictions/select`.
 *
 * @param ids Список идентификаторов выбранных привычек.
 */
@Serializable
internal data class AddictionSelectedRequest(
    @SerialName("ids") val ids: List<Int>,
)
