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
| GET | `addictions/default-selection-groups` | Группы для онбординга |
| POST | `addictions/save-selected` | Сохранить выбранные при онбординге |
| GET | `client/addictions/all` | Привычки пользователя (сгруппированные) |
| POST | `client/addictions/create` | Создать привычку |
| GET | `client/addictions/details/{id}` | Детали привычки |
| POST | `client/addictions/increment/{id}` | Отметить выполнение |
| PATCH | `client/addictions/update/{id}` | Обновить привычку |
| DELETE | `client/addictions/delete/{id}` | Удалить привычку |

**DTO (models/data/, internal, @Serializable):**
- `AddictionAllGroupsResponse : NetworkEntity<AddictionAllGroupsBody>` — `isCreateNewAvailable, items: List<AddictionsAllGroup>`
- `AddictionsAllGroup` — `categoryKey: AddictionCategoryKey, addictions: List<AddictionAllInGroup>`
- `AddictionAllInGroup` — `id, name, iconKey: AddictionIconKey, gradientKey: AddictionGradientKey, controlDays`
- `AddictionSelectionGroupsResponse : NetworkEntity<AddictionSelectionGroupsBody>` — `items: List<AddictionSelectionGroups>`
- `AddictionSelectionGroups` — `categoryKey: AddictionCategoryKey, addictions: List<AddictionSelectionItem>`
- `AddictionSelectionItem` — `id: Int, name: String` (без iconKey/category)
- `AddictionDetailsResponse : NetworkEntity<AddictionDetailsBody>` — `id, name, categoryKey, iconKey, gradientKey, controlDays, description?, completedDates: List<String>, canIncrementToday, nextIncrementAvailableInSeconds: Long`
- `AddictionCreateRequest` → `name, description, iconKey: AddictionIconKey, gradientKey: AddictionGradientKey, categoryKey: AddictionCategoryKey`
- `AddictionUpdateRequest` → `name, description, iconKey, gradientKey, categoryKey`
- `AddictionSelectedRequest` → `ids: List<Int>`

Категории — строковый ключ: `lifestyle/health/sport/productivity/finance/relationships/other`.

---

## Доменные модели (models/domain/, internal)

`AddictionCategory` — enum (7 значений: LIFESTYLE..OTHER).
`AddictionIcon` — sealed (6: Lifestyle/Health/Sport/Productivity/Finance/Relationships).
`AddictionGradient` — sealed (11: Gray/Green/Blue/Indigo/Purple/Pink/Red/Orange/DarkOrange/DarkRed/Black).
`AddictionAllGroups(category, addictions: List<AddictionAllGroup>, isCreateNewAvailable: Boolean)`.
`AddictionAllGroup(id, name, icon: AddictionIcon, gradient: AddictionGradient, controlDays: Int)`.
`AddictionDetails(id, name, category, iconKey: AddictionIcon, gradient: AddictionGradient, controlDays, description, completedDates: Set<String>, canIncrementToday, nextIncrementAvailableInSeconds)`.
`AddictionsSelectionGroups` / `AddictionCreate(name, description, iconKey, gradientKey, category)` / `AddictionUpdate(id, …)`.

`AddictionRepository.updates: SharedFlow<Unit>` — эмитит после каждой мутации; `AddictionAllViewModel` подписывается для автообновления.

---

## MVI — контракты экранов

### AddictionAllScreen — без AppScaffold, без UiEvent

**UiAction:** `Init · Refresh`
**UiState:** `Init | Loading | Successful(AddictionAllUiResult) | Error(message?) | Empty`
`AddictionAllUiResult(isCreateNewAvailable: Boolean, groups: List<UserAddictionGroupUi>)`.
`UserAddictionGroupUi(category: AddictionCategoryUi, addictions: List<UserAddictionUi>)`.
`UserAddictionUi(id, name, category, iconRes: DrawableResource, iconGradient: Brush, controlDays)`.
Автообновление: `interactor.updates.collect { handleRefresh() }`.

### AddictionSelectionScreen — с AppScaffold + LoadingHUD поверх формы

**UiAction:** `Init · OnAddictionToggled(id) · OnSkipClicked · OnNextClicked`
**UiEvent:** `OnSelectionLimitReached · OnFinished`
**UiState:** `Init | Loading | Successful(AddictionSelectionUiResult) | Error`
`AddictionSelectionUiResult(groups, selectedIds: Set<Int>, maxSelectable=3, isSaving, isFailed)` + вычисляемые: `selectedCount, isLimitReached, isNextMode`.
Выбор 4-й → `OnSelectionLimitReached` (haptic в Screen, стейт не меняется). После `saveSelected` → `OnFinished` независимо от ошибки.

### AddictionCreateScreen — с AppScaffold, LoadingHUD поверх формы

**UiAction:** `Init · ChangeTitle · ChangeDescription · SelectIcon · SelectGradient · SelectCategory · Submit(emptyTitleError, emptyCategoryError) · NavigateBack`
**UiEvent:** `OnCreated`
**UiState:** `Init | Loading | Successful(AddictionCreateUiResult) | Error`
`AddictionCreateUiResult(title, description, selectedIcon: AddictionIconUi, selectedGradient: AddictionGradientUi, selectedCategory: AddictionCategoryUi?, availableIcons, availableGradients, availableCategories, isLoading, isError: ErrorMessageUiResult?)`.

