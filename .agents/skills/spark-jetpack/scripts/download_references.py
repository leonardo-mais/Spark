#!/usr/bin/env python3
"""
M3 Expressive - UI Components Reference Downloader & Formatter
Questo script scarica la documentazione ufficiale dei componenti Material 3 / Jetpack Compose
elencati in 'ui-components.md' e genera file di documentazione puliti, ordinati e strutturati
all'interno della cartella 'references/', integrando:
- Decision Trees all'inizio di ogni file di categoria
- Import esatti e snippet 100% conformi a Material 3 Expressive, Navigation 3 e alle regole Spark!
- Cross-reference diretti ai moduli ufficiali Google in '.agents/skills/'
- Modalità di fallback offline automatica (preserva descrizioni e firme già estratte se la rete non è disponibile)
"""

import os
import sys
import re
import json
import time
import hashlib
import argparse
import subprocess
import html
from pathlib import Path
from html.parser import HTMLParser

# Mappatura categorie standard M3 Expressive
CATEGORY_MAP = {
    # Navigation & Toolbars
    "app bars": "navigation",
    "toolbars": "navigation",
    "tabs": "navigation",
    "navigation": "navigation",
    "navigation bar": "navigation",
    "floating bar": "navigation",
    "navigation drawer": "navigation",
    "navigation rail": "navigation",

    # Actions & Buttons
    "buttons": "actions",
    "button groups": "actions",
    "common buttons": "actions",
    "split button": "actions",
    "toggle button groups": "actions",
    "icon buttons": "actions",
    "floating action buttons": "actions",
    "floating action buttons (fabs)": "actions",
    "extended floating action buttons": "actions",
    "extended floating action buttons (extended fabs)": "actions",

    # Inputs & Selection
    "inputs": "inputs",
    "text fields": "inputs",
    "sliders": "inputs",
    "switch": "inputs",
    "checkbox": "inputs",
    "radio button": "inputs",
    "date and time": "inputs",
    "date picker": "inputs",
    "time picker": "inputs",

    # Containment & Surfaces
    "cards": "containment-surfaces",
    "dialogs": "containment-surfaces",
    "divider": "containment-surfaces",
    "menus": "containment-surfaces",
    "search": "containment-surfaces",
    "tooltips": "containment-surfaces",
    "sheets": "containment-surfaces",
    "bottom sheets": "containment-surfaces",
    "side sheets": "containment-surfaces",
    "motion scheme": "containment-surfaces",

    # Display & Feedback
    "lists": "display-feedback",
    "grids": "display-feedback",
    "carousel": "display-feedback",
    "chips": "display-feedback",
    "snackbar": "display-feedback",
    "badges": "display-feedback",
    "loading & progress": "display-feedback",
    "loading indicator": "display-feedback",
    "progress indicator": "display-feedback"
}

