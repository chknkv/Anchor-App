# CorePasscode

KMP-модуль: создание, ввод и смена passcode + биометрия.
Stack: Compose MP · Jetpack Navigation Multiplatform · ViewModel · Koin 4.x · multiplatform-settings.
Зависимости: `:Core:CoreDesignSystem`, `:Core:CoreUtils`. **Запрет:** не импортирует Feature-модули.

---

## Публичный API

```kotlin
@Composable
fun PasscodeFlow(
    mode: PasscodeFlowMode,          // Enter | Change
    onBack: (() -> Unit)? = null,    // null → кнопка «Назад» скрыта, системный back заблокирован
    onEnterSuccess: () -> Unit,
    onChangeSuccess: () -> Unit = {},
    onSkipped: () -> Unit = {},
    onForgotPasscode: () -> Unit = {},
    topPadding: Dp = 32.dp,          // 0.dp из Settings (нет gap статус-бара)
)
```

`onBack = null` — обязательный флоу входа (запуск приложения).
`onBack != null` — флоу смены из настроек, выход допустим.

---

## Режимы и маршруты

| Mode | Есть passcode | Нет passcode |
|------|--------------|-------------|
| `Enter` | Enter → `onEnterSuccess()` | Create → BiometrySetup? → `onEnterSuccess()` |
| `Change` | Enter → Create(isChange=true) → `onChangeSuccess()` | Create(isChange=true) → `onChangeSuccess()` |

```
Enter:  hasPasscode → [Enter] ──ok──► onEnterSuccess()
                               └─forgot──► clearPasscode() → onForgotPasscode()
        no passcode → [Create] ──created──► isAvailable? [Biometry] ──► onEnterSuccess()
                               └─skipped──► onSkipped()
Change: hasPasscode → [Enter] ──ok──► [Create(isChange=true)] ──created──► onChangeSuccess()
        no passcode → [Create(isChange=true)] ──created──► onChangeSuccess()
```

Внутренние маршруты (`internal`): `PasscodeNavRoute.Enter · Create(isChange) · Biometry`.
Навигация через `popUpTo(startDestinationId) { inclusive = true }` — стек не накапливается.

---

## Контроль системного back

`PasscodeBackHandler` — `expect/actual`: Android → `BackHandler(enabled=true)`; iOS → no-op.

`PasscodeFlow` создаёт **собственный** `AppNavHost` (вложенный). Вставлять `PasscodeFlow` только как конечный маршрут в родительском навграфе — не внутри другого вложенного NavHost. Когда внутренний стек пуст, `PredictiveBackWrapper` не срабатывает и управление получает внешний NavHost.

---

## DI

```kotlin
startKoin { modules(coreUtilsModule(AppIdentifier.ANCHOR), corePasscodeModule) }
```

`corePasscodeModule` регистрирует:
- `single(named("corePasscodeSettings")) { Settings() }` — изолированное хранилище
- `single<PasscodeRepository> { PasscodeRepositoryImpl(...) }`
- `viewModel { CreatePasscodeViewModel(get()) }`
- `viewModel { EnterPasscodeViewModel(get(), get()) }`
- `viewModel { BiometrySetupViewModel(get(), get()) }`
- `includes(platformPasscodeModule)` → `BiometricAuthenticator` (android/ios actual)

---

## MVI — контракты экранов

### CreatePasscode

**UiState:** `enteredDigits: List<Int>` · `isConfirming: Boolean` · `isSkipAlertVisible: Boolean` · `isSkipAvailable: Boolean` · `shakeTrigger: Int`
**UiAction:** `Init · NumberClick(digit) · DeleteClick · ShowSkipAlert · DismissSkipAlert · Skip`
**UiEvent:** `PasscodesDoNotMatch · PasscodeCreated · SkipRequested`
**initScreen(isChangeFlow: Boolean)** — вызов из `LaunchedEffect(Unit)`.

Двухшаговый флоу: enter → confirm. На несовпадении: `shakeTrigger++` → shake-анимация, сброс к шагу 1.

### EnterPasscode

**UiState:** `enteredDigits` · `isError` · `isForgotAlertVisible` · `isBiometricAvailable` · `biometricType: BiometricType` · `isChangeFlow: Boolean` · `shakeTrigger: Int`
**UiAction:** `Init · NumberClick(digit) · DeleteClick · ShowForgotAlert · HideForgotAlert · ForgotPasscode · TryBiometric`
**UiEvent:** `InvalidPasscode · EnterSuccess · ForgotPasscodeRequested`
**initScreen(isChangeFlow, biometricContext, biometryTitle, biometryReason, biometryCancel)**

Строки биометрии вычисляются в Composable через `stringResource()` **до** `LaunchedEffect` — ViewModel не имеет доступа к Compose-контексту.

### BiometrySetup

**UiState:** `biometricType: BiometricType` · `isLoading: Boolean`
**UiAction:** `Init · Enable · Skip`
**UiEvent:** `BiometricEnabled · BiometricFailed(reason: String) · SetupFinished`
**initScreen(biometricContext, biometryTitle, biometryReason, biometryCancel)**

