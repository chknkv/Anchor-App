# FeatureAddiction

Управление привычками: онбординг-выбор, главный список, создание, просмотр и редактирование.
KMP, commonMain + androidMain/iosMain (только `AddictionBackHandler` expect/actual).
Зависимости: `:Core:CoreDesignSystem`, `:Core:CoreUtils`, `:Core:CoreNetwork`.

---

## Публичный API

```kotlin
@Composable fun AddictionSelectionScreen(onFinished: () -> Unit)
@Composable fun AddictionAllScreen(onAddAddiction: () -> Unit = {}, onInfoAddiction: (Int) -> Unit = {})
@Composable fun AddictionCreateScreen(onBack: () -> Unit)
@Composable fun AddictionDetailsScreen(addictionId: Int, onBack: () -> Unit)
val featureAddictionModule: Module
```

Всё остальное — `internal`.

---

## Сетевой слой

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| GET | `addiction-groups` | Группы для онбординга |
| POST | `user/addictions/select` | Сохранить выбранные |
| GET | `user/addictions/grouped` | Привычки пользователя |
| POST | `user/addictions` | Создать |
| GET | `user/addictions/{id}` | Детали |
| POST | `user/addictions/{id}/increment` | Отметить выполнение |
| PATCH | `user/addictions/{id}` | Обновить |
| DELETE | `user/addictions/{id}` | Удалить |

**DTO (models/data/, internal, @Serializable):**
- `AddictionSelectionGroupResponse` ← `categoryKey`, `addictions: List<AddictionSelectionItemResponse>`
- `AddictionSelectionItemResponse` ← `id, name, iconKey, category`
- `AddictionAllGroupResponse` ← `categoryKey`, `addictions: List<AddictionDetailResponse>`
- `AddictionDetailResponse` ← `id, name, category, iconKey, gradient, controlDays, description, completedDates, canIncrementToday, nextIncrementAvailableInSeconds`
- `AddictionCreateRequest` → `name, description, iconKey, gradientKey, category`
- `AddictionUpdateRequest` → `name, description, iconKey, gradientKey, category`
- `AddictionSelectedRequest` → `ids: List<Int>`

Категории — строковый ключ: `lifestyle/health/sport/productivity/finance/relationships/other`.
Конвертация: `String.toAddictionCategory()` / `AddictionCategory.toApiKey()` в `AddictionCategoryConverter.kt`.

---

## Доменные модели (models/domain/, internal)

`AddictionCategory` — enum (7 значений). `Addiction(id, name)` — для выбора.
`UserAddiction(id, name, category, iconKey, gradient: String, controlDays, description, completedDates: Set<String>, canIncrementToday, nextIncrementAvailableInSeconds)`.
`UserAddictionGroup(category, addictions)`. `AddictionGroup(category, addictions: List<Addiction>)`.
`AddictionCreate(name, description, iconKey, gradientKey, category)`. `AddictionUpdate(id, …)`.
`AddictionRepository.updates: SharedFlow<Unit>` — эмитит после каждой мутации; `AddictionAllViewModel` подписывается для автообновления.

---

## MVI — контракты экранов

### AddictionAllScreen — без AppScaffold, без UiEvent

**UiAction:** `Init · Refresh`
**UiState:** `Init | Loading | Successful(AddictionAllUiResult) | Error(message?) | Empty`
`AddictionAllUiResult(groups: List<UserAddictionGroupUi>)` — `UserAddictionGroupUi(category: AddictionCategoryUi, addictions: List<UserAddictionUi>)`.
Автообновление: `interactor.updates.collect { handleRefresh() }`.

### AddictionSelectionScreen — без AppScaffold

**UiAction:** `Init · OnAddictionToggled(id) · OnSkipClicked · OnNextClicked`
**UiEvent:** `OnSelectionLimitReached · OnFinished`
**UiState:** `Init | Loading | Successful(AddictionSelectionUiResult) | Error`
`AddictionSelectionUiResult(groups, selectedIds: Set<Int>, maxSelectable=3, isSaving)` + вычисляемые: `selectedCount, isLimitReached, isNextMode`.
Выбор 4-й → `OnSelectionLimitReached` (haptic в Screen, стейт не меняется). После `saveSelected` → `OnFinished` независимо от ошибки.

### AddictionCreateScreen — с AppScaffold, LoadingHUD поверх формы

**UiAction:** `Init · ChangeTitle · ChangeDescription · SelectIcon · SelectGradient · SelectCategory · Submit(emptyTitleError, emptyCategoryError) · NavigateBack`
**UiEvent:** `OnCreated`
**UiState:** `Init | Loading | Successful(AddictionCreateUiResult) | Error`
`AddictionCreateUiResult(title, description, selectedIconKey, selectedGradientKey, selectedCategory: AddictionCategoryUi?, availableIconKeys, availableGradientKeys, isLoading, errorMessage)`.

### AddictionDetailsScreen — с AppScaffold, DetailsMode, expect BackHandler

**UiAction:** `Init · NavigateBack · SwitchToEditMode · ChangeTitle · ChangeDescription · SelectIcon · SelectGradient · SelectCategory · SubmitEdit(emptyTitleError, emptyCategoryError) · IncrementDays · DeleteHabit`
**UiEvent:** `HabitDeleted · NavigateBack`
**UiState:** `Init | Loading | Successful(AddictionDetailsUiResult) | Error`
`AddictionDetailsUiResult` — 18 полей: `addictionId, mode: DetailsMode(ViewMode|EditMode), title, category, iconKey, gradientKey, description, controlDays, editTitle/Description/IconKey/GradientKey/Category, availableIconKeys/GradientKeys, completedDates: Set<String>, canIncrementToday, nextIncrementSeconds, isLoading, errorMessage`.
`NavigateBack` в EditMode → переключает в ViewMode (не закрывает). `countdownJob` тикает раз в секунду. `handleSubmitEdit` после успешного PATCH → вызывает `handleInit()`.

