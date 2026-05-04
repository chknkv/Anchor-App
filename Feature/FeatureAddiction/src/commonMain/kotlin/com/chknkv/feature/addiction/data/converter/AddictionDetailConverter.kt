package com.chknkv.feature.addiction.data.converter

import com.chknkv.feature.addiction.data.converter.base.toDomain
import com.chknkv.feature.addiction.models.data.AddictionDetailsBody
import com.chknkv.feature.addiction.models.domain.AddictionDetails

/**
 * Конвертирует data-model [AddictionDetailsBody] в domain-model [AddictionDetails].
 */
internal fun AddictionDetailsBody.toDomain(): AddictionDetails = AddictionDetails(
    id = id,
    name = name,
    category = categoryKey.toDomain(),
    iconKey = iconKey.toDomain(),
    gradient = gradientKey.toDomain(),
    controlDays = controlDays,
    description = description.orEmpty(),
    completedDates = completedDates.toSet(),
    canIncrementToday = canIncrementToday,
    nextIncrementAvailableInSeconds = nextIncrementAvailableInSeconds
        ?.coerceAtMost(Int.MAX_VALUE.toLong())?.toInt() ?: 0,
)
