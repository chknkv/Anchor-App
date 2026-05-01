package com.chknkv.feature.addiction.presentation.all

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.feature.addiction.models.presentation.all.AddictionAllUiAction
import com.chknkv.feature.addiction.models.presentation.all.AddictionAllUiState
import com.chknkv.feature.addiction.presentation.all.compose.AddictionAllEmptyContent
import com.chknkv.feature.addiction.presentation.all.compose.AddictionAllErrorContent
import com.chknkv.feature.addiction.presentation.all.compose.AddictionAllLoadingContent
import com.chknkv.feature.addiction.presentation.all.compose.AddictionAllSuccessfulContent
import org.koin.compose.viewmodel.koinViewModel

/**
 * Экран всех привычек пользователя.
 *
 * Разветвляется на состояния: загрузка, список привычек, ошибка, пустой список.
 * Навигация передаётся через лямбды — ViewModel не знает о навигации.
 *
 * @param onAddAddiction Коллбэк для перехода к экрану добавления новой привычки.
 * @param onOpenSettings Коллбэк для перехода в раздел настроек (подписки).
 * @param onInfoAddiction Коллбэк для перехода к экрану с информацией о привычке.
 */
@Composable
fun AddictionAllScreen(
    onAddAddiction: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onInfoAddiction: (Int) -> Unit = {},
) {
    val viewModel = koinViewModel<AddictionAllViewModel>()

    LaunchedEffect(Unit) { viewModel.initScreen() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is AddictionAllUiState.Init -> Unit

        is AddictionAllUiState.Loading -> AddictionAllLoadingContent()

        is AddictionAllUiState.Successful -> AddictionAllSuccessfulContent(
            result = state.result,
            onAddAddiction = onAddAddiction,
            onOpenSettings = onOpenSettings,
            onInfoAddiction = onInfoAddiction,
        )

        is AddictionAllUiState.Error -> AddictionAllErrorContent(
            onRetry = { viewModel.emitAction(AddictionAllUiAction.Refresh) }
        )

        is AddictionAllUiState.Empty -> AddictionAllEmptyContent(onAddAddiction = onAddAddiction)
    }
}
