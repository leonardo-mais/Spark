# Material 3 Expressive - Lists

**Categoria:** `display-feedback`  
**Sezione Principale:** [Display & Feedback](../display-feedback.md)

---

## 4. Lists

> [!IMPORTANT]
> **M3 Expressive Update:** Dec 2025 - Added expressive list (Recommended for new designs). New visual styles: Standard or segmented, Highlighted selection states, Flexible slots (https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ListItem%28kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Function0,kotlin.Function0,kotlin.Function0,kotlin.Function0,androidx.compose.material3.ListItemColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp%29)

> **Linee Guida di Design:** **Remember**: When needed, use [LazyVerticalGrid](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyVerticalGrid.composable) and [LazyHorizontalGrid](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyHorizontalGrid.composable)

`Unit`
              
                `@ComposableLazyVerticalGrid(    columns: GridCells,    modifier: Modifier,    state: LazyGridState,    contentPadding: PaddingValues,    reverseLayout: Boolean,    verticalArrangement: Arrangement.Vertical,    horizontalArrangement: Arrangement.Horizontal,    flingBehavior: FlingBehavior,    userScrollEnabled: Boolean,    overscrollEffect: OverscrollEffect?,    cacheWindow: LazyLayoutCacheWindow,    content: LazyGridScope.() -> Unit)`

### Firme Composable Principali
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

### Riferimenti Ufficiali
* **Remember:** [https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyVerticalGrid.composable](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyVerticalGrid.composable)
* **Remember:** [https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyHorizontalGrid.composable](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyHorizontalGrid.composable)
* **Updates:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ListItem%28kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Function0,kotlin.Function0,kotlin.Function0,kotlin.Function0,androidx.compose.material3.ListItemColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp%29](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ListItem%28kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Function0,kotlin.Function0,kotlin.Function0,kotlin.Function0,androidx.compose.material3.ListItemColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp%29)
* **Link:** [https://developer.android.com/develop/ui/compose/lists](https://developer.android.com/develop/ui/compose/lists)

---
