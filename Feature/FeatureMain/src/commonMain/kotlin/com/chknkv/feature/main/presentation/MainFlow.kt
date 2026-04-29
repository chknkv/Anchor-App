package com.chknkv.feature.main.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.chknkv.designsystem.screen.AppNavHost
import com.chknkv.feature.addiction.presentation.create.AddictionCreateScreen
import com.chknkv.feature.addiction.presentation.details.AddictionDetailsScreen
import com.chknkv.feature.main.navigation.MainNavRoute
import com.chknkv.feature.settings.presentation.settingsGraph
import com.chknkv.feature.settings.presentation.settingsStartRoute

/**
 * Основной рабочий флоу приложения (после авторизации).
 *
 * Включает в себя главный экран и навигационный граф настроек.
 *
 * @param onLogout Коллбэк, вызываемый при выходе из аккаунта (возвращает пользователя на Welcome Flow).
 */
@Composable
fun MainFlow(onLogout: () -> Unit) {
    val navController = rememberNavController()

    AppNavHost(
        navController = navController,
        startDestination = MainNavRoute.Main,
    ) {
        composable<MainNavRoute.Main> {
            MainScreen(
                onOpenSettings = { navController.navigate(settingsStartRoute) },
                onAddAddiction = { navController.navigate(MainNavRoute.AddictionCreate) },
                onInfoAddiction = { id -> navController.navigate(MainNavRoute.AddictionDetails(id)) },
            )
        }

        composable<MainNavRoute.AddictionCreate> {
            AddictionCreateScreen(onBack = { navController.popBackStack() })
        }

        composable<MainNavRoute.AddictionDetails> { backStackEntry ->
            val route: MainNavRoute.AddictionDetails = backStackEntry.toRoute()
            AddictionDetailsScreen(
                addictionId = route.addictionId,
                onBack = { navController.popBackStack() },
            )
        }

        settingsGraph(
            navController = navController,
            onBack = { navController.popBackStack() },
            onLogout = onLogout,
        )
    }
}