CATEGORY_METADATA = {
    "navigation": {
        "title": "Navigation & App Bars",
        "desc": "Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per componenti di navigazione (TopAppBars, Toolbars, NavBars, WideNavigationRail, Drawer, Tabs) conformi a Material 3 Expressive e Jetpack Navigation 3.",
        "skills_main": [
            ("Navigation 3 & Multiple Back Stacks", "../navigation-3/SKILL.md"),
            ("Recipe: Multiple Backstacks (Bottom Bar)", "../navigation-3/references/android/guide/navigation/navigation-3/recipes/multiple-backstacks.md"),
            ("Recipe: Modular Hilt Navigation", "../navigation-3/references/android/guide/navigation/navigation-3/recipes/modular-hilt.md"),
            ("Edge-to-Edge & System Insets", "../edge-to-edge/SKILL.md"),
            ("Material 3 Adaptive Layouts", "../adaptive/SKILL.md")
        ],
        "decision_tree": [
            ("Primary Screen Header (Scroll Transition)", "`TopAppBar` (`64.dp`) con `pinnedScrollBehavior()`", "`surface` at rest $\\rightarrow$ `surfaceContainer` on scroll"),
            ("Detail / Sub-Screen Collapsing Header", "`LargeTopAppBar` (`152.dp`) con `exitUntilCollapsedScrollBehavior()`", "Obbligatorio pulsante Back circolare `40.dp` in `surfaceContainerHigh`"),
            ("Bottom Navigation (Stile Classico)", "`NavigationBar` + `NavigationBarItem`", "3–5 tab, contenitore `surfaceContainerLow`, icone `Outlined` vs `Filled`"),
            ("Bottom Navigation (Stile d'Impatto)", "`ExpressiveImpactNavigationBar` (Floating Pill Dock)", "Max 4 tab (zero fallback), icona animata solo sul tab attivo, gradiente protettivo"),
            ("Contextual Floating Actions Bar", "`HorizontalFloatingToolbar` / `VerticalFloatingToolbar`", "Sostituisce la vecchia `BottomAppBar`"),
            ("Tablet / Foldable Side Navigation", "`WideNavigationRail` / `ModalWideNavigationRail`", "Sostituisce il vecchio `NavigationRail` fisso")
        ]
    },
    "actions": {
        "title": "Actions & Buttons",
        "desc": "Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per pulsanti ed elementi di azione (Common Buttons, SplitButtonLayout, ButtonGroup, IconButtons, Terna dei 3 FAB) conformi a Material 3 Expressive.",
        "skills_main": [
            ("Compose Performance & Stability", "../jetpack-compose-performance/SKILL.md"),
            ("Material 3 Adaptive Components", "../adaptive/SKILL.md")
        ],
        "decision_tree": [
            ("1 Azione Primaria Fluttuante (DEFAULT OBBLIGATORIO)", "`MediumFloatingActionButton` (`68.dp`, squircle `20.dp`, icona `30.dp`)", "**SOLO ICONA** (mai testo, mai `CircleShape` 50%)"),
            ("1 Azione Fluttuante Monumentale (Hero)", "`LargeFloatingActionButton` (`96.dp`, squircle `28.dp`, icona `36.dp`)", "Solo per l'azione cardine assoluta della schermata; **SOLO ICONA**"),
            ("1 Azione Primaria + 2–5 Sotto-Azioni Fluttuanti", "`FloatingActionButtonMenu` + `ToggleFloatingActionButton`", "Sostituisce stack manuali di mini-FAB"),
            ("1 Azione Primaria Fissa su Schermata Secondaria", "`ExpressivePrimaryActionButton` (`56.dp` full-width in basso)", "Con maschera gradiente verticale `Transparent` $\\rightarrow$ `surface`"),
            ("1 Azione Primaria + Menu Contestuale Dropdown", "`SplitButtonLayout` (`LeadingButton` + `TrailingButton`)", "Nome esatto API M3 1.4+ (mai scrivere `SplitButton`)"),
            ("Scelta Singola/Multipla tra 2–4 Opzioni o Viste", "`ButtonGroup` con `ToggleButton`", "Sostituisce il deprecato `SegmentedButton`")
        ]
    },
    "inputs": {
        "title": "Inputs & Selection Controls",
        "desc": "Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per campi di testo, slider espressivi, selettori e date/time pickers conformi a Material 3 Expressive.",
        "skills_main": [
            ("Edge-to-Edge & IME Keyboard Insets", "../edge-to-edge/SKILL.md"),
            ("Runtime Permissions & Photo Picker", "../android-permissions-security/SKILL.md"),
            ("State Stability & Snapshot Flow", "../jetpack-compose-performance/references/state-and-stability.md")
        ],
        "decision_tree": [
            ("Campo di Testo Standard / Form", "`OutlinedTextField`", "Bordo `outline`, stato sopravvivente a process death (`rememberSaveable` / `SavedStateHandle`)"),
            ("Selezione Valore Continuo o Discreto", "`Slider` / `RangeSlider`", "Supporta orientamento verticale/orizzontale e taglie M3 Expressive"),
            ("Toggle Binario Impostazioni / Riga Lista", "`Switch`", "Da inserire come `trailingContent` dentro `GroupedListRow`"),
            ("Selezione Data / Intervallo Date", "`DatePicker` / `DatePickerDialog`", "Formattazione output sempre via `DateTimeFormatter.ofLocalizedDate(...)`"),
            ("Selezione Orario", "`TimePicker` / `TimeInput`", "Rispettare formato 12h/24h di sistema")
        ]
    },
    "containment-surfaces": {
        "title": "Containment & Surfaces",
        "desc": "Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per schede tonali, liste raggruppate (`GroupedListCard`), fogli (`ModalBottomSheet`), dialoghi, menu, ricerca (`AppSearchView`) e `MotionScheme` conformi a Material 3 Expressive.",
        "skills_main": [
            ("Navigation 3 BottomSheet & Dialog Scenes", "../navigation-3/references/android/guide/navigation/navigation-3/recipes/bottomsheet.md"),
            ("Navigation 3 List-Detail Adaptive Scene", "../navigation-3/references/android/guide/navigation/navigation-3/recipes/material-listdetail.md"),
            ("Intent Security & Deep Links", "../android-intent-security/SKILL.md")
        ],
        "decision_tree": [
            ("Blocco Unificato di Voci Correlate / Impostazioni", "`GroupedListCard` + `GroupedListRow`", "Squircle `20.dp`, sfondo `surfaceContainerLow`, divisori `outlineVariant`"),
            ("KPI Principale con Numero Gigante (Stile d'Impatto)", "`BentoHeroCard` (`48sp` `ExpressiveType.heroGiant`)", "Include pillola percentuale e `LinearWavyProgressIndicator`"),
            ("Striscia a 3 Metriche Rapide (Stile d'Impatto)", "`QuickMetricsBentoStrip` (`38sp` `ExpressiveType.heroLarge`)", "Usa `errorContainer` / `onErrorContainer` puri per gli alert"),
            ("Barra di Ricerca Primaria + Vista Full-Screen", "`AppSearchTopBar` (`surfaceBright`) + `AppSearchView`", "Obbligatoria nella prima tab; espansione full-screen con `BackHandler` e cronologia"),
            ("Separatore tra Elementi", "`HorizontalDivider` / `VerticalDivider` (`color = outlineVariant`)", "**VIETATO** usare `Divider` (M2) o onde custom con `Canvas`")
        ]
    },
    "display-feedback": {
        "title": "Display & Feedback",
        "desc": "Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per liste espressive (`LazyColumn`), caroselli, chip, badge, indicatori di progresso ondulati (`LinearWavyProgressIndicator`, `CircularWavyProgressIndicator`), `LoadingIndicator` e snackbar conformi a Material 3 Expressive.",
        "skills_main": [
            ("Compose Lazy Layouts Performance", "../jetpack-compose-performance/references/lazy-layouts.md"),
            ("State Stability (`@Immutable` / `derivedStateOf`)", "../jetpack-compose-performance/references/state-and-stability.md")
        ],
        "decision_tree": [
            ("Progresso Lineare Determinato / Indeterminato", "`LinearWavyProgressIndicator(progress = { ... })`", "**MAI** scrivere `WavyLinearProgressIndicator`; `progress` è sempre una lambda `() -> Float`"),
            ("Progresso Circolare Determinato / Indeterminato", "`CircularWavyProgressIndicator(progress = { ... })`", "**MAI** scrivere `WavyCircularProgressIndicator`"),
            ("Attesa Breve / Caricamento Schermata", "`LoadingIndicator` / `ContainedLoadingIndicator`", "Shape morphing espressivo M3"),
            ("Galleria / Carosello Orizzontale", "`HorizontalMultiBrowseCarousel` / `HorizontalUncontainedCarousel`", "Richiede `rememberCarouselState { itemCount }`"),
            ("Collezione Scorrevole di Dati", "`LazyColumn` / `LazyVerticalGrid`", "Fornire sempre `key = { it.id }`, `contentType`, e `consumeWindowInsets(innerPadding)`")
        ]
    }
}

