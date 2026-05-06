# Core/CoreNetwork

KMP-библиотечный модуль. Единственный источник истины для HTTP-взаимодействия с Anchor API.
Предоставляет `ApiClient` DSL всем Feature-модулям. Feature-модули **не импортируют Ktor напрямую**.

## Публичный API модуля

| Тип | Назначение |
|-----|-----------|
| `ApiClient` | DSL-фасад для выполнения запросов |
| `NetworkException` | sealed class сетевых ошибок |
| `ErrorResponse` | тело ошибки `{ code, message }` при HTTP 4xx/5xx |
| `ItemsResponse<T>` | конверт `{ items: List<T> }` для списочных эндпоинтов |
| `TokenStorage` | `saveTokens(access, refresh)` — Feature сохраняют токены после авторизации |
| `coreNetworkModule` | Koin-модуль с реальным HTTP-клиентом |
| `coreMockNetworkModule` | Koin-модуль с `MockEngine` — **только разработка/тестирование** |

`TokenRepository` — internal. Feature-модули работают **только** через `TokenStorage`.
Всё остальное (`HttpClient`, `KtorConfig`, `TokenRepositoryImpl`) — internal.

## REST-контракт API

HTTP-статус — единственный источник истины об успехе/ошибке.

| Код | Смысл | Поведение клиента |
|-----|-------|-------------------|
| **200 OK** | Успех с телом | `execute<T>` десериализует в `T` |
| **204 No Content** | Успех без тела | `execute<Unit>` — тело не читается |
| **400 Bad Request** | Невалидный запрос | `NetworkException.BadRequest(error?)` |
| **401 Unauthorized** | Токен невалиден | bearer-плагин → refresh; повторный 401 → разлогин |
| **403 Forbidden** | Нет прав | `NetworkException.Forbidden(error?)` |
| **404 Not Found** | Ресурс не существует | `NetworkException.NotFound(error?)` |
| **409 Conflict** | Конфликт состояния | `NetworkException.Conflict(error?)` |
| **5xx** | Серверная ошибка | `NetworkException.ServerError(code, error?)` |

Тело ошибки — `ErrorResponse(code: String, message: String)`. Поля `error?.code` и `error?.message` **не логировать** (PII).

## Как добавить новый запрос

```kotlin
// GET с телом ответа
val response: MyResponse = apiClient.execute<MyResponse> {
    endpoint = "resource/path"
    method   = HttpMethod.Get
    query("key" to value)   // null-значения игнорируются
}

// POST с телом запроса, ответ 200
val result: MyResponse = apiClient.execute<MyResponse> {
    endpoint = "resource"
    method   = HttpMethod.Post
    body     = myRequest     // @Serializable
}

// Мутация, ответ 204 No Content
apiClient.execute<Unit> {
    endpoint = "resource/$id"
    method   = HttpMethod.Delete
}
```

Типовой аргумент **всегда указывается явно**: `execute<MyResponse>`, `execute<Unit>`.

## NetworkException — иерархия

```kotlin
sealed class NetworkException {
    data class  BadRequest(val error: ErrorResponse?)                 // 400
    data object Unauthorized                                          // 401
    data class  Forbidden(val error: ErrorResponse?)                  // 403
    data class  NotFound(val error: ErrorResponse?)                   // 404
    data class  Conflict(val error: ErrorResponse?)                   // 409
    data class  ServerError(val code: Int, val error: ErrorResponse?) // 5xx
    data class  HttpError(val code: Int, val error: ErrorResponse?)   // прочие
    data object NoConnection                                          // timeout/no internet
    class       Unknown(cause: Throwable)                             // неизвестная ошибка
}
```

`CancellationException` **не перехватывается** — пробрасывается наружу.

## Koin: что требуется от app-модуля

`coreNetworkModule` **не регистрирует** `baseUrl` сам — это ответственность app-уровня:

```kotlin
// Android — AnchorApplication.kt
single<String>(named("anchorBaseUrl")) { BuildConfig.BASE_URL }

// iOS — AnchorMainViewController.kt
single<String>(named("anchorBaseUrl")) { resolveBaseUrl() }
```

`HttpClient` зарегистрирован с квалификатором `named("anchorHttpClient")`.
Не использовать `get<HttpClient>()` без квалификатора — получите чужой клиент.

`coreNetworkModule` регистрирует `TokenStorage` как отдельный тип через `get<TokenRepository>()`:
```kotlin
single<TokenRepository> { TokenRepositoryImpl(createSecureTokenSettings()) }
single<TokenStorage> { get<TokenRepository>() }
```

### coreMockNetworkModule

