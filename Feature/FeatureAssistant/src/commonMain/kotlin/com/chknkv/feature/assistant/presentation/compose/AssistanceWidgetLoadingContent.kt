package com.chknkv.feature.assistant.presentation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.modifier.shimmer

/**
 * Состояние загрузки виджета помощи.
 *
 * Отображает shimmer-прямоугольник с теми же габаритами, что и карусель в
 * [AssistanceWidgetSuccessfulContent], чтобы избежать скачка макета при переходе
 * из Loading в Successful.
 */
@Composable
internal fun AssistanceWidgetLoadingContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(144.dp)
                .shimmer()
        )

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(32.dp)
                .height(16.dp)
                .shimmer()
        )
    }

}
