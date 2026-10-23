# Rule: Android Development & UI Standards

## Overview

This rule sets the non-negotiable architecture, UI design, performance, accessibility, localization and build standards for all code generated or modified in this repository. Detailed patterns and component references are delegated to the skills in `.agents/skills/`. Priority when guidance conflicts: official Google skills, then `spark-jetpack`, then this file (see `AGENTS.md`).

---

## 1. Architecture

- **Current structure: a single Gradle module `:app`** (package `it.spark.app`). The layers are packages:
  - `presentation/`: Compose screens, components, theme, and ViewModels.
  - `domain/`: pure Kotlin models and use cases, with zero Android SDK dependencies.
  - `data/`: Room DAOs, repositories, and mappers.
- **Dependency direction:** presentation depends only on domain. It never accesses data or network DTOs directly.
- **Future modularization:** when the app grows into feature modules, use `:feature:<name>:domain`, `:feature:<name>:data`, `:feature:<name>:presentation`, plus shared `:core:*` modules. Features never depend on each other. Cross-feature models live in `:core:model` or `:core:domain`. Split a module only when a feature really exists.
- **MVI / UDF presentation pattern:**
  - Every screen defines an immutable `UiState`, a sealed `UiIntent`, and one-off `UiEffect`s, delivered through `Channel<UiEffect>(capacity = Channel.BUFFERED)`.
  - State is exposed as `StateFlow<UiState>` with `stateIn(SharingStarted.WhileSubscribed(5_000))`.
  - In Compose, collect state with `collectAsStateWithLifecycle()` and effects with `LaunchedEffect`.
- **Data layer and offline-first SSOT:**
  - Repositories expose Room DAO `Flow<T>` as the single source of truth.
  - Map entities and DTOs to domain models (`toDomain()`) at the data boundary.
  - Wrap failures in typed domain errors (`Result<T, DomainError>`).
- **Type-safe navigation with Navigation 3:**
  - Every route MUST implement `NavKey` and be a `@Serializable` Kotlin object or class.
  - Use `NavDisplay` with the `entryProvider` DSL.
  - Keep navigation state across process death and rotation with `rememberNavigationState` (for apps with a bottom bar and several back stacks) or `rememberNavBackStack(startRoute)`.
  - Strictly forbidden: navigation driven by hand-made boolean flags (for example `var isSettingsOpen by remember`) or by monolithic `when {}` selectors.
- **Predictive Back Navigation (Android 13/14+ / API 33+):**
  - Applications MUST declare `android:enableOnBackInvokedCallback="true"` in the `<application>` tag of `AndroidManifest.xml` to enable system-level and in-app predictive back animations.
  - In Navigation 3, `NavDisplay` MUST configure `predictivePopTransitionSpec` alongside `popTransitionSpec` to smoothly animate screen transitions during back swipes.
  - In Compose sub-views, modal dialogs, and search overlays (`AppSearchView`), intercept back events via `BackHandler(enabled = ...)` or `PredictiveBackHandler`.
  - Legacy `Activity.onBackPressed()` and raw `KeyEvent.KEYCODE_BACK` overrides are strictly deprecated and forbidden because they lack gesture progress lifecycles (`OnBackAnimationCallback` / `BackEventCompat`).
- **Dependency injection:** Hilt, with `@HiltViewModel`, `@Inject`, `@Module` and `@Binds`, using constructor injection.
- **Official references:** for deep dives and code blueprints, consult the official Google skills in `.agents/skills/`, in particular `navigation-3` (type-safe navigation), `jetpack-compose-performance` (state stability) and `adaptive` (adaptive layouts).

---

## 2. Design System: Material 3 Expressive

- **Theme and motion:**
  - Wrap the UI with `MaterialExpressiveTheme` and `MotionScheme.expressive()`.
  - Interactive micro-interactions on click and toggle use snappy springs (`spring(dampingRatio = 0.8f, stiffness = 380f)`).
  - Spatial transitions: 250–300 ms. Effect transitions (color, alpha, screen fade): **80 ms** (`tween(80)`, doubled speed / ultra-snappy dissolvenza). Never allow slow animations.
