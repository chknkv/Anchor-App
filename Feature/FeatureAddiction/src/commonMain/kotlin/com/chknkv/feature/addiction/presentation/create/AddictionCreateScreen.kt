package com.chknkv.feature.addiction.presentation.create

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addictionCreate_header_title
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.designsystem.loading.LoadingHUD
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.designsystem.screen.ButtonConfig
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import com.chknkv.feature.addiction.models.presentation.create.AddictionCreateUiEvent
import com.chknkv.feature.addiction.models.presentation.create.AddictionCreateUiState
import com.chknkv.feature.addiction.presentation.create.compose.AddictionCreateSuccessfulContent
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экран создания новой привычки.
 *
 * Отображает форму ввода названия, описания, выбора иконки, цвета и категории.
 * При успешном создании вызывает [onBack].
 *
 * @param onBack Коллбэк для возврата на предыдущий экран.
 */
@Composable
fun AddictionCreateScreen(onBack: () -> Unit) {
    val viewModel = koinViewModel<AddictionCreateViewModel>()

    LaunchedEffect(Unit) { viewModel.initScreen() }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                AddictionCreateUiEvent.OnCreated -> onBack()
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppScaffold(
        title = stringResource(Res.string.addictionCreate_header_title),
        backButton = ButtonConfig(onClick = onBack),
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is AddictionCreateUiState.Init -> Unit
                is AddictionCreateUiState.Loading -> Unit
                is AddictionCreateUiState.Error -> Unit
                is AddictionCreateUiState.Successful -> AddictionCreateSuccessfulContent(
                    result = state.result,
                    onAction = viewModel::emitAction,
                    contentPadding = padding,
                )
            }
        }
    }

    if ((uiState as? AddictionCreateUiState.Successful)?.result?.isLoading == true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Tokens.HudBackground.getThemedColor())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                ),
            contentAlignment = Alignment.Center,
        ) {
            LoadingHUD()
        }
    }
}
