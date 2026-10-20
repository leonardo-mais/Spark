---
name: spark-jetpack
description: Official guidelines for UI design using Material 3 Expressive in Jetpack Compose. Defines the 30 typography styles (Baseline + Emphasized), HCT color tokens (including Fixed and Surface Containers), MotionScheme physics, exact component mapping, and architectural patterns for Android apps.
---

# Material 3 Expressive UI Design Skill

This document defines the design constraints, token semantics, and implementation rules required to generate or review interfaces that are 100% compliant with **Material 3 Expressive** in **Jetpack Compose** on Android.

---

## 1. Fundamental Principles of Material 3 Expressive

* **Typographic and Formal Boldness:** Distinct contrasts between rounded geometries, organic shapes (`MaterialShapes`), controlled asymmetries, and a 30-style typographic scale (15 Baseline + 15 Emphasized) powered by Google Sans Flex.
* **Tonal Surfaces Over Shadows:** Depth is expressed exclusively through the 5 tonal levels of `Surface Container` (`Lowest`, `Low`, `Standard`, `High`, `Highest`), eliminating the use of drop shadows (`Modifier.shadow`, `shadowElevation`) or legacy elevation-based `tonalElevation` for content hierarchy.
* **Motion Physics (Motion Scheme):** Spatial animations employ physical springs (`Spring`) with natural subtle overshoot provided by `MaterialTheme.motionScheme`, while color and opacity transitions use non-bouncing curves (`EffectsSpec`). Motion animation **MUST** be applied for every transition.
* **Semantic Adaptability:** No color, shape, or dimension must be hardcoded; every visual element must draw strictly from `MaterialTheme` tokens.

