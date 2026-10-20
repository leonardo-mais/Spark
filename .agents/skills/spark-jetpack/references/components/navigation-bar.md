# Material 3 Expressive - Navigation bar

**Category:** `navigation`  
**Sezione Principale:** [Navigation & App Bars](../navigation.md)

---

## 5. Navigation bar

The navigation bar allows users to switch between destinations in an app. You
should use navigation bars for:

### Esempio Jetpack Compose
```kotlin
@Composable fun NavigationBarExample(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.SONGS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(navController, startDestination, modifier = Modifier.padding(contentPadding))
    }
}
```

### Official references
* **Link https:** [https://developer.android.com/develop/ui/compose/components/navigation-bar](https://developer.android.com/develop/ui/compose/components/navigation-bar)

---
