package com.chknkv.feature.addiction.domain.converter

import com.chknkv.feature.addiction.models.data.AddictionAllGroupResponse
import com.chknkv.feature.addiction.models.domain.UserAddictionGroup

/**
 * Конвертирует сгруппированный ответ сервера в доменную модель группы привычек пользователя.
 */
internal fun AddictionAllGroupResponse.toDomain(): UserAddictionGroup = UserAddictionGroup(
    category = categoryKey.toAddictionCategory(),
    addictions = addictions.map { it.toDomain() },
)
