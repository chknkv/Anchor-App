---
name: strings-resources
description: |
  Generate, translate, insert, and improve string resources for Kotlin Multiplatform (KMP) /
  Compose Multiplatform projects. Use this skill whenever the user asks to write, rewrite,
  translate, fix, insert, or create string resources — even if they just describe a screen,
  paste rough strings, or say "add strings for X". Triggers include: "напиши строки",
  "добавь ресурсы", "переведи строки", "создай strings.xml", "исправь строки",
  "string resources for", "добавь локализацию", "strings for the [screen name] screen",
  "вставь строки в файл", "добавь в strings.xml".
---

# String Resources Skill

Generates, edits, and inserts production-ready string resources for Russian (`values-ru/strings.xml`)
and English default (`values/strings.xml`) locales. Finds the correct files automatically,
applies all editorial rules, and writes directly to disk.

---

## Step 0 — Identify the target module

**Before doing anything else**, determine which module the strings belong to:

1. If the user specified a module name or screen — map it to its Gradle path (see the project's
   Module Map in CLAUDE.md).
2. If ambiguous — ask: *"В какой модуль добавить строки? (например, `:Core:CorePasscode`,
   `:anchor-MobileApp:shared`)"*

**File paths to edit** (both must be updated in every operation):

```
[ModulePath]/src/commonMain/composeResources/values/strings.xml     ← English (default)
[ModulePath]/src/commonMain/composeResources/values-ru/strings.xml  ← Russian
```

**Examples:**
```
Core/CoreDesignSystem/src/commonMain/composeResources/values/strings.xml
Core/CorePasscode/src/commonMain/composeResources/values-ru/strings.xml
```

Use `find [ModulePath] -name "strings.xml"` to verify the exact paths exist.

---

## Step 1 — Understand the input

Handle any of these input forms:

| Input | Action |
|---|---|
| Screen / feature description in prose | Infer all needed strings from context |
| Draft strings in one language | Translate + polish both |
| Draft strings in both languages | Polish, fix errors, enforce all rules |
| Mixed (some strings exist, some missing) | Fill gaps + polish existing ones |
| Request to fix specific strings | Apply editorial rules, note all changes |

---

## Step 2 — Generate the strings

### Naming convention

Pattern: **`screenName_semanticContext_type`**

- `screenName` — camelCase name of the screen or feature (`habitSelection`, `authLogin`,
  `onboarding`, `settings`, `subscriptions`)
- `semanticContext` — UI element or logical group (`header`, `body`, `footer`, `emptyState`,
  `errorBanner`, `sheet`, `dialog`)
- `type` — the string's role:

| Suffix | When to use |
|---|---|
| `title` | Primary heading / screen title |
| `subtitle` | Secondary heading or supporting label |
| `description` | Body text, longer explanatory copy |
| `hint` | Placeholder inside an input field |
| `label` | Short label next to a field or icon |
| `button` | Button / CTA text |
| `action` | Link or tappable text (not a button) |
| `error` | Validation or error message |
| `success` | Confirmation / success state message |
| `empty` | Empty state text |
| `snackbar` | Snackbar / toast message |
| `contentDescription` | Accessibility content description (invisible) |

**Good examples:**
```
habit_selection_header_title
auth_login_email_hint
profile_edit_footer_button
subscriptions_emptyState_description
passcode_setup_errorBanner_error
settings_sheet_logout_action
```

---

## Step 3 — Apply all editorial rules

### Rule 1 — Non-breaking space ` ` (CRITICAL)

` ` must replace a regular space in **all** of the following cases.
Apply this rule exhaustively — every occurrence, not just the obvious ones.

#### 3.1 After prepositions and particles (Russian)

Use ` ` after each of these words when followed by another word:

| Group | Words |
|---|---|
| Single-letter | `в`, `к`, `с`, `у`, `о`, `а`, `и` |
| Two-letter | `на`, `по`, `за`, `из`, `до`, `от`, `со`, `об`, `не`, `но`, `ни` |
| Three-letter | `без`, `для`, `при`, `над`, `под`, `вне`, `про`, `раз`, `уже`, `всё`, `ещё` |
| Longer | `ради`, `меж`, `через`, `между`, `после`, `перед`, `около`, `кроме`, `вместо` |
| Conjunctions | `или`, `что`, `как`, `хоть`, `либо`, `зато`, `если`, `хотя`, `ведь`, `чтоб`, `пока`, `едва`, `чтобы`, `будто`, `словно`, `однако` |

