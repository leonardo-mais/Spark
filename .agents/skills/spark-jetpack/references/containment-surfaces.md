# Material 3 Expressive - Containment & Surfaces

Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per schede, fogli (bottom & side sheets), dialoghi, menu e ricerca conformi a Material 3 Expressive.

> **Riferimenti Locali:** Per la scheda dettagliata di ogni singolo componente, consulta i file dedicati in `references/components/`.

---

## 1. Cards

The `Card` composable acts as a Material Design container for your UI.
Cards typically present a single coherent piece of content. The following
examples show where you might use a card:

### Esempio Jetpack Compose
```kotlin
@Composable fun CardMinimalExample() {
    Card() {
        Text(text = "Hello, world!")
    }
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/card](https://developer.android.com/develop/ui/compose/components/card)

---

## 2. Dialogs

The `Dialog` component displays dialog messages or requests user input on a
layer above the main app content. It creates an interruptive UI experience to
capture user attention.

### Firme Composable Principali
```kotlin
@Composable
fun MinimalDialog(
    onDismissRequest: () -> Unit) {
 Dialog(onDismissRequest = { onDismissRequest() }) {
 Card(
 modifier = Modifier
 .fillMaxWidth()
 .height(200.dp)
 .padding(16.dp),
 shape = RoundedCornerShape(16.dp),
 ) {
 Text(
 text = "This is a minimal dialog",
 modifier = Modifier
 .fillMaxSize()
 .wrapContentSize(Alignment.Center),
 textAlign = TextAlign.Center,
 )
 }
 }
}

@Composable
fun DialogWithImage(
    
 onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    painter: Painter,
    imageDescription: String,
) {
 Dialog(onDismissRequest = { onDismissRequest() }) {
 // Draw a rectangle shape with rounded corners inside the dialog
 Card(
 modifier = Modifier
 .fillMaxWidth()
 .height(375.dp)
 .padding(16.dp),
 shape = RoundedCornerShape(16.dp),
 ) {
 Column(
 modifier = Modifier
 .fillMaxSize(),
 verticalArrangement = Arrangement.Center,
 horizontalAlignment = Alignment.CenterHorizontally,
 ) {
 Image(
 painter = painter,
 contentDescription = imageDescription,
 contentScale = ContentScale.Fit,
 modifier = Modifier
 .height(160.dp)
 )
 Text(
 text = "This is a dialog with buttons and an image.",
 modifier = Modifier.padding(16.dp),
 )
 Row(
 modifier = Modifier
 .fillMaxWidth(),
 horizontalArrangement = Arrangement.Center,
 ) {
 TextButton(
 onClick = { onDismissRequest() },
 modifier = Modifier.padding(8.dp),
 ) {
 Text("Dismiss")
 }
 TextButton(
 onClick = { onConfirmation() },
 modifier = Modifier.padding(8.dp),
 ) {
 Text("Confirm")
 }
 }
 }
 }
 }
}