# Snippet Compose autoritativi 100% conformi a M3 Expressive, Navigation 3 e Spark!
COMPOSE_M3_EXPRESSIVE_SNIPPETS = {
    "app bars": """import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

// Sottoschermata / Dettaglio: LargeTopAppBar con transizione colore su scroll e pulsante Back circolare obbligatorio (40.dp)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveDetailScreenScaffold(
    title: String,
    backContentDescription: String,
    onNavigateBack: () -> Unit,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMediumEmphasized
                    )
                },
                navigationIcon = {
                    // OBBLIGATORIO: Pulsante Back racchiuso in contenitore circolare surfaceContainerHigh (40.dp)
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
                            contentDescription = backContentDescription
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                scrollBehavior = scrollBehavior
            )
        },
        content = content
    )
}""",

    "tabs": """import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

// Tabs state-driven (compatibili con Navigation 3; MAI usare rememberNavController di Navigation 2)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = selectedTabIndex == index
            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = if (isSelected) {
                            MaterialTheme.typography.labelLargeEmphasized
                        } else {
                            MaterialTheme.typography.labelLarge
                        }
                    )
                }
            )
        }
    }
}""",

    "toolbars": """import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveFloatingToolbarExample(
    expanded: Boolean,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onPrimaryActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalFloatingToolbar(
        expanded = expanded,
        floatingActionButton = {
            FloatingToolbarDefaults.VibrantFloatingActionButton(
                onClick = onPrimaryActionClick
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        },
        modifier = modifier,
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    ) {
        IconButton(onClick = onEditClick) {
            Icon(Icons.Rounded.Edit, contentDescription = null)
        }
        IconButton(onClick = onShareClick) {
            Icon(Icons.Rounded.Share, contentDescription = null)
        }
    }
}""",

    "floating bar": """import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VibrantHorizontalFloatingToolbar(
    expanded: Boolean,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalFloatingToolbar(
        expanded = expanded,
        floatingActionButton = {
            FloatingToolbarDefaults.VibrantFloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        },
        modifier = modifier,
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    ) {
        IconButton(onClick = onSearchClick) {
            Icon(Icons.Rounded.Search, contentDescription = null)
        }
        IconButton(onClick = onFilterClick) {
            Icon(Icons.Rounded.FilterList, contentDescription = null)
        }
    }
}""",

    "navigation bar": """import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey

data class ClassicTopLevelDestination(
    val route: NavKey,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

// ARCHETIPO 1 — Stile Classico (3–5 destinazioni, surfaceContainerLow, Outlined vs Filled).
// Per l'ARCHETIPO 2 — Stile d'Impatto (Floating Pill Dock, max 4 destinazioni), usa ExpressiveImpactNavigationBar (SKILL.md Sez. 21.5).
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ClassicMultiLevelRootScaffold(
    destinations: List<ClassicTopLevelDestination>,
    currentRoute: NavKey,
    onNavigateToDestination: (ClassicTopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                destinations.forEach { destination ->
                    val isSelected = currentRoute == destination.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onNavigateToDestination(destination) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                style = if (isSelected) {
                                    MaterialTheme.typography.labelMediumEmphasized
                                } else {
                                    MaterialTheme.typography.labelMedium
                                }
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // REGOLA ZERO BLACK BOXES: passa SOLO il bottom padding allo screen/NavDisplay figlio!
        content(Modifier.padding(bottom = innerPadding.calculateBottomPadding()))
    }
}""",

    "navigation rail": """import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveWideNavigationRail(
    destinations: List<ClassicTopLevelDestination>,
    currentRoute: Any,
    onNavigate: (ClassicTopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val railState = rememberWideNavigationRailState()
    val isExpanded = railState.targetValue == WideNavigationRailValue.Expanded

    WideNavigationRail(
        state = railState,
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            val isSelected = currentRoute == destination.route
            WideNavigationRailItem(
                railExpanded = isExpanded,
                selected = isSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = if (isSelected) {
                            MaterialTheme.typography.labelLargeEmphasized
                        } else {
                            MaterialTheme.typography.labelLarge
                        }
                    )
                }
            )
        }
    }
}""",

    "common buttons": """import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 5 Configurazioni graduate di enfasi in Material 3 Expressive:
// 1. Button (Filled)      -> enfasi massima (primary / onPrimary), zero ombre artificiali
// 2. FilledTonalButton    -> enfasi media (secondaryContainer / onSecondaryContainer)
// 3. OutlinedButton       -> enfasi media-bassa (bordo outline)
// 4. TextButton           -> enfasi minima (azioni secondarie in Dialog)
// 5. ExpressivePrimaryActionButton -> pulsante fisso a fondo pagina con maschera gradiente (SKILL.md Sez. 21.6)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressivePrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLargeEmphasized
        )
    }
}""",

    "button groups": """import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Connected Button Group in M3 Expressive (sostituisce il deprecato SegmentedButton)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveConnectedButtonGroup(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, label ->
            val isChecked = selectedIndex == index
            ToggleButton(
                checked = isChecked,
                onCheckedChange = { onOptionSelected(index) },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = if (isChecked) {
                        MaterialTheme.typography.labelLargeEmphasized
                    } else {
                        MaterialTheme.typography.labelLarge
                    }
                )
            }
        }
    }
}""",

    "split button": """import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// SplitButtonLayout ufficiale M3 Expressive (MAI usare il nome inesistente SplitButton)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveSplitButton(
    primaryLabel: String,
    menuExpanded: Boolean,
    onPrimaryClick: () -> Unit,
    onMenuToggle: (Boolean) -> Unit,
    trailingContentDescription: String,
    modifier: Modifier = Modifier
) {
    SplitButtonLayout(
        modifier = modifier,
        leadingButton = {
            SplitButtonDefaults.LeadingButton(onClick = onPrimaryClick) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = primaryLabel,
                    style = MaterialTheme.typography.labelLargeEmphasized
                )
            }
        },
        trailingButton = {
            SplitButtonDefaults.TrailingButton(
                checked = menuExpanded,
                onCheckedChange = onMenuToggle
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = trailingContentDescription,
                    modifier = Modifier.size(SplitButtonDefaults.TrailingIconSize)
                )
            }
        }
    )
}""",

    "icon buttons": """import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Pulsante Back Circolare Obbligatorio per sottoschermate e dettagli (40.dp, surfaceContainerHigh)
@Composable
fun CircularBackIconButton(
    contentDescription: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onNavigateBack,
        modifier = modifier.size(40.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = contentDescription
        )
    }
}""",

    "floating action buttons (fabs)": """import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// ⚠️ REGOLA TASSATIVA DEI 3 FAB (MEDIUM DEFAULT & DIVIETO ASSOLUTO DI TESTO):
// 1. Standard FAB (56x56 dp): RoundedCornerShape(16.dp), icona 24.dp
// 2. Medium FAB (68x68 dp) [DEFAULT OBBLIGATORIO]: RoundedCornerShape(20.dp), icona 30.dp
// 3. Large FAB (96x96 dp) [Solo azioni cardine]: RoundedCornerShape(28.dp), icona 36.dp
// MAI inserire testo dentro un FAB. MAI usare CircleShape al 50%.
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SparkDefaultMediumFab(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Add
) {
    MediumFloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(68.dp),
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(30.dp)
        )
    }
}""",

    "extended floating action buttons (extended fabs)": """// 🚫 DIVIETO ASSOLUTO IN SPARK!:
// È severamente vietato utilizzare ExtendedFloatingActionButton con etichetta di testo.
// Ogni FAB deve contenere ESCLUSIVAMENTE l'icona centrata (usa MediumFloatingActionButton 68dp con RoundedCornerShape(20.dp)).
// Se una schermata secondaria/dettaglio richiede un pulsante con testo e icona in basso,
// usa ExpressivePrimaryActionButton (SKILL.md Sez. 21.6) con maschera gradiente verticale.""",

    "progress indicator": """import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// ⚠️ REGOLA ANTI-ALLUCINAZIONE:
// 1. Usa SEMPRE LinearWavyProgressIndicator e CircularWavyProgressIndicator (MAI scrivere WavyLinearProgressIndicator!).
// 2. Il parametro progress DEVE SEMPRE essere una lambda: progress = { floatValue }.
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveWavyProgressExamples(
    progressValue: Float,
    modifier: Modifier = Modifier
) {
    // Determinato lineare (con lambda)
    LinearWavyProgressIndicator(
        progress = { progressValue.coerceIn(0f, 1f) },
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
    )

    // Indeterminato circolare (senza parametro progress)
    CircularWavyProgressIndicator(
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
    )
}""",

    "loading indicator": """import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveMorphingLoadingIndicator(
    contained: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (contained) {
        ContainedLoadingIndicator(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    } else {
        LoadingIndicator(
            modifier = modifier,
            color = MaterialTheme.colorScheme.primary
        )
    }
}""",

    "search": """// Per l'implementazione completa della Pill Search Top Bar (72.dp esterni, pillola interna 56.dp in surfaceBright)
// e della vista full-screen obbligatoria al tap (AppSearchTopBar + AppSearchView con BackHandler, Clear e History),
// consulta il blueprint completo in SKILL.md -> Sezione 21.8 (AppSearchView).""",

    "lists": """// Per liste di voci correlate (impostazioni, transazioni, dispositivi), usa SEMPRE il pattern
// GroupedListCard + GroupedListRow (SKILL.md -> Sezione 21.7):
// - Card unificata RoundedCornerShape(20.dp) con sfondo surfaceContainerLow
// - Divisori interni HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
// - Icone a sinistra in squircle 40.dp (RoundedCornerShape(12.dp))
// - Per LazyColumn, fornisci sempre key = { it.id }, contentType e Modifier.consumeWindowInsets(innerPadding).""",

    "side sheets": """import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpressiveSideSheet(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(360.dp),
            shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                content = content
            )
        }
    }
}""",

    "motion scheme": """import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// In Material 3 Expressive:
// - Transizioni Spaziali (movimento, dimensione, forma): usa defaultSpatialSpec() o fastSpatialSpec() (molle fisiche con leggero overshoot)
// - Transizioni di Effetto (colore, alpha): usa defaultEffectsSpec() o fastEffectsSpec() (zero rimbalzo)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveMotionExample(isSelected: Boolean) {
    val spatialSpec = MaterialTheme.motionScheme.fastSpatialSpec<Dp>()
    val effectsSpec = MaterialTheme.motionScheme.fastEffectsSpec<Color>()

    val cornerRadius by animateDpAsState(
        targetValue = if (isSelected) 20.dp else 12.dp,
        animationSpec = spatialSpec,
        label = "cornerMorph"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        },
        animationSpec = effectsSpec,
        label = "tonalShift"
    )
}"""
}


