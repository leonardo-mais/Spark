package it.spark.medications.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import it.spark.medications.R
import it.spark.medications.data.local.dao.DoseLogDao
import it.spark.medications.data.local.dao.MedicineDao
import it.spark.medications.data.local.entity.DoseLogEntity
import it.spark.medications.data.local.prefs.SettingsPreferences
import it.spark.medications.domain.model.DoseStatus
import it.spark.medications.domain.model.NotificationPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject lateinit var medicineDao: MedicineDao
    @Inject lateinit var doseLogDao: DoseLogDao
    @Inject lateinit var settingsPreferences: SettingsPreferences
    @Inject lateinit var alarmScheduler: MedicationAlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val medicineId = intent.getLongExtra(NotificationHelper.EXTRA_MEDICINE_ID, -1L)
        val scheduleId = intent.getLongExtra(NotificationHelper.EXTRA_SCHEDULE_ID, -1L)
        val dateString = intent.getStringExtra(NotificationHelper.EXTRA_DATE_STRING) ?: ""
        val timeMinutes = intent.getIntExtra(NotificationHelper.EXTRA_TIME_MINUTES, 0)
        val notificationId = intent.getIntExtra(NotificationHelper.EXTRA_NOTIFICATION_ID, 0)

        if (medicineId <= 0 || scheduleId <= 0) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val medEntity = medicineDao.getById(medicineId)
                val medName = medEntity?.name ?: "Medicina"

                when (intent.action) {
                    NotificationHelper.ACTION_TAKE -> {
                        // 1. Decrement stock
                        medicineDao.decrementStock(medicineId, 1)

                        // 2. Log dose as taken
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

                        // 3. Cancel repeats
                        alarmScheduler.cancelDoseAlert(medicineId, scheduleId)

                        // 4. Dismiss notification
                        NotificationHelper.dismissNotification(context, notificationId)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.notification_toast_taken, medName),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    NotificationHelper.ACTION_SNOOZE -> {
                        // Snooze interval based on priority or default 10 min
                        val priority = runCatching { NotificationPriority.valueOf(medEntity?.priority ?: "") }
                            .getOrDefault(NotificationPriority.MEDIUM)
                        val settings = settingsPreferences.getPrioritySettings()
                        val interval = when (priority) {
                            NotificationPriority.LOW -> 15
                            NotificationPriority.MEDIUM -> if (settings.mediumIntervalMinutes > 0) settings.mediumIntervalMinutes else 10
                            NotificationPriority.HIGH -> if (settings.highIntervalMinutes > 0) settings.highIntervalMinutes else 5
                        }

                        alarmScheduler.scheduleRepeatAlarm(
                            medicineId = medicineId,
                            scheduleId = scheduleId,
                            dateString = dateString,
                            timeMinutes = timeMinutes,
                            repeatIndex = 999, // manual snooze
                            delayMinutes = interval
                        )

                        NotificationHelper.dismissNotification(context, notificationId)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.notification_toast_snoozed, interval),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    NotificationHelper.ACTION_SKIP -> {
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

                        alarmScheduler.cancelDoseAlert(medicineId, scheduleId)
                        NotificationHelper.dismissNotification(context, notificationId)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.notification_toast_skipped, medName),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