---

## Биометрия

| BiometricType | Иконка | Условие показа |
|--------------|--------|----------------|
| `TOUCH_ID` | `ic_fingerprint` | `isBiometricAvailable && filledCount == 0` |
| `FACE_ID`, `FACE_ANDROID` | `ic_face` | то же |
| `NONE` | скрыто | — |

`BiometricContext` — `expect`-класс; Android: `FragmentActivity`, iOS: маркер без полей.
`rememberBiometricContext()` — `@Composable expect`. Android требует `AppCompatActivity`.
`BiometricResult`: `Success | Cancelled | Error(reason)`.

---

## Временны́е константы

| Константа | Значение | Место |
|-----------|----------|-------|
| `AUTO_SUBMIT_DELAY_MS` | 150 ms | `CreatePasscodeViewModel` |
| `AUTO_CHECK_DELAY_MS` | 200 ms | `EnterPasscodeViewModel` |
| `AUTO_BIOMETRIC_DELAY_MS` | 300 ms | `EnterPasscodeViewModel` — задержка авто-запуска биометрии |
| `SHAKE_DURATION_MS` | 400 ms | `CreatePasscodeViewModel` **и** `durationMillis` в Screen — обязаны совпадать |

---

## Строковые ресурсы

```
create_passcode_step1_title / step2_title / subtitle / skip /
  skip_alert_title / skip_alert_subtitle / skip_alert_confirm / skip_alert_cancel
enter_passcode_title / title_change / subtitle / subtitle_change /
  forgot / forgot_alert_title / forgot_alert_subtitle / forgot_alert_confirm / forgot_alert_cancel
biometry_title / subtitle / enable / skip /
  face_id_title / touch_id_title / biometry_prompt_reason / biometry_prompt_cancel
```

Локализации: `values/` (EN) · `values-ru/` (RU).

---

## Файловая карта

```
commonMain/
├── PasscodeFlow.kt · PasscodeFlowMode.kt
├── navigation/  PasscodeNavRoute.kt         # internal @Serializable sealed interface
├── domain/      PasscodeRepository.kt · BiometricAuthenticator.kt (expect) · HashPasscode.kt (expect)
├── models/
│   ├── domain/       BiometricContext (expect) · BiometricResult · BiometricType
│   └── presentation/ createpasscode/ · enterpasscode/ · biometrysetup/  → UiAction·UiResult·UiState·UiEffect
├── presentation/
│   ├── PasscodeBackHandler.kt (expect) · PasscodeLengths.kt · BiometricContextComposable.kt (expect)
│   ├── alert/    PasscodeAlert.kt
│   ├── keyboard/ PasscodeIndicatorWithShake.kt
│   ├── createpasscode/ · enterpasscode/ · biometrysetup/  → Screen + ViewModel
└── di/  PasscodeModule.kt
androidMain/ → Locale · BiometricAuthenticator · HashPasscode · BiometricContext · Composables (actual)
iosMain/     → то же (iOS actual: LAContext / CC_SHA256 / no-op BackHandler)
```

---

## Жёсткие правила

| # | Правило |
|---|---------|
| 1 | `PASSCODE_LENGTH = 5` — `internal` константа; не хардкодить `5` |
| 2 | `SHAKE_DURATION_MS = 400` в ViewModel **обязан** совпадать с `durationMillis` в Screen |
| 3 | `authenticate()` — только на Main dispatcher; запускать через `viewModelScope.launch` |
| 4 | Строки биометрии — `String`, не `suspend`; резолвятся в Composable до `LaunchedEffect` |
| 5 | `Settings()` — всегда с `named("corePasscodeSettings")`; без него коллизия в Koin |
| 6 | `isAvailable() == true` ⟹ `availableType() != NONE` — инвариант; не нарушать |
| 7 | `biometricJob?.isActive == true` — guard в `tryBiometric()`; не удалять |
| 8 | Навигация — `popUpTo(startDestinationId) { inclusive = true }` (стек не накапливается) |
| 9 | `isScreenInitialized` — флаг в каждой ViewModel; повторный вызов `initScreen()` — no-op |
| 10 | `PasscodeFlow` вставлять только как конечный маршрут; не вкладывать в другой `AppNavHost` |

---

## Добавление нового экрана

1. `models/presentation/<name>/` — `*UiAction · *UiResult · *UiState · *UiEffect`
2. `presentation/<name>/*ViewModel.kt` — `initScreen()` + `emitAction()` + `subscribeToActions()`; `isScreenInitialized`-guard
3. `presentation/<name>/*Screen.kt` — `koinViewModel<>()`, `LaunchedEffect(Unit) { vm.initScreen(...) }`, `LaunchedEffect(vm) { vm.uiEvent.collect { ... } }`
4. `PasscodeNavRoute.kt` — добавить `@Serializable` объект/класс
5. `PasscodeModule.kt` — `viewModel { NewViewModel(get()) }`
6. `PasscodeFlow.kt` — добавить `composable<PasscodeNavRoute.New>` в `AppNavHost`; при необходимости обновить `initialRoute`
