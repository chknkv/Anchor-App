# FeatureWelcome

Флоу онбординга и авторизации (Email + OTP). KMP, commonMain only.
Stack: Compose MP · Jetpack Navigation · ViewModel · Koin 4.x · Coroutines · Napier.
Зависимости: `:Core:CoreDesignSystem`, `:Core:CoreUtils`, `:Core:CorePasscode`, `:Feature:FeatureAddiction`.

---

## Публичный API

```kotlin
// com.chknkv.feature.welcome.presentation
@Composable
fun WelcomeFlow(onFinished: () -> Unit)

// com.chknkv.feature.welcome.di
val featureWelcomeModule: org.koin.core.module.Module
```

`WelcomeFlow` — единственная точка входа. Всё остальное `internal`.

---

## Навигация

```kotlin
@Serializable
internal sealed interface WelcomeNavRoute {
    @Serializable data object Authorization : WelcomeNavRoute
    @Serializable data class Passcode(val isCreation: Boolean) : WelcomeNavRoute
    @Serializable data object HabitSelection : WelcomeNavRoute
}
```

`AppNavHost(enableSwipeBack = false)` — свайп назад в Welcome-флоу отключён.

Все переходы используют `popUpTo(...) { inclusive = true }` — стек не накапливается.

| Событие | Откуда | Куда |
|---------|--------|------|
| `OnAuthorized` (event) | Authorization | `Passcode(isCreation=true)` + `clearPasscode()` |
| `onEnterSuccess` / `onSkipped` | Passcode(isCreation=true) | HabitSelection |
| `onEnterSuccess` | Passcode(isCreation=false) | `onFinished()` |
| `onForgotPasscode` | Passcode (любой) | Authorization + `setAuthorized(false)` |
| `onFinished` | HabitSelection (AddictionSelectionScreen) | `onFinished()` |

`WelcomeViewModel.initialRoute` — вычисляется один раз в `init`, не `StateFlow`:
- `isAuthorized && hasPasscode` → `Passcode(isCreation=false)`
- `isAuthorized && !hasPasscode` → `HabitSelection`
- иначе → `Authorization`

---

## MVI — AuthorizationScreen

### UiState (`AuthorizationUiResult`)

```kotlin
data class AuthorizationUiResult(
    val email: String = "",
    val isGetOtpEnabled: Boolean = false,   // isEmailValid(email)
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val otp: OtpUiResult = OtpUiResult()
)

data class OtpUiResult(
    val pinCode: String = "",
    val pinState: PinInputState = PinInputState.Input,  // Input | Loading | Error
    val isSheetVisible: Boolean = false,
    val timerValue: Int = 60,
    val isResendAvailable: Boolean = false  // true когда timerValue == 0
)
```

### UiAction

| Action | Описание |
|--------|----------|
| `OnEmailChanged(email)` | Обновляет `isGetOtpEnabled = isEmailValid(email)`, `isError = false` |
| `OnGetOtpClicked` | `isLoading=true` → `handleGetOtp()` → открывает Sheet + `startTimer()` (или `isError=true`) |
| `OnTermsClicked` | `interactor.handleTermsClicked()` (заглушка) |
| `OnAuthorizedClicked` | Эмитит `OnAuthorized` event напрямую |
| `OnSheetVisibilityChange(isVisible)` | Управляет видимостью Sheet; `true` → `startTimer()`, `false` → `stopTimer()` |
| `OnPinChange(pinCode)` | Ввод OTP; при `length == 5` → автоматически вызывает `checkOtp()` |
| `OnResendOtpClicked` | `resendOtp()` + сброс таймера на 60с + `startTimer()` |
| `OnTimerTick` | Внутренний — декремент таймера; при `timerValue == 0` → `isResendAvailable=true` |

### UiEvent

```kotlin
sealed interface AuthorizationUiEvent {
    data object OnAuthorized : AuthorizationUiEvent
}
```

### Таймер

`timerJob: Job?` — единственный активный Job. Всегда `stopTimer()` перед `startTimer()`.
Запускается в двух местах: в `onGetOtp()` при успехе и в `OnSheetVisibilityChange(true)`.

