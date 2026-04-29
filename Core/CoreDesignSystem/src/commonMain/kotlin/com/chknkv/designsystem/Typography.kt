package com.chknkv.designsystem

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * Internal helper to reduce boilerplate for typography components.
 *
 * @param text The text to be displayed.
 * @param fontSize The size of glyphs.
 * @param lineHeight The height of a line of text.
 * @param fontWeight The thickness of the glyphs.
 * @param letterSpacing The amount of space to add between each letter.
 * @param modifier The modifier to be applied to the text.
 * @param color The color of the text.
 * @param isSecondary Whether to use the secondary text color from the theme.
 * @param textAlign The alignment of the text.
 * @param maxLines The maximum number of lines for the text to span.
 * @param overflow How visual overflow should be handled.
 */
@Composable
private fun TypographyText(
    text: String,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    fontWeight: FontWeight,
    letterSpacing: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    val textColor = when {
        color != Color.Unspecified -> color
        isSecondary -> Tokens.TextSecondary.getThemedColor()
        else -> Tokens.TextPrimary.getThemedColor()
    }

    Text(
        text = text,
        modifier = modifier,
        color = textColor,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        style = LocalTextStyle.current.copy(
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontWeight = fontWeight,
            fontFamily = FontFamily.Default,
            letterSpacing = letterSpacing
        ),
        onTextLayout = onTextLayout
    )
}

/**
 * Заголовок 1-го уровня. Самый крупный шрифт для наиболее важных заголовков.
 */
@Composable
fun Title1(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) = TypographyText(
    text = text,
    fontSize = 34.sp,
    lineHeight = 32.sp,
    fontWeight = FontWeight.Black,
    letterSpacing = (-0.5).sp,
    modifier = modifier,
    color = color,
    isSecondary = isSecondary,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    onTextLayout = onTextLayout
)

/**
 * Заголовок 2-го уровня. Используется для основных разделов.
 */
@Composable
fun Title2(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) = TypographyText(
    text = text,
    fontSize = 28.sp,
    lineHeight = 28.sp,
    fontWeight = FontWeight.Black,
    letterSpacing = (-0.5).sp,
    modifier = modifier,
    color = color,
    isSecondary = isSecondary,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    onTextLayout = onTextLayout
)

/**
 * Заголовок 3-го уровня. Используется для подразделов.
 */
@Composable
fun Title3(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) = TypographyText(
    text = text,
    fontSize = 22.sp,
    lineHeight = 24.sp,
    fontWeight = FontWeight.Black,
    letterSpacing = 0.sp,
    modifier = modifier,
    color = color,
    isSecondary = isSecondary,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    onTextLayout = onTextLayout
)

/**
 * Акцентный заголовок. Используется для выделения важных меток или заголовков внутри компонентов.
 */
@Composable
fun Headline(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) = TypographyText(
    text = text,
    fontSize = 18.sp,
    lineHeight = 23.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = (-0.50).sp,
    modifier = modifier,
    color = color,
    isSecondary = isSecondary,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    onTextLayout = onTextLayout
)

/**
 * Subheadline text style, used for secondary labels or supporting text.
 */
@Composable
fun Subheadline(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) = TypographyText(
    text = text,
    fontSize = 16.sp,
    lineHeight = 19.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = (-0.35).sp,
    modifier = modifier,
    color = color,
    isSecondary = isSecondary,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    onTextLayout = onTextLayout
)

/**
 * Стандартный текст (Body). Используется для основного контента.
 */
@Composable
fun Body(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) = TypographyText(
    text = text,
    fontSize = 18.sp,
    lineHeight = 23.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = (-0.50).sp,
    modifier = modifier,
    color = color,
    isSecondary = isSecondary,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    onTextLayout = onTextLayout
)

/**
 * Callout text style, used for highlighting specific information or instructions.
 */
@Composable
fun Callout(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) = TypographyText(
    text = text,
    fontSize = 16.sp,
    lineHeight = 21.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = (-0.40).sp,
    modifier = modifier,
    color = color,
    isSecondary = isSecondary,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    onTextLayout = onTextLayout
)

/**
 * Сноска. Используется для дополнительной информации или мелких подписей.
 */
@Composable
fun Footnote(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) = TypographyText(
    text = text,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = (-0.20).sp,
    modifier = modifier,
    color = color,
    isSecondary = isSecondary,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    onTextLayout = onTextLayout
)

/**
 * Smallest primary caption text style.
 */
@Composable
fun Caption1(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) = TypographyText(
    text = text,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = (-0.10).sp,
    modifier = modifier,
    color = color,
    isSecondary = isSecondary,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    onTextLayout = onTextLayout
)

/**
 * Smallest secondary caption text style, used for very dense information.
 */
@Composable
fun Caption2(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    isSecondary: Boolean = false,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) = TypographyText(
    text = text,
    fontSize = 11.sp,
    lineHeight = 13.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = (-0.05).sp,
    modifier = modifier,
    color = color,
    isSecondary = isSecondary,
    textAlign = textAlign,
    maxLines = maxLines,
    overflow = overflow,
    onTextLayout = onTextLayout
)
