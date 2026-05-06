# FeatureWelcome

Флоу онбординга и авторизации (Email + OTP). KMP, commonMain only.
Stack: Compose MP · Jetpack Navigation · ViewModel · Koin 4.x · Coroutines · Napier.
Зависимости: `:Core:CoreDesignSystem`, `:Core:CoreUtils`, `:Core:CorePasscode`, `:Core:CoreNetwork`, `:Feature:FeatureAddiction`.

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
    @Serializable data class Passcode(val isCreation: Boolean, val isFirstAuthorized: Boolean = true) : WelcomeNavRoute
    @Serializable data object HabitSelection : WelcomeNavRoute
}
```

`AppNavHost(enableSwipeBack = false)` — свайп назад в Welcome-флоу отключён.
Все переходы используют `popUpTo(...) { inclusive = true }` — стек не накапливается.

| Событие | Откуда | Куда |
|---------|--------|------|
| `OnAuthorizedNewUser` (event) | Authorization | `Passcode(isCreation=true, isFirstAuthorized=true)` + `clearPasscode()` |
| `OnAuthorizedReturningUser` (event) | Authorization | `Passcode(isCreation=true, isFirstAuthorized=false)` + `clearPasscode()` |
| `onEnterSuccess` / `onSkipped`, `isFirstAuthorized=true` | Passcode(isCreation=true) | HabitSelection |
| `onEnterSuccess` / `onSkipped`, `isFirstAuthorized=false` | Passcode(isCreation=true) | `onFinished()` |
| `onEnterSuccess` | Passcode(isCreation=false) | `onFinished()` |
| `onForgotPasscode` | Passcode (любой) | Authorization + `setAuthorized(false)` |
| `onFinished` | HabitSelection (AddictionSelectionScreen) | `onFinished()` |

`WelcomeViewModel.initialRoute` — вычисляется один раз в `init`, не `StateFlow`:
- `isAuthorized && hasPasscode` → `Passcode(isCreation=false)`
- `isAuthorized && !hasPasscode` → `HabitSelection`
- иначе → `Authorization`

---

## Архитектура Data-слоя

```
AuthorizationViewModel
  └── AuthorizationInteractor          (factory, internal interface)
        └── AuthorizationRepository    (single, internal interface)
              ├── AuthorizationApiMapper (factory, internal interface) → ApiClient
              └── TokenStorage           (из CoreNetwork DI) — saveTokens после verifyOtp
```

`OtpException` (internal sealed class):
- `InvalidOtp` — HTTP 400, неверный код

Цепочка вызовов:
```
AuthorizationViewModel.onGetOtp()
  → interactor.sendOtp(email): String   // sessionId сохраняется в private var ViewModel

AuthorizationViewModel.onCheckOtp()
  → interactor.verifyOtp(sessionId, otp): Boolean
  → isFirstAuthorized: true → OnAuthorizedNewUser; false → OnAuthorizedReturningUser
  catch OtpException.InvalidOtp → pinState = Error

AuthorizationViewModel.onResendOtp()
  → interactor.resendOtp(sessionId)
```

---

## MVI — AuthorizationScreen

### UiResult

```kotlin
@Immutable
data class AuthorizationUiResult(
    val email: String = "",
    val isGetOtpEnabled: Boolean = false,   // isEmailValid(email)
    val isLoading: Boolean = false,
    val isError: Boolean = false,

    val otp: OtpUiResult = OtpUiResult()
)

@Immutable
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
| `OnEmailChanged(email)` | `isGetOtpEnabled = isEmailValid(email)`, сбрасывает `isError`/`sessionId` |
| `OnGetOtpClicked` | `isLoading=true` → `sendOtp()` → Sheet + `startTimer()` (или `isError=true`) |
| `OnTermsClicked` | `interactor.handleTermsClicked()` (заглушка) |
| `OnSheetVisibilityChange(isVisible)` | `true` → `startTimer()`; `false` → `stopTimer()` |
| `OnPinChange(pinCode)` | При `length == 5` → автоматически `checkOtp()` |
| `OnResendOtpClicked` | `resendOtp()` + сброс таймера на 60с + `startTimer()` |

### UiEvent

```kotlin
sealed interface AuthorizationUiEvent {
    data object OnAuthorizedNewUser      : AuthorizationUiEvent
    data object OnAuthorizedReturningUser : AuthorizationUiEvent
}
```

