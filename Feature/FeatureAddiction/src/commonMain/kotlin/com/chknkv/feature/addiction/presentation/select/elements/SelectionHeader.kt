package com.chknkv.feature.addiction.presentation.select.elements

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addiction_selection_body_subtitle
import anchor_app.feature.featureaddiction.generated.resources.addiction_selection_header_title
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Subheadline
import com.chknkv.designsystem.Title1
import org.jetbrains.compose.resources.stringResource

/**
 * Заголовок экрана выбора привычек.
 * Отображает приветственное сообщение и инструкцию.
 */
@Composable
internal fun SelectionHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp),
    ) {
        Title1(text = stringResource(Res.string.addiction_selection_header_title))
        Subheadline(
            text = stringResource(Res.string.addiction_selection_body_subtitle),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            isSecondary = true,
        )
    }
}