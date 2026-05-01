package com.chknkv.feature.addiction.models.data.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Строковый ключ категории привычки из API, представленный как enum.
 */
@Serializable
internal enum class AddictionCategoryKey {
    /** Образ жизни — ключ API: `"lifestyle"`. */
    @SerialName("lifestyle") LIFESTYLE,

    /** Здоровье — ключ API: `"health"`. */
    @SerialName("health") HEALTH,

    /** Спорт — ключ API: `"sport"`. */
    @SerialName("sport") SPORT,

    /** Продуктивность — ключ API: `"productivity"`. */
    @SerialName("productivity") PRODUCTIVITY,

    /** Финансы — ключ API: `"finance"`. */
    @SerialName("finance") FINANCE,

    /** Отношения & развитие — ключ API: `"relationships"`. */
    @SerialName("relationships") RELATIONSHIPS,

    /** Другое — ключ API: `"other"`. */
    @SerialName("other") OTHER,
}