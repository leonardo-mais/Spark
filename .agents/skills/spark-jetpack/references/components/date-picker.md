# Material 3 Expressive - Date picker

**Categoria:** `inputs`  
**Sezione Principale:** [Inputs & Selection Controls](../inputs.md)

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
