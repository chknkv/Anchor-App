# Specialized Agents — Anchor App

Полный реестр 7 специализированных субагентов проекта. Каждый агент имеет детальные инструкции в `.claude/agents/<name>.md`.

## Быстрый справочник

### 🏛️ mobile-architect — The Guardian of Structure
**Файл:** `.claude/agents/mobile-architect.md`  
**Специализация:** Архитектура KMP, граф зависимостей, дизайн публичного API  
**Призывать когда:**
- Нужно спроектировать новый модуль (Feature или Core)
- Вопросы о зависимостях: "куда положить этот код?"
- Риск cyclic dependency или нарушения слоёв
- Review build.gradle.kts, settings.gradle.kts

**Команда:**
```
Agent(subagent_type: "mobile-architect", 
       prompt: "Спроектируй новый Feature/FeatureX: граф зависимостей, API surface, build.gradle.kts")
```

---

### 🔧 ai-process-manager — The Infrastructure Engineer
**Файл:** `.claude/agents/ai-process-manager.md`  
**Специализация:** CLAUDE.md / GEMINI.md, Skills, hooks, автоматизация  
**Призывать когда:**
- Документация устарела (новый файл, новый UiAction)
- Нужен новый Custom Skill
- Нужен bash/python скрипт для автоматизации
- Конфигурация .claude/settings.json

**Команда:**
```
Agent(subagent_type: "ai-process-manager", 
       prompt: "Обнови CLAUDE.md для FeatureX после рефактора")
```

---

### 💻 mobile-coder — The Implementation Expert
**Файл:** `.claude/agents/mobile-coder.md`  
**Специализация:** Kotlin код, ViewModel, Interactor, DI, Flow логика  
**Призывать когда:**
- Нужно написать ViewModel, Interactor, Repository
- Реализовать MVI-слой (UiAction → UiResult → UiState)
- Wiring Koin DI
- Coroutine / Flow issues
- Refactor Kotlin-код

**Команда:**
```
Agent(subagent_type: "mobile-coder", 
       prompt: "Напиши CreatePasscodeViewModel с Pattern B (sealed UiState)")
```

---

### 🎨 mobile-ui-designer — The Visual Engineer
**Файл:** `.claude/agents/mobile-ui-designer.md`  
**Специализация:** Compose Multiplatform, Tokens, анимации, адаптив, accessibility  
**Призывать когда:**
- Нужен новый Screen / composable
- Анимации, transitions
- Использование design tokens
- Recomposition optimization
- Новый component в CoreDesignSystem

**Команда:**
```
Agent(subagent_type: "mobile-ui-designer", 
       prompt: "Напиши AuthorizationScreen с Sheet для OTP ввода")
```

---

### ✅ mobile-review — The Quality Gatekeeper
**Файл:** `.claude/agents/mobile-review.md`  
**Специализация:** Проект-специфичный code review, архитектурные нарушения  
**Призывать когда:**
- Перед коммитом: проверить на нарушения правил проекта
- Проверить visibility (internal/public)
- MVI контракт, Tokens, Koin scopes
- CLAUDE.md compliance
- **NOT generic KMP review** — для этого используй `/kmp-code-review`

**Команда:**
```
Agent(subagent_type: "mobile-review", 
       prompt: "Review FeatureWelcome перед коммитом: visibility, MVI, tokens, hard rules")
```

---

### 🐛 mobile-bug-finder — The Debugging Detective
**Файл:** `.claude/agents/mobile-bug-finder.md`  
**Специализация:** Диагностика крэшей, race conditions, iOS-specific issues  
**Призывать когда:**
- Crash report с stacktrace
- Intermittent баг (иногда случается)
- Race condition подозрение
- Event не доходит / state теряется
- iOS-only баг (Android работает)
- Memory leak

**Команда:**
```
Agent(subagent_type: "mobile-bug-finder", 
       prompt: "OTP Sheet иногда не открывается на первую попытку. Проанализируй EnterPasscodeViewModel")
```

---

