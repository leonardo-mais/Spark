# Adaptive Layout Reference for Spark-Flask

This document provides the responsive HTML structure, 5-zone surface architecture, and CSS container specifications for Spark-Flask applications, directly translating Material Design 3 (M3) Desktop/Web standards, Android Adaptive Layout guidelines (`adaptive/SKILL.md`), and Edge-to-Edge principles (`edge-to-edge/SKILL.md`) to the web.

---

## 1. Material 3 Window Size Classes & Breakpoints

Spark-Flask enforces the four standard Material 3 Window Size Classes:

| Size Class | Viewport Width | Typical Hardware Form Factor | Primary Navigation Pattern | Pane Strategy |
|---|---|---|---|---|
| **Compact** | `< 600px` | Phone portrait | Bottom Navigation Bar | Single-pane (stacked flows) |
| **Medium** | `600px – 839px` | Tablet portrait, Foldable unfolded | Navigation Rail (leading edge) | Dual-pane or flexible single-pane |
| **Expanded** | `840px – 1199px` | Tablet landscape, Small desktop | Dismissible / Modal Navigation Drawer | Dual-pane (List-Detail side by side) |
| **Large** | `≥ 1200px` | Desktop, Ultra-wide displays | Permanent Navigation Drawer + Side Rail | Tri-pane (Nav + 28dp Main Canvas + Utility Rail) |

---

## 2. The 5 M3 Surface & Layout Zones

Visual hierarchy is built on tonal distinction between surface containers rather than drop shadows:

```
+----------------------------------------------------------------------------------------------------+
|  ZONE 2: TOP APP BAR (Surface Container Low)                                                       |
|  [=] [Today]  November 2026                 [Search...]   [ (Calendar) | Tasks ]  (?)  (*)  (Avatar)|
+-------------------+---------------------------------------------------------+----------------------+
|                   |  ZONE 4: MAIN CONTENT CANVAS (28dp Corner)              |  ZONE 5:             |
|  ZONE 3:          |  (Surface / Surface Container Lowest)                   |  SIDE UTILITY RAIL   |
|  NAVIGATION       |  +---------------------------------------------------+  |  (Surface Container  |
|  DRAWER           |  | Header: Sun  Mon [10] Wed  Thu  Fri  Sat          |  |   Low)               |
|  (Surface         |  | - - - - - - - - - - - - - - - - - - - - - - - - - |  |                      |
|   Container Low)  |  | Time Indicator Line (Error / Primary)             |  |  [*] Keep            |
|                   |  | [Event Card: Primary Container] (8dp-12dp)        |  |  [v] Tasks           |
|  [+ Create]       |  | [Task Card: Secondary Container]                  |  |  [@] Contacts        |
|  (Mini DatePicker)|  |                                                   |  |                      |
|  [Search...]      |  |                                                   |  |  (State Layer:       |
|  [v] Category A   |  |                                                   |  |   8% hover,          |
|  [ ] Category B   |  +---------------------------------------------------+  |   12% focus)         |
|  (Checkboxes 2dp) |                                                         |                      |
+-------------------+---------------------------------------------------------+----------------------+
|  ZONE 1: APP BACKGROUND / FRAME (Surface Container Low / Surface Dim in Dark)                     |
+----------------------------------------------------------------------------------------------------+
```

---

## 3. Core Shell Scaffold Structure (`shell.html`)

