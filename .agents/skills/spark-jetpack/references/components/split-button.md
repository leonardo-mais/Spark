# Material 3 Expressive - Split button

**Categoria:** `actions`  
**Sezione Principale:** [Actions & Buttons](../actions.md)

---

## 3. Buttons - Split button

[Split buttons](https://m3.material.io/components/split-button/overview) open a
menu to give people more options related to an action. It is a specialized type
of the connected button group. The trailing button is checkable with an animated
icon.

### Esempio Jetpack Compose
```kotlin
// SplitButton per azione primaria + menu a tendina associato
SplitButtonLayout(
    leadingButton = {
        Button(onClick = onDefaultAction) {
            Text("Salva")
        }
    },
    trailingButton = {
        FilledIconButton(onClick = onOpenMenu) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Opzioni")
        }
    }
)
```

### Riferimenti Ufficiali
* **Split button:** [https://github.com/material-components/material-components-android/blob/master/docs/components/SplitButton.md](https://github.com/material-components/material-components-android/blob/master/docs/components/SplitButton.md)

---
