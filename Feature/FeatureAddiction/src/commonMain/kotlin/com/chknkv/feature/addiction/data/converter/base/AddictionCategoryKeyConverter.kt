package com.chknkv.feature.addiction.data.converter.base

import com.chknkv.feature.addiction.models.data.base.AddictionCategoryKey
import com.chknkv.feature.addiction.models.domain.base.AddictionCategory

/**
 * Конвертирует data-enum [AddictionCategoryKey] в domain-enum [AddictionCategory].
 */
internal fun AddictionCategoryKey.toDomain(): AddictionCategory = when (this) {
    AddictionCategoryKey.LIFESTYLE     -> AddictionCategory.LIFESTYLE
    AddictionCategoryKey.HEALTH        -> AddictionCategory.HEALTH
    AddictionCategoryKey.SPORT         -> AddictionCategory.SPORT
    AddictionCategoryKey.PRODUCTIVITY  -> AddictionCategory.PRODUCTIVITY
    AddictionCategoryKey.FINANCE       -> AddictionCategory.FINANCE
    AddictionCategoryKey.RELATIONSHIPS -> AddictionCategory.RELATIONSHIPS
    AddictionCategoryKey.OTHER         -> AddictionCategory.OTHER
}