```html
<!DOCTYPE html>
<html lang="en" data-theme="auto">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
  <title>{{ page_title | default("Spark App") }}</title>
  <link rel="stylesheet" href="{{ url_for('static', filename='css/tokens.css') }}">
  <link rel="stylesheet" href="{{ url_for('static', filename='css/typography.css') }}">
  <link rel="stylesheet" href="{{ url_for('static', filename='css/layout.css') }}">
  <link rel="stylesheet" href="{{ url_for('static', filename='css/components.css') }}">
  <script src="{{ url_for('static', filename='js/htmx.min.js') }}" defer></script>
  <script type="module" src="{{ url_for('static', filename='js/material-web.js') }}" defer></script>
  <script src="{{ url_for('static', filename='js/shortcuts.js') }}" defer></script>
</head>
<body class="m3-app-scaffold">

  <!-- ZONE 2: Top App Bar (Blends with Surface Container Low) -->
  <header class="m3-top-app-bar">
    <div class="top-bar-leading">
      <button class="m3-icon-button drawer-toggle" id="drawer-toggle" aria-label="Toggle navigation drawer">
        <span class="material-symbols-rounded">menu</span>
      </button>
      
      <a href="/tasks" class="app-branding">
        <span class="material-symbols-rounded app-logo">bolt</span>
        <span class="app-title">{{ config.APP_NAME }}</span>
      </a>

      <!-- Outlined Button "Today" (Shape: Full, Outline Border) -->
      <button class="m3-outlined-pill-button" hx-get="/tasks" hx-target="#workspace-container">
        Today
      </button>

      <!-- Headline / Title -->
      <h1 class="top-bar-title m3-title-large">{{ active_heading | default("Overview") }}</h1>
    </div>

    <!-- Centered Search Field (Surface Container High, Small 8dp or Full) -->
    <div class="top-bar-search">
      <div class="search-pill">
        <span class="material-symbols-rounded search-icon">search</span>
        <input type="search"
               id="global-search-input"
               name="q"
               placeholder="Search... (Press / to focus)"
               hx-get="/tasks"
               hx-trigger="input changed delay:250ms, search"
               hx-target="#task-items-container"
               aria-label="Search">
        <kbd class="shortcut-badge">/</kbd>
      </div>
    </div>

    <!-- Trailing Actions -->
    <div class="top-bar-trailing">
      <!-- Segmented Button (View Toggle: Calendar vs Tasks) -->
      <div class="m3-segmented-button-group" role="group" aria-label="View switch">
        <button class="segmented-btn active" aria-pressed="true">
          <span class="material-symbols-rounded">view_agenda</span>
          <span>Tasks</span>
        </button>
        <button class="segmented-btn" aria-pressed="false">
          <span class="material-symbols-rounded">calendar_month</span>
          <span>Calendar</span>
        </button>
      </div>

      <!-- Icon Buttons in On Surface Variant -->
      <button class="m3-icon-button" id="theme-toggle" aria-label="Toggle Theme">
        <span class="material-symbols-rounded" id="theme-icon">dark_mode</span>
      </button>
      <button class="m3-icon-button" onclick="openShortcutsModal()" aria-label="Keyboard Shortcuts">
        <span class="material-symbols-rounded">keyboard</span>
      </button>
      <a href="/settings" class="m3-icon-button" aria-label="Settings">
        <span class="material-symbols-rounded">settings</span>
      </a>

      <!-- Circular User Avatar -->
      <div class="m3-avatar" title="User Profile">
        <span class="material-symbols-rounded">account_circle</span>
      </div>
    </div>
  </header>

  <!-- Responsive Layout Body -->
  <div class="m3-layout-body">
    
    <!-- ZONE 3: Left Navigation Drawer (Surface Container Low) -->
    <aside class="m3-nav-drawer" id="app-nav-drawer">
      <!-- Extended FAB: Shape Large (16dp) or Full -->
      <div class="drawer-header">
        <button class="m3-extended-fab" hx-get="/tasks/new" hx-target="#detail-pane" aria-keyshortcuts="c">
          <span class="material-symbols-rounded">add</span>
          <span class="fab-label">Create</span>
          <kbd class="shortcut-tag">c</kbd>
        </button>
      </div>

      <!-- Date Picker / Mini Widget with Primary circle on active day -->
      <div class="m3-mini-calendar-widget">
        <div class="mini-cal-header">
          <span class="m3-title-small">November 2026</span>
        </div>
        <div class="mini-cal-grid">
          <span class="cal-day-header">M</span><span class="cal-day-header">T</span><span class="cal-day-header">W</span>
          <span class="cal-day-header">T</span><span class="cal-day-header">F</span><span class="cal-day-header">S</span><span class="cal-day-header">S</span>
          <span class="cal-cell">2</span><span class="cal-cell">3</span><span class="cal-cell active-day">4</span>
          <span class="cal-cell">5</span><span class="cal-cell">6</span><span class="cal-cell">7</span><span class="cal-cell">8</span>
        </div>
      </div>

      <!-- Search Field in Drawer: Surface Container High, Shape Small (8dp) -->
      <div class="drawer-search-container">
        <div class="drawer-search-field">
          <span class="material-symbols-rounded">search</span>
          <input type="text" placeholder="Search people or lists">
        </div>
      </div>

      <!-- Accordion Lists with Checkboxes (2dp corner radius) -->
      <div class="drawer-accordion">
        <details open class="m3-accordion-section">
          <summary class="accordion-header m3-title-small">
            <span>My Lists</span>
            <span class="material-symbols-rounded arrow-icon">expand_more</span>
          </summary>
          <div class="accordion-body">
            <label class="m3-checkbox-label">
              <input type="checkbox" checked class="m3-custom-checkbox">
              <span>Personal</span>
            </label>
            <label class="m3-checkbox-label">
              <input type="checkbox" checked class="m3-custom-checkbox">
              <span>Work & Projects</span>
            </label>
          </div>
        </details>
      </div>
    </aside>

    <!-- ZONE 4: Main Content Canvas (28dp extra-large corners, Surface/Container Lowest) -->
    <main class="m3-main-canvas" id="workspace-container">
      {% block content %}{% endblock %}
    </main>

    <!-- ZONE 5: Side Utility Rail (Surface Container Low, 56px, 8-12% state layer) -->
    <aside class="m3-side-utility-rail" id="side-utility-rail">
      <div class="utility-rail-items">
        <button class="m3-utility-button" title="Keep Notes" aria-label="Keep Notes">
          <span class="material-symbols-rounded">lightbulb</span>
        </button>
        <button class="m3-utility-button active" title="Tasks" aria-label="Tasks">
          <span class="material-symbols-rounded">check_circle</span>
        </button>
        <button class="m3-utility-button" title="Contacts" aria-label="Contacts">
          <span class="material-symbols-rounded">contacts</span>
        </button>
      </div>
    </aside>

  </div>

  <!-- Bottom Navigation Bar (Compact screens: < 600px) -->
  <nav class="m3-bottom-nav-bar" id="app-bottom-nav">
    {% for item in nav_destinations %}
    <a href="{{ item.url }}" class="bottom-nav-item {% if item.active %}active{% endif %}">
      <span class="material-symbols-rounded">{{ item.icon }}</span>
      <span class="bottom-label">{{ item.label }}</span>
    </a>
    {% endfor %}
  </nav>

  <!-- Global Snackbar Host -->
  <div id="m3-snackbar-host" class="m3-snackbar-host" aria-live="polite"></div>

</body>
</html>
```

