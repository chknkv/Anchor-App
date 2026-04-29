package com.chknkv.feature.addiction.presentation.all.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.Separator
import com.chknkv.designsystem.module.Module
import com.chknkv.designsystem.modifier.shimmer

/**
 * Shimmer-скелет экрана всех привычек, отображаемый во время загрузки данных.
 *
 * Структура повторяет компоновку [AddictionAllSuccessfulContent]:
 * горизонтальная строка чипов-заглушек и карточка с четырьмя строками-ячейками.
 */
@Composable
internal fun AddictionAllLoadingContent() {
    Column {
        ShimmerCellAction()

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {

            items(6) {
                Box(
                    modifier = Modifier
                        .sizeIn(minHeight = 45.dp, minWidth = 128.dp)
                        .clip(RoundedCornerShape(50))
                        .padding(2.dp)
                        .shimmer(shape = RoundedCornerShape(50.dp))
                )
            }
        }

        Module {
            Column {
                repeat(5) { index ->
                    ShimmerCellContent()
                    if (index < 4) {
                        Separator(leadingInset = 38.dp)
                    }
                }
            }
        }
    }
}

/** Shimmer-заглушка ячейки-действия «Добавить привычку» в верхней части экрана. */
@Composable
private fun ShimmerCellAction() {
    Module(outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(164.dp)
                    .height(12.dp)
                    .shimmer(shape = RoundedCornerShape(4.dp))
            )
        }
    }
}

/** Shimmer-заглушка одной строки привычки: иконка, два текстовых блока и правый индикатор дней. */
@Composable
private fun ShimmerCellContent() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .shimmer(shape = RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(14.dp)
                    .shimmer(shape = RoundedCornerShape(4.dp)),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(11.dp)
                    .shimmer(shape = RoundedCornerShape(4.dp)),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .width(50.dp)
                .height(14.dp)
                .shimmer(shape = RoundedCornerShape(4.dp))
        )
    }
}


