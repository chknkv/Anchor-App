package com.chknkv.feature.assistant.presentation.compose

import anchor_app.feature.featureassistant.generated.resources.Res
import anchor_app.feature.featureassistant.generated.resources.assistance_alarm_subtitle
import anchor_app.feature.featureassistant.generated.resources.assistance_alarm_title
import anchor_app.feature.featureassistant.generated.resources.assistance_quote_title
import anchor_app.feature.featureassistant.generated.resources.ic_assistance_alarm
import anchor_app.feature.featureassistant.generated.resources.ic_assistance_quote
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Body
import com.chknkv.designsystem.cell.CellInfo
import com.chknkv.designsystem.sheet.Sheet
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.TokensGradient
import com.chknkv.designsystem.theme.getThemedColor
import com.chknkv.designsystem.theme.getThemedGradient
import com.chknkv.feature.assistant.models.presentation.AssistanceWidgetUiAction
import com.chknkv.feature.assistant.models.presentation.AssistanceWidgetUiResult
import org.jetbrains.compose.resources.stringResource

private const val PAGE_COUNT = 2
private const val PAGE_QUOTE = 0

private const val DISABLED_ALPHA = 0.5f

/**
 * Контент виджета помощи в состоянии [com.chknkv.feature.assistant.models.presentation.AssistanceWidgetUiState.Successful].
 *
 * Отображает горизонтальный пейджер с двумя карточками:
 * 1. «Цитата» — кликабельна; нажатие открывает [Sheet] с развёрнутым текстом.
 * 2. «Кнопка тревоги» — заблокирована (alpha 0.5f, без onClick).
 *
 * @param result Данные для отображения.
 * @param onAction Коллбэк для отправки действий в ViewModel.
 */
@Composable
internal fun AssistanceWidgetSuccessfulContent(
    result: AssistanceWidgetUiResult,
    onAction: (AssistanceWidgetUiAction) -> Unit,
) {
    val pageCount = if (result.quote != null) PAGE_COUNT else 1
    val pagerState = rememberPagerState(pageCount = { pageCount })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
    ) { page ->
        when {
            result.quote != null && page == PAGE_QUOTE -> {
                CellInfo(
                    title = stringResource(Res.string.assistance_quote_title),
                    subtitle = result.quote.quoteText,
                    iconRes = Res.drawable.ic_assistance_quote,
                    iconGradient = TokensGradient.Green.getThemedGradient(),
                    outPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    onClick = { onAction(AssistanceWidgetUiAction.ShowQuoteSheet) },
                )
            }

            else -> {
                Box(modifier = Modifier.alpha(DISABLED_ALPHA)) {
                    CellInfo(
                        title = stringResource(Res.string.assistance_alarm_title),
                        subtitle = stringResource(Res.string.assistance_alarm_subtitle),
                        iconRes = Res.drawable.ic_assistance_alarm,
                        iconGradient = TokensGradient.Red.getThemedGradient(),
                        outPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        onClick = null,
                    )
                }
            }
        }
    }

    if (pagerState.pageCount > 1) {
        PagerDotsIndicator(
            pageCount = pagerState.pageCount,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
    }

    if (result.quote != null) {
        Sheet(
            isVisible = result.quote.isQuoteSheetVisible,
            onDismissRequest = { onAction(AssistanceWidgetUiAction.HideQuoteSheet) },
            title = result.quote.quoteText,
            isDragable = true,
            onDragDismissAction = { onAction(AssistanceWidgetUiAction.HideQuoteSheet) },
            isOutsideClickEnabled = true,
            onOutsideClickAction = { onAction(AssistanceWidgetUiAction.HideQuoteSheet) },
        ) {
            Body(
                text = result.quote.quoteDetailText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                isSecondary = true,
            )
        }
    }
}

/**
 * Горизонтальный ряд dot-индикаторов для пейджера.
 *
 * Активная страница отображается с акцентным цветом [Tokens.Action],
 * остальные — с цветом разделителя [Tokens.Separator].
 */
@Composable
private fun PagerDotsIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val color = if (isSelected) {
                Tokens.Action.getThemedColor()
            } else {
                Tokens.Separator.getThemedColor()
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(if (isSelected) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
