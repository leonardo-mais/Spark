# Material 3 Expressive - Text fields

**Categoria:** `inputs`  
**Sezione Principale:** [Inputs & Selection Controls](../inputs.md)

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
