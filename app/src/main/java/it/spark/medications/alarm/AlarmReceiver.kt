package it.spark.medications.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import it.spark.medications.data.local.dao.DoseLogDao
import it.spark.medications.data.local.dao.DoseScheduleDao
import it.spark.medications.data.local.dao.MedicineDao
import it.spark.medications.data.local.prefs.SettingsPreferences
import it.spark.medications.domain.model.DoseStatus
import it.spark.medications.domain.model.NotificationPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var medicineDao: MedicineDao
    @Inject lateinit var doseScheduleDao: DoseScheduleDao
    @Inject lateinit var doseLogDao: DoseLogDao
    @Inject lateinit var settingsPreferences: SettingsPreferences
    @Inject lateinit var alarmScheduler: MedicationAlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val medicineId = intent.getLongExtra(MedicationAlarmScheduler.EXTRA_MEDICINE_ID, -1L)
        val scheduleId = intent.getLongExtra(MedicationAlarmScheduler.EXTRA_SCHEDULE_ID, -1L)
        val dateString = intent.getStringExtra(MedicationAlarmScheduler.EXTRA_DATE_STRING) ?: ""
        val timeMinutes = intent.getIntExtra(MedicationAlarmScheduler.EXTRA_TIME_MINUTES, 0)
        val repeatIndex = intent.getIntExtra(MedicationAlarmScheduler.EXTRA_REPEAT_INDEX, 0)

        if (medicineId <= 0 || scheduleId <= 0) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if already taken or skipped
                val existingLog = doseLogDao.getLog(medicineId, scheduleId, dateString)
                if (existingLog != null && (existingLog.status == DoseStatus.TAKEN.name || existingLog.status == DoseStatus.SKIPPED.name)) {
                    // Already handled by user, no notification needed
                    return@launch
                }

                val medEntity = medicineDao.getById(medicineId) ?: return@launch
                val schedules = doseScheduleDao.getSchedulesForMedicineList(medicineId).map { it.toDomain() }
                val medicine = medEntity.toDomain(schedules)

                // Show notification with Material You dynamic color
                NotificationHelper.showMedicationNotification(
                    context = context,
                    medicine = medicine,
                    scheduleId = scheduleId,
                    dateString = dateString,
                    timeMinutes = timeMinutes,
                    repeatIndex = repeatIndex
                )

                // Check priority repeat settings
                val prioritySettings = settingsPreferences.getPrioritySettings()
                val (maxAlerts, interval) = when (medicine.priority) {
                    NotificationPriority.LOW -> Pair(prioritySettings.lowAlertCount, prioritySettings.lowIntervalMinutes)
                    NotificationPriority.MEDIUM -> Pair(prioritySettings.mediumAlertCount, prioritySettings.mediumIntervalMinutes)
                    NotificationPriority.HIGH -> Pair(prioritySettings.highAlertCount, prioritySettings.highIntervalMinutes)
                }

                val nextRepeatIndex = repeatIndex + 1
                if (nextRepeatIndex < maxAlerts && interval > 0) {
                    // Schedule repeat alarm
                    alarmScheduler.scheduleRepeatAlarm(
                        medicineId = medicineId,
                        scheduleId = scheduleId,
                        dateString = dateString,
                        timeMinutes = timeMinutes,
                        repeatIndex = nextRepeatIndex,
                        delayMinutes = interval
                    )
                } else {
                    // All repeats exhausted or no repeats: schedule next recurring cycle
                    val schedule = schedules.firstOrNull { it.id == scheduleId }
                    if (schedule != null) {
                        alarmScheduler.scheduleNextAlarm(medicine, schedule)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