### AddictionDetailsScreen — с AppScaffold, DetailsMode, expect BackHandler

**UiAction:** `Init · NavigateBack · SwitchToEditMode · ChangeTitle · ChangeDescription · SelectIcon · SelectGradient · SelectCategory · SubmitEdit(emptyTitleError, emptyCategoryError) · IncrementDays · ChangeDeleteConfirmationVisibility(isVisible) · DeleteHabit`
**UiEvent:** `HabitDeleted · NavigateBack`
**UiState:** `Init | Loading | Successful(AddictionDetailsUiResult) | Error`
`AddictionDetailsUiResult` — поля: `addictionId, mode: DetailsMode(ViewMode|EditMode), title, category, icon, gradient, description, controlDays, editTitle, editDescription, editIcon, editGradient, editCategory?, availableIcons, availableGradients, completedDates: Set<String>, canIncrementToday, nextIncrementSeconds, isDeleteConfirmationVisible, isLoading, isError: ErrorMessageUiResult?`.
`NavigateBack` в EditMode → переключает в ViewMode (не закрывает). `countdownJob` тикает раз в секунду. `handleSubmitEdit` после успешного PATCH → вызывает `handleInit()`.

---

## UI-конвертеры (presentation/Utils.kt, все internal)

```kotlin
internal fun List<AddictionAllGroups>.toUiResult(): AddictionAllUiResult
internal fun AddictionAllGroup.toUi(category): UserAddictionUi
internal fun AddictionCategory.toUi(): AddictionCategoryUi
internal fun AddictionCategoryUi.toDomain(): AddictionCategory
internal fun AddictionCategoryUi.toStringResource(): StringResource
internal fun AddictionIcon.toIconDrawableResource(): DrawableResource   // через toUi().toDrawableResource()
internal fun AddictionIconUi.toDrawableResource(): DrawableResource
internal fun AddictionGradient.toGradientBrush(): Brush                 // не @Composable
internal fun AddictionGradientUi.toGradientBrush(): Brush               // не @Composable
@Composable @ReadOnlyComposable
internal fun AddictionGradientUi.toGradientPrimaryColor(): Color
internal val AVAILABLE_ICONS: List<AddictionIconUi>          // 6 значений
internal val AVAILABLE_GRADIENTS: List<AddictionGradientUi>  // 11 значений
internal fun Int.toCountdownString(): String                 // "HH:MM:SS"
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
├── data/converter/        AllGroupConverter · DetailConverter · SelectionGroupConverter
│                          base/*KeyConverter ×3 (Category/Gradient/Icon)
├── data/mapper/           AddictionApiMapper · AddictionApiMapperImpl
├── data/repository/       AddictionRepository (+ updates: SharedFlow) · AddictionRepositoryImpl
├── domain/converter/      CreateConverter · UpdateConverter + base/*KeyConverter ×3
├── domain/interactor/     AddictionInteractor · AddictionInteractorImpl
├── models/data/           DTO (Request/Response + base/Keys, @Serializable, internal)
├── models/domain/         AllGroups · AllGroup · Details · Create · Update · SelectionGroups
│                          base/(AddictionCategory · AddictionGradient · AddictionIcon)
├── models/presentation/   CategoryUi · GradientUi · IconUi · ErrorMessageUiResult
│                          all/ · create/ · details/(DetailsMode) · select/ — UiAction·UiResult·UiState·UiEvent
├── presentation/Utils.kt  конвертеры domain→UI, Brush/DrawableResource
├── presentation/all/      AddictionAllScreen · ViewModel · compose/(4 content)
├── presentation/create/   AddictionCreateScreen · ViewModel · compose/
├── presentation/details/  AddictionDetailsScreen · ViewModel · compose/(4 content)
│                          elements/(AddictionBackHandler expect · CalendarConstants · ActivityCalendar)
├── presentation/select/   AddictionSelectionScreen · ViewModel · compose/(3 content)
│                          elements/(SelectionBottomBar · SelectionHeader)
└── di/FeatureAddictionModule.kt
androidMain/ + iosMain/ → AddictionBackHandler actual
composeResources/ → ic_habit_*.xml (6) · ic_edit.xml · ic_cross.xml · strings.xml + values-ru/
```

---

## Жёсткие правила

| # | Правило |
|---|---------|
| 1 | Публичный API — только 4 Screen-функции + `featureAddictionModule`; всё остальное `internal` |
| 2 | `AddictionAllScreen` — **без** `AppScaffold`; `AddictionSelectionScreen` — **с** `AppScaffold` + `LoadingHUD` |
| 3 | `AddictionApiMapper` и `AddictionRepository` — `single`; `updates: SharedFlow` должен быть общим |
| 4 | `gradient: AddictionGradient` в domain; конвертация в `Brush` — только через `Utils.kt.toGradientBrush()` |
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
| 16 | `toGradientBrush()` — `internal`; не делать public без явного решения |