```

### Esempio Jetpack Compose
```kotlin
@Composable fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/dialog](https://developer.android.com/develop/ui/compose/components/dialog)

---

## 3. Divider

Dividers are thin lines that separate items in lists or other
containers. You can implement dividers in your app using the `HorizontalDivider`
and `VerticalDivider` composables.

### Esempio Jetpack Compose
```kotlin
@Composable fun VerticalDividerExample() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("First item in row")
        VerticalDivider(color = MaterialTheme.colorScheme.secondary)
        Text("Second item in row")
    }
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/divider](https://developer.android.com/develop/ui/compose/components/divider)

---

## 4. Menus

> [!IMPORTANT]
> **M3 Expressive Update:** Nov 2025 - Vertical menus were introduced with new shapes, color styles, selection states, and refined submenu motion. Gaps can be used for a more flexible layout on Android. Variants: Added vertical menus, recommended for new designs, Baseline menu is still available, color styles (Standard, Vibrant)

Drop-down menus let users click an icon, text field, or other component, and
then select from a list of options on a temporary surface. This guide describes
how to create both basic menus and more complex menus with dividers and icons.

### Firme Composable Principali
```kotlin
@Composable
fun MinimalDropdownMenu(
    ) {
 var expanded by remember { mutableStateOf(false) }
 Box(
 modifier = Modifier
 .padding(16.dp)
 ) {
 IconButton(onClick = { expanded = !expanded }) {
 Icon(Icons.Default.MoreVert, contentDescription = "More options")
 }
 DropdownMenu(
 expanded = expanded,
 onDismissRequest = { expanded = false }
 ) {
 DropdownMenuItem(
 text = { Text("Option 1") },
 onClick = { /* Do something... */ }
 )
 DropdownMenuItem(
 text = { Text("Option 2") },
 onClick = { /* Do something... */ }
 )
 }
 }
}

@Composable
fun LongBasicDropdownMenu(
    ) {
 var expanded by remember { mutableStateOf(false) }
 // Placeholder list of 100 strings for demonstration
 val menuItemData = List(100) { "Option ${it + 1}" }

 Box(
 modifier = Modifier
 .padding(16.dp)
 ) {
 IconButton(onClick = { expanded = !expanded }) {
 Icon(Icons.Default.MoreVert, contentDescription = "More options")
 }
 DropdownMenu(
 expanded = expanded,
 onDismissRequest = { expanded = false }
 ) {
 menuItemData.forEach { option ->
 DropdownMenuItem(
 text = { Text(option) },
 onClick = { /* Do something... */ }
 )
 }
 }
 }
}

```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/menu](https://developer.android.com/develop/ui/compose/components/menu)

---

## 5. Search

> [!IMPORTANT]
> **M3 Expressive Update:** Feb 2025 - Naming (Search bar and search view are now collectively named search), Configurations (Styles: Search can be contained (recommended) or divided, Gaps can separate results into groups), Motion (The search bar grows wider when focused) https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#SearchBar(androidx.compose.material3.SearchBarState,kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Shape,androidx.compose.material3.SearchBarColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp)

Build Jetpack Compose UIs with Material 3 Expressive, the latest evolution of Material Design. M3 Expressive includes research-backed updates to theming, components, motion, typography, and more — all designed to help you make engaging and desirable products that users love. M3 Expressive compliments the Android 16 visual style and system UI.

### Esempio Jetpack Compose
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CustomizableSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<String>,
    onResultClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    // Customization options
    placeholder: @Composable () -> Unit = { Text("Search") },
    leadingIcon: @Composable (() -> Unit)? = { Icon(Icons.Default.Search, contentDescription = "Search") },
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingContent: (@Composable (String) -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
) {
    // Track expanded state of search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                // Customizable input field implementation
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        onSearch(query)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            // Show search results in a lazy column for better performance
            LazyColumn {
                items(count = searchResults.size) { index ->
                    val resultText = searchResults[index]
                    ListItem(
                        headlineContent = { Text(resultText) },
                        supportingContent = supportingContent?.let { { it(resultText) } },
                        leadingContent = leadingContent,
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                onResultClick(resultText)
                                expanded = false
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
```

### Riferimenti Ufficiali
* **Updates:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#SearchBar(androidx.compose.material3.SearchBarState,kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Shape,androidx.compose.material3.SearchBarColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#SearchBar(androidx.compose.material3.SearchBarState,kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Shape,androidx.compose.material3.SearchBarColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp)
* **Link:** [https://developer.android.com/develop/ui/compose/components/search-bar](https://developer.android.com/develop/ui/compose/components/search-bar)

---

## 6. Tooltips

Use tooltips to add context to a button or other UI element.
There are two types of tooltips:

### Esempio Jetpack Compose
```kotlin
@Composable fun AdvancedRichTooltipExample(
    modifier: Modifier = Modifier,
    richTooltipSubheadText: String = "Custom Rich Tooltip",
    richTooltipText: String = "Rich tooltips support multiple lines of informational text.",
    richTooltipActionText: String = "Dismiss"
) {
    val tooltipState = rememberTooltipState()
    val coroutineScope = rememberCoroutineScope()

    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                title = { Text(richTooltipSubheadText) },
                action = {
                    Row {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                tooltipState.dismiss()
                            }
                        }) {
                            Text(richTooltipActionText)
                        }
                    }
                },
            ) {
                Text(richTooltipText)
            }
        },
        state = tooltipState
    ) {
        IconButton(onClick = {
            coroutineScope.launch {
                tooltipState.show()
            }
        }) {
            Icon(
                imageVector = Icons.Filled.Camera,
                contentDescription = "Open camera"
            )
        }
    }
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/tooltip](https://developer.android.com/develop/ui/compose/components/tooltip)

---

## 7. Bottom sheets

If you want to implement a bottom sheet, you can use the
`ModalBottomSheet` composable.

### Esempio Jetpack Compose
```kotlin
ModalBottomSheet(onDismissRequest = { /* Executed when the sheet is dismissed */ }) {
    // Sheet content
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/bottom-sheets](https://developer.android.com/develop/ui/compose/components/bottom-sheets)

---

## 8. Side sheets

> **Linee Guida di Design:** **M3**: https://m3.material.io/components/side-sheets/guidelines#4d992de0-362a-41b3-9537-9da4dec148af

[Side sheets](https://m3.material.io/components/side-sheets/overview) are
surfaces containing supplementary content that are anchored to the side of the
screen. There are two variants of side sheets.

### Esempio Jetpack Compose
```kotlin
You can also set the sheet edge with XML, by setting `android:layout_gravity` to
the desired gravity:
```

### Riferimenti Ufficiali
* **Link:** [https://github.com/material-components/material-components-android/blob/master/docs/components/SideSheet.md](https://github.com/material-components/material-components-android/blob/master/docs/components/SideSheet.md)
* **M3:** [https://m3.material.io/components/side-sheets/guidelines#4d992de0-362a-41b3-9537-9da4dec148af](https://m3.material.io/components/side-sheets/guidelines#4d992de0-362a-41b3-9537-9da4dec148af)

---
