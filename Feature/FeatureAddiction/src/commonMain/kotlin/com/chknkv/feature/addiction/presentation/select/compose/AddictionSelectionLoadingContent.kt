package com.chknkv.feature.addiction.presentation.select.compose

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.addiction_selection_button_skip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.button.Button
import com.chknkv.designsystem.button.ButtonStyle
import com.chknkv.designsystem.modifier.shimmer
import com.chknkv.designsystem.module.Module
import com.chknkv.designsystem.screen.AppScaffold
import com.chknkv.feature.addiction.presentation.select.elements.SelectionHeader
import org.jetbrains.compose.resources.stringResource


/**
 * Загрузочное состояние экрана выбора зависимостей по-дефолту.
 *
 * Отображает скелет с shimmer-эффектами вместо реального контента:
 * - Заголовок + подзаголовок (shimmer для заголовка)
 * - 4 секции с группами привычек (карточки ModuleContent)
 * - Каждая секция содержит FlowRow с 7 shimmer-боксами разных размеров
 * - Нижняя панель со счётчиком и отключённой кнопкой
 */
@Composable
internal fun AddictionSelectionLoadingContent(onSkip: () -> Unit) {
    AppScaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                SelectionHeader()
                repeat(5) { SelectionGroupLoading() }
            }

            Button(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                text = stringResource(Res.string.addiction_selection_button_skip),
                style = ButtonStyle.Default,
                onClick = onSkip
            )
        }
    }
}

/**
 * Shimmer-заглушка для одной группы привычек.
 * Генерирует набор боксов разной ширины, имитирующих чипсы.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectionGroupLoading() {
    Module(
        modifier = Modifier.fillMaxWidth(),
        outPaddingValues = PaddingValues(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 8.dp),
        innerPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .sizeIn(minHeight = 45.dp, minWidth = 128.dp)
                        .clip(RoundedCornerShape(50))
                        .padding(4.dp)
                        .shimmer(shape = RoundedCornerShape(50.dp))
                )
            }

            repeat(2) {
                Box(
                    modifier = Modifier
                        .sizeIn(minHeight = 45.dp, minWidth = 96.dp)
                        .clip(RoundedCornerShape(50))
                        .padding(4.dp)
                        .shimmer(shape = RoundedCornerShape(50.dp))
                )
            }

            repeat(5) {
                Box(
                    modifier = Modifier
                        .sizeIn(minHeight = 45.dp, minWidth = 72.dp)
                        .clip(RoundedCornerShape(50))
                        .padding(4.dp)
                        .shimmer(shape = RoundedCornerShape(50.dp))
                )
            }
        }
    }
}
