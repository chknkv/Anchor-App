---
name: mvi
description: >
  Генерирует идиоматичный MVI/UDF-скаффолдинг для Kotlin Multiplatform (KMP) и Compose
  Multiplatform проектов на базе ViewModel (androidx.lifecycle) + Jetpack Navigation
  Component Multiplatform. Используй этот скилл всякий раз, когда пользователь просит
  создать экран, фичу или модуль по паттерну UiAction → UiResult → UiState → ViewModel.
  Триггеры: "создай экран", "добавь фичу", "MVI", "UDF", "UiAction", "UiResult",
  "UiState", "ViewModel", "viewModel<>", "Koin viewModel", "Navigation Component",
  "создай MVI-слой", "скаффолдинг экрана", "NavGraph", а также когда пользователь
  вставляет частичный MVI-код на базе ViewModel и просит дополнить или проверить его.
  ОБЯЗАТЕЛЬНО используй этот скилл для любого запроса на создание нового экрана
  в KMP-проекте — даже если пользователь не упомянул MVI явно.
---

# KMP MVI/UDF — ViewModel + Navigation Component Multiplatform

## Архитектурный контракт

Строгий 4-слойный UDF-цикл:

```
UI (Composable) → UiAction → ViewModel → UiState / UiResult → UI
                                       ↘ UiEvent (side-effect, опционально)
```

Навигация — **лямбды**, прокидываемые из NavGraph прямо в Screen-Composable.
Все слои живут в `commonMain`. Платформенные сплиты — только когда неизбежно.

### Стек

| Решение              | Значение                                                  |
|----------------------|-----------------------------------------------------------|
| ViewModel scope      | `viewModelScope` (androidx.lifecycle)                     |
| DI                   | Koin — `viewModel<MyVM>()` в Composable                   |
| Публичный стейт      | `StateFlow<UiState>`                                      |
| Side-effects         | `SharedFlow<UiEvent>` — только при необходимости          |
| Навигация            | Лямбды из NavGraph, параметры Screen-Composable           |
| Init-экшен           | `onStart { emit(UiAction.Init) }` внутри `subscribeToActions()` |
| Старт коллектора     | `initScreen()` — однократно, защищён флагом               |
| Обработка ошибок     | `CoroutineExceptionHandler` на каждом `handle*`-launch    |
| KDOC                 | Русский язык; Interface — полный KDOC; Impl — только `/** Реализация [XxxInterface]. */` |

---

## Чеклист перед генерацией

Уточни или выведи из контекста:

1. **Имя экрана** → определяет имена всех классов (`SettingsScreen` → `Settings*`)
2. **Список action'ов** → что UI может передать во ViewModel?
3. **Поля UiResult** → какие данные отображаются в `Successful`-состоянии?
4. **Дополнительные варианты UiState** → нужны ли помимо `Init/Loading/Successful/Error`?
5. **Нужен ли `UiEvent`?** → только если есть одноразовые события (Toast, Haptic, SnackBar)
6. **Навигационные лямбды** → какие переходы нужны? (`onNavigateBack`, `onNavigateTo*`)

Если пользователь дал описание фичи без деталей — **выведи разумные defaults и явно озвучь их** перед генерацией кода.

---

## Правила генерации

### 1. UiAction

- Всегда `sealed interface`, **никогда** `sealed class`
- Без payload → `data object`; с payload → `data class`
- **Первый вариант всегда `Init`** — эмитится автоматически через `onStart`
- Навигационные действия: префикс `Navigate*`
- Мутации: глагол (`SelectTheme`, `SubmitForm`, `LoadData`)
- KDOC на интерфейсе и каждом варианте

```kotlin
/**
 * Интенты экрана [ScreenName].
 */
sealed interface ScreenNameUiAction {

    /** Инициализировать экран. Эмитится автоматически через `.onStart` в [ScreenNameViewModel]. */
    data object Init : ScreenNameUiAction

    /**
     * Выбрать элемент списка.
     *
     * @param id Идентификатор выбранного элемента.
     */
    data class SelectItem(val id: String) : ScreenNameUiAction

    /** Вернуться на предыдущий экран. */
    data object NavigateBack : ScreenNameUiAction
}
```

---

### 2. UiResult