### 🔐 mobile-security — The Protection Officer
**Файл:** `.claude/agents/mobile-security.md`  
**Специализация:** Криптография, Keychain, EncryptedPrefs, Biometric auth, ProGuard  
**Призывать когда:**
- Аудит безопасности (перед релизом)
- Вопросы о хешировании паролей, KDF, salt
- Storage (secure vs plain)
- Biometric authentication strength
- API security, certificate pinning
- Android Manifest hardening
- ProGuard/R8 rules
- **NOT generic security review** — для этого используй `/security-review`

**Команда:**
```
Agent(subagent_type: "mobile-security", 
       prompt: "Аудит безопасности FeatureWelcome: passcode hash, auth flag storage, manifest")
```

---

## Таблица выбора агента

| Вопрос | Агент |
|---|---|
| "Куда положить этот код — в Core или Feature?" | **mobile-architect** |
| "Напиши ViewModel / Interactor / Repository" | **mobile-coder** |
| "Напиши UI-экран / composable / компонент" | **mobile-ui-designer** |
| "Проверь код перед коммитом" | **mobile-review** |
| "Почему это крэшится / баг intermittent?" | **mobile-bug-finder** |
| "Безопасно ли хранение пароля?" | **mobile-security** |
| "Обновить CLAUDE.md / создать skill / скрипт" | **ai-process-manager** |

---

## Как вызывать

### Полная форма (с описанием для себя)
```kotlin
Agent(
  subagent_type: "mobile-coder",
  description: "Написать ViewModel для новой фичи",
  prompt: "Создай HabitListViewModel с Pattern B, поддержка фильтрации"
)
```

### Короткая форма (когда очевидно)
```kotlin
Agent(subagent_type: "mobile-coder", 
       prompt: "Напиши ViewModel для экрана X")
```

---

## Где живут подробные инструкции

Каждый агент имеет **полное описание** своих возможностей, примеры, hard constraints:

```
.claude/agents/
├── mobile-architect.md      (2500+ слов)
├── ai-process-manager.md    (2500+ слов)
├── mobile-coder.md          (3500+ слов)
├── mobile-ui-designer.md    (3000+ слов)
├── mobile-review.md         (3500+ слов)
├── mobile-bug-finder.md     (3000+ слов)
└── mobile-security.md       (4000+ слов)
```

Читай нужный файл, чтобы:
- Узнать **точные правила** проекта, которые агент будет проверять
- Увидеть примеры вывода агента
- Понять hard constraints
- Узнать о известных багах/уязвимостях в кодовой базе

---

## Интеграция в workflow

### Before commit:
```
Agent(subagent_type: "mobile-review", 
       prompt: "Code review перед git push")
```

### When bug appears:
```
Agent(subagent_type: "mobile-bug-finder", 
       prompt: "[stacktrace или описание симптома]")
```

### When designing feature:
```
Agent(subagent_type: "mobile-architect", 
       prompt: "Спроектируй новый Feature модуль для X")
```

### When implementing:
```
Agent(subagent_type: "mobile-coder", 
       prompt: "Реализуй ViewModel + Interactor + Repository для X")
```

### When building UI:
```
Agent(subagent_type: "mobile-ui-designer", 
       prompt: "Напиши Screen composable для X")
```

### Before release:
```
Agent(subagent_type: "mobile-security", 
       prompt: "Полный аудит безопасности перед релизом")
```

---

## Замечания

- ✅ **Агенты уже интегрированы** в Claude Code — они автоматически обнаруживаются из `.claude/agents/`
- ✅ **Нет необходимости в MCP** — это просто markdown файлы с system prompts
- ✅ **Можно вызывать параллельно** — например, два агента одновременно на разные части
- ❌ **Не используй для простых задач** — если задача на 5 минут, не спрашивай агента, сделай сам
- 🔍 **Читай подробный файл агента**, когда его вывод кажется неправильным — там могут быть hard constraints или примеры

---

**Создано:** April 25, 2026  
**Статус:** Production-ready — все 7 агентов готовы к использованию