Official resources:
* [Building with M3 Expressive](https://m3.material.io/blog/building-with-m3-expressive)
* [M3 Expressive Motion Theming](https://m3.material.io/blog/m3-expressive-motion-theming)
* [Material 3 Components Spec](https://m3.material.io/components)
* [M3 Color & Token System](https://m3.material.io/styles/color/the-color-system/tokens)
* [Jetpack Compose Material 3 API Reference](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary)
* [AndroidX Material3 Release Notes](https://developer.android.com/jetpack/androidx/releases/compose-material3)

---

## 2. Jetpack Compose Prerequisites & Dependencies

Expressive components reside in the `androidx.compose.material3` package and require modern versions of Material 3:
* **Minimum Gradle Dependency:** `androidx.compose.material3:material3:1.4.0-alpha14` (or `1.4.0+` / updated BOM).
* **Mandatory Opt-In:** All Expressive components and themes require the annotation:
  ```kotlin
  @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
  ```
  Apply this annotation at the Composable function level or at the file level (`@file:OptIn(...)`).
* **Theme Setup:** Initialize the UI hierarchy with `MaterialExpressiveTheme` (which sets `MotionScheme.expressive()` by default):
  ```kotlin
  @OptIn(ExperimentalMaterial3ExpressiveApi::class)
  @Composable
  fun SparkAppTheme(
      colorScheme: ColorScheme,
      content: @Composable () -> Unit
  ) {
      MaterialExpressiveTheme(
          colorScheme = colorScheme,
          motionScheme = MotionScheme.expressive(),
          typography = SparkExpressiveTypography,
          shapes = SparkShapes,
          content = content
      )
  }
  ```

---

## 3. Component Selection Matrix & Reference Protocol (Zero Hallucinations)

> **CRITICAL AGENT RULE:** Before writing any Composable, verify the exact API name in `androidx.compose.material3` using the table below. For advanced implementation details, signatures, and parameters, you **MUST** read the local documents in [`./references/`](./references/README.md) using dedicated file-reading tools:

* [**Navigation & Toolbars** (`./references/navigation.md`)](./references/navigation.md) — TopAppBars, FloatingToolbars, NavigationBar, WideNavigationRail, Drawer, Tabs (individual specs in `./references/components/app-bars.md`, `navigation-bar.md`, `toolbars.md`, `floating-bar.md`, `tabs.md`)
* [**Actions & Buttons** (`./references/actions.md`)](./references/actions.md) — Common Buttons, SplitButtonLayout, ButtonGroup, IconButtons, Medium/Large FABs, FAB Menu (individual specs in `./references/components/floating-action-buttons-fabs.md`, `split-button.md`, `button-groups.md`, `icon-buttons.md`)
* [**Inputs & Selection** (`./references/inputs.md`)](./references/inputs.md) — OutlinedTextField, Sliders, Switch, Checkbox, Radio, Date & Time Pickers (individual specs in `./references/components/text-fields.md`, `sliders.md`, `switch.md`, `date-picker.md`, `time-picker.md`)
* [**Containment & Surfaces** (`./references/containment-surfaces.md`)](./references/containment-surfaces.md) — Cards, GroupedListCard, Bottom/Side Sheets, Dialogs, Menus, SearchBar / AppSearchView, Tooltips (individual specs in `./references/components/cards.md`, `search.md`, `bottom-sheets.md`, `dialogs.md`)
* [**Display & Feedback** (`./references/display-feedback.md`)](./references/display-feedback.md) — Lists, Grids, Carousel, Chips, Wavy Progress Indicators, LoadingIndicator, Badges, Snackbar (individual specs in `./references/components/progress-indicator.md`, `loading-indicator.md`, `carousel.md`, `chips.md`, `lists.md`)

### 3.1 Quick Mapping: Use Case $\rightarrow$ Official M3 Expressive Composable

| Use Case / UI Intent | Official Composable (`androidx.compose.material3`) | Signature / Critical Usage Notes |
| :--- | :--- | :--- |
| **Linear Determinate / Indeterminate Progress** | `LinearWavyProgressIndicator` | Use lambda `progress = { floatValue }` for determinate, omit `progress` for indeterminate. **NEVER** write `WavyLinearProgressIndicator`. |
| **Circular Wavy Progress** | `CircularWavyProgressIndicator` | Use lambda `progress = { floatValue }` or indeterminate overload. **NEVER** write `WavyCircularProgressIndicator`. |
| **Short Wait / Loading (Morphing)** | `LoadingIndicator` / `ContainedLoadingIndicator` | Replaces the traditional circular spinner for expressive loading states featuring shape morphing. |
| **Floating Primary Action (Default)** | `MediumFloatingActionButton` | **Mandatory default** (68dp, squircle `20.dp`, icon `30.dp`). Icon only, never text. |
| **Monumental Primary Action** | `LargeFloatingActionButton` | Only for crucial hero actions (96dp, squircle `28.dp`, icon `36.dp`). Icon only, never text. |
| **Expandable FAB with Sub-Actions** | `FloatingActionButtonMenu` + `ToggleFloatingActionButton` | Replaces manual mini-FAB stacks. Contains `FloatingActionButtonMenuItem`. |
| **Dual Button (Primary Action + Contextual Menu)** | `SplitButtonLayout` | Use `leadingButton = { SplitButtonDefaults.LeadingButton(...) }` and `trailingButton = { SplitButtonDefaults.TrailingButton(...) }`. |
| **Connected Button Group / Selection** | `ButtonGroup` | Replaces `SegmentedButton`. Uses nested `ToggleButton` or `IconToggleButton` with shape morphing on press. |
| **Contextual Floating Toolbar** | `HorizontalFloatingToolbar` / `VerticalFloatingToolbar` | Supports `FloatingToolbarDefaults.vibrantFloatingToolbarColors()` or `standardFloatingToolbarColors()`, with optional integrated FAB. |
| **Tablet / Foldable / Desktop Navigation** | `WideNavigationRail` / `ModalWideNavigationRail` | Replaces the legacy fixed `NavigationRail` when expandable labels are required. |
| **Image Carousel / Expressive Cards** | `HorizontalMultiBrowseCarousel` / `HorizontalUncontainedCarousel` | Requires `rememberCarouselState { itemCount }`. |
| **Full-Screen Pill-to-Search Expansion** | `SearchBar` + `ExpandedFullScreenSearchBar` | Wrapped inside project component `AppSearchView` (Section 21.8) for live filtering and history. |

---

## 4. Anti-Hallucination Rules (Negative Constraints)

Strictly adhere to the following conversion table:

| ❌ NEVER USE (Deprecated / Hallucinated) | ✅ ALWAYS USE (Jetpack Compose M3 Expressive) | Notes |
| :--- | :--- | :--- |
| `MaterialTheme.colors.*` | `MaterialTheme.colorScheme.*` | `colors` belongs to Material 2 |
| `Divider(...)` | `HorizontalDivider(...)` or `VerticalDivider(...)` | `Divider` is deprecated in M3 |
| `BottomNavigation` / `BottomNavigationItem` | `NavigationBar` or `ExpressiveImpactNavigationBar` | M2 deprecated |
| `SegmentedButton` / `SingleChoiceSegmentedButtonRow` | `ButtonGroup` or `SplitButtonLayout` | M3 Expressive favors Connected Button Groups |
| `WavyLinearProgressIndicator` *(Incorrect name!)* | `LinearWavyProgressIndicator(progress = { ... })` | Mind the word order in the real API |
| `WavyCircularProgressIndicator` *(Incorrect name!)* | `CircularWavyProgressIndicator(progress = { ... })` | Mind the word order in the real API |
| `LinearProgressIndicator(progress = 0.5f)` | `LinearWavyProgressIndicator(progress = { 0.5f })` | `progress` **MUST** always be a lambda `() -> Float` |
| `SplitButton(...)` *(Non-existent!)* | `SplitButtonLayout(leadingButton = ..., trailingButton = ...)` | Exact Composable name in M3 1.4+ |
| `CircleShape` or 50% radius on FAB | `MediumFloatingActionButton` with `RoundedCornerShape(20.dp)` | FAB squircle: NEVER create pills or 50% circles |
| `ExtendedFloatingActionButton` with text | `MediumFloatingActionButton` or `LargeFloatingActionButton` (icon only) | Absolute ban on text labels inside FABs |
| `Modifier.shadow(...)`, `shadowElevation`, `tonalElevation` | `containerColor = MaterialTheme.colorScheme.surfaceContainer*` | Use the 5 Surface Container tiers, never elevation/shadows |
| `MaterialTheme.typography.h1..h6`, `body1`, `subtitle1` | `display*`, `headline*`, `title*`, `body*`, `label*` (and `*Emphasized`) | Use the 30 official M3 Expressive styles |
| `MaterialTheme.shapes.pill` or `shapes.circle` | `CircleShape` (`androidx.compose.foundation.shape.CircleShape`) | `shapes.pill` does not exist in Compose |
| `Color(0xFF...)` or `.copy(alpha = ...)` on containers | `MaterialTheme.colorScheme.<role>` | Alpha on containers breaks WCAG contrast |
| Hardcoded strings (`Text("Save")`) | `Text(stringResource(R.string.save))` | Dual-language EN + IT mandatory (`Rule 7`) |
| Emojis in UI (e.g., 💊, 🏠, ⚠️, 🎉, 👤, 📦) | Material Vector Icons (`Icons.*`, Material Symbols XML) | **ABSOLUTE BAN ON EMOJIS IN APPS** |

> **🚫 ABSOLUTE BAN ON EMOJIS IN APPLICATIONS (Zero Emoji Policy):**
> Inserting emoji characters or symbols anywhere in the application's user interface is strictly prohibited (whether in Compose layout files, `strings.xml`, titles, descriptions, buttons, cards, chips, notifications, or empty states). Any iconic or visual concept must be represented **EXCLUSIVELY** via official Material vector icons (`androidx.compose.material.icons.*` or vector XML Material Symbols).

---

## 5. Dynamic Color System (HCT) & Surface Hierarchy

The interface strictly consumes semantic tokens from `MaterialTheme.colorScheme` based on the perceptually uniform HCT (Hue, Chroma, Tone) color space.

### 5.1 Semantic Mapping of Color Roles

| Semantic Role | Usage Context |
| :--- | :--- |
| `primary` / `onPrimary` | Key action with maximum visual prominence (FAB, primary buttons, active indicators). |
| `primaryContainer` / `onPrimaryContainer` | Medium-prominence areas with strong affinity to primary actions (tonal FABs, hero banners). |
| `secondary` / `onSecondary` | Supporting secondary elements (active filters, selected toggles). |
| `secondaryContainer` / `onSecondaryContainer` | Classic `NavigationBar` pill indicator, selected chips, related containers. |
| `tertiary` / `onTertiary` | Creative contrasting accents to balance UI elements or highlight new features. |
| `tertiaryContainer` / `onTertiaryContainer` | Badges, promotional pills, positive state indicators, and secondary KPIs. |
| `primaryFixed` / `primaryFixedDim` / `onPrimaryFixed` | *(M3 Expressive)* Roles maintaining the exact same tone across both Light and Dark themes. Ideal for hero cards or invariant brand accents. Equivalent tokens exist for `secondaryFixed*` and `tertiaryFixed*`. |
| `error` / `onError` / `errorContainer` / `onErrorContainer` | Critical error states, depleted inventory, alerts, or destructive actions. Never apply alpha to `errorContainer`. |
| `outline` | High-contrast boundaries (3:1) for text fields (`OutlinedTextField`), unselected chips, or focus rings. |
| `outlineVariant` | Subtle separators (`HorizontalDivider` / `VerticalDivider`) and low-contrast decorative borders. |
| `inverseSurface` / `inverseOnSurface` / `inversePrimary` | Snackbars, high-contrast tooltips, or contextual banners inverted against the active theme. |
| `scrim` | Modal backdrop veil behind BottomSheets, Dialogs, or Navigation Drawers. |

**Fundamental Pairing Rule:** ALWAYS pair every container with its corresponding `on<Role>` token (e.g., `onPrimaryContainer` on `primaryContainer`, `onErrorContainer` on `errorContainer`, `onSurface` on `surfaceContainer*`), or use `contentColorFor(containerColor)`.

### 5.2 Tonal Hierarchy of Surfaces (The 5 Surface Containers)

In M3 Expressive, shadows (`shadowElevation`) and dynamic `tonalElevation` are **NOT** used to separate cards; hierarchy is governed strictly by surface tokens:

| Surface Token | Hierarchical Role and Use Case |
| :--- | :--- |
| `surface` | Default screen baseline background (`Scaffold` `containerColor`) and `TopAppBar` at rest (`scroll = 0`). |
| `surfaceDim` | Dimmed baseline background for secondary areas or split-screen layouts on large displays. |
| `surfaceBright` | Brightest surface across both Light and Dark themes. **Mandatory default** for the pill Search Top Bar. |
| `surfaceContainerLowest` | Container with minimal separation (cards nested inside another card on light themes). |
| `surfaceContainerLow` | Container for grouped lists (`GroupedListCard`) and Classic `NavigationBar` (subtler contrast: darker in dark theme, lighter in light theme). |
| `surfaceContainer` | Standard default value for Bento cards, Floating Pill Dock, and Bottom Sheets. |
| `surfaceContainerHigh` | Elements requiring immediate prominence: `TopAppBar` while scrolling (`scrolledContainerColor`), circular back buttons, Dialogs. |
| `surfaceContainerHighest` | Maximum tonal contrast: active pill in Floating Dock, filled input fields, inactive tracks of `LinearWavyProgressIndicator`, interactive chips. |

---

## 6. Expressive Typography (30 M3 Styles + 6-Axis Google Sans Flex)

The official and mandatory font is **Google Sans Flex**, bundled locally in `res/font/google_sans_flex.ttf` (zero latency, 100% offline support), featuring 6 variable axes (`wght`, `wdth`, `slnt`, `opsz`, `GRAD`, `ROND`).

### 6.1 The 30 Official Roles of `MaterialTheme.typography` (15 Baseline + 15 Emphasized)

Material 3 Expressive expands the typographic scale from 15 to 30 native styles. Every classic role (`Large`, `Medium`, `Small`) features an `*Emphasized` counterpart (with heavier weight and expressive width/roundness) to direct visual focus without introducing arbitrary non-standard styles:

* **Display:** `displayLarge` / `displayLargeEmphasized` (57sp), `displayMedium` / `displayMediumEmphasized` (45sp), `displaySmall` / `displaySmallEmphasized` (36sp) — Hero numbers and monumental metrics.
* **Headline:** `headlineLarge` / `headlineLargeEmphasized` (32sp), `headlineMedium` / `headlineMediumEmphasized` (28sp), `headlineSmall` / `headlineSmallEmphasized` (24sp) — Page headings, contextual greetings, and key sections.
* **Title:** `titleLarge` / `titleLargeEmphasized` (22sp), `titleMedium` / `titleMediumEmphasized` (16sp), `titleSmall` / `titleSmallEmphasized` (14sp) — Card headings, TopAppBars, dialogs, and list groups.
* **Body:** `bodyLarge` / `bodyLargeEmphasized` (16sp), `bodyMedium` / `bodyMediumEmphasized` (14sp), `bodySmall` / `bodySmallEmphasized` (12sp) — Reading copy, subtitles, and inline emphasized values.
* **Label:** `labelLarge` / `labelLargeEmphasized` (14sp), `labelMedium` / `labelMediumEmphasized` (12sp), `labelSmall` / `labelSmallEmphasized` (11sp) — Buttons, tabs, chips, badges, and micro-copy.

### 6.2 Dynamic Variable-Axis Typography (High-Impact Style)

In the High-Impact Style (*Stile d'Impatto*), Google Sans Flex is controlled directly via its 6 parametric axes:

| Axis | Parameter | Range | Aesthetic Function in the Interface |
| :--- | :--- | :--- | :--- |
| `wght` | Weight | `1..1000` | Ultra-bold weights (`800–850`) for hero metrics and selected states; `480` for inactive dates. |
| `wdth` | Width | `25f..151f` | `115f` for expansive hero numbers; `108f` for scannable headings. |
| `ROND` | Roundness | `0f..100f` | `100f` shapes font stroke terminals into organic rounded pills; `0f` for crisp geometry. |
| `GRAD` | Grade | `0..100` | Adjusts optical density on dark surfaces or badges without altering text width. |
| `slnt` | Slant | `-10f..0f` | Dynamic optical slant. |
| `opsz` | Optical Size | `6..144` | Proportional contrast adjustment between micro-labels and oversized numbers. |

#### Code Blueprint: `Type.kt` & `ExpressiveType`

```kotlin
package it.spark.app.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp
import it.spark.app.R

@OptIn(ExperimentalTextApi::class)
fun flexFont(
    weight: Int = 400,
    width: Float = 100f,
    roundness: Float = 0f,
    grade: Int = 0,
    slant: Float = 0f
): FontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(weight),
            FontVariation.width(width),
            FontVariation.grade(grade),
            FontVariation.slant(slant),
            FontVariation.Setting("ROND", roundness)
        )
    )
)

// Precomputed top-level font families to guarantee Compose Skippability:
val FlexHeroNumericFont = flexFont(weight = 820, width = 115f, roundness = 100f)
val FlexHeadlineFont = flexFont(weight = 750, width = 108f, roundness = 80f)
val FlexSubheadingFont = flexFont(weight = 600, width = 102f, roundness = 60f)
val FlexDateSelectedFont = flexFont(weight = 850, width = 112f, roundness = 100f)
val FlexDateUnselectedFont = flexFont(weight = 480, width = 100f, roundness = 0f)
val FlexClockTimeFont = flexFont(weight = 650, width = 100f, roundness = 50f)
val FlexChipLabelFont = flexFont(weight = 650, width = 105f, roundness = 60f)
val FlexBadgeAlertFont = flexFont(weight = 800, width = 110f, roundness = 80f, grade = 50)
val FlexStatValueFont = flexFont(weight = 780, width = 112f, roundness = 90f)

object ExpressiveType {
    val heroGiant = TextStyle(fontFamily = FlexHeroNumericFont, fontSize = 48.sp, lineHeight = 52.sp, letterSpacing = (-0.5).sp)
    val heroLarge = TextStyle(fontFamily = FlexHeroNumericFont, fontSize = 38.sp, lineHeight = 44.sp, letterSpacing = (-0.25).sp)
    val heroMedium = TextStyle(fontFamily = FlexStatValueFont, fontSize = 28.sp, lineHeight = 34.sp)
    val greetingHeadline = TextStyle(fontFamily = FlexHeadlineFont, fontSize = 26.sp, lineHeight = 32.sp, letterSpacing = (-0.2).sp)
    val dateStripSelected = TextStyle(fontFamily = FlexDateSelectedFont, fontSize = 20.sp, lineHeight = 24.sp)
    val dateStripUnselected = TextStyle(fontFamily = FlexDateUnselectedFont, fontSize = 17.sp, lineHeight = 22.sp)
    val clockTag = TextStyle(fontFamily = FlexClockTimeFont, fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.2.sp)
    val statLabel = TextStyle(fontFamily = FlexSubheadingFont, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp)
    val badgeAlert = TextStyle(fontFamily = FlexBadgeAlertFont, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.1.sp)
}
```

---

## 7. Shape System (`Shapes` & `MaterialShapes`)

M3 Expressive relies on the deliberate contrast between rounded geometric shapes and expressive silhouettes (`MaterialShapes`):

| Abstract Token | Base Radius | Mapping in Jetpack Compose | Usage Example |
| :--- | :--- | :--- | :--- |
| **None** | `0.dp` | `RectangleShape` | Full screen, full-width expanded sheets |
| **Extra Small** | `4.dp` | `MaterialTheme.shapes.extraSmall` | Compact tooltips, dense context menus |
| **Small** | `8.dp` | `MaterialTheme.shapes.small` | Compact chips, rectangular badges |
| **Medium** | `12.dp` | `MaterialTheme.shapes.medium` | Secondary cards, internal icon boxes |
| **Large (Squircle)** | `16.dp–20.dp` | `MaterialTheme.shapes.large` / `RoundedCornerShape(20.dp)` | Medium FAB (**MANDATORY DEFAULT**), Bento Cards, Grouped Lists |
| **Extra Large** | `28.dp` | `MaterialTheme.shapes.extraLarge` | Large FAB (crucial actions), Bottom Sheets, Modal Dialogs |
| **Full / Pill** | `50%` | `CircleShape` | Compact buttons, Search Bar, Floating Pill Dock, Back Button (**NEVER** for FAB) |
| **Expressive Shapes** | Polygonal / Curved | `MaterialShapes.<Shape>.toShape()` | Profile avatars, hero badges, icon containers in empty states (e.g., `Cookie9Sided`, `Sunny`, `Clover4Leaf`) |

> **⚠️ ABSOLUTE RULE FOR THE 3 FABS (MEDIUM DEFAULT & STRICTLY NO TEXT):**
> 1. **Three official variants:** `FloatingActionButton` (`56.dp`, `16.dp` radius, `24.dp` icon), `MediumFloatingActionButton` (`68.dp`, `20.dp` radius, `30.dp` icon) [**ABSOLUTE DEFAULT**], `LargeFloatingActionButton` (`96.dp`, `28.dp` radius, `36.dp` icon [for crucial actions only]).
> 2. **STRICTLY NO TEXT:** It is strictly forbidden to embed text labels inside a FAB (never use `ExtendedFloatingActionButton` with text). Only a centered icon is permitted.
> 3. **Anti-Pill:** Radius must maintain proportional squircle dimensions. **NEVER** apply `CircleShape` at 50% to FABs.
> 4. **Elevation exception:** FABs keep the default M3 elevation of the component. The no-shadow rule above applies to cards and containers, not to FABs.

---

## 8. Spacing and Structural Grid

* **Base Module:** `8.dp` grid system (with `4.dp` increments for micro-elements).
* **Horizontal Screen Padding:** `16.dp` on smartphones (Compact), scalable to `24.dp` on tablets/foldables (Medium / Expanded).
* **Component Gap:** `8.dp–10.dp` between cards within the same Bento grid; `16.dp–24.dp` between distinct semantic sections.
* **Minimum Touch Target:** Every clickable element must guarantee a minimum interactive bounding box of `48.dp x 48.dp` (`Modifier.minimumInteractiveComponentSize()`).

---

## 9. Bottom Navigation Patterns: Classic Standard vs. High-Impact

The bottom navigation bar varies based on the aesthetic archetype selected during the Discovery Interview:

### 9.1 Classic Style: Standard Navigation Bar

When the application adopts the Classic Style (3 to 5 destinations):
1. **Dynamic Icon State (Outlined vs. Filled):**
   * Destination **NOT selected**: Open-stroke icon (`Outlined`, e.g., `Icons.Outlined.Home`). No tonal indicator.
   * Destination **SELECTED**: Solid icon (`Filled`, e.g., `Icons.Filled.Home`), enclosed within the pill indicator (`secondaryContainer`).
2. **Typography:** Label rendered using `MaterialTheme.typography.labelMediumEmphasized` when selected and `labelMedium` when unselected. Inactive text color: `onSurfaceVariant`; active text color: `onSurface`.
3. **Container Color (Refined Dynamic Contrast):**
   * The navigation bar container uses `MaterialTheme.colorScheme.surfaceContainerLow` (or dynamically `if (isSystemInDarkTheme()) surfaceContainerLow else surfaceContainerLowest`).
   * This establishes a cleaner tonal hierarchy: darker in Dark Mode and lighter in Light Mode compared to standard `surfaceContainer`.

### 9.2 High-Impact Style: Floating Pill Dock Navigation Bar (Maximum 4 Elements - No Fallback)

In the High-Impact Style, the navigation bar adopts the Floating Pill Dock structure (`ExpressiveImpactNavigationBar`):
1. **Floating Pill Dock:** Capsule container (`shape = CircleShape`), tonal background `surfaceContainer` (without artificial drop shadows), horizontally centered at the bottom with breathing room (`padding(bottom = 16.dp, horizontal = 16.dp)`).
2. **Dynamic Icon on Click (Animated Entry Only on Selected Tab):**
   * Tab **NOT Selected**: Displays **STRICTLY TEXT ONLY** for the section (no icon visible!). Color: `onSurfaceVariant`.
   * Tab **SELECTED**: Enclosed within an active pill (`surfaceContainerHighest` or `secondaryContainer`). The section icon **APPEARS ONLY WHEN CLICKED**, positioned to the left of the text label.
3. **Native M3 Expressive Animations:**
   * Icon appearance/disappearance and pill resizing use `MaterialTheme.motionScheme.fastSpatialSpec()` (or `spring(dampingRatio = 0.8f, stiffness = 380f)`), while color transitions use `MaterialTheme.motionScheme.fastEffectsSpec()`.
4. **Strict Limit of Maximum 4 Elements (ABSOLUTE PROHIBITION OF FALLBACK):**
   * Supports a strict maximum of 4 elements (`require(items.size in 2..4)`).
   * **NO FALLBACK:** Falling back to the standard 5-element bar is strictly prohibited. The application's information architecture MUST be condensed upfront into at most 4 core destinations.
5. **Soft Gradient Protection Overlay (Zero Occlusion & High Readability):**
   * The floating dock container is enclosed within a full-width background gradient (`Brush.verticalGradient`) extending behind the dock through the navigation bar insets (`.navigationBarsPadding()`).
   * Fades seamlessly from `Color.Transparent` (0% opacity at the top of the dock breathing room) down to `MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)` and `MaterialTheme.colorScheme.surface` (100% solid opacity at the bottom edge).

### 9.3 Fixed Expressive Bottom Action Button (Single Primary Action on Secondary Screens)

When a secondary or detail screen has strictly ONE primary user action (e.g., "Start cooking!", "Start workout", "Confirm Order", "Export Report"):
1. **Singular Focus (No Multiple FABs):** Do not place mini-FAB stacks or multiple conflicting buttons. Anchor a prominent, full-width Expressive button at the bottom of the screen (`Alignment.BottomCenter`).
2. **Soft Gradient Protection Mask:**
   * Exactly like the floating navigation bar, wrap the button inside a full-width `Box` with `Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), MaterialTheme.colorScheme.surface))`.
   * Extends through system bars via `.navigationBarsPadding()` with comfortable padding (`start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp`).
3. **Button Specifications:**
   * Fixed height of `56.dp` and `fillMaxWidth()`.
   * Rounded squircle shape (`RoundedCornerShape(16.dp)` or `CircleShape`).
   * Semantic colors: `containerColor = MaterialTheme.colorScheme.primary`, `contentColor = MaterialTheme.colorScheme.onPrimary`, with zero artificial shadow (`defaultElevation = 0.dp`).
   * Leading action icon (`24.dp`, e.g., `Icons.Rounded.PlayArrow`) paired with bold text in `MaterialTheme.typography.titleMedium`.
4. **Scroll Inset Allocation:** Screens containing this button must apply bottom content padding to their `LazyColumn` (e.g., `contentPadding = PaddingValues(bottom = 96.dp + innerPadding.calculateBottomPadding())`) so content is never trapped behind the button.

---

## 10. Dynamic Dashboards & Expressive Layouts

Primary dashboards must adhere to the following dynamic conventions:
1. **Dynamic Expressive Header:**
   * Contextual greeting (`ContextualGreetingHeader`) in `ExpressiveType.greetingHeadline` or `MaterialTheme.typography.headlineMediumEmphasized`.
   * User profile avatar or Settings icon positioned in the top-right corner of the `TopAppBar`.
2. **2x2 Bento Grid for Quick Actions ("Your Tools"):**
   * A 2x2 grid composed of 4 compact cards (`surfaceContainer` or `surfaceContainerHigh`) with squircle corners at `16.dp–20.dp`.
   * Each card contains an easily recognizable Material vector icon and a readable label in `titleSmallEmphasized` or `labelLargeEmphasized`.
3. **Structured Section Separation:**
   * Clean tonal transition based on surface containers (`surfaceContainer` / `surfaceContainerLow`) or an `HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)`.
   * **ABSOLUTE BAN ON CUSTOM WAVE SEPARATORS (Zero Canvas/Bezier):** Never draw custom wavy dividers with `Canvas` to partition sections; the only approved waves are those intrinsic to the official `LinearWavyProgressIndicator` and `CircularWavyProgressIndicator` components.

---

## 11. Mandatory Settings Screen (System Invariant)

Every application **MUST ALWAYS** include a dedicated, accessible Settings Screen:
* **Primary Entry Point:** Profile avatar or Settings button in the top-right of the `TopAppBar` (`Icons.Outlined.AccountCircle` or `Icons.Outlined.Settings`).
* **Minimum Required Sections:**
  1. **Data & Demo Management:** Options to reset/reload demo data (seed) and export system configuration.
  2. **Notifications & Automations:** Switches controlling status alerts, push notifications, and automation triggers.
  3. **Info & Version:** App version, build number, and Material 3 Expressive compliance note.
  4. **Application Built with Spark! / Realizzata con Spark! (Binding Final Section - ALWAYS Rule):**
     * Every Settings screen must ALWAYS conclude with a grouped card dedicated to Spark (`GroupedListCard`).
     * **Section Title:** Strictly localized (`"Realizzata con Spark!"` in Italian, `"Built with Spark!"` in English via `stringResource`).
     * **Mandatory Interactive Entries (launched via `Intent.ACTION_VIEW`):**
       1. **Project GitHub:** Link to `https://github.com/leonardo-mais/Spark` (with code/GitHub icon).
       2. **Andrea Sorge:** Subtitle `"Architettura e build"` / `"Architecture & build"`, link to `https://github.com/Sorgandri03`.
       3. **leonardo.mais:** Subtitle `"UI e UX"`, link to `https://github.com/leonardo-mais`.

---

## 12. Grouped Lists Pattern (Unified Block Container Cards)

Lists of correlated items (settings, transactions, devices, passes) must adopt the Block Container Card pattern (`GroupedListCard`, Section 21.7):
1. **Unified Container Card:**
   * A single `Surface` or `Card` for each thematic group: `shape = RoundedCornerShape(20.dp)`, background `MaterialTheme.colorScheme.surfaceContainerLow` or `surfaceContainer`.
   * Section title placed outside at the top-left in `MaterialTheme.typography.titleSmallEmphasized` (`primary` or `onSurfaceVariant` color).
2. **Internal Separators:**
   * Insert an `HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)` between consecutive items within the card. No external row margins.
3. **Variant with Icons:**
   * Icon on the left housed in a squircle box (`RoundedCornerShape(12.dp)` or `CircleShape`, `40.dp`) with semantic tonal fill (`primaryContainer`, `secondaryContainer`, `surfaceContainerHighest`).
   * Text hierarchy: Main title in `titleMedium` / `bodyLargeEmphasized` + subtitle in `bodySmall` (`onSurfaceVariant`).
   * Trailing element: numeric value, `Switch`, `Checkbox`, or chevron icon.
4. **Text-Only Variant:**
   * Streamlined rows with `16.dp` horizontal padding and `14.dp` vertical padding, clear typography, and surface-wide click ripple.

---

## 13. Tonal Transition TopAppBar on Scroll & Circular Back Button

### 13.1 Scrolling Color Transition

App bars (`TopAppBar`, `MediumTopAppBar`, `LargeTopAppBar`, `TwoRowsTopAppBar`):
1. **At Top of Page (`Scroll = 0`):** Same color as the screen baseline background (`containerColor = MaterialTheme.colorScheme.surface`), appearing flush with the layout.
2. **During Scrolling (`On Scroll`):** Automatic transition to a higher tonal surface (`scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer` or `surfaceContainerHigh`).
3. **Mandatory Wiring:** Always provide `scrollBehavior` (`TopAppBarDefaults.pinnedScrollBehavior()` or `exitUntilCollapsedScrollBehavior()`) to the AppBar and bind `Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)` to the screen's `Scaffold`.

### 13.2 Mandatory Circular Back Button

On secondary or detail screens, the back navigation button **MUST ALWAYS AND WITHOUT EXCEPTION** be enclosed within a circular `surfaceContainerHigh` container:

```kotlin
IconButton(
    onClick = onNavigateBack,
    modifier = Modifier
        .padding(start = 8.dp)
        .size(40.dp),
    colors = IconButtonDefaults.filledIconButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
) {
    Icon(
        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
        contentDescription = stringResource(R.string.back)
    )
}
```

---

## 14. Pill Search Top Bar & Mandatory Full-Screen SearchView

When search is a primary feature of the application (`AppSearchView`, Section 21.8):
* **Integrated Pill Structure:**
  * **Left:** Navigation menu icon (`Icons.Rounded.Menu`) or search icon (`Icons.Rounded.Search`).
  * **Center:** Pill field (`CircleShape`, fixed height `56.dp` inside a `72.dp` bar) with localized placeholder (`bodyLarge`, `onSurfaceVariant`).
  * **Right:** Up to two optional action icons + circular profile avatar for instant access to Settings.
* **`surfaceBright` Container Color (Mandatory Default):**
  * The search pill uses `MaterialTheme.colorScheme.surfaceBright` to establish a crisp, radiant contrast against the `surface` background, ensuring a minimum contrast ratio of 3:1.
* **Placement in the First Tab:** The screen housing the primary search bar **MUST ALWAYS BE the FIRST TAB** in the bottom navigation bar (e.g., Home).
* **Mandatory Full-Screen SearchView Expansion (`ExpandedFullScreenSearchBar` / `AppSearchView`):**
  * Tapping the bar must expand a full-screen modal search view featuring: top-left circular/standard back button, active text field with Clear button (`Icons.Rounded.Close`), `HorizontalDivider`, recent search history (`Icons.Rounded.History`), live filtered results grouped in cards, and automatic handling of the system back gesture (`BackHandler`).
* **Inviolable Height Rule:** Never compress a `TopAppBar`. Respect standard M3 height metrics (`64.dp` for `TopAppBar`, `72.dp` for the `SearchTopBar` area with internal `56.dp` pill, `152.dp` for expanded `LargeTopAppBar`).

---

## 15. M3 Expressive Motion Physics (`MotionScheme`) & Micro-Interactions

In Material 3 Expressive, animations avoid static time curves (`tween`) for spatial transitions, using physical spring systems from `MaterialTheme.motionScheme`:
1. **Spatial Transitions (Movement, Expansion, Resizing, Morphing, Scale):**
   * Use `MaterialTheme.motionScheme.defaultSpatialSpec()` for layout shifts and card transitions, or `MaterialTheme.motionScheme.fastSpatialSpec()` for smaller elements (navigation pills, switches, buttons, icons).
   * These specifications deliver snappy spring physics (equivalent to `spring(dampingRatio = 0.8f, stiffness = 380f)`) ensuring immediate responsiveness without sluggish drag.
   * **Prohibition of sluggish springs:** Never use `stiffness < 200f`.
2. **Effects Transitions (Color, Alpha, Fades):**
   * Use `MaterialTheme.motionScheme.defaultEffectsSpec()` or `MaterialTheme.motionScheme.fastEffectsSpec()` (`150ms–200ms`, strictly zero overshoot, as color and opacity must never bounce).
3. **State Layers:**
   * Hover (`8%`), Focus (`10%` + outline ring), Press/Ripple (`10%`), Drag (tonal level increment from `surfaceContainer` to `surfaceContainerHigh`).

---

## 16. Application Icon from Material Symbols (Adaptive Scaling & Tinted Icons)

Every application must feature an official adaptive launcher icon derived from the Material Symbols Rounded catalog with active Fill (`FILL = 1`), mathematically scaled to prevent mask clipping and fully equipped for Android 13+ Themed / Tinted Icons:

1. **Geometric Canvas & Safe-Zone Dimensions:**
   * **Adaptive Canvas:** Full dimensions are strictly `108dp x 108dp` (`viewportWidth="108"`, `viewportHeight="108"`).
   * **Safe Area:** Android launchers apply circular, squircle, or rounded-square masks that clip the outer `18dp` on all sides. The safe-zone diameter is `66dp`.
   * Symbol Glyphs must be scaled to an inner optical bounding box of **`48dp` to `54dp`** centered exactly at `(54dp, 54dp)`. Never exceed `56dp` to prevent edges from being clipped by circular masks.
2. **Precise Scaling & Centering Math for Material Symbols:**
   * Wrap the `<path>` inside a `<group>` with explicit scaling and centering translations:
   * **For `24x24` Viewport (standard Google Material Symbols XML):**
     - Target `48dp` glyph: `scaleX="2.0" scaleY="2.0" translateX="30" translateY="30"`.
     - Target `54dp` glyph: `scaleX="2.25" scaleY="2.25" translateX="27" translateY="27"`.
     - Formula: $\text{translation} = \frac{108 - (24 \times \text{scale})}{2}$.
   * **For `960x960` Viewport (SVG / Font-derived glyphs from Material Symbols):**
     - Target `48dp` glyph: `scaleX="0.050" scaleY="0.050" translateX="30" translateY="30"`.
     - Target `54dp` glyph: `scaleX="0.05625" scaleY="0.05625" translateX="27" translateY="27"`.
     - Formula: $\text{translation} = \frac{108 - (960 \times \text{scale})}{2}$.
3. **Themed / Tinted App Icons Support (Android 13+ / API 33+):**
   * Mandatory `<monochrome>` layer: Both `res/mipmap-anydpi-v26/ic_launcher.xml` and `res/mipmap-anydpi-v26/ic_launcher_round.xml` MUST declare the `<monochrome>` tag referencing the foreground drawable:
     ```xml
     <adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
         <background android:drawable="@drawable/ic_launcher_background" />
         <foreground android:drawable="@drawable/ic_launcher_foreground" />
         <monochrome android:drawable="@drawable/ic_launcher_foreground" />
     </adaptive-icon>
     ```
   * The vector glyph must use a single solid fill (`fillColor="@color/ic_launcher_foreground_color"`) without complex multi-colored layers so Android can dynamically tint it seamlessly.
4. **Color Tokens:**
   * Background (`ic_launcher_background.xml`): Solid M3 semantic tone (e.g., `primaryContainer` or brand primary surface).
   * Foreground (`ic_launcher_foreground.xml`): Vector glyph tinted with semantic `onPrimaryContainer` or `primary`.
5. **Manifest Wiring:**
   * Configure both `android:icon="@mipmap/ic_launcher"` and `android:roundIcon="@mipmap/ic_launcher_round"` in `AndroidManifest.xml`.

---

## 17. Pre-Delivery Verification Checklist for the Agent

1. **Exact API Names:** Did you use `LinearWavyProgressIndicator`, `CircularWavyProgressIndicator`, `SplitButtonLayout`, and `MediumFloatingActionButton` without inverted naming?
2. **Zero Hardcoded Colors, Strings, or Container Alpha:** Did you use only `MaterialTheme.colorScheme.<role>` (without `.copy(alpha = ...)` on containers) and `stringResource(R.string.*)` (EN + IT)?
3. **Zero Emoji Policy:** Did you ensure complete absence of emoji characters across Compose code and `strings.xml`, using Material vector icons exclusively?
4. **3-FAB System Rule:** Did you use `MediumFloatingActionButton` (`68dp`, `20.dp` squircle, `30.dp` icon) as the default without text?
5. **Archetype Navigation Bar:** Classic Style $\rightarrow$ `NavigationBar` (3–5 tabs, `surfaceContainerLow` container). High-Impact Style $\rightarrow$ `ExpressiveImpactNavigationBar` (max 4 tabs, no fallback, animated icon on click, soft vertical gradient overlay fading to 0% at top).
6. **Single-Action Secondary Screens:** If a secondary or detail page has a single primary action (e.g., "Start cooking!"), did you implement `ExpressivePrimaryActionButton` with the soft vertical gradient overlay mask?
7. **Edge-to-Edge & System Bars (Zero Black Boxes):** Did you set `window.isNavigationBarContrastEnforced = false` in `MainActivity` for SDK 29+ and wrap the root in `Surface(color = MaterialTheme.colorScheme.surface)`?
8. **Typography:** Did you verify local Google Sans Flex (`res/font/google_sans_flex.ttf`), all 30 M3 styles (`*Emphasized`), and `ExpressiveType` for the High-Impact Style?
9. **Dashboard & Lists:** Did you include a contextual Header, Bento Grid, `GroupedListCard` with `HorizontalDivider(color = outlineVariant)`, and zero custom `Canvas` waves?
10. **TopAppBar & Circular Back:** Did you bind `scrollBehavior` + `nestedScroll`, and enclose the back button in `surfaceContainerHigh` (`40.dp` circle) on secondary screens?
11. **Search in First Tab:** Is the search bar (`surfaceBright`) located in the first tab, triggering full-screen `AppSearchView` on tap?
12. **Settings & Spark! Section:** Is the Settings screen present with the mandatory closing card `"Realizzata con Spark!"` / `"Built with Spark!"` and the 3 GitHub links?
13. **Multi-Level Scaffold & Navigation 3:** Does the root `Scaffold` contain only the bottom bar/snackbar (with `contentWindowInsets = WindowInsets(0.dp)` and passing only `innerPadding.calculateBottomPadding()`), while each destination screen manages its own `Scaffold` and `TopAppBar` using `@Serializable NavKey` routes?
14. **Adaptive & Tinted Icon:** Is the Material Symbol glyph correctly scaled (`48dp–54dp` inside `108dp` canvas) and does `<adaptive-icon>` declare `<monochrome android:drawable="@drawable/ic_launcher_foreground" />`?

---

## 18. Multi-Level Scaffold Architecture & WindowInsets Handling (Zero Black Boxes)

To eliminate black boxes around system bars, floating bars, disappearing components, or layout jumps during transitions:

1. **Streamlined Root Scaffold & Insets Delegation:**
   * Houses **ONLY** the bottom navigation bar (`NavigationBar` or `ExpressiveImpactNavigationBar`) and the `SnackbarHost`.
   * Set `contentWindowInsets = WindowInsets(0.dp)` when delegating insets to child screens. **NEVER** place a `TopAppBar` in the root `Scaffold`.
   * The Root `Scaffold` **MUST** pass **ONLY** the bottom inset (`innerPadding.calculateBottomPadding()`) to child screens / `NavDisplay` (e.g., `Modifier.padding(bottom = innerPadding.calculateBottomPadding())`). **NEVER** apply the entire `innerPadding` (which contains top status bar padding) to the child screen container, as that pushes the child `Scaffold` below the status bar, leaving an unpainted region that exposes Android's default pitch-black window background.
2. **Dedicated Scaffold per Child Screen:**
   * Each screen destination (`HomeScreen`, `DetailScreen`, `SettingsScreen`) maintains its own `Scaffold` with its own `TopAppBar` / `LargeTopAppBar`, consuming its `innerPadding` exactly once.
   * Scrollable lists (`LazyColumn`, `LazyRow`): **NEVER** apply `Modifier.padding(innerPadding)` to the list container (which clips the container above bars and leaves black gaps). **ALWAYS** apply `innerPadding` to `contentPadding` and consume insets with `Modifier.consumeWindowInsets(innerPadding)`.
3. **Edge-to-Edge & Navigation Bar Contrast Elimination:**
   * `enableEdgeToEdge()` must always be active in `MainActivity.onCreate()`.
   * **MANDATORY:** Set `window.isNavigationBarContrastEnforced = false` in `MainActivity.onCreate()` for SDK 29+. This eliminates the dark/black protective translucent scrim that Android automatically applies over the navigation bar in edge-to-edge mode.
4. **Baseline Surface Protection (Zero Black Window Leaks):**
   * Always wrap the root `setContent` composable in `Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface)`.
   * Set `android:windowBackground` to `@android:color/transparent` in `themes.xml`.

---

## 19. Jetpack Navigation 3 Integration & Fluid Transitions

Every application adopts **Jetpack Navigation 3** (`.agents/skills/navigation/navigation-3`):

1. **Type-Safe Routes (`NavKey`):** All routes are `@Serializable data object` or `@Serializable data class` implementations of `NavKey`.
2. **`NavDisplay` and `entryProvider` DSL:** Map each `NavKey` to its Composable via `entryProvider { entry<RouteKey> { ... } }`, maintaining multiple back stacks (`rememberNavigationState` / `rememberNavBackStack`) for top-level navigation tabs so state survives configuration changes and process death.
3. **Transitions:** Native cross-fade between top-level tabs; horizontal slide with `predictivePopTransitionSpec` for list $\rightarrow$ detail navigation; `BottomSheetSceneStrategy` and `DialogSceneStrategy` for modal surfaces.

---

## 20. Progressive Disclosure & Official Modules Integration (official Google skills)

Before authoring critical architectural or subsystem code, consult the official Google Android modules and recipes on-demand in [`.agents/skills/`](../ATTRIBUTION.md) using file-reading tools:

| Operational Domain | Official Module & Direct Recipe Paths in `.agents/skills/` | Consultation & Integration Notes |
| :--- | :--- | :--- |
| **Navigation 3 & Multiple Back Stacks** | • [`navigation/navigation-3/SKILL.md`](../navigation-3/SKILL.md)<br>• [`.../recipes/multiple-backstacks.md`](../navigation-3/references/android/guide/navigation/navigation-3/recipes/multiple-backstacks.md)<br>• [`.../recipes/common-ui.md`](../navigation-3/references/android/guide/navigation/navigation-3/recipes/common-ui.md)<br>• [`.../recipes/modular-hilt.md`](../navigation-3/references/android/guide/navigation/navigation-3/recipes/modular-hilt.md)<br>• [`.../recipes/animations.md`](../navigation-3/references/android/guide/navigation/navigation-3/recipes/animations.md)<br>• [`.../recipes/bottomsheet.md`](../navigation-3/references/android/guide/navigation/navigation-3/recipes/bottomsheet.md) | Use for `NavKey`, `NavDisplay`, `entryProvider`, multi-backstack state persistence (`rememberNavigationState`), Hilt modularization, and scene transitions. **Keep `spark-jetpack`'s Multi-Level Scaffold and Bottom Bar UI components.** |
| **Compose Performance & State Stability** | • [`jetpack-compose/jetpack-compose-performance/SKILL.md`](../jetpack-compose-performance/SKILL.md)<br>• [`.../references/state-and-stability.md`](../jetpack-compose-performance/references/state-and-stability.md)<br>• [`.../references/lazy-layouts.md`](../jetpack-compose-performance/references/lazy-layouts.md) | Mandatory patterns for `@Immutable`/`@Stable`, avoiding backwards writes, lambda phase deferral (`Modifier.offset { }`, `graphicsLayer { }`), `derivedStateOf`, and `LazyColumn` stable `key` + `contentType`. |
| **Material 3 Adaptive & Multi-Pane** | • [`jetpack-compose/adaptive/SKILL.md`](../adaptive/SKILL.md)<br>• [`.../navigation-3/recipes/material-listdetail.md`](../navigation-3/references/android/guide/navigation/navigation-3/recipes/material-listdetail.md)<br>• [`.../navigation-3/recipes/material-supportingpane.md`](../navigation-3/references/android/guide/navigation/navigation-3/recipes/material-supportingpane.md) | Consult for `ListDetailPaneScaffold`, `SupportingPaneScaffold`, adaptive column grids, and `@FormFactorPreviews`. **Note:** On compact phones, always preserve `spark-jetpack`'s `ExpressiveImpactNavigationBar` or Classic `NavigationBar` instead of replacing the root Scaffold with `NavigationSuiteScaffold`. |
| **Edge-to-Edge & System Insets** | • [`system/edge-to-edge/SKILL.md`](../edge-to-edge/SKILL.md) | Consult for `enableEdgeToEdge()`, IME soft-keyboard insets (`WindowInsets.ime`), and `consumeWindowInsets`. Combine with Section 18 (`Multi-Level Scaffold` & `isNavigationBarContrastEnforced = false`). |
| **Runtime Permissions & Privacy** | • [`security/android-permissions-security/SKILL.md`](../android-permissions-security/SKILL.md) | Contextual runtime permission flows, rationale handling, Android Photo Picker (`PickVisualMedia`), and `POST_NOTIFICATIONS`. |
| **Intent Security & Deep Links** | • [`security/android-intent-security/SKILL.md`](../android-intent-security/SKILL.md)<br>• [`.../navigation-3/recipes/deeplinks-uriarguments.md`](../navigation-3/references/android/guide/navigation/navigation-3/recipes/deeplinks-uriarguments.md) | Safe external `Intent.ACTION_VIEW` launching (including the Settings Spark! links), `FileProvider` sharing, and deep link validation. |
| **CameraX, Widgets & Testing** | • [`camera/camerax/SKILL.md`](../camerax/SKILL.md)<br>• [`system/glance-canonical-layouts/SKILL.md`](../glance-canonical-layouts/SKILL.md)<br>• [`testing/testing-setup/SKILL.md`](../testing-setup/SKILL.md) | Consult on-demand when the app requires camera capture, Home Screen Glance widgets, or automated test configuration during the Iterative Loop. |

### 20.1 Domain-Scoped Priority Hierarchy (Conflict Resolution Rule)

To prevent contradictions between generic Google snippets in official Google skills and Spark's visual identity:
1. **For UI Design System, Theme (`MaterialExpressiveTheme`), Color Tokens, Typography, App Bars, Multi-Level Scaffold, and Archetypes (Classic vs. High-Impact):**
   $$\text{1. spark-jetpack} \;\longrightarrow\; \text{2. development-standards.md} \;\longrightarrow\; \text{3. official Google skills}$$
   *(Never let generic examples in official Google skills override `spark-jetpack`'s Multi-Level Scaffold, `ExpressiveImpactNavigationBar`, 3-FAB rule, or `MaterialExpressiveTheme`).*
2. **For Architectural Mechanics, Navigation 3 State & Recipes, Compose Recomposition Performance, System Insets/IME, and Security/Permissions:**
   $$\text{1. official Google skills} \;\longrightarrow\; \text{2. spark-jetpack} \;\longrightarrow\; \text{3. development-standards.md}$$

---

## 21. Design Archetypes & Official Code Blueprints

The aesthetic archetype is determined in alignment with the user during the Planning Interview (`planning-interview.md`):

### 21.1 Archetype Comparison Matrix

| UI/UX Dimension | Classic Style (Rigorous M3 Expressive) | High-Impact Style (Expressive Dynamic) |
| :--- | :--- | :--- |
| **Experiential Goal** | Sobriety, high information density, maximum scannability and efficiency. | "WOW" effect, emotional engagement, monumental hierarchy, and dynamic motion. |
| **Target Use Cases** | Utilities, B2B management apps, inventory trackers, dense input forms, technical finance. | Consumer apps, personal tracking, health & wellness, smart home, habit trackers. |
| **Navigation Bar** | Standard M3 `NavigationBar` (3–5 tabs, `surfaceContainerLow` container, `secondaryContainer` pill). | Floating Pill Dock (`ExpressiveImpactNavigationBar`, max 4 items, icon only on active tab, soft vertical gradient overlay). |
| **Typography** | The 30 official M3 roles (Baseline + `*Emphasized`) on `GoogleSansFlexFontFamily`. | 6-axis `flexFont` (`wght`, `wdth`, `ROND`, `GRAD`) + `ExpressiveType` utility object. |
| **Hero Display / KPI** | Standard M3 card with `headlineMediumEmphasized` (28sp) and subtitle. | `BentoHeroCard` with giant numerals (`48sp` `heroGiant`), fraction counter, percentage pill, and `LinearWavyProgressIndicator`. |
| **Dashboard & Statistics** | Grouped lists with standard icons and right-aligned values. | `QuickMetricsBentoStrip` across 3 columns with scannable `38sp` numerals (`heroLarge`). |
| **TopAppBar & Greetings** | Static page title in `titleLargeEmphasized` / `headlineSmall`. | `ContextualGreetingHeader` (localized morning/afternoon/evening greeting) + formatted system date. |
| **Dates & Temporal Selectors** | Standard 16sp day numbers with conventional circular indicator. | Dynamic numerals: unselected `480 wght` (`0 ROND`); selected `850 wght`, `112 wdth`, `100 ROND`. |

### 21.2 Code Blueprint: `BentoHeroCard` (High-Impact Style)

```kotlin
package it.spark.app.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.spark.app.presentation.theme.ExpressiveType

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BentoHeroCard(
    metricTitle: String,
    primaryValue: Int,
    totalValue: Int,
    percentage: Float,
    statusText: String,
    modifier: Modifier = Modifier
) {
    val isCompleted = primaryValue == totalValue && totalValue > 0

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metricTitle.uppercase(),
                    style = ExpressiveType.statLabel,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    }
                ) {
                    Text(
                        text = "${(percentage.coerceIn(0f, 1f) * 100).toInt()}%",
                        style = ExpressiveType.heroMedium,
                        color = if (isCompleted) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = primaryValue.toString(),
                    style = ExpressiveType.heroGiant,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " / $totalValue",
                    style = ExpressiveType.heroMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearWavyProgressIndicator(
                progress = { percentage.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}
```

### 21.3 Code Blueprint: `QuickMetricsBentoStrip` (High-Impact Style)

> **M3 Color & i18n Compliance Note:** Labels are passed via `stringResource(...)` parameters (zero hardcoded strings), and the alert card uses pure semantic tokens `errorContainer` and `onErrorContainer` (without `.copy(alpha = 0.5f)`), guaranteeing WCAG AA contrast across both Light and Dark themes.

```kotlin
package it.spark.app.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.spark.app.presentation.theme.ExpressiveType

@Composable
fun QuickMetricsBentoStrip(
    totalItems: Int,
    healthyItems: Int,
    alertItems: Int,
    totalLabel: String,
    healthyLabel: String,
    alertLabel: String,
    modifier: Modifier = Modifier
) {
    val hasAlerts = alertItems > 0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Total
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = totalLabel.uppercase(),
                    style = ExpressiveType.statLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = totalItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Card 2: Active / Normal
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = healthyLabel.uppercase(),
                    style = ExpressiveType.statLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = healthyItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Card 3: Alert (Pure M3 tokens errorContainer / onErrorContainer)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (hasAlerts) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = alertLabel.uppercase(),
                    style = ExpressiveType.statLabel,
                    color = if (hasAlerts) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = alertItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = if (hasAlerts) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}
```

### 21.4 Code Blueprint: `ContextualGreetingHeader` (High-Impact Style)

```kotlin
package it.spark.app.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import it.spark.app.presentation.theme.ExpressiveType
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ContextualGreetingHeader(
    morningGreeting: String,
    afternoonGreeting: String,
    eveningGreeting: String,
    modifier: Modifier = Modifier
) {
    val currentHour = remember { LocalTime.now().hour }
    val greeting = when (currentHour) {
        in 5..12 -> morningGreeting
        in 13..17 -> afternoonGreeting
        else -> eveningGreeting
    }

    val todayFormatted = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        ).replaceFirstChar { it.uppercase() }
    }

    Column(modifier = modifier) {
        Text(
            text = greeting,
            style = ExpressiveType.greetingHeadline,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = todayFormatted,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

### 21.5 Code Blueprint: `ExpressiveImpactNavigationBar` (Floating Pill Dock)

This component implements the Floating Pill Dock Navigation Bar by consuming `MaterialTheme.motionScheme` tokens (`fastSpatialSpec()` and `fastEffectsSpec()`) alongside pure Surface Container tokens (`surfaceContainer` and `surfaceContainerHighest`, with zero artificial drop shadows):

```kotlin
package it.spark.app.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

data class ExpressiveNavItem(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String? = null
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveImpactNavigationBar(
    items: List<ExpressiveNavItem>,
    selectedRoute: Any,
    onItemSelected: (ExpressiveNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    require(items.size in 2..4) {
        "ExpressiveImpactNavigationBar supports a strict maximum of 4 elements (provided: ${items.size}). " +
            "Do not use fallback mechanisms: condense the information architecture into at most 4 sections."
    }

    val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntSize>()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .navigationBarsPadding()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 6.dp)
                    .animateContentSize(animationSpec = spatialSpec),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item.route == selectedRoute
                    ExpressiveImpactNavItemView(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onItemSelected(item) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpressiveImpactNavItemView(
    item: ExpressiveNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorEffectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Color>()
    val floatEffectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()
    val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntSize>()

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            Color.Transparent
        },
        animationSpec = colorEffectsSpec,
        label = "pillContainerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = colorEffectsSpec,
        label = "pillContentColor"
    )

    Surface(
        shape = CircleShape,
        color = containerColor,
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .clickable(
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = if (isSelected) 18.dp else 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Icon APPEARS ONLY ON CLICK via M3 Expressive MotionScheme
            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(animationSpec = spatialSpec) + fadeIn(floatEffectsSpec),
                exit = shrinkHorizontally(animationSpec = spatialSpec) + fadeOut(floatEffectsSpec)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            // Text label (uses labelLargeEmphasized when selected, labelLarge when unselected)
            Text(
                text = item.label,
                style = if (isSelected) {
                    MaterialTheme.typography.labelLargeEmphasized
                } else {
                    MaterialTheme.typography.labelLarge
                },
                color = contentColor
            )
        }
    }
}
```

### 21.6 Code Blueprint: `ExpressivePrimaryActionButton` (Single-Action Secondary Screens)

This component implements the Fixed Bottom Action Button with a soft gradient protection mask, designed when a secondary/detail screen has strictly ONE primary action:

```kotlin
package it.spark.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExpressivePrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = Icons.Rounded.PlayArrow,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .navigationBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
```

### 21.7 Code Blueprint: `GroupedListCard` & `GroupedListRow` (Section 12 Standard)

Unified container card (`RoundedCornerShape(20.dp)`, `surfaceContainerLow`) with internal `outlineVariant` dividers and `40.dp` squircle icon badges:

```kotlin
package it.spark.app.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GroupedListCard(
    modifier: Modifier = Modifier,
    sectionTitle: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!sectionTitle.isNullOrBlank()) {
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleSmallEmphasized,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = containerColor
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GroupedListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    iconContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    showDivider: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = iconContainerColor,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = iconContentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(12.dp))
                trailingContent()
            }
        }
        if (showDivider) {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
```

### 21.8 Code Blueprint: `AppSearchView` (Section 14 Pill Search Top Bar + Full-Screen Expansion)

Implements the `72.dp` outer search header with `56.dp` `surfaceBright` inner pill, profile avatar button, and full-screen `ExpandedFullScreenSearchBar` / modal overlay with `BackHandler` and recent history:

```kotlin
package it.spark.app.presentation.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppSearchTopBar(
    placeholderText: String,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    profileContentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(72.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceBright,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(CircleShape)
                .clickable(onClick = onSearchClick)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = profileContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppSearchView(
    expanded: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    placeholderText: String,
    backContentDescription: String,
    clearContentDescription: String,
    recentSearches: List<String> = emptyList(),
    onRecentSearchClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    resultsContent: @Composable ColumnScope.() -> Unit
) {
    BackHandler(enabled = expanded) {
        onExpandedChange(false)
    }

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
        exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec())
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onExpandedChange(false) },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = backContentDescription
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = placeholderText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = clearContentDescription,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                if (query.isBlank() && recentSearches.isNotEmpty()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        GroupedListCard {
                            recentSearches.forEachIndexed { index, item ->
                                GroupedListRow(
                                    title = item,
                                    leadingIcon = Icons.Rounded.History,
                                    iconContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    showDivider = index < recentSearches.lastIndex,
                                    onClick = { onRecentSearchClick(item) }
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        content = resultsContent
                    )
                }
            }
        }
    }
}
```
