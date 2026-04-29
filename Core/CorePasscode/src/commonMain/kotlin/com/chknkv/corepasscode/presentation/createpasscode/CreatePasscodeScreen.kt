package com.chknkv.corepasscode.presentation.createpasscode

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.corepasscode.models.presentation.createpasscode.CreatePasscodeUiAction
import com.chknkv.corepasscode.models.presentation.createpasscode.CreatePasscodeUiEvent
import com.chknkv.corepasscode.models.presentation.createpasscode.CreatePasscodeUiResult
import com.chknkv.corepasscode.models.presentation.createpasscode.CreatePasscodeUiState
import com.chknkv.corepasscode.presentation.PASSCODE_LENGTH
import com.chknkv.corepasscode.presentation.alert.PasscodeAlert
import com.chknkv.corepasscode.presentation.keyboard.PasscodeIndicatorWithShake
import com.chknkv.designsystem.Subheadline
import com.chknkv.designsystem.Title1
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.passcode.PasscodeKeyboard
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.designsystem.screen.ButtonConfig
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import anchor_app.core.corepasscode.generated.resources.Res
import anchor_app.core.corepasscode.generated.resources.create_passcode_skip
import anchor_app.core.corepasscode.generated.resources.create_passcode_skip_alert_cancel
import anchor_app.core.corepasscode.generated.resources.create_passcode_skip_alert_confirm
import anchor_app.core.corepasscode.generated.resources.create_passcode_skip_alert_subtitle
import anchor_app.core.corepasscode.generated.resources.create_passcode_skip_alert_title
import anchor_app.core.corepasscode.generated.resources.create_passcode_step1_title
import anchor_app.core.corepasscode.generated.resources.create_passcode_step2_title
import anchor_app.core.corepasscode.generated.resources.create_passcode_subtitle

/**
 * Экран создания passcode.
 *
 * @param isChangeFlow `true` — режим смены (скрывает кнопку "Пропустить").
 * @param onBack Callback кнопки «Назад»; `null` — кнопка не отображается.
 * @param topPadding Отступ от верхней границы контента до заголовка.
 * @param onCreated Passcode успешно создан — флоу переходит к следующему шагу.
 * @param onSkipped Пользователь пропустил создание passcode.
 */
@Composable
internal fun CreatePasscodeScreen(
    isChangeFlow: Boolean,
    onBack: (() -> Unit)?,
    topPadding: Dp,
    onCreated: () -> Unit,
    onSkipped: () -> Unit,
) {
    val viewModel = koinViewModel<CreatePasscodeViewModel>()

    LaunchedEffect(Unit) { viewModel.initScreen(isChangeFlow) }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                CreatePasscodeUiEvent.PasscodeCreated -> onCreated()
                CreatePasscodeUiEvent.SkipRequested -> onSkipped()
                CreatePasscodeUiEvent.PasscodesDoNotMatch -> Unit
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is CreatePasscodeUiState.Init -> Unit
        is CreatePasscodeUiState.Successful -> CreatePasscodeContent(
            result = state.result,
            onBack = onBack,
            topPadding = topPadding,
            onAction = viewModel::emitAction,
        )
    }
}

@Composable
private fun CreatePasscodeContent(
    result: CreatePasscodeUiResult,
    onBack: (() -> Unit)?,
    topPadding: Dp,
    onAction: (CreatePasscodeUiAction) -> Unit,
) {
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
            AnimatedContent(
                targetState = result.isConfirming,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                label = "create_passcode_title_animation",
            ) { isConfirming ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Title1(
                        text = stringResource(
                            if (isConfirming) Res.string.create_passcode_step2_title
                            else Res.string.create_passcode_step1_title
                        )
                    )
                    Subheadline(
                        text = stringResource(Res.string.create_passcode_subtitle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        isSecondary = true,
                    )
                }
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PasscodeIndicatorWithShake(
                    length = PASSCODE_LENGTH,
                    filledCount = result.enteredDigits.size,
                    shakeTrigger = result.shakeTrigger,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PasscodeKeyboard(
                onDigitClick = { digit -> onAction(CreatePasscodeUiAction.NumberClick(digit)) },
                onDeleteClick = { onAction(CreatePasscodeUiAction.DeleteClick) },
                filledCount = result.enteredDigits.size,
            )

            if (result.isSkipAvailable) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    text = stringResource(Res.string.create_passcode_skip),
                    style = ButtonStyle.Default,
                    onClick = { onAction(CreatePasscodeUiAction.ShowSkipAlert) },
                )
            } else {
                Spacer(modifier = Modifier.height(50.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    PasscodeAlert(
        isVisible = result.isSkipAlertVisible,
        title = stringResource(Res.string.create_passcode_skip_alert_title),
        subtitle = stringResource(Res.string.create_passcode_skip_alert_subtitle),
        positiveText = stringResource(Res.string.create_passcode_skip_alert_confirm),
        negativeText = stringResource(Res.string.create_passcode_skip_alert_cancel),
        onPositive = { onAction(CreatePasscodeUiAction.Skip) },
        onNegative = { onAction(CreatePasscodeUiAction.DismissSkipAlert) },
    )
}
