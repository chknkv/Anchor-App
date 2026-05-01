package com.chknkv.feature.addiction.presentation

import anchor_app.feature.featureaddiction.generated.resources.Res
import anchor_app.feature.featureaddiction.generated.resources.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.chknkv.designsystem.*
import com.chknkv.designsystem.theme.TokensColor
import com.chknkv.designsystem.theme.getThemedColor
import com.chknkv.feature.addiction.models.domain.AddictionAllGroup
import com.chknkv.feature.addiction.models.domain.base.AddictionCategory
import com.chknkv.feature.addiction.models.domain.base.AddictionGradient
import com.chknkv.feature.addiction.models.domain.base.AddictionIcon
import com.chknkv.feature.addiction.models.domain.AddictionAllGroups
import com.chknkv.feature.addiction.models.domain.AddictionsSelectionGroups
import com.chknkv.feature.addiction.models.presentation.AddictionCategoryUi
import com.chknkv.feature.addiction.models.presentation.AddictionGradientUi
import com.chknkv.feature.addiction.models.presentation.AddictionIconUi
import com.chknkv.feature.addiction.models.presentation.all.AddictionAllUiResult
import com.chknkv.feature.addiction.models.presentation.all.UserAddictionGroupUi
import com.chknkv.feature.addiction.models.presentation.all.UserAddictionUi
import com.chknkv.feature.addiction.models.presentation.select.AddictionGroupUi
import com.chknkv.feature.addiction.models.presentation.select.AddictionUi
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

// -----------------------------
// AddictionAll Region
// -----------------------------

/**
 * Преобразует список доменных групп привычек в [AddictionAllUiResult].
 */
internal fun List<AddictionAllGroups>.toUiResult(): AddictionAllUiResult {
    val groups = map { group ->
        UserAddictionGroupUi(
            category = group.category.toUi(),
            addictions = group.addictions.map { it.toUi(group.category) },
        )
    }
    return AddictionAllUiResult(
        groups = groups,
        isCreateNewAvailable = firstOrNull()?.isCreateNewAvailable ?: false,
    )
}

/**
 * Преобразует доменную модель привычки в UI-модель.
 */
internal fun AddictionAllGroup.toUi(category: AddictionCategory): UserAddictionUi = UserAddictionUi(
    id = id,
    name = name,
    category = category.toUi(),
    iconRes = icon.toIconDrawableResource(),
    iconGradient = gradient.toGradientBrush(),
    controlDays = controlDays,
)

// -----------------------------
// AddictionSelect Region
// -----------------------------

/**
 * Преобразует доменную модель группы привычек в модель для UI.
 */
internal fun AddictionsSelectionGroups.toUi() = AddictionGroupUi(
    category = category.toUi(),
    addictions = addictions.map { AddictionUi(id = it.id, name = it.name) },
)

// -----------------------------
// AddictionCategory Region
// -----------------------------

/**
 * Конвертирует domain-enum [AddictionCategory] в presentation-enum [AddictionCategoryUi].
 */
internal fun AddictionCategory.toUi(): AddictionCategoryUi = when (this) {
    AddictionCategory.LIFESTYLE     -> AddictionCategoryUi.LIFESTYLE
    AddictionCategory.HEALTH        -> AddictionCategoryUi.HEALTH
    AddictionCategory.SPORT         -> AddictionCategoryUi.SPORT
    AddictionCategory.PRODUCTIVITY  -> AddictionCategoryUi.PRODUCTIVITY
    AddictionCategory.FINANCE       -> AddictionCategoryUi.FINANCE
    AddictionCategory.RELATIONSHIPS -> AddictionCategoryUi.RELATIONSHIPS
    AddictionCategory.OTHER         -> AddictionCategoryUi.OTHER
}

/**
 * Конвертирует presentation-enum [AddictionCategoryUi] в domain-enum [AddictionCategory].
 */
internal fun AddictionCategoryUi.toDomain(): AddictionCategory = when (this) {
    AddictionCategoryUi.LIFESTYLE     -> AddictionCategory.LIFESTYLE
    AddictionCategoryUi.HEALTH        -> AddictionCategory.HEALTH
    AddictionCategoryUi.SPORT         -> AddictionCategory.SPORT
    AddictionCategoryUi.PRODUCTIVITY  -> AddictionCategory.PRODUCTIVITY
    AddictionCategoryUi.FINANCE       -> AddictionCategory.FINANCE
    AddictionCategoryUi.RELATIONSHIPS -> AddictionCategory.RELATIONSHIPS
    AddictionCategoryUi.OTHER         -> AddictionCategory.OTHER
}

