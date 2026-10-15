# Material 3 Expressive - Divider

**Categoria:** `containment-surfaces`  
**Sezione Principale:** [Containment & Surfaces](../containment-surfaces.md)

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