### Таймер

`timerJob: Job?` — единственный активный Job. Всегда `stopTimer()` перед `startTimer()`.
Запускается в `onGetOtp()` при успехе и в `OnSheetVisibilityChange(true)`.

**Детали ViewModel:** `actions: MutableSharedFlow(extraBufferCapacity=64)`, `tryEmit`.
`CoroutineExceptionHandler` → `isLoading=false`, `isError=true`.
`appSettings.setAuthorized(true)` — ТОЛЬКО в `onCheckOtp()`.
`sessionId: String` — private var; передаётся в каждый вызов interactor явно.

---

## DI

```kotlin
val featureWelcomeModule = module {
    includes(featureAddictionModule)
    factory<AuthorizationApiMapper> { AuthorizationApiMapperImpl(apiClient = get()) }
    single<AuthorizationRepository> { AuthorizationRepositoryImpl(apiMapper = get(), tokenStorage = get()) }
    factory<AuthorizationInteractor> { AuthorizationInteractorImpl(repository = get()) }
    viewModel { WelcomeViewModel(get(), get()) }        // AppSettings, PasscodeRepository
    viewModel { AuthorizationViewModel(get(), get()) }  // AuthorizationInteractor, AppSettings
}
```

Внешние зависимости из глобального Koin: `AppSettings` (CoreUtils), `PasscodeRepository` (CorePasscode), `ApiClient` (CoreNetwork), `TokenStorage` (CoreNetwork).

---

## Файловая карта

```
data/mapper/    AuthorizationApiMapper (interface) + Impl — HTTP-запросы через ApiClient
data/repository/ AuthorizationRepository (interface) + Impl — маппинг NetworkException → OtpException; saveTokens после verifyOtp
domain/         OtpException (sealed: InvalidOtp)
domain/interactor/ AuthorizationInteractor (interface) + Impl — делегирование в Repository
models/data/    OtpSendRequest/Response · OtpVerifyRequest/Response · OtpResendRequest
models/presentation/authorization/  AuthorizationUiAction · UiEvent · UiResult (не internal)
navigation/     WelcomeNavRoute (internal @Serializable sealed)
presentation/   WelcomeFlow · WelcomeViewModel · Utils.isEmailValid()
presentation/authorization/ AuthorizationScreen · AuthorizationViewModel
di/             FeatureWelcomeModule
composeResources/ drawable/ic_cross · values/strings.xml (EN) · values-ru/strings.xml (RU)
```

`OtpVerifyResponse` (flat): `accessToken`, `refreshToken`, `isFirstAuthorized`.
`OtpSendResponse` (flat): `sessionId`.

## Строковые ресурсы

`authorization_header_title` · `authorization_body_subtitle` · `authorization_textinput_placeholder` ·
`authorization_button_get_otp` · `authorization_bottomsheet_title` · `authorization_bottomsheet_footnote` ·
`authorization_footer_terms` · `authorization_resend_button` · `authorization_timer_text` · `authorization_error_otp`.

## Жёсткие правила

| # | Правило |
|---|---------|
| 1 | `WelcomeFlow` — единственная публичная точка входа; всё остальное `internal` |
| 2 | `WelcomeViewModel.initialRoute` — вычисляется один раз в `init`; не `StateFlow`; не пересчитывать |
| 3 | `AppNavHost(enableSwipeBack = false)` — свайп назад в Welcome-флоу отключён |
| 4 | Все переходы с `popUpTo(...) { inclusive = true }` — стек не накапливается |
| 5 | `timerJob` — всегда `stopTimer()` перед `startTimer()`; только один активный Job |
| 6 | `appSettings.setAuthorized(true)` — только в `onCheckOtp()` при успехе; нигде больше |
| 7 | `passcodeRepository.clearPasscode()` — вызывается в `WelcomeFlow` при переходе `Authorization → Passcode` |
| 8 | `AuthorizationUiAction` / `AuthorizationUiResult` / `AuthorizationUiEvent` — не `internal` |
| 9 | `collectAsStateWithLifecycle()`, не `collectAsState()` |
| 10 | `TokenStorage` — внедряется в `AuthorizationRepositoryImpl`; `TokenRepository` — запрещён в Feature |
