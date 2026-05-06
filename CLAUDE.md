# Anchor App

Kotlin Multiplatform + Compose Multiplatform. Android + iOS.
Package-prefix: `com.chknkv`. Kotlin: 2.3.21. ComposeMP: 1.10.3.

Каждый модуль содержит собственный `CLAUDE.md` с детальным контрактом.
Этот файл — глобальные правила, карта зависимостей и паттерны, общие для всего проекта.

---

## Карта модулей

```
:Anchor-MobileApp:androidApp   — Android точка входа (Application + MainActivity)
:Anchor-MobileApp:shared       — KMP точка входа: AnchorApp, AnchorViewModel, AnchorNavRoute, DI-сборка

:Core:CoreUtils                — AppSettings (тема/язык/авторизация), getAppVersion()
:Core:CoreDesignSystem         — Compose UI-компоненты, токены, типографика, навигация
:Core:CoreNetwork              — Ktor ApiClient, JWT refresh, NetworkException, TokenStorage
:Core:CorePasscode             — PasscodeFlow, биометрия

:Feature:FeatureWelcome        — WelcomeFlow: авторизация (email+OTP) → passcode → выбор привычек
:Feature:FeatureMain           — MainFlow: агрегатор главного экрана
:Feature:FeatureSettings       — settingsGraph: тема/язык/passcode/logout
:Feature:FeatureAddiction      — CRUD привычек: выбор, список, создание, детали
:Feature:FeatureAssistant      — виджет мотивационных цитат
```

---

## Граф зависимостей

```
shared
├── CoreUtils · CorePasscode · CoreNetwork
├── FeatureWelcome → CoreDesignSystem · CoreUtils · CorePasscode · CoreNetwork · FeatureAddiction*
└── FeatureMain    → CoreDesignSystem · CoreUtils · CorePasscode
    ├── FeatureSettings  → CoreDesignSystem · CoreUtils · CorePasscode
    ├── FeatureAddiction → CoreDesignSystem · CoreUtils · CoreNetwork
    └── FeatureAssistant → CoreDesignSystem · CoreUtils

CoreDesignSystem → CoreUtils
CorePasscode     → CoreDesignSystem · CoreUtils
CoreNetwork      (нет внутренних зависимостей)
CoreUtils        (нет внутренних зависимостей)
```

`*` FeatureWelcome использует `AddictionSelectionScreen` напрямую — единственное легализованное исключение из запрета на inter-feature зависимости.

**Запрет:** Feature-модули не импортируют друг друга, кроме перечисленного выше исключения.
**Запрет:** Core-модули не импортируют Feature-модули.
**Запрет:** CoreDesignSystem не импортирует Core-модули кроме `CoreUtils`.

---

## Запуск приложения

```
AnchorApplication.onCreate()
  └── initKoin(androidModule)          // регистрирует Context + BASE_URL
        └── sharedModule               // includes все Core + Feature модули
              └── AnchorViewModel      // читает AppSettings + PasscodeRepository

AnchorMainActivity → setContent { AnchorApp() }
  AnchorApp:
    Theme(darkTheme)                   // один раз, из AnchorViewModel.theme
    CompositionLocalProvider(LocalAppLocale provides language)   // без key()
    AppNavHost(startDestination = initialRoute):
      AnchorNavRoute.Welcome → WelcomeFlow(onFinished → navigate(Main, popUpTo inclusive))
      AnchorNavRoute.Main    → MainFlow(onLogout → navigate(Welcome, popUpTo inclusive))
```

**`initialRoute`** в `AnchorViewModel`:
- `isAuthorized && !hasPasscode` → `Main`
- иначе → `Welcome`

---

## Ключевые версии

Kotlin 2.3.21 · ComposeMP 1.10.3 · Navigation 2.9.2 · Koin 4.2.1 · Ktor 3.4.3
Coroutines 1.10.2 · Serialization 1.11.0 · multiplatform-settings 1.3.0
Lifecycle/ViewModel 2.10.0 · AGP 9.2.0 · compileSdk 37 / targetSdk 37 / minSdk 24

---

## MVI-паттерн (единый для всех Feature-модулей)

```
UiAction → ViewModel.emitAction() → _actionFlow (MutableSharedFlow)
  └── subscribeToActions():
        UiAction.Init → инициализация (isScreenInitialized guard)
        остальные     → обработка → _uiResult.update { ... }

_uiResult: MutableStateFlow<UiResult?>
uiState:   StateFlow<UiState> = _uiResult.map { ... }  — sealed: Init|Loading|Successful|Error
uiEvent:   SharedFlow<UiEvent>                          — одноразовые события
```

**Правила MVI:**
- `isScreenInitialized` guard в каждой ViewModel; повторный `initScreen()` — no-op
- `_actionFlow.onStart { emit(Init) }` обязателен; гарантирует доставку Init до внешних эмитов
- `viewModelScope.launch` + `CoroutineExceptionHandler` для всех сетевых операций
- `collectAsStateWithLifecycle()`, не `collectAsState()`
- Строки для UI (ошибки валидации, биометрия) — резолвятся в Composable, передаются через action-параметры