def slugify(text: str) -> str:
    """Trasforma un titolo in slug per nomi file."""
    s = text.lower().strip()
    s = re.sub(r'[\(\)\&]', '', s)
    s = re.sub(r'[^a-z0-9\-]+', '-', s)
    return s.strip('-')


def format_signature(sig: str) -> str:
    """Formatta una firma composable in modo pulito e leggibile."""
    s = re.sub(r'[ \t]+', ' ', sig.strip())
    s = re.sub(r'^@Composable\s*fun\s+', '@Composable\nfun ', s)
    s = re.sub(r'fun\s+(\w+)\s*\(', r'fun \1(\n    ', s)
    s = re.sub(r',\s*([a-zA-Z0-9_]+:\s*)', r',\n    \1', s)
    s = re.sub(r'\)\s*$', r'\n)', s)
    return s


class CacheManager:
    """Gestione cache locale su disco per evitare richieste HTTP ripetute."""
    def __init__(self, cache_dir: Path):
        self.cache_dir = cache_dir
        self.cache_dir.mkdir(parents=True, exist_ok=True)

    def _get_key(self, url: str) -> Path:
        h = hashlib.sha256(url.encode('utf-8')).hexdigest()
        return self.cache_dir / f"{h}.cache"

    def get(self, url: str) -> str | None:
        p = self._get_key(url)
        if p.exists():
            try:
                with open(p, 'r', encoding='utf-8') as f:
                    data = json.load(f)
                    return data.get("content")
            except Exception:
                return None
        return None

    def set(self, url: str, content: str):
        p = self._get_key(url)
        try:
            with open(p, 'w', encoding='utf-8') as f:
                json.dump({"url": url, "timestamp": time.time(), "content": content}, f, ensure_ascii=False)
        except Exception as e:
            print(f"  [!] Impossibile salvare cache: {e}", flush=True)

    def clear(self):
        for f in self.cache_dir.glob("*.cache"):
            f.unlink()


