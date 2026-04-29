package com.chknkv.anchor.root

import androidx.lifecycle.ViewModel
import com.chknkv.corepasscode.domain.PasscodeRepository
import com.chknkv.coreutils.AppLanguage
import com.chknkv.coreutils.AppSettings
import com.chknkv.coreutils.AppTheme
import kotlinx.coroutines.flow.StateFlow

/**
 * Глобальная ViewModel приложения.
 * 
 * Предоставляет доступ к общим настройкам (тема, язык) и определяет 
 * начальный маршрут при запуске приложения.
 * 
 * @param appSettings Сервис настроек приложения.
 * @param passcodeRepository Репозиторий паролей.
 */
class AnchorViewModel(
    appSettings: AppSettings,
    passcodeRepository: PasscodeRepository,
) : ViewModel() {

    val theme: StateFlow<AppTheme> = appSettings.theme

    val language: StateFlow<AppLanguage> = appSettings.language

    val initialRoute: AnchorNavRoute = if (appSettings.isAuthorized.value && !passcodeRepository.hasPasscode()) {
        AnchorNavRoute.Main
    } else {
        AnchorNavRoute.Welcome
    }
}
