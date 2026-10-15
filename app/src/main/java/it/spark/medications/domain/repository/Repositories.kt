package it.spark.medications.domain.repository

import it.spark.medications.domain.model.BackupPayload
import it.spark.medications.domain.model.DailyDoseItem
import it.spark.medications.domain.model.DoseLog
import it.spark.medications.domain.model.DoseSchedule
import it.spark.medications.domain.model.MealPresets
import it.spark.medications.domain.model.Medicine
import it.spark.medications.domain.model.PrioritySettings
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    fun getActiveMedicines(): Flow<List<Medicine>>
    suspend fun getActiveMedicinesList(): List<Medicine>
    fun getMedicineById(id: Long): Flow<Medicine?>
    suspend fun saveMedicine(medicine: Medicine, schedules: List<DoseSchedule>): Long
    suspend fun deleteMedicine(id: Long)
    suspend fun refillStock(id: Long, boxesToAdd: Int)
    suspend fun recordDoseTaken(medicineId: Long, scheduleId: Long, dateString: String, timeMinutes: Int)
    suspend fun recordDoseSkipped(medicineId: Long, scheduleId: Long, dateString: String, timeMinutes: Int)
    fun getDailyDoses(dateString: String, dayOfWeek: Int): Flow<List<DailyDoseItem>>
    fun getLogsForMonth(yearMonthPrefix: String): Flow<List<DoseLog>>
    suspend fun exportBackup(): BackupPayload
    suspend fun restoreBackup(payload: BackupPayload)
}

interface SettingsRepository {
    val mealPresets: Flow<MealPresets>
    fun getMealPresetsSync(): MealPresets
    suspend fun saveMealPresets(presets: MealPresets)

    val prioritySettings: Flow<PrioritySettings>
    fun getPrioritySettingsSync(): PrioritySettings
    suspend fun savePrioritySettings(settings: PrioritySettings)
}
