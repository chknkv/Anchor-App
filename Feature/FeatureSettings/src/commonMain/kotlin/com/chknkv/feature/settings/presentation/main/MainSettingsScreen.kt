package com.chknkv.feature.settings.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.designsystem.Subheadline
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.cell.Cell
import com.chknkv.designsystem.cell.CellAction
import com.chknkv.designsystem.module.ModuleContent
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.designsystem.screen.ButtonConfig
import com.chknkv.designsystem.sheet.Sheet
import com.chknkv.designsystem.theme.getThemedGradient
import com.chknkv.feature.settings.models.presentation.MainSettingsUiState
import com.chknkv.feature.settings.models.presentation.SettingCellModel
import com.chknkv.feature.settings.models.presentation.SettingsLogoutAction
import com.chknkv.feature.settings.models.presentation.SettingsModuleModel
import com.chknkv.feature.settings.models.presentation.SettingsNavigationAction
import com.chknkv.feature.settings.models.presentation.SettingsUiResult
import org.jetbrains.compose.resources.stringResource
import anchor_app.feature.featuresettings.generated.resources.Res
import anchor_app.feature.featuresettings.generated.resources.settings_logout_confirm_button
import anchor_app.feature.featuresettings.generated.resources.settings_logout_confirm_subtitle
import anchor_app.feature.featuresettings.generated.resources.settings_logout_confirm_title
import anchor_app.feature.featuresettings.generated.resources.settings_title
import org.koin.compose.viewmodel.koinViewModel

/**
 * Главный экран настроек.
 * 
 * Отображает список модулей настроек (профиль, внешний вид, язык, безопасность и т.д.).
 * 
 * @param onNavigateBack Коллбэк для возврата на предыдущий экран.
 * @param onOpenAppearance Коллбэк для перехода к настройкам внешнего вида.
 * @param onOpenLanguage Коллбэк для перехода к настройкам языка.
 * @param onOpenPrivacy Коллбэк для перехода к настройкам безопасности (Passcode).
 * @param onLogout Коллбэк для выхода из аккаунта.
 */
@Composable
fun MainSettingsScreen(
    onNavigateBack: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onLogout: () -> Unit,
) {
    val viewModel = koinViewModel<MainSettingsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { action ->
            when (action) {
                SettingsNavigationAction.OpenThemeSettings -> onOpenAppearance()
                SettingsNavigationAction.OpenLanguageSettings -> onOpenLanguage()
                SettingsNavigationAction.OpenPrivacySettings -> onOpenPrivacy()
                SettingsNavigationAction.NavigateBack -> onNavigateBack()
                SettingsNavigationAction.Logout -> onLogout()
            }
        }
    }

    when (val state = uiState) {
        is MainSettingsUiState.Successful -> MainSettingsContent(
            result = state.result,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
private fun MainSettingsContent(
    result: SettingsUiResult,
    onAction: (com.chknkv.feature.settings.models.presentation.SettingsUiAction) -> Unit,
) {
    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(Res.string.settings_title),
        backButton = ButtonConfig(onClick = { onAction(SettingsNavigationAction.NavigateBack) }),
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            result.modules.forEachIndexed { index, module ->
                item {
                    ModuleContent(
                        outPaddingValues = PaddingValues(top = if (index != 0) 16.dp else 0.dp),
                        description = if (module.description != null) stringResource(module.description) else null,
                    ) {
                        Column {
                            module.items.forEachIndexed { itemIndex, item ->
                                if (item.isWarning) {
                                    SettingsCellAction(
                                        item = item,
                                        index = itemIndex,
                                        module = module,
                                        onAction = onAction,
                                    )
                                } else {
                                    SettingsCell(
                                        item = item,
                                        index = itemIndex,
                                        module = module,
                                        onAction = onAction,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Sheet(
            isVisible = result.isLogoutConfirmVisible,
            onDismissRequest = { onAction(SettingsLogoutAction.HideLogoutConfirm) },
            onDragDismissAction = { onAction(SettingsLogoutAction.HideLogoutConfirm) },
            title = stringResource(Res.string.settings_logout_confirm_title),
            subtitle = stringResource(Res.string.settings_logout_confirm_subtitle),
            isDragable = true,
        ) {
            Button(
                text = stringResource(Res.string.settings_logout_confirm_button),
                style = ButtonStyle.Warning,
                onClick = { onAction(SettingsNavigationAction.Logout) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun SettingsCell(
    item: SettingCellModel,
    index: Int,
    module: SettingsModuleModel,
    onAction: (com.chknkv.feature.settings.models.presentation.SettingsUiAction) -> Unit,
) {
    Cell(
        title = item.title ?: item.titleRes?.let { stringResource(it) } ?: "",
        trailingContent = {
            Subheadline(
                text = item.subtitle ?: item.subtitleRes?.let { stringResource(it) } ?: "",
                isSecondary = true,
            )
        },
        iconRes = item.iconRes,
        iconGradient = item.iconGradient?.getThemedGradient(),
        isChevron = item.isChevron,
        onClick = item.action?.let { action -> { onAction(action) } },
        isDivider = index != module.items.lastIndex,
    )
}

@Composable
private fun SettingsCellAction(
    item: SettingCellModel,
    index: Int,
    module: SettingsModuleModel,
    onAction: (com.chknkv.feature.settings.models.presentation.SettingsUiAction) -> Unit,
) {
    CellAction(
        title = item.title ?: item.titleRes?.let { stringResource(it) } ?: "",
        isWarning = item.isWarning,
        onClick = item.action?.let { action -> { onAction(action) } },
        isDivider = index != module.items.lastIndex,
    )
}
