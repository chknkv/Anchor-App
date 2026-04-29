package com.chknkv.coreutils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Сервис управления настройками приложения (тема, язык, авторизация).
 * Предоставляет реактивные состояния через [StateFlow] для наблюдения за изменениями настроек.
 */
interface AppSettings {

    /** Текущая тема оформления (системная, светлая или тёмная). */
    val theme: StateFlow<AppTheme>

    /** Текущий язык интерфейса приложения. */
    val language: StateFlow<AppLanguage>

    /** 
     * Флаг авторизации пользователя. 
     * Определяет, прошел ли пользователь этап входа в аккаунт.
     */
    val isAuthorized: StateFlow<Boolean>

    /** 
     * Устанавливает новую тему оформления.
     * @param theme Новая тема оформления для применения.
     */
    fun setTheme(theme: AppTheme)

    /** 
     * Устанавливает новый язык интерфейса.
     * @param language Новый язык приложения.
     */
    fun setLanguage(language: AppLanguage)

    /** 
     * Обновляет состояние авторизации пользователя.
     * @param isAuthorized true, если пользователь авторизован, иначе false.
     */
    fun setAuthorized(isAuthorized: Boolean)
}

/**
 * Реализация сервиса настроек [AppSettings].
 * 
 * Данный класс синхронизирует состояние в памяти (через [MutableStateFlow]) 
 * с персистентным хранилищем через вспомогательные объекты [ApplicationTheme], 
 * [ApplicationLanguage] и [ApplicationAuth].
 * 
 * @property appIdentifier Идентификатор приложения для изоляции настроек в хранилище.
 */
class AppSettingsImpl(appIdentifier: AppIdentifier) : AppSettings {

    private val _theme = MutableStateFlow(ApplicationTheme.getTheme(AppIdentifier.ANCHOR))
    private val _language = MutableStateFlow(ApplicationLanguage.getLanguage(appIdentifier))
    private val _isAuthorized = MutableStateFlow(ApplicationAuth.isAuthorized(AppIdentifier.ANCHOR))

    override val theme: StateFlow<AppTheme> = _theme.asStateFlow()
    override val language: StateFlow<AppLanguage> = _language.asStateFlow()
    override val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()

    override fun setTheme(theme: AppTheme) {
        ApplicationTheme.setTheme(AppIdentifier.ANCHOR, theme)
        _theme.value = theme
    }

    override fun setLanguage(language: AppLanguage) {
        ApplicationLanguage.setLanguage(AppIdentifier.ANCHOR, language)
        _language.value = language
    }

    override fun setAuthorized(isAuthorized: Boolean) {
        ApplicationAuth.setAuthorized(AppIdentifier.ANCHOR, isAuthorized)
        _isAuthorized.value = isAuthorized
    }
}
