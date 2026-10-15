# Material 3 Expressive - Badges

**Categoria:** `display-feedback`  
**Sezione Principale:** [Display & Feedback](../display-feedback.md)

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

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/badges](https://developer.android.com/develop/ui/compose/components/badges)

---
