# Material 3 Expressive - Sliders

**Category:** `inputs`  
**Sezione Principale:** [Inputs & Selection Controls](../inputs.md)

---

## 3. Sliders

> [!IMPORTANT]
> **M3 Expressive Update:** May 2025 - The slider includes expressive configurations for orientation, shape sizes, and an inset icon. Updated on Android Views (MDC-Android) and Jetpack Compose: Variants and naming (Changed continuous slider to standard slider, The discrete slider is now the stops configuration), New configurations (Orientation: Horizontal, vertical)(Optional inset icon (standard slider only))(Sizes: XS (existing default), S, M, L, XL)

> **Linee Guida di Design:** **Old**: https://developer.android.com/develop/ui/compose/components/slider

`Unit`
              
                `@ComposableSlider(    state: SliderState,    modifier: Modifier,    enabled: Boolean,    onValueChange: ((Float) -> Unit)?,    onValueChangeFinished: (() -> Unit)?,    colors: SliderColors,    interactionSource: MutableInteractionSource,    thumb: @Composable (SliderState) -> Unit,    track: @Composable (SliderState) -> Unit)`

### Main composable signatures
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

### Official references
* **Link:** [https://developer.android.com/reference/kotlin/androidx/compose/material3/Slider.composable](https://developer.android.com/reference/kotlin/androidx/compose/material3/Slider.composable)
* **Old:** [https://developer.android.com/develop/ui/compose/components/slider](https://developer.android.com/develop/ui/compose/components/slider)

---
