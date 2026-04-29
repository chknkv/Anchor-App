package com.chknkv.designsystem.screen

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.chknkv.designsystem.theme.Tokens
import com.chknkv.designsystem.theme.getThemedColor

/**
 * Универсальный NavHost для проекта anchor.
 * Инкапсулирует в себе:
 * 1. Нативные анимации переходов (iOS-style).
 * 2. Оптимизированный жест "свайп назад" через [PredictiveBackWrapper].
 * 3. Системную подложку в цвет [Tokens.Background].
 *
 * @param navController Контроллер навигации.
 * @param startDestination Начальный маршрут (Type-safe).
 * @param enableSwipeBack Разрешить свайп назад. Передавай `false` для флоу без back-навигации (например, WelcomeFlow).
 * @param modifier Модификатор контейнера.
 * @param builder Конструктор графа навигации.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Any,
    enableSwipeBack: Boolean = true,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    builder: NavGraphBuilder.() -> Unit
) {
    val backInterceptorScope = remember { BackInterceptorScope() }

    CompositionLocalProvider(LocalBackInterceptor provides backInterceptorScope) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Tokens.Background.getThemedColor()
        ) {
            PredictiveBackWrapper(
                navController = navController,
                enabled = enableSwipeBack,
                backInterceptor = backInterceptorScope
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = contentAlignment,
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeIn(animationSpec = tween(durationMillis = 300))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it / 3 },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeOut(animationSpec = tween(durationMillis = 300))
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it / 3 },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeIn(animationSpec = tween(durationMillis = 300))
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(durationMillis = 300)
                        ) + fadeOut(animationSpec = tween(durationMillis = 300))
                    },
                    builder = builder
                )
            }
        }
    }
}

/**
 * Максимально оптимизированная обертка для поддержки жеста "свайп назад" на iOS.
 * Работает по всей ширине экрана. Использует низкоуровневый API обработки указателя.
 */
@Composable
private fun PredictiveBackWrapper(
    navController: NavHostController,
    enabled: Boolean = true,
    backInterceptor: BackInterceptor? = null,
    content: @Composable () -> Unit
) {
    val currentInterceptor = backInterceptor ?: LocalBackInterceptor.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(enabled, navController, currentInterceptor) {
                if (!enabled) return@pointerInput

                awaitEachGesture {
                    var accumulatedDragX = 0f
                    var accumulatedDragY = 0f
                    var isDirectionLocked = false
                    var isHorizontalGesture = false
                    var isBackGestureDetected = false

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break

                        if (change.pressed) {
                            accumulatedDragX += change.position.x - change.previousPosition.x
                            accumulatedDragY += change.position.y - change.previousPosition.y

                            if (!isDirectionLocked) {
                                if (abs(accumulatedDragX) > 15f || abs(accumulatedDragY) > 15f) {
                                    isHorizontalGesture = abs(accumulatedDragX) > abs(accumulatedDragY) * 1.5f
                                    isDirectionLocked = true
                                    if (!isHorizontalGesture) break
                                }
                                continue
                            }

                            if (!isBackGestureDetected && accumulatedDragX > 50f) {
                                if (currentInterceptor.isEnabled) {
                                    isBackGestureDetected = true
                                    change.consume()
                                    currentInterceptor.onBack()
                                    break
                                } else if (navController.previousBackStackEntry != null) {
                                    isBackGestureDetected = true
                                    change.consume()
                                    navController.popBackStack()
                                    break
                                }
                            }
                        } else {
                            break
                        }
                    }
                }
            }
    ) {
        content()
    }
}

