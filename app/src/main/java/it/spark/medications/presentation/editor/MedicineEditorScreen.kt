package it.spark.medications.presentation.editor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.spark.medications.R
import it.spark.medications.domain.model.MedicineForm
import it.spark.medications.domain.model.NotificationPriority
import it.spark.medications.presentation.common.CircularBackButton
import it.spark.medications.presentation.common.GroupedCard
import it.spark.medications.presentation.common.MedicineFormBadge
import it.spark.medications.presentation.theme.ExpressiveType
import it.spark.medications.presentation.theme.FlexHeadlineFont
import it.spark.medications.presentation.theme.FlexChipLabelFont
import it.spark.medications.presentation.theme.FlexStatValueFont
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineEditorScreen(
    viewModel: MedicineEditorViewModel,
    medicineId: Long?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showTimePickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(medicineId) {
        if (medicineId != null && medicineId > 0) {
            viewModel.onIntent(MedicineEditorIntent.LoadMedicine(medicineId))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                MedicineEditorEffect.SavedSuccessfully -> onNavigateBack()
                MedicineEditorEffect.DeletedSuccessfully -> onNavigateBack()
                is MedicineEditorEffect.ShowError -> {
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.medicineId > 0) {
                            stringResource(R.string.editor_edit_title)
                        } else {
                            stringResource(R.string.editor_add_title)
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    CircularBackButton(onClick = onNavigateBack, modifier = Modifier.padding(start = 8.dp))
                },
                actions = {
                    if (uiState.medicineId > 0) {
                        IconButton(onClick = { viewModel.onIntent(MedicineEditorIntent.Delete) }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    FilledTonalButton(
                        onClick = { viewModel.onIntent(MedicineEditorIntent.Save) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.save))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Dettagli Principali (Nome & Dosaggio)
            item {
                GroupedCard {
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.onIntent(MedicineEditorIntent.NameChanged(it)) },
                        label = { Text(stringResource(R.string.editor_field_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.dosage,
                        onValueChange = { viewModel.onIntent(MedicineEditorIntent.DosageChanged(it)) },
                        label = { Text(stringResource(R.string.editor_field_dosage)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            // 2. Forma della Medicina
            item {
                GroupedCard {
                    Text(
                        text = stringResource(R.string.editor_section_form),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(MedicineForm.values()) { form ->
                            val isSelected = uiState.form == form
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onIntent(MedicineEditorIntent.FormSelected(form)) },
                                leadingIcon = {
                                    MedicineFormBadge(form = form, modifier = Modifier.size(24.dp))
                                },
                                label = {
                                    val nameRes = when (form) {
                                        MedicineForm.PILL -> R.string.form_pill
                                        MedicineForm.CAPSULE -> R.string.form_capsule
                                        MedicineForm.DROPS -> R.string.form_drops
                                        MedicineForm.SYRUP -> R.string.form_syrup
                                        MedicineForm.INJECTION -> R.string.form_injection
                                        MedicineForm.SACHET -> R.string.form_sachet
                                        MedicineForm.INHALER -> R.string.form_inhaler
                                        MedicineForm.OTHER -> R.string.form_other
                                    }
                                    Text(stringResource(nameRes))
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // 3. Giorni della Settimana (M T W T F S S)
            item {
                GroupedCard {
                    Text(
                        text = stringResource(R.string.editor_section_days),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(
                            Pair(1, R.string.day_mon),
                            Pair(2, R.string.day_tue),
                            Pair(3, R.string.day_wed),
                            Pair(4, R.string.day_thu),
                            Pair(5, R.string.day_fri),
                            Pair(6, R.string.day_sat),
                            Pair(7, R.string.day_sun)
                        ).forEach { (dayNum, strRes) ->
                            val isSelected = uiState.selectedDaysOfWeek.contains(dayNum)
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.05f else 0.95f,
                                animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f),
                                label = "day_scale"
                            )

                            val bg = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest
                            val fg = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .scale(scale)
                                    .clip(CircleShape)
                                    .background(bg)
                                    .clickable { viewModel.onIntent(MedicineEditorIntent.ToggleDayOfWeek(dayNum)) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(strRes),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = fg,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // 4. Orari di Notifica (Preset pasti + Custom TimePicker)
            item {
                GroupedCard {
                    Text(
                        text = stringResource(R.string.editor_section_times),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preset Buttons (Colazione, Pranzo, Cena)
                    val presets = uiState.mealPresets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MealPresetChip(
                            label = stringResource(R.string.preset_breakfast),
                            minutes = presets.breakfastMinutes,
                            isSelected = uiState.reminderTimes.contains(presets.breakfastMinutes),
                            onToggle = {
                                if (uiState.reminderTimes.contains(presets.breakfastMinutes)) {
                                    viewModel.onIntent(MedicineEditorIntent.RemoveReminderTime(presets.breakfastMinutes))
                                } else {
                                    viewModel.onIntent(MedicineEditorIntent.AddReminderTime(presets.breakfastMinutes))
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        MealPresetChip(
                            label = stringResource(R.string.preset_lunch),
                            minutes = presets.lunchMinutes,
                            isSelected = uiState.reminderTimes.contains(presets.lunchMinutes),
                            onToggle = {
                                if (uiState.reminderTimes.contains(presets.lunchMinutes)) {
                                    viewModel.onIntent(MedicineEditorIntent.RemoveReminderTime(presets.lunchMinutes))
                                } else {
                                    viewModel.onIntent(MedicineEditorIntent.AddReminderTime(presets.lunchMinutes))
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        MealPresetChip(
                            label = stringResource(R.string.preset_dinner),
                            minutes = presets.dinnerMinutes,
                            isSelected = uiState.reminderTimes.contains(presets.dinnerMinutes),
                            onToggle = {
                                if (uiState.reminderTimes.contains(presets.dinnerMinutes)) {
                                    viewModel.onIntent(MedicineEditorIntent.RemoveReminderTime(presets.dinnerMinutes))
                                } else {
                                    viewModel.onIntent(MedicineEditorIntent.AddReminderTime(presets.dinnerMinutes))
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // + Orario Personalizzato Button
                    OutlinedButton(
                        onClick = { showTimePickerDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.AccessTime, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.editor_add_custom_time))
                    }

                    // Selected Times List
                    if (uiState.reminderTimes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.reminderTimes) { timeMinutes ->
                                val formatted = "%02d:%02d".format(timeMinutes / 60, timeMinutes % 60)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formatted,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = "Rimuovi",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable {
                                                    viewModel.onIntent(MedicineEditorIntent.RemoveReminderTime(timeMinutes))
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Priorità Notifica (Low, Medium, High)
            item {
                GroupedCard {
                    Text(
                        text = stringResource(R.string.editor_section_priority),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NotificationPriority.values().forEach { prio ->
                            val isSelected = uiState.priority == prio
                            val (titleRes, bg, fg) = when (prio) {
                                NotificationPriority.LOW -> Triple(
                                    R.string.editor_priority_low,
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    MaterialTheme.colorScheme.onSurface
                                )
                                NotificationPriority.MEDIUM -> Triple(
                                    R.string.editor_priority_medium,
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                NotificationPriority.HIGH -> Triple(
                                    R.string.editor_priority_high,
                                    MaterialTheme.colorScheme.errorContainer,
                                    MaterialTheme.colorScheme.onErrorContainer
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) bg else MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.onIntent(MedicineEditorIntent.PrioritySelected(prio)) }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(titleRes),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) fg else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Istruzioni
            item {
                GroupedCard {
                    Text(
                        text = stringResource(R.string.editor_section_instructions),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.instructions,
                        onValueChange = { viewModel.onIntent(MedicineEditorIntent.InstructionsChanged(it)) },
                        placeholder = { Text(stringResource(R.string.editor_instructions_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            }

            // 7. Gestione Scorte
            item {
                GroupedCard {
                    Text(
                        text = stringResource(R.string.editor_section_stock),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.boxes,
                            onValueChange = { viewModel.onIntent(MedicineEditorIntent.BoxesChanged(it)) },
                            label = { Text(stringResource(R.string.editor_field_boxes)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.unitsPerBox,
                            onValueChange = { viewModel.onIntent(MedicineEditorIntent.UnitsPerBoxChanged(it)) },
                            label = { Text(stringResource(R.string.editor_field_units_per_box)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.alertBelowUnits,
                        onValueChange = { viewModel.onIntent(MedicineEditorIntent.AlertBelowChanged(it)) },
                        label = { Text(stringResource(R.string.editor_field_alert_below)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.editor_total_calculated, uiState.totalUnitsCalculated),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Save CTA at bottom
            item {
                Button(
                    onClick = { viewModel.onIntent(MedicineEditorIntent.Save) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Material 3 Time Picker Dialog
    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val minutes = timePickerState.hour * 60 + timePickerState.minute
                    viewModel.onIntent(MedicineEditorIntent.AddReminderTime(minutes))
                    showTimePickerDialog = false
                }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
fun MealPresetChip(
    label: String,
    minutes: Int,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatted = "%02d:%02d".format(minutes / 60, minutes % 60)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
