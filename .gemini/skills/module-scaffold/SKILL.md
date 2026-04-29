---
name: module-scaffold
description: >
  Создаёт KMP-модуль (Feature или Core) для проекта на Kotlin Multiplatform + Compose Multiplatform.
  Генерирует полную файловую структуру на диске, build.gradle.kts, пустые strings.xml и подключает
  модуль в settings.gradle.kts. Используй этот скилл всякий раз, когда пользователь просит создать
  новый модуль, фичу, core-библиотеку или говорит что-то вроде "создай модуль X", "добавь фичу Y",
  "новый core-модуль Z", "scaffoldни модуль", "создай Feature/FeatureX", "создай Core/CoreX".
  ВСЕГДА запускай этот скилл при любом запросе на создание нового Gradle-модуля в KMP-проекте.
---

# KMP Module Scaffold Skill

## Шаг 0 — Получить параметры

Перед генерацией нужно знать три вещи. Всё остальное выводится автоматически.

| Параметр | Пример Feature | Пример Core |
|---|---|---|
| **Тип модуля** | `Feature` | `Core` |
| **Название** (PascalCase) | `Subscriptions` | `Network` |

**Как получить project name:**
Прочитать `settings.gradle.kts` → `rootProject.name`. Значение `"Anchor-App"` →
убрать суффикс `-App` → lowercase → `anchor`. Если неоднозначно — спросить.

Если тип модуля не указан явно — вывести из названия или пути:
- `FeatureX` / `Feature/FeatureX` → Feature
- `CoreX` / `Core/CoreX` → Core

---

## Шаг 1 — Вывести все имена

Показать пользователю **до** создания файлов:

### Feature-модуль (`{Name}` = `Subscriptions`, `{project}` = `anchor`)

| Артефакт | Формула | Результат                           |
|---|---|-------------------------------------|
| Директория | `Feature/Feature{Name}` | `Feature/FeatureSubscriptions`      |
| Gradle path | `:Feature:Feature{Name}` | `:Feature:FeatureSubscriptions`     |
| Android namespace | `com.chknkv.{project}.{name_lower}` | `com.chknkv.anchor.subscriptions`   |
| Kotlin package | `com.chknkv.{project}.{name_lower}` | `com.chknkv.anchor.subscriptions` |
| iOS xcfName | `feature-{name_lower}Kit` | `feature-subscriptionsKit`          |
| iOS isStatic | `false` | `false`                             |

### Core-модуль (`{Name}` = `Network`)

| Артефакт | Формула | Результат |
|---|---|---|
| Директория | `Core/Core{Name}` | `Core/CoreNetwork` |
| Gradle path | `:Core:Core{Name}` | `:Core:CoreNetwork` |
| Android namespace | `com.chknkv.core{name_lower}` | `com.chknkv.corenetwork` |
| Kotlin package | `com.chknkv.{name_lower}` | `com.chknkv.network` |
| iOS xcfName | `core-{name_lower}Kit` | `core-networkKit` |
| iOS isStatic | `true` | `true` |

> `{name_lower}` = название в нижнем регистре без разделителей. `Subscriptions` → `subscriptions`.

---

## Шаг 2 — Создать файловую структуру

Выполнить `mkdir -p` для каждой директории.

### Feature-модуль

```
Feature/Feature{Name}/
├── build.gradle.kts
└── src/commonMain/
    ├── composeResources/
    │   ├── drawable/
    │   ├── values/
    │   │   └── strings.xml          ← создать пустой
    │   └── values-ru/
    │       └── strings.xml          ← создать пустой
    └── kotlin/com/chknkv/{project}/{name_lower}/
        ├── data/
        │   ├── mapper/
        │   └── repository/
        ├── di/
        ├── domain/
        │   ├── converter/
        │   └── interactor/
        ├── models/
        │   ├── data/
        │   ├── domain/
        │   └── presentation/
        ├── navigation/
        └── presentation/
```

### Core-модуль

```
Core/Core{Name}/
├── build.gradle.kts
└── src/commonMain/
    ├── composeResources/
    │   ├── drawable/
    │   ├── values/
    │   │   └── strings.xml          ← создать пустой
    │   └── values-ru/
    │       └── strings.xml          ← создать пустой
    └── kotlin/com/chknkv/{name_lower}/
```

