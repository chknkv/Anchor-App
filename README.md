## Anchor — Mobile App

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.3.21-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Compose%20Multiplatform-1.10.3-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" alt="Compose Multiplatform" />
  <img src="https://img.shields.io/badge/Android-24%2B-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Android 24+" />
  <img src="https://img.shields.io/badge/iOS-15%2B-000000?style=flat-square&logo=apple&logoColor=white" alt="iOS 15+" />
  <img src="https://img.shields.io/badge/KMP-multiplatform-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="KMP" />
</p>

---

> **Master's thesis.** This project was developed and defended in June 2026 at RTU MIREA as part of a Master's degree program in Mobile Development. Grade: _to be updated._

### What is Anchor?

**Anchor** is a cross-platform mobile app for habit tracking — both building positive habits and breaking negative ones. It supports the full habit lifecycle: discovering what you want to change, committing to a daily routine, tracking your progress over time, and staying motivated.

The app works in two directions at once. You can add habits you want to form — morning runs, meditation, reading — and habits you want to quit, such as smoking or excessive phone use. Each habit is tracked daily: one tap marks today as done, and the app records the full completion history with a visual activity calendar.

On the main screen, alongside your habit list, **Anchor** shows a daily motivational quote fetched from the server — a small push to keep you going when willpower runs low.

**Who it's for.** Anyone who wants to take deliberate control of their daily routine. The interface is minimal and opinionated: no gamification noise, no subscription walls on core functionality, no unnecessary friction between you and your habits.

**Authentication** is passwordless — email plus a one-time code (OTP). No password to forget. After the first login, the app guides you through selecting up to three starter habits grouped by category (sport, health, productivity, lifestyle, finance, relationships), then takes you straight to the main screen. All data is synced with a dedicated REST API backend.

**Security.** Access to the app can be protected with a 5-digit passcode and biometric authentication (fingerprint or Face ID). The passcode module is built into the core layer and shared across Android and iOS through Kotlin Multiplatform.

**Customization.** The app supports light, dark, and system-adaptive themes, and is fully localized in English and Russian. Language and theme can be changed at any time in settings.

Anchor is **open source**. The full client source is available on GitHub. The REST API backend was developed by a project co-author as part of the same thesis and is not included in this repository.

### Technical overview

#### Build and run

**Prerequisites**

- **Android:** Android SDK, `ANDROID_HOME` set (or SDK at `~/Library/Android/sdk` on macOS). A device or emulator with API 24+ and USB debugging enabled.
- **iOS:** Xcode, iOS Simulator or a physical device running iOS 15+. For device builds, code signing must be configured.

**Commands**

From the project root:

```bash
# Android — build, install, and launch on connected device/emulator
./scripts/android-run.sh

# iOS — build, install, and launch on Simulator or connected device
./scripts/ios-run.sh
```

The scripts use Gradle for the shared Kotlin framework and, on iOS, invoke `xcodebuild` for the app. They select a single device or simulator automatically if multiple are available.

---

#### Tech stack

