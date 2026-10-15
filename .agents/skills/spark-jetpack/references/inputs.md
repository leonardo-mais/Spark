# Material 3 Expressive - Inputs & Selection Controls

Questo documento raccoglie firme, parametri, varianti e pattern Jetpack Compose per campi di testo, slider espressivi, selettori e date/time pickers conformi a Material 3 Expressive.

> **Riferimenti Locali:** Per la scheda dettagliata di ogni singolo componente, consulta i file dedicati in `references/components/`.

---

## 1. Checkbox

Checkboxes let users select one or more items from a list. You might use a
checkbox to let the user do the following:

### Esempio Jetpack Compose
```kotlin
@Composable fun CheckboxParentExample() {
    // Initialize states for the child checkboxes
    val childCheckedStates = remember { mutableStateListOf(false, false, false) }

    // Compute the parent state based on children's states
    val parentState = when {
        childCheckedStates.all { it } -> ToggleableState.On
        childCheckedStates.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    Column {
        // Parent TriStateCheckbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Select all")
            TriStateCheckbox(
                state = parentState,
                onClick = {
                    // Determine new state based on current state
                    val newState = parentState != ToggleableState.On
                    childCheckedStates.forEachIndexed { index, _ ->
                        childCheckedStates[index] = newState
                    }
                }
            )
        }

        // Child Checkboxes
        childCheckedStates.forEachIndexed { index, checked ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Option ${index + 1}")
                Checkbox(
                    checked = checked,
                    onCheckedChange = { isChecked ->
                        // Update the individual child state
                        childCheckedStates[index] = isChecked
                    }
                )
            }
        }
    }

    if (childCheckedStates.all { it }) {
        Text("All options selected")
    }
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/checkbox](https://developer.android.com/develop/ui/compose/components/checkbox)

---

## 2. Radio button

> [!IMPORTANT]
> **M3 Expressive Update:** Color: New color mappings and compatibility with dynamic color

A radio button lets a user select an option from a set of
options. You use a radio button when only one item can be selected from a
list. If users need to select more than one item, use a switch 
instead.

### Firme Composable Principali
```kotlin
@Composable
fun RadioButtonSingleSelection(
    modifier: Modifier = Modifier) {
 val radioOptions = listOf("Calls", "Missed", "Friends")
 val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
 // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
 Column(modifier.selectableGroup()) {
 radioOptions.forEach { text ->
 Row(
 Modifier
 .fillMaxWidth()
 .height(56.dp)
 .selectable(
 selected = (text == selectedOption),
 onClick = { onOptionSelected(text) },
 role = Role.RadioButton
 )
 .padding(horizontal = 16.dp),
 verticalAlignment = Alignment.CenterVertically
 ) {
 RadioButton(
 selected = (text == selectedOption),
 onClick = null // null recommended for accessibility with screen readers
 )
 Text(
 text = text,
 style = MaterialTheme.typography.bodyLarge,
 modifier = Modifier.padding(start = 16.dp)
 )
 }
 }
 }
}

```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/radio-button](https://developer.android.com/develop/ui/compose/components/radio-button)

---

## 3. Sliders

> [!IMPORTANT]
> **M3 Expressive Update:** May 2025 - The slider includes expressive configurations for orientation, shape sizes, and an inset icon. Updated on Android Views (MDC-Android) and Jetpack Compose: Variants and naming (Changed continuous slider to standard slider, The discrete slider is now the stops configuration), New configurations (Orientation: Horizontal, vertical)(Optional inset icon (standard slider only))(Sizes: XS (existing default), S, M, L, XL)

> **Linee Guida di Design:** **Old**: https://developer.android.com/develop/ui/compose/components/slider

`Unit`
              
                `@ComposableSlider(    state: SliderState,    modifier: Modifier,    enabled: Boolean,    onValueChange: ((Float) -> Unit)?,    onValueChangeFinished: (() -> Unit)?,    colors: SliderColors,    interactionSource: MutableInteractionSource,    thumb: @Composable (SliderState) -> Unit,    track: @Composable (SliderState) -> Unit)`

### Firme Composable Principali
```kotlin
@Composable
fun Slider(
     state: SliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChange: ((Float) -> Unit)? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (SliderState) -> Unit = { _ ->
 SliderDefaults.Thumb(
 interactionSource = interactionSource,
 colors = colors,
 enabled = enabled,
 )
 },
    track: @Composable (SliderState) -> Unit = { sliderState ->
 SliderDefaults.Track(colors = colors, enabled = enabled, sliderState = sliderState)
 }): Unit

@Composable
fun Slider(
     value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: @IntRange(from = 0) Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }): Unit

