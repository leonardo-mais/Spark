# Material 3 Expressive - Floating bar

**Category:** `navigation`  
**Sezione Principale:** [Navigation & App Bars](../navigation.md)

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
