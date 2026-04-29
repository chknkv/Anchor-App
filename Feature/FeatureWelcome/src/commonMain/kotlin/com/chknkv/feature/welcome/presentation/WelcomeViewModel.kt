package com.chknkv.feature.welcome.presentation

import androidx.lifecycle.ViewModel
import com.chknkv.corepasscode.domain.PasscodeRepository
import com.chknkv.coreutils.AppSettings
import com.chknkv.feature.welcome.navigation.WelcomeNavRoute

/**
 * ViewModel для управления корневой навигацией флоу приветствия.
 * 
 * Определяет стартовую точку (экраны авторизации, пароля или выбора привычек) 
 * на основе текущего состояния пользователя.
 * 
 * @param appSettings Общие настройки приложения (флаг авторизации).
 * @param passcodeRepository Репозиторий паролей.
 */
internal class WelcomeViewModel(
    appSettings: AppSettings,
    passcodeRepository: PasscodeRepository,
) : ViewModel() {

    val initialRoute: WelcomeNavRoute = when {
        appSettings.isAuthorized.value && passcodeRepository.hasPasscode() ->
            WelcomeNavRoute.Passcode(isCreation = false)
        appSettings.isAuthorized.value ->
            WelcomeNavRoute.HabitSelection
        else ->
            WelcomeNavRoute.Authorization
    }
}
