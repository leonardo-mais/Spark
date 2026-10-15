# Material 3 Expressive - Switch

**Categoria:** `inputs`  
**Sezione Principale:** [Inputs & Selection Controls](../inputs.md)

---

## 4. Switch

The `Switch` component allows users to toggle between two states: checked
and unchecked. In your app you may use a switch to let the user do one of the
following:

### Firme Composable Principali
```kotlin
@Composable
fun SwitchWithCustomColors(
    ) {
 var checked by remember { mutableStateOf(true) }

 Switch(
 checked = checked,
 onCheckedChange = {
 checked = it
 },
 colors = SwitchDefaults.colors(
 checkedThumbColor = MaterialTheme.colorScheme.primary,
 checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
 uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
 uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
 )
 )
}

```

### Esempio Jetpack Compose
```kotlin
@Composable fun SwitchMinimalExample() {
    var checked by remember { mutableStateOf(true) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
        }
    )
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/switch](https://developer.android.com/develop/ui/compose/components/switch)

---
