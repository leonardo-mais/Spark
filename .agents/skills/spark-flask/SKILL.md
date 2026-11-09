---
name: spark-flask
description: Guidelines and architectural rules for building web applications with Flask, Jinja2, HTMX, and Material Web Components, modeled directly after Google web apps (Google Calendar, Google Tasks) and grounded in official Android architectural and adaptive skills.
---

# Spark! Flask Web Application Architecture & Design Skill (`spark-flask`)

This document defines the architectural, UI, and UX standards for building responsive, accessible, and high-performance web applications using **Flask**, **Jinja2**, **HTMX**, and **Material Web (@material/web)**. The foundations are derived directly from official Android system skills (`adaptive`, `navigation-3`, `edge-to-edge`, `testing-setup`, and `styles`) and translated into modern web standards following the design language of Google's flagship productivity apps (Google Calendar and Google Tasks).

---

## 1. Activation Guard & Pre-Planning Interview Rule

1. **Explicit Request Only**:
   The AI agent **MUST NOT** generate a Flask webapp or scaffold a web project spontaneously. Web app creation is triggered **EXCLUSIVELY** upon explicit user request.
2. **Mandatory Discovery Interview (`planning-flask-interview.md`)**:
   Before generating or modifying any code for a Flask application, the agent **MUST ALWAYS** conduct the structured discovery interview using the `ask_question` tool as defined in:
   - [.agents/rules/planning-flask-interview.md](file:///Users/leona/Documents/Spark!%20Apps/Spark!/.agents/rules/planning-flask-interview.md)
   Key questions include:
   - Core functional scope and features.
   - Authentication strategy (Tailscale single-user vs. session auth).
   - Domain data entities and persistence lifecycle.
   - Screen hierarchy and adaptive multi-pane layout (List-Detail vs. Supporting Pane).
   - Visual typography archetype: **Archetype A (Spark Impact Flex)** vs. **Archetype B (Google Standard M3)**.
   - **Docker container external port** (the mandatory final question before implementation).

---

## 2. Core Architecture: Android-to-Flask Mapping

The web application adheres to Android Clean Architecture and Unidirectional Data Flow (UDF), mapping Android principles into Python and web primitives:

| Android Architectural Concept (from Android Skills) | Spark-Flask Web Equivalent | Responsibility |
|---|---|---|
| **Room Entities & SQLite Database** | SQLAlchemy ORM Models + SQLite (Docker volume) | Schema definition, foreign keys, local single-source-of-truth persistence. |
| **Room DAOs & Repository Pattern** | Repository classes (`repositories/`) | Encapsulates SQL queries; returns domain objects; abstracts storage. |
| **Domain Use Cases / ViewModels** | Service Layer (`services/`) | Business logic, transactional boundaries, validation, view-state coordination. |
| **Navigation 3 / Deep Links** | Flask Blueprints + Semantic REST Routes | Explicit URL routing, deep-linkable views, browser history push. |
| **Jetpack Compose UI & UDF** | Jinja2 Templates + Partials | Declarative rendering of UI states from pure server-provided view context. |
| **StateFlow / Recomposition** | HTMX Attributes (`hx-get`, `hx-post`, `hx-target`, `hx-swap`) | Targeted DOM swaps without full-page reloads, preserving scroll and client state. |
| **Window Size Classes (`adaptive`)** | CSS Container & Media Queries | Compact (<600px), Medium (600–839px), Expanded (840–1199px), Large (≥1200px). |
| **NavigationSuiteScaffold (`adaptive`)** | Responsive App Shell (`shell.html`) | Bottom Navigation Bar on Compact; Nav Rail on Medium; Nav Drawer on Expanded/Large. |
| **Multi-Pane Scenes (List-Detail & Supporting)** | Split-Pane Layouts with HTMX target panes | Two-column responsive panes; detail pane replaces back arrow when displayed side-by-side. |
| **Edge-to-Edge (`edge-to-edge`)** | Viewport Fit Cover + Safe Area Insets | `viewport-fit=cover`, `env(safe-area-inset-top)`, `env(safe-area-inset-bottom)`. |
| **Testing Setup (`testing-setup`)** | `pytest` + Flask Test Client + SQLite In-Memory Fixtures | Unit tests for repositories/services, integration tests for routes and HTMX partials. |

### Directory Structure Pattern
```
<flask-app-root>/
├── app/
│   ├── __init__.py              # Application factory: create_app()
│   ├── config.py                # Environment and configuration settings
│   ├── extensions.py            # SQLAlchemy, Migrate, CSRF extensions
│   ├── models/                  # SQLAlchemy ORM models (Room Entity equivalent)
│   ├── repositories/            # Data access queries (Room DAO equivalent)
│   ├── services/                # Business logic and transactions (ViewModel equivalent)
│   ├── routes/                  # Flask Blueprints (Navigation entries)
│   │   ├── main.py
│   │   ├── items.py
│   │   └── settings.py
│   ├── templates/               # Jinja2 layouts and components
│   │   ├── base.html            # HTML5 shell, head, assets, safe insets
│   │   ├── shell.html           # 5-zone adaptive shell: App Frame, Top Bar, Drawer, Main Canvas, Side Rail
│   │   ├── components/          # Reusable partials (buttons, cards, search, dialogs)
│   │   └── pages/               # Screen templates and HTMX partial fragments
│   └── static/
│       ├── css/
│       │   ├── tokens.css       # Material 3 design tokens (surfaces, shapes, elevation, state layer)
│       │   ├── typography.css   # Archetype A or B typography definitions
│       │   ├── layout.css       # Adaptive shell, 28dp Main Canvas, safe area insets
│       │   └── components.css   # Custom M3 drawer, segmented buttons, checkboxes (2dp), cards
│       ├── js/
│       │   ├── htmx.min.js      # Vendored HTMX
│       │   ├── material-web.js  # Vendored @material/web custom elements bundle
│       │   ├── shortcuts.js     # Google shortcuts engine ('c', '/', '?', Esc)
│       │   └── app.js           # Client theme switcher & visual viewport adapter
│       └── fonts/               # Self-hosted Google Sans Flex variable font
├── migrations/                  # Alembic database migrations
├── tests/                       # Unit and integration test suite
│   ├── conftest.py              # Test client, database fixture
│   ├── test_services.py
│   └── test_routes.py
├── Dockerfile                   # Production container with gunicorn & non-root user
├── docker-compose.yml           # Service orchestration with ${SPARK_FLASK_PORT} and volume
├── pyproject.toml / requirements.txt
└── run.py                       # Local development entrypoint
```

---

## 3. Material Design 3 (M3) Desktop/Web Architectural Standards

A desktop/web interface under Material Design 3 structures its adaptive layout across **surface containers (Surface)**, **navigation bars**, and **interactive components** governed by semantic color, typography, and shape tokens.

In M3, visual hierarchy is no longer established through heavy drop shadows (as in M2), but through **tonal differentiation between surface containers**.

### 3.1 Base Structure & Surfaces (Layout & Surfaces)
- **App Background / Frame (`body.m3-app-scaffold`)**: Uses `Surface Container Low` (or `Surface Dim` in Dark Mode) as the universal foundation on which all primary elements rest.
- **Main Content Canvas (`.m3-main-canvas`)**: The central operational panel (e.g. calendar grid, task workspace) is enclosed in an elevated container with rounded corners (**Shape: Extra Large, 28dp / 28px**) and uses `Surface` or `Surface Container Lowest` to emerge tonally from the outer frame.
- **Dividers (`.m3-divider`)**: Grid lines and section separators use `Outline Variant` to provide subtle, low-contrast separation without cognitive clutter.

### 3.2 Top App Bar (`.m3-top-app-bar`)
The top bar manages global context, temporal/spatial navigation, and system-level actions:
- **Background & Text**: Blends seamlessly with the outer frame (`Surface Container Low`), with primary text and icons in `On Surface` and secondary icons in `On Surface Variant`.
- **Leading Navigation Icon**: Standard Icon Button (`On Surface Variant`) with hamburger menu to expand or collapse the navigation drawer.
- **Contextual Controls**:
  - **Outlined Button**: Pill-shaped button (**Shape: Full**) such as the "Today" button, with `Outline` border and `On Surface` text.
  - **Headline / Title**: High-emphasis text (`Title Large` or `Headline Small`) in `On Surface` indicating the current view or date range.
- **Trailing Actions**:
  - **Icon Buttons**: Search, support, and settings icons in `On Surface Variant`.
  - **Segmented Button**: Two-option or multi-option view switcher (e.g., Calendar vs Tasks). Uses **Shape: Full**, border `Outline`, active segment in `Secondary Container` (with icon/label in `On Secondary Container`) and inactive segments with transparent background.
  - **User Avatar**: Circular element on the far right.

### 3.3 Left Navigation Drawer (`.m3-nav-drawer`)
On expanded screens (Expanded Window Size Class), primary navigation resides in a persistently visible Standard Navigation Drawer:
- **Extended FAB**: Pinned in the top-left for the application's primary action (e.g. "+ Create"). Styled with **Shape: Large (16dp)** or **Full**, background `Surface Container High` (or `Primary Container`), and text/icon in `On Surface` (or `On Primary Container`).
- **Date Picker / Mini Widget**: Compact calendar with text in `On Surface`. The current or selected day is highlighted by a filled circle (**Shape: Full**) in `Primary` with text in `On Primary`.
- **Search Bar / Text Field**: Compact search field ("Search for people / items") with background `Surface Container High`, **Shape: Small (8dp)** or Full, and placeholder/icon in `On Surface Variant`.
- **Lists & Filters (List Items & Checkboxes)**:
  - Collapsible accordion section headers in `Title Small` with `On Surface Variant`.
  - **Checkboxes**: Boxes with gently rounded corners (**Shape: Extra Small, 2dp**). When checked, filled with `Primary` (or category semantic container) with checkmark in `On Primary`.

### 3.4 Main Content Area (Main Content Canvas)
The primary workspace hosting data records and active workflows:
- **Contextual Header**: Displays column or view parameters (e.g. days of the week). The active item receives a circular badge in `Primary` with text in `On Primary`, while others remain in `On Surface`.
- **Cards / Event Containers**: Content blocks inside the canvas use **Shape: Small or Medium (8dp–12dp)**. They follow tonal containers (`Primary Container`, `Secondary Container`, `Tertiary Container`, or custom semantic tokens) with text in `On [Color] Container` to guarantee WCAG AA accessibility contrast.
- **Status & Time Indicators**: High-visibility markers (such as the current-time horizontal indicator) use `Error` or `Primary` to draw immediate visual attention.

### 3.5 Right Side Utility Rail (`.m3-side-utility-rail`)
A compact vertical rail (Compact Rail, 56px wide) dedicated to secondary integrated extensions (e.g. Keep, Tasks, Contacts):
- **Background**: Shares the same tonal level as the outer frame (`Surface Container Low`).
- **Icon Buttons**: Arranged vertically, with hover and focus states governed by an M3 **State Layer** (semi-transparent overlay of `On Surface` at 8%–12% opacity).

---

## 4. Master M3 Token Summary Table

| Component M3 | Color Token (Background) | Color Token (Text / Icon) | Shape Token |
|---|---|---|---|
| **App Background / Frame** | `Surface Container Low` (`Surface Dim` in Dark) | `On Surface Variant` | `None (0dp)` |
| **Main Content Canvas** | `Surface / Surface Container Lowest` | `On Surface` | `Extra Large (28dp)` |
| **Extended FAB** | `Primary Container / Surface Container High` | `On Primary Container / On Surface` | `Large (16dp)` o `Full` |
| **Outlined Button (Today)** | `Transparent (Border: Outline)` | `On Surface / Primary` | `Full (Pill)` |
| **Segmented Button (Active)** | `Secondary Container` | `On Secondary Container` | `Full (Pill)` |
| **Search Field** | `Surface Container High` | `On Surface Variant` | `Small (8dp)` o `Full` |
| **Active / Selected Elements** | `Primary` | `On Primary` | `Full (Circle / Pill)` |
| **Content Cards (Blocks)** | `Primary / Secondary / Tertiary Container` | `On [Primary/Secondary/Tertiary] Container` | `Small (8dp)` o `Medium (12dp)` |
| **Dividers (Grid / Lines)** | `Outline Variant` | — | — |
| **Checkboxes (Selection)** | `Primary` (when checked) / `Outline` | `On Primary` | `Extra Small (2dp)` |
| **Side Utility Rail** | `Surface Container Low` | `On Surface Variant` | `State Layer (8%-12%)` |

---

## 5. Adaptive Layout & Breakpoints (Derived from `adaptive`)

Follow Material 3 Window Size Classes strictly:

- **Compact (< 600px)**: Phone portrait. Top bar simplified, bottom navigation bar with 3–5 destinations, single-pane flow (detail pushes full-screen with back button).
- **Medium (600px – 839px)**: Tablets portrait / Foldables unfolded. Navigation rail on leading edge, dual-pane layout when density permits.
- **Expanded (840px – 1199px)**: Tablet landscape / Small desktop. Modal or dismissible navigation drawer, full dual-pane (List-Detail side by side).
- **Large (≥ 1200px)**: Desktop / Large monitors. Permanent navigation drawer, 28dp Main Canvas, Side Utility Rail.

For complete CSS container queries and markup, consult:
- [.agents/skills/spark-flask/references/layout.md](file:///Users/leona/Documents/Spark!%20Apps/Spark!/.agents/skills/spark-flask/references/layout.md)
- [.agents/skills/spark-flask/references/components.md](file:///Users/leona/Documents/Spark!%20Apps/Spark!/.agents/skills/spark-flask/references/components.md)

---

## 6. The Four Mandatory UI States (Derived from `testing-setup`)

Every screen and HTMX fragment must gracefully handle the 4 canonical UI states:

1. **Loading State**:
   - HTMX indicator (`class="htmx-indicator"`) displaying a subtle linear progress bar at the top edge of the active pane or skeleton placeholder blocks.
2. **Empty State**:
   - Distinctive empty illustration/symbol (Material Symbol with `FILL 1`), clear headline (e.g., "No tasks yet"), helpful description, and an immediate primary CTA button to create the first record.
3. **Content State**:
   - Interactive data rows/cards, clear status cues, keyboard navigable list items, and reactive updates on click/tap.
4. **Error State**:
   - Non-blocking error banner or card with clear user-friendly explanation and a "Retry" or "Refresh" action via HTMX.

---

## 7. Cross-Cutting UX Standards

1. **Edge-to-Edge & Safe Areas (from `edge-to-edge`)**:
   - Set `<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">`.
   - Apply padding variables `env(safe-area-inset-top)`, `env(safe-area-inset-bottom)`, `env(safe-area-inset-left)`, `env(safe-area-inset-right)` to headers, footers, and floating buttons.
   - Respect visual viewport offsets for on-screen mobile keyboards.
2. **Keyboard Navigation & Google Shortcuts**:
   - Pressing `/`: Focuses the global search box.
   - Pressing `c`: Triggers the primary "Create" dialog or navigation.
   - Pressing `?`: Opens the keyboard shortcuts modal.
   - Pressing `Escape`: Closes active dialog, drawer, or search dropdown.
   - All interactive controls feature visible M3 focus rings adhering to WCAG AA contrast standards.
3. **Dark / Light Theme**:
   - Automatic adaptation via `@media (prefers-color-scheme: dark)` with an explicit user toggle stored in `localStorage` and synchronized on the `<html>` root (`data-theme="dark"` / `data-theme="light"`).
4. **PWA Manifest & Mobile Capability**:
   - Web App Manifest (`manifest.json`), theme color meta tag matching surface container tokens, and offline fallback banner.
5. **No Emoji Rule**:
   - Absolutely no emoji characters in icons, buttons, badges, or headers. Use **Material Symbols** exclusively.

---

## 8. Typography Archetypes

During the planning interview, the user selects between two distinct archetypes:
- **Archetype A — Spark Impact Flex**: Dynamic 6-axis Google Sans Flex typography, giant hero numerics, expressive card headings, and fluid layout scaling. Details: [references/typography-impact.md](file:///Users/leona/Documents/Spark!%20Apps/Spark!/.agents/skills/spark-flask/references/typography-impact.md).
- **Archetype B — Google Standard M3**: Rigorous 15-role Material 3 typography scale rendered with Google Sans Flex for sober, high-density utility interfaces. Details: [references/typography-standard.md](file:///Users/leona/Documents/Spark!%20Apps/Spark!/.agents/skills/spark-flask/references/typography-standard.md).

---

## 9. Verification & Quality Protocol

Every generated Flask project must pass:
1. `pytest` unit tests for models, repositories, and business services.
2. `pytest` integration tests verifying that Flask endpoints return status 200, valid HTML fragments, and proper HTMX headers.
3. Container build: `docker compose build`.
4. Run check: `docker compose up -d` responding on `http://localhost:${SPARK_FLASK_PORT}`.
