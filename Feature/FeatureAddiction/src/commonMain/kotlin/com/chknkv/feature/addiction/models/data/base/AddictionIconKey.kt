package com.chknkv.feature.addiction.models.data.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Строковый ключ иконки привычки из API, представленный как enum.
 *
 * Значения соответствуют ключам, возвращаемым бэкендом в полях `icon_key`.
 * При получении неизвестного ключа kotlinx.serialization бросает [kotlinx.serialization.SerializationException].
 */
@Serializable
internal enum class AddictionIconKey {
    /** Образ жизни — ключ API: `"ic_habit_lifestyle"`. */
    @SerialName("ic_habit_lifestyle") LIFESTYLE,

    /** Здоровье — ключ API: `"ic_habit_health"`. */
    @SerialName("ic_habit_health") HEALTH,

    /** Спорт — ключ API: `"ic_habit_sport"`. */
    @SerialName("ic_habit_sport") SPORT,

    /** Продуктивность — ключ API: `"ic_habit_productivity"`. */
    @SerialName("ic_habit_productivity") PRODUCTIVITY,

    /** Финансы — ключ API: `"ic_habit_finance"`. */
    @SerialName("ic_habit_finance") FINANCE,

    /** Отношения & развитие — ключ API: `"ic_habit_relationships"`. */
    @SerialName("ic_habit_relationships") RELATIONSHIPS,
}
