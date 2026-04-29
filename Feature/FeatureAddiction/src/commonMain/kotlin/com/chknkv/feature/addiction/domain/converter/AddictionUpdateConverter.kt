package com.chknkv.feature.addiction.domain.converter

import com.chknkv.feature.addiction.models.data.AddictionUpdateRequest
import com.chknkv.feature.addiction.models.domain.update.AddictionUpdate

/**
 * Конвертирует доменный запрос обновления привычки в тело HTTP-запроса.
 */
internal fun AddictionUpdate.toRequest(): AddictionUpdateRequest = AddictionUpdateRequest(
    name = name,
    description = description,
    iconKey = iconKey,
    gradientKey = gradientKey,
    category = category.toApiKey(),
)
