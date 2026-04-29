package com.chknkv.feature.addiction.presentation.details.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chknkv.designsystem.module.Module
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor
import com.chknkv.feature.addiction.presentation.toGradientPrimaryColor
import kotlin.time.Clock

/** Количество отображаемых недель. */
private const val WEEKS_COUNT = 16
/** Дней в неделе. */
private const val DAYS_IN_WEEK = 7
/** Отступ между ячейками. */
private val CELL_GAP = 5.dp

/**
 * Модель дня для сетки активности.
 */
private data class CalendarDay(val dateIso: String?, val isFuture: Boolean)

/**
 * Компонент календаря активности привычки.
 * 
 * @param completedDates Список дат выполнения (ISO).
 * @param gradientKey Ключ градиента для закрашивания.
 */
@Composable
internal fun AddictionDetailsActivityCalendar(
    completedDates: Set<String>,
    gradientKey: String,
    modifier: Modifier = Modifier,
) {
    val todayIso = remember { getCurrentDateIso() }
    val weeks = remember(todayIso) { buildCalendarWeeks(todayIso) }
    val filledColor = gradientKey.toGradientPrimaryColor()
    val emptyColor = Tokens.IconSecondary.getThemedColor().copy(alpha = 0.6f)

    Module(
        outPaddingValues = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp),
        innerPaddingValues = PaddingValues(horizontal = 24.dp, vertical = 18.dp),
        modifier = modifier,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cellSize = (maxWidth - CELL_GAP * (WEEKS_COUNT - 1)) / WEEKS_COUNT

            Column {
                for (dayIndex in 0 until DAYS_IN_WEEK) {
                    Row {
                        weeks.forEachIndexed { weekIndex, days ->
                            val cell = days[dayIndex]
                            if (cell.isFuture) {
                                Spacer(modifier = Modifier.size(cellSize))
                            } else {
                                val isDone = cell.dateIso != null && completedDates.contains(cell.dateIso)
                                Box(
                                    modifier = Modifier
                                        .size(cellSize)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (isDone) filledColor else emptyColor),
                                )
                            }
                            if (weekIndex < weeks.lastIndex) {
                                Spacer(modifier = Modifier.width(CELL_GAP))
                            }
                        }
                    }
                    if (dayIndex < DAYS_IN_WEEK - 1) {
                        Spacer(modifier = Modifier.height(CELL_GAP))
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Date math helpers — чистая арифметика
// ---------------------------------------------------------------------------

private fun getCurrentDateIso(): String = try {
    val epochMs = Clock.System.now().toEpochMilliseconds()
    epochMsToIso(epochMs)
} catch (e: Exception) { "" }

private fun epochMsToIso(epochMs: Long): String {
    val z = ((epochMs / 86_400_000L) + 719468).toInt()
    val era = (if (z >= 0) z else z - 146096) / 146097
    val doe = z - era * 146097
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = doy - (153 * mp + 2) / 5 + 1
    val m = if (mp < 10) mp + 3 else mp - 9
    val yr = if (m <= 2) y + 1 else y
    return "$yr-${m.toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}"
}

private fun parseIsoDate(iso: String): Triple<Int, Int, Int>? {
    if (iso.length != 10) return null
    return try {
        Triple(iso.substring(0, 4).toInt(), iso.substring(5, 7).toInt(), iso.substring(8, 10).toInt())
    } catch (e: Exception) { null }
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    else -> 30
}

private fun dayOfWeek(year: Int, month: Int, day: Int): Int {
    val t = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
    var y = year
    if (month < 3) y--
    val dow = (y + y / 4 - y / 100 + y / 400 + t[month - 1] + day) % 7
    return if (dow == 0) 7 else dow
}

private fun addDays(year: Int, month: Int, day: Int, deltaDays: Int): Triple<Int, Int, Int> {
    var y = year; var m = month; var d = day + deltaDays
    if (deltaDays < 0) {
        while (d <= 0) {
            m--
            if (m <= 0) { m = 12; y-- }
            d += daysInMonth(y, m)
        }
    } else {
        while (d > daysInMonth(y, m)) {
            d -= daysInMonth(y, m)
            m++
            if (m > 12) { m = 1; y++ }
        }
    }
    return Triple(y, m, d)
}

private fun formatDate(year: Int, month: Int, day: Int): String =
    "${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"

/** Строит структуру недель для отображения в календаре от текущей даты в прошлое. */
private fun buildCalendarWeeks(todayIso: String): List<List<CalendarDay>> {
    val (todayYear, todayMonth, todayDay) = parseIsoDate(todayIso) ?: return emptyList()
    val daysBackToMonday = dayOfWeek(todayYear, todayMonth, todayDay) - 1
    val totalBack = daysBackToMonday + (WEEKS_COUNT - 1) * DAYS_IN_WEEK
    val (sy, sm, sd) = addDays(todayYear, todayMonth, todayDay, -totalBack)

    return List(WEEKS_COUNT) { weekIndex ->
        List(DAYS_IN_WEEK) { dayIndex ->
            val offset = weekIndex * DAYS_IN_WEEK + dayIndex
            val (cy, cm, cd) = addDays(sy, sm, sd, offset)
            val iso = formatDate(cy, cm, cd)
            CalendarDay(dateIso = if (iso > todayIso) null else iso, isFuture = iso > todayIso)
        }
    }
}