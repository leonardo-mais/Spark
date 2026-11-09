# UI Components & Material Web Reference for Spark-Flask

This document specifies the component catalog, HTML templates, CSS classes, and HTMX interaction patterns for Spark-Flask applications. It establishes the bridge between Jetpack Compose Material 3 components and Material Web (`@material/web`) + custom M3 CSS components.

---

## 1. Master M3 Component & Token Summary Table

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

## 2. Interactive Component Specifications

### 2.1 Outlined Button "Today" (Shape: Full)
```html
<button class="m3-outlined-pill-button" hx-get="/tasks" hx-target="#workspace-container">
  Today
</button>
```
```css
.m3-outlined-pill-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  padding: 0 20px;
  border-radius: var(--md-sys-shape-corner-full);
  border: 1px solid var(--md-sys-color-outline);
  background: transparent;
  color: var(--md-sys-color-on-surface);
  font-family: inherit;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease, border-color 0.2s ease;
}

.m3-outlined-pill-button:hover {
  background-color: rgba(var(--md-sys-color-on-surface-rgb), 0.08);
}
```

### 2.2 Segmented Button (Shape: Full)
```html
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
```
```css
.m3-segmented-button-group {
  display: inline-flex;
  border-radius: var(--md-sys-shape-corner-full);
  border: 1px solid var(--md-sys-color-outline);
  overflow: hidden;
  height: 40px;
}

.segmented-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0 16px;
  background: transparent;
  border: none;
  color: var(--md-sys-color-on-surface);
  font-family: inherit;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.segmented-btn.active {
  background-color: var(--md-sys-color-secondary-container);
  color: var(--md-sys-color-on-secondary-container);
}

.segmented-btn:not(:last-child) {
  border-right: 1px solid var(--md-sys-color-outline);
}
```

### 2.3 Extended FAB (Shape: Large 16dp or Full)
```html
<button class="m3-extended-fab" hx-get="/tasks/new" hx-target="#detail-pane" aria-keyshortcuts="c">
  <span class="material-symbols-rounded">add</span>
  <span class="fab-label">Create</span>
  <kbd class="shortcut-tag">c</kbd>
</button>
```
```css
.m3-extended-fab {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  height: 56px;
  padding: 0 20px;
  background-color: var(--md-sys-color-primary-container);
  color: var(--md-sys-color-on-primary-container);
  border: none;
  border-radius: var(--md-sys-shape-corner-large); /* 16dp */
  box-shadow: var(--md-sys-elevation-2);
  font-family: inherit;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: box-shadow 0.2s cubic-bezier(0.2, 0, 1), background-color 0.2s ease;
}

.m3-extended-fab:hover {
  background-color: var(--md-sys-color-primary-container-high);
  box-shadow: var(--md-sys-elevation-3);
}
```

### 2.4 Date Picker / Mini Widget (Active Day: Primary Circle)
```html
<div class="m3-mini-calendar-widget">
  <div class="mini-cal-header">
    <span class="m3-title-small">November 2026</span>
  </div>
  <div class="mini-cal-grid">
    <span class="cal-cell active-day">4</span>
  </div>
</div>
```
```css
.m3-mini-calendar-widget {
  padding: 8px 12px;
}

.mini-cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  text-align: center;
}

.cal-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  font-size: 0.75rem;
  color: var(--md-sys-color-on-surface);
}

.cal-cell.active-day {
  background-color: var(--md-sys-color-primary);
  color: var(--md-sys-color-on-primary);
  border-radius: var(--md-sys-shape-corner-full);
  font-weight: 700;
}
```

### 2.5 Search Field (Surface Container High, Small 8dp or Full)
```css
.search-pill, .drawer-search-field {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: var(--md-sys-color-surface-container-high);
  border-radius: var(--md-sys-shape-corner-small); /* 8dp */
  padding: 0 12px;
  height: 40px;
}
```

### 2.6 Checkboxes (2dp Corner Radius)
```css
.m3-custom-checkbox, .m3-checkbox-button {
  width: 18px;
  height: 18px;
  border-radius: var(--md-sys-shape-corner-extra-small); /* 2dp */
  border: 2px solid var(--md-sys-color-outline);
  cursor: pointer;
  accent-color: var(--md-sys-color-primary);
}
```

### 2.7 Content Cards & Event Blocks (8dp–12dp)
```css
.m3-content-card {
  border-radius: var(--md-sys-shape-corner-small); /* 8dp */
  padding: 12px 16px;
  background-color: var(--md-sys-color-primary-container);
  color: var(--md-sys-color-on-primary-container);
}

.m3-content-card.secondary {
  background-color: var(--md-sys-color-secondary-container);
  color: var(--md-sys-color-on-secondary-container);
}

.m3-content-card.tertiary {
  background-color: var(--md-sys-color-tertiary-container);
  color: var(--md-sys-color-on-tertiary-container);
}
```

### 2.8 Time Indicator Line
```css
.m3-time-indicator {
  position: relative;
  height: 2px;
  background-color: var(--md-sys-color-error);
  width: 100%;
}

.m3-time-indicator::before {
  content: '';
  position: absolute;
  left: -4px;
  top: -4px;
  width: 10px;
  height: 10px;
  background-color: var(--md-sys-color-error);
  border-radius: var(--md-sys-shape-corner-full);
}
```

### 2.9 Side Utility Rail State Layer (8% Hover, 12% Focus)
```css
.m3-utility-button:hover {
  background-color: rgba(var(--md-sys-color-on-surface-rgb), 0.08);
}

.m3-utility-button:focus-visible {
  background-color: rgba(var(--md-sys-color-on-surface-rgb), 0.12);
}
```

---

## 3. The Four Mandatory UI States

### 3.1 Loading State
```html
<div id="pane-loading-indicator" class="htmx-indicator">
  <md-linear-progress indeterminate></md-linear-progress>
</div>
```

### 3.2 Empty State
```html
<div class="m3-empty-state">
  <span class="material-symbols-rounded empty-icon">assignment_late</span>
  <h3 class="m3-title-medium">No tasks found</h3>
  <p class="m3-body-medium">Get started by creating your first task or checking other lists.</p>
  <button class="m3-filled-button" hx-get="/tasks/new" hx-target="#detail-pane">
    <span class="material-symbols-rounded">add</span>
    <span>Create Task</span>
  </button>
</div>
```

### 3.3 Content State
Interactive content rows/cards with status cues and keyboard navigable rows.

### 3.4 Error State
```html
<div class="m3-error-banner" role="alert">
  <span class="material-symbols-rounded error-icon">error</span>
  <div class="error-text">
    <strong class="m3-title-small">Could not load items</strong>
    <p class="m3-body-small">{{ error_message | default("An unexpected error occurred.") }}</p>
  </div>
  <button class="m3-tonal-button" hx-get="/tasks" hx-target="#workspace-container">
    Retry
  </button>
</div>
```
