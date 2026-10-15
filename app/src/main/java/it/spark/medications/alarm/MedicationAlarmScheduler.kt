package it.spark.medications.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import it.spark.medications.data.local.dao.DoseScheduleDao
import it.spark.medications.data.local.dao.MedicineDao
import it.spark.medications.data.local.prefs.SettingsPreferences
import it.spark.medications.domain.model.DoseSchedule
import it.spark.medications.domain.model.Medicine
import it.spark.medications.domain.model.NotificationPriority
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val medicineDao: MedicineDao,
    private val doseScheduleDao: DoseScheduleDao,
    private val settingsPreferences: SettingsPreferences
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val ACTION_ALARM = "it.spark.medications.ACTION_MEDICATION_ALARM"
        const val EXTRA_MEDICINE_ID = "extra_medicine_id"
        const val EXTRA_SCHEDULE_ID = "extra_schedule_id"
        const val EXTRA_DATE_STRING = "extra_date_string"
        const val EXTRA_TIME_MINUTES = "extra_time_minutes"
        const val EXTRA_REPEAT_INDEX = "extra_repeat_index"
    }

    suspend fun rescheduleAllAlarms() {
        val medicines = medicineDao.getAllActiveList()
        for (medEntity in medicines) {
            val schedules = doseScheduleDao.getSchedulesForMedicineList(medEntity.id)
            val medicine = medEntity.toDomain(schedules.map { it.toDomain() })
            scheduleAlarmsForMedicine(medicine)
        }
    }

    fun scheduleAlarmsForMedicine(medicine: Medicine) {
        if (medicine.isArchived || medicine.daysOfWeek.isEmpty() || medicine.schedules.isEmpty()) {
            cancelAllAlarmsForMedicine(medicine.id)
            return
        }

        for (schedule in medicine.schedules) {
            scheduleNextAlarm(medicine, schedule)
        }
    }

    fun scheduleNextAlarm(medicine: Medicine, schedule: DoseSchedule) {
        val nextDateTime = calculateNextDateTime(medicine.daysOfWeek, schedule.timeMinutes)
        val triggerMillis = nextDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val dateString = nextDateTime.toLocalDate().toString()

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM
            putExtra(EXTRA_MEDICINE_ID, medicine.id)
            putExtra(EXTRA_SCHEDULE_ID, schedule.id)
            putExtra(EXTRA_DATE_STRING, dateString)
            putExtra(EXTRA_TIME_MINUTES, schedule.timeMinutes)
            putExtra(EXTRA_REPEAT_INDEX, 0)
        }

        val requestCode = (medicine.id * 1000 + schedule.id).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setExactAlarm(triggerMillis, pendingIntent)
    }

    fun scheduleRepeatAlarm(
        medicineId: Long,
        scheduleId: Long,
        dateString: String,
        timeMinutes: Int,
        repeatIndex: Int,
        delayMinutes: Int
    ) {
        val triggerMillis = System.currentTimeMillis() + (delayMinutes * 60 * 1000L)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM
            putExtra(EXTRA_MEDICINE_ID, medicineId)
            putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            putExtra(EXTRA_DATE_STRING, dateString)
            putExtra(EXTRA_TIME_MINUTES, timeMinutes)
            putExtra(EXTRA_REPEAT_INDEX, repeatIndex)
        }

        val requestCode = (medicineId * 1000 + scheduleId).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setExactAlarm(triggerMillis, pendingIntent)
    }

    fun cancelDoseAlert(medicineId: Long, scheduleId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM
        }
        val requestCode = (medicineId * 1000 + scheduleId).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun cancelAllAlarmsForMedicine(medicineId: Long) {
        // Can cancel base requests
        for (scheduleSlot in 0..20) {
            val requestCode = (medicineId * 1000 + scheduleSlot).toInt()
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_ALARM
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
    }

    private fun setExactAlarm(triggerMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }
    }

    private fun calculateNextDateTime(daysOfWeek: Set<Int>, timeMinutes: Int): LocalDateTime {
        val now = LocalDateTime.now()
        val scheduledTime = LocalTime.of(timeMinutes / 60, timeMinutes % 60, 0)
        var candidateDate = now.toLocalDate()

        // Check if today matches and time has not passed yet
        val todayDow = candidateDate.dayOfWeek.value // 1 = Monday ... 7 = Sunday
        if (daysOfWeek.contains(todayDow)) {
            val todayCandidate = candidateDate.atTime(scheduledTime)
            if (todayCandidate.isAfter(now.plusMinutes(1))) {
                return todayCandidate
            }
        }

        // Look ahead for the next 7 days
        for (i in 1..7) {
            val nextDate = candidateDate.plusDays(i.toLong())
            val nextDow = nextDate.dayOfWeek.value
            if (daysOfWeek.contains(nextDow)) {
                return nextDate.atTime(scheduledTime)
            }
        }

        // Default fallback: tomorrow
        return candidateDate.plusDays(1).atTime(scheduledTime)
    }
}
