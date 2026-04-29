# FeatureMain

Основной рабочий флоу приложения после авторизации. KMP, commonMain only.
Stack: Compose MP · Jetpack Navigation · Koin 4.x.

Модуль — **агрегатор**: содержит `MainFlow`, `MainScreen` и DI-модуль, который подключает зависимости дочерних Feature-модулей. Собственной бизнес-логики нет, собственный MVI отсутствует.

Зависимости: `:Core:CoreDesignSystem`, `:Core:CoreUtils`, `:Feature:FeatureAddiction`, `:Feature:FeatureAssistant`, `:Feature:FeatureSettings`.

---

## Публичный API

```kotlin
// com.chknkv.feature.main.presentation
@Composable
fun MainFlow(onLogout: () -> Unit)

// com.chknkv.feature.main.di
val featureMainModule: org.koin.core.module.Module
```

`MainFlow` — единственная точка входа. Принимает `onLogout: () -> Unit` — коллбэк, который вызывается при выходе из аккаунта через Settings; `MainFlow` не знает об `AnchorNavRoute` и не управляет верхнеуровневой навигацией.

---

## Навигация

```kotlin
@Serializable
internal sealed interface MainNavRoute {
    @Serializable data object Main : MainNavRoute
    @Serializable data object AddictionCreate : MainNavRoute
    @Serializable data class AddictionDetails(val addictionId: Int) : MainNavRoute
}
```

`MainNavRoute` — `internal`, не экспортируется за пределы модуля.

### Граф переходов

| Действие | Откуда | Куда |
|----------|--------|------|
| Кнопка Settings | `MainScreen` | `settingsStartRoute` (из FeatureSettings) |
| Добавить привычку | `AddictionAllScreen` | `MainNavRoute.AddictionCreate` |
| Информация о привычке | `AddictionAllScreen` | `MainNavRoute.AddictionDetails(id)` |
| Back / свайп | любой экран | `popBackStack()` |
| Logout | Settings | `onLogout()` → верхний уровень → WelcomeFlow |

`settingsGraph()` — `NavGraphBuilder` extension из `FeatureSettings`, монтируется в общий граф `MainFlow`. Вложенный `NavHost` для Settings не создаётся.

---

## MainScreen

Composable без ViewModel. Отображает:
1. `AssistanceWidget()` — виджет из FeatureAssistant
2. `AddictionAllScreen(onAddAddiction, onInfoAddiction)` — список привычек из FeatureAddiction

Layout: `AppScaffold` с `ic_settings` в `actionButton` (без заголовка). Тело — `Column` с `verticalScroll`. `padding(paddingValues)` применяется на `Column` целиком; нижний отступ дублируется через `Spacer(height = paddingValues.calculateBottomPadding())` в конце колонки — стандартный edge-to-edge паттерн.

```kotlin
@Composable
fun MainScreen(
    onOpenSettings: () -> Unit,
    onAddAddiction: () -> Unit,
    onInfoAddiction: (Int) -> Unit,
)
```

---

## DI

```kotlin
val featureMainModule = module {
    includes(featureSettingsModule, featureAddictionModule, featureAssistantModule)
}
```

Собственных зарегистрированных зависимостей нет — только агрегация через `includes`. Подключается из `SharedModule` или `androidApp`.

---

## Файловая карта

```
src/commonMain/
├── composeResources/
│   ├── drawable/
│   │   └── ic_settings.xml              — иконка шестерёнки для кнопки в AppScaffold
│   ├── values/strings.xml               — пустой (строки хранятся в дочерних модулях)
│   └── values-ru/strings.xml            — пустой
└── kotlin/com/chknkv/feature/main/
    ├── di/
    │   └── FeatureMainModule.kt          — val featureMainModule; includes(...)
    ├── navigation/
    │   └── MainNavRoute.kt               — internal sealed interface с тремя маршрутами
    └── presentation/
        ├── MainFlow.kt                   — публичная точка входа; NavController + AppNavHost
        └── MainScreen.kt                 — AppScaffold + Column + AssistanceWidget + AddictionAllScreen
```

---

## Жёсткие правила

| # | Правило |
|---|---------|
| 1 | Один `AppNavHost` в `MainFlow` — не создавать вложенный `NavHost` для Settings (ломает жест назад) |
| 2 | Навигация в Settings — через `navController.navigate(settingsStartRoute)`; маршрут не хардкодить |
| 3 | `featureMainModule` только агрегирует через `includes` — не регистрировать зависимости напрямую |
| 4 | `AssistanceWidget()` идёт первым в `Column`, перед `AddictionAllScreen` |
| 5 | `MainNavRoute` — `internal`; не экспортировать за пределы модуля |
| 6 | `MainFlow` не импортирует другие Feature-модули кроме тех, что уже подключены |
| 7 | Строки не добавлять в `FeatureMain/strings.xml` — каждый дочерний модуль хранит свои строки сам |
