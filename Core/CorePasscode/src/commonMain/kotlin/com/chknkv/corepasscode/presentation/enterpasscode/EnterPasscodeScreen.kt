package com.chknkv.corepasscode.presentation.enterpasscode

import anchor_app.core.corepasscode.generated.resources.Res
import anchor_app.core.corepasscode.generated.resources.biometry_prompt_cancel
import anchor_app.core.corepasscode.generated.resources.biometry_prompt_reason
import anchor_app.core.corepasscode.generated.resources.biometry_title
import anchor_app.core.corepasscode.generated.resources.enter_passcode_forgot
import anchor_app.core.corepasscode.generated.resources.enter_passcode_forgot_alert_cancel
import anchor_app.core.corepasscode.generated.resources.enter_passcode_forgot_alert_confirm
import anchor_app.core.corepasscode.generated.resources.enter_passcode_forgot_alert_subtitle
import anchor_app.core.corepasscode.generated.resources.enter_passcode_forgot_alert_title
import anchor_app.core.corepasscode.generated.resources.enter_passcode_subtitle
import anchor_app.core.corepasscode.generated.resources.enter_passcode_subtitle_change
import anchor_app.core.corepasscode.generated.resources.enter_passcode_title
import anchor_app.core.corepasscode.generated.resources.enter_passcode_title_change
import anchor_app.core.corepasscode.generated.resources.ic_face
import anchor_app.core.corepasscode.generated.resources.ic_fingerprint
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
import com.chknkv.corepasscode.models.domain.BiometricType
import com.chknkv.corepasscode.models.presentation.enterpasscode.EnterPasscodeUiAction
import com.chknkv.corepasscode.models.presentation.enterpasscode.EnterPasscodeUiEvent
import com.chknkv.corepasscode.models.presentation.enterpasscode.EnterPasscodeUiResult
import com.chknkv.corepasscode.models.presentation.enterpasscode.EnterPasscodeUiState
import com.chknkv.corepasscode.presentation.PASSCODE_LENGTH
import com.chknkv.corepasscode.presentation.alert.PasscodeAlert
import com.chknkv.corepasscode.presentation.keyboard.PasscodeIndicatorWithShake
import com.chknkv.corepasscode.presentation.rememberBiometricContext
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.Subheadline
import com.chknkv.designsystem.Title1
import com.chknkv.designsystem.modifier.link
import com.chknkv.designsystem.passcode.PasscodeKeyboard
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.designsystem.screen.ButtonConfig
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экран ввода passcode.
 *
 * @param isChangeFlow `true` — режим верификации перед сменой (меняет заголовок и подпись).
 * @param onBack Callback кнопки «Назад»; `null` — кнопка не отображается.
 * @param topPadding Отступ от верхней границы контента до заголовка.
 * @param onSuccess Верификация прошла успешно.
 * @param onForgotPasscode Пользователь подтвердил сброс passcode.
 */
@Composable
internal fun EnterPasscodeScreen(
    isChangeFlow: Boolean,
    onBack: (() -> Unit)?,
    topPadding: Dp,
    onSuccess: () -> Unit,
    onForgotPasscode: () -> Unit,
) {
    val viewModel = koinViewModel<EnterPasscodeViewModel>()
    val biometricContext = rememberBiometricContext()

    val biometryTitle = stringResource(Res.string.biometry_title)
    val biometryReason = stringResource(Res.string.biometry_prompt_reason)
    val biometryCancel = stringResource(Res.string.biometry_prompt_cancel)

    LaunchedEffect(Unit) {
        viewModel.initScreen(
            isChangeFlow = isChangeFlow,
            biometricContext = biometricContext,
            biometryTitle = biometryTitle,
            biometryReason = biometryReason,
            biometryCancel = biometryCancel,
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                EnterPasscodeUiEvent.EnterSuccess -> onSuccess()
                EnterPasscodeUiEvent.ForgotPasscodeRequested -> onForgotPasscode()
                EnterPasscodeUiEvent.InvalidPasscode -> Unit
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is EnterPasscodeUiState.Init -> Unit
        is EnterPasscodeUiState.Successful -> EnterPasscodeContent(
            result = state.result,
            onBack = onBack,
            topPadding = topPadding,
            onAction = viewModel::emitAction,
        )
    }
}

@Composable
private fun EnterPasscodeContent(
    result: EnterPasscodeUiResult,
    onBack: (() -> Unit)?,
    topPadding: Dp,
    onAction: (EnterPasscodeUiAction) -> Unit,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
            ) {
                Title1(
                    text = stringResource(
                        if (result.isChangeFlow) Res.string.enter_passcode_title_change
                        else Res.string.enter_passcode_title
                    )
                )
                Subheadline(
                    text = stringResource(
                        if (result.isChangeFlow) Res.string.enter_passcode_subtitle_change
                        else Res.string.enter_passcode_subtitle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    isSecondary = true,
                )
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PasscodeIndicatorWithShake(
                    length = PASSCODE_LENGTH,
                    filledCount = result.enteredDigits.size,
                    shakeTrigger = result.shakeTrigger,
                    isError = result.isError,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            val showBiometryKey = result.isBiometricAvailable && result.biometricType != BiometricType.NONE
            val biometryPainter = if (showBiometryKey) {
                painterResource(
                    when (result.biometricType) {
                        BiometricType.TOUCH_ID -> Res.drawable.ic_fingerprint
                        else -> Res.drawable.ic_face
                    }
                )
            } else null

            PasscodeKeyboard(
                onDigitClick = { digit -> onAction(EnterPasscodeUiAction.NumberClick(digit)) },
                onDeleteClick = { onAction(EnterPasscodeUiAction.DeleteClick) },
                onBiometryClick = if (showBiometryKey) {
                    { onAction(EnterPasscodeUiAction.TryBiometric) }
                } else null,
                biometryPainter = biometryPainter,
                filledCount = result.enteredDigits.size,
            )

            Footnote(
                text = stringResource(Res.string.enter_passcode_forgot),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 48.dp)
                    .link { onAction(EnterPasscodeUiAction.ShowForgotAlert) },
                isSecondary = true,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    PasscodeAlert(
        isVisible = result.isForgotAlertVisible,
        title = stringResource(Res.string.enter_passcode_forgot_alert_title),
        subtitle = stringResource(Res.string.enter_passcode_forgot_alert_subtitle),
        positiveText = stringResource(Res.string.enter_passcode_forgot_alert_confirm),
        negativeText = stringResource(Res.string.enter_passcode_forgot_alert_cancel),
        onPositive = { onAction(EnterPasscodeUiAction.ForgotPasscode) },
        onNegative = { onAction(EnterPasscodeUiAction.HideForgotAlert) },
    )
}
