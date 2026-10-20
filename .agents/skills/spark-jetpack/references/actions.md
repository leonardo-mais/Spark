# Material 3 Expressive - Actions & Buttons

This document collects the Jetpack Compose signatures, parameters, variants and patterns for buttons and action elements (Common Buttons, SplitButton, Connected ButtonGroups, IconButtons, FABs), compliant with Material 3 Expressive.

> **Local references:** for the detailed card of each component, see the dedicated files in `references/components/`.

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

## 2. Buttons - Button groups

[Button groups](https://m3.material.io/components/button-groups/overview)
organize buttons and add interactions between them.There are two variants of a
button group.

### Esempio Jetpack Compose
```kotlin
> **M3 Expressive update:** the old SegmentedButton is deprecated in favor of the Connected Button Group.
// Connected Button Group in M3 Expressive:
ButtonGroup(
    modifier = Modifier.fillMaxWidth()
) {
    // Connected buttons with morphing corner radius
}
```

### Official references
* **Button groups:** [https://github.com/material-components/material-components-android/blob/master/docs/components/ButtonGroup.md](https://github.com/material-components/material-components-android/blob/master/docs/components/ButtonGroup.md)

---

## 3. Buttons - Split button

[Split buttons](https://m3.material.io/components/split-button/overview) open a
menu to give people more options related to an action. It is a specialized type
of the connected button group. The trailing button is checkable with an animated
icon.

### Esempio Jetpack Compose
```kotlin
// SplitButton for a primary action + associated dropdown menu
SplitButtonLayout(
    leadingButton = {
        Button(onClick = onDefaultAction) {
            Text("Save")
        }
    },
    trailingButton = {
        FilledIconButton(onClick = onOpenMenu) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Options")
        }
    }
)
```

### Official references
* **Split button:** [https://github.com/material-components/material-components-android/blob/master/docs/components/SplitButton.md](https://github.com/material-components/material-components-android/blob/master/docs/components/SplitButton.md)

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

## 5. Floating action buttons (FABs)

### The three FAB variants and the medium default rule

I Floating Action Button supportano **tre dimensioni ufficiali**:
1. **Standard FAB (56×56 dp)**: for compact layouts or secondary actions (`RoundedCornerShape(16.dp)`, 24dp icon).
2. **Medium FAB (68×68 dp)**: **THE ABSOLUTE DEFAULT, ALWAYS USE IT** (`RoundedCornerShape(20.dp)`, 30dp icon).
3. **Large FAB (96×96 dp)**: Use **ONLY IF the action is truly crucial / primary** (`RoundedCornerShape(28.dp)`, 36dp icon).

> [!CRITICAL]
> **DIVIETO ASSOLUTO DI TESTO NEL FAB (SENZA MAI TESTO):**
> Text labels inside a FAB are strictly forbidden. Using `ExtendedFloatingActionButton` with text labels is prohibited. A FAB must contain **ONLY the centered icon**.
> **Anti-Capsula:** MAI usare `CircleShape` o raggi al 50%. Solo forme squircle proporzionali.

### Esempio Jetpack Compose: Medium FAB (Default)
```kotlin
// 1. Medium FAB (Default obbligatorio - SOLO ICONA)
FloatingActionButton(
    onClick = { /* azione */ },
    modifier = Modifier.size(68.dp),
    shape = RoundedCornerShape(20.dp),
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Icon(Icons.Rounded.Add, contentDescription = "Aggiungi", modifier = Modifier.size(30.dp))
}

// 2. Large FAB (Solo se azione proprio importante)
LargeFloatingActionButton(
    onClick = { /* azione cruciale */ },
    modifier = Modifier.size(96.dp),
    shape = RoundedCornerShape(28.dp),
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Icon(Icons.Rounded.Add, contentDescription = "Azione primaria", modifier = Modifier.size(36.dp))
}
```

### Official references
* **Floating action buttons (FABs):** [https://github.com/material-components/material-components-android/blob/master/docs/components/FloatingActionButton.md](https://github.com/material-components/material-components-android/blob/master/docs/components/FloatingActionButton.md)

---

## 7. Buttons - Button groups

> **Linee Guida di Design:** **Toggle button groups**: The segmented button is being deprecated. Use the connected button group instead

### Esempio Jetpack Compose
```kotlin
> **M3 Expressive update:** the old SegmentedButton is deprecated in favor of the Connected Button Group.
// Connected Button Group in M3 Expressive:
ButtonGroup(
    modifier = Modifier.fillMaxWidth()
) {
    // Connected buttons with morphing corner radius
}
```

---
