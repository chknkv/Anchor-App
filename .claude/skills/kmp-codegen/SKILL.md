---
name: kmp-codegen
description: >
  Generates production-quality Kotlin Multiplatform (KMP) + Compose Multiplatform code for
  April 2026. Use this skill whenever the user asks to write, generate, create, or implement
  any Kotlin/KMP code — including full features, individual classes, or refactoring existing
  code. Triggers on: "create feature X", "write ViewModel for", "implement Repository",
  "add Interactor", "write Mapper", "generate DI module", "refactor this class",
  "how should I structure", or any request to produce Kotlin code in a KMP project.
  Also triggers for architecture questions, code review requests, and "is this idiomatic KMP?".
  Always use this skill before writing any Kotlin code.
---

# KMP Code Generation — April 2026

## Stack (do not ask — already known)
| | |
|-|-|
| Kotlin | 2.3.20 (K2) |
| Compose Multiplatform | 1.10.x+ |
| DI | Koin 4.x |
| Network | Ktor 3.x |
| Local DB | SQLDelight 2.x |
| Async | Coroutines + Flow only |
| Presentation | MVI per feature (Pattern A flat / Pattern B sealed) |
| Navigation | Jetpack Navigation Component Multiplatform |
| Platforms | Android + iOS |

**Before generating a full feature** → read `references/full-feature.md`
**Before generating any class** → read `references/patterns.md`

---

## Decision Tree

```
request type
├── "create feature [X]"      → full-feature.md → generate all layers
├── "write [ClassName]"       → identify layer → patterns.md → generate
├── "refactor [code]"         → check violations list → rewrite
└── "review [code]"           → check violations list → report findings
```

---

## Layer Ownership

| Artifact | Source set | Forbidden imports |
|----------|-----------|-------------------|
| `@Composable` Screen | `commonMain/presentation/` | No logic, no coroutines |
| ViewModel / Store | `commonMain/presentation/` | No Android SDK |
| Interactor interface + impl | `commonMain/domain/interactor/` | No data layer |
| Converter (data↔domain) | `commonMain/domain/converter/` | No data layer, no presentation imports, no Compose types |
| Converter (domain↔presentation, UI-типы) | `commonMain/presentation/Utils.kt` | Один файл на модуль; region-комментарии на каждый экран |
| Repository interface | `commonMain/data/repository/` | No domain constructors |
| RepositoryImpl | `commonMain/data/repository/` | No domain constructors |
| ApiMapper interface + impl | `commonMain/data/mapper/` | No DI dependencies, no domain |
| Ktor Api | `commonMain/data/remote/` | No DTOs in domain |
| SQLDelight Dao | `commonMain/data/local/` | No raw SQL outside `.sq` |
| Koin modules | `commonMain/di/` + `androidMain/di/` | No `androidContext()` in common |
| `expect/actual` | declared common, implemented platform | No business logic |

---

## KDOC — обязательно для всего

**Каждый** публичный и `internal` символ обязан иметь KDOC. Без исключений.

| Что | Правило |
|-----|---------|
| `interface XxxInteractor / XxxRepository / XxxApiMapper` | Полный KDOC: назначение класса + каждый метод с `@param`, `@return`, `@throws` |
| `class XxxInteractorImpl / XxxRepositoryImpl / XxxApiMapperImpl` | Только `/** Реализация [XxxInterface]. */` + `@param` на конструктор. Методы **не** документируются. |
| Остальные классы / interface / object | KDOC на объявлении: назначение в 1–2 предложениях |
| Конструктор с параметрами | `@param` на каждый параметр |
| `fun` (публичная / internal, кроме override impl) | KDOC: что делает + `@param` + `@return` если не `Unit` |
| `val` / `var` (публичный / internal) | Однострочный KDOC |
| `sealed interface` / `sealed class` варианты | KDOC на каждом варианте |
| `data class` поля через конструктор | `@param` в KDOC класса |

**Принцип Interface vs Impl:** контракт документируется **один раз** — в интерфейсе. Impl ссылается на него через `[XxxInterface]`.