class Downloader:
    """Downloader HTTP con gestione trasparente di redirect, cache locale e modalità offline."""
    def __init__(self, cache: CacheManager, bypass_cache: bool = False, offline: bool = False):
        self.cache = cache
        self.bypass_cache = bypass_cache
        self.offline = offline

    def fetch(self, url: str) -> str | None:
        target_url = url.split('#')[0]

        if 'developer.android.com' in target_url and 'hl=' not in target_url:
            target_url += '&hl=en' if '?' in target_url else '?hl=en'
        elif 'github.com' in target_url and '/blob/' in target_url:
            target_url = target_url.replace('github.com', 'raw.githubusercontent.com').replace('/blob/', '/')

        if not self.bypass_cache:
            cached = self.cache.get(target_url)
            if cached is not None:
                return cached

        if self.offline:
            return None

        cmd = [
            "curl",
            "-sL",
            "--max-time", "15",
            "-A", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko)",
            target_url
        ]

        try:
            res = subprocess.run(cmd, capture_output=True, text=True, timeout=20)
            if res.returncode == 0 and res.stdout.strip():
                content = res.stdout
                self.cache.set(target_url, content)
                return content
            else:
                print(f"  [i] Download non disponibile per {target_url} (codice {res.returncode}), uso fallback locale.", flush=True)
        except Exception as e:
            print(f"  [i] Rete non disponibile per {target_url} ({e}), uso fallback locale.", flush=True)

        return None


class DevSiteHTMLCleaner(HTMLParser):
    """Estrae testo esplicativo, titoli, firme e snippet di codice puliti."""
    def __init__(self):
        super().__init__()
        self.output_parts = []
        self.code_blocks = []
        self.signatures = []
        self.ignore_depth = 0
        self.in_pre = False
        self.current_pre = []
        self.in_heading = False
        self.current_heading_level = 2
        self.current_heading = []

    def handle_starttag(self, tag, attrs):
        attrs_dict = dict(attrs)
        tag_lower = tag.lower()

        if tag_lower in ('script', 'style', 'devsite-page-rating', 'devsite-feedback',
                         'devsite-bookmark', 'devsite-header', 'devsite-footer', 'nav'):
            self.ignore_depth += 1
            return

        cls = attrs_dict.get('class', '')
        if 'devsite-nav' in cls or 'devsite-banner' in cls or 'devsite-github-link' in cls:
            self.ignore_depth += 1
            return

        if self.ignore_depth > 0:
            return

        if tag_lower in ('h1', 'h2', 'h3', 'h4', 'h5', 'h6'):
            self.in_heading = True
            self.current_heading_level = int(tag_lower[1])
            self.current_heading = []
        elif tag_lower == 'pre':
            self.in_pre = True
            self.current_pre = []
        elif tag_lower == 'p':
            self.output_parts.append('\n\n')
        elif tag_lower == 'li':
            self.output_parts.append('\n* ')
        elif tag_lower == 'code' and not self.in_pre:
            self.output_parts.append('`')
        elif tag_lower == 'blockquote':
            self.output_parts.append('\n\n> ')

    def handle_endtag(self, tag):
        tag_lower = tag.lower()
        if tag_lower in ('script', 'style', 'devsite-page-rating', 'devsite-feedback',
                         'devsite-bookmark', 'devsite-header', 'devsite-footer', 'nav'):
            self.ignore_depth = max(0, self.ignore_depth - 1)
            return

        if self.ignore_depth > 0:
            return

        if self.in_heading and tag_lower.startswith('h'):
            self.in_heading = False
            htext = ''.join(self.current_heading).strip()
            if htext and not any(skip in htext.lower() for skip in ['additional resources', 'feedback', 'more android']):
                self.output_parts.append(f"\n\n{'#' * self.current_heading_level} {htext}\n\n")
            self.current_heading = []
        elif tag_lower == 'pre':
            self.in_pre = False
            code_str = html.unescape(''.join(self.current_pre)).replace('\xa0', ' ').strip()
            code_str = re.sub(r'\s*\b[A-Za-z0-9_]+\.(?:kt|java)\s*$', '', code_str).strip()
            if code_str:
                self._classify_and_store_code(code_str)
                self.output_parts.append(f"\n\n```kotlin\n{code_str}\n```\n\n")
            self.current_pre = []
        elif tag_lower == 'code' and not self.in_pre:
            self.output_parts.append('`')

    def _classify_and_store_code(self, code: str):
        trimmed = code.strip()
        trimmed_norm = re.sub(r'@Composable\s*fun', '@Composable fun', trimmed)

        is_signature = (
            bool(re.match(r'^\s*@?Composable\s+fun\s+[A-Z]\w*\s*\(', trimmed_norm)) and
            not any(ex in trimmed_norm for ex in ['Example', 'Showcase', 'Preview', 'Screen']) and
            'import ' not in trimmed_norm
        )

        if is_signature:
            self.signatures.append(trimmed_norm)
        else:
            self.code_blocks.append(trimmed_norm)

    def handle_data(self, data):
        if self.ignore_depth > 0:
            return
        if self.in_pre:
            self.current_pre.append(data)
        elif self.in_heading:
            self.current_heading.append(data)
        else:
            self.output_parts.append(data)

    def get_markdown(self) -> str:
        text = ''.join(self.output_parts)
        text = re.sub(r'\n{3,}', '\n\n', text)
        return text.strip()


