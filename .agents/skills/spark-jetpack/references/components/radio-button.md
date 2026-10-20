# Material 3 Expressive - Radio button

**Category:** `inputs`  
**Sezione Principale:** [Inputs & Selection Controls](../inputs.md)

---

## 2. Radio button

> [!IMPORTANT]
> **M3 Expressive Update:** Color: New color mappings and compatibility with dynamic color

A radio button lets a user select an option from a set of
options. You use a radio button when only one item can be selected from a
list. If users need to select more than one item, use a switch 
instead.

### Main composable signatures
```kotlin
@Composable
fun RadioButtonSingleSelection(
    modifier: Modifier = Modifier) {
 val radioOptions = listOf("Calls", "Missed", "Friends")
 val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
 // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
 Column(modifier.selectableGroup()) {
 radioOptions.forEach { text ->
 Row(
 Modifier
 .fillMaxWidth()
 .height(56.dp)
 .selectable(
 selected = (text == selectedOption),
 onClick = { onOptionSelected(text) },
 role = Role.RadioButton
 )
 .padding(horizontal = 16.dp),
 verticalAlignment = Alignment.CenterVertically
 ) {
 RadioButton(
 selected = (text == selectedOption),
 onClick = null // null recommended for accessibility with screen readers
 )
 Text(
 text = text,
 style = MaterialTheme.typography.bodyLarge,
 modifier = Modifier.padding(start = 16.dp)
 )
 }
 }
 }
}

```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/radio-button](https://developer.android.com/develop/ui/compose/components/radio-button)

---