```kotlin
// ✅ Интерфейс — полный KDOC
/**
 * Контракт доменного слоя для работы с X.
 */
internal interface XInteractor {
    /**
     * Возвращает список элементов X.
     * @return Список доменных моделей [XModel].
     */
    suspend fun getItems(): List<XModel>
}

// ✅ Реализация — только ссылка + @param конструктора
/**
 * Реализация [XInteractor].
 * @param repository Источник данных.
 */
internal class XInteractorImpl(
    private val repository: XRepository,
) : XInteractor {
    override suspend fun getItems(): List<XModel> = repository.fetchAll()
}
```

**Язык KDOC — русский.** Идентификаторы и технические термины — на английском.

**Запрещено:**
- Комментарий `// ...` вместо `/** ... */` для публичного/internal API
- KDOC, который просто дублирует имя метода (`/** Возвращает items. */ fun getItems()`)
- Пустые KDOC `/** */`
- Дублировать KDOC метода в интерфейсе и реализации одновременно

---

## Hard Violations — never generate

```
❌ Business logic in @Composable
❌ ViewModel in iosMain
❌ LiveData anywhere
❌ GlobalScope
❌ Dispatchers.Main/IO directly — use expect/actual AppDispatchers
❌ @Serializable or @Entity on domain models
❌ Domain layer importing kotlinx.serialization or SQLDelight
❌ runBlocking in production code
❌ !! without explicit justifying comment
❌ Mutable collections exposed from ViewModel/Store
❌ androidContext() in commonMain Koin module
❌ Mixing MVVM and MVI within one feature
❌ UI-конвертер (domain→presentation с Compose-типами) в domain/converter/
❌ Отдельный файл XxxUiConverter.kt в domain/ или data/
❌ Эндпоинт-строки прямо в методах ApiMapperImpl — только через companion object константы
❌ Response DTO без наследования NetworkEntity<XxxBody>
❌ Возврат List<T> напрямую из ApiMapper — всегда через Body-обёртку
❌ Body-класс наследует NetworkEntity вместо отдельного класса-обёртки
```

---

## NetworkEntity — сетевой конверт Anchor API

Все сетевые ответы оборачиваются в `NetworkEntity<T>` из `CoreNetwork`:

```kotlin
@Serializable
open class NetworkEntity<T>(
    val success: Boolean = true,
    val body: T? = null,
    val message: String? = null,
    val timeout: String? = null,
)
```

### Response DTO — структура

```kotlin
// ПРАВИЛЬНО — Response наследует NetworkEntity; Body — отдельный data class
@Serializable
internal class XxxResponse : NetworkEntity<XxxBody>()

@Serializable
internal data class XxxBody(
    @SerialName("items") val items: List<XxxItemBody>,
)

// ЗАПРЕЩЕНО — не возвращать List<T> напрямую
@Serializable
internal class XxxResponse : NetworkEntity<List<XxxItemBody>>()  // ❌
```

### Extension-функции для разворачивания ответа

| Функция | Когда использовать |
|---------|-------------------|
| `NetworkEntity<T>.requireBody(): T` | GET-запросы, возвращающие тело ответа |
| `NetworkEntity<*>.isSuccessfulExecute()` | POST / PATCH / DELETE без тела ответа |

```kotlin
// GET с телом
override suspend fun getItems(): XxxBody =
    apiClient.request<XxxResponse> {
        endpoint = ENDPOINT_ITEMS
        method = HttpMethod.Get
    }.requireBody()

// POST/PATCH/DELETE без тела
override suspend fun createItem(request: XxxCreateRequest) {
    apiClient.request<NetworkEntity<Unit>> {
        endpoint = ENDPOINT_CREATE
        method = HttpMethod.Post
        body = request
    }.isSuccessfulExecute()
}
```

---

## ApiMapper — эталонный паттерн

### Интерфейс — полный KDOC