def parse_ui_components_file(filepath: Path) -> list[dict]:
    """Legge 'ui-components.md' e genera una lista ordinata e deduplicata di componenti con i rispettivi link."""
    if not filepath.exists():
        raise FileNotFoundError(f"File non trovato: {filepath}")

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    lines = content.split('\n')
    components = []
    current_h3 = ""
    current_item = None

    for line in lines:
        line_s = line.strip()
        if line_s.startswith('### '):
            if current_item:
                components.append(current_item)
            title = line_s.replace('### ', '').strip()
            current_h3 = title
            current_item = {
                "name": title,
                "parent": "",
                "category": CATEGORY_MAP.get(title.lower(), "containment-surfaces"),
                "updates": [],
                "notes": [],
                "links": []
            }
        elif line_s.startswith('#### '):
            if current_item:
                components.append(current_item)
            title = line_s.replace('#### ', '').strip()
            category = CATEGORY_MAP.get(title.lower(), CATEGORY_MAP.get(current_h3.lower(), "containment-surfaces"))
            current_item = {
                "name": title,
                "parent": current_h3,
                "category": category,
                "updates": [],
                "notes": [],
                "links": []
            }
        elif current_item:
            named_link_m = re.match(r'^\*\*([A-Za-z0-9_\- \(\)]+)\*\*:\s*(https?://[^\s\)]+)', line_s)
            if named_link_m and current_h3.lower() in ["buttons"]:
                sub_label, sub_url = named_link_m.groups()
                # Se il sub_label coincide con il current_item (es. "Button groups"), aggiungi il link senza duplicare il componente
                if sub_label.strip().lower() == current_item["name"].strip().lower():
                    current_item["links"].append({"label": sub_label, "url": sub_url})
                    continue
                sub_comp = {
                    "name": sub_label,
                    "parent": current_h3,
                    "category": "actions",
                    "updates": [],
                    "notes": [],
                    "links": [{"label": sub_label, "url": sub_url}]
                }
                components.append(sub_comp)
                continue

            if line_s.startswith('**Update') or '**Updates**:' in line_s:
                current_item["updates"].append(line_s)
            elif any(line_s.startswith(p) for p in [
                '**Remember**:', '**Preferenze**:', '**Old**:', '**Toggle button groups**:',
                '**Floating**:', '**Docked**:', '**M3**:', '**Linear**:', '**Circular**:'
            ]):
                current_item["notes"].append(line_s)

            urls = re.findall(r'https?://[^\s\)\>]+', line_s)
            for u in urls:
                label = "Doc"
                if line_s.startswith('**') and ':' in line_s:
                    label = line_s.split(':')[0].replace('*', '').strip()
                elif 'Link' in line_s:
                    label = "Android Developers"
                current_item["links"].append({"label": label, "url": u})

    if current_item:
        components.append(current_item)

    # Filtra contenitori padre vuoti e deduplica per slug
    result = []
    seen_slugs = set()
    for c in components:
        if c["links"] or c["updates"] or c["notes"]:
            s = slugify(c["name"])
            if s not in seen_slugs:
                seen_slugs.add(s)
                result.append(c)

    return result


def extract_content_from_html(raw_html: str, url: str) -> dict:
    """Estrae testo pulito, firme Kotlin e composable code snippets da una pagina HTML."""
    result = {
        "title": "",
        "markdown": "",
        "code_blocks": [],
        "signatures": []
    }

    article_match = re.search(r'<div class=\"devsite-article-body[^\"]*\">(.*?)</div>\s*</article>', raw_html, re.DOTALL)
    if not article_match:
        article_match = re.search(r'<devsite-content>(.*?)</devsite-content>', raw_html, re.DOTALL)

    body_html = article_match.group(1) if article_match else raw_html

    body_html = re.sub(r'<div class="devsite-github-link.*?</div>', '', body_html, flags=re.DOTALL)
    body_html = re.sub(r'<devsite-page-rating.*?</devsite-page-rating>', '', body_html, flags=re.DOTALL)
    body_html = re.sub(r'<devsite-feature-tooltip.*?</devsite-feature-tooltip>', '', body_html, flags=re.DOTALL)
    body_html = re.sub(r'<devsite-heading id=\"additional_resources\".*$', '', body_html, flags=re.DOTALL)

    cleaner = DevSiteHTMLCleaner()
    cleaner.feed(body_html)

    result["markdown"] = cleaner.get_markdown()
    result["code_blocks"] = cleaner.code_blocks
    result["signatures"] = cleaner.signatures

    title_m = re.search(r'<title>(.*?)</title>', raw_html, re.DOTALL)
    if title_m:
        raw_title = title_m.group(1).split('|')[0].replace('&nbsp;', ' ').strip()
        result["title"] = raw_title

    return result


def extract_content_from_github_md(raw_md: str) -> dict:
    """Pulisce la documentazione markdown da repository GitHub (es. MDC-Android)."""
    clean_md = re.sub(r'<!--docs:.*?-->', '', raw_md, flags=re.DOTALL).strip()
    clean_md = re.sub(r'!\[.*?\]\(assets/.*?\)', '', clean_md)
    code_blocks = re.findall(r'```(?:kotlin|java|xml)?\n(.*?)```', clean_md, flags=re.DOTALL)

    return {
        "title": "",
        "markdown": clean_md,
        "code_blocks": [c.strip() for c in code_blocks if c.strip()],
        "signatures": []
    }


def extract_existing_component_fallback(existing_file: Path) -> dict | None:
    """Legge la scheda componente già presente su disco per preservare overview, firme e codice quando offline."""
    if not existing_file.exists():
        return None
    try:
        text = existing_file.read_text(encoding='utf-8')
    except Exception:
        return None

    signatures = []
    code_blocks = []

    sig_match = re.search(r'### Firme Composable Principali\s*```kotlin\n(.*?)```', text, re.DOTALL)
    if sig_match:
        signatures.append(sig_match.group(1).strip())

    code_match = re.search(r'### Esempio Jetpack Compose[^\n]*\s*```kotlin\n(.*?)```', text, re.DOTALL)
    if code_match:
        code_blocks.append(code_match.group(1).strip())

    # Estrai paragrafo descrittivo pulito
    overview_lines = []
    in_code = False
    for line in text.split('\n'):
        ls = line.strip()
        if ls.startswith('```'):
            in_code = not in_code
            continue
        if in_code:
            continue
        if (not ls or ls.startswith('#') or ls.startswith('>') or ls.startswith('*')
                or ls.startswith('---') or ls.startswith('**Categoria:')
                or ls.startswith('**Sezione Principale:')
                or ls.startswith('`Unit`') or '@Composable' in ls or '\xa0' in ls):
            continue
        if len(ls) > 40:
            overview_lines.append(ls)

    return {
        "title": "",
        "markdown": "\n\n".join(overview_lines[:2]),
        "code_blocks": code_blocks,
        "signatures": signatures
    }


def is_clean_overview_paragraph(p: str) -> bool:
    """Filtra frammenti sporchi di tabelle KDoc (es. `Unit` @ComposableLinearProgressIndicator)."""
    ps = p.strip()
    if not ps or len(ps) <= 50:
        return False
    if ps.startswith(('#', '```', '*', '>', '@Composable', '`Unit`', 'Unit', 'Modifier')):
        return False
    if '@Composable' in ps or '\xa0' in ps or 'You can also set the sheet edge with XML' in ps:
        return False
    return True