```

### Esempio Jetpack Compose
```kotlin
@Preview
@Composable fun SliderAdvancedExample() {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 3,
            valueRange = 0f..50f
        )
        Text(text = sliderPosition.toString())
    }
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/Slider.composable](https://developer.android.com/reference/kotlin/androidx/compose/material3/Slider.composable)
* **Old:** [https://developer.android.com/develop/ui/compose/components/slider](https://developer.android.com/develop/ui/compose/components/slider)

---

## 4. Switch

The `Switch` component allows users to toggle between two states: checked
and unchecked. In your app you may use a switch to let the user do one of the
following:

### Firme Composable Principali
```kotlin
@Composable
fun SwitchWithCustomColors(
    ) {
 var checked by remember { mutableStateOf(true) }

 Switch(
 checked = checked,
 onCheckedChange = {
 checked = it
 },
 colors = SwitchDefaults.colors(
 checkedThumbColor = MaterialTheme.colorScheme.primary,
 checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
 uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
 uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
 )
 )
}

```

### Esempio Jetpack Compose
```kotlin
@Composable fun SwitchMinimalExample() {
    var checked by remember { mutableStateOf(true) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
        }
    )
}
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/switch](https://developer.android.com/develop/ui/compose/components/switch)

---

## 5. Text fields

`Unit`
              
                `@ComposableOutlinedTextField(    value: String,    onValueChange: (String) -> Unit,    modifier: Modifier,    enabled: Boolean,    readOnly: Boolean,    textStyle: TextStyle,    label: (@Composable () -> Unit)?,    placeholder: (@Composable () -> Unit)?,    leadingIcon: (@Composable () -> Unit)?,    trailingIcon: (@Composable () -> Unit)?,    prefix: (@Composable () -> Unit)?,    suffix: (@Composable () -> Unit)?,    supportingText: (@Composable () -> Unit)?,    isError: Boolean,    visualTransformation: VisualTransformation,    keyboardOptions: KeyboardOptions,    keyboardActions: KeyboardActions,    singleLine: Boolean,    maxLines: Int,    minLines: Int,    interactionSource: MutableInteractionSource?,    shape: Shape,    colors: TextFieldColors)`

### Firme Composable Principali
```kotlin
@Composable
fun OutlinedTextField(
     value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()): Unit

@Composable
fun OutlinedTextField(
     value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()): Unit

```

### Esempio Jetpack Compose
```kotlin
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.remember

OutlinedTextField(
    state = rememberTextFieldState(),
    lineLimits = TextFieldLineLimits.SingleLine,
    label = { Text("Label") },
)
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/OutlinedTextField.composable#OutlinedTextField](https://developer.android.com/reference/kotlin/androidx/compose/material3/OutlinedTextField.composable#OutlinedTextField)

---

## 6. Date and Time - Date picker

Date pickers let users select a date, a date range, or both. They use a
calendar dialog or text input to let users select dates.

### Firme Composable Principali
```kotlin
@Composable
fun DatePickerDocked(
    ) {
 var showDatePicker by remember { mutableStateOf(false) }
 val datePickerState = rememberDatePickerState()
 val selectedDate = datePickerState.selectedDateMillis?.let {
 convertMillisToDate(it)
 } ?: ""

 Box(
 modifier = Modifier.fillMaxWidth()
 ) {
 OutlinedTextField(
 value = selectedDate,
 onValueChange = { },
 label = { Text("DOB") },
 readOnly = true,
 trailingIcon = {
 IconButton(onClick = { showDatePicker = !showDatePicker }) {
 Icon(
 imageVector = Icons.Default.DateRange,
 contentDescription = "Select date"
 )
 }
 },
 modifier = Modifier
 .fillMaxWidth()
 .height(64.dp)
 )

 if (showDatePicker) {
 Popup(
 onDismissRequest = { showDatePicker = false },
 alignment = Alignment.TopStart
 ) {
 Box(
 modifier = Modifier
 .fillMaxWidth()
 .offset(y = 64.dp)
 .shadow(elevation = 4.dp)
 .background(MaterialTheme.colorScheme.surface)
 .padding(16.dp)
 ) {
 DatePicker(
 state = datePickerState,
 showModeToggle = false
 )
 }
 }
 }
 }
}

@Composable fun DatePickerFieldToModal(
    modifier: Modifier = Modifier) {
 var selectedDate by remember { mutableStateOf<Long?>(null) }
 var showModal by remember { mutableStateOf(false) }

 OutlinedTextField(
 value = selectedDate?.let { convertMillisToDate(it) } ?: "",
 onValueChange = { },
 label = { Text("DOB") },
 placeholder = { Text("MM/DD/YYYY") },
 trailingIcon = {
 Icon(Icons.Default.DateRange, contentDescription = "Select date")
 },
 modifier = modifier
 .fillMaxWidth()
 .pointerInput(selectedDate) {
 awaitEachGesture {
 // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
 // in the Initial pass to observe events before the text field consumes them
 // in the Main pass.
 awaitFirstDown(pass = PointerEventPass.Initial)
 val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
 if (upEvent != null) {
 showModal = true
 }
 }
 }
 )

 if (showModal) {
 DatePickerModal(
 onDateSelected = { selectedDate = it },
 onDismiss = { showModal = false }
 )
 }
}

fun convertMillisToDate(
    millis: Long): String {
 val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
 return formatter.format(Date(millis))
}

@Composable
fun DatePickerModal(
    
 onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
 val datePickerState = rememberDatePickerState()

 DatePickerDialog(
 onDismissRequest = onDismiss,
 confirmButton = {
 TextButton(onClick = {
 onDateSelected(datePickerState.selectedDateMillis)
 onDismiss()
 }) {
 Text("OK")
 }
 },
 dismissButton = {
 TextButton(onClick = onDismiss) {
 Text("Cancel")
 }
 }
 ) {
 DatePicker(state = datePickerState)
 }
}

```

### Esempio Jetpack Compose
```kotlin
// ...
    var selectedDate by remember { mutableStateOf<Long?>(null) }
// ...
        if (selectedDate != null) {
            val date = Date(selectedDate!!)
            val formattedDate = SimpleDateFormat("MMM dd, yyyy", LocalLocale.current.platformLocale).format(date)
            Text("Selected date: $formattedDate")
        } else {
            Text("No date selected")
        }
// ...
        DatePickerModal(
            onDateSelected = {
                selectedDate = it
                showModal = false
            },
            onDismiss = { showModal = false }
        )
    }
// ...
```

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/datepickers](https://developer.android.com/develop/ui/compose/components/datepickers)

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

### Riferimenti Ufficiali
* **Link:** [https://developer.android.com/develop/ui/compose/components/time-pickers](https://developer.android.com/develop/ui/compose/components/time-pickers)

---
