package com.chknkv.feature.addiction.domain.converter

import com.chknkv.feature.addiction.models.domain.AddictionCategory

/**
 * Конвертирует строковый ключ категории из API в доменный enum [AddictionCategory].
 *
 * Неизвестные ключи маппируются в [AddictionCategory.OTHER].
 */
internal fun String.toAddictionCategory(): AddictionCategory = when (lowercase()) {
    "lifestyle"     -> AddictionCategory.LIFESTYLE
    "health"        -> AddictionCategory.HEALTH
    "sport"         -> AddictionCategory.SPORT
    "productivity"  -> AddictionCategory.PRODUCTIVITY
    "finance"       -> AddictionCategory.FINANCE
    "relationships" -> AddictionCategory.RELATIONSHIPS
    else            -> AddictionCategory.OTHER
}

/**
 * Конвертирует доменный enum [AddictionCategory] в строковый ключ для API.
 */
internal fun AddictionCategory.toApiKey(): String = when (this) {
    AddictionCategory.LIFESTYLE     -> "lifestyle"
    AddictionCategory.HEALTH        -> "health"
    AddictionCategory.SPORT         -> "sport"
    AddictionCategory.PRODUCTIVITY  -> "productivity"
    AddictionCategory.FINANCE       -> "finance"
    AddictionCategory.RELATIONSHIPS -> "relationships"
    AddictionCategory.OTHER         -> "other"
}
