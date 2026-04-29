package com.chknkv.feature.settings.presentation.appearance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.coreutils.AppTheme
import com.chknkv.designsystem.cell.CellPicker
import com.chknkv.designsystem.module.Module
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.designsystem.screen.ButtonConfig
import org.jetbrains.compose.resources.stringResource
import anchor_app.feature.featuresettings.generated.resources.Res
import anchor_app.feature.featuresettings.generated.resources.settings_appearance
import anchor_app.feature.featuresettings.generated.resources.settings_theme_dark
import anchor_app.feature.featuresettings.generated.resources.settings_theme_light
import anchor_app.feature.featuresettings.generated.resources.settings_theme_system
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экран выбора темы оформления приложения (светлая, тёмная, системная).
 * 
 * @param onBack Коллбэк для возврата на предыдущий экран.
 */
@Composable
fun AppearanceScreen(onBack: () -> Unit) {
    val viewModel = koinViewModel<AppearanceViewModel>()
    val selectedTheme by viewModel.theme.collectAsStateWithLifecycle()

    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(Res.string.settings_appearance),
        backButton = ButtonConfig(onClick = onBack),
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Module(outPaddingValues = PaddingValues(start = 16.dp, end = 16.dp, bottom = 0.dp)) {
                Column {
                    CellPicker(
                        title = stringResource(Res.string.settings_theme_system),
                        isSelected = selectedTheme == AppTheme.SYSTEM,
                        onClick = { viewModel.setTheme(AppTheme.SYSTEM) },
                        isDivider = true,
                    )
                    CellPicker(
                        title = stringResource(Res.string.settings_theme_light),
                        isSelected = selectedTheme == AppTheme.LIGHT,
                        onClick = { viewModel.setTheme(AppTheme.LIGHT) },
                        isDivider = true,
                    )
                    CellPicker(
                        title = stringResource(Res.string.settings_theme_dark),
                        isSelected = selectedTheme == AppTheme.DARK,
                        onClick = { viewModel.setTheme(AppTheme.DARK) },
                        isDivider = false,
                    )
                }
            }
        }
    }
}
