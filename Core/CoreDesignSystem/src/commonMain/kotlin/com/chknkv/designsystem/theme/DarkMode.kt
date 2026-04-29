package com.chknkv.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.chknkv.designsystem.Black0
import com.chknkv.designsystem.Black1
import com.chknkv.designsystem.Black2
import com.chknkv.designsystem.Black3
import com.chknkv.designsystem.Black5
import com.chknkv.designsystem.Black7
import com.chknkv.designsystem.Blue1
import com.chknkv.designsystem.Blue2
import com.chknkv.designsystem.Gray1
import com.chknkv.designsystem.Gray3
import com.chknkv.designsystem.Gray4
import com.chknkv.designsystem.Gray7
import com.chknkv.designsystem.Green1
import com.chknkv.designsystem.Green2
import com.chknkv.designsystem.Green4
import com.chknkv.designsystem.Indigo2
import com.chknkv.designsystem.Indigo3
import com.chknkv.designsystem.Orange1
import com.chknkv.designsystem.Orange3
import com.chknkv.designsystem.Orange5
import com.chknkv.designsystem.Pink2
import com.chknkv.designsystem.Pink3
import com.chknkv.designsystem.Purple2
import com.chknkv.designsystem.Purple3
import com.chknkv.designsystem.Red1
import com.chknkv.designsystem.Red4
import com.chknkv.designsystem.Red5
import com.chknkv.designsystem.Red7
import com.chknkv.designsystem.Black6
import com.chknkv.designsystem.Gray5
import com.chknkv.designsystem.White0
import com.chknkv.designsystem.White2

/**
 * Provides the mapping of semantic [Tokens] to specific colors for the Dark theme.
 */
fun darkThemeTokensMap(): Map<Tokens, Color> = mapOf(
    Tokens.Background to Black0,
    Tokens.BackgroundSheet to Black1,
    Tokens.Module to Black1,
    Tokens.TextPrimary to White0,
    Tokens.TextSecondary to White2,
    Tokens.IconPrimary to White0,
    Tokens.IconSecondary to White2,
    Tokens.Separator to Gray4,
    Tokens.Action to Blue1,
    Tokens.Warning to Red1,
    Tokens.Switcher to Green1,
    Tokens.Shimmer to Black2,
    Tokens.ShimmerHighlight to Black5,
    Tokens.ProgressTrack to Gray7,
    Tokens.HudBackground to Black7,
    Tokens.ChipBackgroundSelected to Blue1,
    Tokens.ChipBackgroundUnselected to Black6,
    Tokens.ChipContentSelected to White0,
    Tokens.ChipContentUnselected to White0,
    Tokens.PasscodeKeyGlass to Gray5,
)

/**
 * Provides the mapping of named [TokensColor] to specific colors for the Dark theme.
 */
fun darkThemeColorsMap(): Map<TokensColor, Color> = mapOf(
    TokensColor.Black to White0,
    TokensColor.Gray to Gray1,
    TokensColor.Blue to Blue1,
    TokensColor.Indigo to Indigo2,
    TokensColor.Purple to Purple2,
    TokensColor.Green to Green1,
    TokensColor.Orange to Orange1,
    TokensColor.DarkOrange to Orange1,
    TokensColor.Red to Red1,
    TokensColor.DarkRed to Red5,
    TokensColor.Pink to Pink2,
)

/**
 * Provides the mapping of [TokensGradient] to specific gradients for the Dark theme.
 */
fun darkThemeGradientsMap(): Map<TokensGradient, Brush> = mapOf(
    TokensGradient.Black to Brush.verticalGradient(listOf(Black3, Black2)),
    TokensGradient.Gray to Brush.verticalGradient(listOf(Gray1, Gray3)),
    TokensGradient.Blue to Brush.verticalGradient(listOf(Blue2, Blue1)),
    TokensGradient.Indigo to Brush.verticalGradient(listOf(Indigo2, Indigo3)),
    TokensGradient.Purple to Brush.verticalGradient(listOf(Purple2, Purple3)),
    TokensGradient.Green to Brush.verticalGradient(listOf(Green2, Green4)),
    TokensGradient.Orange to Brush.verticalGradient(listOf(Orange3, Orange1)),
    TokensGradient.DarkOrange to Brush.verticalGradient(listOf(Orange1, Orange5)),
    TokensGradient.Red to Brush.verticalGradient(listOf(Red4, Red1)),
    TokensGradient.DarkRed to Brush.verticalGradient(listOf(Red5, Red7)),
    TokensGradient.Pink to Brush.verticalGradient(listOf(Pink2, Pink3)),
)
