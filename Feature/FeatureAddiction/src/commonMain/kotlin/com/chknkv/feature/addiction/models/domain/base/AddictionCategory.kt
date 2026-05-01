package com.chknkv.feature.addiction.models.domain.base

/**
 * Категория привычки в доменном слое.
 *
 * Является единственным источником правды о категориях во всём модуле.
 * Отображаемые названия хранятся в строковых ресурсах и разрешаются в presentation-слое.
 */
internal enum class AddictionCategory {
    /** Образ жизни — ключ API: `"lifestyle"`. */
    LIFESTYLE,

    /** Здоровье — ключ API: `"health"`. */
    HEALTH,

    /** Спорт — ключ API: `"sport"`. */
    SPORT,

    /** Продуктивность — ключ API: `"productivity"`. */
    PRODUCTIVITY,

    /** Финансы — ключ API: `"finance"`. */
    FINANCE,

    /** Отношения & развитие — ключ API: `"relationships"`. */
    RELATIONSHIPS,

    /** Другое / неизвестная категория — ключ API: `"other"`. */
    OTHER,
}
