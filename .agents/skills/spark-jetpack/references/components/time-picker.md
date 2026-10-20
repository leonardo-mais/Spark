# Material 3 Expressive - Time picker

**Category:** `inputs`  
**Sezione Principale:** [Inputs & Selection Controls](../inputs.md)

---

## 7. Date and Time - Time picker

Time pickers provide a way for users to select a time. You can
use the `TimePicker` and `TimeInput` composables to implement a time
picker in your app.
Note: `TimePicker` and `TimeInput` are experimental. File any issues on the
issue tracker.

### Esempio Jetpack Compose
```kotlin
@Composable fun DialUseStateExample(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Column {
        TimePicker(
            state = timePickerState,
        )
        Button(onClick = onDismiss) {
            Text("Dismiss picker")
        }
        Button(onClick = { onConfirm(timePickerState) }) {
            Text("Confirm selection")
        }
    }
}
```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/time-pickers](https://developer.android.com/develop/ui/compose/components/time-pickers)

---
