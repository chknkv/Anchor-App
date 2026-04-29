package com.chknkv.feature.settings.presentation.language

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.coreutils.AppLanguage
import com.chknkv.designsystem.cell.CellPicker
import com.chknkv.designsystem.module.Module
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.designsystem.screen.ButtonConfig
import org.jetbrains.compose.resources.stringResource
import anchor_app.feature.featuresettings.generated.resources.Res
import anchor_app.feature.featuresettings.generated.resources.settings_language
import anchor_app.feature.featuresettings.generated.resources.settings_language_english
import anchor_app.feature.featuresettings.generated.resources.settings_language_russian
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экран выбора языка приложения.
 * 
 * @param onBack Коллбэк для возврата на предыдущий экран.
 */
@Composable
fun LanguageScreen(onBack: () -> Unit) {
    val viewModel = koinViewModel<LanguageViewModel>()
    val selectedLanguage by viewModel.language.collectAsStateWithLifecycle()

    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(Res.string.settings_language),
        backButton = ButtonConfig(onClick = onBack),
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Module(outPaddingValues = PaddingValues(start = 16.dp, end = 16.dp, bottom = 0.dp)) {
                Column {
                    CellPicker(
                        title = stringResource(Res.string.settings_language_russian),
                        isSelected = selectedLanguage == AppLanguage.RUSSIAN,
                        isDivider = true,
                        onClick = { viewModel.setLanguage(AppLanguage.RUSSIAN) },
                    )
                    CellPicker(
                        title = stringResource(Res.string.settings_language_english),
                        isSelected = selectedLanguage == AppLanguage.ENGLISH,
                        isDivider = false,
                        onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
                    )
                }
            }
        }
    }
}
