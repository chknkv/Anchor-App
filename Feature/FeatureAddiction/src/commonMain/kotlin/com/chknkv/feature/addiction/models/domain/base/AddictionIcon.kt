package com.chknkv.feature.addiction.models.domain.base

/**
 * Иконка привычки в доменном слое.
 *
 * Является единственным источником правды об иконках во всём модуле.
 */
internal enum class AddictionIcon {
    /** Образ жизни — ключ API: `"ic_habit_lifestyle"`. */
    Lifestyle,

    /** Здоровье — ключ API: `"ic_habit_health"`. */
   Health,

    /** Спорт — ключ API: `"ic_habit_sport"`. */
    Sport,

    /** Продуктивность — ключ API: `"ic_habit_productivity"`. */
    Productivity,

    /** Финансы — ключ API: `"ic_habit_finance"`. */
    Finance,

    /** Отношения & развитие — ключ API: `"ic_habit_relationships"`. */
    Relationships
}
