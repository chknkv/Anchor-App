package com.chknkv.feature.addiction.domain.converter

import com.chknkv.feature.addiction.models.data.AddictionDetailResponse
import com.chknkv.feature.addiction.models.domain.UserAddiction

/**
 * Конвертирует полный ответ сервера по привычке пользователя в доменную модель.
 */
internal fun AddictionDetailResponse.toDomain(): UserAddiction = UserAddiction(
    id = id,
    name = name,
    category = category.toAddictionCategory(),
    iconKey = iconKey,
    gradient = gradient,
    controlDays = controlDays,
    description = description,
    completedDates = completedDates.toSet(),
    canIncrementToday = canIncrementToday,
    nextIncrementAvailableInSeconds = nextIncrementAvailableInSeconds,
)