def format_component_section(comp: dict, downloaded_docs: list[dict], index: int) -> str:
    """Formatta una sezione completa di un componente nel formato curato M3 Expressive."""
    title = comp["name"]
    parent = comp.get("parent")
    full_title = f"{parent} - {title}" if parent and parent.lower() not in title.lower() else title

    lines = [f"## {index}. {full_title}\n"]

    # Callout aggiornamenti M3 Expressive
    if comp.get("updates"):
        lines.append("> [!IMPORTANT]")
        for u in comp["updates"]:
            clean_u = re.sub(r'^\*\*Updates?\*\*:\s*', '', u)
            lines.append(f"> **M3 Expressive Update:** {clean_u}")
        lines.append("")

    # Note di design specifiche
    if comp.get("notes"):
        for n in comp["notes"]:
            lines.append(f"> **Linee Guida di Design:** {n}")
        lines.append("")

    all_code = []
    all_signatures = []
    overview_text_snippets = []

    for doc in downloaded_docs:
        if doc.get("signatures"):
            all_signatures.extend(doc["signatures"])
        if doc.get("code_blocks"):
            all_code.extend(doc["code_blocks"])

        md = doc.get("markdown", "")
        paragraphs = [
            p.strip() for p in md.split('\n\n')
            if is_clean_overview_paragraph(p)
        ]
        if paragraphs:
            overview_text_snippets.extend(paragraphs[:2])

    if overview_text_snippets:
        lines.append(overview_text_snippets[0])
        lines.append("")

    # Firme Composable Principali
    if all_signatures:
        lines.append("### Firme Composable Principali")
        lines.append("```kotlin")
        for sig in all_signatures[:2]:
            lines.append(format_signature(sig))
            lines.append("")
        lines.append("```\n")

    # Esempi Jetpack Compose: priorità assoluta agli snippet curati M3 Expressive + Navigation 3 + Spark!
    compose_code = None
    slug_key = comp["name"].lower().strip()
    if slug_key in COMPOSE_M3_EXPRESSIVE_SNIPPETS:
        compose_code = COMPOSE_M3_EXPRESSIVE_SNIPPETS[slug_key]
    elif all_code:
        for code in all_code:
            if "@Composable" in code and "rememberNavController" not in code and ("Scaffold" in code or "Column" in code or "Row" in code or "Button" in code):
                if compose_code is None or len(code) > len(compose_code):
                    compose_code = code

        if not compose_code and all_code:
            non_xml = [
                c for c in all_code
                if not c.startswith('<')
                and "rememberNavController" not in c
                and "You can also set the sheet edge with XML" not in c
            ]
            if non_xml:
                compose_code = non_xml[0]

    if compose_code:
        lines.append("### Esempio Jetpack Compose (M3 Expressive)")
        lines.append("```kotlin")
        lines.append(compose_code.strip())
        lines.append("```\n")

    # Link ufficiali
    if comp.get("links"):
        lines.append("### Riferimenti Ufficiali")
        for l in comp["links"]:
            label = l.get("label", "Documentazione")
            url = l.get("url")
            lines.append(f"* **{label}:** [{url}]({url})")
        lines.append("")

    lines.append("---\n")
    return '\n'.join(lines)


def format_category_header(cat_name: str, cat_meta: dict) -> str:
    """Genera l'intestazione del file di categoria con Decision Tree e link a official Google skills."""
    lines = [
        f"# Material 3 Expressive - {cat_meta['title']}\n",
        f"{cat_meta['desc']}\n",
        "> **Riferimenti Locali:** Per la scheda dettagliata di ogni singolo componente, consulta i file dedicati in `references/components/`. Per i blueprint completi di progetto (`BentoHeroCard`, `QuickMetricsBentoStrip`, `ExpressiveImpactNavigationBar`, `ExpressivePrimaryActionButton`, `GroupedListCard`, `AppSearchView`), consulta [`../SKILL.md`](../SKILL.md) (Sezione 21).\n"
    ]

    # Decision Tree della categoria
    decision_tree = cat_meta.get("decision_tree", [])
    if decision_tree:
        lines.append("## Decision Tree Rapido (Quando usare cosa)\n")
        lines.append("| Caso d'Uso / Intento UI | Componente Ufficiale M3 Expressive / Spark! | Note Critiche & Vincoli |")
        lines.append("| :--- | :--- | :--- |")
        for use_case, comp_api, notes in decision_tree:
            lines.append(f"| **{use_case}** | {comp_api} | {notes} |")
        lines.append("")

    # Link diretti a official Google skills
    skills_links = cat_meta.get("skills_main", [])
    if skills_links:
        lines.append("## Integrazione con `.agents/skills/` (Progressive Disclosure)\n")
        for label, rel_path in skills_links:
            lines.append(f"* **{label}:** [`{rel_path}`]({rel_path})")
        lines.append("")

    lines.append("---\n")
    return '\n'.join(lines)


