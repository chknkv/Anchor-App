package com.chknkv.feature.addiction.presentation.all.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_errorState_description
import anchor_app.feature.featureaddiction.generated.resources.addictionAll_errorState_retry_button
import com.chknkv.designsystem.module.Module
import org.jetbrains.compose.resources.stringResource

/**
 * Экран ошибки загрузки для экрана всех привычек.
 *
 * Отображает фиксированное сообщение об ошибке по центру и кнопку повторной попытки.
 *
 * @param onRetry Коллбэк для повторной загрузки данных.
 */
@Composable
internal fun AddictionAllErrorContent(
    onRetry: () -> Unit
) {
    Module(innerPaddingValues = PaddingValues(horizontal = 14.dp, vertical = 16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(256.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Footnote(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.addictionAll_errorState_description),
                    isSecondary = true,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                text = stringResource(Res.string.addictionAll_errorState_retry_button),
                style = ButtonStyle.Default,
                onClick = onRetry
            )
        }
    }
}