---

## 4. CSS Implementation for 5-Zone Architecture (`layout.css`)

```css
/* ========================================================
   ZONE 1: APP BACKGROUND / FRAME
   ======================================================== */
body.m3-app-scaffold {
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  min-height: 100dvh;
  background-color: var(--md-sys-color-surface-container-low);
  color: var(--md-sys-color-on-surface);
  overflow: hidden;
}

[data-theme="dark"] body.m3-app-scaffold {
  background-color: var(--md-sys-color-surface-dim);
}

/* ========================================================
   ZONE 2: TOP APP BAR
   ======================================================== */
.m3-top-app-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: var(--safe-top) 16px 0 16px;
  background-color: var(--md-sys-color-surface-container-low);
  border-bottom: 1px solid var(--md-sys-color-outline-variant);
  z-index: 20;
}

/* ========================================================
   ZONE 3: NAVIGATION DRAWER (LEFT)
   ======================================================== */
.m3-nav-drawer {
  width: 280px;
  background-color: var(--md-sys-color-surface-container-low);
  border-right: 1px solid var(--md-sys-color-outline-variant);
  display: flex;
  flex-direction: column;
  padding: 16px 12px;
  gap: 16px;
  overflow-y: auto;
}

/* ========================================================
   ZONE 4: MAIN CONTENT CANVAS (28dp BORDER RADIUS)
   ======================================================== */
.m3-main-canvas {
  flex: 1;
  display: flex;
  margin: 8px 8px 8px 0;
  background-color: var(--md-sys-color-surface-container-lowest);
  border-radius: var(--md-sys-shape-corner-extra-large); /* 28dp */
  border: none;
  overflow: hidden;
  position: relative;
}

/* ========================================================
   ZONE 5: SIDE UTILITY RAIL (RIGHT)
   ======================================================== */
.m3-side-utility-rail {
  width: 56px;
  background-color: var(--md-sys-color-surface-container-low);
  border-left: 1px solid var(--md-sys-color-outline-variant);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 0;
  z-index: 10;
}

.utility-rail-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
}

.m3-utility-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: var(--md-sys-shape-corner-full);
  background: transparent;
  border: none;
  color: var(--md-sys-color-on-surface-variant);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

/* M3 State Layer: 8% on hover, 12% on focus */
.m3-utility-button:hover {
  background-color: rgba(var(--md-sys-color-on-surface-rgb), 0.08);
}

.m3-utility-button:focus-visible {
  background-color: rgba(var(--md-sys-color-on-surface-rgb), 0.12);
  outline: 2px solid var(--md-sys-color-primary);
}

.m3-utility-button.active {
  background-color: var(--md-sys-color-secondary-container);
  color: var(--md-sys-color-on-secondary-container);
}
```
