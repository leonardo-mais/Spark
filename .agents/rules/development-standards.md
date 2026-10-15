---
trigger: always_on
---

# Antigravity & AI Agent Rule: Android Development & UI Standards

## Overview
This rule enforces the core, non-negotiable architectural, UI design, performance, accessibility, and build standards for all code generated or modified in this repository. Detailed implementation patterns and component references are delegated to the specialized skills.

---

## 🏛️ 1. Architecture: Modular Clean Architecture & MVI
- **Modular Layering**:
  - Features are split by feature and layer: `:feature:<name>:domain`, `:feature:<name>:data`, and `:feature:<name>:presentation`.
  - **Features NEVER depend on each other**. Cross-feature models reside in `:core:model` or `:core:domain`.
  - Domain is pure Kotlin (zero Android SDK dependencies).
  - Presentation depends only on Domain (and `:core:designsystem` / `:core:presentation`). It never accesses Data or network DTOs directly.
- **MVI / UDF Presentation Pattern**:
  - Every screen defines an immutable `UiState`, a sealed `UiIntent`, and a one-off `UiEffect` (consumed via `Channel<UiEffect>(capacity = Channel.BUFFERED)`).
  - State is exposed as `StateFlow<UiState>` using `stateIn(SharingStarted.WhileSubscribed(5_000))`.
  - In Compose, collect state with `collectAsStateWithLifecycle()` and effects with `LaunchedEffect`.
- **Data Layer & Offline-First SSOT**:
  - Repositories expose Room DAO `Flow<T>` as the Single Source of Truth.
  - Map entities/DTOs to Domain Models (`toDomain()`) at the data layer boundary.
  - Wrap failures in domain-specific typed errors (`Result<T, DomainError>`).
- **Type-Safe Navigation con Navigation 3**:
  - Tutte le rotte DEVONO implementare l'interfaccia `NavKey` e definire classi/oggetti Kotlin `@Serializable`.
  - Utilizzare `NavDisplay` combinato con `entryProvider` DSL.
  - Gestire lo stato di navigazione persistente (sopravvivenza a process death e rotazione) tramite `rememberNavigationState` (per architetture con bottom bar a multiple back stacks) o `rememberNavBackStack(startRoute)`.
  - È severamente vietato gestire la navigazione tramite variabili di stato booleane artigianali (`var isSettingsOpen by remember`) o selettori `when {}` monolitici.
- **Dependency Injection**:
  - Use Hilt (`@HiltViewModel`, `@Inject`, `@Module`, `@Binds`) with constructor injection.
- 📖 **Deep Dive & Code Blueprints**: Consultare i moduli ufficiali Google in `.agents/skills-main/` (in particolare `skills-main/navigation/navigation-3` per la navigazione e `skills-main/architecture` per i pattern di persistenza).

---

## 🎨 2. Design System: Material 3 Expressive
- **Theme & Motion**:
  - Initialize/wrap UI with `MaterialExpressiveTheme` using `MotionScheme.expressive()`.
  - Snappy Spring-based interactive micro-interactions on click/toggle (`spring(dampingRatio = 0.8f, stiffness = 380f)`).
  - Spatial transitions: snappy 250ms–300ms; Effect transitions: 150ms–200ms. Never allow sluggish slow animations.
- **App Icon Standard (Material Symbols Filled)**:
  - Every application MUST have an official adaptive icon (`ic_launcher` and `ic_launcher_round`) generated from a **Material Symbol with Fill active** (`FILL = 1`, Rounded style from Google Fonts Icons).
  - Composed of `ic_launcher_background.xml` (semantic M3 tone e.g. `primaryContainer`) and `ic_launcher_foreground.xml` (filled vector glyph in `onPrimaryContainer` / `primary`).
- **Dynamic Semantic Colors (Zero Hardcoded Colors)**:
  - **NEVER hardcode colors** (`Color(0xFF...)` is strictly forbidden in UI components).
  - Colors must strictly come from `MaterialTheme.colorScheme.*`.
  - Always pair container colors with their respective `on<Role>` content colors (`onPrimaryContainer` on `primaryContainer`, `onSurface` on `surface`, etc.).
  - Use tonal surfaces (`surfaceContainerLowest` through `surfaceContainerHighest`) instead of artificial elevation shadows.
  - Dividers and subtle borders: `outlineVariant`.
