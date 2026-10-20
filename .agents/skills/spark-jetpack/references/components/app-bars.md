# Material 3 Expressive - App bars

**Category:** `navigation`  
**Sezione Principale:** [Navigation & App Bars](../navigation.md)

---

## 1. App bars

App bars are containers that provide the user access to key features and
navigation items. There are two types of app bars, top app bars and bottom app
bars. Their respective appearance and purpose are as follows:

### Esempio Jetpack Compose
```kotlin
@Composable fun BottomAppBarExample() {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Filled.Check, contentDescription = "Localized description")
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Mic,
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = "Localized description",
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* do something */ },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                }
            )
        },
    ) { innerPadding ->
        Text(
            modifier = Modifier.padding(innerPadding),
            text = "Example of a scaffold with a bottom app bar."
        )
    }
}
```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/app-bars?hl=en](https://developer.android.com/develop/ui/compose/components/app-bars?hl=en)

---
