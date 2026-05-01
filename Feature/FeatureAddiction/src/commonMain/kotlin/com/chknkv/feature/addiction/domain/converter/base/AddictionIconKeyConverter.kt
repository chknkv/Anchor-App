package com.chknkv.feature.addiction.domain.converter.base

import com.chknkv.feature.addiction.models.data.base.AddictionIconKey
import com.chknkv.feature.addiction.models.domain.base.AddictionIcon

/**
 * Конвертирует domain-enum [AddictionIcon] в data-enum [AddictionIconKey].
 */
internal fun AddictionIcon.toApiIconKey(): AddictionIconKey = when (this) {
    AddictionIcon.Lifestyle     -> AddictionIconKey.LIFESTYLE
    AddictionIcon.Health        -> AddictionIconKey.HEALTH
    AddictionIcon.Sport         -> AddictionIconKey.SPORT
    AddictionIcon.Productivity  -> AddictionIconKey.PRODUCTIVITY
    AddictionIcon.Finance       -> AddictionIconKey.FINANCE
    AddictionIcon.Relationships -> AddictionIconKey.RELATIONSHIPS
}
