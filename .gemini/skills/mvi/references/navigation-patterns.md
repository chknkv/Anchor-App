# Navigation Component Multiplatform — паттерны

## Типизированные маршруты (рекомендуется)

Jetpack Navigation Component Multiplatform поддерживает типизированные маршруты
через kotlinx.serialization. Предпочитай их строковым маршрутам.

```kotlin
// routes/AppRoutes.kt (commonMain)
import kotlinx.serialization.Serializable

@Serializable
data object ScreenNameRoute

@Serializable
data class DetailRoute(val id: String)
```

## Регистрация экрана в NavGraph

```kotlin
// navigation/AppNavGraph.kt
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenNameRoute,
    ) {
        screenNameScreen(navController)
        detailScreen(navController)
    }
}

fun NavGraphBuilder.screenNameScreen(navController: NavController) {
    composable<ScreenNameRoute> {
        ScreenNameScreen(
            onNavigateBack = navController::navigateUp,
            onNavigateToDetail = { id -> navController.navigate(DetailRoute(id)) },
        )
    }
}
```

## Передача параметров экрану

Параметры приходят из NavBackStackEntry — не из лямбд.
ViewModel читает их через `SavedStateHandle`.

```kotlin
// Маршрут с параметром
@Serializable
data class ScreenNameRoute(val itemId: String)

// ViewModel читает параметр
class ScreenNameViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ScreenNameRepository,
) : ViewModel() {
    private val itemId: String = checkNotNull(savedStateHandle["itemId"])
    // или через типизированный extension:
    // private val route = savedStateHandle.toRoute<ScreenNameRoute>()
    // private val itemId = route.itemId
}
```

## Правило: ViewModel не знает о NavController

```kotlin
// ❌ Не делай так
class ScreenNameViewModel(
    private val navController: NavController, // ViewModel не должна держать NavController
) : ViewModel()

// ✅ Навигация — лямбды, прокинутые снаружи
@Composable
fun ScreenNameScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (id: String) -> Unit,
) {
    // ViewModel получает только бизнес-зависимости
    val viewModel = viewModel<ScreenNameViewModel>()
    // ...
    // Навигационный экшен обрабатывается здесь, не во ViewModel:
    LaunchedEffect(uiState) {
        if (uiState is ScreenNameUiState.Successful) {
            // если нужно — триггер навигации через стейт или отдельный UiEvent
        }
    }
}
```

## Koin — регистрация ViewModel

```kotlin
// di/ScreenNameModule.kt
val screenNameModule = module {
    viewModel { ScreenNameViewModel(repository = get()) }
}
```

```kotlin
// Screen — получение ViewModel
val viewModel = viewModel<ScreenNameViewModel>()
// или с параметрами:
val viewModel = viewModel<ScreenNameViewModel> {
    parametersOf(itemId)
}
```

## Навигационные действия из ViewModel через UiEvent (если нужен обратный вызов)

Если ViewModel должна инициировать навигацию (например, после успешного сохранения),
используй `UiEvent`, а в Screen подписывайся и вызывай лямбду:

```kotlin
// UiEvent
sealed interface ScreenNameUiEvent {
    data object NavigateBackOnSuccess : ScreenNameUiEvent
}

// Screen
LaunchedEffect(viewModel) {
    viewModel.uiEvent.collect { event ->
        when (event) {
            ScreenNameUiEvent.NavigateBackOnSuccess -> onNavigateBack()
        }
    }
}
```

Это единственный допустимый случай, когда навигация проходит через UiEvent.
