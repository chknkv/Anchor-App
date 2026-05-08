# CoreDesignSystem

KMP-библиотека дизайн-системы Anchor. Stack: Compose Multiplatform · Jetpack Navigation Multiplatform · commonMain + androidMain + iosMain.
Package: `com.chknkv.designsystem`. Единственная внутренняя зависимость: `:Core:CoreUtils`.

---

## Файловая карта

```
src/commonMain/kotlin/com/chknkv/designsystem/
├── screen/   AppNavHost.kt · AppScaffold.kt · BackInterceptor.kt
├── theme/    Theme.kt · Tokens.kt · TokensColor.kt · TokensGradient.kt
│             LightMode.kt · DarkMode.kt · SystemBarsAppearance.kt (expect)
├── button/   Button.kt · ButtonCircle.kt · SlideToAct.kt
├── cell/     Cell.kt · CellAction.kt · CellInfo.kt · CellPicker.kt
├── chip/     Chip.kt
├── loading/  ActivityIndicator.kt · LoadingHUD.kt · ProgressRing.kt
├── modifier/ LinkModifier.kt · ShimmerModifier.kt
├── module/   Module.kt · ModuleContent.kt
├── otp/      OtpCodeInput.kt
├── passcode/ PasscodeKeyboard.kt · PasscodeIndicator.kt · KeyClickSound.kt (expect)
├── sheet/    Sheet.kt
├── switcher/ Switcher.kt
├── textinput/ TextInput.kt
├── Colors.kt · Typography.kt · Locale.kt (expect) · Separator.kt · SquareIcon.kt
src/androidMain/ → Locale · KeyClickSound · SystemBarsAppearance (actual)
src/iosMain/     → Locale · KeyClickSound · SystemBarsAppearance (actual)
```

---

## Тема

`Theme(darkTheme: Boolean? = null)` — вызывается **один раз** в корне `AnchorApp`. Предоставляет три `CompositionLocal`: `LocalTokens`, `LocalTokensGradient`, `LocalTokensColor`.

```kotlin
Theme.tokens    // Map<Tokens, Color>
Theme.gradients // Map<TokensGradient, Brush>
Theme.colors    // Map<TokensColor, Color>

Tokens.Background.getThemedColor()          // → Color
TokensColor.Blue.getThemedColor()           // → Color
TokensGradient.Purple.getThemedGradient()   // → Brush
```

### Семантические токены (Tokens)

`Background` · `BackgroundSheet` · `Module` · `TextPrimary` · `TextSecondary` · `IconPrimary` · `IconSecondary` · `Separator` · `Action` (Blue) · `Warning` (Red) · `Switcher` (Green) · `Shimmer` · `ShimmerHighlight` · `ProgressTrack` · `HudBackground` (Black 80%) · `ChipBackgroundSelected/Unselected` · `ChipContentSelected/Unselected` · `PasscodeKeyGlass`

### Именованные акценты (TokensColor / TokensGradient)

`Black · Gray · Blue · Indigo · Purple · Green · Orange · DarkOrange · Red · DarkRed · Pink`
Каждый цвет имеет парный `TokensGradient` — `Brush.verticalGradient` из двух оттенков той же палитры.

---

## Навигация — AppNavHost

```kotlin
AppNavHost(
    navController, startDestination: Any,   // @Serializable type-safe route
    enableSwipeBack: Boolean = true,
    modifier, contentAlignment,
    builder: NavGraphBuilder.() -> Unit
)
```

- iOS-style анимации: `slideInHorizontally + fadeIn` (300 ms); обратные при pop.
- `PredictiveBackWrapper`: порог 50 px, direction lock 15 px → `popBackStack()`.
- Не вкладывать `AppNavHost` в `AppNavHost` — двойной `popBackStack()` при свайпе.
- Под-графы → `NavGraphBuilder` extension-функции; `enableSwipeBack = false` для WelcomeFlow.

---

## Каркас экрана — AppScaffold

```kotlin
AppScaffold(
    modifier, containerColor = Tokens.Background,
    title: String? = null,
    backButton: ButtonConfig? = null,
    actionButton: ButtonConfig? = null,
    floatingButton: ButtonConfig? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (PaddingValues) -> Unit
)

data class ButtonConfig(
    val onClick: () -> Unit,
    val iconRes: DrawableResource = ic_chevron_left,
    val iconColor: Color? = null,
    val backgroundColor: Color? = null,
    val hapticFeedbackType: HapticFeedbackType? = null
)
```

- Пустой топбар → gradient fade, высота `statusBarPadding + 16 dp`.
- Непустой → blur overlay, высота `statusBarPadding + 72 dp`; `Headline` по центру.
- `content` получает `PaddingValues(top = topBlurHeight, bottom = navBarPadding + 12 dp)`.
- В скроллируемых контейнерах дублировать `calculateBottomPadding()` через `Spacer` в конце.

---

## Типографика

Все принимают: `(text, modifier, color, isSecondary, textAlign, maxLines, overflow, onTextLayout?)`.
`isSecondary = true` → `Tokens.TextSecondary`; явный `color` отменяет `isSecondary`.

