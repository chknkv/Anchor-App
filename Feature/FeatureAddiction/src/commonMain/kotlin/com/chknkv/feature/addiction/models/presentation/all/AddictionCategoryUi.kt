package com.chknkv.feature.addiction.models.presentation.all

/**
 * Категория привычки в UI-слое.
 *
 * Используется для фильтрации на экране [com.chknkv.feature.addiction.presentation.all.AddictionAllScreen]
 * и выбора категории при создании/редактировании привычки.
 *
 * Цепочка конвертации:
 * - [com.chknkv.feature.addiction.models.domain.AddictionCategory] → [AddictionCategoryUi]: `AddictionCategory.toUi()` (Utils.kt)
 * - [AddictionCategoryUi] → [com.chknkv.feature.addiction.models.domain.AddictionCategory]: `AddictionCategoryUi.toDomain()` (Utils.kt)
 * - Заголовок из строковых ресурсов: `AddictionCategoryUi.toTitleStringResource()` (Utils.kt)
 */
internal enum class AddictionCategoryUi {
    /** Образ жизни. */
    LIFESTYLE,

    /** Здоровье. */
    HEALTH,

    /** Спорт. */
    SPORT,

    /** Продуктивность. */
    PRODUCTIVITY,

    /** Финансы. */
    FINANCE,

    /** Отношения & развитие. */
    RELATIONSHIPS,

    /** Другое. */
    OTHER,
}
