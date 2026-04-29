# FeatureSettings

Флоу настроек приложения: тема, язык, смена passcode, выход из аккаунта.
KMP, commonMain only. Зависимости: CoreDesignSystem, CoreUtils, CorePasscode.

---

## Публичный API

```kotlin
// com.chknkv.feature.settings.presentation
val settingsStartRoute: Any = SettingsNavRoute.Main

fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    onBack: () -> Unit,
    onLogout: () -> Unit,
)
```

`settingsGraph` регистрирует все маршруты **напрямую в родительском NavHost** через `composable<>` —
без вложенного `NavHost`. Логика разлогина (setAuthorized(false)) вызывается внутри `settingsGraph`,
до вызова `onLogout()`.

---

## Навигация

```
SettingsNavRoute (internal @Serializable sealed interface)
  ├── Main         — список настроек
  ├── Appearance   — выбор темы
  ├── Language     — выбор языка
  └── PasscodeFlow — CorePasscode с режимом PasscodeFlowMode.Change
```

| Событие | Откуда | Результат |
|---------|--------|-----------|
| `OpenThemeSettings` uiEvent | Main | navigate(Appearance) |
| `OpenLanguageSettings` uiEvent | Main | navigate(Language) |
| `OpenPrivacySettings` uiEvent | Main | navigate(PasscodeFlow) |
| `NavigateBack` uiEvent | Main | onBack() коллбэк |
| `Logout` uiEvent | Main | setAuthorized(false) → onLogout() |
| onBack | Appearance / Language / PasscodeFlow | popBackStack() → Main |
| onForgotPasscode | PasscodeFlow | setAuthorized(false) → onLogout() |

---

## MVI — контракты экранов

### MainSettingsScreen

```kotlin
// UiState
sealed interface MainSettingsUiState {
    data class Successful(val result: SettingsUiResult) : MainSettingsUiState
}

data class SettingsUiResult(
    val modules: List<SettingsModuleModel>,
    val isLogoutConfirmVisible: Boolean = false,
)

data class SettingsModuleModel(
    val items: List<SettingCellModel>,
    val description: StringResource? = null,
)

data class SettingCellModel(
    val title: String? = null,
    val subtitle: String? = null,
    val titleRes: StringResource? = null,
    val subtitleRes: StringResource? = null,
    val iconRes: DrawableResource? = null,
    val iconGradient: TokensGradient? = null,
    val action: SettingsUiAction? = null,
    val isChevron: Boolean = action != null,
    val isWarning: Boolean = false,
)

// UiAction
sealed interface SettingsUiAction

sealed interface SettingsNavigationAction : SettingsUiAction {
    data object OpenThemeSettings : SettingsNavigationAction
    data object OpenLanguageSettings : SettingsNavigationAction
    data object OpenPrivacySettings : SettingsNavigationAction
    data object NavigateBack : SettingsNavigationAction
    data object Logout : SettingsNavigationAction
}

sealed interface SettingsLogoutAction : SettingsUiAction {
    data object ShowLogoutConfirm : SettingsLogoutAction
    data object HideLogoutConfirm : SettingsLogoutAction
}

// UiEvent (SharedFlow)
SharedFlow<SettingsNavigationAction>  // только навигационные действия, logout — тоже здесь
```

**MainSettingsViewModel:**
- `init { combine(theme, language).collect { refreshState() } }` — авто-обновление списка при смене темы/языка
- `SettingsLogoutAction` обрабатывается локально (флаг + refreshState), не попадает в uiEvent
- `SettingsNavigationAction` → emit в uiEvent → Screen вызывает коллбэк

### AppearanceScreen

```kotlin
// StateFlow из interactor.theme, нет отдельного UiState
val theme: StateFlow<AppTheme>  // SYSTEM | LIGHT | DARK
fun setTheme(theme: AppTheme)
```

CellPicker × 3: System / Light / Dark.

### LanguageScreen

```kotlin
val language: StateFlow<AppLanguage>  // RUSSIAN | ENGLISH
fun setLanguage(language: AppLanguage)
```

CellPicker × 2: Русский / English.

---

## SettingsInteractor

```kotlin
interface SettingsInteractor {
    val theme: StateFlow<AppTheme>
    val language: StateFlow<AppLanguage>
    fun setTheme(theme: AppTheme)
    fun setLanguage(language: AppLanguage)
    fun setAuthorized(isAuthorized: Boolean)
    fun buildSettingsResult(isLogoutConfirmVisible: Boolean): SettingsUiResult
}
```

`buildSettingsResult` формирует три модуля:
1. Premium (ic_star, TokensGradient.Purple) — заглушка, без action
2. Настройки (Appearance/Language/Privacy/Version) — с subtitleRes текущего значения
3. Logout (isWarning=true, без иконки) — action = ShowLogoutConfirm

Версия подтягивается через `getAppVersion()` из CoreUtils.

---

## DI

```kotlin
val featureSettingsModule = module {
    single<SettingsInteractor> { SettingsInteractorImpl(get()) }  // get() = AppSettings
    viewModel { MainSettingsViewModel(get()) }
    viewModel { AppearanceViewModel(get()) }
    viewModel { LanguageViewModel(get()) }
}
```

Подключение: `includes(featureSettingsModule)` в `featureMainModule` (FeatureMain).

## Файловая карта

```
src/commonMain/kotlin/com/chknkv/feature/settings/
├── di/FeatureSettingsModule.kt              — Koin: single + 3× viewModel
├── domain/SettingsInteractor.kt             — интерфейс + impl; строит SettingsUiResult
├── models/presentation/
│   ├── MainSettingsUiState.kt               — sealed: Successful(SettingsUiResult)
│   ├── SettingsUiAction.kt                  — SettingsNavigationAction | SettingsLogoutAction
│   └── SettingsUiResult.kt                  — SettingsUiResult + SettingsModuleModel + SettingCellModel
├── navigation/SettingsNavRoute.kt           — internal @Serializable sealed interface
└── presentation/
    ├── SettingsFlow.kt                      — ПУБЛИЧНЫЕ: settingsStartRoute + settingsGraph
    ├── appearance/AppearanceViewModel.kt    — theme: StateFlow, setTheme()
    ├── appearance/AppearanceScreen.kt       — CellPicker × 3
    ├── language/LanguageViewModel.kt        — language: StateFlow, setLanguage()
    ├── language/LanguageScreen.kt           — CellPicker × 2
    ├── main/MainSettingsViewModel.kt        — combine(theme,language), uiState + uiEvent
    └── main/MainSettingsScreen.kt           — LazyColumn модулей + Sheet подтверждения logout

composeResources/
├── drawable/  ic_appearance, ic_language, ic_lighter, ic_lock, ic_star
└── values/strings.xml + values-ru/strings.xml  — префикс settings_*
```

---

## Жёсткие правила

| # | Правило |
|---|---------|
| 1 | Не создавать вложенный `NavHost` внутри `settingsGraph` — только `composable<Route>` |
| 2 | `SettingsInteractor` регистрируется как `single<>`, не `factory` |
| 3 | Экраны обращаются к теме/языку только через `SettingsInteractor`, не через `AppSettings` напрямую |
| 4 | `buildSettingsResult()` вызывается только из `MainSettingsViewModel.refreshState()` |
| 5 | `koinInject<SettingsInteractor>()` в `settingsGraph` — внутри `composable {}` (`@Composable`-контекст) |
| 6 | `collectAsStateWithLifecycle()`, не `collectAsState()` |
| 7 | `SettingsLogoutAction` не попадает в `uiEvent` — обрабатывается в ViewModel локально |
