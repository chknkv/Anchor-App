package com.chknkv.corepasscode.presentation.biometrysetup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.corepasscode.models.domain.BiometricType
import com.chknkv.corepasscode.models.presentation.biometrysetup.BiometrySetupUiAction
import com.chknkv.corepasscode.models.presentation.biometrysetup.BiometrySetupUiEvent
import com.chknkv.corepasscode.models.presentation.biometrysetup.BiometrySetupUiResult
import com.chknkv.corepasscode.models.presentation.biometrysetup.BiometrySetupUiState
import com.chknkv.corepasscode.presentation.rememberBiometricContext
import com.chknkv.designsystem.Subheadline
import com.chknkv.designsystem.Title1
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.designsystem.screen.ButtonConfig
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import anchor_app.core.corepasscode.generated.resources.Res
import anchor_app.core.corepasscode.generated.resources.biometry_enable
import anchor_app.core.corepasscode.generated.resources.biometry_prompt_cancel
import anchor_app.core.corepasscode.generated.resources.biometry_prompt_reason
import anchor_app.core.corepasscode.generated.resources.biometry_skip
import anchor_app.core.corepasscode.generated.resources.biometry_subtitle
import anchor_app.core.corepasscode.generated.resources.biometry_title
import anchor_app.core.corepasscode.generated.resources.ic_face
import anchor_app.core.corepasscode.generated.resources.ic_fingerprint

/**
 * Экран предложения подключить биометрию.
 *
 * @param onBack Callback кнопки «Назад»; `null` — кнопка не отображается.
 * @param topPadding Отступ от верхней границы контента до заголовка.
 * @param onFinished Биометрия подключена или пропущена — флоу завершается.
 */
@Composable
internal fun BiometrySetupScreen(
    onBack: (() -> Unit)?,
    topPadding: Dp,
    onFinished: () -> Unit,
) {
    val viewModel = koinViewModel<BiometrySetupViewModel>()
    val biometricContext = rememberBiometricContext()

    val biometryTitle = stringResource(Res.string.biometry_title)
    val biometryReason = stringResource(Res.string.biometry_prompt_reason)
    val biometryCancel = stringResource(Res.string.biometry_prompt_cancel)

    LaunchedEffect(Unit) {
        viewModel.initScreen(
            biometricContext = biometricContext,
            biometryTitle = biometryTitle,
            biometryReason = biometryReason,
            biometryCancel = biometryCancel,
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                BiometrySetupUiEvent.SetupFinished -> onFinished()
                BiometrySetupUiEvent.BiometricEnabled -> Unit
                is BiometrySetupUiEvent.BiometricFailed -> Unit
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is BiometrySetupUiState.Init -> Unit
        is BiometrySetupUiState.Successful -> BiometrySetupContent(
            result = state.result,
            onBack = onBack,
            topPadding = topPadding,
            onAction = viewModel::emitAction,
        )
    }
}

@Composable
private fun BiometrySetupContent(
    result: BiometrySetupUiResult,
    onBack: (() -> Unit)?,
    topPadding: Dp,
    onAction: (BiometrySetupUiAction) -> Unit,
) {
    val biometryIconRes = when (result.biometricType) {
        BiometricType.FACE_ID, BiometricType.FACE_ANDROID -> Res.drawable.ic_face
        BiometricType.TOUCH_ID -> Res.drawable.ic_fingerprint
        BiometricType.NONE -> null
    }

    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        backButton = onBack?.let { ButtonConfig(onClick = it) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = topPadding)
        ) {
            Title1(text = stringResource(Res.string.biometry_title))

            Subheadline(
                text = stringResource(Res.string.biometry_subtitle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                isSecondary = true,
            )

            biometryIconRes?.let { iconRes ->
                Icon(
                    painter = painterResource(iconRes),
                    tint = Tokens.IconPrimary.getThemedColor(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .weight(1f),
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                text = stringResource(Res.string.biometry_enable),
                style = ButtonStyle.Action,
                onClick = { onAction(BiometrySetupUiAction.Enable) },
                enabled = !result.isLoading,
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.biometry_skip),
                style = ButtonStyle.Default,
                onClick = { onAction(BiometrySetupUiAction.Skip) },
                enabled = !result.isLoading,
            )
        }
    }
}