|   | Area | Technologies |
|---|------|--------------|
| 🧩 | **Language & multiplatform** | [Kotlin 2.3.21](https://kotlinlang.org), [Kotlin Multiplatform (KMP)](https://kotlinlang.org/docs/multiplatform.html) |
| 🎨 | **UI** | [Compose Multiplatform 1.10.3](https://www.jetbrains.com/compose-multiplatform/) |
| 🧭 | **Navigation** | [Jetpack Navigation 2.9.2](https://developer.android.com/jetpack/compose/navigation) |
| ⚙️ | **DI** | [Koin 4.2.1](https://insert-koin.io/) |
| 🌐 | **Networking** | [Ktor 3.4.3](https://ktor.io/) — HTTP client, Bearer JWT, automatic token refresh |
| 🔐 | **Security** | 5-digit passcode, biometrics (fingerprint / Face ID), JWT token storage |
| 📦 | **Serialization** | [kotlinx.serialization 1.11.0](https://github.com/Kotlin/kotlinx.serialization) |
| ⚡ | **Async** | [kotlinx.coroutines 1.10.2](https://github.com/Kotlin/kotlinx.coroutines) — Flow, StateFlow, SharedFlow |
| 🗂️ | **Persistence** | [multiplatform-settings 1.3.0](https://github.com/russhwolf/multiplatform-settings) |
| 🏗️ | **Architecture** | MVI (UiAction → UiResult → UiState / UiEvent), Clean Architecture, Gradle multi-module |
| 📝 | **Logging** | [Napier](https://github.com/AAkira/Napier) |

---

#### Project structure

```
Anchor-App/
├── Anchor-MobileApp/
│   ├── androidApp/         # Android application module (Application + MainActivity)
│   └── shared/             # KMP entry point: AnchorApp, AnchorViewModel, top-level DI assembly
│
├── Core/
│   ├── CoreUtils/          # AppSettings (theme / language / auth state), getAppVersion(), openUrl()
│   ├── CoreDesignSystem/   # Compose UI components, design tokens, typography, AppScaffold, AppNavHost
│   ├── CoreNetwork/        # Ktor ApiClient, JWT refresh with Mutex, NetworkException, TokenStorage
│   └── CorePasscode/       # PasscodeFlow, biometric authentication, passcode repository
│
├── Feature/
│   ├── FeatureWelcome/     # Auth flow: email → OTP → passcode creation → habit selection
│   ├── FeatureMain/        # Main screen aggregator: AssistanceWidget + habit list + navigation
│   ├── FeatureSettings/    # Theme / language / passcode change / logout
│   ├── FeatureAddiction/   # Habit CRUD: onboarding selection, full list, create, details, edit, delete
│   └── FeatureAssistant/   # Motivational quote widget (daily quote card + bottom sheet)
│
├── docs/                   # Privacy Policy and Terms of Use (EN + RU)
└── scripts/                # Build & run helpers (android-run.sh, ios-run.sh)
```

**Module dependency rules (enforced):**
- Feature modules do not import other Feature modules (one exception: `FeatureWelcome` uses `FeatureAddiction`'s selection screen during onboarding)
- Core modules do not import Feature modules
- `CoreDesignSystem` only imports `CoreUtils` among Core modules

---

#### Other technical details

**Architecture.** Every Feature module follows a strict MVI contract: `UiAction` is emitted by the UI, processed by a `ViewModel` into `UiResult` (a plain data class), and exposed as a sealed `UiState` (Init / Loading / Successful / Error). One-shot navigation and side-effect events are delivered via `SharedFlow<UiEvent>`. All UI state is collected with `collectAsStateWithLifecycle()`.

**Networking.** All HTTP communication goes through a single `ApiClient` in `CoreNetwork`, built on Ktor. HTTP status codes are the sole source of truth for success or failure — there is no `success` flag in response bodies. Token refresh is fully automatic: on a `401`, the client calls `auth/refresh` under a `Mutex`, then replays the original request. All errors surface as typed `NetworkException` subtypes (`Unauthorized`, `NotFound`, `Conflict`, etc.).

**Backend-Driven UI (BDUI).** Habit icons, gradient colors, and categories are driven by server-provided string keys (e.g. `"gradient_key": "orange"`, `"icon_key": "ic_habit_sport"`). The client maps these keys to visual components at render time. This allows the backend to evolve the habit catalog without requiring a client update.

**Security.** The passcode module tracks consecutive failed attempts and resets automatically after a configurable threshold. Biometric authentication (fingerprint / Face ID) is available as an alternative to the passcode on both platforms. Tokens are stored in platform-secure storage via `TokenStorage` — the public interface of `CoreNetwork`; Feature modules never access the underlying token repository directly.

**Localization.** English and Russian are supported throughout. String resources live in each Feature module's `composeResources/`. Language can be switched at runtime from Settings without restarting the app.

**Build configuration.** `BASE_URL` is read from `local.properties` via `BuildConfig` and is never hardcoded. Android release builds have minification and resource shrinking enabled; ProGuard rules are maintained per module.

---

<p align="center">
  If you find Anchor useful, consider giving the repo a ⭐ on GitHub.
</p>

---
