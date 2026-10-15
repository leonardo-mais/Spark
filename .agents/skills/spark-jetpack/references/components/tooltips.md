# Material 3 Expressive - Tooltips

**Categoria:** `containment-surfaces`  
**Sezione Principale:** [Containment & Surfaces](../containment-surfaces.md)

---

## 6. Tooltips

Use tooltips to add context to a button or other UI element.
There are two types of tooltips:

### Esempio Jetpack Compose
```kotlin
@Composable fun AdvancedRichTooltipExample(
    modifier: Modifier = Modifier,
    richTooltipSubheadText: String = "Custom Rich Tooltip",
    richTooltipText: String = "Rich tooltips support multiple lines of informational text.",
    richTooltipActionText: String = "Dismiss"
) {
    val tooltipState = rememberTooltipState()
    val coroutineScope = rememberCoroutineScope()

    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                title = { Text(richTooltipSubheadText) },
                action = {
                    Row {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                tooltipState.dismiss()
                            }
                        }) {
                            Text(richTooltipActionText)
                        }
                    }
                },
            ) {
                Text(richTooltipText)
            }
        },
        state = tooltipState
    ) {
        IconButton(onClick = {
            coroutineScope.launch {
                tooltipState.show()
            }
        }) {
            Icon(
                imageVector = Icons.Filled.Camera,
                contentDescription = "Open camera"
            )
        }
    }
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/tooltip](https://developer.android.com/develop/ui/compose/components/tooltip)

---
