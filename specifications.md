# Specifications: Medications (Promemoria & Inventario Medicine)

## 1. Vision & Architecture Overview
**Medications** is an offline-first, highly reliable medicine reminder and inventory management application designed according to **Material 3 Expressive** and **Clean Architecture + MVI**.
Its primary mission is absolute adherence: medication alarms must **NEVER fail, arrive late, or be killed by Android battery optimization**, while maintaining a delightful, expressive visual experience with live dynamic colorized notifications.

### High-Level Architecture
- **Language & Runtime:** Kotlin 2.1+, Jetpack Compose, Material 3 Expressive (`1.4.0-alpha10`+).
- **Architecture Pattern:** Clean Architecture + MVI (Model-View-Intent) with Unidirectional Data Flow (UDF).
- **Persistence & SSOT:** Room Database (SQLite) with reactive `Flow` emissions for all UI updates.
- **Background & Alarms:**
  - `AlarmManager.setExactAndAllowWhileIdle()` / `setAlarmClock()` for zero-delay time-critical execution.
  - `BootReceiver` on `ACTION_BOOT_COMPLETED` & `ACTION_MY_PACKAGE_REPLACED` to guarantee persistent alarm restoration.
  - `NotificationActionReceiver` to handle "Prendi" (Take), "Snooze", and "Salta" (Skip) actions directly from the notification shade without launching the UI.
  - Battery optimization whitelist educational prompt (`ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`).
- **Typography:** Google Sans font family (Regular, Medium, Bold).
- **Navigation:** Multi-Scaffold architecture: root Scaffold hosts solely the bottom `NavigationBar` with 3 tabs; each screen/subscreen manages its own `TopAppBar` and window insets.

---

## 2. Navigation & Information Hierarchy

The bottom navigation bar contains exactly 3 tabs:
1. 📅 **Today (`Oggi`)**: Day-by-day schedule strip, progress header with `WavyLinearProgressIndicator`, and actionable list of doses for the selected day.
2. 📊 **History (`Cronologia`)**: Monthly visual calendar with colored compliance dots (green = complete, yellow = partial, red = missed) and detailed log below the selected date.
3. 💊 **Inventory (`Inventario`)**: Full medicine catalog, stock levels, low-stock warnings, box refill dialog, and quick filter/search.

Top App Bar access:
- Profile/Settings icon in the TopAppBar opens the full **Settings (`Impostazioni`)** screen.

---

## 3. Data Models & Database Schema

### 3.1 Entity: `MedicineEntity`
```kotlin
@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String, // e.g. "500 mg", "10 ml"
    val form: MedicineForm, // PILL, CAPSULE, DROPS, SYRUP, INJECTION, SACHET, INHALER, OTHER
    val daysOfWeek: Int, // Bitmask: bit 0 = Monday, bit 6 = Sunday (or Set<DayOfWeek>)
    val priority: NotificationPriority, // LOW, MEDIUM, HIGH
    val instructions: String, // e.g. "Take with food"
    val boxes: Int, // Number of unopened/active boxes
    val unitsPerBox: Int, // Units per box
    val totalUnits: Int, // Remaining individual units in stock
    val alertBelowUnits: Int, // Threshold for low stock warning
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
```

### 3.2 Entity: `DoseScheduleEntity`
Represents scheduled reminder times for a medicine:
```kotlin
@Entity(
    tableName = "dose_schedules",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicineId")]
)
data class DoseScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineId: Long,
    val timeMinutes: Int, // Minutes from midnight (e.g. 8:00 AM = 480)
    val doseUnits: Int = 1 // Units to take per dose
)
```

### 3.3 Entity: `DoseLogEntity`
Tracks actual compliance history:
```kotlin
@Entity(
    tableName = "dose_logs",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicineId"), Index("scheduledDate")]
)
data class DoseLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineId: Long,
    val scheduleId: Long,
    val scheduledDate: String, // YYYY-MM-DD
    val scheduledTimeMinutes: Int,
    val status: DoseStatus, // PENDING, TAKEN, SKIPPED, MISSED
    val actionTimestamp: Long? = null, // When taken or skipped
    val repeatCountTriggered: Int = 0 // Number of alert repeats dispatched
)
```

### 3.4 Entity: `AppSettings` (DataStore / SharedPreferences)
- `mealBreakfastMinutes`: Int (default 480 = 08:00)
- `mealLunchMinutes`: Int (default 780 = 13:00)
- `mealDinnerMinutes`: Int (default 1200 = 20:00)
- `lowAlertCount`: Int (default 1)
- `lowAlertIntervalMinutes`: Int (default 0)
- `mediumAlertCount`: Int (default 3)
- `mediumAlertIntervalMinutes`: Int (default 10)
- `highAlertCount`: Int (default 5)
- `highAlertIntervalMinutes`: Int (default 5)

