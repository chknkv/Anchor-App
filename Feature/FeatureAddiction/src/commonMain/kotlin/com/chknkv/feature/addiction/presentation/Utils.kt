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
import com.chknkv.feature.addiction.models.domain.AddictionCategory
import com.chknkv.feature.addiction.models.domain.UserAddiction
import com.chknkv.feature.addiction.models.domain.UserAddictionGroup
import com.chknkv.feature.addiction.models.domain.select.AddictionGroup
import com.chknkv.feature.addiction.models.presentation.all.AddictionAllUiResult
import com.chknkv.feature.addiction.models.presentation.all.AddictionCategoryUi
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
internal fun List<UserAddictionGroup>.toUiResult(): AddictionAllUiResult {
    val groups = map { group ->
        UserAddictionGroupUi(
            category = group.category.toUi(),
            addictions = group.addictions.map { it.toUi() },
        )
    }
    return AddictionAllUiResult(groups = groups)
}

/**
 * Преобразует доменную модель привычки в UI-модель.
 */
internal fun UserAddiction.toUi(): UserAddictionUi = UserAddictionUi(
    id = id,
    name = name,
    category = category.toUi(),
    iconRes = iconKey.toIconDrawableResource(),
    iconGradient = gradient.toGradientBrush(),
    controlDays = controlDays,
)

// -----------------------------
// AddictionSelect Region
// -----------------------------

/**
 * Преобразует доменную модель группы привычек в модель для UI.
 */
internal fun AddictionGroup.toUi() = AddictionGroupUi(
    category = category.toUi(),
    addictions = addictions.map { AddictionUi(id = it.id, name = it.name) },
)

// -----------------------------
// AddictionCategory Region
// -----------------------------

/**
 * Конвертирует доменную категорию в UI-enum.
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
 * Конвертирует UI-категорию в доменный enum.
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
 * Возвращает строковый ресурс заголовка для UI-категории.
 */
internal fun AddictionCategoryUi.toTitleStringResource(): StringResource = when (this) {
    AddictionCategoryUi.LIFESTYLE     -> Res.string.habit_group_lifestyle_title
    AddictionCategoryUi.HEALTH        -> Res.string.habit_group_health_title
    AddictionCategoryUi.SPORT         -> Res.string.habit_group_sport_title
    AddictionCategoryUi.PRODUCTIVITY  -> Res.string.habit_group_productivity_title
    AddictionCategoryUi.FINANCE       -> Res.string.habit_group_finance_title
    AddictionCategoryUi.RELATIONSHIPS -> Res.string.habit_group_relationships_title
    AddictionCategoryUi.OTHER         -> Res.string.habit_group_other_title
}

// -----------------------------
// UI Utils Region
// -----------------------------

/**
 * Конвертирует строковый ключ иконки в [DrawableResource].
 */
internal fun String.toIconDrawableResource(): DrawableResource = when (this) {
    ICON_HABIT_LIFESTYLE        -> Res.drawable.ic_habit_lifestyle
    ICON_HABIT_HEALTH           -> Res.drawable.ic_habit_health
    ICON_HABIT_SPORT            -> Res.drawable.ic_habit_sport
    ICON_HABIT_PRODUCTIVITY     -> Res.drawable.ic_habit_productivity
    ICON_HABIT_FINANCE          -> Res.drawable.ic_habit_finance
    ICON_HABIT_RELATIONSHIPS    -> Res.drawable.ic_habit_relationships
    else                        -> Res.drawable.ic_habit_placeholder
}

/**
 * Возвращает основной цвет градиента по его строковому ключу.
 */
@Composable
@ReadOnlyComposable
internal fun String.toGradientPrimaryColor(): Color = when (this) {
    GRADIENT_GRAY        -> TokensColor.Gray.getThemedColor()
    GRADIENT_GREEN       -> TokensColor.Green.getThemedColor()
    GRADIENT_BLUE        -> TokensColor.Blue.getThemedColor()
    GRADIENT_INDIGO      -> TokensColor.Indigo.getThemedColor()
    GRADIENT_PURPLE      -> TokensColor.Purple.getThemedColor()
    GRADIENT_ORANGE      -> TokensColor.Orange.getThemedColor()
    GRADIENT_DARK_ORANGE -> TokensColor.DarkOrange.getThemedColor()
    GRADIENT_RED         -> TokensColor.Red.getThemedColor()
    GRADIENT_DARK_RED    -> TokensColor.DarkRed.getThemedColor()
    GRADIENT_PINK        -> TokensColor.Pink.getThemedColor()
    GRADIENT_BLACK       -> TokensColor.Black.getThemedColor()
    else                 -> TokensColor.Gray.getThemedColor()
}

