package com.chknkv.feature.settings.domain

import com.chknkv.coreutils.AppLanguage
import com.chknkv.coreutils.AppSettings
import com.chknkv.coreutils.AppTheme
import com.chknkv.coreutils.getAppVersion
import com.chknkv.designsystem.theme.TokensGradient
import com.chknkv.feature.settings.models.presentation.SettingCellModel
import com.chknkv.feature.settings.models.presentation.SettingsLogoutAction
import com.chknkv.feature.settings.models.presentation.SettingsModuleModel
import com.chknkv.feature.settings.models.presentation.SettingsNavigationAction
import com.chknkv.feature.settings.models.presentation.SettingsUiResult
import anchor_app.feature.featuresettings.generated.resources.Res
import anchor_app.feature.featuresettings.generated.resources.ic_appearance
import anchor_app.feature.featuresettings.generated.resources.ic_language
import anchor_app.feature.featuresettings.generated.resources.ic_lighter
import anchor_app.feature.featuresettings.generated.resources.ic_lock
import anchor_app.feature.featuresettings.generated.resources.ic_star
import anchor_app.feature.featuresettings.generated.resources.settings_anchor_premium
import anchor_app.feature.featuresettings.generated.resources.settings_appearance
import anchor_app.feature.featuresettings.generated.resources.settings_language
import anchor_app.feature.featuresettings.generated.resources.settings_language_english
import anchor_app.feature.featuresettings.generated.resources.settings_language_russian
import anchor_app.feature.featuresettings.generated.resources.settings_logout
import anchor_app.feature.featuresettings.generated.resources.settings_not_available
import anchor_app.feature.featuresettings.generated.resources.settings_privacy
import anchor_app.feature.featuresettings.generated.resources.settings_theme_dark
import anchor_app.feature.featuresettings.generated.resources.settings_theme_light
import anchor_app.feature.featuresettings.generated.resources.settings_theme_system
import anchor_app.feature.featuresettings.generated.resources.settings_version
import kotlinx.coroutines.flow.StateFlow

/**
 * Интерфейс бизнес-логики раздела настроек.
 * 
 * Оборачивает базовые настройки [AppSettings] и предоставляет методы для 
 * формирования структуры данных главного экрана настроек.
 */
interface SettingsInteractor {

    /** Текущая тема оформления. */
    val theme: StateFlow<AppTheme>

    /** Текущий язык приложения. */
    val language: StateFlow<AppLanguage>

    /** Устанавливает новую тему. */
    fun setTheme(theme: AppTheme)

    /** Устанавливает новый язык. */
    fun setLanguage(language: AppLanguage)

    /** Обновляет статус авторизации. */
    fun setAuthorized(isAuthorized: Boolean)

    /** 
     * Формирует иерархическую структуру данных для отображения на главном экране настроек.
     * @param isLogoutConfirmVisible Флаг отображения подтверждения выхода.
     */
    fun buildSettingsResult(isLogoutConfirmVisible: Boolean): SettingsUiResult
}

class SettingsInteractorImpl(
    private val appSettings: AppSettings
) : SettingsInteractor {

    override val theme: StateFlow<AppTheme> = appSettings.theme

    override val language: StateFlow<AppLanguage> = appSettings.language

    override fun setTheme(theme: AppTheme) {
        appSettings.setTheme(theme)
    }

    override fun setLanguage(language: AppLanguage) {
        appSettings.setLanguage(language)
    }

    override fun setAuthorized(isAuthorized: Boolean) {
        appSettings.setAuthorized(isAuthorized)
    }

    override fun buildSettingsResult(isLogoutConfirmVisible: Boolean): SettingsUiResult = SettingsUiResult(
        isLogoutConfirmVisible = isLogoutConfirmVisible,
        modules = listOf(
            SettingsModuleModel(
                items = listOf(
                    SettingCellModel(
                        titleRes = Res.string.settings_anchor_premium,
                        iconRes = Res.drawable.ic_star,
                        iconGradient = TokensGradient.Purple,
                        isChevron = true,
                    ),
                ),
                description = Res.string.settings_not_available,
            ),
            SettingsModuleModel(
                items = listOf(
                    SettingCellModel(
                        titleRes = Res.string.settings_appearance,
                        subtitleRes = when (theme.value) {
                            AppTheme.SYSTEM -> Res.string.settings_theme_system
                            AppTheme.LIGHT  -> Res.string.settings_theme_light
                            AppTheme.DARK   -> Res.string.settings_theme_dark
                        },
                        iconRes = Res.drawable.ic_appearance,
                        iconGradient = TokensGradient.Orange,
                        action = SettingsNavigationAction.OpenThemeSettings,
                    ),
                    SettingCellModel(
                        titleRes = Res.string.settings_language,
                        subtitleRes = when (language.value) {
                            AppLanguage.RUSSIAN -> Res.string.settings_language_russian
                            AppLanguage.ENGLISH -> Res.string.settings_language_english
                        },
                        iconRes = Res.drawable.ic_language,
                        iconGradient = TokensGradient.Blue,
                        action = SettingsNavigationAction.OpenLanguageSettings,
                    ),
                    SettingCellModel(
                        titleRes = Res.string.settings_privacy,
                        iconRes = Res.drawable.ic_lock,
                        iconGradient = TokensGradient.Gray,
                        isChevron = true,
                        action = SettingsNavigationAction.OpenPrivacySettings,
                    ),
                    SettingCellModel(
                        titleRes = Res.string.settings_version,
                        subtitle = getAppVersion(),
                        iconRes = Res.drawable.ic_lighter,
                        iconGradient = TokensGradient.Green,
                    ),
                ),
            ),
            SettingsModuleModel(
                items = listOf(
                    SettingCellModel(
                        titleRes = Res.string.settings_logout,
                        iconGradient = TokensGradient.Red,
                        isWarning = true,
                        action = SettingsLogoutAction.ShowLogoutConfirm,
                    ),
                ),
            ),
        ),
    )
}
