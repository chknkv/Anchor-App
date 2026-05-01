package com.chknkv.feature.addiction.domain.converter.base

import com.chknkv.feature.addiction.models.data.base.AddictionGradientKey
import com.chknkv.feature.addiction.models.domain.base.AddictionGradient

/**
 * Конвертирует domain-enum [AddictionGradient] в data-enum [AddictionGradientKey].
 */
internal fun AddictionGradient.toApiGradientKey(): AddictionGradientKey = when (this) {
    AddictionGradient.Gray        -> AddictionGradientKey.GRAY
    AddictionGradient.Green       -> AddictionGradientKey.GREEN
    AddictionGradient.Blue        -> AddictionGradientKey.BLUE
    AddictionGradient.Indigo      -> AddictionGradientKey.INDIGO
    AddictionGradient.Purple      -> AddictionGradientKey.PURPLE
    AddictionGradient.Pink        -> AddictionGradientKey.PINK
    AddictionGradient.Red         -> AddictionGradientKey.RED
    AddictionGradient.Orange      -> AddictionGradientKey.ORANGE
    AddictionGradient.DarkOrange  -> AddictionGradientKey.DARK_ORANGE
    AddictionGradient.DarkRed     -> AddictionGradientKey.DARK_RED
    AddictionGradient.Black       -> AddictionGradientKey.BLACK
}