# FeatureAddiction

Модуль управления привычками: выбор привычек при онбординге, список привычек пользователя, создание новой привычки.

---

## Категории привычек

Категории — это строковые ключи, которыми оперирует бэкенд. На каждом слое своё представление:

| Слой | Тип | Описание |
|------|-----|----------|
| Data | `String` | Строковый ключ из API (`"lifestyle"`, `"sport"`, …) |
| Domain | `AddictionCategory` enum | Типобезопасный enum в `models/domain/AddictionCategory.kt` |
| Presentation | `AddictionCategoryUi` enum | UI-enum; заголовок берётся из строковых ресурсов через `toTitleStringResource()` |

Цепочка конвертации:

```
API String → AddictionCategory (domain) → AddictionCategoryUi (UI)
                                            ↓
                                  stringResource(category.toTitleStringResource())
```

И в обратную сторону (при создании/обновлении привычки):

```
AddictionCategoryUi → AddictionCategoryUi.toDomain() → AddictionCategory
                                                              ↓
                                                   AddictionCategory.toApiKey()
                                                              ↓
                                                     API String ("sport")
```

### Доступные категории

| Ключ API | `AddictionCategory` | `AddictionCategoryUi` | Строковый ресурс |
|----------|--------------------|-----------------------|-----------------|
| `"lifestyle"` | `LIFESTYLE` | `LIFESTYLE` | `habit_group_lifestyle_title` |
| `"health"` | `HEALTH` | `HEALTH` | `habit_group_health_title` |
| `"sport"` | `SPORT` | `SPORT` | `habit_group_sport_title` |
| `"productivity"` | `PRODUCTIVITY` | `PRODUCTIVITY` | `habit_group_productivity_title` |
| `"finance"` | `FINANCE` | `FINANCE` | `habit_group_finance_title` |
| `"relationships"` | `RELATIONSHIPS` | `RELATIONSHIPS` | `habit_group_relationships_title` |
| `"other"` | `OTHER` | `OTHER` | `habit_group_other_title` |

Конвертеры: `String.toAddictionCategory()` + `AddictionCategory.toApiKey()` — в `domain/converter/AddictionCategoryConverter.kt`.
UI-конвертеры: `AddictionCategory.toUi()`, `AddictionCategoryUi.toDomain()`, `AddictionCategoryUi.toTitleStringResource()` — в `presentation/Utils.kt`.

### Группы привычек

`AddictionGroupResponse` использует строковый ключ `categoryKey: String` (не числовой ID).
`AddictionGroup` (domain) хранит `category: AddictionCategory`.
`AddictionGroupUi` хранит `category: AddictionCategoryUi`; заголовок группы разрешается composable-ом:

```kotlin
title = stringResource(group.category.toTitleStringResource())
```

---

## Строковые ключи: иконки и градиенты

Бэкенд не оперирует Compose-типами (`DrawableResource`, `Brush`). Вместо этого клиент и сервер договариваются о **строковых ключах** — простых строках вроде `"ic_habbit_sport"` или `"green"`. Конвертация ключей в Compose-объекты происходит исключительно в `presentation/Utils.kt`.

Такой подход применяется и для иконок, и для градиентов — по одной и той же схеме.

---

## Иконки

### Доступные ключи

| Ключ | Drawable-ресурс | Категория |
|---|---|---|
| `ic_habbit_lifestyle` | `Res.drawable.ic_habbit_lifestyle` | Образ жизни |
| `ic_habbit_health` | `Res.drawable.ic_habbit_health` | Здоровье |
| `ic_habbit_sport` | `Res.drawable.ic_habbit_sport` | Спорт |
| `ic_habbit_productivity` | `Res.drawable.ic_habbit_productivity` | Продуктивность |
| `ic_habbit_finance` | `Res.drawable.ic_habbit_finance` | Финансы |
| `ic_habbit_relationships` | `Res.drawable.ic_habbit_relationships` | Отношения & Развитие |

Любой неизвестный ключ → `Res.drawable.ic_habit_placeholder` (без краша).

### Конвертер (Utils.kt)

```kotlin
// presentation/Utils.kt
internal fun String.toIconDrawableResource(): DrawableResource = when (this) {
    "ic_habbit_lifestyle"     -> Res.drawable.ic_habbit_lifestyle
    "ic_habbit_sport"         -> Res.drawable.ic_habbit_sport
    // ...
    else                      -> Res.drawable.ic_habit_placeholder
}
```

### Создание привычки: клиент → сервер

Пользователь выбирает иконку на экране создания привычки. ViewModel хранит ключ как строку. При отправке запроса:

```
POST /habits
{
  "name": "Бег по утрам",
  "iconKey": "ic_habbit_sport",
  "gradientKey": "orange",
  ...
}
```

Бэкенд сохраняет `"iconKey"` как есть — просто строка в базе данных.

### Отображение в списке: сервер → клиент

Бэкенд возвращает список привычек:

```
GET /habits/user
[
  {
    "id": 34,
    "name": "Бег",
    "iconKey": "ic_habbit_sport",
    "gradientKey": "orange",
    "controlDays": 18
  }
]
```

