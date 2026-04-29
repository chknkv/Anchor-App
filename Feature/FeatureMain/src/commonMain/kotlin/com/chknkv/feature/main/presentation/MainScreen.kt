package com.chknkv.feature.main.presentation

import anchor_app.feature.featuremain.generated.resources.Res
import anchor_app.feature.featuremain.generated.resources.ic_settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.designsystem.screen.ButtonConfig
import com.chknkv.feature.addiction.presentation.all.AddictionAllScreen
import com.chknkv.feature.assistant.presentation.AssistanceWidget

/**
 * Главный экран приложения.
 *
 * Отображает основную информацию и предоставляет доступ к настройкам через кнопку в шапке.
 *
 * @param onOpenSettings Коллбэк для перехода в раздел настроек.
 * @param onAddAddiction Коллбэк для перехода к экрану создания новой привычки.
 * @param onInfoAddiction Коллбэк для перехода к экрану информации о привычке.
 */
@Composable
fun MainScreen(
    onOpenSettings: () -> Unit,
    onAddAddiction: () -> Unit,
    onInfoAddiction: (Int) -> Unit,
) {
    AppScaffold(
        modifier = Modifier.fillMaxWidth(),
        actionButton = ButtonConfig(
            onClick = onOpenSettings,
            iconRes = Res.drawable.ic_settings,
        ),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            AssistanceWidget()

            Spacer(modifier = Modifier.fillMaxWidth().height(12.dp))

            AddictionAllScreen(
                onAddAddiction = onAddAddiction,
                onInfoAddiction = onInfoAddiction,
            )

            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }
}