- **App icon (Material Symbols, Filled):**
  - Every application MUST have an official adaptive icon (`ic_launcher` and `ic_launcher_round`), generated from a **Material Symbol with Fill active** (`FILL = 1`, Rounded style). See `spark-jetpack` section 16.
  - Built from `ic_launcher_background.xml` (a semantic M3 tone, for example `primaryContainer`) and `ic_launcher_foreground.xml` (a filled vector glyph in `onPrimaryContainer` or `primary`).
- **Dynamic semantic colors (zero hardcoded colors):**
  - NEVER hardcode colors. `Color(0xFF...)` is strictly forbidden in UI components.
  - Colors come strictly from `MaterialTheme.colorScheme.*`.
  - Always pair a container color with its `on<Role>` color (`onPrimaryContainer` on `primaryContainer`, `onSurface` on `surface`, and so on).
  - Use tonal surfaces (`surfaceContainerLowest` through `surfaceContainerHighest`) instead of elevation shadows.
  - Dividers and subtle borders use `outlineVariant`.
- **Multi-level scaffold and bar invariant:**
  - The application-level root `Scaffold` hosts ONLY the bottom `NavigationBar` / `ExpressiveImpactNavigationBar` and the `SnackbarHost`.
  - Strictly forbidden: a `TopAppBar` or `when` / `if` conditionals in the root `Scaffold` to show or hide the navigation bar. This causes bars that float or disappear.
  - Each screen (Home, Detail, Settings) owns its own `Scaffold`, with its own `TopAppBar` or `LargeTopAppBar`.
  - **WindowInsets and edge-to-edge:** `enableEdgeToEdge()` must always be active in `MainActivity.onCreate()`, accompanied by `window.isNavigationBarContrastEnforced = false` (SDK 29+) to eliminate system scrim overlays. The root `Scaffold` passes `contentWindowInsets = WindowInsets(0.dp)`. When using the floating dock (`ExpressiveImpactNavigationBar`), `NavDisplay` MUST use `Modifier.fillMaxSize()` WITHOUT `Modifier.padding(bottom = innerPadding.calculateBottomPadding())` so child screens draw edge-to-edge behind the floating dock and progressive gradient. Bottom spacing is allocated strictly via `contentPadding` on scrollable lists (`LazyColumn`), NEVER via `Modifier.padding(innerPadding)` on the list container.
- **Expressive components and opt-in:**
  - Annotate with `@OptIn(ExperimentalMaterial3ExpressiveApi::class)`.
  - **Three FAB variants, and the medium default (invariant):**
    1. **Standard FAB (56×56 dp):** `RoundedCornerShape(16.dp)`, 24dp icon. For ordinary actions or compact spaces.
    2. **Medium FAB (68×68 dp):** `RoundedCornerShape(20.dp)`, 30dp icon. **THIS IS THE MANDATORY DEFAULT.**
    3. **Large FAB (96×96 dp):** `RoundedCornerShape(28.dp)`, 36dp icon. Use **ONLY** when the action is the crucial, central action of the screen.
  - **NO TEXT IN FABs:** FABs never contain text. Extended FABs with a text label are strictly forbidden. Each FAB contains only a centered icon.
  - **Anti-capsule / squircle:** never use capsule or pill shapes (`CircleShape` or 50% radius) on FABs. Use only the proportional squircle shape.
  - **Elevation exception:** FABs keep the default M3 elevation of `MediumFloatingActionButton`. The no-shadow rule above applies to cards and containers, not to FABs.
