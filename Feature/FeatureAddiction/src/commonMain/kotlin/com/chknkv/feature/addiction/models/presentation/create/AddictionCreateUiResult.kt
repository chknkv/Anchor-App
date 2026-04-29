package com.chknkv.feature.addiction.models.presentation.create

import androidx.compose.runtime.Immutable
import com.chknkv.feature.addiction.models.presentation.all.AddictionCategoryUi

/**
 * Состояние содержимого формы создания привычки.
 *
 * Используется внутри [AddictionCreateUiState.Successful].
 * [isLoading] и [errorMessage] управляют inline-индикаторами — экран при этом
 * не скрывается, данные формы сохраняются.
 *
 * @param title Текущее значение поля «Название».
 * @param description Текущее значение поля «Описание».
 * @param selectedIconKey Ключ выбранной иконки.
 * @param selectedGradientKey Ключ выбранного градиента.
 * @param selectedCategory Выбранная категория; null — категория не выбрана.
 * @param availableIconKeys Список ключей доступных иконок для пикера.
 * @param availableGradientKeys Список ключей доступных градиентов для пикера.
 * @param isLoading true — идёт отправка запроса на создание.
 * @param errorMessage Текст inline-ошибки под кнопкой; null — ошибок нет.
 */
@Immutable
internal data class AddictionCreateUiResult(
    val title: String = "",
    val description: String = "",
    val selectedIconKey: String,
    val selectedGradientKey: String,
    val selectedCategory: AddictionCategoryUi? = null,
    val availableIconKeys: List<String>,
    val availableGradientKeys: List<String>,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
