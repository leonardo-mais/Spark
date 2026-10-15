# Material 3 Expressive - Floating Action Buttons (I 3 FAB)

**Categoria:** `actions`  
**Sezione Principale:** [Actions & Buttons](../actions.md)

---

## 5. Floating action buttons (I Tre FAB)

Esistono **tre dimensioni ufficiali** di Floating Action Button:

1. **Standard FAB (56×56 dp):** Per layout compatti o azioni secondarie.
2. **Medium FAB (68×68 dp):** ⭐️ **IL DEFAULT ASSOLUTO DA USARE SEMPRE**.
3. **Large FAB (96×96 dp):** Da usare **SE E SOLO SE l'azione è proprio cruciale / primaria**.

> [!CRITICAL]
> **DIVIETO ASSOLUTO DI TESTO NEL FAB (SENZA MAI TESTO):**
> È severamente vietato inserire etichette di testo all'interno di un FAB. L'uso di `ExtendedFloatingActionButton` con etichette testuali è proibito. Il FAB deve contenere **ESCLUSIVAMENTE l'icona** centrata.
> **Anti-Capsula:** MAI usare `CircleShape` o raggi a pillola al 50%. Solo forme squircle proporzionali: `RoundedCornerShape(20.dp)` per Medium e `RoundedCornerShape(28.dp)` per Large.

---

### Esempi Jetpack Compose

#### 1. Medium FAB (Default Obbligatorio)
```kotlin
FloatingActionButton(
    onClick = { /* azione */ },
    modifier = Modifier.size(68.dp),
    shape = RoundedCornerShape(20.dp),
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Icon(
        imageVector = Icons.Rounded.Add,
        contentDescription = "Aggiungi elemento",
        modifier = Modifier.size(30.dp)
    )
}
```

#### 2. Large FAB (Solo se l'azione è molto importante)
```kotlin
LargeFloatingActionButton(
    onClick = { /* azione cruciale */ },
    modifier = Modifier.size(96.dp),
    shape = RoundedCornerShape(28.dp),
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Icon(
        imageVector = Icons.Rounded.PlayArrow,
        contentDescription = "Avvia sessione principale",
        modifier = Modifier.size(36.dp)
    )
}
```

#### 3. Standard FAB (Azioni secondarie o spazi ristretti)
```kotlin
FloatingActionButton(
    onClick = { /* azione secondaria */ },
    modifier = Modifier.size(56.dp),
    shape = RoundedCornerShape(16.dp),
    containerColor = MaterialTheme.colorScheme.secondaryContainer,
    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Icon(
        imageVector = Icons.Rounded.Edit,
        contentDescription = "Modifica",
        modifier = Modifier.size(24.dp)
    )
}
```

---

### Riferimenti Ufficiali
* **Floating action buttons (FABs):** [https://github.com/material-components/material-components-android/blob/master/docs/components/FloatingActionButton.md](https://github.com/material-components/material-components-android/blob/master/docs/components/FloatingActionButton.md)
