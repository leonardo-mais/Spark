# Adaptive Layout Reference for Spark-Flask

This document provides the responsive HTML structure and CSS container query specifications for Spark-Flask applications, directly translating the Android Adaptive Layout guidelines (`adaptive/SKILL.md`) and Edge-to-Edge principles (`edge-to-edge/SKILL.md`) to the web.

---

## 1. Material 3 Window Size Classes & Breakpoints

Spark-Flask enforces the four standard Material 3 Window Size Classes:

| Size Class | Viewport Width | Typical Hardware Form Factor | Primary Navigation Pattern | Pane Strategy |
|---|---|---|---|---|
| **Compact** | `< 600px` | Phone portrait | Bottom Navigation Bar | Single-pane (stacked flows) |
| **Medium** | `600px – 839px` | Tablet portrait, Foldable unfolded | Navigation Rail (leading edge) | Dual-pane or flexible single-pane |
| **Expanded** | `840px – 1199px` | Tablet landscape, Small desktop | Dismissible / Modal Navigation Drawer | Dual-pane (List-Detail side by side) |
| **Large** | `≥ 1200px` | Desktop, Ultra-wide displays | Permanent Navigation Drawer | Tri-pane (Nav + List + Detail / Supporting) |

---

## 2. Core Shell Scaffold Structure (`shell.html`)

The master shell wraps all application screens and adapts its layout based on screen width.

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

  <!-- Top App Bar with Google Search pill -->
  <header class="m3-top-app-bar">
    <div class="top-bar-leading">
      <button class="m3-icon-button drawer-toggle" id="drawer-toggle" aria-label="Toggle navigation drawer">
        <span class="material-symbols-rounded">menu</span>
      </button>
      <div class="app-branding">
        <span class="material-symbols-rounded app-logo">spark</span>
        <span class="app-title">{{ app_name }}</span>
      </div>
    </div>

    <!-- Centered Search Pill (Google Calendar / Tasks style) -->
    <div class="top-bar-search">
      <div class="search-pill">
        <span class="material-symbols-rounded search-icon">search</span>
        <input type="text"
               id="global-search-input"
               placeholder="Search... (Press / to focus)"
               hx-get="/search"
               hx-trigger="keyup changed delay:300ms"
               hx-target="#search-results"
               aria-label="Search">
        <kbd class="shortcut-badge">/</kbd>
      </div>
      <div id="search-results" class="search-dropdown-container"></div>
    </div>

    <div class="top-bar-trailing">
      <button class="m3-icon-button" id="theme-toggle" aria-label="Toggle color theme">
        <span class="material-symbols-rounded">dark_mode</span>
      </button>
      <a href="/settings" class="m3-avatar-button" aria-label="Open Settings">
        <span class="material-symbols-rounded">settings</span>
      </a>
    </div>
  </header>

  <!-- Responsive Layout Container -->
  <div class="m3-layout-body">
    
    <!-- Navigation Drawer (Desktop / Large / Expanded) -->
    <aside class="m3-nav-drawer" id="app-nav-drawer">
      <div class="drawer-header">
        <!-- Floating Google-style Create Action -->
        <button class="m3-extended-fab" hx-get="/items/new" hx-target="#detail-pane" aria-keyshortcuts="c">
          <span class="material-symbols-rounded">add</span>
          <span class="fab-label">Create</span>
          <kbd class="fab-shortcut">c</kbd>
        </button>
      </div>
      <nav class="drawer-nav-items">
        {% for item in nav_destinations %}
        <a href="{{ item.url }}" class="nav-drawer-item {% if item.active %}active{% endif %}">
          <span class="material-symbols-rounded">{{ item.icon }}</span>
          <span class="item-label">{{ item.label }}</span>
          {% if item.badge %}
            <span class="m3-badge">{{ item.badge }}</span>
          {% endif %}
        </a>
        {% endfor %}
      </nav>
    </aside>

    <!-- Navigation Rail (Medium screens: 600px - 839px) -->
    <aside class="m3-nav-rail" id="app-nav-rail">
      <button class="m3-fab-compact" hx-get="/items/new" hx-target="#detail-pane" aria-label="Create item">
        <span class="material-symbols-rounded">add</span>
      </button>
      <nav class="rail-nav-items">
        {% for item in nav_destinations %}
        <a href="{{ item.url }}" class="nav-rail-item {% if item.active %}active{% endif %}" title="{{ item.label }}">
          <span class="material-symbols-rounded">{{ item.icon }}</span>
          <span class="rail-label">{{ item.label }}</span>
        </a>
        {% endfor %}
      </nav>
    </aside>

    <!-- Primary Workspace: Multi-Pane Container -->
    <main class="m3-workspace-container" id="workspace-container">
      {% block content %}{% endblock %}
    </main>
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

  <!-- Global Snackbar / Toast Container -->
  <div id="m3-snackbar-host" class="m3-snackbar-host" aria-live="polite"></div>

</body>
</html>
```

---

## 3. Multi-Pane Scene Layouts (Derived from `adaptive`)

### 3.1 List-Detail Pattern (Google Tasks Style)
In expanded view, both list and detail are visible side by side. Clicking an item updates `#detail-pane` via HTMX without changing the URL or reloading the page.

