package com.chknkv.feature.addiction.models.data.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Строковый ключ градиента привычки из API, представленный как enum.
 *
 * Значения соответствуют ключам, возвращаемым бэкендом в полях `gradient` и `gradient_key`.
 * При получении неизвестного ключа kotlinx.serialization бросает [kotlinx.serialization.SerializationException].
 */
@Serializable
internal enum class AddictionGradientKey {
    /** Серый градиент — ключ API: `"gray"`. */
    @SerialName("gray") GRAY,

    /** Зелёный градиент — ключ API: `"green"`. */
    @SerialName("green") GREEN,

    /** Синий градиент — ключ API: `"blue"`. */
    @SerialName("blue") BLUE,

    /** Индиго-градиент — ключ API: `"indigo"`. */
    @SerialName("indigo") INDIGO,

    /** Фиолетовый градиент — ключ API: `"purple"`. */
    @SerialName("purple") PURPLE,

    /** Розовый градиент — ключ API: `"pink"`. */
    @SerialName("pink") PINK,

    /** Красный градиент — ключ API: `"red"`. */
    @SerialName("red") RED,

    /** Оранжевый градиент — ключ API: `"orange"`. */
    @SerialName("orange") ORANGE,

    /** Тёмно-оранжевый градиент — ключ API: `"dark_orange"`. */
    @SerialName("dark_orange") DARK_ORANGE,

    /** Тёмно-красный градиент — ключ API: `"dark_red"`. */
    @SerialName("dark_red") DARK_RED,

    /** Чёрный градиент — ключ API: `"black"`. */
    @SerialName("black") BLACK,
}
