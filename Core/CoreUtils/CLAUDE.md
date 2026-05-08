# CoreUtils

KMP-библиотека утилит уровня приложения, общих для Android и iOS.
Package: `com.chknkv.coreutils`. Платформы: `commonMain · androidMain · iosMain`.
Зависимости: `multiplatform-settings · kotlinx.coroutines · koin-core · kotlinx-datetime`.
**Запрет:** CoreUtils не импортирует другие Core-модули и Feature-модули.

---

## Ключевые абстракции

| Тип | Роль |
|-----|------|
| `AppSettings` | Интерфейс реактивных настроек — `StateFlow<T>` для темы, языка, авторизации |
| `AppSettingsImpl(appIdentifier)` | Реализация: `MutableStateFlow` + синглтоны для персистентности |
| `ApplicationTheme` | `object`. Persist тема; ключ: `"${appIdentifier.name}_APPLICATION_THEME"` |
| `ApplicationLanguage` | `object`. Persist язык; ключ: `"${appIdentifier.name}_APPLICATION_LANGUAGE"` |
| `ApplicationAuth` | `object`. Persist флаг авторизации; ключ: `"${appIdentifier.name}_IS_AUTHORIZED"` |
| `AppIdentifier` | `enum { ANCHOR }` — namespace для изоляции ключей в общем хранилище |
| `AppTheme` | `enum { SYSTEM, LIGHT, DARK }` |
| `AppLanguage` | `enum { RUSSIAN, ENGLISH }` |
| `getAppVersion()` | `expect fun` — Android: `PackageManager`; iOS: `CFBundleShortVersionString` |
| `openUrl(url)` | `expect fun` — открывает URL в браузере по умолчанию; без DI, прямой вызов |
| `coreUtilsModule(appIdentifier)` | Koin-модуль: `single<AppSettings> { AppSettingsImpl(appIdentifier) }` |

---

## AppSettings

```kotlin
interface AppSettings {
    val theme: StateFlow<AppTheme>       // SYSTEM | LIGHT | DARK
    val language: StateFlow<AppLanguage> // RUSSIAN | ENGLISH
    val isAuthorized: StateFlow<Boolean>

    fun setTheme(theme: AppTheme)
    fun setLanguage(language: AppLanguage)
    fun setAuthorized(isAuthorized: Boolean)
}
```

`AppSettingsImpl` инициализирует каждый `MutableStateFlow` значением из синглтона при создании.
`set*` методы атомарно обновляют `StateFlow` **и** персистентное хранилище.

**Важно:** в `AppSettingsImpl` параметр `appIdentifier` используется только для языка.
Тема и авторизация хардкодят `AppIdentifier.ANCHOR`. При добавлении нового `AppIdentifier` это нужно учесть.

---

## Язык — приоритет источников

`ApplicationLanguage.getLanguage(appIdentifier)` выбирает язык в порядке:
1. `getPlatformAppLanguageCode()` — per-app настройки платформы (Android 13+, iOS)
2. Сохранённое значение в `multiplatform-settings`
3. Системный язык устройства (`getSystemLanguageCode()`)

Если сохранённое значение не парсится через `AppLanguage.valueOf()` — fallback к системному языку.
`setLanguage()` сохраняет в settings **и** немедленно вызывает `updateSystemLocale(code)`.
Оба языка маппятся: `RUSSIAN → "ru"`, `ENGLISH → "en"`. Все прочие системные коды → `ENGLISH`.

---

## expect/actual функции

| Функция | Android | iOS |
|---------|---------|-----|
| `getSystemLanguageCode()` | `Resources.getSystem().configuration.locales[0].language` | `NSLocale.currentLocale.languageCode` |
| `updateSystemLocale(code)` | `AppCompatDelegate.setApplicationLocales(LocaleListCompat)` | `NSUserDefaults["AppleLanguages"] = [code]` |
| `getPlatformAppLanguageCode()` | `AppCompatDelegate.getApplicationLocales()[0]?.language` | `NSLocale.preferredLanguages[0].split("-")[0]` |
| `getAppVersion()` | `PackageManager.getPackageInfo().versionName` | `NSBundle.mainBundle["CFBundleShortVersionString"]` |
| `openUrl(url)` | `Intent(ACTION_VIEW, Uri.parse(url))` + `FLAG_ACTIVITY_NEW_TASK` через `appContext` | `UIApplication.sharedApplication.openURL(NSURL, emptyMap, null)` (iOS 10+); невалидный URL — no-op |

