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
│   │   ├── shell.html           # Adaptive top bar, nav drawer/rail/bar, main container
│   │   ├── components/          # Reusable partials (buttons, cards, search, dialogs)
│   │   └── pages/               # Screen templates and HTMX partial fragments
│   └── static/
│       ├── css/
│       │   ├── tokens.css       # Material 3 design tokens (colors, shapes, elevation)
│       │   ├── typography.css   # Archetype A or B typography definitions
│       │   ├── layout.css       # Adaptive shell, multi-pane rules, safe area insets
│       │   └── components.css   # Custom M3 drawer, rail, and custom controls
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

## 3. UI Patterns Modeled After Google Calendar & Google Tasks

Every Spark-Flask application must present the recognizable polish of Google's flagship web interfaces:

1. **Top App Bar with Integrated Search**:
   - Elevated or flat surface with hamburger toggle for the navigation drawer, app logo/title, centered pill-shaped search input with shortcut hint (`/`), and an action menu or user settings avatar.
2. **Floating "Create" Action**:
   - Google-style extended floating action button (FAB) or high-visibility pill button (`+ Create` or `+ Add item`) pinned in the primary drawer/rail or bottom corner, accessible via shortcut `c`.
3. **Adaptive Left Navigation**:
   - Collapsible navigation drawer on large screens (expanded view).
   - Compact navigation rail on medium screens (tablets/half-screen desktop).
   - Bottom navigation bar on mobile (compact view).
4. **Multi-Pane Content Area**:
   - **List-Detail** (Tasks pattern): master list in left pane, detail editor/viewer in right pane.
   - **Supporting Pane** (Calendar pattern): primary calendar/workspace grid with collapsible auxiliary panel (agenda, side panel utilities).
5. **No Full-Page Flashes**:
   - All state transitions, item selections, completions, filters, and edits swap cleanly into their target containers via HTMX (`hx-target`, `hx-swap="outerHTML"` or `innerHTML`).
6. **Toast & Snackbar Host**:
   - Bottom-left or bottom-center transient snackbar notifications for undo operations (e.g., "Item deleted — [Undo]"), auto-dismissing after 4 seconds.

---

## 4. Adaptive Layout & Breakpoints (Derived from `adaptive`)

Follow Material 3 Window Size Classes strictly:

- **Compact (< 600px)**: Phone portrait. Top bar simplified, bottom navigation bar with 3–5 destinations, single-pane flow (detail pushes full-screen with back button).
- **Medium (600px – 839px)**: Tablets portrait / Foldables unfolded. Navigation rail on leading edge, dual-pane layout when density permits.
- **Expanded (840px – 1199px)**: Tablet landscape / Small desktop. Modal or dismissible navigation drawer, full dual-pane (List-Detail side by side).
- **Large (≥ 1200px)**: Desktop / Large monitors. Permanent navigation drawer, multi-pane (List + Detail + Supporting Pane).

For complete CSS container queries and markup, consult:
- [.agents/skills/spark-flask/references/layout.md](file:///Users/leona/Documents/Spark!%20Apps/Spark!/.agents/skills/spark-flask/references/layout.md)

---

## 5. UI Components & Material 3 Web Mapping (Derived from `styles`)

Where `@material/web` elements are available, use them directly. Where components are not supplied by `@material/web` (such as navigation rails, drawers, or multi-pane scaffolds), implement them using M3 CSS Custom Properties:

- **Buttons**: `<md-filled-button>`, `<md-tonal-button>`, `<md-outlined-button>`, `<md-text-button>`, `<md-elevated-button>`.
- **Text Inputs**: `<md-filled-text-field>`, `<md-outlined-text-field>`.
- **Selection**: `<md-checkbox>`, `<md-radio>`, `<md-switch>`.
- **Chips**: `<md-chip-set>`, `<md-assist-chip>`, `<md-filter-chip>`, `<md-suggestion-chip>`.
- **Progress**: `<md-linear-progress>`, `<md-circular-progress>`.
- **Dialogs**: `<md-dialog>`.
- **Icons**: `<md-icon>` rendering **Material Symbols Rounded** with `font-variation-settings: 'FILL' 1`. **NEVER use emoji.**

For the full component registry and HTML snippets, see:
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