Репозиторий создаёт `UserAddiction(iconKey = "ic_habbit_sport", ...)`. При конвертации в UI-модель `UserAddiction.toUi()` вызывает `"ic_habbit_sport".toIconDrawableResource()` и получает `Res.drawable.ic_habbit_sport`.

---

## Градиенты

### Доступные ключи

| Ключ | Цвет | Описание |
|---|---|---|
| `"gray"` | Серый | Нейтральный, для категории "Другое" |
| `"green"` | Зелёный | Образ жизни |
| `"blue"` | Синий | Здоровье |
| `"indigo"` | Индиго | Продуктивность |
| `"purple"` | Фиолетовый | Финансы |
| `"pink"` | Розовый | Отношения & Развитие |
| `"orange"` | Оранжевый | Спорт |
| `"dark_orange"` | Тёмно-оранжевый | — |
| `"red"` | Красный | — |
| `"dark_red"` | Тёмно-красный | — |
| `"black"` | Чёрный | — |

Любой неизвестный ключ → серый градиент (без краша).

### Конвертер (GradientTokenMapper.kt в CoreDesignSystem)

```kotlin
// CoreDesignSystem/theme/GradientTokenMapper.kt
fun String.toGradientBrush(): Brush = when (this) {
    "green" -> Brush.verticalGradient(listOf(Green3, Green0))
    "blue"  -> Brush.verticalGradient(listOf(Blue3, Blue0))
    "orange"-> Brush.verticalGradient(listOf(Orange2, Orange0))
    // ...
    else    -> Brush.verticalGradient(listOf(Gray0, Gray2))
}
```

Константы (`GRADIENT_GREEN = "green"` и т.д.) объявлены в том же файле — их используют ViewModel и Repository вместо строк напрямую:

```kotlin
// ViewModel / Repository — используем константы, не строки вручную
iconKey = "ic_habbit_sport", gradient = GRADIENT_ORANGE
```

### Создание привычки: клиент → сервер

```
POST /habits
{
  "name": "Бег по утрам",
  "iconKey": "ic_habbit_sport",
  "gradientKey": "orange"
}
```

### Отображение в списке: сервер → клиент

```
GET /habits/user
[
  {
    "id": 34,
    "iconKey": "ic_habbit_sport",
    "gradientKey": "orange"
  }
]
```

`"orange".toGradientBrush()` возвращает `Brush` — оранжевый вертикальный градиент для фона иконки в карточке.

---

## Полный цикл создания привычки

```
Пользователь на экране создания:
  выбирает иконку  →  iconKey = "ic_habbit_sport"
  выбирает цвет    →  gradientKey = "orange"
  вводит название  →  "Бег по утрам"
  выбирает категорию → SPORT

Нажимает «Создать»:
  ViewModel формирует AddictionCreate(
      name = "Бег по утрам",
      iconKey = "ic_habbit_sport",
      gradientKey = "orange",
      category = AddictionCategory.SPORT  ← конвертирован из AddictionCategoryUi.SPORT
  )

  Отправляет через Interactor → Repository → POST /habits

Бэкенд сохраняет всё как строки.

Следующий GET /habits/user возвращает эти же строки.
  Repository создаёт UserAddiction(
      iconKey = "ic_habbit_sport",
      gradient = "orange",
      ...
  )

  Utils.kt конвертирует при отображении:
      "ic_habbit_sport".toIconDrawableResource() → Res.drawable.ic_habbit_sport
      "orange".toGradientBrush()                 → Brush (оранжевый градиент)
```

---

## Контракт для бэкенда

Поля, которые клиент ожидает в модели привычки пользователя:

| Поле | Тип | Пример | Примечание |
|---|---|---|---|
| `iconKey` | `String` | `"ic_habbit_sport"` | Один из ключей из таблицы иконок |
| `gradientKey` | `String` | `"orange"` | Один из ключей из таблицы градиентов |

Поля, которые клиент отправляет при создании привычки:

| Поле | Тип | Пример | Примечание |
|---|---|---|---|
| `iconKey` | `String` | `"ic_habbit_sport"` | Ключ выбранной иконки |
| `gradientKey` | `String` | `"orange"` | Ключ выбранного цвета |

Оба поля хранятся и передаются как строки. Клиент сам конвертирует их в нужные Compose-типы при отрисовке.

---

## Правила расширения

Чтобы добавить новую иконку:
1. Добавить SVG/XML в `composeResources/drawable/` с именем `ic_habbit_<name>.xml`
2. Добавить строку в `String.toIconDrawableResource()` в `Utils.kt`
3. Добавить ключ в `AVAILABLE_ICON_KEYS` в `AddictionCreateViewModel`
4. Согласовать новый ключ с бэкендом

Чтобы добавить новый градиент:
1. Добавить цвета в `Colors.kt` в CoreDesignSystem
2. Добавить ветку в `String.toGradientBrush()` в `GradientTokenMapper.kt`
3. Объявить константу `GRADIENT_<NAME> = "<name>"`
4. Добавить ключ в `AVAILABLE_GRADIENT_KEYS` в `AddictionCreateViewModel`
5. Согласовать новый ключ с бэкендом
