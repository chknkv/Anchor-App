package com.chknkv.feature.addiction.presentation.select.elements

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addiction_selection_button_next
import anchor_app.feature.featureaddiction.generated.resources.addiction_selection_button_skip
import anchor_app.feature.featureaddiction.generated.resources.addiction_selection_counter
import anchor_app.feature.featureaddiction.generated.resources.addiction_selection_saveFailed_warning
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import com.chknkv.feature.addiction.models.presentation.select.AddictionSelectionUiResult
import org.jetbrains.compose.resources.stringResource

/**
 * Нижняя панель экрана выбора привычек.
 * Содержит счётчик выбранных элементов и адаптивную кнопку (Пропустить/Далее).
 * 
 * @param result Состояние выбора привычек.
 * @param onSkip Коллбэк при нажатии "Пропустить".
 * @param onNext Коллбэк при нажатии "Далее".
 */
@Composable
internal fun SelectionBottomBar(
    result: AddictionSelectionUiResult,
    onSkip: () -> Unit,
    onNext: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Footnote(
            text = stringResource(Res.string.addiction_selection_counter)
                .replace($$"%1$d", result.selectedCount.toString())
                .replace($$"%2$d", result.maxSelectable.toString()),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            isSecondary = true,
            textAlign = TextAlign.Center,
        )

        if (result.isFailed) {
            Footnote(
                text = stringResource(Res.string.addiction_selection_saveFailed_warning),
                color = Tokens.Warning.getThemedColor(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 8.dp),
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            text = if (result.isNextMode) {
                stringResource(Res.string.addiction_selection_button_next)
            } else {
                stringResource(Res.string.addiction_selection_button_skip)
            },
            style = if (result.isNextMode) ButtonStyle.Action else ButtonStyle.Default,
            onClick = if (result.isNextMode) onNext else onSkip,
        )
    }
}