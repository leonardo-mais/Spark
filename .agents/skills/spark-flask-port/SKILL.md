---
name: spark-flask-port
description: Instructions and workflow for porting an existing Spark! Android application into a responsive, containerized Flask web application, targeting specifically the tablet/expanded multi-pane layout with Jinja2, HTMX, and Material 3 web tokens.
---

# Spark! Android-to-Flask Port Skill (`spark-flask-port`)

This skill defines the structured engineering workflow for translating an existing Android application built with **Spark!** (Jetpack Compose, Room, Kotlin Coroutines, Material 3 Expressive) into an equivalent, production-grade **Flask web application** (`spark-flask`).

The port directly leverages the **tablet (expanded) form factor** of the Android app, mapping its side-by-side multi-pane layouts (List-Detail and Supporting Pane) to desktop/tablet browser interfaces with zero build step, using **Flask**, **Jinja2**, **HTMX**, and **Material Web** custom elements.

---

## 1. Prerequisites & Source Code Inspection

Before beginning the migration, the agent inspects the `:app` module of the target Spark! project:
1. **Entities & Room Schema**:
   Locate all `@Entity` annotated classes in `app/src/main/java/.../data/` or `core/database/`.
2. **Room DAOs**:
   Locate all `@Dao` interfaces to extract SQL queries and CRUD methods.
3. **ViewModels & StateFlow**:
   Examine UI state classes (`*UiState`), actions/events, and business use cases in `presentation/` and `domain/`.
4. **Theme & Tokens**:
   Inspect `ui/theme/Color.kt`, `Theme.kt`, and `Type.kt` to extract custom HCT color roles and typography settings.
5. **Screen Hierarchy & Multi-Pane Layouts**:
   Analyze Composable screens configured with `NavigationSuiteScaffold` or `ListDetailSceneStrategy` / two-pane rows for the tablet/expanded window size class.

---

## 2. Translation & Mapping Specifications

### 2.1 Room Entities → SQLAlchemy Models
Translate Kotlin data classes into SQLAlchemy declarative models:

| Android Room (`androidx.room.*`) | Flask SQLAlchemy (`flask_sqlalchemy`) |
|---|---|
| `@Entity(tableName = "items")` | `__tablename__ = "items"` |
| `@PrimaryKey(autoGenerate = true) val id: Long = 0` | `id = db.Column(db.Integer, primary_key=True, autoincrement=True)` |
| `@ColumnInfo(name = "title") val title: String` | `title = db.Column(db.String(255), nullable=False)` |
| `val notes: String?` | `notes = db.Column(db.Text, nullable=True)` |
| `val isCompleted: Boolean = false` | `is_completed = db.Column(db.Boolean, default=False, nullable=False)` |
| `val timestamp: Instant` | `created_at = db.Column(db.DateTime, default=lambda: datetime.now(timezone.utc))` |
| `@ForeignKey(...)` / Relations | `db.ForeignKey(...)` and `db.relationship(...)` |

### 2.2 Room DAOs → Repository Pattern
Convert asynchronous Kotlin DAOs (`Flow<List<T>>`, `suspend fun`) into Python repository classes:

```kotlin
// Android Room DAO
@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY isCompleted ASC, id DESC")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity): Long

    @Query("UPDATE items SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)
}
```

Translates to:

```python
# Python Repository (app/repositories/item_repository.py)
from typing import List, Optional
from app.models.item import Item
from app.repositories.base import BaseRepository

class ItemRepository(BaseRepository[Item]):
    def __init__(self):
        super().__init__(Item)

    def get_all_ordered(self) -> List[Item]:
        return Item.query.order_by(Item.is_completed.asc(), Item.id.desc()).all()

    def set_completed(self, item_id: int, completed: bool) -> Optional[Item]:
        item = self.get_by_id(item_id)
        if item:
            item.is_completed = completed
            self.update(item)
        return item
```

### 2.3 Android ViewModel & UDF → Flask Service + Blueprints
In Jetpack Compose, the ViewModel exposes a `StateFlow<UiState>` and handles user events (`onAction(...)`). In `spark-flask`:
1. The **Service Layer** (`app/services/`) holds domain business logic and state computation.
2. The **Flask Blueprint** handles HTTP requests, invokes the service, and renders either:
   - The full HTML page for initial direct hits / browser refreshes.
   - A concise **Jinja2 partial fragment** when triggered by HTMX (`request.headers.get("HX-Request")`).

### 2.4 Tablet / Expanded Compose Layout → Jinja2 Multi-Pane Scaffold
On Android tablets, Spark! apps use `NavigationSuiteScaffold` with a navigation rail or drawer, and display master and detail panes side-by-side.

In `spark-flask`:
- **Navigation Area**: Mapped to `.m3-nav-drawer` or `.m3-nav-rail` in `shell.html`.
- **List Pane**: Mapped to `#master-list-pane` (`m3-list-pane`) containing the collection list and search filters.
- **Detail Pane**: Mapped to `#detail-pane` (`m3-detail-pane`) displaying the selected item's view or edit form.
- **Back Navigation**: As per Android adaptive guidelines, the back arrow is omitted when panes are displayed side by side.

### 2.5 HCT Theme Colors → CSS Tokens
Export the color tokens from `Color.kt` / `Theme.kt` directly into `app/static/css/tokens.css`:
- `md_theme_light_primary` → `--md-sys-color-primary`
- `md_theme_light_surface` → `--md-sys-color-surface`
- `md_theme_light_surfaceContainer` → `--md-sys-color-surface-container`
- `md_theme_dark_primary` → `--md-sys-color-primary` under `[data-theme="dark"]`

---

## 3. Step-by-Step Port Workflow

To port an app, follow this checklist sequentially:

1. **Scaffold the Web Project**:
   Copy the base template from `.agents/skills/spark-flask/template/` into the destination directory (or `web/` module if co-located).
2. **Translate Data Models**:
   Convert all Room `@Entity` classes to SQLAlchemy models in `app/models/`.
3. **Translate DAOs**:
   Implement queries in `app/repositories/`.
4. **Translate Use Cases & ViewModel Logic**:
   Port business logic into `app/services/`.
5. **Construct Templates from Tablet Screens**:
   - Replicate the tablet multi-pane hierarchy in `app/templates/pages/<feature>/`.
   - Implement the 4 UI states: Loading (`.htmx-indicator`), Empty (`.m3-empty-state`), Content, and Error (`.m3-error-banner`).
6. **Port Theme Tokens**:
   Update `tokens.css` with the exact color hex codes from the Android app's theme.
7. **Configure Container Port**:
   Set `SPARK_FLASK_PORT` in `docker-compose.yml` and `.env` according to user requirements.
8. **Verify with Pytest**:
   Execute `PYTHONPATH=. pytest` to ensure all CRUD operations, HTMX fragments, and route endpoints function correctly.

