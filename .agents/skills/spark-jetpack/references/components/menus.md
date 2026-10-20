# Material 3 Expressive - Menus

**Category:** `containment-surfaces`  
**Sezione Principale:** [Containment & Surfaces](../containment-surfaces.md)

---

## 4. Menus

> [!IMPORTANT]
> **M3 Expressive Update:** Nov 2025 - Vertical menus were introduced with new shapes, color styles, selection states, and refined submenu motion. Gaps can be used for a more flexible layout on Android. Variants: Added vertical menus, recommended for new designs, Baseline menu is still available, color styles (Standard, Vibrant)

Drop-down menus let users click an icon, text field, or other component, and
then select from a list of options on a temporary surface. This guide describes
how to create both basic menus and more complex menus with dividers and icons.

### Main composable signatures
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

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/menu](https://developer.android.com/develop/ui/compose/components/menu)

---