**Examples:**
```xml
<!-- WRONG -->
<string name="auth_login_body_description">Введите код для подтверждения входа</string>

<!-- CORRECT -->
<string name="auth_login_body_description">Введите код для подтверждения входа</string>
```

```xml
<!-- WRONG -->
<string name="settings_notifications_label">Уведомления о новых подписках</string>

<!-- CORRECT -->
<string name="settings_notifications_label">Уведомления о новых подписках</string>
```

#### 3.2 Between logically inseparable word pairs

Always use ` ` between words that form a single concept:

| Russian pairs | English pairs |
|---|---|
| `Политика конфиденциальности` | `Privacy Policy` |
| `Пользовательское соглашение` | `Terms of Use` |
| `Условия использования` | `End User Agreement` |
| `Touch ID` / `Face ID` | `Touch ID` / `Face ID` |
| `App Store` / `Google Play` | `App Store` / `Google Play` |
| `Войти через` | `Sign in with` |

#### 3.3 Before numeric format arguments and units of measure

```xml
<string name="subscriptions_stats_trial_label">Пробный период: %1$d дней</string>
<string name="subscriptions_stats_trial_label">Trial period: %1$d days</string>
```

Units: ` МБ`, ` ГБ`, ` КБ`, ` ТБ`, ` мин`, ` ч`, ` сут`, ` мес`

#### 3.4 Before string format arguments (dynamic names)

```xml
<string name="profile_greeting_header_title">Привет, %1$s</string>
<string name="profile_greeting_header_title">Hello, %1$s</string>
```

#### 3.5 English prepositions before short words in compound phrases

In English, apply ` ` after `a`, `I` (when standalone) and inside fixed compound phrases:
- `Sign in`, `Log in`, `Log out`, `Sign up`
- `Set up`, `Back up`

---

### Rule 2 — No trailing period

Strings must **never** end with `.` (period):

- Titles, subtitles, buttons, labels, hints, actions → never a period
- Body copy and descriptions → no period on the final sentence
- Exception: ellipsis `…` is allowed where text intentionally trails off

---

### Rule 3 — Tone

- Neutral and formal. No exclamation marks unless context is explicitly celebratory
  (success state, onboarding completion).
- No slang. No filler words («просто», «легко», «быстро») unless they carry real meaning.
- Russian: address users as «вы» (formal), not «ты».

---

### Rule 4 — Conciseness

- One phrase over two where meaning is preserved.
- Remove redundant qualifiers: «данный шаг» → «этот шаг».
- **Buttons**: imperative verb + object, ≤ 3–4 words.

| Language | GOOD | BAD |
|---|---|---|
| RU | `Сохранить изменения` | `Нажмите, чтобы сохранить изменения` |
| EN | `Save changes` | `Click here to save your changes` |

---

### Rule 5 — Accuracy and spelling

- Fix all spelling, grammar, and punctuation errors silently.
- If an existing string's meaning is awkward — improve it and note the change briefly
  after the XML blocks.

---

### Rule 6 — Parallelism

If a set of strings belongs to the same UI group (a list, a set of tab labels, feature bullets),
keep grammatical structure parallel across all items.

---

### Rule 7 — Escaping

| Character | Escaped form |
|---|---|
| Apostrophe `'` | `\'` (required in Android XML) |
| Double quote `"` | `\"` |
| Ampersand `&` | `&amp;` |
| Less-than `<` | `&lt;` |
| Percent `%` | `%%` (when literal, not a format arg) |

---

### Rule 8 — Format arguments

Always use **positional** format args — required for correct translation:

```xml
<!-- WRONG -->
<string name="greeting">Hello, %s!</string>

<!-- CORRECT -->
<string name="greeting">Hello, %1$s</string>
```

Types: `%1$s` (string), `%1$d` (integer), `%1$f` (float), `%1$.2f` (float 2dp).

---

## Step 4 — Special string types

### Plurals

Use `<plurals>` when a string depends on a quantity.

