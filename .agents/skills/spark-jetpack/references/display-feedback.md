# Material 3 Expressive - Display & Feedback

This document collects the Jetpack Compose signatures, parameters, variants and patterns for expressive lists, carousels, chips, badges, wavy progress indicators and snackbars, compliant with Material 3 Expressive.

> **Local references:** for the detailed card of each component, see the dedicated files in `references/components/`.

---

## 1. Badges

Use a badge to display a small visual element to denote status
or a numeric value on another composable. Here are a few common scenarios where
you might use a badge:

### Esempio Jetpack Compose
```kotlin
@Composable fun BadgeInteractiveExample() {
    var itemCount by remember { mutableIntStateOf(0) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BadgedBox(
            badge = {
                if (itemCount > 0) {
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) {
                        Text("$itemCount")
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "Shopping cart",
            )
        }
        Button(onClick = { itemCount++ }) {
            Text("Add item")
        }
    }
}
```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/badges](https://developer.android.com/develop/ui/compose/components/badges)

---

## 2. Carousel

A carousel displays a scrollable list of items that adapt dynamically based on
window size. Use carousels to showcase a collection of related content.
Carousel items emphasize visuals, but can also contain brief text that adapts to
the item size.

### Esempio Jetpack Compose
```kotlin
@Composable fun CarouselExample_MultiBrowse() {
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    val items = remember {
        listOf(
            CarouselItem(0, R.drawable.cupcake, "cupcake"),
            CarouselItem(1, R.drawable.donut, "donut"),
            CarouselItem(2, R.drawable.eclair, "eclair"),
            CarouselItem(3, R.drawable.froyo, "froyo"),
            CarouselItem(4, R.drawable.gingerbread, "gingerbread"),
        )
    }

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp, bottom = 16.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = items[i]
        Image(
            modifier = Modifier
                .height(205.dp)
                .maskClip(MaterialTheme.shapes.extraLarge),
            painter = painterResource(id = item.imageResId),
            contentDescription = item.contentDescription,
            contentScale = ContentScale.Crop
        )
    }
}
```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/carousel](https://developer.android.com/develop/ui/compose/components/carousel)

---

## 3. Chips

> [!IMPORTANT]
> **M3 Expressive Update:** Aug 2024 - Updated stroke color from outline to outline variant

The `Chip` component is a compact, interactive UI element. It represents complex
entities like a contact or tag, often with an icon and label. It can be
checkable, dismissible, or clickable.

### Esempio Jetpack Compose
```kotlin
@Composable fun AssistChipExample() {
    AssistChip(
        onClick = { Log.d("Assist chip", "hello world") },
        label = { Text("Assist chip") },
        leadingIcon = {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Localized description",
                Modifier.size(AssistChipDefaults.IconSize)
            )
        }
    )
}
```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/chip](https://developer.android.com/develop/ui/compose/components/chip)

---

## 4. Lists

> [!IMPORTANT]
> **M3 Expressive Update:** Dec 2025 - Added expressive list (Recommended for new designs). New visual styles: Standard or segmented, Highlighted selection states, Flexible slots (https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ListItem%28kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Function0,kotlin.Function0,kotlin.Function0,kotlin.Function0,androidx.compose.material3.ListItemColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp%29)

> **Linee Guida di Design:** **Remember**: When needed, use [LazyVerticalGrid](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyVerticalGrid.composable) and [LazyHorizontalGrid](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyHorizontalGrid.composable)

`Unit`
              
                `@ComposableLazyVerticalGrid(    columns: GridCells,    modifier: Modifier,    state: LazyGridState,    contentPadding: PaddingValues,    reverseLayout: Boolean,    verticalArrangement: Arrangement.Vertical,    horizontalArrangement: Arrangement.Horizontal,    flingBehavior: FlingBehavior,    userScrollEnabled: Boolean,    overscrollEffect: OverscrollEffect?,    cacheWindow: LazyLayoutCacheWindow,    content: LazyGridScope.() -> Unit)`

### Main composable signatures
```kotlin
@Composable
fun LazyVerticalGrid(
     columns: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    cacheWindow: LazyLayoutCacheWindow = LazyGridDefaults.cacheWindow(state),
    content: LazyGridScope.() -> Unit): Unit

@Composable
fun LazyHorizontalGrid(
     rows: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = if (!reverseLayout) Arrangement.Start else Arrangement.End,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
    cacheWindow: LazyLayoutCacheWindow = LazyGridDefaults.cacheWindow(state),
    content: LazyGridScope.() -> Unit): Unit

```

### Esempio Jetpack Compose
```kotlin
@Composable
private fun OrderButtons(
    resetOrder: () -> Unit,
    orderAlphabetically: () -> Unit,
    orderByLength: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        var selectedIndex by remember { mutableIntStateOf(0) }
        val options = listOf("Reset", "Alphabetical", "Length")

        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    onClick = {
                        Log.d("AnimatedOrderedList", "selectedIndex: $selectedIndex")
                        selectedIndex = index
                        when (options[selectedIndex]) {
                            "Reset" -> resetOrder()
                            "Alphabetical" -> orderAlphabetically()
                            "Length" -> orderByLength()
                        }
                    },
                    selected = index == selectedIndex
                ) {
                    Text(label)
                }
            }
        }
    }
}
```

### Official references
* **Remember:** [https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyVerticalGrid.composable](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyVerticalGrid.composable)
* **Remember:** [https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyHorizontalGrid.composable](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyHorizontalGrid.composable)
* **Updates:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ListItem%28kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Function0,kotlin.Function0,kotlin.Function0,kotlin.Function0,androidx.compose.material3.ListItemColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp%29](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ListItem%28kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Function0,kotlin.Function0,kotlin.Function0,kotlin.Function0,androidx.compose.material3.ListItemColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp%29)
* **Link:** [https://developer.android.com/develop/ui/compose/lists](https://developer.android.com/develop/ui/compose/lists)

---

## 5. Snackbar

The snackbar component serves as a brief notification that appears at the
bottom of the screen. It provides feedback about an operation or action without
interrupting the user experience. Snackbars disappear after a few seconds. The
user can also dismiss them with an action, such as tapping a button.

### Esempio Jetpack Compose
```kotlin
val scope = rememberCoroutineScope()
val snackbarHostState = remember { SnackbarHostState() }
Scaffold(
    snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    },
    floatingActionButton = {
        ExtendedFloatingActionButton(
            text = { Text("Show snackbar") },
            icon = { Icon(Icons.Filled.Image, contentDescription = "") },
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("Snackbar")
                }
            }
        )
    }
) { contentPadding ->
    // Screen content
}
```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/snackbar](https://developer.android.com/develop/ui/compose/components/snackbar)

---

## 6. Loading & progress - Loading indicator

`Unit`
              
                `@ExperimentalMaterial3ExpressiveApi@ComposableLoadingIndicator(    modifier: Modifier,    color: Color,    polygons: List<RoundedPolygon>)`

### Esempio Jetpack Compose
```kotlin
@ExperimentalMaterial3ExpressiveApi@Composable fun LoadingIndicator(    modifier: Modifier = Modifier,    color: Color = LoadingIndicatorDefaults.indicatorColor,    polygons: List<RoundedPolygon> = LoadingIndicatorDefaults.IndeterminateIndicatorPolygons): Unit
```

### Official references
* **Link:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/LoadingIndicator.composable](https://developer.android.com/reference/kotlin/androidx/compose/material3/LoadingIndicator.composable)

---

## 7. Loading & progress - Progress indicator

> [!IMPORTANT]
> **M3 Expressive Update:** Aug 2024 - The progress indicators have configurations for height and wavy shape. Choose the visual style that best fits your product. Prefer Shape: Wavy

> **Linee Guida di Design:** **Linear**: https://developer.android.com/reference/kotlin/androidx/compose/material3/LinearProgressIndicator.composable#LinearProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp,kotlin.Function1)
> **Linee Guida di Design:** **Circular**: https://developer.android.com/reference/kotlin/androidx/compose/material3/CircularProgressIndicator.composable#CircularProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp)

`Unit`
              
                `@ComposableLinearProgressIndicator(    modifier: Modifier,    color: Color,    trackColor: Color,    strokeCap: StrokeCap,    gapSize: Dp)`

### Main composable signatures
```kotlin
@Composable
fun LinearProgressIndicator(
     modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    gapSize: Dp = ProgressIndicatorDefaults.LinearIndicatorTrackGapSize): Unit

@Composable
fun LinearProgressIndicator(
     progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap): Unit

```

### Esempio Jetpack Compose
```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.Alignment

Column(horizontalAlignment = Alignment.CenterHorizontally) { LinearProgressIndicator() }
```

### Official references
* **Linear:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/LinearProgressIndicator.composable#LinearProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp,kotlin.Function1](https://developer.android.com/reference/kotlin/androidx/compose/material3/LinearProgressIndicator.composable#LinearProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp,kotlin.Function1)
* **Circular:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/CircularProgressIndicator.composable#CircularProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp](https://developer.android.com/reference/kotlin/androidx/compose/material3/CircularProgressIndicator.composable#CircularProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp)

---