---

## 4. Screen-by-Screen Functional Specifications

### 4.1 Today Screen (`TodayScreen`)
- **Top Week Bar**: Horizontal day strip (M T W T F S S) showing day letter and date number. The active/selected day is highlighted with a primaryContainer pill shape. Tap any day to view that day's scheduled doses. Defaults to today.
- **Progress Banner**:
  - Container: `surfaceContainer` card with rounded corners (`16.dp`).
  - Text: e.g. "3 su 5 medicine assunte" / "3 of 5 medicines taken".
  - Indicator: `WavyLinearProgressIndicator` showing adherence percentage for the selected day.
- **Daily Schedule List**:
  - Grouped cards for each scheduled dose sorted by time.
  - Item Card:
    - Left: Form icon badge in squircle container (Pill, Capsule, Drops, etc.) and priority indicator badge (if High priority).
    - Center: Medicine name, dosage, scheduled time, and instructions.
    - Right: Status badge or quick actions:
      - If PENDING: Two buttons: **"Prendi" (Take)** (filled tonal button with check icon) and **"Salta" (Skip)** (text/outlined button).
      - If TAKEN: Check badge with time taken ("Presa alle 08:05").
      - If SKIPPED: Greyed/outline badge ("Saltata").
    - **Inventory Interaction**: Tapping "Prendi" automatically subtracts 1 unit from `totalUnits` in Room, records a `DoseLog` with `TAKEN` status, and updates the `WavyLinearProgressIndicator` with snappy spring animation.

### 4.2 History Screen (`HistoryScreen`)
- **TopAppBar**: Title "Cronologia" / "History".
- **Monthly Calendar Component**:
  - Month navigation (Previous / Next month buttons + current month name & year).
  - 7-column grid (M T W T F S S).
  - Each date cell shows day number and a small color indicator:
    - 🟢 Green: All scheduled doses taken (100% adherence).
    - 🟡 Yellow: Partial adherence (at least one taken, some skipped or pending).
    - 🔴 Red: All doses missed/skipped.
    - Neutral/Empty: No doses scheduled or future date.
  - Tapping a date selects it and loads the list below.
- **Selected Day Dose Details**:
  - Card list showing all logged doses for that date with exact action timestamps and status chips.

### 4.3 Inventory Screen (`InventoryScreen`)
- **TopAppBar**: Title "Inventario" / "Inventory" with search action and filter chips (Tutte, In esaurimento / Low stock, Per forma).
- **Medium FAB**: 68×68 dp squircle (`RoundedCornerShape(20.dp)`), NO TEXT, icon `Icons.Rounded.Add` to open the "Aggiungi Medicina" (Add Medicine) sheet/screen.
- **Medicine Cards**:
  - Unified block container (`surfaceContainerLow`).
  - Header: Medicine name, dosage, form chip, and priority badge (Low, Med, High).
  - Schedule summary: Selected days of week (active dots) and notification times.
  - Stock Info:
    - "Scorte rimanenti: X unità (Y scatole)" / "Stock: X units (Y boxes)".
    - If `totalUnits <= alertBelowUnits`: High-contrast error/warning badge ("⚠️ Scorte in esaurimento: rimangono X unità!").
  - Actions:
    - **"Ricarica scorte" (Refill)** Button: Opens dialog with numeric input for boxes to add. Confirming multiplies boxes by `unitsPerBox`, adds to `totalUnits`, updates `boxes`, and dismisses warning.
    - Card tap: Opens edit medicine screen.
    - Swipe/menu: Option to delete or archive.

### 4.4 Add / Edit Medicine Screen (`MedicineEditorScreen`)
- Form inputs:
  - **Nome medicina** (`OutlinedTextField`).
  - **Dosaggio** (`OutlinedTextField`, e.g. "500 mg").
  - **Forma medicina**: Horizontal chips with icons (Compressa, Capsula, Gocce, Sciroppo, Iniezione, Bustina, Inalatore, Altro).
  - **Giorni della settimana**: 7 circular toggle buttons (L M M G V S D / M T W T F S S) with spring click effect.
  - **Orari di notifica**:
    - Chips per i 3 preset pasto configurabili: `[Colazione (08:00)]`, `[Pranzo (13:00)]`, `[Cena (20:00)]`. Tapping a preset adds that time.
    - "+ Aggiungi orario personalizzato": Opens M3 TimePicker dialog.
    - Chip list of all chosen times with "X" to remove.
  - **Priorità notifica**: Segmented ButtonGroup: `Bassa (Low)`, `Media (Medium)`, `Alta (High)`.
  - **Istruzioni**: Multi-line text field (e.g. "A stomaco pieno con acqua").
  - **Gestione Scorte (Stock)**:
    - Scatole iniziali (Boxes count).
    - Unità per scatola (Units per box).
    - Calcolo automatico: Unità totali = scatole × unità per scatola.
    - Avvisa quando le unità scendono sotto: (Alert below units threshold).