---

## UI-конвертеры (presentation/Utils.kt)

```kotlin
internal fun List<UserAddictionGroup>.toUiResult(): AddictionAllUiResult
internal fun UserAddiction.toUi(): UserAddictionUi
internal fun AddictionCategory.toUi(): AddictionCategoryUi
internal fun AddictionCategoryUi.toDomain(): AddictionCategory
internal fun String.toIconDrawableResource(): DrawableResource   // fallback → ic_habit_placeholder
       fun String.toGradientBrush(): Brush                       // PUBLIC; fallback → Gray; НЕ @Composable
@Composable @ReadOnlyComposable
internal fun String.toGradientPrimaryColor(): Color              // только в Composable
val AVAILABLE_ICON_KEYS: List<String>     // 7 ключей
val AVAILABLE_GRADIENT_KEYS: List<String> // 11 ключей
```

`toGradientBrush()` — не `@Composable`; оборачивать в `remember(key) { key.toGradientBrush() }`.

---

## DI

```kotlin
val featureAddictionModule = module {
    single<AddictionApiMapper>  { AddictionApiMapperImpl(get<ApiClient>()) }
    single<AddictionRepository> { AddictionRepositoryImpl(get()) }
    factory<AddictionInteractor>{ AddictionInteractorImpl(get()) }
    viewModel { AddictionSelectionViewModel(get()) }
    viewModel { AddictionAllViewModel(get()) }
    viewModel { AddictionCreateViewModel(get()) }
    viewModel { params -> AddictionDetailsViewModel(params.get(), get()) }  // params.get() = addictionId: Int
}
```

`Mapper` и `Repository` — `single` (общий `updates: SharedFlow`). Details: `koinViewModel(parameters = { parametersOf(addictionId) })`.

---

## Файловая карта

```
commonMain/kotlin/com/chknkv/feature/addiction/
├── data/mapper/       AddictionApiMapper · AddictionApiMapperImpl (Ktor)
├── data/repository/   AddictionRepository (+ updates: SharedFlow) · AddictionRepositoryImpl
├── domain/converter/  8 конвертеров domain↔DTO
├── domain/interactor/ AddictionInteractor · AddictionInteractorImpl (прокси)
├── models/data/       DTO (Request/Response, @Serializable, internal)
├── models/domain/     доменные модели (internal)
├── models/presentation/ all/ · create/ · details/ · select/ — UiAction·UiResult·UiState·UiEvent
├── presentation/Utils.kt        конвертеры domain→UI, ключи, Brush/Color
├── presentation/all/            AddictionAllScreen (публичный) · ViewModel · compose/
├── presentation/create/         AddictionCreateScreen (публичный) · ViewModel · compose/
├── presentation/details/        AddictionDetailsScreen (публичный) · ViewModel · compose/ · elements/
│   └── elements/                AddictionBackHandler.kt (expect) · AddictionDetailsActivityCalendar
├── presentation/select/         AddictionSelectionScreen (публичный) · ViewModel · compose/ · elements/
└── di/FeatureAddictionModule.kt
androidMain/ + iosMain/ → AddictionBackHandler actual
composeResources/ → ic_habit_*.xml (7 иконок) · ic_edit.xml · strings.xml + values-ru/
```

---

## Жёсткие правила

| # | Правило |
|---|---------|
| 1 | Публичный API — только 4 Screen-функции + `featureAddictionModule`; всё остальное `internal` |
| 2 | `AddictionAllScreen` и `AddictionSelectionScreen` — без `AppScaffold` |
| 3 | `AddictionApiMapper` и `AddictionRepository` — `single`; `updates: SharedFlow` должен быть общим |
| 4 | `gradient: String` в domain; конвертация в `Brush` — только в `Utils.kt.toGradientBrush()` |
| 5 | Никаких Compose-типов (`Brush`, `DrawableResource`, `Color`) в domain-слое |
| 6 | `isScreenInitialized` guard — повторный `initScreen()` no-op; не убирать |
| 7 | `_actionFlow.onStart { emit(Init) }` — не убирать; гарантирует доставку Init |
| 8 | Haptic (`OnSelectionLimitReached`) — в Screen, не в ViewModel |
| 9 | `collectAsStateWithLifecycle()`, не `collectAsState()` |
| 10 | Строки ошибок валидации передаются в ViewModel через action-параметры; ViewModel не читает ресурсы |
| 11 | `NavigateBack` в EditMode → переключает в ViewMode, не закрывает экран |
| 12 | `countdownJob` — отменять через `cancel()` перед перезапуском; не запускать если `canIncrementToday` |
| 13 | `handleSubmitEdit` после успешного PATCH → вызывает `handleInit()`, не обновляет поля вручную |
| 14 | `addictionId: Int` в Details ViewModel — через Koin `params.get()`, не через `SavedStateHandle` |
| 15 | LoadingHUD — поверх AppScaffold во втором `Box`; перехватывать клики снаружи |
| 16 | `toGradientBrush()` — `public`, не `internal`; не добавлять `@Composable` |
