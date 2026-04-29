# FeatureAssistant

Виджет мотивационных цитат и тревожной кнопки, встраиваемый в `MainScreen`.
KMP, commonMain only. Зависимости: `:Core:CoreDesignSystem`, `:Core:CoreUtils`.
Stack: Compose MP · Koin 4.x · Napier 2.7.1 · kotlinx.coroutines.

---

## Публичный API

Два публичных символа — всё остальное `internal`:

```kotlin
// точка входа в UI
@Composable
fun AssistanceWidget()   // com.chknkv.feature.assistant.presentation

// Koin-модуль
val featureAssistantModule: Module   // com.chknkv.feature.assistant.di
```

Модуль **не является отдельным навигационным экраном** — вставляется как composable внутрь
`MainScreen`. Собственного NavRoute нет.

---

## MVI — контракт виджета

### UiAction → AssistanceWidgetUiAction (sealed interface, internal)

| Action | Когда |
|--------|-------|
| `Init` | Первичная загрузка, эмитится автоматически через `onStart` |
| `Refresh` | Повторная загрузка по запросу пользователя |
| `ShowQuoteSheet` | Нажатие на карточку цитаты |
| `HideQuoteSheet` | Закрытие BottomSheet (любым способом) |

### UiResult → AssistanceWidgetUiResult (data class, internal)

| Поле | Тип | По умолчанию |
|------|-----|--------------|
| `quoteText` | `String` | — |
| `quoteDetailText` | `String` | — |
| `isQuoteSheetVisible` | `Boolean` | `false` |

### UiState → AssistanceWidgetUiState (sealed interface, internal)

| Ветка | Содержимое | UI |
|-------|------------|----|
| `Loading` | — | Shimmer-скелетон |
| `Successful(result)` | `AssistanceWidgetUiResult` | HorizontalPager + Sheet |
| `Error(message?)` | `String?` | Виджет скрыт (Unit) |

UiEvent отсутствует.

### ViewModel — ключевые механики

- `initWidget()` защищён `isInitialized` guard — повторный вызов no-op; вызывается из `LaunchedEffect(Unit)`.
- `_actionFlow` — `MutableSharedFlow(extraBufferCapacity = 64)`, `onStart { emit(Init) }` гарантирует Init первым.
- `successfulResult` — computed property: `(_uiState.value as? Successful)?.result`; используется в `handleShowQuoteSheet` / `handleHideQuoteSheet` как safe guard.
- `exceptionHandler` → `Napier.e(...)` + `_uiState.value = Error(throwable.message)`.
- `Init` и `Refresh` оба маршрутизируются в `handleLoadData()` — единая точка перезагрузки.

---

## Карусель (AssistanceWidgetSuccessfulContent)

`HorizontalPager` с `PAGE_COUNT = 2`:

| Константа | Индекс | Градиент | Иконка | onClick |
|-----------|--------|----------|--------|---------|
| `PAGE_QUOTE` | 0 | `TokensGradient.Green` | `ic_assistance_quote` | `ShowQuoteSheet` |
| `PAGE_PANIC` | 1 | `TokensGradient.Red` | `ic_assistance_alarm` | `null` (отключено) |

- Страница `PAGE_PANIC` обёрнута в `Box(modifier = Modifier.alpha(0.5f))` — функционал не реализован.
- `PagerDotsIndicator`: активная точка — `Tokens.Action`, неактивная — `Tokens.Separator`; размеры 8dp / 6dp.
- `Sheet`: `isVisible = result.isQuoteSheetVisible`; `onDismissRequest`, `onDragDismissAction`, `onOutsideClickAction` — все три → `HideQuoteSheet`.
- Контент Sheet: `Body(text = result.quoteDetailText, isSecondary = true)`.
- `CellInfo` вызывается с параметром `outPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp)`.

---

## Слои данных

```
ViewModel
  └─ AssistanceInteractor (interface, internal)
       └─ AssistanceInteractorImpl — тонкий прокси, без доп. логики
            └─ AssistanceRepository (interface, internal)
                 └─ AssistanceRepositoryImpl — ЗАГЛУШКА: delay(3500) + хардкод
```

`MotivationalQuote(text: String, detailText: String)` — единственная domain-модель (internal data class).

---

## DI

```kotlin
val featureAssistantModule = module {
    single<AssistanceRepository> { AssistanceRepositoryImpl() }   // single — future-proof для кэша/БД
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
| `assistance_quote_sheet_detail_label` | Remember: | Помни: |
| `assistance_alarm_title` | Alarm Button | Кнопка тревоги |
| `assistance_alarm_subtitle` | Coming soon | Скоро будет доступно |

Drawables: `ic_assistance_quote`, `ic_assistance_alarm`.

---

## Файловая карта

```
src/commonMain/kotlin/com/chknkv/feature/assistant/
├── di/
│   └── FeatureAssistantModule.kt           — val featureAssistantModule (public)
├── models/
│   ├── domain/
│   │   └── MotivationalQuote.kt            — internal data class(text, detailText)
│   └── presentation/
│       ├── AssistanceWidgetUiAction.kt     — internal sealed interface (Init/Refresh/ShowQuoteSheet/HideQuoteSheet)
│       ├── AssistanceWidgetUiResult.kt     — internal data class (quoteText, quoteDetailText, isQuoteSheetVisible)
│       └── AssistanceWidgetUiState.kt      — internal sealed interface (Loading/Successful/Error)
├── domain/interactor/
│   ├── AssistanceInteractor.kt             — internal interface
│   └── AssistanceInteractorImpl.kt         — internal class, прокси к репозиторию
├── data/repository/
│   ├── AssistanceRepository.kt             — internal interface
│   └── AssistanceRepositoryImpl.kt         — internal class, ЗАГЛУШКА delay(3500)
└── presentation/
    ├── AssistanceWidget.kt                 — public @Composable, точка входа
    ├── AssistanceWidgetViewModel.kt        — internal ViewModel
    └── compose/
        ├── AssistanceWidgetLoadingContent.kt   — internal; shimmer 144dp + dot-placeholder 32×16dp
        └── AssistanceWidgetSuccessfulContent.kt — internal; HorizontalPager + PagerDotsIndicator + Sheet

src/commonMain/composeResources/
├── drawable/  ic_assistance_quote.xml, ic_assistance_alarm.xml
└── values/ + values-ru/  strings.xml
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
| 6 | `CellInfo` вызывается с параметром `outPaddingValues`, не `innerPaddingValues` — проверь актуальную сигнатуру в CoreDesignSystem при изменении |