- Всегда **отдельный** `data class` — никогда не inline внутри `Successful`
- Содержит только отображаемые данные — никаких флагов загрузки, кодов ошибки
- Опциональные поля — с default-значением
- KDOC на классе и каждом `@param`

```kotlin
/**
 * Результирующее состояние экрана [ScreenName].
 *
 * @param items Список элементов для отображения.
 * @param selectedId Идентификатор выбранного элемента, или `null` если ничего не выбрано.
 */
data class ScreenNameUiResult(
    val items: List<ItemModel>,
    val selectedId: String? = null,
)
```

---

### 3. UiState

- Всегда `sealed interface`
- Стандартные варианты: `Init`, `Loading`, `Successful(val result: XxxUiResult)`, `Error(val message: String)`
- Дополнительные — только при явной необходимости
- KDOC на интерфейсе и каждом варианте

```kotlin
/**
 * Состояние экрана [ScreenName].
 */
sealed interface ScreenNameUiState {

    /** Начальное состояние до первой загрузки. */
    data object Init : ScreenNameUiState

    /** Данные загружаются. */
    data object Loading : ScreenNameUiState

    /**
     * Данные успешно загружены.
     *
     * @param result Результирующее состояние для отрисовки UI.
     */
    data class Successful(val result: ScreenNameUiResult) : ScreenNameUiState

    /**
     * Произошла ошибка.
     *
     * @param message Локализованное сообщение об ошибке.
     */
    data class Error(val message: String) : ScreenNameUiState
}
```

---

### 4. UiEvent — только при необходимости

Генерируй `UiEvent` **только** если экран имеет реальные одноразовые side-effect'ы
(Toast, Haptic, SnackBar, системный диалог). Если таких событий нет — не добавляй слой.

```kotlin
/**
 * Одноразовые события экрана [ScreenName].
 * Не используются для навигации — для этого есть лямбды в [ScreenName].
 */
sealed interface ScreenNameUiEvent {

    /** Показать haptic-отклик при достижении лимита выбора. */
    data object OnSelectionLimitReached : ScreenNameUiEvent

    /**
     * Показать сообщение об ошибке.
     *
     * @param message Локализованный текст сообщения.
     */
    data class ShowErrorToast(val message: String) : ScreenNameUiEvent
}
```

---

### 5. ViewModel

**Ключевые механики:**
- `_actionFlow` — `MutableSharedFlow<UiAction>(extraBufferCapacity = 64)`: внутренняя шина интентов
- `_uiState` — `MutableStateFlow(Init)`: единственный источник истины
- `_uiEvent` — `MutableSharedFlow<UiEvent>()`: side-effect'ы (опционально)
- `isScreenInitialized` — `Boolean`-флаг, защищает от повторного запуска потока
- `initScreen()` — вызывается из `LaunchedEffect(Unit)` в UI; запускает `subscribeToActions()` однократно
- `subscribeToActions()` использует `.onStart { emit(Init) }` — гарантирует, что `Init` будет первым обработанным экшеном
- `emitAction()` — `viewModelScope.launch { _actionFlow.emit(action) }` — suspending emit, не блокирует UI-поток
- Все `handle*`-функции запускают отдельный `viewModelScope.launch(screenNameCoroutineExceptionHandler) { }` — не блокируют коллектор и изолируют ошибки
- `screenNameCoroutineExceptionHandler` — `CoroutineExceptionHandler` уровня ViewModel; логирует через Napier + выставляет `Error`-стейт
- `companion object` с `TAG` — единственный источник строки-тега для Napier
- Репозиторий/interactor переключает диспетчер внутри себя (`withContext(Dispatchers.IO)`)

> **Почему `subscribeToActions()` внутри `initScreen()`, а не `init {}`?**
> ViewModel создаётся раньше, чем Composable впервые рендерится. Если коллектор стартует в `init {}`,
> а `onStart { emit(Init) }` срабатывает немедленно — к этому моменту UI ещё не подписан на `uiState`,
> и первый стейт-переход (Init → Loading) будет пропущен коллектором Compose.
> Запуск из `initScreen()` гарантирует, что `LaunchedEffect` уже выполнился и Composable подписан.

> **Почему `onStart { emit(Init) }`, а не явный `emit` в `initScreen()`?**
> `onStart` — часть самого Flow-оператора: эмит происходит **внутри** коллектора, до первого входящего
> события. Это атомарно и семантически чисто: `Init` — первое событие в потоке, а не внешний вызов.
> Явный `emit` в `initScreen()` потребовал бы отдельного `launch`, создавая race condition
> между запуском коллектора и эмитом.

