package it.spark.medications.presentation.editor

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spark.medications.domain.model.DoseSchedule
import it.spark.medications.domain.model.MealPresets
import it.spark.medications.domain.model.Medicine
import it.spark.medications.domain.model.MedicineForm
import it.spark.medications.domain.model.NotificationPriority
import it.spark.medications.domain.repository.MedicationRepository
import it.spark.medications.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class MedicineEditorUiState(
    val medicineId: Long = 0,
    val name: String = "",
    val dosage: String = "",
    val form: MedicineForm = MedicineForm.PILL,
    val selectedDaysOfWeek: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7), // All days by default
    val reminderTimes: List<Int> = emptyList(), // Minutes from midnight
    val priority: NotificationPriority = NotificationPriority.MEDIUM,
    val instructions: String = "",
    val boxes: String = "1",
    val unitsPerBox: String = "20",
    val alertBelowUnits: String = "5",
    val mealPresets: MealPresets = MealPresets(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val totalUnitsCalculated: Int
        get() {
            val b = boxes.toIntOrNull() ?: 0
            val u = unitsPerBox.toIntOrNull() ?: 0
            return b * u
        }
}

sealed interface MedicineEditorIntent {
    data class LoadMedicine(val id: Long) : MedicineEditorIntent
    data class NameChanged(val name: String) : MedicineEditorIntent
    data class DosageChanged(val dosage: String) : MedicineEditorIntent
    data class FormSelected(val form: MedicineForm) : MedicineEditorIntent
    data class ToggleDayOfWeek(val dayNumber: Int) : MedicineEditorIntent
    data class AddReminderTime(val minutesFromMidnight: Int) : MedicineEditorIntent
    data class RemoveReminderTime(val minutesFromMidnight: Int) : MedicineEditorIntent
    data class PrioritySelected(val priority: NotificationPriority) : MedicineEditorIntent
    data class InstructionsChanged(val instructions: String) : MedicineEditorIntent
    data class BoxesChanged(val boxes: String) : MedicineEditorIntent
    data class UnitsPerBoxChanged(val units: String) : MedicineEditorIntent
    data class AlertBelowChanged(val alert: String) : MedicineEditorIntent
    object Save : MedicineEditorIntent
    object Delete : MedicineEditorIntent
}

sealed interface MedicineEditorEffect {
    object SavedSuccessfully : MedicineEditorEffect
    object DeletedSuccessfully : MedicineEditorEffect
    data class ShowError(val message: String) : MedicineEditorEffect
}

@HiltViewModel
class MedicineEditorViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicineEditorUiState())
    val uiState: StateFlow<MedicineEditorUiState> = _uiState.asStateFlow()

    private val _effects = Channel<MedicineEditorEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val presets = settingsRepository.mealPresets.first()
            _uiState.update { it.copy(mealPresets = presets) }
        }
    }

    fun onIntent(intent: MedicineEditorIntent) {
        when (intent) {
            is MedicineEditorIntent.LoadMedicine -> loadMedicine(intent.id)
            is MedicineEditorIntent.NameChanged -> _uiState.update { it.copy(name = intent.name) }
            is MedicineEditorIntent.DosageChanged -> _uiState.update { it.copy(dosage = intent.dosage) }
            is MedicineEditorIntent.FormSelected -> _uiState.update { it.copy(form = intent.form) }
            is MedicineEditorIntent.ToggleDayOfWeek -> {
                _uiState.update { state ->
                    val current = state.selectedDaysOfWeek.toMutableSet()
                    if (current.contains(intent.dayNumber)) {
                        if (current.size > 1) current.remove(intent.dayNumber) // keep at least 1
                    } else {
                        current.add(intent.dayNumber)
                    }
                    state.copy(selectedDaysOfWeek = current)
                }
            }
            is MedicineEditorIntent.AddReminderTime -> {
                _uiState.update { state ->
                    val current = state.reminderTimes.toMutableList()
                    if (!current.contains(intent.minutesFromMidnight)) {
                        current.add(intent.minutesFromMidnight)
                        current.sort()
                    }
                    state.copy(reminderTimes = current)
                }
            }
            is MedicineEditorIntent.RemoveReminderTime -> {
                _uiState.update { state ->
                    val current = state.reminderTimes.toMutableList()
                    current.remove(intent.minutesFromMidnight)
                    state.copy(reminderTimes = current)
                }
            }
            is MedicineEditorIntent.PrioritySelected -> _uiState.update { it.copy(priority = intent.priority) }
            is MedicineEditorIntent.InstructionsChanged -> _uiState.update { it.copy(instructions = intent.instructions) }
            is MedicineEditorIntent.BoxesChanged -> _uiState.update { it.copy(boxes = intent.boxes.filter { c -> c.isDigit() }) }
            is MedicineEditorIntent.UnitsPerBoxChanged -> _uiState.update { it.copy(unitsPerBox = intent.units.filter { c -> c.isDigit() }) }
            is MedicineEditorIntent.AlertBelowChanged -> _uiState.update { it.copy(alertBelowUnits = intent.alert.filter { c -> c.isDigit() }) }
            MedicineEditorIntent.Save -> saveMedicine()
            MedicineEditorIntent.Delete -> deleteMedicine()
        }
    }

    private fun loadMedicine(id: Long) {
        if (id <= 0) return
        viewModelScope.launch {
            val med = medicationRepository.getMedicineById(id).first() ?: return@launch
            _uiState.update {
                it.copy(
                    medicineId = med.id,
                    name = med.name,
                    dosage = med.dosage,
                    form = med.form,
                    selectedDaysOfWeek = med.daysOfWeek,
                    reminderTimes = med.schedules.map { s -> s.timeMinutes }.sorted(),
                    priority = med.priority,
                    instructions = med.instructions,
                    boxes = med.boxes.toString(),
                    unitsPerBox = med.unitsPerBox.toString(),
                    alertBelowUnits = med.alertBelowUnits.toString()
                )
            }
        }
    }

    private fun saveMedicine() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            viewModelScope.launch {
                _effects.send(MedicineEditorEffect.ShowError("Inserisci il nome della medicina"))
            }
            return
        }
        if (state.reminderTimes.isEmpty()) {
            viewModelScope.launch {
                _effects.send(MedicineEditorEffect.ShowError("Seleziona almeno un orario di notifica"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val boxes = state.boxes.toIntOrNull() ?: 1
            val unitsPerBox = state.unitsPerBox.toIntOrNull() ?: 20
            val alertBelow = state.alertBelowUnits.toIntOrNull() ?: 5
            val totalUnits = boxes * unitsPerBox

            val medicine = Medicine(
                id = state.medicineId,
                name = state.name.trim(),
                dosage = state.dosage.trim(),
                form = state.form,
                daysOfWeek = state.selectedDaysOfWeek,
                priority = state.priority,
                instructions = state.instructions.trim(),
                boxes = boxes,
                unitsPerBox = unitsPerBox,
                totalUnits = totalUnits,
                alertBelowUnits = alertBelow
            )

            val schedules = state.reminderTimes.map { DoseSchedule(timeMinutes = it, doseUnits = 1) }

            medicationRepository.saveMedicine(medicine, schedules)
            _effects.send(MedicineEditorEffect.SavedSuccessfully)
        }
    }

    private fun deleteMedicine() {
        val id = _uiState.value.medicineId
        if (id <= 0) return
        viewModelScope.launch {
            medicationRepository.deleteMedicine(id)
            _effects.send(MedicineEditorEffect.DeletedSuccessfully)
        }
    }
}
