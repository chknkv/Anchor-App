package com.chknkv.feature.welcome.presentation.authorization

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.designsystem.Footnote
import com.chknkv.designsystem.Subheadline
import com.chknkv.designsystem.Title1
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonCircle
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.loading.LoadingHUD
import com.chknkv.designsystem.modifier.LinkSegment
import com.chknkv.designsystem.modifier.link
import com.chknkv.designsystem.module.ModuleContent
import com.chknkv.designsystem.otp.OtpCodeInput
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.designsystem.sheet.Sheet
import com.chknkv.designsystem.sheet.SheetHeightBehavior
import com.chknkv.designsystem.textinput.TextInput
import com.chknkv.feature.welcome.models.presentation.authorization.AuthorizationUiAction
import com.chknkv.feature.welcome.models.presentation.authorization.AuthorizationUiEvent
import com.chknkv.feature.welcome.models.presentation.authorization.AuthorizationUiResult
import org.jetbrains.compose.resources.stringResource
import anchor_app.feature.featurewelcome.generated.resources.Res
import anchor_app.feature.featurewelcome.generated.resources.authorization_body_subtitle
import anchor_app.feature.featurewelcome.generated.resources.authorization_button_get_otp
import anchor_app.feature.featurewelcome.generated.resources.authorization_header_title
import anchor_app.feature.featurewelcome.generated.resources.authorization_textinput_placeholder
import anchor_app.feature.featurewelcome.generated.resources.authorization_bottomsheet_title
import anchor_app.feature.featurewelcome.generated.resources.authorization_bottomsheet_footnote
import anchor_app.feature.featurewelcome.generated.resources.authorization_footer_terms
import anchor_app.feature.featurewelcome.generated.resources.authorization_footer_terms_first
import anchor_app.feature.featurewelcome.generated.resources.authorization_footer_terms_second
import anchor_app.feature.featurewelcome.generated.resources.authorization_resend_button
import anchor_app.feature.featurewelcome.generated.resources.authorization_timer_text
import anchor_app.feature.featurewelcome.generated.resources.authorization_error_otp
import anchor_app.feature.featurewelcome.generated.resources.ic_cross
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экран авторизации пользователя по Email.
 *
 * Включает в себя поле ввода email и модальное окно (Sheet) для ввода OTP-кода.
 *
 * @param onAuthorizedNewUser Коллбэк после успешной авторизации нового пользователя.
 * @param onAuthorizedReturningUser Коллбэк после успешной авторизации возвращающегося пользователя.
 */
@Composable
fun AuthorizationScreen(
    onAuthorizedNewUser: () -> Unit,
    onAuthorizedReturningUser: () -> Unit,
) {
    val viewModel = koinViewModel<AuthorizationViewModel>()
    val state by viewModel.uiResult.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                AuthorizationUiEvent.OnAuthorizedNewUser -> onAuthorizedNewUser()
                AuthorizationUiEvent.OnAuthorizedReturningUser -> onAuthorizedReturningUser()
            }
        }
    }

    AuthorizationContent(
        state = state,
        onAction = viewModel::emitAction,
        focusManager = focusManager,
    )
}

@Composable
private fun AuthorizationContent(
    state: AuthorizationUiResult,
    onAction: (AuthorizationUiAction) -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager,
) {
    AppScaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, end = 16.dp, top = 32.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Title1(text = stringResource(Res.string.authorization_header_title))

                Subheadline(
                    text = stringResource(Res.string.authorization_body_subtitle),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    isSecondary = true
                )

                ModuleContent(
                    modifier = Modifier.fillMaxWidth(),
                    outPaddingValues = PaddingValues(top = 12.dp),
                    description = when {
                        state.isError -> stringResource(Res.string.authorization_error_otp)
                        else -> null
                    },
                ) {
                    TextInput(
                        value = state.email,
                        onValueChange = { newValue ->
                            onAction(AuthorizationUiAction.OnEmailChanged(newValue))
                        },
                        placeholder = stringResource(Res.string.authorization_textinput_placeholder),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        isDivider = false
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    text = stringResource(Res.string.authorization_button_get_otp),
                    style = ButtonStyle.Action,
                    enabled = state.isGetOtpEnabled,
                    onClick = {
                        focusManager.clearFocus()
                        onAction(AuthorizationUiAction.OnGetOtpClicked)
                    }
                )
            }

            val termsText = stringResource(Res.string.authorization_footer_terms)
            val termsFirst = stringResource(Res.string.authorization_footer_terms_first)
            val termsSecond = stringResource(Res.string.authorization_footer_terms_second)
            val termsSegments = remember(termsText, termsFirst, termsSecond) {
                buildList {
                    val idx1 = termsText.indexOf(termsFirst)
                    if (idx1 >= 0) add(
                        LinkSegment(
                            range = idx1..<idx1 + termsFirst.length,
                            onAction = { onAction(AuthorizationUiAction.OnTermsClicked) }
                        )
                    )
                    val idx2 = termsText.indexOf(termsSecond)
                    if (idx2 >= 0) add(
                        LinkSegment(
                            range = idx2..<idx2 + termsSecond.length,
                            onAction = { onAction(AuthorizationUiAction.OnPrivacyPolicyClicked) }
                        )
                    )
                }
            }
            val termsTextLayout = remember { mutableStateOf<TextLayoutResult?>(null) }
            Footnote(
                text = termsText,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .link(
                        textLayoutResult = termsTextLayout,
                        segments = termsSegments,
                    ),
                textAlign = TextAlign.Center,
                isSecondary = true,
                onTextLayout = { termsTextLayout.value = it }
            )
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
        ) {
            LoadingHUD()
        }
    }

    AuthorizationOtpSheet(state = state, onAction = onAction)
}

@Composable
private fun AuthorizationOtpSheet(
    state: AuthorizationUiResult,
    onAction: (AuthorizationUiAction) -> Unit,
) {
    Sheet(
        isVisible = state.otp.isSheetVisible,
        isDragable = true,
        onDismissRequest = { onAction(AuthorizationUiAction.OnSheetVisibilityChange(false)) },
        isOutsideClickEnabled = true,
        heightBehavior = SheetHeightBehavior.WrapContent,
        title = stringResource(Res.string.authorization_bottomsheet_title),
        subtitle = stringResource(Res.string.authorization_bottomsheet_footnote),
        actionButton = {
            ButtonCircle(
                onClick = { onAction(AuthorizationUiAction.OnSheetVisibilityChange(false)) },
                iconRes = Res.drawable.ic_cross
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OtpCodeInput(
                    modifier = Modifier.padding(top = 32.dp, bottom = 44.dp),
                    value = state.otp.pinCode,
                    onValueChange = { onAction(AuthorizationUiAction.OnPinChange(it)) },
                    length = 5,
                    state = state.otp.pinState
                )

                val timerText = if (state.otp.timerValue > 0) {
                    stringResource(Res.string.authorization_timer_text, state.otp.timerValue)
                } else {
                    stringResource(Res.string.authorization_resend_button)
                }

                Footnote(
                    text = timerText,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .link(
                            enabled = state.otp.isResendAvailable,
                            onAction = { onAction(AuthorizationUiAction.OnResendOtpClicked) }
                        ),
                    isSecondary = true
                )
            }
        }
    )
}