> **Почему `extraBufferCapacity = 64`?**
> `emit` в `launch` suspended при заполненном буфере — событие не потеряется, но `launch` будет
> висеть. Буфер на 64 исключает suspension при любом реалистичном burst пользовательских действий.

> **Почему `CoroutineExceptionHandler` на `handle*`, а не на `subscribeToActions`?**
> `viewModelScope` использует `SupervisorJob`: дочерние корутины падают независимо друг от друга.
> Если повесить handler на `subscribeToActions` — при первой же ошибке handler сработает,
> эта корутина отменится и главный flow перестанет обрабатывать экшены навсегда.
> Если handler передаётся в каждый `viewModelScope.launch(handler) { }` внутри `handle*` —
> при ошибке отменяется только эта дочерняя корутина, `subscribeToActions` продолжает работать.

```kotlin
/**
 * ViewModel экрана [ScreenName].
 *
 * @param repository Репозиторий для загрузки данных экрана.
 */
class ScreenNameViewModel(
    private val repository: ScreenNameRepository,
) : ViewModel() {

    /**
     * Внутренний поток интентов.
     * Буфер на 64 события исключает потери при кратковременной незанятости коллектора.
     */
    private val _actionFlow = MutableSharedFlow<ScreenNameUiAction>(extraBufferCapacity = 64)

    private val _uiState = MutableStateFlow<ScreenNameUiState>(ScreenNameUiState.Init)
    val uiState: StateFlow<ScreenNameUiState> = _uiState.asStateFlow()

    // Раскомментировать при необходимости:
    // private val _uiEvent = MutableSharedFlow<ScreenNameUiEvent>()
    // val uiEvent: SharedFlow<ScreenNameUiEvent> = _uiEvent.asSharedFlow()

    /**
     * Перехватывает неожиданные исключения из дочерних корутин.
     * Логирует ошибку и переводит экран в [ScreenNameUiState.Error].
     * НЕ вешать на `subscribeToActions` — это убьёт главный flow.
     */
    private val screenNameCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Napier.e(tag = TAG, message = throwable.message ?: "Unknown error", throwable = throwable)
        _uiState.value = ScreenNameUiState.Error(throwable.message.orEmpty())
    }

    /**
     * Флаг первичной инициализации экрана.
     * Гарантирует, что [initScreen] запустит коллектор ровно один раз за жизнь ViewModel.
     */
    private var isScreenInitialized: Boolean = false

    /**
     * Инициализировать экран.
     * Вызывается однократно из `LaunchedEffect(Unit)` корневого Composable.
     * Повторные вызовы (рекомпозиция, пересоздание Composable) игнорируются.
     */
    fun initScreen() {
        if (isScreenInitialized) return
        isScreenInitialized = true
        subscribeToActions()
    }

    /**
     * Передать интент во ViewModel.
     *
     * @param action Действие, инициированное пользователем или системой.
     */
    fun emitAction(action: ScreenNameUiAction) {
        viewModelScope.launch { _actionFlow.emit(action) }
    }

    /**
     * Запускает коллектор [_actionFlow].
     * `.onStart` гарантирует, что [ScreenNameUiAction.Init] будет первым обработанным событием —
     * до любых пользовательских экшенов, атомарно внутри Flow.
     */
    private fun subscribeToActions() {
        viewModelScope.launch {
            _actionFlow
                .onStart { emit(ScreenNameUiAction.Init) }
                .collect { action ->
                    when (action) {
                        ScreenNameUiAction.Init          -> handleInit()
                        is ScreenNameUiAction.SelectItem -> handleSelectItem(action.id)
                        ScreenNameUiAction.NavigateBack  -> { /* навигация через лямбду в Screen */ }
                    }
                }
        }
    }

    /**
     * Обрабатывает инициализацию: загружает начальные данные.
     * Ошибки перехватываются [screenNameCoroutineExceptionHandler] — стейт переходит в [ScreenNameUiState.Error],
     * главный flow остаётся живым.
     */
    private fun handleInit() {
        viewModelScope.launch(screenNameCoroutineExceptionHandler) {
            _uiState.value = ScreenNameUiState.Loading
            val items = repository.fetchItems()
            _uiState.value = ScreenNameUiState.Successful(
                result = ScreenNameUiResult(items = items),
            )
        }
    }

    /**
     * Обновляет выбранный элемент в текущем [ScreenNameUiState.Successful].
     * Вызов игнорируется, если текущий стейт не [ScreenNameUiState.Successful].
     *
     * @param id Идентификатор выбранного элемента.
     */
    private fun handleSelectItem(id: String) {
        val current = _uiState.value as? ScreenNameUiState.Successful ?: return
        _uiState.value = current.copy(result = current.result.copy(selectedId = id))
    }

    companion object {
        private const val TAG = "ScreenNameViewModel"
    }
}
```

