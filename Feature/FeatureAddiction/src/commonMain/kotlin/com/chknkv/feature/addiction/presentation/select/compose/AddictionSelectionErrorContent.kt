package com.chknkv.feature.addiction.presentation.select.compose

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addiction_selection_button_skip
import anchor_app.feature.featureaddiction.generated.resources.addiction_selection_errorState_description
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.feature.addiction.presentation.select.elements.SelectionHeader
import org.jetbrains.compose.resources.stringResource

/**
 * Отображает состояние ошибки на экране выбора привычек.
 * 
 * Используется, когда не удалось загрузить группы привычек из репозитория.
 */
@Composable
internal fun AddictionSelectionErrorContent(onSkip: () -> Unit) {
    AppScaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                SelectionHeader()
                SelectionGroupFailed()
            }

            Button(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                text = stringResource(Res.string.addiction_selection_button_skip),
                style = ButtonStyle.Default,
                onClick = onSkip
            )
        }
    }
}

@Composable
private fun ColumnScope.SelectionGroupFailed() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Footnote(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.addiction_selection_errorState_description),
            isSecondary = true,
            textAlign = TextAlign.Center
        )
    }
}