```kotlin
/**
 * Сетевой маппер для работы с API X.
 *
 * Предоставляет доступ к эндпоинтам X через [ApiClient].
 */
internal interface XxxApiMapper {

    /**
     * Возвращает список элементов X.
     *
     * `GET /xxx`
     *
     * @return Body-объект [XxxBody] с результатами.
     * @throws NetworkException.Unauthorized При истёкшем / невалидном токене.
     * @throws NetworkException.NoConnection При отсутствии сети.
     * @throws NetworkException.HttpError При ошибке на стороне сервера.
     */
    suspend fun getItems(): XxxBody

    /**
     * Создаёт новый элемент X.
     *
     * `POST /xxx`
     *
     * @param request Тело запроса с данными нового элемента.
     * @throws NetworkException.Unauthorized При истёкшем / невалидном токене.
     * @throws NetworkException.NoConnection При отсутствии сети.
     * @throws NetworkException.HttpError При ошибке на стороне сервера.
     */
    suspend fun createItem(request: XxxCreateRequest)
}
```

### Реализация — только ссылка + эндпоинты в companion object

```kotlin
/**
 * Реализация [XxxApiMapper].
 *
 * @param apiClient DSL-клиент из CoreNetwork.
 */
internal class XxxApiMapperImpl(
    private val apiClient: ApiClient,
) : XxxApiMapper {

    override suspend fun getItems(): XxxBody =
        apiClient.request<XxxResponse> {
            endpoint = ENDPOINT_ITEMS
            method = HttpMethod.Get
        }.requireBody()

    override suspend fun createItem(request: XxxCreateRequest) {
        apiClient.request<NetworkEntity<Unit>> {
            endpoint = ENDPOINT_CREATE
            method = HttpMethod.Post
            body = request
        }.isSuccessfulExecute()
    }

    companion object {
        private const val ENDPOINT_ITEMS = "xxx/items"
        private const val ENDPOINT_CREATE = "xxx/create"
    }
}
```

**Правила ApiMapper:**
- Все строки эндпоинтов — **только** в `companion object` реализации; нигде больше.
- `ApiMapper` — `single<>` в Koin; принимает `ApiClient` через конструктор.
- Методы реализации — без KDOC (контракт задокументирован в интерфейсе).

---

## Конвертеры — расположение и формат

### domain/converter/ — domain↔data (без Compose-типов)

Один файл на сущность: `domain/converter/XxxConverter.kt`.

```kotlin
/** Конвертирует data-model [XxxBody] в domain-model [XxxDomain]. */
internal fun XxxBody.toDomain(): XxxDomain = XxxDomain(
    id = id,
    name = name,
)

/** Конвертирует domain-model [XxxDomain] в data-model [XxxCreateRequest]. */
internal fun XxxDomain.toRequest(): XxxCreateRequest = XxxCreateRequest(
    name = name,
)
```

### domain/converter/base/ — data-enum↔domain-type

Один файл на enum: `domain/converter/base/XxxKeyConverter.kt`.

```kotlin
/** Конвертирует data-enum [XxxKey] в domain-sealed [XxxDomain]. */
internal fun XxxKey.toDomain(): XxxDomain = when (this) {
    XxxKey.ALPHA -> XxxDomain.Alpha
    XxxKey.BETA  -> XxxDomain.Beta
}

/** Конвертирует domain-sealed [XxxDomain] в data-enum [XxxKey]. */
internal fun XxxDomain.toApiKey(): XxxKey = when (this) {
    XxxDomain.Alpha -> XxxKey.ALPHA
    XxxDomain.Beta  -> XxxKey.BETA
}
```

**Запрещено:** любые `Brush`, `DrawableResource`, `Color`, `StringResource` в `domain/converter/` или `domain/converter/base/`.

### presentation/Utils.kt — domain↔presentation (с Compose-типами)

Один файл на модуль. Region-комментарии на каждый экран/сущность.

