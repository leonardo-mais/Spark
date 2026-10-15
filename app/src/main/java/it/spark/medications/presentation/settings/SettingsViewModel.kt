package it.spark.medications.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spark.medications.domain.model.BackupPayload
import it.spark.medications.domain.model.MealPresets
import it.spark.medications.domain.model.PrioritySettings
import it.spark.medications.domain.repository.MedicationRepository
import it.spark.medications.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Immutable
data class SettingsUiState(
    val mealPresets: MealPresets = MealPresets(),
    val prioritySettings: PrioritySettings = PrioritySettings(),
    val isExporting: Boolean = false,
    val isImporting: Boolean = false
)

sealed interface SettingsIntent {
    data class UpdateMealBreakfast(val minutes: Int) : SettingsIntent
    data class UpdateMealLunch(val minutes: Int) : SettingsIntent
    data class UpdateMealDinner(val minutes: Int) : SettingsIntent

    data class UpdateLowPriority(val count: Int, val interval: Int) : SettingsIntent
    data class UpdateMediumPriority(val count: Int, val interval: Int) : SettingsIntent
    data class UpdateHighPriority(val count: Int, val interval: Int) : SettingsIntent

    data class ExportBackup(val uri: Uri, val context: Context) : SettingsIntent
    data class ImportBackup(val uri: Uri, val context: Context) : SettingsIntent
}

sealed interface SettingsEffect {
    data class ShowToast(val message: String) : SettingsEffect
    data class TriggerShare(val jsonContent: String) : SettingsEffect
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    private val _effects = Channel<SettingsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.mealPresets,
        settingsRepository.prioritySettings
    ) { presets, priorities ->
        SettingsUiState(
            mealPresets = presets,
            prioritySettings = priorities
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.UpdateMealBreakfast -> {
                val updated = uiState.value.mealPresets.copy(breakfastMinutes = intent.minutes)
                viewModelScope.launch { settingsRepository.saveMealPresets(updated) }
            }
            is SettingsIntent.UpdateMealLunch -> {
                val updated = uiState.value.mealPresets.copy(lunchMinutes = intent.minutes)
                viewModelScope.launch { settingsRepository.saveMealPresets(updated) }
            }
            is SettingsIntent.UpdateMealDinner -> {
                val updated = uiState.value.mealPresets.copy(dinnerMinutes = intent.minutes)
                viewModelScope.launch { settingsRepository.saveMealPresets(updated) }
            }
            is SettingsIntent.UpdateLowPriority -> {
                val updated = uiState.value.prioritySettings.copy(
                    lowAlertCount = intent.count,
                    lowIntervalMinutes = intent.interval
                )
                viewModelScope.launch { settingsRepository.savePrioritySettings(updated) }
            }
            is SettingsIntent.UpdateMediumPriority -> {
                val updated = uiState.value.prioritySettings.copy(
                    mediumAlertCount = intent.count,
                    mediumIntervalMinutes = intent.interval
                )
                viewModelScope.launch { settingsRepository.savePrioritySettings(updated) }
            }
            is SettingsIntent.UpdateHighPriority -> {
                val updated = uiState.value.prioritySettings.copy(
                    highAlertCount = intent.count,
                    highIntervalMinutes = intent.interval
                )
                viewModelScope.launch { settingsRepository.savePrioritySettings(updated) }
            }
            is SettingsIntent.ExportBackup -> exportBackup(intent.uri, intent.context)
            is SettingsIntent.ImportBackup -> importBackup(intent.uri, intent.context)
        }
    }

    suspend fun getExportJsonString(): String {
        val payload = medicationRepository.exportBackup()
        return json.encodeToString(BackupPayload.serializer(), payload)
    }

    private fun exportBackup(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val payload = medicationRepository.exportBackup()
                val jsonString = json.encodeToString(BackupPayload.serializer(), payload)
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(jsonString.toByteArray())
                }
                _effects.send(SettingsEffect.ShowToast("Backup esportato con successo"))
            } catch (e: Exception) {
                _effects.send(SettingsEffect.ShowToast("Errore durante l'esportazione: ${e.localizedMessage}"))
            }
        }
    }

    private fun importBackup(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.bufferedReader().readText()
                } ?: throw IllegalArgumentException("Impossibile leggere il file")

                val payload = json.decodeFromString(BackupPayload.serializer(), jsonString)
                medicationRepository.restoreBackup(payload)
                _effects.send(SettingsEffect.ShowToast("Dati ripristinati e allarmi riprogrammati!"))
            } catch (e: Exception) {
                _effects.send(SettingsEffect.ShowToast("Errore di ripristino: file JSON non valido"))
            }
        }
    }
}
