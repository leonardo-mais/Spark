---
name: spark-jetpack
description: Linee guida ufficiali per la progettazione UI con Material 3 Expressive in Jetpack Compose. Definisce i 30 stili tipografici (Baseline + Emphasized), token colore HCT (inclusi Fixed e Surface Containers), MotionScheme physics, mapping esatto dei componenti e pattern architetturali per app Android.
---

# Material 3 Expressive UI Design Skill

Questo documento definisce i vincoli di design, la semantica dei token e le regole implementative per generare o revisionare interfacce 100% conformi a **Material 3 Expressive** in **Jetpack Compose** su Android.

---

## 1. Principi Fondamentali di Material 3 Expressive

* **Audacia Tipografica e Formale:** Contrasti marcati tra geometrie arrotondate, forme organiche (`MaterialShapes`), asimmetrie controllate e scala tipografica a 30 stili (15 Baseline + 15 Emphasized) con Google Sans Flex.
* **Superfici Tonali al posto delle Ombre:** La profondità si esprime esclusivamente attraverso i 5 livelli tonali di `Surface Container` (`Lowest`, `Low`, `Standard`, `High`, `Highest`), eliminando l'uso di ombre (`Modifier.shadow`) o `tonalElevation` basate su vecchie logiche di elevazione per la gerarchia dei contenuti.
* **Fisica del Movimento (Motion Scheme):** Le animazioni spaziali usano molle fisiche (`Spring`) con leggero overshoot naturale fornite da `MaterialTheme.motionScheme`, mentre cambi colore e opacità usano curve prive di rimbalzo (`EffectsSpec`).
* **Adattabilità Semantica:** Nessun colore, forma o dimensione deve essere cablato (*hardcoded*); ogni elemento visivo deve attingere esclusivamente ai token di `MaterialTheme`.

