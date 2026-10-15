# Material 3 Expressive - Progress indicator

**Categoria:** `display-feedback`  
**Sezione Principale:** [Display & Feedback](../display-feedback.md)

---

## 7. Loading & progress - Progress indicator

> [!IMPORTANT]
> **M3 Expressive Update:** Aug 2024 - The progress indicators have configurations for height and wavy shape. Choose the visual style that best fits your product. Prefer Shape: Wavy

> **Linee Guida di Design:** **Linear**: https://developer.android.com/reference/kotlin/androidx/compose/material3/LinearProgressIndicator.composable#LinearProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp,kotlin.Function1)
> **Linee Guida di Design:** **Circular**: https://developer.android.com/reference/kotlin/androidx/compose/material3/CircularProgressIndicator.composable#CircularProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp)

`Unit`
              
                `@ComposableLinearProgressIndicator(    modifier: Modifier,    color: Color,    trackColor: Color,    strokeCap: StrokeCap,    gapSize: Dp)`

### Firme Composable Principali
```kotlin
@Composable
fun LinearProgressIndicator(
     modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    gapSize: Dp = ProgressIndicatorDefaults.LinearIndicatorTrackGapSize): Unit

@Composable
fun LinearProgressIndicator(
     progress: Float,
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap): Unit

```

### Esempio Jetpack Compose
```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.Alignment

Column(horizontalAlignment = Alignment.CenterHorizontally) { LinearProgressIndicator() }
```

### Riferimenti Ufficiali
* **Linear:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/LinearProgressIndicator.composable#LinearProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp,kotlin.Function1](https://developer.android.com/reference/kotlin/androidx/compose/material3/LinearProgressIndicator.composable#LinearProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp,kotlin.Function1)
* **Circular:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/CircularProgressIndicator.composable#CircularProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp](https://developer.android.com/reference/kotlin/androidx/compose/material3/CircularProgressIndicator.composable#CircularProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp)

---