/**
 * Конвертирует presentation-enum [AddictionCategoryUi] в [StringResource] заголовка.
 */
internal fun AddictionCategoryUi.toStringResource(): StringResource = when (this) {
    AddictionCategoryUi.LIFESTYLE     -> Res.string.habit_group_lifestyle_title
    AddictionCategoryUi.HEALTH        -> Res.string.habit_group_health_title
    AddictionCategoryUi.SPORT         -> Res.string.habit_group_sport_title
    AddictionCategoryUi.PRODUCTIVITY  -> Res.string.habit_group_productivity_title
    AddictionCategoryUi.FINANCE       -> Res.string.habit_group_finance_title
    AddictionCategoryUi.RELATIONSHIPS -> Res.string.habit_group_relationships_title
    AddictionCategoryUi.OTHER         -> Res.string.habit_group_other_title
}

// -----------------------------
// AddictionIcon Region
// -----------------------------

/**
 * Конвертирует domain-sealed [AddictionIcon] в presentation-enum [AddictionIconUi].
 */
internal fun AddictionIcon.toUi(): AddictionIconUi = when (this) {
    AddictionIcon.Lifestyle     -> AddictionIconUi.LIFESTYLE
    AddictionIcon.Health        -> AddictionIconUi.HEALTH
    AddictionIcon.Sport         -> AddictionIconUi.SPORT
    AddictionIcon.Productivity  -> AddictionIconUi.PRODUCTIVITY
    AddictionIcon.Finance       -> AddictionIconUi.FINANCE
    AddictionIcon.Relationships -> AddictionIconUi.RELATIONSHIPS
}

/**
 * Конвертирует presentation-enum [AddictionIconUi] в domain-sealed [AddictionIcon].
 */
internal fun AddictionIconUi.toDomain(): AddictionIcon = when (this) {
    AddictionIconUi.LIFESTYLE     -> AddictionIcon.Lifestyle
    AddictionIconUi.HEALTH        -> AddictionIcon.Health
    AddictionIconUi.SPORT         -> AddictionIcon.Sport
    AddictionIconUi.PRODUCTIVITY  -> AddictionIcon.Productivity
    AddictionIconUi.FINANCE       -> AddictionIcon.Finance
    AddictionIconUi.RELATIONSHIPS -> AddictionIcon.Relationships
}

/**
 * Конвертирует presentation-enum [AddictionIconUi] в [DrawableResource].
 */
internal fun AddictionIconUi.toDrawableResource(): DrawableResource = when (this) {
    AddictionIconUi.LIFESTYLE     -> Res.drawable.ic_habit_lifestyle
    AddictionIconUi.HEALTH        -> Res.drawable.ic_habit_health
    AddictionIconUi.SPORT         -> Res.drawable.ic_habit_sport
    AddictionIconUi.PRODUCTIVITY  -> Res.drawable.ic_habit_productivity
    AddictionIconUi.FINANCE       -> Res.drawable.ic_habit_finance
    AddictionIconUi.RELATIONSHIPS -> Res.drawable.ic_habit_relationships
}

/**
 * Конвертирует domain-sealed [AddictionIcon] в [DrawableResource].
 * Используется при построении [UserAddictionUi] для экрана списка.
 */
internal fun AddictionIcon.toIconDrawableResource(): DrawableResource = toUi().toDrawableResource()

// -----------------------------
// AddictionGradient Region
// -----------------------------

/**
 * Конвертирует domain-sealed [AddictionGradient] в presentation-enum [AddictionGradientUi].
 */
internal fun AddictionGradient.toUi(): AddictionGradientUi = when (this) {
    AddictionGradient.Gray       -> AddictionGradientUi.GRAY
    AddictionGradient.Green      -> AddictionGradientUi.GREEN
    AddictionGradient.Blue       -> AddictionGradientUi.BLUE
    AddictionGradient.Indigo     -> AddictionGradientUi.INDIGO
    AddictionGradient.Purple     -> AddictionGradientUi.PURPLE
    AddictionGradient.Pink       -> AddictionGradientUi.PINK
    AddictionGradient.Red        -> AddictionGradientUi.RED
    AddictionGradient.Orange     -> AddictionGradientUi.ORANGE
    AddictionGradient.DarkOrange -> AddictionGradientUi.DARK_ORANGE
    AddictionGradient.DarkRed    -> AddictionGradientUi.DARK_RED
    AddictionGradient.Black      -> AddictionGradientUi.BLACK
}