### Важные детали `AuthorizationViewModel`

- `actions: MutableSharedFlow(extraBufferCapacity=64)` — `tryEmit` (не `launch { emit }`)
- `CoroutineExceptionHandler` → `isLoading=false`, `isError=true`, логирует через Napier
- `appSettings.setAuthorized(true)` — ТОЛЬКО в `onCheckOtp()` при успехе
- `LaunchedEffect(viewModel)` в Screen — коллект `uiEvent` без утечки

---

## DI

```kotlin
val featureWelcomeModule = module {
    includes(featureAddictionModule)

    factory<AuthorizationInteractor> { AuthorizationInteractorImpl() }

    viewModel { WelcomeViewModel(get(), get()) }        // AppSettings, PasscodeRepository
    viewModel { AuthorizationViewModel(get(), get()) }  // AuthorizationInteractor, AppSettings
}
```

Внешние зависимости из глобального Koin: `AppSettings` (CoreUtils), `PasscodeRepository` (CorePasscode).

---

## Файловая карта

```
src/commonMain/kotlin/com/chknkv/feature/welcome/
├── di/
│   └── FeatureWelcomeModule.kt            — val featureWelcomeModule (публичный)
├── domain/
│   └── AuthorizationInteractor.kt         — interface + Impl (MOCK: тестовые email/OTP)
├── models/presentation/authorization/
│   ├── AuthorizationUiAction.kt           — sealed interface (не internal)
│   ├── AuthorizationUiEvent.kt            — sealed interface (не internal)
│   └── AuthorizationUiResult.kt           — data class + OtpUiResult (не internal)
├── navigation/
│   └── WelcomeNavRoute.kt                 — internal sealed interface (@Serializable)
└── presentation/
    ├── WelcomeFlow.kt                     — публичная точка входа; NavController
    ├── WelcomeViewModel.kt                — internal; только initialRoute
    ├── Utils.kt                           — fun isEmailValid(email: String): Boolean (не internal)
    └── authorization/
        ├── AuthorizationScreen.kt         — @Composable публичный; AppScaffold без топбара
        └── AuthorizationViewModel.kt      — internal; MVI + таймер

src/commonMain/composeResources/
├── drawable/  ic_cross
└── values/    strings.xml (EN) + values-ru/strings.xml (RU)
```

---

## Строковые ресурсы (EN + RU)

`authorization_header_title`, `authorization_body_subtitle`, `authorization_textinput_placeholder`,
`authorization_button_get_otp`, `authorization_bottomsheet_title`, `authorization_bottomsheet_footnote`,
`authorization_footer_terms`, `authorization_resend_button`, `authorization_timer_text`, `authorization_error_otp`.

Drawable: `ic_cross`.

---

## Жёсткие правила

| # | Правило |
|---|---------|
| 1 | `WelcomeFlow` — единственная публичная точка входа; всё остальное `internal` |
| 2 | `WelcomeViewModel.initialRoute` — вычисляется один раз в `init`; не `StateFlow`; не пересчитывать |
| 3 | `AppNavHost(enableSwipeBack = false)` — свайп назад в Welcome-флоу отключён; не менять без решения |
| 4 | Все переходы с `popUpTo(...) { inclusive = true }` — стек не накапливается; сохранять на новых переходах |
| 5 | `timerJob` — всегда `stopTimer()` перед `startTimer()`; только один активный Job |
| 6 | `appSettings.setAuthorized(true)` — только в `onCheckOtp()` при успехе; нигде больше |
| 7 | `passcodeRepository.clearPasscode()` — вызывается в `WelcomeFlow` при переходе `Authorization → Passcode(isCreation=true)` |
| 8 | `AuthorizationUiAction` / `AuthorizationUiResult` — не `internal` (импортируются из `AuthorizationScreen`) |
| 9 | `collectAsStateWithLifecycle()`, не `collectAsState()` |
| 10 | Feature-модуль не импортирует другие Feature-модули напрямую; `FeatureAddiction` подключается через `featureAddictionModule` в DI и `AddictionSelectionScreen` в `WelcomeFlow` |
