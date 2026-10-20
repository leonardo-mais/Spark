# Material 3 Expressive - Common buttons

**Category:** `actions`  
**Sezione Principale:** [Actions & Buttons](../actions.md)

---

## 1. Common buttons

[Buttons](https://m3.material.io/components/buttons/overview) allow users to
take actions, and make choices, with a single tap. There are two variants of
common buttons.

### Esempio Jetpack Compose
```kotlin
// 5 Configurazioni graduate di enfasi in Material 3 Expressive:
// Button (Filled) -> enfasi massima (primary / on-primary)
// ElevatedButton  -> enfasi elevata su sfondi dinamici
// FilledTonalButton -> enfasi media (secondary-container)
// OutlinedButton   -> enfasi media-bassa (outline / primary)
// TextButton       -> enfasi minima (dialoghi, azioni terziarie)

Button(onClick = { /* azione */ }, modifier = Modifier.fillMaxWidth()) {
    Icon(Icons.Default.Add, contentDescription = null)
    Spacer(Modifier.width(8.dp))
    Text("Azione Primaria", style = MaterialTheme.typography.labelLarge)
}
```

### Official references
* **Common buttons:** [https://github.com/material-components/material-components-android/blob/master/docs/components/CommonButton.md](https://github.com/material-components/material-components-android/blob/master/docs/components/CommonButton.md)

---