/**
 * Конвертирует presentation-enum [AddictionGradientUi] в domain-sealed [AddictionGradient].
 */
internal fun AddictionGradientUi.toDomain(): AddictionGradient = when (this) {
    AddictionGradientUi.GRAY        -> AddictionGradient.Gray
    AddictionGradientUi.GREEN       -> AddictionGradient.Green
    AddictionGradientUi.BLUE        -> AddictionGradient.Blue
    AddictionGradientUi.INDIGO      -> AddictionGradient.Indigo
    AddictionGradientUi.PURPLE      -> AddictionGradient.Purple
    AddictionGradientUi.PINK        -> AddictionGradient.Pink
    AddictionGradientUi.RED         -> AddictionGradient.Red
    AddictionGradientUi.ORANGE      -> AddictionGradient.Orange
    AddictionGradientUi.DARK_ORANGE -> AddictionGradient.DarkOrange
    AddictionGradientUi.DARK_RED    -> AddictionGradient.DarkRed
    AddictionGradientUi.BLACK       -> AddictionGradient.Black
}

/**
 * Конвертирует presentation-enum [AddictionGradientUi] в [Brush] для отрисовки в Compose.
 */
internal fun AddictionGradientUi.toGradientBrush(): Brush = when (this) {
    AddictionGradientUi.BLACK       -> Brush.verticalGradient(listOf(Black4, Black0))
    AddictionGradientUi.GRAY        -> Brush.verticalGradient(listOf(Gray0, Gray2))
    AddictionGradientUi.BLUE        -> Brush.verticalGradient(listOf(Blue3, Blue0))
    AddictionGradientUi.INDIGO      -> Brush.verticalGradient(listOf(Indigo0, Indigo1))
    AddictionGradientUi.PURPLE      -> Brush.verticalGradient(listOf(Purple1, Purple0))
    AddictionGradientUi.GREEN       -> Brush.verticalGradient(listOf(Green3, Green0))
    AddictionGradientUi.ORANGE      -> Brush.verticalGradient(listOf(Orange2, Orange0))
    AddictionGradientUi.DARK_ORANGE -> Brush.verticalGradient(listOf(Orange3, Orange4))
    AddictionGradientUi.RED         -> Brush.verticalGradient(listOf(Red3, Red0))
    AddictionGradientUi.DARK_RED    -> Brush.verticalGradient(listOf(Red0, Red6))
    AddictionGradientUi.PINK        -> Brush.verticalGradient(listOf(Pink1, Pink0))
}

/**
 * Возвращает основной цвет presentation-enum [AddictionGradientUi] для использования в Composable.
 */
@Composable
@ReadOnlyComposable
internal fun AddictionGradientUi.toGradientPrimaryColor(): Color = when (this) {
    AddictionGradientUi.GRAY        -> TokensColor.Gray.getThemedColor()
    AddictionGradientUi.GREEN       -> TokensColor.Green.getThemedColor()
    AddictionGradientUi.BLUE        -> TokensColor.Blue.getThemedColor()
    AddictionGradientUi.INDIGO      -> TokensColor.Indigo.getThemedColor()
    AddictionGradientUi.PURPLE      -> TokensColor.Purple.getThemedColor()
    AddictionGradientUi.ORANGE      -> TokensColor.Orange.getThemedColor()
    AddictionGradientUi.DARK_ORANGE -> TokensColor.DarkOrange.getThemedColor()
    AddictionGradientUi.RED         -> TokensColor.Red.getThemedColor()
    AddictionGradientUi.DARK_RED    -> TokensColor.DarkRed.getThemedColor()
    AddictionGradientUi.PINK        -> TokensColor.Pink.getThemedColor()
    AddictionGradientUi.BLACK       -> TokensColor.Black.getThemedColor()
}

/**
 * Конвертирует domain-sealed [AddictionGradient] в [Brush].
 * Используется при построении [UserAddictionUi] для экрана списка.
 */
internal fun AddictionGradient.toGradientBrush(): Brush = toUi().toGradientBrush()

/** Список всех доступных иконок для выбора пользователем. */
internal val AVAILABLE_ICONS: List<AddictionIconUi> = AddictionIconUi.entries

/** Список всех доступных градиентов для выбора пользователем. */
internal val AVAILABLE_GRADIENTS: List<AddictionGradientUi> = AddictionGradientUi.entries

/**
 * Форматирует количество секунд в строку обратного отсчёта формата ЧЧ:ММ:СС.
 */
internal fun Int.toCountdownString(): String {
    val h = this / 3600
    val m = (this % 3600) / 60
    val s = this % 60
    return "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}
