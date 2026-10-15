# Material 3 Expressive - Chips

**Categoria:** `display-feedback`  
**Sezione Principale:** [Display & Feedback](../display-feedback.md)

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

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/chip](https://developer.android.com/develop/ui/compose/components/chip)

---
