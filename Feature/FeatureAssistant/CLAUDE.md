# FeatureAssistant

Виджет мотивационных цитат и тревожной кнопки, встраиваемый в `MainScreen`.
KMP, commonMain only. Зависимости: `:Core:CoreDesignSystem`, `:Core:CoreUtils`, `:Core:CoreNetwork`.

---

## Публичный API

```kotlin
@Composable fun AssistanceWidget()          // com.chknkv.feature.assistant.presentation
val featureAssistantModule: Module          // com.chknkv.feature.assistant.di
```

Модуль **не является отдельным навигационным экраном** — вставляется как composable внутрь `MainScreen`. Собственного NavRoute нет.

---

## MVI — контракт

### UiAction (sealed interface, internal)

| Action | Когда |
|--------|-------|
| `Init` | Первичная загрузка, эмитится автоматически через `onStart` |
| `Refresh` | Повторная загрузка по запросу пользователя |
| `ShowQuoteSheet` | Нажатие на карточку цитаты |
| `HideQuoteSheet` | Закрытие BottomSheet (любым способом) |

### UiResult → AssistanceWidgetUiResult (data class, internal)

```kotlin
data class AssistanceWidgetUiResult(
    val quote: QuoteUiResult? = null,   // null = ошибка загрузки; карточка цитаты скрыта
)

data class QuoteUiResult(
    val quoteText: String,
    val quoteDetailText: String,
    val isQuoteSheetVisible: Boolean = false,
)
```

### UiState → AssistanceWidgetUiState (sealed interface, internal)

| Ветка | Содержимое | UI |
|-------|------------|----|
| `Loading` | — | Shimmer-скелетон |
| `Successful(result)` | `AssistanceWidgetUiResult` | HorizontalPager + Sheet |

`Error`-ветки нет: при ошибке загрузки переходит в `Successful(AssistanceWidgetUiResult())` с `quote = null` — карточка тревожной кнопки остаётся видимой.

UiEvent отсутствует.

### ViewModel — ключевые механики

- `initWidget()` защищён `isInitialized` guard — повторный вызов no-op; вызывается из `LaunchedEffect(Unit)`.
- `_actionFlow` — `MutableSharedFlow(extraBufferCapacity = 64)`, `onStart { emit(Init) }`.
- `Init` и `Refresh` оба маршрутизируются в `handleLoadData()` — единая точка перезагрузки.
- `successfulResult` — computed property: `(_uiState.value as? Successful)?.result`; safe guard в `handleShowQuoteSheet` / `handleHideQuoteSheet`.
- `exceptionHandler` → `Napier.e(...)` + `_uiState.value = Successful(AssistanceWidgetUiResult())`.

---

## Карусель (AssistanceWidgetSuccessfulContent)

`HorizontalPager` с `PAGE_COUNT = 2` (если `quote != null`), иначе 1.

| Индекс | Константа | Градиент | Иконка | onClick |
|--------|-----------|----------|--------|---------|
| 0 | `PAGE_QUOTE` | `TokensGradient.Green` | `ic_assistance_quote` | `ShowQuoteSheet` |
| 1 | (else) | `TokensGradient.Red` | `ic_assistance_alarm` | `null` |

- Страница тревожной кнопки обёрнута в `Box(Modifier.alpha(0.5f))` — функционал не реализован.
- `PagerDotsIndicator`: активная точка — `Tokens.Action`, неактивная — `Tokens.Separator`; 8dp / 6dp. Показывается только при `pageCount > 1`.
- `Sheet`: управляется через `isQuoteSheetVisible`; `onDismissRequest`, `onDragDismissAction`, `onOutsideClickAction` — все три → `HideQuoteSheet`.
- `CellInfo` вызывается с `outPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp)`.

---

## Слои данных

```
ViewModel
  └─ AssistanceInteractor (interface, factory)
       └─ AssistanceInteractorImpl — прокси к репозиторию
            └─ AssistanceRepository (interface, single)
                 └─ AssistanceRepositoryImpl — вызывает apiMapper.getMotivationalQuote().toDomain()
                      └─ AssistanceApiMapper (interface, single)
                           └─ AssistanceApiMapperImpl — GET assistant/motivational-quote
```

Цепочка конвертации: `MotivationalQuoteResponse` → `MotivationalQuoteBody` → (toDomain) → `MotivationalQuote` → (toQuoteUiResult) → `QuoteUiResult`.

