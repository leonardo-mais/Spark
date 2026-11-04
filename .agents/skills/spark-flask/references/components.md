# UI Components & Material Web Reference for Spark-Flask

This document specifies the component catalog, HTML templates, CSS classes, and HTMX interaction patterns for Spark-Flask applications. It establishes the bridge between Jetpack Compose Material 3 components and Material Web (`@material/web`) + custom M3 CSS components.

---

## 1. Android Compose to Web Component Mapping

| Android Jetpack Compose Component | Web Equivalent | Description & Usage |
|---|---|---|
| `Button(onClick = ...)` | `<md-filled-button>` | Primary high-emphasis call to action. |
| `FilledTonalButton(...)` | `<md-tonal-button>` | Medium-emphasis action (e.g. secondary actions). |
| `OutlinedButton(...)` | `<md-outlined-button>` | Low-medium emphasis with border. |
| `TextButton(...)` | `<md-text-button>` | Low-emphasis action (e.g., dialog cancel, text links). |
| `FloatingActionButton(...)` | `.m3-fab` / `.m3-extended-fab` | Elevated Google-style primary action (Google Calendar / Tasks "Create"). |
| `TextField(...)` | `<md-filled-text-field>` | Standard input with container fill. |
| `OutlinedTextField(...)` | `<md-outlined-text-field>` | Input with outline border. |
| `Checkbox(...)` | `<md-checkbox>` | Checkbox for task completion, multi-selection. |
| `Switch(...)` | `<md-switch>` | Binary toggle switch for settings. |
| `FilterChip(...)` | `<md-filter-chip>` | Filterable pill chips for categories and labels. |
| `AssistChip(...)` | `<md-assist-chip>` | Action chip for contextual operations. |
| `LinearProgressIndicator(...)` | `<md-linear-progress>` | Subtle horizontal loading bar for HTMX requests. |
| `CircularProgressIndicator(...)` | `<md-circular-progress>` | Spinner indicator for pane-level loading. |
| `Card(...)` / `ElevatedCard(...)` | `.m3-card` / `.m3-elevated-card` | Container surface with rounded corners (`16px`). |
| `AlertDialog(...)` | `<md-dialog>` | Modal confirmation or creation dialog. |
| `Icon(...)` | `<span class="material-symbols-rounded">` | Material Symbols Rounded with `FILL 1`. **NEVER emoji.** |
| `SnackbarHost(...)` | `.m3-snackbar-host` | Transient bottom alert notifications. |

---

## 2. Google-Style "Create" Extended FAB & Shortcuts

Google Calendar and Google Tasks feature a prominent floating "Create" button:

```html
<!-- In navigation drawer or header -->
<button class="m3-extended-fab"
        hx-get="/items/new"
        hx-target="#detail-pane"
        hx-swap="innerHTML"
        aria-keyshortcuts="c"
        aria-label="Create item">
  <span class="material-symbols-rounded">add</span>
  <span class="fab-label">Create</span>
  <kbd class="shortcut-tag">c</kbd>
</button>
```

CSS Implementation:
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
  border-radius: 16px;
  box-shadow: var(--md-sys-elevation-3);
  font-family: inherit;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: box-shadow 0.2s cubic-bezier(0.2, 0, 0, 1), background-color 0.2s ease;
}

.m3-extended-fab:hover {
  background-color: var(--md-sys-color-primary-container-high);
  box-shadow: var(--md-sys-elevation-4);
}

.shortcut-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.08);
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  margin-left: auto;
}
```

---

## 3. The Four Mandatory UI States: HTML & HTMX Templates

Every view and HTMX fragment must implement all 4 states:

### 3.1 Loading State
Use HTMX indicators to show progress without layout jitter:
```html
<!-- Top-of-pane progress indicator -->
<div id="pane-loading-indicator" class="htmx-indicator">
  <md-linear-progress indeterminate></md-linear-progress>
</div>
```

### 3.2 Empty State
Displayed when the repository returns an empty collection:
```html
<div class="m3-empty-state">
  <span class="material-symbols-rounded empty-icon">assignment_late</span>
  <h3 class="m3-title-medium">No tasks found</h3>
  <p class="m3-body-medium">Get started by creating your first task or checking other lists.</p>
  <button class="m3-filled-button"
          hx-get="/items/new"
          hx-target="#detail-pane">
    <span class="material-symbols-rounded">add</span>
    <span>Create Task</span>
  </button>
</div>
```

### 3.3 Content State
Interactive items using Material Web components and HTMX actions:
```html
<div class="m3-list-group">
  {% for item in items %}
  <div class="m3-list-item {% if selected_id == item.id %}selected{% endif %}"
       id="item-row-{{ item.id }}"
       hx-get="/items/{{ item.id }}"
       hx-target="#detail-pane"
       hx-swap="innerHTML">
    
    <md-checkbox
      touch-target="wrapper"
      {% if item.is_completed %}checked{% endif %}
      hx-post="/items/{{ item.id }}/toggle"
      hx-target="#item-row-{{ item.id }}"
      hx-swap="outerHTML"
      onclick="event.stopPropagation()">
    </md-checkbox>

    <div class="item-text-content">
      <span class="item-title {% if item.is_completed %}completed{% endif %}">{{ item.title }}</span>
      {% if item.due_date %}
        <span class="item-subtitle">{{ item.due_date }}</span>
      {% endif %}
    </div>

    <md-icon-button
      aria-label="Delete"
      hx-delete="/items/{{ item.id }}"
      hx-target="#item-row-{{ item.id }}"
      hx-swap="outerHTML"
      onclick="event.stopPropagation()">
      <span class="material-symbols-rounded">delete</span>
    </md-icon-button>
  </div>
  {% endfor %}
</div>
```

### 3.4 Error State
Displayed on failure with a clear recovery path:
```html
<div class="m3-error-banner" role="alert">
  <span class="material-symbols-rounded error-icon">error</span>
  <div class="error-text">
    <strong class="m3-title-small">Could not load items</strong>
    <p class="m3-body-small">{{ error_message | default("An unexpected error occurred. Please try again.") }}</p>
  </div>
  <button class="m3-tonal-button" hx-get="/items" hx-target="#master-list-pane">
    Retry
  </button>
</div>
```

---

## 4. Transient Snackbars / Toasts with Undo Support

```html
<div class="m3-snackbar" id="active-snackbar" role="status">
  <span class="snackbar-message">{{ message }}</span>
  {% if undo_url %}
  <button class="m3-text-button snackbar-action"
          hx-post="{{ undo_url }}"
          hx-target="#workspace-container">
    Undo
  </button>
  {% endif %}
  <button class="m3-icon-button snackbar-close" onclick="this.closest('.m3-snackbar').remove()">
    <span class="material-symbols-rounded">close</span>
  </button>
</div>
```

