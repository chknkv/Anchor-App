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

**DTO (models/data/, internal, @Serializable, наследуют `NetworkEntity<BodyType>`):**
- `AddictionAllGroupsResponse : NetworkEntity<AddictionAllGroupsBody>` ← `isCreateNewAvailable, items: List<AddictionsAllGroup>`
- `AddictionsAllGroup` ← `categoryKey: AddictionCategoryKey, addictions: List<AddictionAllInGroup>`
- `AddictionAllInGroup` ← `id, name, iconKey: AddictionIconKey, gradientKey: AddictionGradientKey, controlDays`
- `AddictionSelectionGroupResponse` ← `categoryKey, addictions: List<AddictionSelectionItemResponse>`
- `AddictionSelectionItemResponse` ← `id, name, iconKey, category`
- `AddictionDetailsResponse` ← `id, name, category, iconKey, gradient, controlDays, description, completedDates, canIncrementToday, nextIncrementAvailableInSeconds`
- `AddictionCreateRequest` → `name, description, iconKey, gradientKey, category`
- `AddictionUpdateRequest` → `name, description, iconKey, gradientKey, category`
- `AddictionSelectedRequest` → `ids: List<Int>`

Категории — строковый ключ: `lifestyle/health/sport/productivity/finance/relationships/other`.
Конвертация: `String.toAddictionCategory()` / `AddictionCategory.toApiKey()` в `AddictionCategoryConverter.kt`.

---

## Доменные модели (models/domain/, internal)

`AddictionCategory` — enum (7 значений).

`AddictionIcon` — enum (6): `Lifestyle, Health, Sport, Productivity, Finance, Relationships`; `AddictionIconKey.toDomain()` → `AddictionIcon` → `DrawableResource` через `Utils.toIconDrawableResource()`.
`AddictionGradient` — enum; `AddictionGradientKey.toDomain()` → `AddictionGradient`.
`AddictionAllGroups(category, addictions: List<AddictionAllGroup>, isCreateNewAvailable: Boolean)`.
`AddictionAllGroup(id, name, icon: AddictionIcon, gradient: AddictionGradient, controlDays: Int)`.
`AddictionDetails(id, name, category, iconKey: AddictionIcon, gradient: AddictionGradient, controlDays, description, completedDates: Set<String>, canIncrementToday, nextIncrementAvailableInSeconds)` — заменила `UserAddiction`.

`AddictionsSelectionGroups` / `AddictionCreate(name, description, iconKey, gradientKey, category)` / `AddictionUpdate(id, …)`.

`AddictionRepository.updates: SharedFlow<Unit>` — эмитит после каждой мутации; `AddictionAllViewModel` подписывается для автообновления.

---

## MVI — контракты экранов

### AddictionAllScreen — **без AppScaffold**, без UiEvent

**UiAction:** `Init · Refresh`
**UiState:** `Init | Loading | Successful(AddictionAllUiResult) | Error(message?) | Empty`
`AddictionAllUiResult(groups: List<UserAddictionGroupUi>)` — `UserAddictionGroupUi(category: AddictionCategoryUi, addictions: List<UserAddictionUi>)`.
Автообновление: `interactor.updates.collect { handleRefresh() }`.

### AddictionSelectionScreen — **с AppScaffold + LoadingHUD поверх формы**

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
internal fun List<AddictionAllGroups>.toUiResult(): AddictionAllUiResult
internal fun AddictionAllGroup.toUi(): UserAddictionUi
internal fun AddictionCategory.toUi(): AddictionCategoryUi
internal fun AddictionCategoryUi.toDomain(): AddictionCategory
internal fun String.toIconDrawableResource(): DrawableResource   // fallback → ic_habit_placeholder
       fun String.toGradientBrush(): Brush                       // PUBLIC; fallback → Gray; НЕ @Composable
@Composable @ReadOnlyComposable
internal fun String.toGradientPrimaryColor(): Color              // только в Composable
val AVAILABLE_ICON_KEYS: List<String>     // 6 ключей (Lifestyle..Relationships)
val AVAILABLE_GRADIENT_KEYS: List<String> // 11 ключей
```

`toGradientBrush()` — не `@Composable`; оборачивать в `remember(key) { key.toGradientBrush() }`.

---

## Строковые ресурсы (strings.xml + values-ru/strings.xml)

Ключи, которые уже есть — **не дублировать**:
- `addiction_common_error_generic`
- `addiction_selection_`: header_title · body_subtitle · counter · errorState_description · button_skip · button_next · saveFailed_warning
- `habit_group_`: lifestyle/health/sport/productivity/finance/relationships/other (7 ключей)
- `habit_`: 40+ предустановленных привычек (no_smoking, no_alcohol, water, gym_cardio…)
- `addictionCreate_`: header_title · name_hint · description_hint · icon/gradient/category_section_label · submit_button · nameEmpty_error · submit_error · categoryEmpty_error
- `addictionDetails_`: header_title · header_edit_title · activity_section_title · edit_button · delete_button · deleteConfirmation_title/subtitle/button · incrementDays_button · controlDays_label · nameEmpty/categoryEmpty/submit_error · errorState_description/retry_button
- `addictionAll_`: addHabit_button · filterAll_label · filterEmpty/emptyState/errorState_description · errorState_retry_button · limitSheet_title/subtitle/button · controlDays_label (plurals)

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
├── data/converter/        AllGroupConverter · DetailConverter · SelectionGroupConverter (+ base/*KeyConverter ×3)
├── data/mapper/           AddictionApiMapper · AddictionApiMapperImpl
├── data/repository/       AddictionRepository (+ updates: SharedFlow) · AddictionRepositoryImpl
├── domain/converter/      CreateConverter · UpdateConverter (+ base/*KeyConverter ×3)
├── domain/interactor/     AddictionInteractor · AddictionInteractorImpl
├── models/data/           DTO (Request/Response + base/Keys, @Serializable, internal)
├── models/domain/         AllGroups · AllGroup · Details · Create · Update · SelectionGroups
│                          base/(AddictionCategory · AddictionGradient · AddictionIcon enums)
├── models/presentation/   CategoryUi · GradientUi · IconUi · ErrorMessageUiResult
│                          all/ · create/ · details/(DetailsMode) · select/ — UiAction·UiResult·UiState·UiEvent
├── presentation/Utils.kt  конвертеры domain→UI, Brush/Color
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
| 4 | `gradient: AddictionGradient` в domain; конвертация в `Brush` — только в `Utils.kt.toGradientBrush()` |
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
