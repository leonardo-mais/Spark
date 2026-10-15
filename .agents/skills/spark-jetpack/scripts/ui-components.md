### App bars
**Link**: https://developer.android.com/develop/ui/compose/components/app-bars?hl=en

### Badges
**Link**: https://developer.android.com/develop/ui/compose/components/badges

### Cards
**Link**: https://developer.android.com/develop/ui/compose/components/card

### Carousel
**Link**: https://developer.android.com/develop/ui/compose/components/carousel

### Checkbox
**Link**: https://developer.android.com/develop/ui/compose/components/checkbox

### Chips
**Updates**: Aug 2024 - Updated stroke color from outline to outline variant
**Link**: https://developer.android.com/develop/ui/compose/components/chip

### Dialogs
**Link**: https://developer.android.com/develop/ui/compose/components/dialog

### Divider
**Link**: https://developer.android.com/develop/ui/compose/components/divider

### Lists
**Remember**: When needed, use [LazyVerticalGrid](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyVerticalGrid.composable) and [LazyHorizontalGrid](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/grid/LazyHorizontalGrid.composable)
**Updates**: Dec 2025 - Added expressive list (Recommended for new designs). New visual styles: Standard or segmented, Highlighted selection states, Flexible slots (https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ListItem%28kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Function0,kotlin.Function0,kotlin.Function0,kotlin.Function0,androidx.compose.material3.ListItemColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp%29)
**Link**: https://developer.android.com/develop/ui/compose/lists

### Menus
**Updates**: Nov 2025 - Vertical menus were introduced with new shapes, color styles, selection states, and refined submenu motion. Gaps can be used for a more flexible layout on Android. Variants: Added vertical menus, recommended for new designs, Baseline menu is still available, color styles (Standard, Vibrant)
**Link**: https://developer.android.com/develop/ui/compose/components/menu

### Radio button
**Updates**: Color: New color mappings and compatibility with dynamic color
**Link**: https://developer.android.com/develop/ui/compose/components/radio-button

### Search
**Updates**: Feb 2025 - Naming (Search bar and search view are now collectively named search), Configurations (Styles: Search can be contained (recommended) or divided, Gaps can separate results into groups), Motion (The search bar grows wider when focused) https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#SearchBar(androidx.compose.material3.SearchBarState,kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Shape,androidx.compose.material3.SearchBarColors,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp)
**Link**: https://developer.android.com/develop/ui/compose/components/search-bar

### Sliders
**Updates**: May 2025 - The slider includes expressive configurations for orientation, shape sizes, and an inset icon. Updated on Android Views (MDC-Android) and Jetpack Compose: Variants and naming (Changed continuous slider to standard slider, The discrete slider is now the stops configuration), New configurations (Orientation: Horizontal, vertical)(Optional inset icon (standard slider only))(Sizes: XS (existing default), S, M, L, XL)
**Link**: https://developer.android.com/reference/kotlin/androidx/compose/material3/Slider.composable
**Old**: https://developer.android.com/develop/ui/compose/components/slider

### Snackbar
**Link**: https://developer.android.com/develop/ui/compose/components/snackbar

### Switch
**Link**: https://developer.android.com/develop/ui/compose/components/switch

### Tabs
**Link**: https://developer.android.com/develop/ui/compose/components/tabs

### Text fields
**Link**: https://developer.android.com/reference/kotlin/androidx/compose/material3/OutlinedTextField.composable#OutlinedTextField

### Toolbars
**Updates**: The bottom app bar is no longer recommended and should be replaced with the docked toolbar, which functions similarly, but is shorter and has more flexibility. The floating toolbar was created for more versatility, greater amounts of actions, and more variety in where it's placed. Added floating toolbar with the following configurations (Layout: Horizontal or vertical, Color: Standard or vibrant, Flexibility: Can hold many elements and components. Can be paired with FAB)
**FlexibleBottomAppBar**: https://developer.android.com/reference/kotlin/androidx/compose/material3/FlexibleBottomAppBar.composable
**HorizontalFloatingToolbar**: https://developer.android.com/reference/kotlin/androidx/compose/material3/HorizontalFloatingToolbar.composable

### Tooltips
**Link**: https://developer.android.com/develop/ui/compose/components/tooltip

### Buttons

#### Button groups
Buttons allow users to take actions, and make choices, with a single tap. For full documentation on the usage, behavior, and attributes of different types of Material buttons, see:
**Common buttons**: https://github.com/material-components/material-components-android/blob/master/docs/components/CommonButton.md
**Button groups**: https://github.com/material-components/material-components-android/blob/master/docs/components/ButtonGroup.md
**Split button**: https://github.com/material-components/material-components-android/blob/master/docs/components/SplitButton.md
**Toggle button groups**: The segmented button is being deprecated. Use the connected button group instead
**Icon buttons**: https://github.com/material-components/material-components-android/blob/master/docs/components/IconButton.md
**Floating action buttons (FABs)**: https://github.com/material-components/material-components-android/blob/master/docs/components/FloatingActionButton.md
**Extended floating action buttons (Extended FABs)**: https://github.com/material-components/material-components-android/blob/master/docs/components/ExtendedFloatingActionButton.md

### Motion scheme
**Link**: https://developer.android.com/reference/kotlin/androidx/compose/material3/MotionScheme

### Navigation

#### Floating bar
**Link**: https://developer.android.com/reference/kotlin/androidx/compose/material3/HorizontalFloatingToolbar.composable

#### Navigation bar
**Link** https://developer.android.com/develop/ui/compose/components/navigation-bar

#### Navigation drawer
**Link**: https://developer.android.com/develop/ui/compose/components/drawer

#### Navigation rail
**Updates**: May 2025 -  A collapsed and expanded navigation rail have been introduced to replace the baseline nav rail. The expanded nav rail is meant to replace the navigation drawer. More on M3 Expressive
**Link**: https://github.com/material-components/material-components-android/blob/master/docs/components/NavigationRail.md

### Sheets

#### Bottom sheets
**Link**: https://developer.android.com/develop/ui/compose/components/bottom-sheets

#### Side sheets
**Link**: https://github.com/material-components/material-components-android/blob/master/docs/components/SideSheet.md
**M3**: https://m3.material.io/components/side-sheets/guidelines#4d992de0-362a-41b3-9537-9da4dec148af

### Date and Time

#### Date picker
**Link**: https://developer.android.com/develop/ui/compose/components/datepickers

#### Time picker
**Link**: https://developer.android.com/develop/ui/compose/components/time-pickers

### Loading & progress

#### Loading indicator
**Link**: https://developer.android.com/reference/kotlin/androidx/compose/material3/LoadingIndicator.composable

#### Progress indicator
**Update**: Aug 2024 - The progress indicators have configurations for height and wavy shape. Choose the visual style that best fits your product. Prefer Shape: Wavy
**Linear**: https://developer.android.com/reference/kotlin/androidx/compose/material3/LinearProgressIndicator.composable#LinearProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp,kotlin.Function1)
**Circular**: https://developer.android.com/reference/kotlin/androidx/compose/material3/CircularProgressIndicator.composable#CircularProgressIndicator(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.StrokeCap,androidx.compose.ui.unit.Dp)