```kotlin
package com.chknkv.feature.xxx.presentation

// -----------------------------
// XxxAll Region
// -----------------------------

internal fun XxxDomain.toUi(): XxxUi = XxxUi(
    id = id,
    iconRes = iconKey.toIconDrawableResource(),   // DrawableResource — только здесь
    brush = gradientKey.toGradientBrush(),         // Brush — только здесь
)

// -----------------------------
// XxxCreate Region
// -----------------------------

internal fun XxxCategoryUi.toDomain(): XxxCategory = when (this) { ... }
```

Именование extension-функций:
- `XxxDomain.toXxxUi()` — domain → UI model
- `XxxUi.toXxxDomain()` — UI model → domain
- `String.toXxxDomain()` — строковый ключ → domain type

---

---

## Naming Conventions

| Class | Convention | Example |
|-------|-----------|---------|
| Screen | `<Feature>Screen` | `ProfileScreen` |
| MVVM VM | `<Feature>ViewModel` | `ProfileViewModel` |
| MVI VM | `<Feature>Store` | `ProfileStore` |
| UI state | `<Feature>UiState` | `ProfileUiState` |
| Intent | `<Feature>Intent` | `ProfileIntent` |
| Side effect | `<Feature>SideEffect` | `ProfileSideEffect` |
| Interactor | `<Feature>Interactor` | `ProfileInteractor` |
| Repo interface | `<Feature>Repository` | `ProfileRepository` |
| Repo impl | `<Feature>RepositoryImpl` | `ProfileRepositoryImpl` |
| Ktor impl | `<Feature>ApiImpl` | `ProfileApiImpl` |
| DTO (запрос к серверу) | `<Feature>Request` | `ProfileRequest` |
| DTO (ответ сервера) | `<Feature>Response` | `ProfileResponse` |
| DB entity | `<Feature>Entity` | `ProfileEntity` |
| Domain model | plain | `Profile` |
| Koin module val | camelCase | `profileModule` |

---

## Choose MVVM vs MVI

- **MVVM** → CRUD, list-detail, settings, simple forms
- **MVI** → auth flows, wizards, multi-step, heavy side effects

When ambiguous — ask one question before generating.

---

## Pre-output Checklist

Apply before finalizing every response:

- [ ] No layer boundary violations
- [ ] No Hard Violations
- [ ] All public APIs have explicit return types
- [ ] No `var` where `val` suffices
- [ ] `data class` for all state/model types
- [ ] No magic strings — use `const val`
- [ ] `sealed interface` for Intent/SideEffect, `sealed class` for NetworkResult
- [ ] Extension functions over utility objects
- [ ] `collectAsStateWithLifecycle()` not `collectAsState()`
- [ ] Screen split into `Screen` (wires VM) + `Content` (pure, previewable)
- [ ] KDOC на каждом публичном и internal символе (русский язык)
- [ ] Interface (`Interactor`, `Repository`, `ApiMapper`) — полный KDOC на каждом методе
- [ ] Impl — только `/** Реализация [XxxInterface]. */` + `@param` на конструктор; методы не документируются
- [ ] Interactor interface + impl — в `domain/interactor/`
- [ ] Converter (data↔domain, без Compose) — top-level extension-функции в `domain/converter/`, по одному файлу на сущность
- [ ] Converter (domain↔presentation, с Compose-типами) — в `presentation/Utils.kt`, region-комментарии на каждый экран
- [ ] Repository interface + impl — в `data/repository/`; impl хранит `MutableSharedFlow<Unit>` для `updates`
- [ ] ApiMapper interface + impl — в `data/mapper/`; impl принимает `ApiClient` через конструктор
- [ ] DTO-модели: суффиксы `Request` (запрос) и `Response` (ответ); не `Dto`
- [ ] Response DTO наследует `NetworkEntity<XxxBody>()`; Body — отдельный `@Serializable data class`
- [ ] GET с телом → `.requireBody()`; POST/PATCH/DELETE без тела → `.isSuccessfulExecute()`
- [ ] Все эндпоинт-строки в `companion object` реализации ApiMapper; нигде больше
- [ ] base-конвертеры (data-enum↔domain-type) — в `domain/converter/base/XxxKeyConverter.kt`; без Compose-типов