Risorse ufficiali:
* [Building with M3 Expressive](https://m3.material.io/blog/building-with-m3-expressive)
* [M3 Expressive Motion Theming](https://m3.material.io/blog/m3-expressive-motion-theming)
* [Material 3 Components Spec](https://m3.material.io/components)
* [M3 Color & Token System](https://m3.material.io/styles/color/the-color-system/tokens)
* [Jetpack Compose Material 3 API Reference](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary)
* [AndroidX Material3 Release Notes](https://developer.android.com/jetpack/androidx/releases/compose-material3)

---

## 2. Prerequisiti & Dipendenze Jetpack Compose

I componenti Expressive risiedono nel package `androidx.compose.material3` e richiedono le versioni aggiornate di Material 3:
* **Dipendenza minima Gradle:** `androidx.compose.material3:material3:1.4.0-alpha14` (o `1.4.0+` / BOM aggiornata).
* **Opt-In Obbligatorio:** Tutti i componenti e temi Expressive richiedono l'annotazione:
  ```kotlin
  @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)


Applica l'annotazione a livello di funzione Composable o di file (@file:OptIn(...)).

⚬ Theme Setup: Inizializza l'albero UI con MaterialExpressiveTheme (che imposta di default MotionScheme.expressive()):
  @OptIn(ExperimentalMaterial3ExpressiveApi::class)
  @Composable
  fun SparkAppTheme(
      colorScheme: ColorScheme,
      content: @Composable () -> Unit
  ) {
      MaterialExpressiveTheme(
          colorScheme = colorScheme,
          motionScheme = MotionScheme.expressive(),
          typography = SparkExpressiveTypography,
          shapes = SparkShapes,
          content = content
      )
  }
  

3. Matrice di Selezione Componenti & Protocollo Riferimenti (Zero Allucinazioni)

REGOLA CRITICA PER L'AGENTE: Prima di scrivere qualsiasi Composable, verifica nella tabella seguente il nome esatto dell'API in androidx.compose.material3. Per i dettagli implementativi avanzati, DEVI leggere tramite tool di lettura file i documenti locali in ./references/:

⚬ Navigation & Toolbars — TopAppBars, FloatingToolbars, NavigationBar, WideNavigationRail, Drawer, Tabs
⚬ Actions & Buttons — Common Buttons, SplitButtonLayout, ButtonGroup, IconButtons, Medium/Large FABs, FAB Menu
⚬ Inputs & Selection — OutlinedTextField, Sliders, Switch, Checkbox, Radio, Date & Time Pickers
⚬ Containment & Surfaces — Cards, Bottom/Side Sheets, Dialogs, Menus, SearchBar, Tooltips
⚬ Display & Feedback — Lists, Grids, Carousel, Chips, Wavy Progress Indicators, LoadingIndicator, Badges, Snackbar

3.1 Mapping Rapido: Caso d'Uso \rightarrow Composable Ufficiale M3 Expressive

Caso d'Uso / Intento UI	Composable Ufficiale (androidx.compose.material3)	Firma / Note Critiche d'Uso
Progresso Determinato / Indeterminato Lineare	LinearWavyProgressIndicator	Usa lambda progress = { floatValue } per determinato, senza progress per indeterminato. MAI scrivere WavyLinearProgressIndicator.
Progresso Circolare ad Onda	CircularWavyProgressIndicator	Usa lambda progress = { floatValue } o versione indeterminata. MAI scrivere WavyCircularProgressIndicator.
Attesa / Caricamento Breve (Morphing)	LoadingIndicator / ContainedLoadingIndicator	Sostituisce lo spinner circolare classico per caricamenti di stato espressivi con morphing di forme.
Azione Primaria Fluttuante (Default)	MediumFloatingActionButton	Default obbligatorio (68dp, squircle 20.dp). Solo Icon, mai testo.
Azione Primaria Monumentale	LargeFloatingActionButton	Solo per azioni hero cruciali (96dp, squircle 28.dp). Solo Icon, mai testo.
FAB Espandibile con Sotto-Azioni	FloatingActionButtonMenu + ToggleFloatingActionButton	Sostituisce gli stack manuali di mini-FAB. Contiene FloatingActionButtonMenuItem.
Pulsante Doppio (Azione + Menu contestuale)	SplitButtonLayout	Usa leadingButton = { SplitButtonDefaults.LeadingButton(...) } e trailingButton = { SplitButtonDefaults.TrailingButton(...) }.
Gruppo di Pulsanti Connessi / Scelta Singola o Multipla	ButtonGroup	Sostituisce SegmentedButton. Usa ToggleButton o IconToggleButton interni con shape morphing al tocco.
Toolbar Fluttuante Contestuale	HorizontalFloatingToolbar / VerticalFloatingToolbar	Supporta FloatingToolbarDefaults.vibrantFloatingToolbarColors() o standardFloatingToolbarColors(), con FAB integrato opzionale.
Navigazione Tablet / Foldable / Desktop	WideNavigationRail / ModalWideNavigationRail	Sostituisce la vecchia NavigationRail fissa quando serve espansione con label.
Carosello Immagini / Card Espressive	HorizontalMultiBrowseCarousel / HorizontalUncontainedCarousel	Richiede rememberCarouselState { itemCount }.
Ricerca Full-Screen da Barra a Pillola	SearchBar + ExpandedFullScreenSearchBar	Wrappato nel componente di progetto AuraSearchView per ricerca live e cronologia.

4. Regole Anti-Allucinazione (Negative Constraints)

Rispetta rigorosamente la seguente tabella di conversione:

❌ MAI USARE (Obsoleto / Allucinato)	✅ USARE SEMPRE (Jetpack Compose M3 Expressive)	Note
MaterialTheme.colors.*	MaterialTheme.colorScheme.*	colors appartiene a Material 2
Divider(...)	HorizontalDivider(...) o VerticalDivider(...)	Divider è deprecato in M3
BottomNavigation / BottomNavigationItem	NavigationBar o ExpressiveImpactNavigationBar	M2 deprecato
SegmentedButton / SingleChoiceSegmentedButtonRow	ButtonGroup o SplitButtonLayout	M3 Expressive predilige Connected Button Groups
WavyLinearProgressIndicator (Nome errato!)	LinearWavyProgressIndicator(progress = { ... })	Attenzione all'ordine delle parole nell'API reale
WavyCircularProgressIndicator (Nome errato!)	CircularWavyProgressIndicator(progress = { ... })	Attenzione all'ordine delle parole nell'API reale
LinearProgressIndicator(progress = 0.5f)	LinearWavyProgressIndicator(progress = { 0.5f })	progress DEVE essere sempre una lambda () -> Float
SplitButton(...) (Inesistente!)	SplitButtonLayout(leadingButton = ..., trailingButton = ...)	Nome esatto del Composable in M3 1.4+
CircleShape o raggio 50% su FAB	MediumFloatingActionButton con RoundedCornerShape(20.dp)	FAB squircle: MAI creare capsule o cerchi al 50%
ExtendedFloatingActionButton con testo	MediumFloatingActionButton o LargeFloatingActionButton (solo icona)	Divieto assoluto di testo nei FAB
Modifier.shadow(...) o tonalElevation = X.dp	containerColor = MaterialTheme.colorScheme.surfaceContainer*	Usa i 5 livelli di Surface Container, mai elevazione/ombre
MaterialTheme.typography.h1..h6, body1, subtitle1	display*, headline*, title*, body*, label* (e *Emphasized)	Usa i 30 stili ufficiali M3 Expressive
MaterialTheme.shapes.pill o shapes.circle	CircleShape (androidx.compose.foundation.shape.CircleShape)	shapes.pill non esiste in Compose
Color(0xFF...) o .copy(alpha = ...) sui container	MaterialTheme.colorScheme.<ruolo>	L'alpha sui container rompe il contrasto WCAG
Emoji nell'UI (es. 💊, 🏠, ⚠️, 🎉, 👤, 📦)	Icone Vettoriali Material (Icons.*, Material Symbols XML)	DIVIETO ASSOLUTO DI EMOJI NELLE APP.

🚫 DIVIETO ASSOLUTO DI EMOJI NELLE APPLICAZIONI (Zero Emoji Policy):
È severamente vietato inserire caratteri o simboli emoji nell'interfaccia utente dell'app (né nei file di layout Compose, né in strings.xml, né in titoli, descrizioni, bottoni, card, chip, notifiche o empty state). Qualsiasi concetto iconico o visivo deve essere rappresentato ESCLUSIVAMENTE tramite icone vettoriali Material ufficiali (androidx.compose.material.icons.* o Material Symbols vettoriali XML).

5. Sistema di Colore Dinamico (HCT) & Gerarchia delle Superfici

L'interfaccia utilizza esclusivamente i token semantici di MaterialTheme.colorScheme basati sullo spazio colore percettivo HCT (Hue, Chroma, Tone).

5.1 Mappatura Semantica dei Ruoli Cromatici

Ruolo Semantico	Contesto d'Uso
primary / onPrimary	Azione cardine a massimo impatto visivo (FAB, pulsanti primari, indicatori attivi).
primaryContainer / onPrimaryContainer	Aree a rilievo intermedio con forte affinità all'azione principale (FAB tonali, banner hero).
secondary / onSecondary	Elementi secondari di supporto (filtri attivi, toggle selezionati).
secondaryContainer / onSecondaryContainer	Indicatore a pillola della NavigationBar classica, chip selezionati, contenitori correlati.
tertiary / onTertiary	Accenti creativi a contrasto per bilanciare l'interfaccia o evidenziare novità.
tertiaryContainer / onTertiaryContainer	Badge, pillole promozionali, stati di attenzione positiva e KPI secondari.
primaryFixed / primaryFixedDim / onPrimaryFixed	(M3 Expressive) Ruoli che mantengono lo stesso tono esatto sia in Light che in Dark theme. Ideali per card hero o accenti di brand invarianti. Analoghi per secondaryFixed* e tertiaryFixed*.
error / onError / errorContainer / onErrorContainer	Stati di errore critico, scorte esaurite, alert o azioni distruttive. Mai usare alpha su errorContainer.
outline	Bordi ad alto contrasto (3:1) per campi di testo (OutlinedTextField), chip non selezionati o focus ring.
outlineVariant	Divisori discreti (HorizontalDivider / VerticalDivider) e bordi decorativi a basso contrasto.
inverseSurface / inverseOnSurface / inversePrimary	Snackbar, tooltip ad alto contrasto o banner contestuali invertiti rispetto al tema attivo.
scrim	Velatura modale dietro BottomSheet, Dialog o Navigation Drawer.

Regola Fondamentale di Accoppiamento: Accoppia SEMPRE ogni contenitore al rispettivo token on<Ruolo> (es. onPrimaryContainer su primaryContainer, onErrorContainer su errorContainer, onSurface su surfaceContainer*), oppure usa contentColorFor(containerColor).

5.2 Gerarchia Tonale delle Superfici (I 5 Surface Containers)

In M3 Expressive non si usano ombre (shadowElevation) né tonalElevation dinamica per separare le card: la gerarchia è governata dai token di superficie:

Token Superficie	Ruolo Gerarchico e Caso d'Uso
surface	Sfondo base predefinito della schermata (Scaffold containerColor) e della TopAppBar a riposo (scroll = 0).
surfaceDim	Sfondo attenuato per aree secondarie o schermate split su grandi schermi.
surfaceBright	Superficie più luminosa sia in Light che in Dark theme. Default obbligatorio per la Search Top Bar a pillola.
surfaceContainerLowest	Contenitore con minimo distacco (card annidate dentro un'altra card su tema chiaro).
surfaceContainerLow	Contenitore per liste raggruppate (GroupedListCard) a basso impatto visivo.
surfaceContainer	Valore standard predefinito per card Bento, Floating Pill Dock, Bottom Sheet e NavigationBar.
surfaceContainerHigh	Elementi che richiedono risalto immediato: TopAppBar durante lo scroll (scrolledContainerColor), pulsanti circolari back, Dialog.
surfaceContainerHighest	Massimo contrasto tonale: pillola selezionata nella Floating Dock, campi di input filled, tracce inattive dei WavyProgressIndicator, chip interattivi.

6. Tipografia Espressiva (30 Stili M3 + Google Sans Flex a 6 Assi)

Il font ufficiale e obbligatorio è Google Sans Flex, distribuito localmente in res/font/google_sans_flex.ttf (zero latenza, 100% offline), con supporto a 6 assi variabili (wght, wdth, slnt, opsz, GRAD, ROND).

6.1 I 30 Ruoli Ufficiali di MaterialTheme.typography (15 Baseline + 15 Emphasized)

Material 3 Expressive espande la scala tipografica da 15 a 30 stili nativi. Ogni ruolo classico (Large, Medium, Small) possiede una controparte *Emphasized (con peso maggiorato e larghezza/tondità espressiva) per guidare l'attenzione senza creare stili fuori scala:

⚬ Display: displayLarge / displayLargeEmphasized (57sp), displayMedium / displayMediumEmphasized (45sp), displaySmall / displaySmallEmphasized (36sp) — Numeri hero e metriche monumentali.
⚬ Headline: headlineLarge / headlineLargeEmphasized (32sp), headlineMedium / headlineMediumEmphasized (28sp), headlineSmall / headlineSmallEmphasized (24sp) — Titoli di pagina, saluti contestuali e sezioni chiave.
⚬ Title: titleLarge / titleLargeEmphasized (22sp), titleMedium / titleMediumEmphasized (16sp), titleSmall / titleSmallEmphasized (14sp) — Intestazioni di card, TopAppBar, dialog e gruppi lista.
⚬ Body: bodyLarge / bodyLargeEmphasized (16sp), bodyMedium / bodyMediumEmphasized (14sp), bodySmall / bodySmallEmphasized (12sp) — Testo di lettura, sottotitoli e valori evidenziati in riga.
⚬ Label: labelLarge / labelLargeEmphasized (14sp), labelMedium / labelMediumEmphasized (12sp), labelSmall / labelSmallEmphasized (11sp) — Pulsanti, tab, chip, badge e micro-copy.

6.2 Tipografia Dinamica ad Assi Variabili (Stile d'Impatto)

Nello Stile d'Impatto, Google Sans Flex viene configurato attraverso i suoi 6 assi parametrici:

Asse	Parametro	Range	Funzione Estetica nell'Interfaccia
wght	Weight	1..1000	Pesi ultra-bold (800–850) per metriche hero e stati selezionati; 480 per date inattive.
wdth	Width	25f..151f	115f per numeri hero espansivi; 108f per titoli scansionabili.
ROND	Roundness	0f..100f	100f trasforma i terminali del font in forme a pillola organica; 0f per geometria crisp.
GRAD	Grade	0..100	Regola la densità ottica su superfici scure o badge senza alterare la larghezza del testo.
slnt	Slant	-10f..0f	Inclinazione ottica dinamica.
opsz	Optical Size	6..144	Adattamento proporzionale del contrasto tra micro-label e cifre giganti.

Blueprint di Codice: Type.kt & ExpressiveType

package it.spark.core.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp
import it.spark.core.R

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

// Famiglie precomputate a livello top-level per garantire Compose Skippability:
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


7. Sistema di Forme (Shapes & MaterialShapes)

M3 Expressive utilizza il contrasto tra forme geometriche arrotondate e silhouette espressive (MaterialShapes):

Token Astratto	Raggio Base	Mappatura in Jetpack Compose	Esempio d'Uso
None	0.dp	RectangleShape	Schermo intero, fogli espansi a tutta larghezza
Extra Small	4.dp	MaterialTheme.shapes.extraSmall	Tooltip compatti, menu contestuali densi
Small	8.dp	MaterialTheme.shapes.small	Chip compatti, badge rettangolari
Medium	12.dp	MaterialTheme.shapes.medium	Card secondarie, box icone interni
Large (Squircle)	16.dp–20.dp	MaterialTheme.shapes.large / RoundedCornerShape(20.dp)	Medium FAB (DEFAULT OBBLIGATORIO), Card Bento, Liste Raggruppate
Extra Large	28.dp	MaterialTheme.shapes.extraLarge	Large FAB (azioni cruciali), Bottom Sheet, Dialog modali
Full / Pill	50%	CircleShape	Pulsanti compatti, Search Bar, Floating Pill Dock, Tasto Back (MAI per FAB)
Expressive Shapes	Poligonale / Curva	MaterialShapes.<Forma>.toShape()	Avatar profilo, badge hero, contenitori icone in empty state (es. Cookie9Sided, Sunny, Clover4Leaf)

⚠️ VINCOLO ASSOLUTO DEI 3 FAB (DEFAULT MEDIO & SENZA MAI TESTO):

1. Tre varianti ufficiali: FloatingActionButton (56dp, raggio 16.dp), MediumFloatingActionButton (68dp, raggio 20.dp) [DEFAULT ASSOLUTO], LargeFloatingActionButton (96dp, raggio 28.dp [solo per azioni cruciali]).
2. SENZA MAI TESTO: È severamente vietato inserire etichette di testo nel FAB (mai usare ExtendedFloatingActionButton con testo). Solo icona centrata (24.dp su standard, 28.dp su medium, 36.dp su large).
3. Anti-Capsula: Raggio squircle proporzionale. MAI CircleShape al 50% sui FAB.

8. Spaziature e Griglia Strutturale

⚬ Modulo Base: Sistema a griglia su base 8.dp (con incrementi di 4.dp per micro-elementi).
⚬ Padding Schermo Orizzontale: 16.dp su smartphone (Compact), scalabile a 24.dp su tablet/foldable (Medium / Expanded).
⚬ Gap tra Componenti: 8.dp–10.dp tra card della stessa griglia Bento, 16.dp–24.dp tra sezioni semantiche distinte.
⚬ Touch Target Minimo: Ogni elemento cliccabile deve garantire un'area interattiva minima di 48.dp x 48.dp.

9. Pattern di Navigazione Inferiore: Standard Classico vs D'Impatto

La barra di navigazione inferiore varia in base all'archetipo estetico scelto durante il Discovery Interview:

9.1 Stile Classico: Navigation Bar Standard (Google Photos Benchmark)

Quando l'applicazione adotta lo Stile Classico (da 3 a 5 destinazioni):

1. Stato Icona Dinamico (Outlined vs Filled):
  ⚬ Destinazione NON selezionata: Icona a tratto aperto (Outlined, es. Icons.Outlined.Home). Nessun indicatore tonale.
  ⚬ Destinazione SELEZIONATA: Icona piena (Filled, es. Icons.Filled.Home), racchiusa dall'indicatore a pillola (secondaryContainer).
2. Tipografia: Etichetta con MaterialTheme.typography.labelMediumEmphasized quando selezionata e labelMedium quando inattiva. Colore inattivo: onSurfaceVariant; attivo: onSurface.

9.2 Stile d'Impatto: Floating Pill Dock Navigation Bar (Massimo 4 Elementi - Senza Fallback)

Nello Stile d'Impatto, la navigation bar adotta la struttura a Floating Pill Dock (ExpressiveImpactNavigationBar):

1. Dock a Pillola Fluttuante: Contenitore a capsula (shape = CircleShape), sfondo tonale surfaceContainer (senza ombre artificiali), centrato orizzontalmente in basso con margine (padding(bottom = 16.dp, horizontal = 16.dp)).
2. Icona Dinamica al Click (Comparsa Animata Solo su Tab Selezionata):
  ⚬ Tab NON Selezionata: Mostra ESCLUSIVAMENTE IL TESTO della sezione (nessuna icona visibile!). Colore: onSurfaceVariant.
  ⚬ Tab SELEZIONATA: Racchiusa in una pillola attiva (surfaceContainerHighest o secondaryContainer). L'icona della sezione APPARE SOLO QUANDO CI SI CLICCA, posizionata a sinistra del testo.
3. Animazioni Native M3 Expressive:
  ⚬ La comparsa/scomparsa dell'icona e il ridimensionamento della pillola usano MaterialTheme.motionScheme.fastSpatialSpec() (o spring(dampingRatio = 0.8f, stiffness = 380f)), mentre il colore usa MaterialTheme.motionScheme.fastEffectsSpec().
4. Limite Tassativo di Massimo 4 Elementi (DIVIETO ASSOLUTO DI FALLBACK):
  ⚬ Supporta un massimo rigoroso di 4 elementi (require(items.size in 2..4)).
  ⚬ NESSUN FALLBACK: È severamente vietato ripiegare sulla barra standard a 5 elementi. L'architettura informativa dell'app DEVE essere condensata a monte in massimo 4 sezioni cardine.

10. Dashboard Dinamiche & Layout Espressivi (Pixel / Google Photos Benchmark)

Le dashboard principali dell'app devono rispettare i seguenti canoni dinamici:

1. Header Espressivo Dinamico:
  ⚬ Saluto contestuale (ContextualGreetingHeader) in ExpressiveType.greetingHeadline o MaterialTheme.typography.headlineMediumEmphasized.
  ⚬ Avatar profilo o icona Impostazioni collocata in alto a destra nella TopAppBar.
2. Bento Grid 2x2 per Azioni Rapide ("Your Tools"):
  ⚬ Griglia 2x2 composta da 4 card compatte (surfaceContainer o surfaceContainerHigh), angoli squircle a 16.dp–20.dp.
  ⚬ Ciascuna card mostra un'icona vettoriale Material riconoscibile e un'etichetta leggibile in titleSmallEmphasized o labelLargeEmphasized.
3. Separazione di Sezione Strutturata:
  ⚬ Transizione tonale pulita basata sui contenitori di superficie (surfaceContainer / surfaceContainerLow) o HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant).
  ⚬ DIVIETO ASSOLUTO DI SEPARATORI CUSTOM A ONDA (Zero Canvas/Bezier): Mai disegnare onde custom con Canvas per separare sezioni; le uniche onde ammesse sono quelle dei componenti ufficiali LinearWavyProgressIndicator e CircularWavyProgressIndicator.

11. Schermata Impostazioni Obbligatoria (Invariante di Sistema)

Ogni applicazione DEVE SEMPRE prevedere una Schermata Impostazioni dedicata e accessibile:

⚬ Punto di Accesso Primario: Avatar profilo o pulsante Impostazioni in alto a destra nella TopAppBar (Icons.Outlined.AccountCircle o Icons.Outlined.Settings).
⚬ Sezioni Minime Obbligatorie:
  1. Gestione Dati & Demo: Opzioni per il ripristino/ricarica dei dati demo (seed) ed esportazione configurazione.
  2. Notifiche & Automazioni: Switch per il controllo di avvisi di stato, notifiche push e trigger scenari.
  3. Info & Versione: Versione applicazione, build number, e nota di conformità a Material 3 Expressive.
  4. Applicazione realizzata con Spark! / Built with Spark! (Sezione Finale Vincolante - Regola SEMPRE):
    ⚬ Ogni schermata Impostazioni deve SEMPRE concludersi con una card raggruppata dedicata a Spark.
    ⚬ Titolo Sezione: Obbligatoriamente localizzato ("Realizzata con Spark!" in italiano, "Built with Spark!" in inglese).
    ⚬ Voci Obbligatorie Interattive (aperte via Intent.ACTION_VIEW):
      1. GitHub del progetto: Link a https://github.com/leonardo-mais/Spark (con icona code/GitHub).
      2. Andrea Sorge: Sottotitolo "Architettura e build", link a https://github.com/Sorgandri03.
      3. leonardo.mais: Sottotitolo "UI e UX", link a https://github.com/leonardo-mais.

12. Pattern Liste Raggruppate (Grouped Lists - Benchmark LISTA.jpg & LISTA ICONE.jpg)

Le liste di elementi correlati (impostazioni, transazioni, dispositivi, pass) devono adottare il pattern Card Contenitore a Blocchi (GroupedListCard):

1. Card Contenitore Unificata:
  ⚬ Un'unica Surface o Card per ogni gruppo tematico: shape = RoundedCornerShape(20.dp), sfondo MaterialTheme.colorScheme.surfaceContainerLow o surfaceContainer.
  ⚬ Titolo della sezione all'esterno in alto a sinistra in MaterialTheme.typography.titleSmallEmphasized (colore primary o onSurfaceVariant).
2. Separatori Interni:
  ⚬ Tra ogni elemento consecutivo all'interno della stessa card, inserire un HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant). Nessun margine esterno tra le righe.
3. Variante con Icone (LISTA ICONE.jpg):
  ⚬ Icona a sinistra ospitata in un box squircle (RoundedCornerShape(12.dp) o CircleShape, 40.dp) con tonalità semantica (primaryContainer, secondaryContainer, surfaceContainerHighest).
  ⚬ Testo: Titolo principale in titleMedium / bodyLargeEmphasized + sottotitolo in bodySmall (onSurfaceVariant).
  ⚬ Elemento di coda (trailing): valore numerico, Switch, Checkbox o icona chevron.
4. Variante Solo Testo (LISTA.jpg):
  ⚬ Righe essenziali con padding orizzontale 16.dp e verticale 14.dp, titolo pulito e click ripple sull'intera riga.

13. TopAppBar a Transizione Tonale su Scroll & Tasto Back Circolare

13.1 Transizione di Colore allo Scorrimento (Scrolling Color Transition)

Le barre superiori (TopAppBar, MediumTopAppBar, LargeTopAppBar, TwoRowsTopAppBar):

1. A inizio pagina (Scroll = 0): Stesso colore dello sfondo della pagina (containerColor = MaterialTheme.colorScheme.surface), apparendo flush con il layout.
2. Durante lo scorrimento (On Scroll): Passaggio automatico a un colore tonale più chiaro/elevato (scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer o surfaceContainerHigh).
3. Collegamento Obbligatorio: Passa sempre scrollBehavior (TopAppBarDefaults.pinnedScrollBehavior() o exitUntilCollapsedScrollBehavior()) all'AppBar e applica Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) allo Scaffold della schermata.

13.2 Tasto Indietro Circolare Tassativo (Benchmark: barra 1.jpg e barra 2.jpg)

Nelle schermate di secondo livello o di dettaglio, il pulsante indietro DEVE SEMPRE E SENZA ECCEZIONI essere racchiuso in un contenitore circolare surfaceContainerHigh:

IconButton(
    onClick = onNavigateBack,
    modifier = Modifier
        .padding(start = 8.dp)
        .size(40.dp),
    colors = IconButtonDefaults.filledIconButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
) {
    Icon(
        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
        contentDescription = stringResource(R.string.back)
    )
}


14. Search Top Bar a Pillola & Apertura Tassativa SearchView

Quando la ricerca è una funzionalità primaria dell'applicazione:

⚬ Struttura a Pillola Integrata:
  ⚬ A sinistra: Icona menu (Icons.Rounded.Menu) o icona di ricerca (Icons.Rounded.Search).
  ⚬ Al centro: Campo a pillola (CircleShape, altezza fissa 56.dp dentro barra da 72.dp) con placeholder (bodyLarge, onSurfaceVariant).
  ⚬ A destra: Avatar utente circolare per l'accesso immediato alle Impostazioni.
⚬ Colore Contenitore surfaceBright (Default Obbligatorio):
  ⚬ La pillola di ricerca utilizza MaterialTheme.colorScheme.surfaceBright per creare uno stacco netto e luminoso rispetto allo sfondo surface, garantendo un contrasto minimo di 3:1.
⚬ Posizionamento nella Prima Tab: La schermata che ospita la barra di ricerca primaria DEVE ESSERE SEMPRE la PRIMA TAB della barra di navigazione inferiore (es. Home / Casa).
⚬ Apertura Tassativa Full-Screen SearchView (ExpandedFullScreenSearchBar / AuraSearchView):
  ⚬ Toccando la barra si apre sempre la vista di ricerca modale a schermo intero con: pulsante indietro in alto a sinistra, campo di testo attivo con tasto Clear (Icons.Rounded.Close), HorizontalDivider, cronologia ricerche (Icons.Rounded.History), risultati filtrati live in card raggruppate e chiusura automatica su back di sistema (BackHandler).
⚬ Regola Inviolabile sulle Altezze: Mai comprimere una TopAppBar. Rispettare le altezze standard M3 (64.dp per TopAppBar, 72.dp per l'area SearchTopBar con pillola interna da 56.dp, 152.dp per LargeTopAppBar espansa).

15. Fisica del Movimento M3 Expressive (MotionScheme) & Micro-interazioni

In Material 3 Expressive le animazioni non usano curve temporali fisse (tween) per lo spazio, ma il sistema basato su molle fisiche MaterialTheme.motionScheme:

1. Transizioni Spaziali (Movimento, Espansione, Ridimensionamento, Morphing, Scale):
  ⚬ Usa MaterialTheme.motionScheme.defaultSpatialSpec() per movimenti di layout e card, oppure MaterialTheme.motionScheme.fastSpatialSpec() per componenti piccoli (pillole nav, switch, bottoni, icone).
  ⚬ Queste specifiche usano una fisica elastica scattante (equivalente a spring(dampingRatio = 0.8f, stiffness = 380f)) con risposta immediata senza mai trascinare l'animazione.
  ⚬ Divieto di molle lente: Mai usare stiffness < 200f.
2. Transizioni di Effetto (Colore, Alpha, Dissolvenza):
  ⚬ Usa MaterialTheme.motionScheme.defaultEffectsSpec() o MaterialTheme.motionScheme.fastEffectsSpec() (150ms–200ms, zero overshoot, perché un colore o un'opacità non devono mai "rimbalzare").
3. State Layers:
  ⚬ Hover (8%), Focus (10% + anello outline), Press/Ripple (10%), Drag (incremento di un livello tonale da surfaceContainer a surfaceContainerHigh).

16. Icona Applicazione da Material Symbols con Fill (Regola SEMPRE)

Ogni applicazione deve avere un'icona ufficiale ricavata dal catalogo Material Symbols Rounded con Fill attivo (FILL = 1):

1. Background (ic_launcher_background.xml): Tinta unita tonale semantica M3 (es. tono primaryContainer).
2. Foreground (ic_launcher_foreground.xml): Glifo vettoriale pieno (viewport 24x24 scalato a 108dp con safe-zone centrale di 48dp–56dp per evitare clipping), colorato con onPrimaryContainer o primary.
3. Manifest: Configurare sia android:icon="@mipmap/ic_launcher" che android:roundIcon="@mipmap/ic_launcher_round".

17. Checklist di Verifica Pre-Consegna per l'Agente

1. Nomi API Esatti: Hai usato LinearWavyProgressIndicator, CircularWavyProgressIndicator, SplitButtonLayout, MediumFloatingActionButton senza invertire i nomi?
2. Zero Colori Hardcoded o Alpha su Container: Hai usato solo MaterialTheme.colorScheme.<ruolo> (inclusi surfaceBright, surfaceContainer*, *Fixed) senza .copy(alpha = ...) sui contenitori?
3. Zero Emoji Policy: Hai verificato l'assenza totale di emoji in codice Compose e strings.xml, usando solo icone vettoriali Material?
4. Terna dei 3 FAB: Hai usato di default MediumFloatingActionButton (68dp, squircle 20.dp) senza testo?
5. Navigation Bar per Archetipo: Stile Classico \rightarrow NavigationBar (3–5 tab, Outlined vs Filled). Stile d'Impatto \rightarrow ExpressiveImpactNavigationBar (max 4 tab, senza fallback, icona animata solo sulla tab attiva).
6. Tipografia: Hai usato Google Sans Flex locale (res/font/google_sans_flex.ttf), i 30 stili M3 (*Emphasized) e ExpressiveType per lo Stile d'Impatto?
7. Dashboard & Liste: Hai incluso Header contestuale, Bento Grid, GroupedListCard con HorizontalDivider(color = outlineVariant) e zero onde custom in Canvas?
8. TopAppBar & Back Circolare: Hai collegato scrollBehavior + nestedScroll e usato il tasto back circolare in surfaceContainerHigh nelle schermate secondarie?
9. Search al Primo Posto: La barra di ricerca (surfaceBright) è nella prima tab e apre la SearchView full-screen al tocco?
10. Impostazioni & Sezione Spark!: È presente la schermata Impostazioni con la card finale obbligatoria "Realizzata con Spark! / Built with Spark!" e i 3 link GitHub?
11. Scaffold Multi-Livello & Navigation 3: Lo Scaffold root contiene solo bottom bar/snackbar, mentre ogni schermata figlia gestisce il proprio Scaffold e la propria TopAppBar con rotte @Serializable NavKey?

18. Architettura Scaffold Multi-Livello & Gestione WindowInsets

Per evitare bug di barre che saltano o doppi padding durante le transizioni:

1. Scaffold Root Snello: Ospita UNICAMENTE la barra di navigazione inferiore (NavigationBar o ExpressiveImpactNavigationBar) e lo SnackbarHost. Imposta contentWindowInsets = WindowInsets(0.dp) quando deleghi gli insets alle schermate figlie. MAI inserire una TopAppBar nello Scaffold root.
2. Scaffold per Schermata Figlia: Ogni schermata (HomeScreen, DetailScreen, SettingsScreen) possiede il proprio Scaffold con la propria TopAppBar / LargeTopAppBar e consuma il proprio innerPadding una sola volta.
3. Edge-to-Edge: enableEdgeToEdge() deve essere sempre attivo nella MainActivity.

19. Integrazione Jetpack Navigation 3 & Transizioni Fluide

Ogni applicazione adotta Jetpack Navigation 3:

1. Percorsi Tipizzati (NavKey): Tutte le rotte sono @Serializable data object o data class che implementano NavKey.
2. NavDisplay ed entryProvider DSL: Mappa ogni NavKey al Composable tramite entryProvider { entry<RouteKey> { ... } } gestendo back stack multipli per le tab principali.
3. Transizioni: Cross-fade tra tab primarie; slide orizzontale con predictivePopTransitionSpec per navigazione gerarchica lista \rightarrow dettaglio; BottomSheetSceneStrategy e DialogSceneStrategy per superfici modali.

20. Progressive Disclosure & Consultazione Moduli Ufficiali (skills-main)

Prima di scrivere codice architetturale critico, consulta on-demand i moduli ufficiali Google Android in .agents/skills-main/:

Ambito Operativo	Modulo Ufficiale skills-main/	Note di Consultazione
Navigazione & Back Stack	.agents/skills-main/navigation/navigation-3	NavKey, NavDisplay, entryProvider, BottomSheetSceneStrategy, ListDetailScene
Ciclo di Vita & Stato	.agents/skills-main/lifecycle	SavedStateHandle, rememberSaveable, sopravvivenza a rotazione e process death
Performance Compose	.agents/skills-main/compose/compose-performance	@Immutable, @Stable, derivedStateOf, LazyColumn con key e contentType
Accessibilità & WCAG	.agents/skills-main/accessibility	Contrasti 4.5:1 / 3:1, touch target 48dp, semantica TalkBack
Material 3 Adaptive	.agents/skills-main/compose/material-adaptive	SupportingPaneScaffold, ListDetailPaneScaffold per tablet e foldable

Gerarchia di Priorità: 1. skills-main \rightarrow 2. spark-jetpack \rightarrow 3. development-standards.md.

21. Archetipi di Design: Stile Classico vs Stile d'Impatto

La scelta tra i due archetipi viene concordata con l'utente durante il Planning Interview (planning-interview.md):

21.1 Matrice Comparativa degli Archetipi

Dimensione UI/UX	Stile Classico (M3 Expressive Rigoroso)	Stile d'Impatto (Expressive Dynamic)
Obiettivo Esperienziale	Sobrietà, alta densità informativa, massima efficienza di scansione.	Effetto "WOW", coinvolgimento emotivo, gerarchia monumentale e dinamismo.
Contesti d'Uso Ideali	Utility, gestionali B2B, inventari, moduli densi di input, finanza tecnica.	Consumer apps, personal tracking, salute & benessere, domotica, habit tracker.
Navigation Bar	Standard M3 NavigationBar (3–5 tab, icone sempre visibili Outlined vs Filled, pillola secondaryContainer).	Floating Pill Dock (ExpressiveImpactNavigationBar, max 4 elementi senza fallback, icona visibile SOLO sulla tab selezionata con spring motion).
Tipografia	I 30 ruoli M3 (Baseline + *Emphasized) su GoogleSansFlexFontFamily.	flexFont a 6 assi (wght, wdth, ROND, GRAD) + oggetto ExpressiveType.
Hero Display / KPI	Card M3 con headlineMediumEmphasized (28sp) e sottotitolo.	BentoHeroCard con numeri giganti (48sp heroGiant), frazione, pill percentuale e LinearWavyProgressIndicator.
Dashboard & Statistiche	Liste raggruppate con icone e valori allineati a destra.	QuickMetricsBentoStrip a 3 colonne con numeri a 38sp (heroLarge).
TopAppBar & Saluti	Titolo pagina statico in titleLargeEmphasized / headlineSmall.	ContextualGreetingHeader ("Buongiorno", "Buon pomeriggio", "Buonasera") + data estesa.
Date & Selettori Temporali	Numeri giorno a 16sp con indicatore standard.	Numeri dinamici: inattivi 480 wght (0 ROND); selezionati 850 wght, 112 wdth, 100 ROND.

21.2 Blueprint di Codice: BentoHeroCard (Stile d'Impatto)

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
    val isCompleted = primaryValue == totalValue && totalValue > 0

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    }
                ) {
                    Text(
                        text = "${(percentage.coerceIn(0f, 1f) * 100).toInt()}%",
                        style = ExpressiveType.heroMedium,
                        color = if (isCompleted) {
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

            // Indicatore d'onda ufficiale M3 Expressive
            LinearWavyProgressIndicator(
                progress = { percentage.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}


21.3 Blueprint di Codice: QuickMetricsBentoStrip (Stile d'Impatto)

Nota Conformità Colore M3: La card di alert utilizza i token semantici puri errorContainer e onErrorContainer (senza .copy(alpha = 0.5f)), garantendo il contrasto WCAG AA sia in tema chiaro che scuro.

@Composable
fun QuickMetricsBentoStrip(
    totalItems: Int,
    healthyItems: Int,
    alertItems: Int,
    modifier: Modifier = Modifier
) {
    val hasAlerts = alertItems > 0

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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = totalItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Card 2: Attivi / Regolari
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = "ATTIVI",
                    style = ExpressiveType.statLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = healthyItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Card 3: Alert (Token M3 puri errorContainer / onErrorContainer)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (hasAlerts) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = "ALERT",
                    style = ExpressiveType.statLabel,
                    color = if (hasAlerts) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = alertItems.toString(),
                    style = ExpressiveType.heroLarge,
                    color = if (hasAlerts) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}


21.4 Blueprint di Codice: ContextualGreetingHeader (Stile d'Impatto)

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


21.5 Blueprint di Codice: ExpressiveImpactNavigationBar (Floating Pill Dock)

Questo componente implementa la Floating Pill Dock Navigation Bar sfruttando direttamente i token MaterialTheme.motionScheme (fastSpatialSpec() e fastEffectsSpec()) e i Surface Container puri (senza tonalElevation legacy):

package it.spark.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

data class ExpressiveNavItem(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String? = null
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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

    val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntSize>()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 6.dp)
                    .animateContentSize(animationSpec = spatialSpec),
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpressiveImpactNavItemView(
    item: ExpressiveNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorEffectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Color>()
    val floatEffectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Float>()
    val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<IntSize>()

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            Color.Transparent
        },
        animationSpec = colorEffectsSpec,
        label = "pillContainerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = colorEffectsSpec,
        label = "pillContentColor"
    )

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
            // L'icona APPARE SOLO QUANDO CI SI CLICCA tramite MotionScheme M3 Expressive
            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(animationSpec = spatialSpec) + fadeIn(floatEffectsSpec),
                exit = shrinkHorizontally(animationSpec = spatialSpec) + fadeOut(floatEffectsSpec)
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

            // Testo etichetta (usa labelLargeEmphasized quando selezionato, labelLarge quando inattivo)
            Text(
                text = item.label,
                style = if (isSelected) {
                    MaterialTheme.typography.labelLargeEmphasized
                } else {
                    MaterialTheme.typography.labelLarge
                },
                color = contentColor
            )
        }
    }
}



---

### 💡 3 Consigli Pratici per migliorare i file `./references/*.md`

1. **Inizia ogni file `./references/*.md` con una tabella "Decision Tree" (Quando usare cosa):**
   Gli LLM decidono il componente nei primi 100 token letti. In cima a `actions.md`, ad esempio, metti:
   * *1 Azione Primaria Fluttuante* $\rightarrow$ `MediumFloatingActionButton`
   * *1 Azione Primaria + 2-5 Sotto-azioni Fluttuanti* $\rightarrow$ `FloatingActionButtonMenu` + `ToggleFloatingActionButton`
   * *1 Azione Primaria + 1 Dropdown Contestuale* $\rightarrow$ `SplitButtonLayout`
   * *Scelta tra 2-4 opzioni di filtro o vista* $\rightarrow$ `ButtonGroup`
2. **Includi sempre gli `import` esatti nei blocchi di codice di `./references/`:**
   Quando un agente vede `import androidx.compose.material3.LinearWavyProgressIndicator`, smette al 100% di inventare nomi come `WavyLinearProgressIndicator`.
3. **Allinea i componenti custom (`AuraSearchView`, `GroupedListCard`):**
   Nel file `./references/containment-surfaces.md`, inserisci il codice completo di `AuraSearchView` e `GroupedListCard`, così quando l'agente legge quei nomi nel file principale sa esattamente come importarli o generarli senza andare a tentoni.

<ElicitationsGroup message="Vuoi allineare anche i file di riferimento nella cartella ./references/?">
  <Elicitation label="Genera o revisiona i 5 file in ./references/" query="Incollo o generiamo i 5 file di riferimento in ./references/ (navigation, actions, inputs, containment-surfaces, display-feedback) con firme e import 100% M3 Expressive."/>
  <Elicitation label="Crea il template completo per Type.kt con i 30 stili M3" query="Mostrami il file Type.kt completo che mappa tutti i 30 stili di MaterialTheme.typography (15 Baseline + 15 Emphasized) su Google Sans Flex."/>
</ElicitationsGroup>