---

## Платформенные детали

**Android:**
- Смена локали — `AppCompatDelegate.setApplicationLocales()`. Требует `AppCompat`-темы в манифесте.
- `appContext: Context` (`lateinit var` в `ApplicationVersion.android.kt`) — **обязателен**. Установить в `Application.onCreate()` до первого вызова `getAppVersion()`.

**iOS:**
- `updateSystemLocale()` записывает в `NSUserDefaults["AppleLanguages"]` и вызывает `synchronize()`.
- Смена языка на iOS вступает в силу **только при следующем запуске** приложения.

---

## DI

```kotlin
startKoin {
    modules(coreUtilsModule(AppIdentifier.ANCHOR))
}
```

`AppSettings` — `single<>` в Koin, один экземпляр на весь граф. Feature-модули получают через `get<AppSettings>()`.

---

## Структура файлов

```
commonMain/kotlin/com/chknkv/coreutils/
├── AppSettings.kt          # interface AppSettings + AppSettingsImpl
├── ApplicationTheme.kt     # enum AppTheme · enum AppIdentifier · object ApplicationTheme
├── ApplicationLanguage.kt  # enum AppLanguage · expect-функции · object ApplicationLanguage
├── ApplicationAuth.kt      # object ApplicationAuth
├── ApplicationVersion.kt   # expect fun getAppVersion()
├── OpenUrl.kt              # expect fun openUrl(url: String)
└── CoreUtilsModule.kt      # fun coreUtilsModule(appIdentifier): Module

androidMain/kotlin/com/chknkv/coreutils/
├── ApplicationLanguage.android.kt  # actual: AppCompatDelegate · Resources
├── ApplicationVersion.android.kt   # actual: appContext lateinit · PackageManager
└── OpenUrl.android.kt              # actual: Intent(ACTION_VIEW) + FLAG_ACTIVITY_NEW_TASK через appContext

iosMain/kotlin/com/chknkv/coreutils/
├── ApplicationLanguage.ios.kt      # actual: NSLocale · NSUserDefaults
├── ApplicationVersion.ios.kt       # actual: NSBundle · CFBundleShortVersionString
└── OpenUrl.ios.kt                  # actual: UIApplication.openURL (iOS 10+); невалидный URL — no-op
```

---

## Расширение модуля

**Новая настройка (например, `notifications: StateFlow<Boolean>`):**
1. Добавить `val notificationsEnabled: StateFlow<Boolean>` и `fun setNotificationsEnabled(Boolean)` в `AppSettings`
2. Создать `object ApplicationNotifications` по образцу `ApplicationAuth` (Settings + appIdentifier-ключ)
3. В `AppSettingsImpl`: `private val _notificationsEnabled = MutableStateFlow(ApplicationNotifications.isEnabled(AppIdentifier.ANCHOR))`; в `setNotificationsEnabled` — атомарно обновить оба

**Новая платформа:** реализовать `actual`-функции из `ApplicationLanguage.kt` и `ApplicationVersion.kt` в новом source set.

---

## Жёсткие правила

| # | Правило |
|---|---------|
| 1 | `AppSettings` — `single<>` в Koin; никогда не создавать `AppSettingsImpl()` в обход Koin |
| 2 | Feature-модули используют `AppSettings` только через DI (`get<AppSettings>()`); не передавать напрямую через конструктор |
| 3 | `StateFlow` — только для чтения из UI; мутация только через `set*` методы `AppSettings` |
| 4 | `AppIdentifier.ANCHOR` — единственный идентификатор; не хардкодить строки-ключи настроек вне синглтонов |
| 5 | `appContext` на Android — устанавливается **один раз** в `Application.onCreate()`; не переустанавливать |
| 6 | Ключи `Settings` формируются как `"${appIdentifier.name}_SUFFIX"`; не использовать иной формат |
| 7 | Новый синглтон (по образцу `ApplicationTheme`) — `private val settings by lazy { Settings() }`; не передавать `Settings` через конструктор |
