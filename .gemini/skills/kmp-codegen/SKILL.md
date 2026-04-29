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
| Kotlin | 2.1.x |
| Compose Multiplatform | latest stable |
| DI | Koin 4.x |
| Network | Ktor 3.x |
| Local DB | SQLDelight 2.x |
| Async | Coroutines + Flow only |
| Error model | `sealed class NetworkResult<out T>` |
| Presentation | MVVM or MVI per feature |
| Navigation | Jetpack Navigation (androidMain) |
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
| Repository interface | `commonMain/domain/` | No suspend — use `Flow` |
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
| Класс / interface / object | KDOC на объявлении: назначение в 1–2 предложениях |
| Конструктор с параметрами | `@param` на каждый параметр |
| `fun` (публичная / internal) | KDOC: что делает + `@param` + `@return` если не `Unit` |
| `val` / `var` (публичный / internal) | Однострочный KDOC |
| `sealed interface` / `sealed class` варианты | KDOC на каждом варианте |
| `data class` поля через конструктор | `@param` в KDOC класса |

**Язык KDOC — русский.** Идентификаторы и технические термины — на английском.

**Запрещено:**
- Комментарий `// ...` вместо `/** ... */` для публичного/internal API
- KDOC, который просто дублирует имя метода (`/** Возвращает items. */ fun getItems()`)
- Пустые KDOC `/** */`

**Пример:**
```kotlin
/**
 * Загружает список привычек пользователя из удалённого источника.
 *
 * @param userId Идентификатор пользователя.
 * @return Flow с результатом запроса.
 */
fun getUserHabits(userId: Int): Flow<NetworkResult<List<Habit>>>
```

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
❌ Repository interface with suspend — use Flow<NetworkResult<T>>
❌ runBlocking in production code
❌ !! without explicit justifying comment
❌ Mutable collections exposed from ViewModel/Store
❌ androidContext() in commonMain Koin module
❌ Mixing MVVM and MVI within one feature
❌ UI-конвертер (domain→presentation с Compose-типами) в domain/converter/
❌ Отдельный файл XxxUiConverter.kt в domain/ или data/
```

---

## NetworkResult (canonical — do not vary)

```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: AppException) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(cause: Throwable) : AppException("Network error", cause)
    class ServerException(val code: Int, val body: String) : AppException("Server error $code")
    class UnknownException(cause: Throwable) : AppException("Unknown", cause)
}
```

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
| DTO | `<Feature>Dto` | `ProfileDto` |
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
- [ ] Interactor и его реализация — в `domain/interactor/`
- [ ] Converter (data↔domain, без Compose) — в `domain/converter/`
- [ ] Converter (domain↔presentation, с Compose-типами) — в `presentation/Utils.kt`
- [ ] Repository interface — в `domain/`, реализация — в `data/repository/`
- [ ] ApiMapper interface + impl — в `data/mapper/`