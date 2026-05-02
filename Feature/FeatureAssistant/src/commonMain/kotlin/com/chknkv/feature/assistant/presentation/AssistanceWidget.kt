package com.chknkv.feature.assistant.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.feature.assistant.models.presentation.AssistanceWidgetUiState
import com.chknkv.feature.assistant.presentation.compose.AssistanceWidgetLoadingContent
import com.chknkv.feature.assistant.presentation.compose.AssistanceWidgetSuccessfulContent
import org.koin.compose.viewmodel.koinViewModel

/**
 * Публичная точка входа виджета помощи.
 */
@Composable
fun AssistanceWidget() {
    val viewModel = koinViewModel<AssistanceWidgetViewModel>()
    LaunchedEffect(Unit) { viewModel.initWidget() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is AssistanceWidgetUiState.Loading -> AssistanceWidgetLoadingContent()
        is AssistanceWidgetUiState.Successful -> AssistanceWidgetSuccessfulContent(
            result = state.result,
            onAction = viewModel::emitAction,
        )
    }
}
