---
name: spark-jetpack
description: Linee guida per la progettazione UI con Material 3 Expressive. Definisce token semantici, gerarchie visive e regole di implementazione per Jetpack Compose (App Android).
---

# Material 3 Expressive UI Design Skill

Questo documento definisce i vincoli di design, la semantica dei token e le regole implementative per generare o revisionare interfacce conformi a **Material 3 Expressive** in **Jetpack Compose** su Android.

---

## 1. Principi Fondamentali di Material 3 Expressive

* **Audacia Tipografica e Formale:** Contrasti marcati tra geometrie arrotondate, asimmetrie controllate e scala tipografica ad alta leggibilità.
* **Superfici Tonali al posto delle Ombre:** La profondità si esprime primariamente attraverso i 5 livelli tonali di `Surface Container`, eliminando l'uso di ombre o elevazioni artificiali per la normale gerarchia dei contenuti.
* **Adattabilità Semantica:** Nessun colore, forma o dimensione deve essere cablato (*hardcoded*); ogni elemento visivo deve attingere esclusivamente ai token di `MaterialTheme`.

Risorse ufficiali:
* [Building with M3 Expressive](https://m3.material.io/blog/building-with-m3-expressive)
* [Material 3 Components Spec](https://m3.material.io/components)
* [M3 Color & Token System](https://m3.material.io/styles/color/the-color-system/tokens)
* [Jetpack Compose Material 3 API Reference](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary)
* [Compose Material 3 Expressive Package](https://developer.android.com/reference/kotlin/androidx/compose/material3/expressive/package-summary)
* [AndroidX Material3 Release Notes](https://developer.android.com/jetpack/androidx/releases/compose-material3)

---

## 2. Prerequisiti & Dipendenze Jetpack Compose

I componenti Expressive richiedono le versioni preview/alpha di Material 3:
* **Dipendenza minima Gradle:** `androidx.compose.material3:material3:1.4.0-alpha01` (o superiore).
* **Opt-In Obbligatorio:** I componenti Expressive (es. `HorizontalFloatingToolbar`, `WavyLinearProgressIndicator`, `ButtonGroup`, `MotionScheme`) richiedono l'annotazione:
  ```kotlin
  @OptIn(ExperimentalMaterial3ExpressiveApi::class)
  ```
  Applica l'annotazione a livello di funzione Composable o di file. Se usi componenti M3 sperimentali standard, includi anche `@OptIn(ExperimentalMaterial3Api::class)`.
* **Theme Setup:** Inizializza o racchiudi l'albero UI con `MaterialExpressiveTheme` impostando lo schema di movimento espressivo:
  ```kotlin
  MaterialExpressiveTheme(
      motionScheme = MotionScheme.expressive()
  ) {
      // Content
  }
  ```

---

## 3. Protocollo di Ispezione Riferimenti (Zero Allucinazioni)

> **REGOLA CRITICA PER L'AGENTE:** Non tentare mai di indovinare le firme dei componenti Expressive o inventare Composable inesistenti. Prima di proporre o scrivere codice, consulta sempre i file locali dedicati in `./references/`:
>
> * [Navigation & Toolbars](./references/navigation.md) — TopAppBars, Toolbars, NavBars, WideNavigationRail, Drawer, Tabs
> * [Actions & Buttons](./references/actions.md) — Common Buttons, SplitButton, Connected ButtonGroup, IconButtons, FABs
> * [Inputs & Selection](./references/inputs.md) — OutlinedTextField, Sliders, Switch, Checkbox, Radio, Date & Time Pickers
> * [Containment & Surfaces](./references/containment-surfaces.md) — Cards, Bottom/Side Sheets, Dialogs, Menus, SearchBar, Tooltips
> * [Display & Feedback](./references/display-feedback.md) — Lists, Grids, Carousel, Chips, Wavy Progress Indicators, Badges, Snackbar

---

## 4. Regole Anti-Allucinazione (Negative Constraints)

I modelli linguistici tendono a riproporre API deprecate di Material 2 o della prima versione di Material 3. Rispetta rigorosamente la seguente tabella di conversione:

| ❌ MAI USARE (Obsoleto / Allucinato) | ✅ USARE SEMPRE (Jetpack Compose M3 Expressive) | Note |
|---|---|---|
| `MaterialTheme.colors.*` | `MaterialTheme.colorScheme.*` | `colors` appartiene a Material 2 |
| `Divider(...)` | `HorizontalDivider(...)` o `VerticalDivider(...)` | `Divider` è deprecato in M3 |
| `BottomNavigation` / `BottomNavigationItem` | `NavigationBar` o `HorizontalFloatingToolbar` | M2 deprecato |
| `SegmentedButton` | `ButtonGroup` (Connected Button Group) o `SplitButton` | Expressive adotta Connected Buttons |
| `LinearProgressIndicator` (statico) | `WavyLinearProgressIndicator(...)` | Iconico di M3 Expressive |
| `CircularProgressIndicator` (statico) | `WavyCircularProgressIndicator(...)` | Iconico di M3 Expressive |
| `CircleShape` o raggio 50% su FAB / Extended FAB | `RoundedCornerShape(16.dp)` | FAB squircle: MAI creare capsule/pillole allungate |
| `Modifier.shadow(...)` per separare card | `containerColor = MaterialTheme.colorScheme.surfaceContainer*` | Usa la gerarchia tonale, non le ombre |
| `MaterialTheme.typography.h1..h6`, `body1`, `subtitle1` | `display*`, `headline*`, `title*`, `body*`, `label*` | Solo i 15 stili ufficiali M3 |
| `MaterialTheme.shapes.pill` o `shapes.circle` | `CircleShape` da `androidx.compose.foundation.shape.CircleShape` | `shapes.pill` non esiste in Compose |
| `Color(0xFF...)` hardcoded nei composable | `MaterialTheme.colorScheme.<ruolo>` | Dinamicità HCT obbligatoria |
| `RoundedCornerShape(16.dp)` sparso nei componenti | `MaterialTheme.shapes.<taglia>` o token standard | Consistenza morfologica |
| **Emoji nell'UI** (es. 💊, 🏠, ⚠️, 🎉, 👤, 📦) | **Icone Vettoriali Material** (`Icons.*`, Material Symbols) | **DIVIETO ASSOLUTO DI EMOJI NELLE APP**. Le emoji degradano il design e sono severamente proibite in testi, titoli, chip, card, dialog ed empty states. |

> 🚫 **DIVIETO ASSOLUTO DI EMOJI NELLE APPLICAZIONI (Zero Emoji Policy):**
> È severamente vietato inserire caratteri o simboli emoji nell'interfaccia utente dell'app (né nei file di layout Compose, né nelle stringhe `strings.xml`, né in titoli, descrizioni, bottoni, card, chip, notifiche o empty state). Qualsiasi concetto iconico o visivo deve essere rappresentato **ESCLUSIVAMENTE tramite icone vettoriali Material ufficiali** (`androidx.compose.material.icons.*` o `Material Symbols` vettoriali XML). L'uso di emoji rende l'applicazione amatoriale ed infrange i requisiti di qualità premium di Spark!.

---

## 5. Sistema di Colore Dinamico & Ruoli delle Superfici

L'interfaccia si adatta dinamicamente a palette HCT (Hue, Chroma, Tone).

### Mappatura Semantica dei Ruoli Cromatici

| Ruolo Semantico | Contesto d'Uso |
|---|---|
| `primary` / `onPrimary` | Azione cardine a massimo impatto visivo (FAB, pulsanti principali). |
| `primaryContainer` / `onPrimaryContainer` | Aree a rilievo intermedio con forte affinità all'azione principale. |
| `secondary` / `onSecondary` | Elementi secondari (filtri attivi, chip, toggle selezionati). |
| `secondaryContainer` / `onSecondaryContainer` | Contenitori a basso contrasto per raggruppare elementi correlati. |
| `tertiary` / `onTertiary` | Accenti a contrasto per bilanciare l'interfaccia o evidenziare novità. |
| `tertiaryContainer` / `onTertiaryContainer` | Badge, pillole promozionali o stati di attenzione positiva e statistiche. |
| `outline` | Bordi ad alto contrasto per campi di testo in focus o selezioni. |
| `outlineVariant` | Divisori discreti (`HorizontalDivider`/`VerticalDivider`) e bordi card a basso contrasto. |
| `error` / `onError` / `errorContainer` | Stati di errore critico, alert o azioni distruttive. |

> **Regola Fondamentale di Accoppiamento:** Accoppia SEMPRE i contenitori al rispettivo colore `on<Ruolo>` (es. `onPrimaryContainer` su `primaryContainer`, `onSurface` su `surface`). Mai usare colori statici (`Color(0xFF...)`).

### Gerarchia delle Superfici (Surface Containers)
In M3 Expressive non si usano ombre (`elevation`) per distinguere i contenuti, ma i **5 livelli tonali**:

1. `surfaceDim` / `surface` / `surfaceBright`: Sfondo base della schermata.
2. `surfaceContainerLowest`: Contenitori con minimo distacco (es. card annidate su fondo chiaro).
3. `surfaceContainerLow`: Schede a basso impatto visivo.
4. `surfaceContainer`: Valore predefinito per card, bottom sheet e dialog.
5. `surfaceContainerHigh`: Elementi che richiedono risalto visivo immediato.
6. `surfaceContainerHighest`: Input fields, barre di ricerca, header in scorrimento.

> **Accessibilità:** Il contrasto tra testo/icone (`on*`) e il rispettivo contenitore deve sempre rispettare WCAG AA (minimo 4.5:1 per testo normale, 3:1 per testo grande).

---

## 6. Tipografia Espressiva (Standard Google Sans Flex)

Il font ufficiale e obbligatorio per l'applicazione è **Google Sans Flex** ([Google Fonts Specimen](https://fonts.google.com/specimen/Google+Sans+Flex)), un font variabile di nuova generazione con spazio a 6 assi (`wght`, `wdth`, `slnt`, `opsz`, `GRAD`, `ROND`). È distribuito localmente come risorsa bundle in `res/font/google_sans_flex.ttf` per garantire rendering istantaneo a zero latenza e funzionamento 100% offline.

### 6.1 I 15 Ruoli Standard M3 (Stile Classico)
In Compose M3 esistono **esclusivamente 15 ruoli tipografici**, strutturati in 5 ruoli su 3 taglie (`Large`, `Medium`, `Small`), tutti mappati sulla famiglia `GoogleSansFlexFontFamily`:

* **Display (`displayLarge`, `displayMedium`, `displaySmall`):** Numeri e dashboard (Google Sans Flex Bold, 36sp–57sp).
* **Headline (`headlineLarge`, `headlineMedium`, `headlineSmall`):** Titoli di pagina e sezioni ad alto impatto (Google Sans Flex Bold/SemiBold, 24sp–32sp).
* **Title (`titleLarge`, `titleMedium`, `titleSmall`):** Intestazioni di card, toolbar, dialog title (Google Sans Flex Medium/SemiBold, 14sp–22sp).
* **Body (`bodyLarge`, `bodyMedium`, `bodySmall`):** Testo di lettura continuo, descrizioni e valori (Google Sans Flex Regular, 12sp–16sp).
* **Label (`labelLarge`, `labelMedium`, `labelSmall`):** Testi funzionali su navigation bar, pulsanti, tab, chip, micro-copy e badge (Google Sans Flex Medium, 11sp–14sp).

### 6.2 Tipografia Dinamica ad Assi Variabili (Stile d'Impatto)
Nello **Stile d'Impatto**, Google Sans Flex viene manipolato direttamente attraverso i suoi 6 assi parametrici per generare un'espressività visiva viva e reattiva:

| Asse | Parametro | Range | Funzione Estetica nell'Interfaccia |
|---|---|---|---|
| `wght` | **Weight** | `1..1000` | Supporto a pesi ultra-bold (`820`, `850`) per metriche hero, e `480` per date inattive. |
| `wdth` | **Width** | `25f..151f` | `115f` per numeri hero espansivi e impattanti; `108f` per titoli scansionabili. |
| `ROND` | **Roundness** | `0f..100f` | `100f` trasforma i tratti del font in terminazioni a pillola rotonda organica; `0f` per geometria crisp. |
| `GRAD` | **Grade** | `0..100` | Ottimizza la densità visiva e la luminescenza su superfici scure senza alterare il layout. |
| `slnt` | **Slant** | `-10f..0f` | Inclinazione ottica dinamica. |
| `opsz` | **Optical Size** | `6..144` | Adattamento proporzionale di contrasto tra micro-label e numeri giganti. |

#### Blueprint di Codice: `Type.kt` & `ExpressiveType`
```kotlin
package it.spark.medications.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import it.spark.medications.R

@OptIn(ExperimentalTextApi::class)
fun flexFont(
    weight: Int = 400,
    width: Float = 100f,
    roundness: Float = 0f,
    grade: Int = 0,
    slant: Float = 0f
): FontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(weight),
            FontVariation.width(width),
            FontVariation.grade(grade),
            FontVariation.slant(slant),
            FontVariation.Setting("ROND", roundness)
        )
    )
)

// Famiglie precomputate ad alte prestazioni (Compose skipping):
val FlexHeroNumericFont = flexFont(weight = 820, width = 115f, roundness = 100f)
val FlexHeadlineFont = flexFont(weight = 750, width = 108f, roundness = 80f)
val FlexSubheadingFont = flexFont(weight = 600, width = 102f, roundness = 60f)
val FlexDateSelectedFont = flexFont(weight = 850, width = 112f, roundness = 100f)
val FlexDateUnselectedFont = flexFont(weight = 480, width = 100f, roundness = 0f)
val FlexClockTimeFont = flexFont(weight = 650, width = 100f, roundness = 50f)
val FlexChipLabelFont = flexFont(weight = 650, width = 105f, roundness = 60f)
val FlexBadgeAlertFont = flexFont(weight = 800, width = 110f, roundness = 80f, grade = 50)
val FlexStatValueFont = flexFont(weight = 780, width = 112f, roundness = 90f)

object ExpressiveType {
    val heroGiant = TextStyle(fontFamily = FlexHeroNumericFont, fontSize = 48.sp, lineHeight = 52.sp, letterSpacing = (-0.5).sp)
    val heroLarge = TextStyle(fontFamily = FlexHeroNumericFont, fontSize = 38.sp, lineHeight = 44.sp, letterSpacing = (-0.25).sp)
    val heroMedium = TextStyle(fontFamily = FlexStatValueFont, fontSize = 28.sp, lineHeight = 34.sp)
    val greetingHeadline = TextStyle(fontFamily = FlexHeadlineFont, fontSize = 26.sp, lineHeight = 32.sp, letterSpacing = (-0.2).sp)
    val dateStripSelected = TextStyle(fontFamily = FlexDateSelectedFont, fontSize = 20.sp, lineHeight = 24.sp)
    val dateStripUnselected = TextStyle(fontFamily = FlexDateUnselectedFont, fontSize = 17.sp, lineHeight = 22.sp)
    val clockTag = TextStyle(fontFamily = FlexClockTimeFont, fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.2.sp)
    val statLabel = TextStyle(fontFamily = FlexSubheadingFont, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp)
    val badgeAlert = TextStyle(fontFamily = FlexBadgeAlertFont, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.1.sp)
}
```

---

## 7. Sistema di Forme (Shape Scale & Expressive Morphology)

M3 Expressive enfatizza angoli ampi, geometrie morbide e asimmetrie controllate:

| Token Astratto | Raggio Base | Mappatura in Jetpack Compose | Esempio d'Uso |
|---|---|---|---|
| **None** | `0.dp` | `RectangleShape` | Schermo intero, fogli a tutta larghezza |
| **Extra Small** | `4.dp` | `MaterialTheme.shapes.extraSmall` | Elementi minimi, tooltip compatti |
| **Small** | `8.dp` | `MaterialTheme.shapes.small` | Chip compatti, indicatori |
| **Medium** | `12.dp` | `MaterialTheme.shapes.medium` | Card standard, dialog modali compatti |
| **Medium / Squircle**| `16.dp`–`20.dp` | `RoundedCornerShape(20.dp)` | **Medium FAB (IL DEFAULT OBBLIGATORIO)**, card in evidenza |
| **Large / Squircle**| `24.dp`–`28.dp` | `RoundedCornerShape(28.dp)` | **Large FAB (azioni cruciali)**, bottom sheets ampi |
| **Full / Pill** | `50%` | `CircleShape` | Pulsanti compatti, chip, barre di ricerca (MAI per FAB) |

> ⚠️ **VINCOLO ASSOLUTO DEI 3 FAB (DEFAULT MEDIO & SENZA MAI TESTO):**
> 1. **Tre varianti:** Standard (56dp), **Medium (68dp) [DEFAULT ASSOLUTO]**, Large (96dp [solo per azioni cruciali]).
> 2. **SENZA MAI TESTO:** È severamente vietato inserire etichette di testo nel FAB (mai usare `ExtendedFloatingActionButton` con testo). Solo icona centrata.
> 3. **Anti-Capsula:** Raggio squircle proporzionale (`16.dp` per standard, `20.dp` per medium, `28.dp` per large). MAI `CircleShape` al 50%.

---

## 8. Spaziature e Griglia Strutturale

* **Modulo Base:** Sistema a griglia su base **8.dp** (con incrementi di **4.dp** per elementi compatti).
* **Padding Schermo:** `16.dp` su smartphone, scalabile a `24.dp` su tablet o display ampi.
* **Gap tra Componenti:** `8.dp` tra elementi correlati nello stesso gruppo, `16.dp` tra sezioni semantiche distinte.

---

## 9. Pattern di Navigazione Inferiore: Standard Classico vs D'Impatto

La barra di navigazione inferiore varia in base all'archetipo estetico scelto durante il Discovery Interview:

### 9.1 Stile Classico: Navigation Bar Standard Google Photos
Quando l'applicazione adotta lo Stile Classico (da 3 a 5 destinazioni), lo stile visivo segue lo standard **Google Photos**:
1. **Stato Icona Dinamico (Outlined vs Filled):**
   - **Destinazione NON selezionata:** Icona a tratto aperto (**Outlined**, es. `Icons.Outlined.Home`, `Icons.Outlined.Widgets`). Nessun indicatore tonale.
   - **Destinazione SELEZIONATA:** Icona piena (**Filled**, es. `Icons.Filled.Home`, `Icons.Filled.Widgets`), racchiusa dall'indicatore a pillola.
2. **Indicatore a Pillola Compatto:** Colore `MaterialTheme.colorScheme.secondaryContainer`, avvolge l'icona con contrasto morbido.
3. **Tipografia & Ritmo:** Etichetta con `MaterialTheme.typography.labelMedium` in Google Sans Medium (~12sp). Colore testo inattivo: `onSurfaceVariant`; testo attivo: `onSurface`.
4. **Transizioni:** Cross-fade nativo per il cambio di pagina.

### 9.2 Stile d'Impatto: Floating Pill Dock Navigation Bar (Massimo 4 Elementi - Senza Fallback)
Nello Stile d'Impatto, la navigation bar adotta la struttura a **Floating Pill Dock** compatta e dinamica:
1. **Dock a Pillola Fluttuante:**
   - Contenitore orizzontale a capsula (`shape = CircleShape` o `RoundedCornerShape(32.dp)`), sfondo tonale `surfaceContainer` (o `surfaceContainerHigh`), centrato orizzontalmente in basso con margine di respiro (`padding(bottom = 16.dp, horizontal = 16.dp)`).
2. **Icona Dinamica al Click (Comparsa Animata Solo su Tab Selezionata):**
   - **Tab NON Selezionata:** Mostra **ESCLUSIVAMENTE IL TESTO** della sezione (nessuna icona visibile!). Colore: `onSurfaceVariant` / `onSurface.copy(alpha = 0.7f)`.
   - **Tab SELEZIONATA:** Racchiusa in una pillola attiva (`surfaceContainerHighest` o `secondaryContainer`). L'icona della sezione **APPARE SOLO QUANDO CI SI CLICCA**, posizionata a sinistra del testo.
3. **Animazioni Native di Android (Snappy Spring Motion):**
   - La comparsa e scomparsa dell'icona sfrutta le animazioni native di Jetpack Compose:
     `AnimatedVisibility(visible = isSelected, enter = expandHorizontally(spring(0.8f, 380f)) + fadeIn(tween(180)), exit = shrinkHorizontally(spring(0.8f, 380f)) + fadeOut(tween(150)))`.
   - La pillola attiva si adatta con `Modifier.animateContentSize(spring(dampingRatio = 0.8f, stiffness = 380f))` e il colore con `animateColorAsState(if (isSelected) surfaceContainerHighest else Transparent)`.
4. **Limite Tassativo di Massimo 4 Elementi (DIVIETO ASSOLUTO DI FALLBACK):**
   - Questo stile supporta un **massimo rigoroso di 4 elementi** (da 2 a 4 destinazioni, ideale 3 o 4).
   - **NESSUN FALLBACK:** È severamente vietato ripiegare sulla barra standard a 5 elementi se le sezioni sono troppe. L'architettura informativa dell'applicazione DEVE essere progettata e condensata a monte in massimo 4 sezioni cardine.

---

## 10. Dashboard Dinamiche & Layout Espressivi (Pixel / Google Photos Benchmark)

Le dashboard principali dell'app non devono mai apparire come elenchi piatti o statici. Devono rispettare i seguenti canoni dinamici:

1. **Header Espressivo Dinamico:**
   - Saluto contestuale (es. "Bentornato a casa", orario, o stato globale della casa) in `Google Sans Bold` (`headlineLarge` o `headlineMedium`).
   - Avatar profilo o icona Impostazioni collocata in alto a destra nella `TopAppBar`.
2. **Bento Grid 2x2 per Azioni Rapide ("Your Tools"):**
   - Una griglia 2x2 composta da 4 card compatte (`surfaceContainerHigh` o `surfaceContainer`), angoli squircle a `16.dp`.
   - Ciascuna card mostra un'icona centrata ben riconoscibile (es. `Icons.Outlined.*`) e un'etichetta leggibile in Google Sans Medium.
3. **Separazione di Sezione Strutturata:**
   - Tra la sezione hero/strumenti in alto e l'elenco sottostante delle stanze/dispositivi, utilizzare una transizione tonale pulita basata sui contenitori di superficie (`surfaceContainer` / `surfaceContainerLow`) o un `HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)`.
   - **DIVIETO ASSOLUTO DI SEPARATORI CUSTOM A ONDA (Zero Canvas/Bezier):** È severamente vietato creare curve custom o separatori a onda tramite `Canvas`: ogni gerarchia visiva deve basarsi esclusivamente su token M3 e superfici semantiche.

---

## 11. Schermata Impostazioni Obbligatoria (Invariante di Sistema)

Ogni applicazione DEVE SEMPRE prevedere una **Schermata Impostazioni** dedicata e accessibile:
* **Punto di Accesso Primario:** Avatar profilo o pulsante Impostazioni in alto a destra nella TopAppBar (`Icons.Outlined.AccountCircle` o `Icons.Outlined.Settings`).
* **Sezioni Minime Obbligatorie:**
  1. **Gestione Dati & Demo:** Opzioni per il ripristino/ricarica dei dati demo (seed) ed esportazione configurazione.
  2. **Notifiche & Automazioni:** Switch per il controllo di avvisi di stato, notifiche push e trigger scenari.
  3. **Info & Versione:** Versione applicazione, build number, e nota di conformità a Material 3 Expressive.
  4. **Applicazione realizzata con Spark! / Built with Spark! (Sezione Finale Vincolante - Regola SEMPRE):**
     - Ogni schermata Impostazioni creata o modificata deve SEMPRE concludersi con un blocco/card dedicato a Spark.
     - **Titolo Sezione:** Obbligatoriamente localizzato ("Realizzata con Spark!" in italiano, "Built with Spark!" in inglese, matchando la lingua del device).
     - **Voci Obbligatorie Interattive (con link aperti via `Intent.ACTION_VIEW`):**
       1. **GitHub del progetto:** Link a `https://github.com/leonardo-mais/Spark` (con icona GitHub / code).
       2. **Andrea Sorge:** Sottotitolo "Architettura e build", link cliccabile a `https://github.com/Sorgandri03`.
       3. **leonardo.mais:** Sottotitolo "UI e UX", link cliccabile a `https://github.com/leonardo-mais`.

---

---

## 12. Pattern Liste Raggruppate (Grouped Lists - Benchmark LISTA.jpg & LISTA ICONE.jpg)

Le liste di elementi correlati (impostazioni, transazioni, dispositivi, pass) non devono apparire come righe flottanti slegate. Devono adottare il pattern **Card Contenitore a Blocchi**:

1. **Card Contenitore Unificata:**
   - Un'unica card per ogni gruppo tematico: `shape = RoundedCornerShape(16.dp)` (o `20.dp`), sfondo `containerColor = MaterialTheme.colorScheme.surfaceContainerLow` o `surfaceContainer`.
   - Titolo della sezione all'esterno in alto a sinistra in `Google Sans Medium` (`titleSmall` / `labelLarge`, color `primary` o `onSurfaceVariant`).
2. **Separatori Interni:**
   - Tra ogni elemento consecutivo all'interno della stessa card, inserire un `HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)`. Nessun margine o padding esterno tra le righe.
3. **Variante con Icone (`LISTA ICONE.jpg`):**
   - Icona a sinistra ospitata in un box/squircle (`RoundedCornerShape(12.dp)` o `CircleShape`, ~40dp) con tonalità di sfondo semantica (`primaryContainer`, `surfaceContainerHighest`, ecc.).
   - Testo: Titolo principale in `titleMedium` / `bodyLarge` + eventuale sottotitolo temporale/descrittivo in `bodySmall`.
   - Elemento di coda (trailing): valore numerico/monetario, stella preferito, switch o chevron.
4. **Variante Solo Testo (`LISTA.jpg`):**
   - Righe compatte ed essenziali con padding orizzontale di `16.dp` e verticale di `14.dp`, titolo pulito e tocco su tutta la superficie della riga.

---

## 13. TopAppBar a Elevazione Dinamica & Transizione Colore su Scorrimento (Benchmark Scrolling M3 & barra 1/barra 2)

### 🌊 Transizione di Colore allo Scorrimento (Scrolling Color Transition)
Le barre superiori dell'applicazione (siano esse `TopAppBar`, `MediumTopAppBar` o `LargeTopAppBar`):
1. **A inizio pagina (Scroll = 0):** Devono inizialmente avere **lo stesso colore dello sfondo della pagina** (`containerColor = MaterialTheme.colorScheme.surface`), apparendo flush e perfettamente integrate con il layout.
2. **Durante lo scorrimento (On Scroll):** Devono riempirsi con un **colore contrastante elevato** (`scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer` o `surfaceContainerHigh`) per creare una netta separazione visiva rispetto al testo e alle card che scorrono sotto.
3. **Persistenza e Movimento:** L'app bar può rimanere fissa a schermo (`TopAppBarDefaults.pinnedScrollBehavior()`), oppure contrarsi/scomparire durante lo scroll (`exitUntilCollapsedScrollBehavior()` o `enterAlwaysScrollBehavior()`), preservando la transizione di colore tonale.
4. **Collegamento Obbligatorio in Compose:** Associa sempre `scrollBehavior` all'app bar e `Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)` al container genitore o `Scaffold`.

### 🔘 Tasto Indietro Circolare Tassativo (Benchmark: barra 1.jpg e barra 2.jpg)
Nelle schermate di secondo livello o di dettaglio, il pulsante back **DEVE SEMPRE E SENZA ECCEZIONI** essere alloggiato in un contenitore circolare:
```kotlin
Surface(
    shape = CircleShape,
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    modifier = Modifier.size(40.dp)
) {
    Box(contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.back),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}
```

---

## 14. Search Top Bar a Pillola & Dimensionamento App Bar (Benchmark Material 3)

Quando la ricerca è una funzionalità primaria dell'applicazione (es. catalogo dispositivi, esplorazione file, rubrica):
* La barra superiore adotta la struttura a pillola integrata:
  - **A sinistra:** Icona Hamburger menu (`Icons.Rounded.Menu`) per aprire il navigation drawer o sheet filtri.
  - **Al centro:** Campo di ricerca a pillola (`Surface(shape = CircleShape)`) con testo placeholder ("Cerca...", `bodyLarge`).
  - **A destra:** Avatar utente in cerchio (`Icons.Outlined.Person`) per l'accesso immediato alle Impostazioni.

### 🎨 Alternate Color Options (Preferito come Default)
Per impostazione predefinita su sfondi scuri o tonali, il contenitore della barra di ricerca deve preferire l'opzione colore alternativa **`surfaceBright`**:
- **Colore Contenitore:** `MaterialTheme.colorScheme.surfaceBright` (anziché `surfaceContainer`), creando uno stacco chiaro e netto rispetto allo sfondo dell'app.
- **Rapporto di Contrasto:** Il testo di ricerca (`onSurface`) e il testo di placeholder/icone (`onSurfaceVariant`) devono garantire sempre un contrasto visivo di almeno **3:1** per la leggibilità.
- **Posizionamento Primario nella Navigation Bar:** La scheda che ospita la barra di ricerca primaria DEVE ESSERE SEMPRE la **PRIMA TAB** della barra di navigazione inferiore (es. la Home / `Casa`), fungendo da punto di ingresso immediato per esplorazione, stanze e dispositivi.

### 🔘 Buttons in Search App Bar
Oltre all'avatar utente posizionato a destra all'esterno della barra:
- Su dispositivi mobile, le search app bar possono supportare **fino a due icone di azione aggiuntive** collocate all'interno o all'esterno della barra di ricerca (es. microfono vocale, filtri, clear button).
- **Regola di ordinamento:** Colloca sempre le azioni più utilizzate a sinistra e quelle meno utilizzate a destra.
- Se l'applicazione richiede un layout minimale senza scorciatoie vocali o filtri extra, mantieni la barra pulita mostrando unicamente l'avatar utente all'esterno.

### 🔍 Apertura Tassativa SearchView su Selezione (Regola SEMPRE - Benchmark Material 3)
Quando l'utente tocca o seleziona la barra di ricerca, questa **DEVE SEMPRE E TASSATIVAMENTE APRIRE IL COMPONENTE SEARCHVIEW** a schermo intero (`AuraSearchView`):
- **Full-Screen Modal su Mobile:** Si espande occupando l'intero schermo per dare massima focalizzazione alla ricerca e ai risultati.
- **Header con Freccia Indietro:** In alto a sinistra è sempre presente il pulsante indietro (`Icons.AutoMirrored.Rounded.ArrowBack`) per uscire dalla vista di ricerca e ripristinare la schermata precedente.
- **Campo Testo Focalizzato:** Il campo di input si attiva immediatamente con placeholder dedicato e pulsante `Close` (X) per cancellare il testo digitato.
- **Divisore Orizzontale:** Una riga divisoria (`HorizontalDivider`) separa l'intestazione di ricerca dalla lista sottostante.
- **Cronologia Ricerche & Suggerimenti:** Quando il campo è vuoto, visualizza l'elenco delle ricerche recenti contrassegnate dall'icona orologio (`Icons.Rounded.History`).
- **Risultati Live:** Durante la digitazione, visualizza i risultati filtrati in tempo reale (adottando il pattern a card raggruppate `AuraGroupedListCard`).
- **Integrazione Back di Sistema:** Il tasto o gesto back di sistema dell'OS deve sempre chiudere la SearchView (`BackHandler`).

### 📏 Regola Inviolabile: "Usa Sempre le Default Height"
- **MAI comprimere o accorciare una TopAppBar**: Tutte le app bar devono utilizzare rigorosamente la loro altezza predefinita di sistema M3 per garantire la corretta leggibilità e accessibilità degli elementi a schermo.
- **Larghezza intera obbligatoria**: L'app bar deve sempre estendersi per l'intera larghezza della finestra (`fillMaxWidth()`).
- **Altezze Standard M3:**
  - `TopAppBar` standard: `64.dp`.
  - `SearchTopBar` integrata: altezza barra `72.dp` con contenitore a pillola interno ad altezza fissa standard di `56.dp`.
  - `LargeTopAppBar`: `152.dp` espansa, `64.dp` collassata.

---

## 15. Micro-interazioni e Stati (Feedback Visivo) & Snappy Expressive Motion

Gli stati interattivi sfruttano gli **State Layer** trasparenti applicati sopra il colore base del componente:
* **Hover:** Layer all'8% di opacità del rispettivo `on<Ruolo>`.
* **Focus:** Layer al 10% di opacità con anello di contorno `outline` ad alto contrasto.
* **Press / Ripple:** Layer al 10% di opacità con feedback di espansione dal tocco.
* **Drag:** Elevazione tonale incrementata di un livello (es. da `surfaceContainer` a `surfaceContainerHigh`).

### ⚡ Temporizzazioni e Dinamica M3 Expressive (Anti-Lentezza)
Le transizioni animate non devono MAI dare una sensazione di lentezza o trascinamento farraginoso. Rispettare i seguenti parametri temporali M3 Expressive:
* **Transizioni Spaziali (Movimento, Apertura, Slide):**
  - Durata standard: **250ms – 300ms** (massimo 350ms per grandi layout espansi).
  - Curve elastiche (Spring Physics): Usa rigidità elevate per risposta immediata e smorzamento naturale senza oscillazioni prolungate:
    ```kotlin
    spring(dampingRatio = 0.8f, stiffness = 380f)
    ```
    *Non usare molle lente con basse rigidità (`stiffness < 200f`) che fanno galleggiare gli elementi oltre i 400ms.*
* **Transizioni di Effetto (Dissolvenze, Fade, Alpha, Cambi Colore):**
  - Durata compresa tra **150ms e 200ms** con interpolazione scattante `FastOutSlowInEasing` o `LinearOutSlowInEasing`.
* **Transizioni Bottom Bar & Sheet:**
  - Slide/fade della NavigationBar: `spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium)` o `tween(250)`.

---

## 16. Icona Applicazione da Material Symbols con Fill (Regola SEMPRE)

Ogni applicazione deve essere dotata di un'icona ufficiale e iconica ricavata direttamente dal catalogo **Material Symbols con Fill attivo** (`FILL = 1`, stile Rounded da [Google Fonts Icons](https://fonts.google.com/icons)):
1. **Selezione Semantica:** Individuare il simbolo Material più affine al dominio dell'applicazione (es. `home_iot_device` per hub domotico).
2. **Icona Adattiva (Adaptive Icon):**
   - **Background (`ic_launcher_background.xml`):** Tinta unita tonale semantica M3 (es. `primaryContainer` o gradiente tonale coerente).
   - **Foreground (`ic_launcher_foreground.xml`):** Il glifo vettoriale pieno del simbolo (`vector` con viewport 24x24 scalato a 108dp con inset centrale a ~48dp o 56dp per evitare clipping della maschera circolare di sistema), colorato con il colore di contrasto associato (`onPrimaryContainer` o `primary`).
3. **Round Icon (`ic_launcher_round.xml`):** Configurare sia la variante standard che `ic_launcher_round` nel manifest.

---

## 17. Checklist di Output per l'Agente

1. **Icona App con Fill:** Icona adattiva basata su Material Symbol pieno (`FILL=1`) con `ic_launcher_background` e `ic_launcher_foreground`.
2. **Transizioni Scattanti (Snappy M3 Motion):** Tempi scattanti (250-300ms spaziali, 150-200ms effetti, spring stiffness 380f). Mai animazioni lente o pesanti.
3. **Tab con Search al Primo Posto:** La search bar è posizionata nella prima tab (`Casa` / `Home`) con opzione alternate color `surfaceBright`.
4. **Liste Raggruppate con Separatore M3:** Card unificate con `HorizontalDivider(color = outlineVariant)` tra gli elementi.
5. **Terna dei 3 FAB (Default Medio, SENZA MAI TESTO):** Usa di default il Medium FAB (68×68 dp, squircle 20dp). Se l'azione è cruciale usa il Large FAB (96×96 dp, squircle 28dp). Non inserire MAI testo nel FAB (solo icona centrata). MAI pillole o Extended FAB con etichette.
6. **Navigation Bar per Archetipo:** Per **Stile Classico**, NavigationBar standard Google Photos (3–5 tab, outlined vs filled, indicatore compatto). Per **Stile d'Impatto**, Floating Pill Dock Navigation Bar (massimo 4 elementi senza fallback, icona che appare solo al click sulla tab attiva tramite animazioni native Android AnimatedVisibility + spring).
7. **Tipografia Google Sans Flex:** Assicurati che l'app usi Google Sans Flex in `Type.kt` come risorsa locale in `res/font/google_sans_flex.ttf`.
8. **Dashboard Dinamica:** Includi header contestuale, griglia 2x2 Bento ("Your tools") e separazione tonale pulita (MAI separatori ad onda Canvas).
9. **TopAppBar Collassabile & Tasto Back Circolare:** Nelle pagine di dettaglio o sottoschermate, usa SEMPRE `LargeTopAppBar` con scroll elevate (`surface` -> `surfaceContainer`) e il pulsante indietro alloggiato in un `CircleShape`.
10. **Apertura Tassativa SearchView:** Tocco su search bar apre full-screen `AuraSearchView` con history e live filtering.
11. **Schermata Impostazioni con Sezione Spark!:** Verifica che esista la schermata Impostazioni con la sezione finale fissa "Applicazione realizzata con Spark!".
12. **Zero Colori Hardcoded:** Usa esclusivamente `MaterialTheme.colorScheme.<ruolo>`.
13. **Scaffold Multi-Livello & Navigazione Reattiva:** Scaffold root solo per NavigationBar/Snackbar; Scaffold interno per ciascuna schermata con la propria TopAppBar. NavKey strongly typed con Navigation 3.
14. **Zero Emoji Policy (DIVIETO ASSOLUTO DI EMOJI):** Non inserire MAI caratteri o glifi emoji (es. 💊, 🏠, ⚠️, 🎉) in alcuna parte dell'applicazione (né nei layout Compose, né in `strings.xml`, né in empty state, titoli, bottoni o notifiche). Rappresentare qualsiasi concetto visivo unicamente con icone vettoriali Material ufficiali.

---

## 18. Architettura Scaffold Multi-Livello & Gestione WindowInsets

Per eliminare definitivamente bug di barre che fluttuano, scompaiono o causano salti sgradevoli di layout durante la navigazione:

1. **Scaffold Root Unico & Snello:**
   - Lo `Scaffold` root di primo livello ospita **UNICAMENTE** la `NavigationBar` inferiore e lo `SnackbarHost`.
   - **DIVIETO DI TOPAPPBAR NEL ROOT:** Non inserire mai una `TopAppBar` o condizionali `when`/`if` nello `Scaffold` root. La visibilità della barra di navigazione deve essere coerente o gestita a livello di route Nav 3.
2. **Scaffold Dedicato per Ciascuna Schermata Figlia:**
   - Ciascuna schermata figlia (es. HomeScreen, DetailScreen, SettingsScreen) possiede il proprio `Scaffold` dedicato.
   - Lo `Scaffold` della schermata figlia ospita la propria `TopAppBar` (`LargeTopAppBar` con scroll elevate, oppure `TopAppBar` con il tasto indietro circolare).
3. **Gestione WindowInsets & Edge-to-Edge:**
   - `enableEdgeToEdge()` deve essere sempre attivo nell'Activity.
   - Nello `Scaffold` root, passare `contentWindowInsets = WindowInsets(0.dp)` se la gestione degli insets è delegata ai figli, oppure passare `innerPadding` al container del `NavDisplay`.
   - Ogni composable figlio consuma i `WindowInsets` corretti senza doppie spaziature (`Modifier.padding(innerPadding)` applicato una sola volta).

---

## 19. Integrazione Jetpack Navigation 3 & Transizioni Fluide

Ogni applicazione deve adottare **Jetpack Navigation 3** come standard architetturale di navigazione:

1. **Definizione Tipizzata dei Percorsi (`NavKey`):**
   - Tutte le rotte sono oggetti/classi Kotlin `@Serializable` che implementano `NavKey`.
   - Zero stringhe hardcoded o passaggi manuali di parametri non tipizzati.
2. **Uso di `NavDisplay` ed `entryProvider` DSL:**
   - Il `NavDisplay` mappa ogni `NavKey` al rispettivo Composable tramite `entryProvider { entry<Key> { ... } }`.
   - Per applicazioni con barra di navigazione inferiore a schede multiple, implementare lo schema a **Multiple Back Stacks** (`rememberNavigationState` con `Map<NavKey, NavBackStack<NavKey>>`).
3. **Transizioni Fluide Native:**
   - Tra schede della NavigationBar: transizione cross-fade / fade-in / fade-out nativa.
   - Tra lista e dettaglio o avanti/indietro: `slideInHorizontally` + `slideOutHorizontally` con supporto a `predictivePopTransitionSpec`.
   - Per sheet contestuali e dialoghi: `BottomSheetSceneStrategy` e `DialogSceneStrategy`.

---

## 20. Progressive Disclosure & Consultazione Moduli Ufficiali (`skills-main`)

Questo repository include il catalogo completo ufficiale Google Android in `.agents/skills-main/`. L'agente DEVE consultare i moduli pertinenti on-demand (Progressive Disclosure) prima di scrivere codice critico:

| Ambito Operativo | Modulo Ufficiale `skills-main/` | Note di Consultazione |
|---|---|---|
| **Navigazione & Back Stack** | `.agents/skills-main/navigation/navigation-3` | Consultare per `NavKey`, `NavDisplay`, `entryProvider`, `BottomSheetSceneStrategy`, `ListDetailScene` |
| **Ciclo di Vita & Process Death** | `.agents/skills-main/lifecycle` | Consultare per `SavedStateHandle`, `rememberSaveable`, sopravvivenza a rotazione e background kill |
| **Performance & Ricomposizioni** | `.agents/skills-main/compose/compose-performance` | Consultare per `@Immutable`, `@Stable`, `derivedStateOf`, liste Lazy con key stabili |
| **Accessibilità & Touch Target** | `.agents/skills-main/accessibility` | Consultare per contrasti WCAG, touch target minimo 48dp, TalkBack semantics |
| **Material 3 Adaptive** | `.agents/skills-main/compose/material-adaptive` | Consultare per layout a due pannelli e supporto tablet/foldable |

> **Gerarchia di Priorità:**
> 1. `skills-main` (regole e convenzioni architetturali ufficiali Google Android)
> 2. `spark-jetpack` (identità visiva, token semantici, design system Material 3 Expressive)
> 3. `development-standards.md` (regole di progetto, build invariants, MVI, offline-first)

---

## 21. Archetipi di Design: Classica vs D'Impatto

Spark! supporta due archetipi visivi complementari. La scelta viene concordata con l'utente nella domanda conclusiva del **Planning Interview** (`planning-interview.md`):

### 21.1 Matrice Comparativa degli Archetipi

| Dimensione UI/UX | 🏛️ Stile Classico (M3 Expressive Rigoroso) | ⚡ Stile d'Impatto (Expressive Dynamic) |
|---|---|---|
| **Obiettivo Esperienziale** | Sobrietà, alta densità, massima efficienza di scansione e produttività. | Effetto "WOW", coinvolgimento emotivo, gerarchia monumentale e dinamismo. |
| **Contesti d'Uso Ideali** | Utility, app enterprise, gestionali B2B, moduli densi di input, tabelle. | Consumer apps, personal tracking, salute & benessere, habit tracker, lifestyle. |
| **Navigation Bar** | Standard M3 `NavigationBar` (3–5 tab, icone sempre visibili outlined vs filled, indicatore a pillola compatto sull'icona, label sotto). | **Floating Pill Dock Navigation Bar** (massimo 4 elementi senza fallback, pillola attiva ad espansione, icona visibile **SOLO** al click con animazioni native Android `AnimatedVisibility` + spring, tab inattive solo testo). |
| **Tipografia Base** | 15 ruoli M3 standard su `GoogleSansFlexFontFamily` (Regular, Medium, Bold). | `flexFont` a 6 assi (`wght`, `wdth`, `ROND`, `GRAD`) + oggetto `ExpressiveType`. |
| **Hero Display / KPI** | Card M3 standard con `headlineMedium` (28sp) e testo descrittivo. | **Bento Hero Card** con numeri giganti (48sp `FlexHeroNumericFont`), frazioni e pill percentuale. |
| **Dashboard & Statistiche** | Liste raggruppate con icone e testi ordinari. | **Quick Metrics Bento Strip** a 3 colonne con numeri scansionabili a 38sp (`heroLarge`). |
| **TopAppBar & Saluti** | Titolo pagina statico (es. "Oggi", "Inventario") a 28sp. | **Saluto Contestuale Orario** ("Buongiorno", "Buon pomeriggio", "Buonasera") + data estesa. |
| **Date & Selettori Temporali** | Numeri giorno a 16sp con badge circolare ordinario. | Numeri dinamici: inattivi `480 wght` crisp; selezionati `850 wght`, `112 wdth`, `100 ROND`. |
| **Pill Tags & Orari** | Testo orario ordinario affiancato al titolo. | Chip pill dedicati con micro-icona orologio in `ExpressiveType.clockTag` e sfondo tonale. |
| **Micro-Interazioni** | Elevazione standard M3 e click ripple nativo. | Spring scaling elastico al tocco (`scale(0.92f)`, `spring(0.8f, 380f)`). |

---

### 20.2 Blueprint di Codice: Bento Hero Card (Stile d'Impatto)

```kotlin
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BentoHeroCard(
    metricTitle: String,
    primaryValue: Int,
    totalValue: Int,
    percentage: Float,
    statusText: String,
    modifier: Modifier = Modifier
) {
    GroupedCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        // Riga superiore: Titolo categoria + Pill percentuale tonale
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = metricTitle.uppercase(),
                style = ExpressiveType.statLabel,
                color = MaterialTheme.colorScheme.primary
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (primaryValue == totalValue && totalValue > 0) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                }
            ) {
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = ExpressiveType.heroMedium,
                    color = if (primaryValue == totalValue && totalValue > 0) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Frazione gigante ad altissimo impatto (Google Sans Flex wght:820, wdth:115, ROND:100)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = primaryValue.toString(),
                style = ExpressiveType.heroGiant,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = " / $totalValue",
                style = ExpressiveType.heroMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Indicatore d'onda M3 Expressive
        LinearWavyProgressIndicator(
            progress = { percentage },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    }
}
```

---

### 20.3 Blueprint di Codice: Quick Metrics Bento Strip (Stile d'Impatto)

```kotlin
@Composable
fun QuickMetricsBentoStrip(
    totalItems: Int,
    healthyItems: Int,
    alertItems: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Totale
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = "TOTALE",
                    style = ExpressiveType.statLabel,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = totalItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Card 2: Regolare
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = "ATTIVI",
                    style = ExpressiveType.statLabel,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = healthyItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Card 3: Alert / Scorta Bassa
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (alertItems > 0) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = "ALERT",
                    style = ExpressiveType.statLabel,
                    color = if (alertItems > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = alertItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = if (alertItems > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
```

---

### 20.4 Blueprint di Codice: Saluto Orario Contestuale (Stile d'Impatto)

```kotlin
@Composable
fun ContextualGreetingHeader(
    modifier: Modifier = Modifier
) {
    val currentHour = remember { java.time.LocalTime.now().hour }
    val greeting = when (currentHour) {
        in 5..12 -> "Buongiorno"
        in 13..17 -> "Buon pomeriggio"
        else -> "Buonasera"
    }

    val todayFormatted = remember {
        java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.FULL)
        ).replaceFirstChar { it.uppercase() }
    }

    Column(modifier = modifier) {
        Text(
            text = greeting,
            style = ExpressiveType.greetingHeadline,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = todayFormatted,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

---

### 21.5 Blueprint di Codice: Floating Pill Dock Navigation Bar (Stile d'Impatto)

Regola Fondamentale per lo Stile d'Impatto:
1. **Dock a Pillola Fluttuante:** Contenitore ellittico (`CircleShape`) tonale (`surfaceContainer`), distanziato dai bordi dello schermo.
2. **Icona Dinamica al Click:** L'icona appare **SOLO ED ESCLUSIVAMENTE QUANDO CI SI CLICCA** (sulla scheda attiva). Le schede inattive mostrano unicamente il testo.
3. **Animazioni Native di Android:** Comparsa/scomparsa con `AnimatedVisibility(visible = isSelected, enter = expandHorizontally(spring(0.8f, 380f)) + fadeIn(), exit = shrinkHorizontally(spring(0.8f, 380f)) + fadeOut())`, pillola attiva animata con `animateContentSize(spring(0.8f, 380f))` e `animateColorAsState`.
4. **Massimo 4 Elementi (DIVIETO DI FALLBACK):** Questo componente accetta un massimo di 4 destinazioni (`require(items.size in 2..4)`). È severamente vietato ripiegare su barre standard a 5 elementi: organizza l'architettura dei flussi condensandola in massimo 4 sezioni cardine.

```kotlin
package it.spark.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modello dati per le destinazioni della Pill Dock Navigation Bar nello Stile d'Impatto.
 */
data class ExpressiveNavItem(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String? = null
)

/**
 * Floating Pill Dock Navigation Bar per lo "Stile d'Impatto".
 *
 * Canoni Visivi & Interattivi:
 * - Forma a pillola fluttuante centrata in basso.
 * - Icona visibile SOLO sulla scheda attiva (AnimatedVisibility + spring motion nativo Android).
 * - Schede inattive con solo testo.
 * - Limite tassativo di massimo 4 elementi (senza fallback).
 */
@Composable
fun ExpressiveImpactNavigationBar(
    items: List<ExpressiveNavItem>,
    selectedRoute: Any,
    onItemSelected: (ExpressiveNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    require(items.size in 2..4) {
        "ExpressiveImpactNavigationBar supporta un massimo rigoroso di 4 elementi (forniti: ${items.size}). " +
            "Non ricorrere a fallback: organizza l'architettura informativa in massimo 4 sezioni."
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 2.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 6.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = 380f
                        )
                    ),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item.route == selectedRoute
                    ExpressiveImpactNavItemView(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onItemSelected(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpressiveImpactNavItemView(
    item: ExpressiveNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 180),
        label = "pillContainerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 180),
        label = "pillContentColor"
    )

    val springMotion = remember {
        spring<androidx.compose.ui.unit.IntSize>(
            dampingRatio = 0.8f,
            stiffness = 380f
        )
    }

    Surface(
        shape = CircleShape,
        color = containerColor,
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .clickable(
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = if (isSelected) 18.dp else 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // L'icona APPARE SOLO QUANDO CI SI CLICCA tramite animazioni native Android
            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(animationSpec = springMotion) + fadeIn(tween(180)),
                exit = shrinkHorizontally(animationSpec = springMotion) + fadeOut(tween(150))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.contentDescription ?: item.label,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            // Testo etichetta (sempre visibile su tutte le tab)
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = contentColor
            )
        }
    }
}
```

