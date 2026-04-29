package com.chknkv.feature.addiction.presentation.select

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.feature.addiction.models.presentation.select.AddictionSelectionUiAction
import com.chknkv.feature.addiction.models.presentation.select.AddictionSelectionUiEvent
import com.chknkv.feature.addiction.models.presentation.select.AddictionSelectionUiState
import com.chknkv.feature.addiction.presentation.select.compose.AddictionSelectionErrorContent
import com.chknkv.feature.addiction.presentation.select.compose.AddictionSelectionLoadingContent
import com.chknkv.feature.addiction.presentation.select.compose.AddictionSelectionSuccessfulContent
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экран выбора привычек по умолчанию (онбординг).
 * 
 * Позволяет пользователю выбрать несколько привычек из предложенного списка 
 * для персонализации приложения при первом запуске.
 * 
 * @param onFinished Коллбэк, вызываемый после завершения выбора (или пропуска этапа).
 */
@Composable
fun AddictionSelectionScreen(onFinished: () -> Unit) {
    val viewModel = koinViewModel<AddictionSelectionViewModel>()

    LaunchedEffect(Unit) { viewModel.initScreen() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                AddictionSelectionUiEvent.OnSelectionLimitReached ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                AddictionSelectionUiEvent.OnFinished -> onFinished()
            }
        }
    }

    when (val state = uiState) {
        is AddictionSelectionUiState.Init -> Unit
        is AddictionSelectionUiState.Error -> AddictionSelectionErrorContent()
        is AddictionSelectionUiState.Loading -> AddictionSelectionLoadingContent { viewModel.emitAction(AddictionSelectionUiAction.OnSkipClicked) }
        is AddictionSelectionUiState.Successful -> AddictionSelectionSuccessfulContent(
            result = state.result,
            onAction = viewModel::emitAction,
        )
    }
}