/**
 * Преобразует строковое название градиента в [Brush].
 */
fun String.toGradientBrush(): Brush = when (this) {
    GRADIENT_BLACK       -> Brush.verticalGradient(listOf(Black4, Black0))
    GRADIENT_GRAY        -> Brush.verticalGradient(listOf(Gray0, Gray2))
    GRADIENT_BLUE        -> Brush.verticalGradient(listOf(Blue3, Blue0))
    GRADIENT_INDIGO      -> Brush.verticalGradient(listOf(Indigo0, Indigo1))
    GRADIENT_PURPLE      -> Brush.verticalGradient(listOf(Purple1, Purple0))
    GRADIENT_GREEN       -> Brush.verticalGradient(listOf(Green3, Green0))
    GRADIENT_ORANGE      -> Brush.verticalGradient(listOf(Orange2, Orange0))
    GRADIENT_DARK_ORANGE -> Brush.verticalGradient(listOf(Orange3, Orange4))
    GRADIENT_RED         -> Brush.verticalGradient(listOf(Red3, Red0))
    GRADIENT_DARK_RED    -> Brush.verticalGradient(listOf(Red0, Red6))
    GRADIENT_PINK        -> Brush.verticalGradient(listOf(Pink1, Pink0))
    else                 -> Brush.verticalGradient(listOf(Gray0, Gray2))
}

// -----------------------------
// Константы (GRADIENT / ICON)
// -----------------------------

/** Ключ чёрного градиента. */
internal const val GRADIENT_BLACK = "black"
/** Ключ серого градиента. */
internal const val GRADIENT_GRAY = "gray"
/** Ключ синего градиента. */
internal const val GRADIENT_BLUE = "blue"
/** Ключ индиго-градиента. */
internal const val GRADIENT_INDIGO = "indigo"
/** Ключ фиолетового градиента. */
internal const val GRADIENT_PURPLE = "purple"
/** Ключ зелёного градиента. */
internal const val GRADIENT_GREEN = "green"
/** Ключ оранжевого градиента. */
internal const val GRADIENT_ORANGE = "orange"
/** Ключ тёмно-оранжевого градиента. */
internal const val GRADIENT_DARK_ORANGE = "dark_orange"
/** Ключ красного градиента. */
internal const val GRADIENT_RED = "red"
/** Ключ тёмно-красного градиента. */
internal const val GRADIENT_DARK_RED = "dark_red"
/** Ключ розового градиента. */
internal const val GRADIENT_PINK = "pink"

/** Ключ иконки образа жизни. */
internal const val ICON_HABIT_LIFESTYLE = "ic_habit_lifestyle"
/** Ключ иконки здоровья. */
internal const val ICON_HABIT_HEALTH = "ic_habit_health"
/** Ключ иконки спорта. */
internal const val ICON_HABIT_SPORT = "ic_habit_sport"
/** Ключ иконки продуктивности. */
internal const val ICON_HABIT_PRODUCTIVITY = "ic_habit_productivity"
/** Ключ иконки финансов. */
internal const val ICON_HABIT_FINANCE = "ic_habit_finance"
/** Ключ иконки отношений. */
internal const val ICON_HABIT_RELATIONSHIPS = "ic_habit_relationships"
/** Ключ иконки-заглушки. */
internal const val ICON_HABIT_PLACEHOLDER = "ic_habit_placeholder"

/** Список всех доступных ключей иконок. */
internal val AVAILABLE_ICON_KEYS = listOf(
    ICON_HABIT_PLACEHOLDER,
    ICON_HABIT_LIFESTYLE,
    ICON_HABIT_HEALTH,
    ICON_HABIT_SPORT,
    ICON_HABIT_PRODUCTIVITY,
    ICON_HABIT_FINANCE,
    ICON_HABIT_RELATIONSHIPS,
)

/** Список всех доступных ключей градиентов. */
internal val AVAILABLE_GRADIENT_KEYS = listOf(
    GRADIENT_GRAY,
    GRADIENT_GREEN,
    GRADIENT_BLUE,
    GRADIENT_INDIGO,
    GRADIENT_PURPLE,
    GRADIENT_PINK,
    GRADIENT_RED,
    GRADIENT_ORANGE,
    GRADIENT_DARK_ORANGE,
    GRADIENT_DARK_RED,
    GRADIENT_BLACK,
)
