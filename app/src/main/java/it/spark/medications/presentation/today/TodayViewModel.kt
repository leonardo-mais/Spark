package it.spark.medications.presentation.today

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spark.medications.domain.model.DailyDoseItem
import it.spark.medications.domain.model.DoseStatus
import it.spark.medications.domain.repository.MedicationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@Immutable
data class WeekDayItem(
    val date: LocalDate,
    val dayOfWeekNumber: Int, // 1 = Monday ... 7 = Sunday
    val isSelected: Boolean,
    val isToday: Boolean
)

@Immutable
data class TodayUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val weekDays: List<WeekDayItem> = emptyList(),
    val doseItems: List<DailyDoseItem> = emptyList(),
    val isLoading: Boolean = false,
    val takenCount: Int = 0,
    val totalCount: Int = 0
) {
    val progress: Float
        get() = if (totalCount == 0) 0f else (takenCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
}

sealed interface TodayIntent {
    data class SelectDate(val date: LocalDate) : TodayIntent
    data class MarkTaken(val item: DailyDoseItem) : TodayIntent
    data class MarkSkipped(val item: DailyDoseItem) : TodayIntent
    object OpenSettings : TodayIntent
}

sealed interface TodayEffect {
    object NavigateToSettings : TodayEffect
    data class ShowSnackbar(val message: String) : TodayEffect
}

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val repository: MedicationRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _effects = Channel<TodayEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<TodayUiState> = _selectedDate
        .flatMapLatest { date ->
            val dateString = date.toString()
            val dayOfWeek = date.dayOfWeek.value
            repository.getDailyDoses(dateString, dayOfWeek)
                .mapDailyState(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TodayUiState(
                selectedDate = LocalDate.now(),
                weekDays = generateWeekDays(LocalDate.now(), LocalDate.now())
            )
        )

    fun onIntent(intent: TodayIntent) {
        when (intent) {
            is TodayIntent.SelectDate -> {
                _selectedDate.value = intent.date
            }
            is TodayIntent.MarkTaken -> {
                viewModelScope.launch {
                    val item = intent.item
                    repository.recordDoseTaken(
                        medicineId = item.medicine.id,
                        scheduleId = item.schedule.id,
                        dateString = item.dateString,
                        timeMinutes = item.schedule.timeMinutes
                    )
                }
            }
            is TodayIntent.MarkSkipped -> {
                viewModelScope.launch {
                    val item = intent.item
                    repository.recordDoseSkipped(
                        medicineId = item.medicine.id,
                        scheduleId = item.schedule.id,
                        dateString = item.dateString,
                        timeMinutes = item.schedule.timeMinutes
                    )
                }
            }
            TodayIntent.OpenSettings -> {
                viewModelScope.launch {
                    _effects.send(TodayEffect.NavigateToSettings)
                }
            }
        }
    }

    private fun kotlinx.coroutines.flow.Flow<List<DailyDoseItem>>.mapDailyState(selectedDate: LocalDate) =
        this.combine(MutableStateFlow(selectedDate)) { doses, date ->
            val today = LocalDate.now()
            val week = generateWeekDays(date, today)
            val taken = doses.count { it.status == DoseStatus.TAKEN }
            val total = doses.size
            TodayUiState(
                selectedDate = date,
                weekDays = week,
                doseItems = doses,
                isLoading = false,
                takenCount = taken,
                totalCount = total
            )
        }

    private fun generateWeekDays(selected: LocalDate, today: LocalDate): List<WeekDayItem> {
        val startOfWeek = selected.minusDays((selected.dayOfWeek.value - 1).toLong())
        return (0..6).map { offset ->
            val d = startOfWeek.plusDays(offset.toLong())
            WeekDayItem(
                date = d,
                dayOfWeekNumber = d.dayOfWeek.value,
                isSelected = d == selected,
                isToday = d == today
            )
        }
    }
}