- **Scaffold Multi-Livello & Invarianza BottomBar/TopBar (Invariante Tassativa)**:
  - Lo `Scaffold` root di livello applicazione ospita **UNICAMENTE** la `NavigationBar` inferiore e lo `SnackbarHost`.
  - È severamente vietato inserire una `TopAppBar` o condizionali `when`/`if` nello `Scaffold` root per mostrare/nascondere la navigation bar: questo causa bug di barre che fluttuano o scompaiono.
  - Ciascuna schermata/sottoschermata (Rooms, Devices, Scenarios, Settings, RoomDetail) gestisce il proprio `Scaffold` dedicato contenente la rispettiva `TopAppBar` o `LargeTopAppBar`.
  - **WindowInsets & Edge-to-Edge**: `enableEdgeToEdge()` deve essere sempre attivo in `MainActivity`. Lo Scaffold root passa `contentWindowInsets = WindowInsets(0.dp)` se la gestione degli insets è delegata ai figli, oppure passa l'innerPadding a `NavDisplay`. Ciascun composable consuma i `WindowInsets` correttamente senza doppie spaziature.
- **Expressive Components & Opt-in**:
  - Annotate with `@OptIn(ExperimentalMaterial3ExpressiveApi::class)`.
  - **Terna dei 3 FAB e Regola del Default Medio (Invariante Tassativa)**:
    - Esistono **3 varianti di FAB**:
      1. **Standard FAB (56×56 dp)**: forma `RoundedCornerShape(16.dp)`, icona 24dp. Per azioni ordinarie o spazi compatti.
      2. **Medium FAB (68×68 dp)**: forma `RoundedCornerShape(20.dp)`, icona 30dp. **È IL DEFAULT OBBLIGATORIO DA USARE SEMPRE**.
      3. **Large FAB (96×96 dp)**: forma `RoundedCornerShape(28.dp)`, icona 36dp. Da usare **SE E SOLO SE l'azione è proprio cruciale / cardine assoluto della schermata**.
    - **DIVIETO ASSOLUTO DI TESTO (SENZA MAI TESTO NEL FAB)**: I FAB non devono MAI contenere testo (è severamente vietato l'uso di Extended FAB con label di testo). Ogni FAB deve contenere **ESCLUSIVAMENTE l'icona** centrata.
    - **Anti-Capsula / Squircle**: Non usare MAI forme a capsula/pillola (`CircleShape` o raggio 50%). Usare sempre e solo la forma squircle M3 proporzionale.
- **Typography & Font Standard (Dual Archetypes: Classica vs D'Impatto)**:
  - The standard font family is **Google Sans Flex** ([Google Fonts Specimen](https://fonts.google.com/specimen/Google+Sans+Flex)), bundled locally as a variable font in `res/font/google_sans_flex.ttf` for offline-first zero-latency rendering across all 15 M3 roles.
  - Supports two design archetypes agreed during Discovery Interview:
    1. **Stile Classico**: 15 standard M3 roles on `GoogleSansFlexFontFamily` (ideal for enterprise/utility form-heavy apps).
    2. **Stile d'Impatto**: 6-axis parametric typography (`wght`, `wdth`, `ROND`, `GRAD`), Bento Hero Cards (48sp `FlexHeroNumericFont`), Quick Metrics Strips, contextual greetings, and pill tags (ideal for consumer/wellness/lifestyle apps). Full specs in `.agents/skills/spark-jetpack/SKILL.md`.
- **Mandatory Circular Back Button**:
  - In detail and sub-screens, the back button MUST ALWAYS be housed in a circular container: `Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh, modifier = Modifier.size(40.dp))` with `Icons.AutoMirrored.Rounded.ArrowBack`.
- **Collapsing LargeTopAppBar with Tonal Elevation & Scrolling Color Transition**:
  - App bars should initially be the same color as the background (`containerColor = surface`), then fill with a contrasting color on scroll (`scrolledContainerColor = surfaceContainer` or `surfaceContainerHigh`) to provide visual separation from the background.
  - The app bar can remain on a page at all times (`pinnedScrollBehavior`), or can hide and reappear when scrolling (`enterAlwaysScrollBehavior` or `exitUntilCollapsedScrollBehavior`).
  - In sub-screens, use `LargeTopAppBar` with `TopAppBarDefaults.exitUntilCollapsedScrollBehavior()`. At top: `containerColor = surface`; when scrolled: `scrolledContainerColor = surfaceContainer` (tonally lighter in dark mode to clearly indicate elevation over text).
- **Grouped List Standard (Unified Card Blocks)**:
  - Related list items must be grouped inside unified container cards (`shape = RoundedCornerShape(16.dp)`), separated by `HorizontalDivider(color = outlineVariant)`, with left icon badges in squircles (~40dp).
- **Search-Centric Top Bar Pill & Alternate Colors**:
  - Primary screens where search is fundamental must feature a pill search bar with an hamburger menu icon on the left and a circular user profile avatar on the right.
  - **Alternate Color Default**: By default on dark/tonal backgrounds, the search container must use `surfaceBright` (instead of `surfaceContainer`) to ensure immediate contrast against surrounding elements, maintaining a contrast ratio of at least **3:1** for text and icons (`onSurface` / `onSurfaceVariant`).
  - **Search Bar Action Buttons**: In addition to the trailing avatar, search bars may support up to two trailing action icons on mobile (placed inside or outside the search bar, ordered from most used on left to least used on right).
  - **Mandatory SearchView on Selection (SEMPRE)**: Selecting/tapping the search bar **MUST ALWAYS** open the full-screen `SearchView` component (`AuraSearchView`) with back navigation arrow (`ArrowBack`), focused input field, clear button, divider, recent search history items (`History` icon), and live query filtering with system `BackHandler` integration.
- **Mandatory Default Heights for App Bars**:
  - **NEVER compress or shorten an app bar**: Every app bar must strictly use the standard Material 3 default height (64.dp for `TopAppBar`, 72.dp for `SearchTopBar` with a 56.dp inner container pill, 152.dp for expanded `LargeTopAppBar`). App bars must always span the full width of the window (`fillMaxWidth()`).
- **Mandatory Settings Screen & Spark Section (Invariante Tassativa)**:
  - Every application MUST provide a dedicated Settings screen accessible from the top-right profile avatar or settings button in the `TopAppBar`.
  - **Spark Section Invariant (SEMPRE)**: In qualsiasi schermata Impostazioni creata o modificata, la sezione finale DEVE SEMPRE essere dedicata a Spark ed intitolata nella lingua del device (es. "Realizzata con Spark!" / "Built with Spark!"). La card raggruppata deve contenere tre voci interattive con apertura URL esterno via `Intent.ACTION_VIEW`:
    1. **GitHub del progetto**: link a `https://github.com/leonardo-mais/Spark`.
    2. **Andrea Sorge**: sottotitolo "Architettura e build", link cliccabile a `https://github.com/Sorgandri03`.
    3. **leonardo.mais**: sottotitolo "UI e UX", link cliccabile a `https://github.com/leonardo-mais`.
    Questo requisito è permanente e non derogabile.
- **Strict Prohibitions**:
  - Do NOT import from `androidx.compose.material.*` (Material 2).
  - Do NOT use plain, flat Material 3 designs with default sharp corners or rigid linear animations.
  - Do NOT make an app bar shorter than its default height.
  - Do NOT use custom Canvas / Bezier wave separators or draw manual decorative waves between sections. Use semantic M3 tonal containers (`surfaceContainerLow`, `surfaceContainer`) and `HorizontalDivider(color = outlineVariant)`.
  - Do NOT use single monolithic Scaffolds with conditional top/bottom bars.
- 📖 **Component Signatures & Reference Specs**: Consult skill `@.agents/skills/spark-jetpack` before writing UI components to verify official signatures and anti-hallucination guidelines. **Priority Hierarchy**: `skills-main > spark-jetpack > development-standards.md`.

---

## ⚡ 3. Compose Performance & Recomposition Hygiene
- **UiState Stability**:
  - Annotate UI state data classes with `@Immutable` or `@Stable` (ensuring all properties are immutable primitives, data classes, or persistent collections) to enable Compose compiler smart recomposition skipping.
- **Lazy Collections (`LazyColumn` / `LazyRow`)**:
  - ALWAYS provide a stable key: `items(items, key = { it.id })`.
  - Specify `contentType = { ... }` in heterogeneous lists to enable view recycling.
- **Composition Phase Optimization**:
  - Use `derivedStateOf` when computing state derived from frequently mutating observables (e.g., scroll offset, live filtering, button enabled criteria).
  - Defer state reads using lambda modifiers (e.g., `Modifier.offset { ... }`, `Modifier.graphicsLayer { ... }`) to bypass composition and layout phases during animations.

---

## 🛡️ 4. State Restoration & UI Resilience
- **Process Death & Configuration Changes**:
  - Ephemeral user input (partially entered form text, search query drafts, active filters) MUST survive background process termination and screen rotation.
  - Use `rememberSaveable` for UI-local state or `SavedStateHandle` inside the `ViewModel`.
- **The 4 Fundamental UI States**:
  - Every screen state machine MUST explicitly handle all 4 states:
    1. **Loading**: Render `WavyLinearProgressIndicator` or `WavyCircularProgressIndicator`.
    2. **Empty**: Engaging illustration or message with a clear call-to-action (CTA).
    3. **Content**: Standard data display.
    4. **Error**: User-friendly explanation with a retry mechanism (`Button` or `SnackbarAction`).
  - Never allow blank screens, unhandled exceptions, or silent failures.

---

## 🔒 5. Privacy, Contextual Permissions & Modern Android APIs
- **Contextual Permission Requests**:
  - NEVER request permissions on cold start or app launch.
  - Request runtime permissions exclusively in response to an explicit user action, accompanied by a clear educational rationale dialog when needed.
  - Handle rejection gracefully: provide fallback functionality or guide to settings without entering infinite prompt loops.
- **Android 13/14+ Modern Capabilities**:
  - **Notifications**: Check and request `POST_NOTIFICATIONS` before dispatching alerts.
  - **Photo & Media Picker**: Strictly use the modern Android Photo Picker (`ActivityResultContracts.PickVisualMedia`) instead of requesting broad `READ_EXTERNAL_STORAGE` or `READ_MEDIA_*` permissions.

---

## ♿ 6. Accessibility (A11y) & Inclusive Usability
- **Minimum Touch Target**:
  - Every interactive element (buttons, icon buttons, switches, chips) MUST have an effective touch target of at least **48×48 dp** (`Modifier.minimumInteractiveComponentSize()`).
- **Semantic Content Descriptions**:
  - Informative and interactive icons/images MUST provide a localized, meaningful `contentDescription = stringResource(...)`.
  - Purely decorative icons accompanying visible text MUST explicitly declare `contentDescription = null` to avoid polluting TalkBack screen reader flows.
- **Dynamic Font Scaling**:
  - ALWAYS use `sp` units for font sizing (never `dp` for text).
  - Ensure layouts adapt fluidly and do not clip or truncate text when system font scale is set to 130%–200%.

---

## 🌐 7. Strict Localization & System Formatting (i18n)
- **Zero Hardcoded Strings**:
  - Hardcoded strings in composables (`Text("Save")`) are strictly forbidden. All UI copy MUST use `stringResource(R.string.<id>)`.
  - Provide a dual-language strategy: default English in `res/values/strings.xml` and Italian in `res/values-it/strings.xml`, switching automatically based on system locale.
- **System-Aware Formatters**:
  - Never concatenate or manually format dates, times, currencies, or numbers with static punctuation.
  - Use system formatters (`DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)`, `NumberFormat.getCurrencyInstance()`, etc.) to honor device locale conventions.

---

## 📦 8. Build & Delivery Invariants
- **Universal Root `/apk` Delivery**:
  - ALL APK compilation outputs (debug, release, local scripts, or CI/CD) **MUST** be placed directly into `<projectRoot>/apk/`.
  - Naming convention: `apk/<AppName>-v<versionName>-b<versionCode>-<buildType>.apk`
  - Print SHA256 checksum, size, and date upon generation.
- **Local Automation & Continuous Release**:
  - Maintain executable build automation scripts in `build/` (e.g., `build/build.sh`).
  - Build automation supports both debug and release pipelines: `./build/build.sh` (debug) and `./build/build.sh --release` or `./release-agent [patch|minor|major] -m "..."`.
  - The AI Agent executes `./release-agent` directly whenever version bumps, Obtainium distribution feeds, or releases are requested, ensuring continuous integration and deployment without requiring manual terminal operations by the user.
