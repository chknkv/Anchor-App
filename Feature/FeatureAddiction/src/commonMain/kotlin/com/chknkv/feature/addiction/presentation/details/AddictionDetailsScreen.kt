package com.chknkv.feature.addiction.presentation.details

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addictionDetails_header_title
import anchor_app.feature.featureaddiction.generated.resources.ic_edit
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
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiAction
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiEvent
import com.chknkv.feature.addiction.models.presentation.details.AddictionDetailsUiState
import com.chknkv.feature.addiction.models.presentation.details.DetailsMode
import com.chknkv.feature.addiction.presentation.details.compose.AddictionDetailsEditContent
import com.chknkv.feature.addiction.presentation.details.compose.AddictionDetailsErrorContent
import com.chknkv.feature.addiction.presentation.details.compose.AddictionDetailsLoadingContent
import com.chknkv.feature.addiction.presentation.details.compose.AddictionDetailsViewContent
import com.chknkv.feature.addiction.presentation.details.elements.AddictionBackHandler
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Экран с детальной информацией о привычке.
 *
 * Поддерживает два режима: ViewMode (просмотр) и EditMode (редактирование).
 *
 * @param addictionId Идентификатор привычки.
 * @param onBack Коллбэк для возврата на предыдущий экран.
 */
@Composable
fun AddictionDetailsScreen(addictionId: Int, onBack: () -> Unit) {
    val viewModel = koinViewModel<AddictionDetailsViewModel>(
        parameters = { parametersOf(addictionId) },
    )

    LaunchedEffect(Unit) { viewModel.initScreen() }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddictionDetailsUiEvent.HabitDeleted -> onBack()
                is AddictionDetailsUiEvent.NavigateBack -> onBack()
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isEditMode = (uiState as? AddictionDetailsUiState.Successful)?.result?.mode is DetailsMode.EditMode
    AddictionBackHandler(enabled = isEditMode) {
        viewModel.emitAction(AddictionDetailsUiAction.NavigateBack)
    }

    val scaffoldTitle = when (val state = uiState) {
        is AddictionDetailsUiState.Successful ->
            if (state.result.mode is DetailsMode.ViewMode) state.result.title
            else stringResource(Res.string.addictionDetails_header_title)
        else -> stringResource(Res.string.addictionDetails_header_title)
    }

    val actionButton = when (val state = uiState) {
        is AddictionDetailsUiState.Successful ->
            if (state.result.mode is DetailsMode.ViewMode) {
                ButtonConfig(
                    onClick = remember(viewModel) {
                        { viewModel.emitAction(AddictionDetailsUiAction.SwitchToEditMode) }
                    },
                    iconRes = Res.drawable.ic_edit,
                )
            } else null
        else -> null
    }

    AppScaffold(
        modifier = Modifier.fillMaxSize(),
        title = scaffoldTitle,
        backButton = ButtonConfig(
            onClick = remember(viewModel) {
                { viewModel.emitAction(AddictionDetailsUiAction.NavigateBack) }
            }
        ),
        actionButton = actionButton,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is AddictionDetailsUiState.Init    -> Unit
                is AddictionDetailsUiState.Loading -> AddictionDetailsLoadingContent(contentPadding = padding)

                is AddictionDetailsUiState.Error   -> AddictionDetailsErrorContent(
                    message = state.message,
                    onRetry = remember(viewModel) {
                        { viewModel.emitAction(AddictionDetailsUiAction.Init) }
                    },
                )
                is AddictionDetailsUiState.Successful -> when (state.result.mode) {
                    is DetailsMode.ViewMode -> AddictionDetailsViewContent(
                        result = state.result,
                        onAction = viewModel::emitAction,
                        contentPadding = padding,
                    )

                    is DetailsMode.EditMode -> AddictionDetailsEditContent(
                        result = state.result,
                        onAction = viewModel::emitAction,
                        contentPadding = padding,
                    )
                }
            }
        }
    }

    if ((uiState as? AddictionDetailsUiState.Successful)?.result?.isLoading == true) {
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
