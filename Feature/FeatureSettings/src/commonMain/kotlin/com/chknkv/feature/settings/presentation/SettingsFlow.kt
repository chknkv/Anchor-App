package com.chknkv.feature.settings.presentation

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.chknkv.corepasscode.PasscodeFlow
import com.chknkv.corepasscode.PasscodeFlowMode
import com.chknkv.feature.settings.domain.SettingsInteractor
import com.chknkv.feature.settings.navigation.SettingsNavRoute
import com.chknkv.feature.settings.presentation.appearance.AppearanceScreen
import com.chknkv.feature.settings.presentation.language.LanguageScreen
import com.chknkv.feature.settings.presentation.main.MainSettingsScreen
import org.koin.compose.koinInject

// Публичная точка входа для навигации в настройки из родительского NavController
val settingsStartRoute: Any = SettingsNavRoute.Main

/**
 * Регистрирует экраны настроек в основном навигационном графе приложения.
 * 
 * Включает в себя главный экран настроек, выбор темы, выбор языка 
 * и управление паролем (Passcode).
 * 
 * @param navController Контроллер навигации для переходов между экранами настроек.
 * @param onBack Коллбэк для выхода из раздела настроек.
 * @param onLogout Коллбэк, вызываемый после выхода пользователя из аккаунта.
 */
fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    composable<SettingsNavRoute.Main> {
        val interactor: SettingsInteractor = koinInject()
        MainSettingsScreen(
            onNavigateBack = onBack,
            onOpenAppearance = { navController.navigate(SettingsNavRoute.Appearance) },
            onOpenLanguage = { navController.navigate(SettingsNavRoute.Language) },
            onOpenPrivacy = { navController.navigate(SettingsNavRoute.PasscodeFlow) },
            onLogout = {
                interactor.setAuthorized(false)
                onLogout()
            },
        )
    }

    composable<SettingsNavRoute.Appearance> {
        AppearanceScreen(onBack = { navController.popBackStack() })
    }

    composable<SettingsNavRoute.Language> {
        LanguageScreen(onBack = { navController.popBackStack() })
    }

    composable<SettingsNavRoute.PasscodeFlow> {
        val interactor: SettingsInteractor = koinInject()
        PasscodeFlow(
            mode = PasscodeFlowMode.Change,
            onBack = { navController.popBackStack() },
            onEnterSuccess = { navController.popBackStack() },
            onChangeSuccess = { navController.popBackStack() },
            onForgotPasscode = {
                interactor.setAuthorized(false)
                onLogout()
            },
            topPadding = 0.dp,
        )
    }
}