**Russian** (requires `one / few / many / other`):
```xml
<plurals name="subscriptions_stats_count">
    <item quantity="one">%1$d подписка</item>
    <item quantity="few">%1$d подписки</item>
    <item quantity="many">%1$d подписок</item>
    <item quantity="other">%1$d подписки</item>
</plurals>
```

**English** (requires `one / other`):
```xml
<plurals name="subscriptions_stats_count">
    <item quantity="one">%1$d subscription</item>
    <item quantity="other">%1$d subscriptions</item>
</plurals>
```

### String arrays

For ordered lists of items:
```xml
<string-array name="settings_theme_options">
    <item>Системная</item>
    <item>Светлая</item>
    <item>Тёмная</item>
</string-array>
```

### HTML inline markup

Use CDATA when a string contains HTML tags:
```xml
<string name="onboarding_body_description"><![CDATA[Нажимая кнопку, вы соглашаетесь с <b>Условиями использования</b>]]></string>
```

Allowed tags: `<b>`, `<i>`, `<u>`. Avoid `<font>`, `<small>` — not supported in Compose.

---

## Step 5 — Insert into files

After generating strings, **write them directly to the correct files** using Edit or Write.

### Insertion rules

1. **Read both files first** before editing.
2. **Check for key collisions** — if any key already exists, update the value in-place rather
   than appending a duplicate.
3. **Match the region/section comment style** used in the file:
   ```xml
   <!-- [Region Name] Region -->
   <string name="...">...</string>
   <!-- End [Region Name] Region -->
   ```
4. **Group new strings** under a matching existing region comment, or create a new one
   at the end of `<resources>` if no match:
   ```xml
   <!-- [ScreenName] Region -->
   <string name="...">...</string>
   <!-- End [ScreenName] Region -->
   ```
5. **Maintain the same key order** in both files — RU and EN must be in sync.
6. **Indentation**: 4 spaces. No tabs.

### After writing

Report what was written:
```
Добавлено в values/strings.xml и values-ru/strings.xml (Core/CorePasscode):
  ✓ passcode_setup_header_title
  ✓ passcode_setup_body_description
  ✓ passcode_setup_footer_button
```

---

## Step 6 — Analysis report (when editing existing strings)

After the XML blocks, output a compact **Changes** section listing only non-trivial edits:

```
### Changes
- `auth_login_body_description` (ru): добавлен   перед «подтверждения», убрана точка
- `auth_login_body_description` (en): rewritten for conciseness
- `profile_edit_footer_button` (ru): «данный» → «этот»
```

Skip this section entirely if no changes were made.

---

## Quick reference — complete worked example

**Input:** "Экран настройки пасскода. Нужен заголовок, подзаголовок с пояснением, кнопка продолжить."

**values/strings.xml (English — default):**
```xml
<!-- Passcode Setup Region -->
<string name="passcode_setup_header_title">Set up a passcode</string>
<string name="passcode_setup_body_subtitle">Your passcode protects access to the app</string>
<string name="passcode_setup_footer_button">Continue</string>
<!-- End Passcode Setup Region -->
```

**values-ru/strings.xml (Russian):**
```xml
<!-- Passcode Setup Region -->
<string name="passcode_setup_header_title">Создайте пасскод</string>
<string name="passcode_setup_body_subtitle">Пасскод защищает доступ к приложению</string>
<string name="passcode_setup_footer_button">Продолжить</string>
<!-- End Passcode Setup Region -->
```

---

## Pre-output checklist

Run this mentally on **every string** before writing:

- [ ] Key follows `screenName_semanticContext_type`
- [ ] No trailing period
- [ ] ` ` after every preposition, conjunction, single-letter word, and before units / format args
- [ ] ` ` between all logically inseparable word pairs
- [ ] Apostrophes escaped as `\'`; `&` as `&amp;`; `"` as `\"`
- [ ] Positional format args used (`%1$s`, not `%s`)
- [ ] Tone is neutral and formal — no exclamation marks, no filler words
- [ ] Text is concise — buttons ≤ 3–4 words
- [ ] Russian uses «вы» (formal)
- [ ] RU and EN files have **identical keys in identical order**
- [ ] No duplicate keys introduced
- [ ] Both files written to disk