Альтернатива `coreNetworkModule` для разработки без реального сервера:
- Использует `MockEngine` + `HttpCallValidator` (бросает `ResponseException` для non-2xx)
- JSON-стабы читаются из `composeResources/files/mock/` через `Res.readBytes`
- Регистрирует `HttpClient(named("anchorHttpClient"))` + `ApiClient`
- **Не регистрирует** `TokenRepository` / `TokenStorage` — авторизация не нужна для мока
- Переключение: в `SharedModule` заменить `includes(coreNetworkModule)` → `includes(coreMockNetworkModule)`
- Добавление нового стаба: положить `<name>.json` в `composeResources/files/mock/` и добавить ветку в `MockApiResponses.resolve()`
- `resolve()` возвращает `Pair<String, HttpStatusCode>` — для 204 ответов возвращать `"" to HttpStatusCode.NoContent`

## Токены: как это работает

Ktor Auth-плагин (`bearer {}`) управляет токенами полностью автоматически:

1. `loadTokens` — читает пару access/refresh из защищённого хранилища перед каждым запросом.
2. При HTTP 401 — автоматически вызывает `POST auth/refresh` с текущим refresh-токеном.
3. При успехе — сохраняет новую пару токенов и **повторяет оригинальный запрос**.
4. При 400/401/403 от `/auth/refresh` — удаляет токены (`clearTokens`), пользователь разлогинен.
5. При 5xx/429/сетевой ошибке — токены **сохраняются**, повтор произойдёт позже.

Внутренний Mutex Ktor-плагина сериализует конкурентные refresh-вызовы — один запрос, остальные ждут.
`TokenRepositoryImpl` имеет собственный `Mutex` для атомарности read/write операций.

**Запрещено:** вызывать методы `TokenRepositoryImpl` рекурсивно из одной корутины — `Mutex` не реентрантен.

## Хранение токенов

| Платформа | Хранилище | Шифрование |
|-----------|-----------|------------|
| Android | `EncryptedSharedPreferences` (`anchor_secure_tokens`) | AES256-GCM (Android Keystore) |
| iOS | `KeychainSettings` (service `com.chknkv.anchor.tokens`) | Системный Keychain |

## expect/actual точки расширения

| expect | Android actual | iOS actual |
|--------|---------------|------------|
| `createAnchorHttpClient(...)` | OkHttp engine | Darwin engine |
| `createSecureTokenSettings()` | EncryptedSharedPreferences | KeychainSettings |
| `ktorLogLevel: LogLevel` | `LogLevel.NONE` | `LogLevel.NONE` |
| `jsonConfig: Json` | `ignoreUnknownKeys=true, isLenient=false` | то же |

**Нельзя** устанавливать `LogLevel.BODY` или `LogLevel.ALL` — `applyAnchorConfig` бросит исключение.
Причина: тело ответа `/auth/refresh` содержит JWT-токены, редакция regex покрывает только заголовки.

## Правила безопасности

- `RefreshRequest` / `RefreshResponse` — **не** `data class`: исключает автогенерацию `toString` с токенами
- Bearer-заголовки в логах автоматически редактируются → `Bearer [REDACTED]`
- В production `LogLevel.NONE` — Ktor-логов нет вообще
- `sendWithoutRequest` гарантирует: токен прикрепляется **только** к хосту из `baseUrl`
- `iosBaseUrl` — `internal`; iOS-приложение читает `BASE_URL` из `NSBundle` самостоятельно

## Запрещённые паттерны

```
❌ Импортировать io.ktor.* в Feature-модулях (кроме io.ktor.http.HttpMethod — TODO: CoreNetwork должен реэкспортировать HttpMethod)
❌ Регистрировать HttpClient без named("anchorHttpClient") квалификатора
❌ Добавлять BuildConfig или платформенный код в commonMain
❌ Делать RefreshRequest / RefreshResponse data class
❌ Устанавливать LogLevel.BODY или LogLevel.ALL
❌ Хранить токены в обычном Settings() / SharedPreferences / NSUserDefaults
❌ Вызывать методы TokenRepository рекурсивно внутри одного withLock
❌ Добавлять новые expect без actual для обеих платформ
❌ Использовать coreMockNetworkModule в production-сборках
❌ Внедрять TokenRepository в Feature-модули — только TokenStorage
❌ Логировать error.code / error.message из ErrorResponse (PII)
❌ Наследоваться от NetworkEntity — класс удалён; Response-классы должны быть flat data class
```

## Таймауты (NetworkConstants)

| Константа | Значение |
|-----------|----------|
| `CONNECT_TIMEOUT_MS` | 10 000 мс |
| `REQUEST_TIMEOUT_MS` | 30 000 мс |
| `SOCKET_TIMEOUT_MS` | 30 000 мс |
| `REFRESH_ENDPOINT` | `"auth/refresh"` |

## Известные ограничения (TODO перед production)

- **Certificate pinning** не реализован — ждёт production-сертификат
- **iOS Keychain accessibility** — `KeychainSettings` не гарантирует `kSecAttrAccessibleWhenUnlockedThisDeviceOnly`; при необходимости заменить на прямую обёртку над `Security.framework`
