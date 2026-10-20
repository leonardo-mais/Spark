# Material 3 Expressive - Icon buttons

**Category:** `actions`  
**Sezione Principale:** [Actions & Buttons](../actions.md)

---

## 4. Icon buttons

[Icon buttons](https://m3.material.io/components/icon-buttons/overview) help
people take minor actions with one tap. There are two variants of icon buttons.

### Esempio Jetpack Compose
```kotlin
// Icon Buttons M3 (Standard, Filled, FilledTonal, Outlined)
IconButton(onClick = { /* ... */ }) {
    Icon(Icons.Default.FavoriteBorder, contentDescription = "Preferiti")
}
FilledIconButton(onClick = { /* ... */ }) {
    Icon(Icons.Default.Bookmark, contentDescription = "Saved")
}
FilledTonalIconButton(onClick = { /* ... */ }) {
    Icon(Icons.Default.Share, contentDescription = "Condividi")
}
OutlinedIconButton(onClick = { /* ... */ }) {
    Icon(Icons.Default.MoreVert, contentDescription = "Altro")
}
```

### Official references
* **Icon buttons:** [https://github.com/material-components/material-components-android/blob/master/docs/components/IconButton.md](https://github.com/material-components/material-components-android/blob/master/docs/components/IconButton.md)

---
