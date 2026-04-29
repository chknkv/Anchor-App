package com.chknkv.feature.addiction.models.presentation.all

/**
 * Действия пользователя на экране всех привычек.
 */
internal sealed interface AddictionAllUiAction {

    /** Инициализирует экран — загружает данные. Отправляется автоматически через onStart. */
    data object Init : AddictionAllUiAction

    /** Принудительно перезагружает список привычек (например, по нажатию «Повторить»). */
    data object Refresh : AddictionAllUiAction
}
