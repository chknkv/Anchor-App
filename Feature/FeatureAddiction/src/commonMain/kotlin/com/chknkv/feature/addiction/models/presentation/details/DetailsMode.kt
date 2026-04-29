package com.chknkv.feature.addiction.models.presentation.details

/**
 * Режим работы экрана деталей привычки.
 */
internal sealed interface DetailsMode {
    /** Просмотр информации. */
    data object ViewMode : DetailsMode
    /** Редактирование информации. */
    data object EditMode : DetailsMode
}