| Composable | fontSize | fontWeight | Применение |
|------------|----------|------------|------------|
| `Title1` | 34 sp | Black 900 | Главные заголовки |
| `Title2` | 28 sp | Black 900 | Заголовки разделов |
| `Title3` | 22 sp | Black 900 | Подразделы |
| `Headline` | 18 sp | SemiBold | Топбар, акценты |
| `Subheadline` | 16 sp | Normal | Вторичные метки |
| `Body` | 18 sp | Normal | Основной контент |
| `Callout` | 16 sp | Normal | Инструкции |
| `Footnote` | 13 sp | Normal | Сноски |
| `Caption1` | 12 sp | Normal | Мелкие подписи |
| `Caption2` | 11 sp | Normal | Плотный текст |

---

## Ключевые компоненты

**Button** — высота 50 dp, `RoundedCornerShape(25 dp)`, haptic при нажатии, анимация scale+alpha.
```kotlin
Button(text, style: ButtonStyle, onClick, modifier, enabled, description: String?)
// ButtonStyle: Action | Warning | Default | Custom(bgColor, contentColor)
```

**Module / ModuleContent** — карточка: `outPadding(top=8,h=16)`, `innerPadding(h=14)`, `radius=24 dp`.
```kotlin
Module(modifier, outPaddingValues, innerPaddingValues, shape, content: BoxScope.() -> Unit)
ModuleContent(modifier, inModule=true, title?, titlePadding, description?, ..., content)
```

**Cell** — иконка `SquareIcon(34 dp)`, разделитель `Separator(leadingInset = 46/4 dp)`.
```kotlin
Cell(title, subtitle?, iconRes?, iconColor?, iconGradient?, isChevron, isDivider, trailingContent?, onClick?)
```

**Chip** — `minHeight=45 dp`, `minWidth=64 dp`, `Footnote`, haptic `LongPress` при смене состояния.
```kotlin
Chip(text, isSelected, onActionHandler, modifier, isAnimateColor, configuration, externalPadding, internalHorizontalPadding)
```

**LoadingHUD** — `100×100 dp`, `radius=20 dp`, фон `Tokens.HudBackground`. Требует внешнего перехватчика кликов.
```kotlin
LoadingHUD(modifier?, text?, spinnerColor = Color.White)
```

**SquareIcon** — `radius=8 dp`, `iconTint=White`, `backgroundGradient` имеет приоритет над `backgroundColor`.
```kotlin
SquareIcon(modifier, iconSize=28dp, iconRes, iconResSize=20dp, backgroundColor?, backgroundGradient?, iconTint)
```

**Sheet** — нижний модальный лист.
```kotlin
Sheet(isVisible, onDismissRequest, title?, subtitle?, actionButton?, isDragable, onDragDismissAction?,
      isOutsideClickEnabled, onOutsideClickAction?, heightBehavior: WrapContent|HalfScreen|FullScreen, content)
```

**OtpCodeInput** — 4 цифры, shake при ошибке, wave при загрузке. `PinInputState: Input | Loading | Error`.

**PasscodeKeyboard / PasscodeIndicator** — клавиши 82 dp, glass-эффект (`Tokens.PasscodeKeyGlass`).

**Модификаторы** — `Modifier.shimmer()` (sweep 1300 ms).

**LinkModifier** (`modifier/LinkModifier.kt`) — кликабельные диапазоны в тексте. `data class LinkSegment(val range: IntRange, val onAction: () -> Unit)`.
```kotlin
Modifier.link(enabled, color, onAction)                                          // всё, один action
Modifier.link(textLayoutResult, range: IntRange, enabled, color, onAction)       // один диапазон
Modifier.link(textLayoutResult, ranges: List<IntRange>, enabled, color, onAction)// несколько, один action
Modifier.link(textLayoutResult, segments: List<LinkSegment>, enabled, color)     // независимые action
```

**Locale** — `CompositionLocalProvider(LocalAppLocale provides language)`. Не оборачивать в `key()`.

---

## Жёсткие правила

| # | Правило |
|---|---------|
| 1 | Только типографические компоненты — не `Text()` напрямую |
| 2 | Только `Tokens.X.getThemedColor()` — не `MaterialTheme.colorScheme.*` |
| 3 | Цвета в Feature-слое только через `Tokens.*` / `TokensColor.*` — не хардкод |
| 4 | Каркас экрана только `AppScaffold` — не `Scaffold` из Material3 |
| 5 | Не вкладывать `AppNavHost` в `AppNavHost`; под-графы через `NavGraphBuilder` extension |
| 6 | `Theme()` вызывается один раз в корне — Feature-модули не создают свой `Theme` |
| 7 | `LocalAppLocale` только через `CompositionLocalProvider`; без обёртки `key()` |
| 8 | `Colors.kt` (raw-палитра) не используется напрямую в Feature-слое |
| 9 | `CoreDesignSystem` не импортирует Feature-модули и Core-модули кроме `CoreUtils` |
| 10 | Фон `LoadingHUD` — `Tokens.HudBackground`; не хардкодить `Color.Black.copy(alpha=...)` |
