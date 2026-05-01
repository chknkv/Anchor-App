package com.chknkv.feature.addiction.presentation.details.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.modifier.shimmer
import com.chknkv.designsystem.module.Module
import com.chknkv.feature.addiction.presentation.details.elements.AddictionCalendarConstants

/** Отступ между ячейками. */
private val CELL_GAP = 5.dp

/**
 * Содержимое экрана деталей привычки во время загрузки.
 * Отображает индикатор активности по центру экрана.
 *
 * @param contentPadding Отступы, передаваемые от Scaffold (учитывают безопасные зоны и топбар).
 */
@Composable
internal fun AddictionDetailsLoadingContent(
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        Module(
            innerPaddingValues = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            outPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .shimmer(shape = RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(24.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp))
                )
            }
        }

        Module(
            outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp),
            innerPaddingValues = PaddingValues(horizontal = 24.dp, vertical = 18.dp),
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val cellSize = (maxWidth - CELL_GAP * (AddictionCalendarConstants.WEEKS_COUNT - 1)) / AddictionCalendarConstants.WEEKS_COUNT

                Column {
                    for (dayIndex in 0 until AddictionCalendarConstants.DAYS_IN_WEEK) {
                        Row {
                            repeat(AddictionCalendarConstants.WEEKS_COUNT) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(cellSize)
                                        .shimmer(shape = RoundedCornerShape(3.dp))
                                )
                                if (index < AddictionCalendarConstants.WEEKS_COUNT - 1) {
                                    Spacer(modifier = Modifier.width(CELL_GAP))
                                }
                            }
                        }
                        if (dayIndex < AddictionCalendarConstants.DAYS_IN_WEEK - 1) {
                            Spacer(modifier = Modifier.height(CELL_GAP))
                        }
                    }
                }
            }
        }

        Module(
            outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp),
            innerPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = if (index == 0) 0.dp else 4.dp)
                            .height(12.dp)
                            .shimmer(shape = RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        Module(
            outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp),
            innerPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(12.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp))
                )
            }
        }

        Module(
            outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp),
            innerPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.55f)
                        .height(18.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(14.dp)
                        .shimmer(shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}
