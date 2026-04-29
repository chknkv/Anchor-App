package com.chknkv.feature.addiction.domain.converter

import com.chknkv.feature.addiction.models.data.AddictionCreateRequest
import com.chknkv.feature.addiction.models.domain.create.AddictionCreate

/**
 * Конвертирует доменный запрос создания привычки в тело HTTP-запроса.
 */
internal fun AddictionCreate.toRequest(): AddictionCreateRequest = AddictionCreateRequest(
    name = name,
    description = description,
    iconKey = iconKey,
    gradientKey = gradientKey,
    category = category.toApiKey(),
)
