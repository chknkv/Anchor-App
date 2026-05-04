package com.chknkv.feature.welcome.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.chknkv.corepasscode.PasscodeFlow
import com.chknkv.corepasscode.PasscodeFlowMode
import com.chknkv.corepasscode.domain.PasscodeRepository
import com.chknkv.coreutils.AppSettings
import com.chknkv.designsystem.screen.AppNavHost
import com.chknkv.feature.addiction.presentation.select.AddictionSelectionScreen
import com.chknkv.feature.welcome.navigation.WelcomeNavRoute
import com.chknkv.feature.welcome.presentation.authorization.AuthorizationScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Корневой флоу онбординга и авторизации (Welcome Flow).
 * 
 * Управляет навигацией между экранами авторизации, создания/ввода пароля 
 * и первичного выбора привычек.
 * 
 * @param onFinished Коллбэк, вызываемый после успешного прохождения всех этапов приветствия.
 */
@Composable
fun WelcomeFlow(onFinished: () -> Unit) {
    val viewModel = koinViewModel<WelcomeViewModel>()
    val passcodeRepository: PasscodeRepository = koinInject()
    val appSettings: AppSettings = koinInject()
    val navController = rememberNavController()

    AppNavHost(
        navController = navController,
        startDestination = viewModel.initialRoute,
        enableSwipeBack = false,
    ) {
        composable<WelcomeNavRoute.Authorization> {
            AuthorizationScreen(
                onAuthorizedNewUser = {
                    passcodeRepository.clearPasscode()
                    navController.navigate(WelcomeNavRoute.Passcode(isCreation = true, isFirstAuthorized = true)) {
                        popUpTo<WelcomeNavRoute.Authorization> { inclusive = true }
                    }
                },
                onAuthorizedReturningUser = {
                    passcodeRepository.clearPasscode()
                    navController.navigate(WelcomeNavRoute.Passcode(isCreation = true, isFirstAuthorized = false)) {
                        popUpTo<WelcomeNavRoute.Authorization> { inclusive = true }
                    }
                },
            )
        }

        composable<WelcomeNavRoute.Passcode> { backStackEntry ->
            val route = backStackEntry.toRoute<WelcomeNavRoute.Passcode>()
            val onSuccess: () -> Unit = when {
                !route.isCreation -> onFinished
                route.isFirstAuthorized -> {
                    {
                        navController.navigate(WelcomeNavRoute.HabitSelection) {
                            popUpTo<WelcomeNavRoute.Passcode> { inclusive = true }
                        }
                    }
                }
                else -> onFinished
            }
            PasscodeFlow(
                mode = PasscodeFlowMode.Enter,
                onEnterSuccess = onSuccess,
                onSkipped = onSuccess,
                onForgotPasscode = {
                    appSettings.setAuthorized(false)
                    navController.navigate(WelcomeNavRoute.Authorization) {
                        popUpTo<WelcomeNavRoute.Passcode> { inclusive = true }
                    }
                }
            )
        }

        composable<WelcomeNavRoute.HabitSelection> {
            AddictionSelectionScreen(onFinished = onFinished)
        }
    }
}
