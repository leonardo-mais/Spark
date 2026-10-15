package it.spark.medications.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import it.spark.medications.R
import it.spark.medications.domain.model.Medicine
import it.spark.medications.domain.model.NotificationPriority
import it.spark.medications.presentation.MainActivity

object NotificationHelper {

    const val CHANNEL_ID_HIGH = "medications_channel_high"
    const val CHANNEL_ID_DEFAULT = "medications_channel_default"
    const val CHANNEL_ID_LOW = "medications_channel_low"

    const val ACTION_TAKE = "it.spark.medications.ACTION_DOSE_TAKEN"
    const val ACTION_SNOOZE = "it.spark.medications.ACTION_DOSE_SNOOZE"
    const val ACTION_SKIP = "it.spark.medications.ACTION_DOSE_SKIPPED"

    const val EXTRA_MEDICINE_ID = "extra_medicine_id"
    const val EXTRA_SCHEDULE_ID = "extra_schedule_id"
    const val EXTRA_DATE_STRING = "extra_date_string"
    const val EXTRA_TIME_MINUTES = "extra_time_minutes"
    const val EXTRA_REPEAT_COUNT = "extra_repeat_count"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            // High Priority Channel: insistent vibration, sound, lights
            val highChannel = NotificationChannel(
                CHANNEL_ID_HIGH,
                context.getString(R.string.priority_high_badge),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority alarms for critical medications"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 800)
                setSound(alarmSound, audioAttributes)
            }

            // Default / Medium Priority Channel
            val defaultChannel = NotificationChannel(
                CHANNEL_ID_DEFAULT,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setSound(alarmSound, audioAttributes)
            }

            // Low Priority Channel
            val lowChannel = NotificationChannel(
                CHANNEL_ID_LOW,
                context.getString(R.string.editor_priority_low),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Discreet reminders for vitamins and supplements"
                enableLights(false)
                enableVibration(false)
            }

            notificationManager.createNotificationChannels(listOf(highChannel, defaultChannel, lowChannel))
        }
    }

    fun showMedicationNotification(
        context: Context,
        medicine: Medicine,
        scheduleId: Long,
        dateString: String,
        timeMinutes: Int,
        repeatIndex: Int
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = (medicine.id * 1000 + scheduleId).toInt()

        val channelId = when (medicine.priority) {
            NotificationPriority.HIGH -> CHANNEL_ID_HIGH
            NotificationPriority.MEDIUM -> CHANNEL_ID_DEFAULT
            NotificationPriority.LOW -> CHANNEL_ID_LOW
        }

        // Tap on notification opens MainActivity
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 1: Prendi (Take)
        val takeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_TAKE
            putExtra(EXTRA_MEDICINE_ID, medicine.id)
            putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            putExtra(EXTRA_DATE_STRING, dateString)
            putExtra(EXTRA_TIME_MINUTES, timeMinutes)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val takePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 1,
            takeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 2: Snooze
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_MEDICINE_ID, medicine.id)
            putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            putExtra(EXTRA_DATE_STRING, dateString)
            putExtra(EXTRA_TIME_MINUTES, timeMinutes)
            putExtra(EXTRA_REPEAT_COUNT, repeatIndex)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 3: Salta (Skip)
        val skipIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SKIP
            putExtra(EXTRA_MEDICINE_ID, medicine.id)
            putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            putExtra(EXTRA_DATE_STRING, dateString)
            putExtra(EXTRA_TIME_MINUTES, timeMinutes)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val skipPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 3,
            skipIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = context.getString(R.string.notification_title_reminder, medicine.name)
        val details = context.getString(
            R.string.notification_content_reminder,
            medicine.dosage,
            if (medicine.instructions.isNotBlank()) medicine.instructions else medicine.form.name
        )

        // Use dynamic colorized background from Material You
        val primaryAccentColor = ContextCompat.getColor(context, R.color.ic_launcher_background_color)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(details)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText("$details\n" + (if (repeatIndex > 0) "Avviso di sollecito #$repeatIndex" else ""))
            )
            .setContentIntent(contentPendingIntent)
            .setColor(primaryAccentColor)
            .setColorized(true)
            .setAutoCancel(true)
            .setOngoing(medicine.priority == NotificationPriority.HIGH)
            .setPriority(
                when (medicine.priority) {
                    NotificationPriority.HIGH -> NotificationCompat.PRIORITY_MAX
                    NotificationPriority.MEDIUM -> NotificationCompat.PRIORITY_HIGH
                    NotificationPriority.LOW -> NotificationCompat.PRIORITY_DEFAULT
                }
            )
            .addAction(R.drawable.ic_notification, context.getString(R.string.notification_action_take), takePendingIntent)
            .addAction(R.drawable.ic_notification, context.getString(R.string.notification_action_snooze), snoozePendingIntent)
            .addAction(R.drawable.ic_notification, context.getString(R.string.notification_action_skip), skipPendingIntent)

        if (medicine.priority == NotificationPriority.HIGH) {
            builder.setCategory(NotificationCompat.CATEGORY_ALARM)
        } else {
            builder.setCategory(NotificationCompat.CATEGORY_REMINDER)
        }

        notificationManager.notify(notificationId, builder.build())
    }

    fun dismissNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}
