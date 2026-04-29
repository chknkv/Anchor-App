package com.chknkv.corepasscode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.chknkv.corepasscode.domain.BiometricAuthenticator
import com.chknkv.corepasscode.domain.PasscodeRepository
import com.chknkv.corepasscode.navigation.PasscodeNavRoute
import com.chknkv.corepasscode.presentation.PasscodeBackHandler
import com.chknkv.corepasscode.presentation.biometrysetup.BiometrySetupScreen
import com.chknkv.corepasscode.presentation.createpasscode.CreatePasscodeScreen
import com.chknkv.corepasscode.presentation.enterpasscode.EnterPasscodeScreen
import com.chknkv.designsystem.screen.AppNavHost
import org.koin.compose.koinInject

/**
 * Публичная точка входа в модуль управления паролем (passcode).
 * 
 * Данный компонент автоматически определяет начальный экран в зависимости от [mode] 
 * и наличия сохранённого пароля. Управляет внутренней навигацией между экранами 
 * ввода, создания и настройки биометрии.
 *
 * ### Обработка системного события "Назад"
 * - Если `onBack == null`: кнопка "Назад" скрыта, системный жест/кнопка поглощаются (выход заблокирован).
 * - Если `onBack != null`: кнопка "Назад" отображается, системный жест вызывает [onBack].
 *
 * @param mode Режим работы флоу: [PasscodeFlowMode.Enter] (вход) или [PasscodeFlowMode.Change] (смена).
 * @param onBack Коллбэк для выхода из флоу. Если null — выход невозможен.
 * @param onEnterSuccess Вызывается при успешном вводе пароля или первичном создании в режиме Enter.
 * @param onChangeSuccess Вызывается после успешного завершения всего цикла смены пароля.
 * @param onSkipped Вызывается, если пользователь пропустил создание пароля.
 * @param onForgotPasscode Вызывается, если пользователь нажал "Забыл пароль" (пароль при этом сбрасывается).
 * @param topPadding Отступ сверху для контента экранов (используется для визуальной настройки).
 */
@Composable
fun PasscodeFlow(
    mode: PasscodeFlowMode,
    onBack: (() -> Unit)? = null,
    onEnterSuccess: () -> Unit,
    onChangeSuccess: () -> Unit = {},
    onSkipped: () -> Unit = {},
    onForgotPasscode: () -> Unit = {},
    topPadding: Dp = 32.dp,
) {
    val repository: PasscodeRepository = koinInject()
    val authenticator: BiometricAuthenticator = koinInject()

    val initialRoute: PasscodeNavRoute = remember(mode) {
        when (mode) {
            PasscodeFlowMode.Enter ->
                if (repository.hasPasscode()) PasscodeNavRoute.Enter
                else PasscodeNavRoute.Create(isChange = false)
            PasscodeFlowMode.Change ->
                if (repository.hasPasscode()) PasscodeNavRoute.Enter
                else PasscodeNavRoute.Create(isChange = true)
        }
    }

    PasscodeBackHandler(enabled = true) { onBack?.invoke() }

    val navController = rememberNavController()

    AppNavHost(navController = navController, startDestination = initialRoute) {
        composable<PasscodeNavRoute.Enter> {
            EnterPasscodeScreen(
                isChangeFlow = mode == PasscodeFlowMode.Change,
                onBack = onBack,
                topPadding = topPadding,
                onSuccess = {
                    when (mode) {
                        PasscodeFlowMode.Enter -> onEnterSuccess()
                        PasscodeFlowMode.Change -> navController.navigate(
                            PasscodeNavRoute.Create(isChange = true)
                        ) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                },
                onForgotPasscode = onForgotPasscode,
            )
        }

        composable<PasscodeNavRoute.Create> { backStackEntry ->
            val route = backStackEntry.toRoute<PasscodeNavRoute.Create>()
            CreatePasscodeScreen(
                isChangeFlow = route.isChange,
                onBack = onBack,
                topPadding = topPadding,
                onCreated = {
                    if (authenticator.isAvailable()) {
                        navController.navigate(PasscodeNavRoute.Biometry) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    } else {
                        when (mode) {
                            PasscodeFlowMode.Enter -> onEnterSuccess()
                            PasscodeFlowMode.Change -> onChangeSuccess()
                        }
                    }
                },
                onSkipped = onSkipped,
            )
        }

        composable<PasscodeNavRoute.Biometry> {
            BiometrySetupScreen(
                onBack = onBack,
                topPadding = topPadding,
                onFinished = {
                    when (mode) {
                        PasscodeFlowMode.Enter -> onEnterSuccess()
                        PasscodeFlowMode.Change -> onChangeSuccess()
                    }
                },
            )
        }
    }
}
