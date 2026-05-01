package com.chknkv.feature.addiction.models.domain.base

/**
 * Доменное представление градиента привычки.
 *
 * Каждый вариант соответствует визуальной теме оформления карточки привычки.
 * Конвертация в [androidx.compose.ui.graphics.Brush] происходит исключительно
 * в presentation-слое через `Utils.kt`.
 */
internal enum class AddictionGradient {
    /** Серый градиент. */
    Gray,

    /** Зелёный градиент. */
    Green,

    /** Синий градиент. */
    Blue,

    /** Индиго-градиент. */
    Indigo,

    /** Фиолетовый градиент. */
    Purple,

    /** Розовый градиент. */
    Pink,

    /** Красный градиент. */
    Red,

    /** Оранжевый градиент. */
    Orange,

    /** Тёмно-оранжевый градиент. */
    DarkOrange,

    /** Тёмно-красный градиент. */
    DarkRed,

    /** Чёрный градиент. */
    Black
}