---

### 6. Screen — корневой Composable

Принимает навигационные лямбды напрямую — **не** `NavController`.
`viewModel` получается через Koin: `val viewModel = viewModel<ScreenNameViewModel>()`.

```kotlin
/**
 * Корневой экран [ScreenName].
 *
 * @param onNavigateBack Callback для возврата на предыдущий экран.
 * @param onNavigateToDetail Callback для перехода на экран детали.
 */
@Composable
fun ScreenNameScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (id: String) -> Unit,
) {
    val viewModel = viewModel<ScreenNameViewModel>()

    LaunchedEffect(Unit) { viewModel.initScreen() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Раскомментировать при наличии UiEvent:
    // LaunchedEffect(viewModel) {
    //     viewModel.uiEvent.collect { event ->
    //         when (event) {
    //             is ScreenNameUiEvent.ShowErrorToast -> { /* показать Toast */ }
    //         }
    //     }
    // }

    when (val state = uiState) {
        is ScreenNameUiState.Init       -> Unit
        is ScreenNameUiState.Loading    -> ScreenNameLoadingState()
        is ScreenNameUiState.Error      -> ScreenNameErrorState(message = state.message)
        is ScreenNameUiState.Successful -> ScreenNameSuccessfulState(
            result = state.result,
            onAction = viewModel::emitAction,
        )
    }
}
```

> **`LaunchedEffect(Unit)`** — ключ `Unit` запускает эффект ровно один раз при первой композиции.
> `isScreenInitialized` во ViewModel — второй уровень защиты: если Composable пересоздастся
> (например, при смене конфигурации до того, как ViewModel был уничтожен), повторный вызов
> `initScreen()` будет no-op.

> **`LaunchedEffect(viewModel)` для UiEvent** — ключ `viewModel` (а не `Unit`) важен:
> при пересоздании Composable со старым ViewModel эффект перезапустится и переподпишется
> на тот же SharedFlow без дублирования.

---

### 7. State-Composable'ы

Каждое визуальное состояние — отдельный файл в `compose/`. Модификатор `internal`.

```kotlin
// compose/ScreenNameLoadingState.kt
@Composable
internal fun ScreenNameLoadingState() {
    // skeleton / shimmer / CircularProgressIndicator
}

// compose/ScreenNameErrorState.kt
@Composable
internal fun ScreenNameErrorState(message: String) {
    // error layout с кнопкой retry
}

// compose/ScreenNameSuccessfulState.kt
@Composable
internal fun ScreenNameSuccessfulState(
    result: ScreenNameUiResult,
    onAction: (ScreenNameUiAction) -> Unit,
) {
    // основной контент экрана
}
```

---

### 8. NavGraph — регистрация экрана

Навигационные лямбды прокидываются из графа. ViewModel не знает о NavController.

```kotlin
// NavGraph.kt (или соответствующий composable графа)
fun NavGraphBuilder.screenNameScreen(
    navController: NavController,
) {
    composable(route = ScreenNameRoute) {
        ScreenNameScreen(
            onNavigateBack = navController::navigateUp,
            onNavigateToDetail = { id -> navController.navigate(DetailRoute(id)) },
        )
    }
}
```

---

## Раскладка файлов

