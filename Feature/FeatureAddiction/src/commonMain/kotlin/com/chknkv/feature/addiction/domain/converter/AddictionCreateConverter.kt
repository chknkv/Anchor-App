package com.chknkv.feature.addiction.domain.converter

import com.chknkv.feature.addiction.domain.converter.base.toApiGradientKey
import com.chknkv.feature.addiction.domain.converter.base.toApiIconKey
import com.chknkv.feature.addiction.domain.converter.base.toApiKey
import com.chknkv.feature.addiction.models.data.AddictionCreateRequest
import com.chknkv.feature.addiction.models.domain.AddictionCreate

/**
 * Конвертирует domain-model [AddictionCreate] в data-model [AddictionCreateRequest].
 */
internal fun AddictionCreate.toRequest(): AddictionCreateRequest = AddictionCreateRequest(
    name = name,
    description = description,
    iconKey = iconKey.toApiIconKey(),
    gradientKey = gradientKey.toApiGradientKey(),
    categoryKey = categoryKey.toApiKey(),
)
