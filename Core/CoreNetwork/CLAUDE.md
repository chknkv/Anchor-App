# Core/CoreNetwork

KMP-библиотечный модуль. Единственный источник истины для HTTP-взаимодействия с Anchor API.
Предоставляет `ApiClient` DSL всем Feature-модулям. Feature-модули **не импортируют Ktor напрямую**.

## Публичный API модуля

| Тип | Назначение |
|-----|-----------|
| `ApiClient` | DSL-фасад для выполнения запросов |
| `NetworkException` | sealed class сетевых ошибок |
| `NetworkEntity<T>` | универсальный конверт ответа Anchor API |
| `NetworkEntity<T>.requireBody()` | извлечь тело или бросить `IllegalStateException` |
| `NetworkEntity<*>.isSuccessfulExecute()` | проверить успешность void-запроса |
| `coreNetworkModule` | Koin-модуль с реальным HTTP-клиентом |
| `coreMockNetworkModule` | Koin-модуль с `MockEngine` — **только разработка/тестирование** |

Всё остальное — `internal`. `TokenRepository`, `HttpClient`, `KtorConfig` — детали реализации.

## NetworkEntity<T>: конверт ответа

Все эндпоинты Anchor API возвращают:
```json
{ "success": true, "body": { ... } }
{ "success": false, "message": "Описание ошибки", "timeout": "..." }
```

```kotlin
@Serializable
open class NetworkEntity<T>(
    val success: Boolean = true,
    val body: T?         = null,
    val message: String? = null,
    val timeout: String? = null,
)
```

**Наследование DTO от `NetworkEntity<BodyType>`** — рекомендуемый паттерн для всех Response-классов:

```kotlin
// models/data/, internal, @Serializable
@Serializable
internal class AddictionAllGroupsResponse : NetworkEntity<AddictionAllGroupsBody>()

// В Mapper:
override suspend fun getAllGroups(): AddictionAllGroups {
    val response = apiClient.request<AddictionAllGroupsResponse> { endpoint = "client/addictions/all" }
    return response.requireBody().toDomain()
}

// Для void-эндпоинта (не возвращает body):
override suspend fun deleteAddiction(id: Int) {
    val response = apiClient.request<NetworkEntity<Unit>> {
        endpoint = "client/addictions/delete/$id"
        method = HttpMethod.Delete
    }
    response.isSuccessfulExecute()
}
```

- `requireBody()` бросает `IllegalStateException` если `success==false` или `body==null`
- `isSuccessfulExecute()` бросает `IllegalStateException` если `success==false`
- Текст ошибки берётся из `message ?: timeout ?: "Unknown server error"`

## Как добавить новый запрос в Feature-модуле

```kotlin
class MyApiMapperImpl(private val apiClient: ApiClient) : MyApiMapper {
    override suspend fun getItems(): List<ItemResponse> = apiClient.request {
        endpoint = "items"
    }
    override suspend fun createItem(body: ItemRequest): ItemResponse = apiClient.request {
        endpoint = "items"
        method   = HttpMethod.Post
        body     = body
    }
    override suspend fun search(query: String, page: Int): List<ItemResponse> = apiClient.request {
        endpoint = "items/search"
        query("q" to query, "page" to page)
    }
}
```

- `endpoint` — relative path без ведущего слэша, добавляется к `baseUrl`
- `query(...)` — `null`-значения игнорируются, в URL не попадают
- `body` — должен быть `@Serializable`; `Content-Type: application/json` выставляется автоматически
- Тип `T` в `request<T>` — тип десериализованного ответа, тоже должен быть `@Serializable`

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

### coreMockNetworkModule

Альтернатива `coreNetworkModule` для разработки без реального сервера:
- Использует `MockEngine` вместо OkHttp/Darwin
- JSON-стабы читаются из `composeResources/files/mock/` через `Res.readBytes`
- Регистрирует `HttpClient(named("anchorHttpClient"))` + `ApiClient`
- **Не регистрирует** `TokenRepository` — авторизация не нужна для мока
- Переключение: в `SharedModule` заменить `includes(coreNetworkModule)` → `includes(coreMockNetworkModule)`
- Добавление нового стаба: положить `<name>.json` в `composeResources/files/mock/` и добавить ветку в `MockApiResponses.resolve()`

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

## Обработка ошибок в Feature-модулях

```kotlin
try {
    val result = apiClient.request<MyResponse> { endpoint = "..." }
} catch (e: NetworkException.Unauthorized) {
    // разлогинить пользователя
} catch (e: NetworkException.NoConnection) {
    // показать экран "нет интернета"
} catch (e: NetworkException.HttpError) {
    // e.code — HTTP статус, e.description — текст статуса
} catch (e: NetworkException.Unknown) {
    // непредвиденная ошибка
}
// CancellationException НЕ перехватывается внутри ApiClient — пробрасывается наружу
```

## Запрещённые паттерны

```
❌ Импортировать io.ktor.* в Feature-модулях (кроме HttpMethod через ApiRequestBuilder)
❌ Регистрировать HttpClient без named("anchorHttpClient") квалификатора
❌ Добавлять BuildConfig или платформенный код в commonMain
❌ Делать RefreshRequest / RefreshResponse data class
❌ Устанавливать LogLevel.BODY или LogLevel.ALL
❌ Хранить токены в обычном Settings() / SharedPreferences / NSUserDefaults
❌ Вызывать методы TokenRepository рекурсивно внутри одного withLock
❌ Добавлять новые expect без actual для обеих платформ
❌ Использовать coreMockNetworkModule в production-сборках
```

## Таймауты (NetworkConstants)

| Константа | Значение |
|-----------|----------|
| `CONNECT_TIMEOUT_MS` | 10 000 мс |
| `REQUEST_TIMEOUT_MS` | 30 000 мс |
| `SOCKET_TIMEOUT_MS` | 30 000 мс |
| `REFRESH_ENDPOINT` | `"auth/refresh"` |

## Известные ограничения (TODO перед production)

- **Certificate pinning** не реализован — ждёт production-сертификат (добавить в `network_security_config.xml` и iOS URLSession challenge handler)
- **iOS Keychain accessibility** — `KeychainSettings` не гарантирует `kSecAttrAccessibleWhenUnlockedThisDeviceOnly` на уровне библиотеки; при необходимости заменить на прямую обёртку над `Security.framework`