```
feature-name/
├── models/
│   └── presentation/
│       └── screenname/
│           ├── ScreenNameUiAction.kt
│           ├── ScreenNameUiState.kt
│           ├── ScreenNameUiResult.kt      ← один результат — лежит рядом с UiState
│           └── ScreenNameUiEvent.kt       ← только при наличии side-effect'ов
│
│           # Если результатов несколько:
│           └── uiresult/
│               ├── ScreenNameSuccessfulUiResult.kt
│               └── ScreenNamePartialUiResult.kt
│
└── presentation/
    └── screenname/
        ├── element/
        │   └── ...                        ← кастомные UI-элементы, отсутствующие в ДС
        ├── compose/
        │   ├── ScreenNameErrorState.kt
        │   ├── ScreenNameLoadingState.kt
        │   └── ScreenNameSuccessfulState.kt
        ├── ScreenNameViewModel.kt         ← только класс, без отдельного interface
        └── ScreenNameScreen.kt            ← корневой Composable, точка входа
```

**Правила раскладки:**
- `models/.../screenname/` — только MVI-контракт. Никакой бизнес-логики.
- `uiresult/` — создаётся только если **более одного** UiResult-класса.
- `ScreenNameViewModel.kt` — только класс. Interface не нужен: ViewModel тестируется напрямую.
- `ScreenNameScreen.kt` — только `when (uiState)` развилка + `LaunchedEffect`. Никакой логики.
- `compose/` — по одному файлу на каждое визуальное состояние (`ScreenNameXxxState.kt`).
- `element/` — создаётся только при наличии кастомных компонентов, специфичных для экрана.
- Все файлы в `commonMain`, если не требуется платформенный сплит.
- `presentation/Utils.kt` — обязательный файл для модулей с UI-конвертерами (domain→UI); создаётся при первом конвертере и пополняется при добавлении экранов.

---

## Конвертеры UI (domain ↔ presentation)

UI-конвертеры (преобразующие доменные модели в UI-модели и обратно) **всегда** живут
в одном файле `presentation/Utils.kt` (корень presentation-пакета модуля), а **не** в `domain/converter/`.

**Путь:** `Feature/[Module]/src/commonMain/.../presentation/Utils.kt`

**Структура файла** — один `// region` на каждый экран/регион:
```kotlin
package com.chknkv.feature.xxx.presentation

// region ScreenName
internal fun DomainModel.toUi(): UiModel = ...
internal fun List<DomainModel>.toUiResult(): XxxUiResult = ...
// endregion

// region AnotherScreen
internal fun AnotherDomain.toUi(): AnotherUiModel = ...
// endregion
```

**Правило:** domain-слой не знает о `Brush`, `StringResource`, `DrawableResource` и других
Compose-типах. Если конвертер порождает Compose-тип — он **не** относится к `domain/`.

**`domain/converter/`** — только конвертеры domain↔data (без Compose-зависимостей).

---

## Сетевой слой — краткий справочник для MVI-экранов с загрузкой данных

При генерации ViewModel для экранов, загружающих данные из API, применяй эти правила совместно с MVI-паттерном.

**handleInit() и handleXxx() — паттерн с сетью:**

```kotlin
private fun handleInit() {
    viewModelScope.launch(screenNameCoroutineExceptionHandler) {
        _uiState.value = ScreenNameUiState.Loading
        // interactor вызывает repository, который вызывает ApiMapper.requireBody()
        val data = interactor.getItems()
        _uiState.value = ScreenNameUiState.Successful(
            result = data.toUiResult(),   // конвертер в presentation/Utils.kt
        )
    }
}
```

**presentation/Utils.kt** — конвертеры domain→UI вызываются в ViewModel через extension:
```kotlin
// В Utils.kt (presentation-слой модуля):
// region ScreenName
internal fun List<XxxDomain>.toUiResult(): ScreenNameUiResult = ScreenNameUiResult(
    items = map { it.toUi() },
)
internal fun XxxDomain.toUi(): XxxUi = XxxUi(id = id, name = name)
// endregion
```

**Converter файлы для новой сущности:**
- `domain/converter/XxxConverter.kt` — `XxxBody.toDomain()` и `XxxDomain.toRequest()`
- `domain/converter/base/XxxKeyConverter.kt` — если есть enum-ключи (категории, типы)
- `presentation/Utils.kt` — `XxxDomain.toXxxUi()`, конвертеры с Compose-типами

## Reference files

- `references/uievent-patterns.md` — когда добавлять UiEvent, anti-patterns, примеры с Haptic/Toast/SnackBar
- `references/navigation-patterns.md` — интеграция лямбд с Navigation Component Multiplatform, типизированные маршруты
