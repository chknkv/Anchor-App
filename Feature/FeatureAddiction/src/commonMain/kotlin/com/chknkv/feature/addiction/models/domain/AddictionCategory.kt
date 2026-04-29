package com.chknkv.feature.addiction.models.domain

/**
 * Категория привычки в доменном слое.
 *
 * Является единственным источником правды о категориях во всём модуле.
 * Каждое значение соответствует строковому ключу из API (см. [toApiKey]).
 * Отображаемые названия хранятся в строковых ресурсах и разрешаются в presentation-слое.
 *
 * Цепочка конвертации:
 * - data String → domain: [String.toAddictionCategory] (AddictionCategoryConverter.kt)
 * - domain → data String: [toApiKey] (AddictionCategoryConverter.kt)
 * - domain → UI: [AddictionCategoryUi.fromDomain] (Utils.kt)
 * - UI → domain: [AddictionCategoryUi.toDomain] (Utils.kt)
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
