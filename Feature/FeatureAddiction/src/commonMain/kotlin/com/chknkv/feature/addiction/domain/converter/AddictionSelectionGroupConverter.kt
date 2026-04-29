package com.chknkv.feature.addiction.domain.converter

import com.chknkv.feature.addiction.models.data.AddictionSelectionGroupResponse
import com.chknkv.feature.addiction.models.domain.select.AddictionGroup

/**
 * Конвертирует группу привычек для онбординг-выбора из ответа сервера в доменную модель.
 */
internal fun AddictionSelectionGroupResponse.toDomain(): AddictionGroup = AddictionGroup(
    category = categoryKey.toAddictionCategory(),
    addictions = addictions.map { it.toDomain() },
)
