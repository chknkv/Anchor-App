package com.chknkv.feature.addiction.domain.converter

import com.chknkv.feature.addiction.domain.converter.base.toApiGradientKey
import com.chknkv.feature.addiction.domain.converter.base.toApiIconKey
import com.chknkv.feature.addiction.domain.converter.base.toApiKey
import com.chknkv.feature.addiction.models.data.AddictionUpdateRequest
import com.chknkv.feature.addiction.models.domain.AddictionUpdate

/**
 * Конвертирует domain-model [AddictionUpdate] в data-model [AddictionUpdateRequest].
 */
internal fun AddictionUpdate.toRequest(): AddictionUpdateRequest = AddictionUpdateRequest(
    name = name,
    description = description,
    iconKey = iconKey.toApiIconKey(),
    gradientKey = gradientKey.toApiGradientKey(),
    categoryKey = categoryKey.toApiKey(),
)
