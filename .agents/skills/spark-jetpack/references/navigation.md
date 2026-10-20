# Material 3 Expressive - Navigation & App Bars

This document collects the Jetpack Compose signatures, parameters, variants and patterns for navigation components (TopAppBars, Toolbars, NavBars, WideNavigationRail, Drawer, Tabs), compliant with Material 3 Expressive.

> **Local references:** for the detailed card of each component, see the dedicated files in `references/components/`.

---

## 1. App bars

App bars are containers that provide the user access to key features and
navigation items. There are two types of app bars, top app bars and bottom app
bars. Their respective appearance and purpose are as follows:

### Esempio Jetpack Compose
```kotlin
@Composable fun BottomAppBarExample() {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Filled.Check, contentDescription = "Localized description")
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Mic,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = "Localized description",
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* do something */ },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                }
            )
        },
    ) { innerPadding ->
        Text(
            modifier = Modifier.padding(innerPadding),
            text = "Example of a scaffold with a bottom app bar."
        )
    }
}
```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/app-bars?hl=en](https://developer.android.com/develop/ui/compose/components/app-bars?hl=en)

---

## 2. Tabs

Tabs allow you to organize groups of related content. There are two types of
tabs:

### Esempio Jetpack Compose
```kotlin
@Composable fun NavigationTabExample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.SONGS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(modifier = modifier) { contentPadding ->
        PrimaryTabRow(selectedTabIndex = selectedDestination, modifier = Modifier.padding(contentPadding)) {
            Destination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        navController.navigate(route = destination.route)
                        selectedDestination = index
                    },
                    text = {
                        Text(
                            text = destination.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        AppNavHost(navController, startDestination)
    }
}
```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/tabs](https://developer.android.com/develop/ui/compose/components/tabs)

---

## 3. Toolbars

> [!IMPORTANT]
> **M3 Expressive Update:** The bottom app bar is no longer recommended and should be replaced with the docked toolbar, which functions similarly, but is shorter and has more flexibility. The floating toolbar was created for more versatility, greater amounts of actions, and more variety in where it's placed. Added floating toolbar with the following configurations (Layout: Horizontal or vertical, Color: Standard or vibrant, Flexibility: Can hold many elements and components. Can be paired with FAB)

`Unit`
              
                `@ComposableFlexibleBottomAppBar(    modifier: Modifier,    containerColor: Color,    contentColor: Color,    contentPadding: PaddingValues,    horizontalArrangement: Arrangement.Horizontal,    expandedHeight: Dp,    windowInsets: WindowInsets,    scrollBehavior: BottomAppBarScrollBehavior?,    content: @Composable RowScope.() -> Unit)`

### Main composable signatures
```kotlin
@Composable
fun FlexibleBottomAppBar(
     modifier: Modifier = Modifier,
    containerColor: Color = BottomAppBarDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    contentPadding: PaddingValues = BottomAppBarDefaults.FlexibleContentPadding,
    horizontalArrangement: Arrangement.Horizontal = BottomAppBarDefaults.FlexibleHorizontalArrangement,
    expandedHeight: Dp = BottomAppBarDefaults.FlexibleBottomAppBarHeight,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
    scrollBehavior: BottomAppBarScrollBehavior? = null,
    content: @Composable RowScope.() -> Unit): Unit

@Composable
fun HorizontalFloatingToolbar(
     expanded: Boolean,
    modifier: Modifier = Modifier,
    colors: FloatingToolbarColors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
    contentPadding: PaddingValues = FloatingToolbarDefaults.ContentPadding,
    scrollBehavior: FloatingToolbarScrollBehavior? = null,
    shape: Shape = FloatingToolbarDefaults.ContainerShape,
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
    expandedShadowElevation: Dp = FloatingToolbarDefaults.ContainerExpandedElevation,
    collapsedShadowElevation: Dp = FloatingToolbarDefaults.ContainerCollapsedElevation,
    content: @Composable RowScope.() -> Unit): Unit

```

### Esempio Jetpack Compose
```kotlin
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

val icons =
    listOf(
        Icons.AutoMirrored.Filled.ArrowBack,
        Icons.AutoMirrored.Filled.ArrowForward,
        Icons.Filled.Add,
        Icons.Filled.Check,
        Icons.Filled.Edit,
        Icons.Filled.Favorite,
    )
val items = listOf("Back", "Forward", "Add", "Check", "Edit", "Favorite")
FlexibleBottomAppBar(
    contentPadding = PaddingValues(horizontal = 96.dp),
    horizontalArrangement = BottomAppBarDefaults.FlexibleFixedHorizontalArrangement,
) {
    AppBarRow(
        overflowIndicator = { menuState ->
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above
                    ),
                tooltip = {
                    PlainTooltip(
                        modifier =
                            Modifier.semantics {
                                // TODO(b/496338253): Remove this modifier once bug where
                                // tooltip text
                                // is not announced
                                //  by a11y screen readers is resolved.
                                liveRegion = LiveRegionMode.Assertive
                                paneTitle = "Overflow"
                            }
                    ) {
                        Text("Overflow")
                    }
                },
                state = rememberTooltipState(),
            ) {
                IconButton(
                    onClick = {
                        if (menuState.isShowing) {
                            menuState.dismiss()
                        } else {
                            menuState.show()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Overflow")
                }
            }
        }
    ) {
        items.forEachIndexed { index, item ->
            clickableItem(
                onClick = { /* doSomething() */ },
                icon = { Icon(icons[index], contentDescription = item) },
                label = item,
            )
        }
    }
}
```

### Official references
* **FlexibleBottomAppBar:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/FlexibleBottomAppBar.composable](https://developer.android.com/reference/kotlin/androidx/compose/material3/FlexibleBottomAppBar.composable)
* **HorizontalFloatingToolbar:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/HorizontalFloatingToolbar.composable](https://developer.android.com/reference/kotlin/androidx/compose/material3/HorizontalFloatingToolbar.composable)

---

## 4. Navigation - Floating bar

`Unit`
              
                `@ComposableHorizontalFloatingToolbar(    expanded: Boolean,    modifier: Modifier,    colors: FloatingToolbarColors,    contentPadding: PaddingValues,    scrollBehavior: FloatingToolbarScrollBehavior?,    shape: Shape,    leadingContent: (@Composable RowScope.() -> Unit)?,    trailingContent: (@Composable RowScope.() -> Unit)?,    expandedShadowElevation: Dp,    collapsedShadowElevation: Dp,    content: @Composable RowScope.() -> Unit)`

### Main composable signatures
```kotlin
@Composable
fun HorizontalFloatingToolbar(
     expanded: Boolean,
    modifier: Modifier = Modifier,
    colors: FloatingToolbarColors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
    contentPadding: PaddingValues = FloatingToolbarDefaults.ContentPadding,
    scrollBehavior: FloatingToolbarScrollBehavior? = null,
    shape: Shape = FloatingToolbarDefaults.ContainerShape,
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
    expandedShadowElevation: Dp = FloatingToolbarDefaults.ContainerExpandedElevation,
    collapsedShadowElevation: Dp = FloatingToolbarDefaults.ContainerCollapsedElevation,
    content: @Composable RowScope.() -> Unit): Unit

@Composable
fun HorizontalFloatingToolbar(
     expanded: Boolean,
    floatingActionButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    colors: FloatingToolbarColors = FloatingToolbarDefaults.standardFloatingToolbarColors(),
    contentPadding: PaddingValues = FloatingToolbarDefaults.ContentPadding,
    scrollBehavior: FloatingToolbarScrollBehavior? = null,
    shape: Shape = FloatingToolbarDefaults.ContainerShape,
    floatingActionButtonPosition: FloatingToolbarHorizontalFabPosition = FloatingToolbarHorizontalFabPosition.End,
    animationSpec: FiniteAnimationSpec<Float> = FloatingToolbarDefaults.animationSpec(),
    expandedShadowElevation: Dp = FloatingToolbarDefaults.ContainerExpandedElevationWithFab,
    collapsedShadowElevation: Dp = FloatingToolbarDefaults.ContainerCollapsedElevationWithFab,
    content: @Composable RowScope.() -> Unit): Unit

```

### Esempio Jetpack Compose
```kotlin
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

var expanded by rememberSaveable { mutableStateOf(true) }
Scaffold(
    content = { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            // The toolbar should receive focus before the screen content, so place it first.
            // Make sure to set its zIndex so it's above the screen content visually.
            HorizontalFloatingToolbar(
                modifier =
                    Modifier.align(Alignment.BottomCenter).offset(y = -ScreenOffset).zIndex(1f),
                expanded = expanded,
                leadingContent = { LeadingContent() },
                trailingContent = { TrailingContent() },
                content = {
                    TooltipBox(
                        positionProvider =
                            TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                        tooltip = {
                            PlainTooltip(
                                modifier =
                                    Modifier.semantics {
                                        // TODO(b/496338253): Remove this modifier once bug
                                        //  where tooltip text is not announced by a11y screen
                                        //  readers is resolved.
                                        liveRegion = LiveRegionMode.Assertive
                                        paneTitle = "Localized description"
                                    }
                            ) {
                                Text("Localized description")
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        FilledIconButton(
                            modifier = Modifier.width(64.dp),
                            onClick = { /* doSomething() */ },
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Localized description")
                        }
                    }
                },
            )
            LazyColumn(
                // Apply a floatingToolbarVerticalNestedScroll Modifier toggle the expanded
                // state of the HorizontalFloatingToolbar.
                modifier =
                    Modifier.floatingToolbarVerticalNestedScroll(
                        expanded = expanded,
                        onExpand = { expanded = true },
                        onCollapse = { expanded = false },
                    ),
                state = rememberLazyListState(),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val list = (0..75).map { it.toString() }
                items(count = list.size) {
                    Text(
                        text = list[it],
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
)
```

### Official references
* **Link:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/HorizontalFloatingToolbar.composable](https://developer.android.com/reference/kotlin/androidx/compose/material3/HorizontalFloatingToolbar.composable)

---

## 5. Navigation bar

The navigation bar allows users to switch between destinations in an app. You
should use navigation bars for:

### Esempio Jetpack Compose
```kotlin
@Composable fun NavigationBarExample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.SONGS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(navController, startDestination, modifier = Modifier.padding(contentPadding))
    }
}
```

### Pattern Google Photos (Icone Outlined/Filled + Tipografia Google Sans)
Nei layout M3 di riferimento come Google Foto:
* **State-reactive icons:** when an item is inactive, use the Outlined icon (for example `Icons.Outlined.Photo`); when it is selected, use the Filled version (for example `Icons.Filled.Photo`) inside the tonal pill indicator.
* **Compact indicator:** the M3 elliptical indicator must wrap the icon with soft contrast (`secondaryContainer`).
* **Compact typography:** use Google Sans `labelMedium` (about 12sp, medium weight) to keep items scannable without vertical bulk.

```kotlin
NavigationBarItem(
    selected = isSelected,
    onClick = { onSelect() },
    icon = {
        Icon(
            imageVector = if (isSelected) Icons.Filled.Photo else Icons.Outlined.Photo,
            contentDescription = destination.label
        )
    },
    label = {
        Text(
            text = destination.label,
            style = MaterialTheme.typography.labelMedium
        )
    },
    colors = NavigationBarItemDefaults.colors(
        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedTextColor = MaterialTheme.colorScheme.onSurface,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
)
```

### Floating Pill Dock Navigation Bar (Impact style, max 4 items)
For apps that use the **Impact style** (Expressive Dynamic), the bottom navigation bar uses the floating pill dock:
* **Icon visible ONLY on click or selection:** unselected tabs show **only the text**, with no icon. When a tab is tapped, the icon appears to the left of the text with an `AnimatedVisibility` animation and a spring expansion (`expandHorizontally(spring(0.8f, 380f)) + fadeIn(tween(180))`).
* **Animated active pill:** the active pill uses `surfaceContainerHighest` and resizes with `animateContentSize`.
* **Maximum 4 items (no fallback):** a strict limit of 4 items (`items.size in 2..4`). No fallback: the application's flows must condense every section into at most 4 tabs.

```kotlin
@Composable
fun ExpressiveImpactNavigationBar(
    items: List<ExpressiveNavItem>,
    selectedRoute: Any,
    onItemSelected: (ExpressiveNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    require(items.size in 2..4) {
        "Massimo 4 elementi supportati. Non usare fallback: riorganizza l'architettura in massimo 4 tab."
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
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 6.dp)
                    .animateContentSize(spring(0.8f, 380f)),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item.route == selectedRoute
                    Surface(
                        shape = CircleShape,
                        color = animateColorAsState(
                            if (isSelected) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent,
                            animationSpec = tween(180)
                        ).value,
                        modifier = Modifier
                            .height(48.dp)
                            .clip(CircleShape)
                            .clickable { onItemSelected(item) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = if (isSelected) 18.dp else 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedVisibility(
                                visible = isSelected,
                                enter = expandHorizontally(spring(0.8f, 380f)) + fadeIn(tween(180)),
                                exit = shrinkHorizontally(spring(0.8f, 380f)) + fadeOut(tween(150))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                }
                            }
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
```

### Official references
* **Link https:** [https://developer.android.com/develop/ui/compose/components/navigation-bar](https://developer.android.com/develop/ui/compose/components/navigation-bar)

---

## 6. Navigation drawer

The navigation drawer component is a slide-in menu that lets users navigate
to various sections of your app. Users can activate it by swiping from the side
or tapping a menu icon.

### Esempio Jetpack Compose
```kotlin
@Composable fun DetailedDrawerExample(
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    Text("Drawer Title", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()

                    Text("Section 1", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                    NavigationDrawerItem(
                        label = { Text("Item 1") },
                        selected = false,
                        onClick = { /* Handle click */ }
                    )
                    NavigationDrawerItem(
                        label = { Text("Item 2") },
                        selected = false,
                        onClick = { /* Handle click */ }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Section 2", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        badge = { Text("20") }, // Placeholder
                        onClick = { /* Handle click */ }
                    )
                    NavigationDrawerItem(
                        label = { Text("Help and feedback") },
                        selected = false,
                        icon = { Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = null) },
                        onClick = { /* Handle click */ },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Navigation Drawer Example") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}
```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/drawer](https://developer.android.com/develop/ui/compose/components/drawer)

---

## 7. Navigation rail

> [!IMPORTANT]
> **M3 Expressive Update:** May 2025 -  A collapsed and expanded navigation rail have been introduced to replace the baseline nav rail. The expanded nav rail is meant to replace the navigation drawer. More on M3 Expressive

[Navigation rail](https://m3.material.io/components/navigation-rail/overview)
lets people switch between UI views on mid-sized devices. There are two types of
navigation rail.

### Esempio Jetpack Compose
```kotlin
There's also a method for detecting if navigation items have been reselected:
```

### Official references
* **Link:** [https://github.com/material-components/material-components-android/blob/master/docs/components/NavigationRail.md](https://github.com/material-components/material-components-android/blob/master/docs/components/NavigationRail.md)

---