`MotivationalQuoteBody`: `@SerialName("text") val text`, `@SerialName("detail_text") val detailText`.

---

## DI

```kotlin
val featureAssistantModule = module {
    single<AssistanceApiMapper> { AssistanceApiMapperImpl(get<ApiClient>()) }
    single<AssistanceRepository> { AssistanceRepositoryImpl(get()) }
    factory<AssistanceInteractor> { AssistanceInteractorImpl(get()) }
    viewModel { AssistanceWidgetViewModel(get()) }
}
```

Подключение: `includes(featureAssistantModule)` в `featureMainModule` — **не** в `SharedModule` напрямую.

---

## Строковые ресурсы (values / values-ru)

| Ключ | EN | RU |
|------|----|----|
| `assistance_quote_title` | Quote of the day | Цитата дня |
| `assistance_alarm_title` | Alarm Button | Кнопка тревоги |
| `assistance_alarm_subtitle` | Coming soon | Скоро будет доступно |

Drawables: `ic_assistance_quote`, `ic_assistance_alarm`.

---

## Файловая карта

```
src/commonMain/kotlin/com/chknkv/feature/assistant/
├── di/
│   └── FeatureAssistantModule.kt               — val featureAssistantModule (public)
├── models/
│   ├── domain/
│   │   └── MotivationalQuote.kt                — internal data class(text, detailText)
│   ├── data/
│   │   └── MotivationalQuoteResponse.kt        — MotivationalQuoteResponse : NetworkEntity<MotivationalQuoteBody>; MotivationalQuoteBody(@SerialName)
│   └── presentation/
│       ├── AssistanceWidgetUiAction.kt         — internal sealed interface
│       ├── AssistanceWidgetUiResult.kt         — AssistanceWidgetUiResult(quote: QuoteUiResult?); QuoteUiResult(quoteText, quoteDetailText, isQuoteSheetVisible)
│       └── AssistanceWidgetUiState.kt          — internal sealed interface (Loading / Successful)
├── domain/interactor/
│   ├── AssistanceInteractor.kt                 — internal interface
│   └── AssistanceInteractorImpl.kt             — internal class, прокси
├── data/
│   ├── converter/
│   │   └── MotivationalQuoteConverter.kt       — fun MotivationalQuoteBody.toDomain(): MotivationalQuote
│   ├── mapper/
│   │   ├── AssistanceApiMapper.kt              — internal interface; getMotivationalQuote(): MotivationalQuoteBody
│   │   └── AssistanceApiMapperImpl.kt          — GET assistant/motivational-quote; импортирует io.ktor.http.HttpMethod
│   └── repository/
│       ├── AssistanceRepository.kt             — internal interface
│       └── AssistanceRepositoryImpl.kt         — вызывает apiMapper → converter
└── presentation/
    ├── AssistanceWidget.kt                     — public @Composable, точка входа
    ├── AssistanceWidgetViewModel.kt            — internal ViewModel
    └── compose/
        ├── AssistanceWidgetLoadingContent.kt   — shimmer-скелетон
        └── AssistanceWidgetSuccessfulContent.kt — HorizontalPager + PagerDotsIndicator + Sheet
```

---

## Жёсткие правила

| # | Правило |
|---|---------|
| 1 | Всё, кроме `AssistanceWidget` и `featureAssistantModule`, должно быть `internal` |
| 2 | Видимость Sheet управляется только через `ShowQuoteSheet` / `HideQuoteSheet` в ViewModel — локальный Compose state для этого не использовать |
| 3 | Domain-модели (`MotivationalQuote`) не должны содержать Compose-типы (`Brush`, `DrawableResource`, `StringResource`) |
| 4 | `featureAssistantModule` подключается через `includes()` в `featureMainModule`, не напрямую в `SharedModule` |
| 5 | FeatureAssistant не импортирует другие Feature-модули |
| 6 | `io.ktor.http.HttpMethod` допустим в `AssistanceApiMapperImpl` — это единственное исключение; прямой импорт других Ktor-пакетов в Feature запрещён (TODO: CoreNetwork должен реэкспортировать `HttpMethod`) |
| 7 | `CellInfo` вызывается с параметром `outPaddingValues`, не `innerPaddingValues` — проверь актуальную сигнатуру в CoreDesignSystem при изменении |
