package com.chknkv.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.chknkv.designsystem.Black0
import com.chknkv.designsystem.Black4
import com.chknkv.designsystem.Black7
import com.chknkv.designsystem.Black8
import com.chknkv.designsystem.Blue0
import com.chknkv.designsystem.Blue3
import com.chknkv.designsystem.Gray0
import com.chknkv.designsystem.Gray1
import com.chknkv.designsystem.Gray2
import com.chknkv.designsystem.Gray6
import com.chknkv.designsystem.Gray7
import com.chknkv.designsystem.Gray8
import com.chknkv.designsystem.Green0
import com.chknkv.designsystem.Green3
import com.chknkv.designsystem.Indigo0
import com.chknkv.designsystem.Indigo1
import com.chknkv.designsystem.Orange0
import com.chknkv.designsystem.Orange2
import com.chknkv.designsystem.Orange3
import com.chknkv.designsystem.Orange4
import com.chknkv.designsystem.Pink0
import com.chknkv.designsystem.Pink1
import com.chknkv.designsystem.Purple0
import com.chknkv.designsystem.Purple1
import com.chknkv.designsystem.Red0
import com.chknkv.designsystem.Red3
import com.chknkv.designsystem.Red6
import com.chknkv.designsystem.White0
import com.chknkv.designsystem.White1
import com.chknkv.designsystem.White3
import com.chknkv.designsystem.White4

/**
 * Provides the mapping of semantic [Tokens] to specific colors for the Light theme.
 */
fun lightThemeTokensMap(): Map<Tokens, Color> = mapOf(
    Tokens.Background to White1,
    Tokens.BackgroundSheet to White0,
    Tokens.Module to White0,
    Tokens.TextPrimary to Black0,
    Tokens.IconPrimary to Black0,
    Tokens.TextSecondary to Gray1,
    Tokens.IconSecondary to Gray1,
    Tokens.Separator to Gray6,
    Tokens.Action to Blue0,
    Tokens.Warning to Red0,
    Tokens.Switcher to Green0,
    Tokens.Shimmer to White3,
    Tokens.ShimmerHighlight to White4,
    Tokens.ProgressTrack to Gray7,
    Tokens.HudBackground to Black7,
    Tokens.ChipBackgroundSelected to Blue0,
    Tokens.ChipBackgroundUnselected to Black8,
    Tokens.ChipContentSelected to White0,
    Tokens.ChipContentUnselected to Black0,
    Tokens.PasscodeKeyGlass to Gray8,
)

/**
 * Provides the mapping of named [TokensColor] to specific colors for the Light theme.
 */
fun lightThemeColorsMap(): Map<TokensColor, Color> = mapOf(
    TokensColor.Black to Black0,
    TokensColor.Gray to Gray2,
    TokensColor.Blue to Blue0,
    TokensColor.Indigo to Indigo0,
    TokensColor.Purple to Purple0,
    TokensColor.Green to Green0,
    TokensColor.Orange to Orange0,
    TokensColor.DarkOrange to Orange4,
    TokensColor.Red to Red0,
    TokensColor.DarkRed to Red6,
    TokensColor.Pink to Pink0,
)

/**
 * Provides the mapping of [TokensGradient] to specific gradients for the Light theme.
 */
fun lightThemeGradientsMap(): Map<TokensGradient, Brush> = mapOf(
    TokensGradient.Black to Brush.verticalGradient(listOf(Black4, Black0)),
    TokensGradient.Gray to Brush.verticalGradient(listOf(Gray0, Gray2)),
    TokensGradient.Blue to Brush.verticalGradient(listOf(Blue3, Blue0)),
    TokensGradient.Indigo to Brush.verticalGradient(listOf(Indigo0, Indigo1)),
    TokensGradient.Purple to Brush.verticalGradient(listOf(Purple1, Purple0)),
    TokensGradient.Green to Brush.verticalGradient(listOf(Green3, Green0)),
    TokensGradient.Orange to Brush.verticalGradient(listOf(Orange2, Orange0)),
    TokensGradient.DarkOrange to Brush.verticalGradient(listOf(Orange3, Orange4)),
    TokensGradient.Red to Brush.verticalGradient(listOf(Red3, Red0)),
    TokensGradient.DarkRed to Brush.verticalGradient(listOf(Red0, Red6)),
    TokensGradient.Pink to Brush.verticalGradient(listOf(Pink1, Pink0)),
)
