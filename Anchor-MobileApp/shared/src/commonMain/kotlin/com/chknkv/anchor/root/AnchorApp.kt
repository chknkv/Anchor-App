package com.chknkv.anchor.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chknkv.coreutils.AppTheme
import com.chknkv.designsystem.LocalAppLocale
import com.chknkv.designsystem.screen.AppNavHost
import com.chknkv.designsystem.theme.Theme
import com.chknkv.feature.main.presentation.MainFlow
import com.chknkv.feature.welcome.presentation.WelcomeFlow
import org.koin.compose.viewmodel.koinViewModel

/**
 * Корневой Composable-компонент всего приложения.
 * 
 * Настраивает тему ([Theme]), локализацию ([LocalAppLocale]) и управляет глобальной 
 * навигацией между флоу приветствия ([WelcomeFlow]) и основным рабочим флоу ([MainFlow]).
 */
@Composable
fun AnchorApp() {
    val vm = koinViewModel<AnchorViewModel>()
    val theme by vm.theme.collectAsStateWithLifecycle()
    val language by vm.language.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    val darkTheme = when (theme) {
        AppTheme.SYSTEM -> null
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }

    CompositionLocalProvider(LocalAppLocale provides language) {
        Theme(darkTheme = darkTheme) {
            AppNavHost(
                navController = navController,
                startDestination = vm.initialRoute,
            ) {
                composable<AnchorNavRoute.Welcome> {
                    WelcomeFlow(
                        onFinished = {
                            navController.navigate(AnchorNavRoute.Main) {
                                popUpTo<AnchorNavRoute.Welcome> { inclusive = true }
                            }
                        }
                    )
                }

                composable<AnchorNavRoute.Main> {
                    MainFlow(
                        onLogout = {
                            navController.navigate(AnchorNavRoute.Welcome) {
                                popUpTo<AnchorNavRoute.Main> { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
