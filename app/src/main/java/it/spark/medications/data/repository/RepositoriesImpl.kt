package it.spark.medications.data.repository

import it.spark.medications.alarm.MedicationAlarmScheduler
import it.spark.medications.data.local.dao.DoseLogDao
import it.spark.medications.data.local.dao.DoseScheduleDao
import it.spark.medications.data.local.dao.MedicineDao
import it.spark.medications.data.local.entity.DoseLogEntity
import it.spark.medications.data.local.entity.DoseScheduleEntity
import it.spark.medications.data.local.entity.MedicineEntity
import it.spark.medications.data.local.prefs.SettingsPreferences
import it.spark.medications.domain.model.BackupPayload
import it.spark.medications.domain.model.DailyDoseItem
import it.spark.medications.domain.model.DoseLog
import it.spark.medications.domain.model.DoseSchedule
import it.spark.medications.domain.model.DoseStatus
import it.spark.medications.domain.model.MealPresets
import it.spark.medications.domain.model.Medicine
import it.spark.medications.domain.model.PrioritySettings
import it.spark.medications.domain.repository.MedicationRepository
import it.spark.medications.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationRepositoryImpl @Inject constructor(
    private val medicineDao: MedicineDao,
    private val doseScheduleDao: DoseScheduleDao,
    private val doseLogDao: DoseLogDao,
    private val settingsPreferences: SettingsPreferences,
    private val alarmScheduler: MedicationAlarmScheduler
) : MedicationRepository {

    override fun getActiveMedicines(): Flow<List<Medicine>> {
        return medicineDao.getAllActive().map { list ->
            list.map { entity ->
                val schedules = doseScheduleDao.getSchedulesForMedicineList(entity.id)
                    .map { it.toDomain() }
                entity.toDomain(schedules)
            }
        }
    }

    override suspend fun getActiveMedicinesList(): List<Medicine> {
        val list = medicineDao.getAllActiveList()
        return list.map { entity ->
            val schedules = doseScheduleDao.getSchedulesForMedicineList(entity.id)
                .map { it.toDomain() }
            entity.toDomain(schedules)
        }
    }

    override fun getMedicineById(id: Long): Flow<Medicine?> {
        return medicineDao.getByIdFlow(id).map { entity ->
            if (entity == null) null
            else {
                val schedules = doseScheduleDao.getSchedulesForMedicineList(entity.id)
                    .map { it.toDomain() }
                entity.toDomain(schedules)
            }
        }
    }

    override suspend fun saveMedicine(medicine: Medicine, schedules: List<DoseSchedule>): Long {
        val entity = MedicineEntity.fromDomain(medicine)
        val medId = if (medicine.id == 0L) {
            medicineDao.insert(entity)
        } else {
            medicineDao.update(entity)
            medicine.id
        }

        // Replace schedules
        doseScheduleDao.deleteForMedicine(medId)
        val scheduleEntities = schedules.map { DoseScheduleEntity.fromDomain(it, medId) }
        doseScheduleDao.insertAll(scheduleEntities)

        // Reschedule alarms for this medicine
        alarmScheduler.rescheduleAllAlarms()

        return medId
    }

    override suspend fun deleteMedicine(id: Long) {
        val entity = medicineDao.getById(id)
        if (entity != null) {
            medicineDao.delete(entity)
            alarmScheduler.rescheduleAllAlarms()
        }
    }

    override suspend fun refillStock(id: Long, boxesToAdd: Int) {
        if (boxesToAdd > 0) {
            medicineDao.refillBoxes(id, boxesToAdd)
        }
    }

    override suspend fun recordDoseTaken(
        medicineId: Long,
        scheduleId: Long,
        dateString: String,
        timeMinutes: Int
    ) {
        // Decrement stock by 1
        medicineDao.decrementStock(medicineId, 1)

        val existing = doseLogDao.getLog(medicineId, scheduleId, dateString)
        val log = DoseLogEntity(
            id = existing?.id ?: 0,
            medicineId = medicineId,
            scheduleId = scheduleId,
            scheduledDate = dateString,
            scheduledTimeMinutes = timeMinutes,
            status = DoseStatus.TAKEN.name,
            actionTimestamp = System.currentTimeMillis(),
            repeatCountTriggered = existing?.repeatCountTriggered ?: 0
        )
        doseLogDao.insertOrUpdate(log)

        // Cancel pending repeats for this dose
        alarmScheduler.cancelDoseAlert(medicineId, scheduleId)
    }

    override suspend fun recordDoseSkipped(
        medicineId: Long,
        scheduleId: Long,
        dateString: String,
        timeMinutes: Int
    ) {
        val existing = doseLogDao.getLog(medicineId, scheduleId, dateString)
        val log = DoseLogEntity(
            id = existing?.id ?: 0,
            medicineId = medicineId,
            scheduleId = scheduleId,
            scheduledDate = dateString,
            scheduledTimeMinutes = timeMinutes,
            status = DoseStatus.SKIPPED.name,
            actionTimestamp = System.currentTimeMillis(),
            repeatCountTriggered = existing?.repeatCountTriggered ?: 0
        )
        doseLogDao.insertOrUpdate(log)

        // Cancel pending repeats
        alarmScheduler.cancelDoseAlert(medicineId, scheduleId)
    }

    override fun getDailyDoses(dateString: String, dayOfWeek: Int): Flow<List<DailyDoseItem>> {
        val medicinesFlow = getActiveMedicines()
        val logsFlow = doseLogDao.getLogsForDate(dateString)

        return combine(medicinesFlow, logsFlow) { medicines, logEntities ->
            val logs = logEntities.map { it.toDomain() }
            val logMap = logs.associateBy { "${it.medicineId}_${it.scheduleId}" }

            val items = mutableListOf<DailyDoseItem>()
            for (medicine in medicines) {
                if (medicine.daysOfWeek.contains(dayOfWeek)) {
                    for (schedule in medicine.schedules) {
                        val key = "${medicine.id}_${schedule.id}"
                        items.add(
                            DailyDoseItem(
                                medicine = medicine,
                                schedule = schedule,
                                log = logMap[key],
                                dateString = dateString
                            )
                        )
                    }
                }
            }
            items.sortedBy { it.schedule.timeMinutes }
        }
    }

    override fun getLogsForMonth(yearMonthPrefix: String): Flow<List<DoseLog>> {
        return doseLogDao.getLogsForMonth(yearMonthPrefix).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun exportBackup(): BackupPayload {
        val medicines = getActiveMedicinesList()
        val logs = doseLogDao.getAllLogsList().map { it.toDomain() }
        return BackupPayload(
            medicines = medicines,
            logs = logs,
            prioritySettings = settingsPreferences.getPrioritySettings(),
            mealPresets = settingsPreferences.getMealPresets()
        )
    }

    override suspend fun restoreBackup(payload: BackupPayload) {
        // Clear all
        medicineDao.clearAll()
        doseScheduleDao.clearAll()
        doseLogDao.clearAll()

        // Insert restored medicines and schedules
        for (med in payload.medicines) {
            val medEntity = MedicineEntity.fromDomain(med)
            val newId = medicineDao.insert(medEntity)
            val scheduleEntities = med.schedules.map { DoseScheduleEntity.fromDomain(it, newId) }
            doseScheduleDao.insertAll(scheduleEntities)
        }

        // Insert logs
        for (log in payload.logs) {
            doseLogDao.insertOrUpdate(DoseLogEntity.fromDomain(log))
        }

        // Restore settings
        settingsPreferences.savePrioritySettings(payload.prioritySettings)
        settingsPreferences.saveMealPresets(payload.mealPresets)

        // Reschedule alarms
        alarmScheduler.rescheduleAllAlarms()
    }
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val prefs: SettingsPreferences
) : SettingsRepository {

    override val mealPresets: Flow<MealPresets> = prefs.mealPresetsFlow
    override fun getMealPresetsSync(): MealPresets = prefs.getMealPresets()
    override suspend fun saveMealPresets(presets: MealPresets) = prefs.saveMealPresets(presets)

    override val prioritySettings: Flow<PrioritySettings> = prefs.prioritySettingsFlow
    override fun getPrioritySettingsSync(): PrioritySettings = prefs.getPrioritySettings()
    override suspend fun savePrioritySettings(settings: PrioritySettings) = prefs.savePrioritySettings(settings)
}
