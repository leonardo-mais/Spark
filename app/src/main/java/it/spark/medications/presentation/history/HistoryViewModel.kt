package it.spark.medications.presentation.history

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spark.medications.domain.model.DailyDoseItem
import it.spark.medications.domain.model.DoseLog
import it.spark.medications.domain.model.DoseStatus
import it.spark.medications.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

enum class DayComplianceColor {
    GREEN,
    YELLOW,
    RED,
    NONE
}

@Immutable
data class CalendarDayItem(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean,
    val complianceColor: DayComplianceColor
)

@Immutable
data class HistoryUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val calendarDays: List<CalendarDayItem> = emptyList(),
    val selectedDayDoses: List<DailyDoseItem> = emptyList(),
    val monthlyAdherencePercent: Int = 100,
    val isLoading: Boolean = false
)

sealed interface HistoryIntent {
    data class SelectDate(val date: LocalDate) : HistoryIntent
    object PreviousMonth : HistoryIntent
    object NextMonth : HistoryIntent
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: MedicationRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<HistoryUiState> = combine(_currentMonth, _selectedDate) { month, selectedDate ->
        Pair(month, selectedDate)
    }.flatMapLatest { (month, selectedDate) ->
        val prefix = "%04d-%02d".format(month.year, month.monthValue)
        val monthLogsFlow = repository.getLogsForMonth(prefix)
        val dailyDosesFlow = repository.getDailyDoses(selectedDate.toString(), selectedDate.dayOfWeek.value)

        combine(monthLogsFlow, dailyDosesFlow) { logs, dailyDoses ->
            val logsByDate = logs.groupBy { it.scheduledDate }
            val calendarDays = buildCalendarDays(month, selectedDate, logsByDate)

            // Monthly adherence
            val totalMonthLogged = logs.size
            val takenMonthCount = logs.count { it.status == DoseStatus.TAKEN }
            val adherence = if (totalMonthLogged > 0) (takenMonthCount * 100) / totalMonthLogged else 100

            HistoryUiState(
                currentMonth = month,
                selectedDate = selectedDate,
                calendarDays = calendarDays,
                selectedDayDoses = dailyDoses,
                monthlyAdherencePercent = adherence,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState()
    )

    fun onIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.SelectDate -> _selectedDate.value = intent.date
            HistoryIntent.PreviousMonth -> _currentMonth.update { it.minusMonths(1) }
            HistoryIntent.NextMonth -> _currentMonth.update { it.plusMonths(1) }
        }
    }

    private fun buildCalendarDays(
        month: YearMonth,
        selectedDate: LocalDate,
        logsByDate: Map<String, List<DoseLog>>
    ): List<CalendarDayItem> {
        val days = mutableListOf<CalendarDayItem>()
        val firstOfMonth = month.atDay(1)
        val firstDayOfWeek = firstOfMonth.dayOfWeek.value // 1 = Mon ... 7 = Sun

        // Leading days from previous month
        val prevMonth = month.minusMonths(1)
        for (i in (firstDayOfWeek - 1) downTo 1) {
            val d = firstOfMonth.minusDays(i.toLong())
            days.add(CalendarDayItem(d, isCurrentMonth = false, isSelected = d == selectedDate, complianceColor = DayComplianceColor.NONE))
        }

        // Days of current month
        val daysInMonth = month.lengthOfMonth()
        for (dayNum in 1..daysInMonth) {
            val d = month.atDay(dayNum)
            val dateStr = d.toString()
            val dayLogs = logsByDate[dateStr] ?: emptyList()

            val color = if (dayLogs.isEmpty()) {
                DayComplianceColor.NONE
            } else {
                val taken = dayLogs.count { it.status == DoseStatus.TAKEN }
                val total = dayLogs.size
                when {
                    taken == total -> DayComplianceColor.GREEN
                    taken > 0 -> DayComplianceColor.YELLOW
                    else -> DayComplianceColor.RED
                }
            }

            days.add(CalendarDayItem(d, isCurrentMonth = true, isSelected = d == selectedDate, complianceColor = color))
        }

        // Trailing days from next month to complete grid (up to 42 cells)
        val remaining = (7 - (days.size % 7)) % 7
        for (i in 1..remaining) {
            val d = month.atDay(daysInMonth).plusDays(i.toLong())
            days.add(CalendarDayItem(d, isCurrentMonth = false, isSelected = d == selectedDate, complianceColor = DayComplianceColor.NONE))
        }

        return days
    }
}