- **Typography (Classic and Impact styles):**
  - The standard font is **Google Sans Flex** ([Google Fonts specimen](https://fonts.google.com/specimen/Google+Sans+Flex)), bundled locally as a variable font in `res/font/google_sans_flex.ttf`, for offline, zero-latency rendering.
  - The 30 M3 styles (15 Baseline + 15 Emphasized) are the type scale. See `spark-jetpack` section 6.
  - Two archetypes, chosen during the discovery interview (see `grill-me`):
    1. **Classic style:** standard M3 roles on `GoogleSansFlexFontFamily`. For enterprise and form-heavy apps.
    2. **Impact style:** six-axis parametric typography (`wght`, `wdth`, `ROND`, `GRAD`), Bento hero cards with large numbers, quick-metric strips, contextual greetings, and pill tags. For consumer, wellness and lifestyle apps. Full specifications in `spark-jetpack` section 21.
- **Navigation bar, two archetypes (invariant):**
  - **Classic style:** standard M3 `NavigationBar`, 3 to 5 destinations, icons always visible (outlined when inactive, filled when active), compact pill indicator, label below.
  - **Impact style: Floating Pill Dock:**
    - **Floating pill container:** compact capsule (`shape = CircleShape`), `surfaceContainer` tonal surface, centered at the bottom with breathing room inside a soft progressive multi-stop ease-in gradient (`Brush.verticalGradient(0.0f to Transparent, 0.2f to surface(0.08f), 0.4f to surface(0.25f), 0.6f to surface(0.52f), 0.8f to surface(0.82f), 1.0f to surface(0.98f))`). Content smoothly scrolls underneath without visible rectangular clipping.
    - **Icon only on tap:** inactive destinations show ONLY the text (no icon). The icon of the active destination appears ONLY WHEN IT IS TAPPED, to the left of the text, inside the active pill (`surfaceContainerHighest`).
    - **Native animations:** icon transition with `AnimatedVisibility` and horizontal expansion (`expandHorizontally(spring(1.0f, 420f)) + fadeIn(tween(80))`; exit `shrinkHorizontally(spring(1.0f, 420f)) + fadeOut(tween(80))`) with critically damped spring (zero bounce). The active pill uses `animateContentSize(spring(1.0f, 420f))`, and color uses `animateColorAsState(tween(80))`.
    - **Maximum of 4 items (ABSOLUTE PROHIBITION OF FALLBACK):** this style accepts 2 to 4 destinations. Falling back to a 5-item bar is strictly forbidden. The information architecture and the flows MUST be condensed into at most 4 main sections.
- **Mandatory circular back button:** in detail and sub-screens, the back button MUST ALWAYS sit in a circular container: `Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh, modifier = Modifier.size(40.dp))` with `Icons.AutoMirrored.Rounded.ArrowBack`.
- **Collapsing LargeTopAppBar with tonal transition on scroll:**
  - App bars start with the background color (`containerColor = surface`), then fill with a contrasting color on scroll (`scrolledContainerColor = surfaceContainer` or `surfaceContainerHigh`).
  - The bar can stay pinned (`pinnedScrollBehavior`), or hide and reappear on scroll (`enterAlwaysScrollBehavior` or `exitUntilCollapsedScrollBehavior`).
  - In sub-screens, use `LargeTopAppBar` with `TopAppBarDefaults.exitUntilCollapsedScrollBehavior()`.
- **Grouped list standard (unified card blocks):**
  - Related list items are grouped inside one container card (`shape = RoundedCornerShape(16.dp)`), separated by `HorizontalDivider(color = outlineVariant)`, with left icon badges in squircles (about 40dp).
- **Search-centric top bar pill and alternate colors:**
  - Primary screens where search is fundamental show a pill search bar, with a menu icon on the left and a circular user avatar on the right.
  - **Alternate color default:** on dark or tonal backgrounds the search container uses `surfaceBright` (instead of `surfaceContainer`), so it stands out from the surrounding elements. Keep a contrast ratio of at least **3:1** for text and icons (`onSurface` / `onSurfaceVariant`).
  - **Search bar actions:** besides the trailing avatar, the search bar may show up to two trailing action icons on mobile, ordered from most used (left) to least used (right).
  - **Mandatory SearchView on selection:** tapping the search bar MUST ALWAYS open the full-screen search view (`AppSearchView`) with automatic keyboard focus (`FocusRequester.requestFocus()` + `keyboardController.show()`), back arrow, focused input field, clear button, divider, recent searches (`History` icon), live filtering, `BackHandler` integration, and fast doubled-speed fade animation (`80ms`).
- **Mandatory default heights for app bars:**
  - Never compress or shorten an app bar. Use the standard M3 heights: 64.dp for `TopAppBar`, 72.dp for the search top bar with a 56.dp inner pill, and 152.dp for an expanded `LargeTopAppBar`.
  - App bars always span the full width of the window (`fillMaxWidth()`).
- **Mandatory settings screen and Spark section (invariant):**
  - Every application MUST provide a dedicated Settings screen, reachable from the profile avatar or the settings button in the top bar.
  - **Spark section (ALWAYS):** the last section of every Settings screen that is created or modified MUST be the Spark section, titled in the device language through string resources (English "Built with Spark!", Italian "Realizzata con Spark!"). It is a grouped card with three interactive entries that open an external URL with `Intent.ACTION_VIEW`:
    1. **Project GitHub:** `https://github.com/leonardo-mais/Spark`.
    2. **Andrea Sorge:** subtitle "Architecture & build", link `https://github.com/Sorgandri03`.
    3. **Leonardo Maistrello:** subtitle "UI & UX", link `https://github.com/leonardo-mais`.
    This requirement is permanent.
- **Strict prohibitions:**
  - Do NOT import Material 2 components from `androidx.compose.material.*`. The icon set `androidx.compose.material.icons.*` is allowed.
  - Do NOT use flat Material 3 designs with sharp default corners or rigid linear animations.
  - Do NOT make an app bar shorter than its default height.
  - Do NOT use custom Canvas or Bezier wave separators. Use tonal containers (`surfaceContainerLow`, `surfaceContainer`) and `HorizontalDivider(color = outlineVariant)`.
  - Do NOT use a single monolithic Scaffold with conditional top and bottom bars.
  - **ZERO EMOJI POLICY:** do NOT use emoji anywhere in the app: UI, Compose code, resources (titles, descriptions, empty states, chips, buttons, notifications, dialogs, or `strings.xml`). Represent visual concepts only with official Material vector icons or Material Symbols.
- **Component signatures and references:** before writing UI components, consult the `spark-jetpack` skill to verify the official signatures and the anti-hallucination rules. **Priority:** official Google skills > `spark-jetpack` > this file.

---

## 3. Compose Performance & Recomposition Hygiene

- **UiState stability:** annotate UI state data classes with `@Immutable` or `@Stable`, and make sure every property is an immutable primitive, data class, or persistent collection. This lets the Compose compiler skip recompositions.
- **Lazy collections (`LazyColumn` / `LazyRow`):**
  - ALWAYS provide a stable key: `items(items, key = { it.id })`.
  - Provide `contentType = { ... }` in heterogeneous lists, to enable item reuse.
- **Composition-phase optimization:**
  - Use `derivedStateOf` for state computed from frequently changing observables (scroll offset, live filtering, enabled-button criteria).
  - Defer state reads with lambda modifiers (`Modifier.offset { ... }`, `Modifier.graphicsLayer { ... }`) so animations skip composition and, when possible, layout.

---

## 4. State Restoration & UI Resilience

- **Process death and configuration changes:**
  - Ephemeral input (partially typed text, search drafts, active filters) MUST survive process death and screen rotation.
  - Use `rememberSaveable` for UI-local state, or `SavedStateHandle` inside the ViewModel.
- **The four fundamental UI states:** every screen MUST explicitly handle all four:
  1. **Loading:** render `LinearWavyProgressIndicator` or `CircularWavyProgressIndicator`, or a `LoadingIndicator`.
  2. **Empty:** a meaningful illustration or message, with a clear call to action.
  3. **Content:** the standard data display.
  4. **Error:** a user-friendly explanation with a retry action (`Button` or `SnackbarAction`).
  - Never show blank screens, unhandled exceptions, or silent failures.

---

## 5. Privacy, Contextual Permissions & Modern Android APIs

- **Contextual permission requests:**
  - NEVER request permissions on cold start or at app launch.
  - Request runtime permissions only in response to an explicit user action, with a clear rationale dialog when needed.
  - Handle refusal gracefully: offer a fallback, or guide the user to system settings, without infinite prompt loops.
- **Android 13/14+ capabilities:**
  - **Notifications:** check and request `POST_NOTIFICATIONS` before sending alerts.
  - **Photo and media picker:** use the Android Photo Picker (`ActivityResultContracts.PickVisualMedia`). Do not request broad `READ_EXTERNAL_STORAGE` or `READ_MEDIA_*` permissions.

---

## 6. Accessibility (A11y) & Inclusive Usability

- **Minimum touch target:** every interactive element (buttons, icon buttons, switches, chips) MUST have an effective touch target of at least **48×48 dp** (`Modifier.minimumInteractiveComponentSize()`).
- **Semantic content descriptions:**
  - Informative and interactive icons or images MUST provide a localized `contentDescription = stringResource(...)`.
  - Purely decorative icons next to visible text MUST set `contentDescription = null`, so TalkBack does not read them twice.
- **Dynamic font scaling:**
  - ALWAYS use `sp` units for text sizes, never `dp`.
  - Layouts must adapt and must not clip or truncate text at 130%–200% system font scale.

---

## 7. Localization & System Formatting (i18n)

- **Zero hardcoded strings:**
  - Hardcoded user-visible strings in composables (`Text("Save")`) are strictly forbidden. All UI copy MUST use `stringResource(R.string.<id>)`. This includes content descriptions.
  - **Language strategy:** English is the default, in `res/values/strings.xml`. Other languages live in `res/values-<code>/strings.xml` (for example `values-it/strings.xml` for Italian), and Android selects them from the device language. Every new key MUST be added to every locale file that the project ships.
  - Repository files (rules, skills, documentation, code comments) are written in English, regardless of the app locales.
- **System-aware formatters:**
  - Never concatenate or format dates, times, currencies or numbers by hand with static punctuation.
  - Use system formatters (`DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)`, `NumberFormat.getCurrencyInstance()`, and so on) so the device locale conventions apply.
- **Zero emoji invariant:** UI copy and localized resources (`strings.xml`, every locale) must NEVER contain emoji. Use Material Symbols or vector drawables for icons.

---

## 8. Build & Delivery Invariants

- **Build tooling:** `./gradlew testDebugUnitTest`, `./gradlew assembleDebug` and `./gradlew assembleRelease` are the build entry points.
- **Universal root `/apk` delivery:**
  - ALL APK outputs (debug, release, local scripts, CI/CD) MUST be placed in `<projectRoot>/apk/`.
  - Naming convention: `apk/<AppName>-v<versionName>-b<versionCode>-<buildType>.apk`.
  - Print the SHA-256 checksum, size and date when an APK is generated.
- **Continuous release:**
  - Release automation is `./release-agent` (a wrapper around `scripts/release.sh`). Usage: `./release-agent [patch|minor|major] -m "Release notes"`.
  - The agent runs `./release-agent` through the shell whenever a version bump, an Obtainium feed update or a release is requested. The user is never asked to run release commands by hand.
- **Signing:** release builds use the project release keystore (v2 signing scheme), with its credentials read from the environment as described in section 9. If the keystore or a password is missing, the release build fails. There is no fallback to the debug key.

---

## 9. Secrets & Sensitive Data (invariant)

- **Never in clear text in code or in the repository:** passwords, tokens, API keys, signing credentials (keystore path, alias, passwords) and private URLs must NEVER appear in sources, `build.gradle.kts`, `gradle.properties`, scripts, documentation or tests.
- **Only environment variables or a local `.env` file:** read sensitive values with `System.getenv(...)` or from a local `.env` file. The repository stores only a template without values (`.env.example`).
- **No fallback to real values:** when a secret is missing, the build MUST fail with a clear message (for example `requireNotNull(System.getenv("..."))`). It must never fall back to a default secret value.
- **Keystores:** `*.jks` and `*.keystore` files are never committed (they are in `.gitignore`).
- **Logs and responses:** never print or report secret values.