def process_references(
    ui_components_path: Path,
    references_dir: Path,
    bypass_cache: bool = False,
    category_filter: str | None = None,
    offline: bool = False
):
    """Flusso principale di scaricamento, elaborazione e scrittura delle referenze."""
    print(f"[*] Lettura componenti da: {ui_components_path}", flush=True)
    components = parse_ui_components_file(ui_components_path)
    print(f"[✓] Trovati {len(components)} componenti univoci con specifiche e link.", flush=True)

    cache_dir = ui_components_path.parent / ".cache"
    cache = CacheManager(cache_dir)
    downloader = Downloader(cache, bypass_cache=bypass_cache, offline=offline)

    components_dir = references_dir / "components"
    components_dir.mkdir(parents=True, exist_ok=True)

    categories: dict[str, list[dict]] = {k: [] for k in CATEGORY_METADATA.keys()}
    for c in components:
        cat = c["category"]
        if cat not in categories:
            cat = "containment-surfaces"
        categories[cat].append(c)

    selected_categories = categories.items()
    if category_filter:
        if category_filter not in categories:
            print(f"[X] Categoria '{category_filter}' non valida. Opzioni: {list(categories.keys())}", flush=True)
            sys.exit(1)
        selected_categories = [(category_filter, categories[category_filter])]

    for cat_name, cat_components in selected_categories:
        cat_meta = CATEGORY_METADATA[cat_name]
        print(f"\n==========================================", flush=True)
        print(f"[*] Elaborazione Categoria: {cat_meta['title']} ({len(cat_components)} componenti)", flush=True)
        print(f"==========================================", flush=True)

        category_sections = []
        cat_index = 1

        for comp in cat_components:
            comp_name = comp["name"]
            slug = slugify(comp_name)
            individual_file = components_dir / f"{slug}.md"
            print(f"\n  -> [{cat_index}/{len(cat_components)}] Componente: {comp_name}", flush=True)

            downloaded_docs = []
            for link_info in comp.get("links", []):
                url = link_info["url"]
                print(f"     [+] Elaborazione: {url}", flush=True)
                raw_content = downloader.fetch(url)
                if not raw_content:
                    continue

                if "github.com" in url or url.endswith(".md"):
                    parsed_doc = extract_content_from_github_md(raw_content)
                else:
                    parsed_doc = extract_content_from_html(raw_content, url)

                downloaded_docs.append(parsed_doc)

            # Fallback offline: se non è stato scaricato nulla da rete/cache, preserva i dati dalla scheda esistente
            if not downloaded_docs:
                fallback_doc = extract_existing_component_fallback(individual_file)
                if fallback_doc:
                    downloaded_docs.append(fallback_doc)

            section_md = format_component_section(comp, downloaded_docs, cat_index)
            category_sections.append(section_md)

            # Scheda individuale del componente
            individual_content = [
                f"# Material 3 Expressive - {comp_name}\n",
                f"**Categoria:** `{cat_name}`  ",
                f"**Sezione Principale:** [{cat_meta['title']}](../{cat_name}.md)\n",
                "---\n",
                section_md
            ]
            with open(individual_file, 'w', encoding='utf-8') as f:
                f.write('\n'.join(individual_content))

            cat_index += 1

        # Sezione aggiuntiva per Containment & Surfaces con rimando esplicito ai componenti custom Spark!
        if cat_name == "containment-surfaces":
            spark_custom_section = (
                f"## {cat_index}. Spark Custom Components (`GroupedListCard` & `AppSearchView`)\n\n"
                "Per l'implementazione completa e pronta all'uso dei contenitori a blocchi unificati "
                "(`GroupedListCard` + `GroupedListRow`) e della barra di ricerca a pillola in `surfaceBright` con "
                "espansione full-screen obbligatoria (`AppSearchTopBar` + `AppSearchView`), consulta direttamente "
                "le sezioni **21.7** e **21.8** in [`../SKILL.md`](../SKILL.md).\n\n---\n"
            )
            category_sections.append(spark_custom_section)

        # File consolidato della categoria
        category_file = references_dir / f"{cat_name}.md"
        cat_doc_parts = [
            format_category_header(cat_name, cat_meta),
            '\n'.join(category_sections)
        ]

        with open(category_file, 'w', encoding='utf-8') as f:
            f.write('\n'.join(cat_doc_parts))
        print(f"\n  [✓] File categoria generato: {category_file}", flush=True)

    # Indice generale navigabile README.md
    readme_path = references_dir / "README.md"
    readme_lines = [
        "# Material 3 Expressive - Documentazione Locale Offline (References)\n",
        "Questo archivio contiene le linee guida ufficiali, Decision Trees, firme Composable, token e snippet di codice pronti all'uso per Jetpack Compose conformi a **Material 3 Expressive**, **Jetpack Navigation 3** e agli standard **Spark!**.\n",
        "Tutti i dati sono generati ed aggiornati automaticamente tramite lo script `scripts/download_references.py` a partire da `scripts/ui-components.md`.\n",
        "## Categorie Principali\n",
        "| Categoria | Descrizione | File di Riferimento | Componenti Inclusi |",
        "| :--- | :--- | :--- | :--- |"
    ]

    for cat_name, cat_meta in CATEGORY_METADATA.items():
        comp_names = ", ".join([c["name"] for c in categories.get(cat_name, [])])
        readme_lines.append(f"| **{cat_meta['title']}** | {cat_meta['desc']} | [{cat_name}.md](./{cat_name}.md) | {comp_names} |")

    readme_lines.append("\n## Schede Componenti Individuali\n")
    readme_lines.append("Per consultare direttamente le specifiche e i codici di un singolo elemento:\n")
    for comp in sorted(components, key=lambda x: x["name"]):
        s = slugify(comp["name"])
        readme_lines.append(f"* [{comp['name']}](./components/{s}.md) (`{comp['category']}`)")

    with open(readme_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(readme_lines))

    print(f"\n==========================================", flush=True)
    print(f"[✓] Documentazione references completata con successo!", flush=True)
    print(f"[✓] File di categoria aggiornati in: {references_dir}", flush=True)
    print(f"[✓] {len(components)} schede componenti individuali create in: {components_dir}", flush=True)
    print(f"[✓] Indice generale generato in: {readme_path}", flush=True)
    print(f"==========================================", flush=True)


def main():
    parser = argparse.ArgumentParser(description="Scarica e organizza la documentazione di UI Components per M3 Expressive")
    parser.add_argument(
        "--input", "-i",
        type=Path,
        default=Path(__file__).parent / "ui-components.md",
        help="Percorso al file markdown sorgente (default: scripts/ui-components.md)"
    )
    parser.add_argument(
        "--output-dir", "-o",
        type=Path,
        default=Path(__file__).parent.parent / "references",
        help="Directory di destinazione per le referenze (default: ../references)"
    )
    parser.add_argument(
        "--category", "-c",
        type=str,
        default=None,
        help="Scarica/aggiorna solo una categoria specifica (navigation, actions, inputs, containment-surfaces, display-feedback)"
    )
    parser.add_argument(
        "--force", "-f",
        action="store_true",
        help="Bypassa la cache locale e riscarica tutte le pagine dal web"
    )
    parser.add_argument(
        "--offline",
        action="store_true",
        help="Rigenera tutti i file usando la cache locale e le schede esistenti su disco senza effettuare chiamate di rete"
    )
    parser.add_argument(
        "--clean-cache",
        action="store_true",
        help="Cancella tutti i file nella cache locale ed esce"
    )

    args = parser.parse_args()

    if args.clean_cache:
        cache_dir = args.input.parent / ".cache"
        CacheManager(cache_dir).clear()
        print("[✓] Cache locale pulita con successo.", flush=True)
        return

    process_references(
        ui_components_path=args.input,
        references_dir=args.output_dir,
        bypass_cache=args.force,
        category_filter=args.category,
        offline=args.offline
    )


if __name__ == "__main__":
    main()
