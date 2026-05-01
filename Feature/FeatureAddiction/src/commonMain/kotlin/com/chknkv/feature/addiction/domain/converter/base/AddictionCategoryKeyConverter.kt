package com.chknkv.feature.addiction.domain.converter.base

import com.chknkv.feature.addiction.models.data.base.AddictionCategoryKey
import com.chknkv.feature.addiction.models.domain.base.AddictionCategory

/**
 * Конвертирует domain-enum [AddictionCategory] в data-enum [AddictionCategoryKey].
 */
internal fun AddictionCategory.toApiKey(): AddictionCategoryKey = when (this) {
    AddictionCategory.LIFESTYLE     -> AddictionCategoryKey.LIFESTYLE
    AddictionCategory.HEALTH        -> AddictionCategoryKey.HEALTH
    AddictionCategory.SPORT         -> AddictionCategoryKey.SPORT
    AddictionCategory.PRODUCTIVITY  -> AddictionCategoryKey.PRODUCTIVITY
    AddictionCategory.FINANCE       -> AddictionCategoryKey.FINANCE
    AddictionCategory.RELATIONSHIPS -> AddictionCategoryKey.RELATIONSHIPS
    AddictionCategory.OTHER         -> AddictionCategoryKey.OTHER
}