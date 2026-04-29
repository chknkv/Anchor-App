package com.chknkv.feature.settings.models.presentation

import com.chknkv.designsystem.theme.TokensGradient
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Результирующее состояние главного экрана настроек.
 *
 * @param modules Список модулей (групп) ячеек для отображения.
 * @param isLogoutConfirmVisible Видимость подтверждения выхода.
 */
data class SettingsUiResult(
    val modules: List<SettingsModuleModel>,
    val isLogoutConfirmVisible: Boolean = false,
)

/**
 * Модель данных для модуля настроек.
 *
 * @param items Список ячеек в модуле.
 * @param description Ресурс описания-подсказки.
 */
data class SettingsModuleModel(
    val items: List<SettingCellModel>,
    val description: StringResource? = null,
)

/**
 * Модель данных для ячейки настроек.
 *
 * @param title Заголовок ячейки (строка).
 * @param subtitle Подзаголовок (текущее значение настройки).
 * @param titleRes Ресурс заголовка.
 * @param subtitleRes Ресурс подзаголовка.
 * @param iconRes Ресурс иконки.
 * @param iconGradient Градиент иконки.
 * @param action Действие при клике на ячейку.
 * @param isChevron Отображать ли иконку перехода.
 * @param isWarning Выделять ли ячейку как предупреждение (красным).
 */
data class SettingCellModel(
    val title: String? = null,
    val subtitle: String? = null,
    val titleRes: StringResource? = null,
    val subtitleRes: StringResource? = null,
    val iconRes: DrawableResource? = null,
    val iconGradient: TokensGradient? = null,
    val action: SettingsUiAction? = null,
    val isChevron: Boolean = action != null,
    val isWarning: Boolean = false,
)
