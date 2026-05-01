package com.chknkv.feature.addiction.data.converter.base

import com.chknkv.feature.addiction.models.data.base.AddictionGradientKey
import com.chknkv.feature.addiction.models.domain.base.AddictionGradient

/**
 * Конвертирует data-enum [AddictionGradientKey] в domain-enum [AddictionGradient].
 */
internal fun AddictionGradientKey.toDomain(): AddictionGradient = when (this) {
    AddictionGradientKey.GRAY        -> AddictionGradient.Gray
    AddictionGradientKey.GREEN       -> AddictionGradient.Green
    AddictionGradientKey.BLUE        -> AddictionGradient.Blue
    AddictionGradientKey.INDIGO      -> AddictionGradient.Indigo
    AddictionGradientKey.PURPLE      -> AddictionGradient.Purple
    AddictionGradientKey.PINK        -> AddictionGradient.Pink
    AddictionGradientKey.RED         -> AddictionGradient.Red
    AddictionGradientKey.ORANGE      -> AddictionGradient.Orange
    AddictionGradientKey.DARK_ORANGE -> AddictionGradient.DarkOrange
    AddictionGradientKey.DARK_RED    -> AddictionGradient.DarkRed
    AddictionGradientKey.BLACK       -> AddictionGradient.Black
}