```html
<div class="m3-list-detail-scaffold">
  <!-- Master List Pane -->
  <section class="m3-list-pane" id="master-list-pane">
    <div class="pane-header">
      <h2 class="m3-title-medium">All Items</h2>
      <span class="item-count">{{ items|length }} items</span>
    </div>

    <!-- 4 UI States handled inside -->
    <div class="pane-scrollable-content" id="list-items-container">
      {% include 'pages/items/_list_items.html' %}
    </div>
  </section>

  <!-- Detail Pane -->
  <section class="m3-detail-pane" id="detail-pane">
    {% if selected_item %}
      {% include 'pages/items/_detail_view.html' %}
    {% else %}
      <div class="m3-pane-placeholder">
        <span class="material-symbols-rounded placeholder-icon">select_window</span>
        <p class="m3-body-large">Select an item from the list to view details</p>
      </div>
    {% endif %}
  </section>
</div>
```

> [!IMPORTANT]
> **No Back Button Rule on Multi-Pane**:
> As specified in `adaptive/SKILL.md`: when a detail pane is displayed side-by-side with the list on medium/expanded/large screens, the detail screen **MUST NOT** show a back arrow. The back arrow appears **ONLY** on compact screens when the detail occupies the entire viewport.

### 3.2 Supporting Pane Pattern (Google Calendar Style)
In Google Calendar, the main pane holds the primary view (monthly/weekly grid), while a collapsible supporting pane holds supplementary views (agenda, tasks, mini-calendar):

```html
<div class="m3-supporting-pane-scaffold">
  <!-- Primary Workspace Pane -->
  <div class="m3-primary-pane">
    {% block main_grid %}{% endblock %}
  </div>

  <!-- Auxiliary / Supporting Pane (Collapsible) -->
  <aside class="m3-supporting-pane" id="supporting-pane">
    <div class="supporting-header">
      <h3 class="m3-title-small">Upcoming & Overview</h3>
      <button class="m3-icon-button" onclick="toggleSupportingPane()" aria-label="Close panel">
        <span class="material-symbols-rounded">close</span>
      </button>
    </div>
    <div class="supporting-body">
      {% block supporting_content %}{% endblock %}
    </div>
  </aside>
</div>
```

---

## 4. CSS Responsive Rules & Safe Insets (`layout.css`)

```css
/* Viewport safe areas from edge-to-edge guidelines */
:root {
  --safe-top: env(safe-area-inset-top, 0px);
  --safe-bottom: env(safe-area-inset-bottom, 0px);
  --safe-left: env(safe-area-inset-left, 0px);
  --safe-right: env(safe-area-inset-right, 0px);
}

.m3-app-scaffold {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  min-height: 100dvh;
  margin: 0;
  padding: 0;
  background-color: var(--md-sys-color-surface);
  color: var(--md-sys-color-on-surface);
}

.m3-top-app-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: var(--safe-top) 16px 0 16px;
  background-color: var(--md-sys-color-surface-container);
  border-bottom: 1px solid var(--md-sys-color-outline-variant);
  z-index: 20;
}

.m3-layout-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* ========================================================
   ADAPTIVE BREAKPOINTS & NAVIGATION SUITE
   ======================================================== */

/* Compact screens (< 600px): Bottom Nav active, drawer/rail hidden */
@media (max-width: 599.98px) {
  .m3-nav-drawer,
  .m3-nav-rail {
    display: none !important;
  }
  .drawer-toggle {
    display: none;
  }
  .m3-bottom-nav-bar {
    display: flex;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: calc(64px + var(--safe-bottom));
    padding-bottom: var(--safe-bottom);
    background-color: var(--md-sys-color-surface-container);
    border-top: 1px solid var(--md-sys-color-outline-variant);
    z-index: 30;
  }
  .m3-workspace-container {
    padding-bottom: 72px;
  }
  .m3-list-detail-scaffold {
    display: flex;
    flex-direction: column;
  }
  .m3-detail-pane.has-selection {
    position: fixed;
    inset: 0;
    z-index: 40;
    background: var(--md-sys-color-surface);
  }
}

/* Medium screens (600px - 839px): Navigation Rail active */
@media (min-width: 600px) and (max-width: 839.98px) {
  .m3-bottom-nav-bar,
  .m3-nav-drawer {
    display: none !important;
  }
  .m3-nav-rail {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 80px;
    background-color: var(--md-sys-color-surface-container-low);
    border-right: 1px solid var(--md-sys-color-outline-variant);
    padding: 16px 0;
  }
  .m3-list-detail-scaffold {
    display: grid;
    grid-template-columns: 360px 1fr;
    flex: 1;
    overflow: hidden;
  }
}

/* Expanded screens (840px - 1199px): Dismissible / Modal Navigation Drawer */
@media (min-width: 840px) and (max-width: 1199.98px) {
  .m3-bottom-nav-bar,
  .m3-nav-rail {
    display: none !important;
  }
  .m3-nav-drawer {
    display: flex;
    flex-direction: column;
    width: 280px;
    background-color: var(--md-sys-color-surface-container-low);
    border-right: 1px solid var(--md-sys-color-outline-variant);
    transition: transform 0.25s cubic-bezier(0.2, 0, 0, 1);
  }
  .m3-nav-drawer.collapsed {
    transform: translateX(-100%);
    position: absolute;
  }
  .m3-list-detail-scaffold {
    display: grid;
    grid-template-columns: 380px 1fr;
    flex: 1;
    overflow: hidden;
  }
}

/* Large screens (>= 1200px): Permanent Navigation Drawer */
@media (min-width: 1200px) {
  .m3-bottom-nav-bar,
  .m3-nav-rail {
    display: none !important;
  }
  .m3-nav-drawer {
    display: flex;
    flex-direction: column;
    width: 280px;
    background-color: var(--md-sys-color-surface-container-low);
    border-right: 1px solid var(--md-sys-color-outline-variant);
  }
  .m3-list-detail-scaffold {
    display: grid;
    grid-template-columns: 420px 1fr;
    flex: 1;
    overflow: hidden;
  }
}
```

