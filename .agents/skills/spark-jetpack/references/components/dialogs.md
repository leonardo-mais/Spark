# Material 3 Expressive - Dialogs

**Category:** `containment-surfaces`  
**Sezione Principale:** [Containment & Surfaces](../containment-surfaces.md)

---

## 2. Dialogs

The `Dialog` component displays dialog messages or requests user input on a
layer above the main app content. It creates an interruptive UI experience to
capture user attention.

### Main composable signatures
```kotlin
@Composable
fun MinimalDialog(
    onDismissRequest: () -> Unit) {
 Dialog(onDismissRequest = { onDismissRequest() }) {
 Card(
 modifier = Modifier
 .fillMaxWidth()
 .height(200.dp)
 .padding(16.dp),
 shape = RoundedCornerShape(16.dp),
 ) {
 Text(
 text = "This is a minimal dialog",
 modifier = Modifier
 .fillMaxSize()
 .wrapContentSize(Alignment.Center),
 textAlign = TextAlign.Center,
 )
 }
 }
}

@Composable
fun DialogWithImage(
    
 onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    painter: Painter,
    imageDescription: String,
) {
 Dialog(onDismissRequest = { onDismissRequest() }) {
 // Draw a rectangle shape with rounded corners inside the dialog
 Card(
 modifier = Modifier
 .fillMaxWidth()
 .height(375.dp)
 .padding(16.dp),
 shape = RoundedCornerShape(16.dp),
 ) {
 Column(
 modifier = Modifier
 .fillMaxSize(),
 verticalArrangement = Arrangement.Center,
 horizontalAlignment = Alignment.CenterHorizontally,
 ) {
 Image(
 painter = painter,
 contentDescription = imageDescription,
 contentScale = ContentScale.Fit,
 modifier = Modifier
 .height(160.dp)
 )
 Text(
 text = "This is a dialog with buttons and an image.",
 modifier = Modifier.padding(16.dp),
 )
 Row(
 modifier = Modifier
 .fillMaxWidth(),
 horizontalArrangement = Arrangement.Center,
 ) {
 TextButton(
 onClick = { onDismissRequest() },
 modifier = Modifier.padding(8.dp),
 ) {
 Text("Dismiss")
 }
 TextButton(
 onClick = { onConfirmation() },
 modifier = Modifier.padding(8.dp),
 ) {
 Text("Confirm")
 }
 }
 }
 }
 }
}

```

### Esempio Jetpack Compose
```kotlin
@Composable fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
```

### Official references
* **Link:** [https://developer.android.com/develop/ui/compose/components/dialog](https://developer.android.com/develop/ui/compose/components/dialog)

---