- Save action: Validates inputs, saves to Room, schedules exact alarms via `AlarmManager`.

### 4.5 Settings Screen (`SettingsScreen`)
- **Sezione 1: Preset Orari Pasti**:
  - Colazione: orario modificabile con TimePicker (default 08:00).
  - Pranzo: orario modificabile con TimePicker (default 13:00).
  - Cena: orario modificabile con TimePicker (default 20:00).
- **Sezione 2: Livelli di Priorità Notifiche**:
  - Card per ciascun livello (Low, Medium, High):
    - Numero di allarmi (alert repeats): slider / number picker.
    - Intervallo tra gli allarmi in minuti: slider / number picker.
    - Indicatori visivi di comportamento (Bassa = discreta; Media = 3 avvisi; Alta = avviso prioritario).
- **Sezione 3: Batteria & Affidabilità Allarmi**:
  - Stato permessi `SCHEDULE_EXACT_ALARM` e `POST_NOTIFICATIONS`.
  - Bottone per richiedere esclusione dall'ottimizzazione batteria (`ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`) per garantire che Android non sospenda i reminder.
- **Sezione 4: Backup e Ripristino**:
  - Bottone "Esporta dati (JSON)": apre SAF CreateDocument o ShareSheet.
  - Bottone "Ripristina da file JSON": apre SAF OpenDocument, valida e riprogramma allarmi.
- **Sezione 5: Spark Section Invariant (OBBLIGATORIA)**:
  - Titolo: "Realizzata con Spark!" (in italiano) / "Built with Spark!" (in inglese).
  - Badge: "Spark! Framework".
  - Voce 1: "GitHub del progetto" con link a `https://github.com/leonardo-mais/Spark-`.
  - Voce 2: "Andrea Sorge", sottotitolo "Architettura e build", link cliccabile a `https://github.com/Sorgandri03`.
  - Voce 3: "leonardo.mais", sottotitolo "UI e UX", link cliccabile a `https://github.com/leonardo-mais`.

---

## 5. Live Notifications & Exact Alarm Engine

### 5.1 Scheduling Engine
- `MedicationAlarmScheduler`:
  - Computes next trigger timestamp for each active `(medicine, schedule)` based on selected days of week.
  - Calls `alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)`.
  - PendingIntent targets `AlarmReceiver`, carrying `medicineId`, `scheduleId`, and `repeatIndex`.
- `BootReceiver`:
  - Catches `Intent.ACTION_BOOT_COMPLETED`, `Intent.ACTION_TIME_CHANGED`, `Intent.ACTION_TIMEZONE_CHANGED`, and `Intent.ACTION_MY_PACKAGE_REPLACED`.
  - Instantly queries Room for all active medicines and restores all exact alarms.

### 5.2 Notification Presentation & Material You Tinting
- Notification Channel: High importance channel with sound, vibration, and lights enabled.
- `NotificationCompat.Builder`:
  - `setColorized(true)` and `setColor(dynamicPrimaryColor)` to highlight the notification prominently in the system shade.
  - BigTextStyle with medicine dosage and instructions.
  - Action 1: **"Prendi" (Take)** (`ACTION_DOSE_TAKEN`) -> decrements Room stock, marks `DoseLog` as `TAKEN`, cancels any scheduled repeat alarms for this dose, and dismisses the notification.
  - Action 2: **"Snooze"** (`ACTION_DOSE_SNOOZE`) -> schedules a repeat alarm after the configured priority interval.
  - Action 3: **"Salta" (Skip)** (`ACTION_DOSE_SKIPPED`) -> records `DoseLog` as `SKIPPED`, cancels remaining repeats, and dismisses the notification.
- High Priority Behavior:
  - Set as `FLAG_INSISTENT` / audio attributes with alarm usage so sound plays clearly even in noisy environments.
  - Automatically re-alerts up to configured repeat count if not acknowledged.

---

## 6. Build, Icons & Delivery Standards
- **App Name:** `Medications`
- **App Icon:** Material Symbol `medication` (FILL = 1), adaptive icon with `ic_launcher_background.xml` (tonal `primaryContainer`) and `ic_launcher_foreground.xml` (`onPrimaryContainer`).
- **FAB:** Medium FAB (68×68 dp, squircle 20dp, NO TEXT) as default.
- **Delivery Invariant:** Universal output in `apk/` with semantic versioning via `./release-agent`.
