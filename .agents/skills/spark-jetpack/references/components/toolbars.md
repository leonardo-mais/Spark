# Material 3 Expressive - Toolbars

**Categoria:** `navigation`  
**Sezione Principale:** [Navigation & App Bars](../navigation.md)

---

## 3. Toolbars

> [!IMPORTANT]
> **M3 Expressive Update:** The bottom app bar is no longer recommended and should be replaced with the docked toolbar, which functions similarly, but is shorter and has more flexibility. The floating toolbar was created for more versatility, greater amounts of actions, and more variety in where it's placed. Added floating toolbar with the following configurations (Layout: Horizontal or vertical, Color: Standard or vibrant, Flexibility: Can hold many elements and components. Can be paired with FAB)

`Unit`
              
                `@ComposableFlexibleBottomAppBar(    modifier: Modifier,    containerColor: Color,    contentColor: Color,    contentPadding: PaddingValues,    horizontalArrangement: Arrangement.Horizontal,    expandedHeight: Dp,    windowInsets: WindowInsets,    scrollBehavior: BottomAppBarScrollBehavior?,    content: @Composable RowScope.() -> Unit)`

### Firme Composable Principali
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

### Riferimenti Ufficiali
* **FlexibleBottomAppBar:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/FlexibleBottomAppBar.composable](https://developer.android.com/reference/kotlin/androidx/compose/material3/FlexibleBottomAppBar.composable)
* **HorizontalFloatingToolbar:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/HorizontalFloatingToolbar.composable](https://developer.android.com/reference/kotlin/androidx/compose/material3/HorizontalFloatingToolbar.composable)

---
