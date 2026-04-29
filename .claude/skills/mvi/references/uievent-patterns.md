# UiEvent — паттерны и anti-patterns

## Когда добавлять UiEvent

Добавляй `UiEvent` только если экран генерирует **одноразовые** события,
которые нельзя выразить через `UiState`:

| Сценарий                     | UiEvent? | Почему                                             |
|------------------------------|----------|----------------------------------------------------|
| Показ Toast / Snackbar        | ✅       | Одноразовое, не часть постоянного стейта           |
| Haptic-отклик (вибрация)      | ✅       | Нет смысла хранить в стейте                        |
| Системный диалог / Permission | ✅       | Разовый триггер                                    |
| Скролл к позиции              | ✅       | Императивное действие, не декларативный стейт      |
| Сообщение об ошибке (inline)  | ❌       | Хранить в `UiState.Error` или в поле `UiResult`    |
| Навигация                     | ❌       | Лямбды из NavGraph — не SharedFlow                 |
| Состояние загрузки            | ❌       | Это `UiState.Loading`                              |

## Шаблон с Haptic

```kotlin
// ViewModel
private val _uiEvent = MutableSharedFlow<ScreenNameUiEvent>()
val uiEvent: SharedFlow<ScreenNameUiEvent> = _uiEvent.asSharedFlow()

private fun handleSelectionLimitReached() {
    viewModelScope.launch {
        _uiEvent.emit(ScreenNameUiEvent.OnSelectionLimitReached)
    }
}
```

```kotlin
// Screen
val haptic = LocalHapticFeedback.current

LaunchedEffect(viewModel) {
    viewModel.uiEvent.collect { event ->
        when (event) {
            ScreenNameUiEvent.OnSelectionLimitReached ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}
```

> Ключ `LaunchedEffect(viewModel)` — а не `Unit`.
> При пересоздании Composable (например, при смене ориентации до уничтожения ViewModel)
> старый эффект отменяется и стартует новый с той же ViewModel, без дублирования подписки.

## Шаблон с SnackBar (Scaffold)

```kotlin
// Screen
val snackbarHostState = remember { SnackbarHostState() }

LaunchedEffect(viewModel) {
    viewModel.uiEvent.collect { event ->
        when (event) {
            is ScreenNameUiEvent.ShowMessage ->
                snackbarHostState.showSnackbar(event.message)
        }
    }
}

Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
    // контент
}
```

## Anti-patterns

```kotlin
// ❌ Навигация через UiEvent — не делай так
data object NavigateBack : ScreenNameUiEvent

// ✅ Навигация — лямбда в Screen из NavGraph
fun ScreenNameScreen(onNavigateBack: () -> Unit)
```

```kotlin
// ❌ UiEvent для состояния ошибки
data class ShowError(val message: String) : ScreenNameUiEvent
// затем в Screen: показываем/скрываем через локальный var

// ✅ Ошибка — в UiState
data class Error(val message: String) : ScreenNameUiState
```

```kotlin
// ❌ Пустой UiEvent-слой "на всякий случай"
sealed interface ScreenNameUiEvent  // нет вариантов

// ✅ Не генерируй UiEvent, если событий нет
```