---

## DI (Koin 4.x)

```kotlin
sharedModule {
    includes(coreUtilsModule(AppIdentifier.ANCHOR))
    includes(corePasscodeModule)
    includes(coreNetworkModule)
    includes(featureMainModule)     // транзитивно включает Settings, Addiction, Assistant
    includes(featureWelcomeModule)  // включает Addiction повторно — Koin idempotent
    viewModel { AnchorViewModel(get()) }
}
```

`featureWelcomeModule` регистрирует слоистую авторизацию:
```kotlin
factory<AuthorizationApiMapper> { AuthorizationApiMapperImpl(apiClient = get()) }
single<AuthorizationRepository> { AuthorizationRepositoryImpl(apiMapper = get(), tokenStorage = get()) }
factory<AuthorizationInteractor> { AuthorizationInteractorImpl(repository = get()) }
viewModel { WelcomeViewModel(get(), get()) }        // AppSettings, PasscodeRepository
viewModel { AuthorizationViewModel(get(), get()) }  // AuthorizationInteractor, AppSettings
```

- `single<Interface>` — репозитории и интеракторы с состоянием (SharedFlow)
- `factory<Interface>` — интеракторы без состояния
- `named("qualifier")` — при конфликте одного типа (пример: `corePasscodeSettings`)
- `AppSettings` — ровно один `single<>` на весь граф

---

## Навигация

- Маршруты — `@Serializable` sealed interface / data object / data class (параметры экрана — поля класса)
- `AppNavHost` — один на уровень; **не вкладывать** в другой `AppNavHost`
- Под-графы — `NavGraphBuilder` extension-функции, не отдельный `AppNavHost`
- Очистка стека при смене корневого флоу: `popUpTo(startDestination) { inclusive = true }`
- `enableSwipeBack = false` — для WelcomeFlow и других флоу без back-навигации
- Параметры экрана передаются через поля `@Serializable data class` маршрута, не через `Bundle`/`SavedStateHandle`

---

## Дизайн-система

- Цвета: `Tokens.X.getThemedColor()`, `TokensColor.X.getThemedColor()` — никаких хардкодов
- Текст: `Title1`…`Caption2` из CoreDesignSystem — не `Text()` напрямую
- Каркас: `AppScaffold` — не Material3 `Scaffold`
- `Theme()` — один раз в `AnchorApp`; Feature не создают свой `Theme`
- `LocalAppLocale` — `CompositionLocalProvider` без `key()` (сбрасывает NavHost)
- `LoadingHUD` — фон `Tokens.HudBackground`; снаружи обязателен перехват кликов

---

## Сеть

```kotlin
apiClient.execute<ResponseType> {
    endpoint = "path/resource"
    method = HttpMethod.Post   // GET по умолчанию
    body = requestObject       // @Serializable
    query("key" to value)      // null-значения игнорируются автоматически
}

// Мутации без тела ответа (204 No Content):
apiClient.execute<Unit> {
    endpoint = "resource/$id"
    method = HttpMethod.Delete
}
```

- `NetworkException`: `BadRequest(error?) · Unauthorized · Forbidden(error?) · NotFound(error?) · Conflict(error?) · ServerError(code, error?) · HttpError(code, error?) · NoConnection · Unknown`
- `ErrorResponse(code: String, message: String)` — тело ошибки при 4xx/5xx; клиент не показывает `message` пользователю
- `ItemsResponse<T>(items: List<T>)` — конверт для списочных эндпоинтов
- HTTP status code — единственный источник истины об успехе/ошибке; тела `success/body/message` нет
- `TokenStorage` — публичный интерфейс CoreNetwork; Feature используют только его для сохранения токенов после авторизации. `TokenRepository` — internal, Feature-модулям недоступен.
- JWT refresh — автоматический через Ktor `bearer { }`; Mutex предотвращает гонку
- `BASE_URL` — из `BuildConfig.BASE_URL` → `local.properties`; не хардкодить

---

## Глобальные жёсткие правила

| # | Правило |
|---|---------|
| 1 | Feature не импортирует Feature (исключение: FeatureWelcome → FeatureAddiction) |
| 2 | Core не импортирует Feature |
| 3 | `AppNavHost` не вкладывается в `AppNavHost`; под-графы — NavGraphBuilder extension |
| 4 | `Theme()` — один раз в `AnchorApp`; `LocalAppLocale` — без `key()` |
| 5 | Все цвета через `Tokens`; весь текст через типографику CoreDesignSystem |
| 6 | `ApiClient` — только через DI; `BASE_URL` — только из `BuildConfig` |
| 7 | `AppSettings` — `single<>` в Koin; мутация только через `set*`-методы |
| 8 | Параметры экрана — в полях `@Serializable data class` маршрута |
| 9 | `collectAsStateWithLifecycle()` везде; `isScreenInitialized` guard в каждой ViewModel |
| 10 | Именование DTO: суффиксы `Request` / `Response`; не `Dto` |
| 11 | Feature получают `TokenStorage` из DI; прямой доступ к `TokenRepository` — запрещён |
