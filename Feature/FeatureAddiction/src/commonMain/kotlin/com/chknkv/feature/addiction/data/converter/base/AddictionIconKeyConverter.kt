package com.chknkv.feature.addiction.data.converter.base

import com.chknkv.feature.addiction.models.data.base.AddictionIconKey
import com.chknkv.feature.addiction.models.domain.base.AddictionIcon

/**
 * Конвертирует data-enum [AddictionIconKey] в domain-enum [AddictionIcon].
 */
internal fun AddictionIconKey.toDomain(): AddictionIcon = when (this) {
    AddictionIconKey.LIFESTYLE     -> AddictionIcon.Lifestyle
    AddictionIconKey.HEALTH        -> AddictionIcon.Health
    AddictionIconKey.SPORT         -> AddictionIcon.Sport
    AddictionIconKey.PRODUCTIVITY  -> AddictionIcon.Productivity
    AddictionIconKey.FINANCE       -> AddictionIcon.Finance
    AddictionIconKey.RELATIONSHIPS -> AddictionIcon.Relationships
}