> Core-модуль не содержит `di/data/domain/models/presentation/navigation` — только корневой пакет.
> Kotlin-директории **пустые** (никаких `.kt`-заглушек). Только `strings.xml` создаются.
> Для Feature-модуля `mkdir -p` создаёт и все вложенные пакеты: `data/mapper`, `data/repository`, `domain/interactor`, `domain/converter`.

---

## Шаг 3 — Содержимое strings.xml

Оба файла (EN и RU) создаются одинаковыми:

```xml
<resources>
</resources>
```

---

## Шаг 4 — build.gradle.kts

### Feature-модуль

```kotlin
description = "Feature module: {Name}."

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "com.chknkv.{project}.{name_lower}"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    val xcfName = "feature-{name_lower}Kit"
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = xcfName
            isStatic = false
        }
    }

    sourceSets {

        commonMain.dependencies {
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.components.resources)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.core)
            implementation(libs.koin.core.viewmodel)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.napier)

            implementation(project(":Core:CoreDesignSystem"))
            implementation(project(":Core:CoreUtils"))
        }

        iosMain.dependencies {
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.napier)
        }
    }
}
```

### Core-модуль

```kotlin
description = "Core module: {Name}."

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "com.chknkv.core{name_lower}"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    val xcfName = "core-{name_lower}Kit"
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.components.resources)
        }
    }
}
```

---

## Шаг 5 — Обновить settings.gradle.kts

**Найти файл:**
```bash
find . -maxdepth 2 -name "settings.gradle.kts" | head -1
```

**Вставить строку `include` внутрь нужного региона** — перед закрывающим комментарием:

Для Feature-модуля — найти `// END Feature Region` и вставить строку перед ним:
```
// Feature Region
include(":Feature:FeatureSubscriptions")    ← добавить здесь
// END Feature Region
```

Для Core-модуля — найти `// END Core Region` и вставить перед ним:
```
// Core Region
include(":Core:CoreNetwork")                ← добавить здесь
// END Core Region
```

**Перед вставкой — проверить на дубль:**
```bash
grep -q '":Feature:Feature{Name}"' settings.gradle.kts && echo "ALREADY EXISTS"
```

Если строка уже присутствует — не добавлять, сообщить пользователю.

---

## Шаг 6 — Итоговый отчёт

После создания показать точное дерево и статусы:

```
✓ Создан модуль Feature/FeatureSubscriptions

  Gradle path : :Feature:FeatureSubscriptions
  Namespace   : com.chknkv.anchor.subscriptions
  Package     : com.chknkv.anchor.subscriptions
  iOS target  : feature-subscriptionsKit (isStatic = false)

Файлы:
  Feature/FeatureSubscriptions/build.gradle.kts                              ← создан
  Feature/FeatureSubscriptions/src/commonMain/composeResources/values/strings.xml     ← создан
  Feature/FeatureSubscriptions/src/commonMain/composeResources/values-ru/strings.xml  ← создан
  Feature/FeatureSubscriptions/src/commonMain/composeResources/drawable/               ← папка
  Feature/FeatureSubscriptions/src/commonMain/kotlin/com/chknkv/anchor/subscriptions/
    data/mapper/ data/repository/ di/
    domain/interactor/ domain/converter/
    models/data/ models/domain/ models/presentation/
    navigation/ presentation/                                                  ← папки

  settings.gradle.kts                                                         ← обновлён
```

---

## Алгоритм выполнения (чеклист)

1. [ ] Прочитать `settings.gradle.kts` → получить project name
2. [ ] Показать пользователю выведенные имена (namespace, package, xcfName, path)
3. [ ] Выполнить `mkdir -p` для всех директорий, включая `data/mapper`, `data/repository`, `domain/interactor`, `domain/converter`
4. [ ] Записать `build.gradle.kts`
5. [ ] Записать оба `strings.xml` (`values/` и `values-ru/`)
6. [ ] Проверить отсутствие дубля в `settings.gradle.kts`
7. [ ] Вставить `include(...)` в нужный регион
8. [ ] Вывести итоговый отчёт

---

## Примеры вызова

| Запрос | Тип | Name | Действие |
|---|---|---|---|
| "создай Feature/FeatureSubscriptions" | Feature | Subscriptions | project = прочитать из settings |
| "новый Core-модуль CoreNetwork" | Core | Network | project не нужен |
| "scaffoldни FeatureWelcome для anchor" | Feature | Welcome | project = anchor |
| "добавь CoreStorage" | Core | Storage | project не нужен |
| "создай модуль для онбординга" | Feature | Onboarding | спросить если неоднозначно |
