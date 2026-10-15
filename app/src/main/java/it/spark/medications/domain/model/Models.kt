package it.spark.medications.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
enum class MedicineForm {
    PILL,
    CAPSULE,
    DROPS,
    SYRUP,
    INJECTION,
    SACHET,
    INHALER,
    OTHER
}

@Serializable
enum class NotificationPriority {
    LOW,
    MEDIUM,
    HIGH
}

@Serializable
enum class DoseStatus {
    PENDING,
    TAKEN,
    SKIPPED,
    MISSED
}

@Immutable
@Serializable
data class Medicine(
    val id: Long = 0,
    val name: String,
    val dosage: String,
    val form: MedicineForm,
    val daysOfWeek: Set<Int>, // 1 = Monday ... 7 = Sunday
    val priority: NotificationPriority,
    val instructions: String,
    val boxes: Int,
    val unitsPerBox: Int,
    val totalUnits: Int,
    val alertBelowUnits: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false,
    val schedules: List<DoseSchedule> = emptyList()
) {
    val isLowStock: Boolean
        get() = totalUnits <= alertBelowUnits
}

@Immutable
@Serializable
data class DoseSchedule(
    val id: Long = 0,
    val medicineId: Long = 0,
    val timeMinutes: Int, // Minutes from midnight (e.g. 8:00 = 480)
    val doseUnits: Int = 1
) {
    val formattedTime: String
        get() {
            val hours = timeMinutes / 60
            val minutes = timeMinutes % 60
            return "%02d:%02d".format(hours, minutes)
        }
}

@Immutable
@Serializable
data class DoseLog(
    val id: Long = 0,
    val medicineId: Long,
    val scheduleId: Long,
    val scheduledDate: String, // YYYY-MM-DD
    val scheduledTimeMinutes: Int,
    val status: DoseStatus,
    val actionTimestamp: Long? = null,
    val repeatCountTriggered: Int = 0
)

@Immutable
data class DailyDoseItem(
    val medicine: Medicine,
    val schedule: DoseSchedule,
    val log: DoseLog?,
    val dateString: String
) {
    val status: DoseStatus
        get() = log?.status ?: DoseStatus.PENDING
}

@Immutable
@Serializable
data class PrioritySettings(
    val lowAlertCount: Int = 1,
    val lowIntervalMinutes: Int = 0,
    val mediumAlertCount: Int = 3,
    val mediumIntervalMinutes: Int = 10,
    val highAlertCount: Int = 5,
    val highIntervalMinutes: Int = 5
)

@Immutable
@Serializable
data class MealPresets(
    val breakfastMinutes: Int = 8 * 60, // 08:00
    val lunchMinutes: Int = 13 * 60,     // 13:00
    val dinnerMinutes: Int = 20 * 60     // 20:00
)

@Serializable
data class BackupPayload(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val medicines: List<Medicine>,
    val logs: List<DoseLog>,
    val prioritySettings: PrioritySettings,
    val mealPresets: MealPresets
)
