package it.spark.medications

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import it.spark.medications.alarm.NotificationHelper

@HiltAndroidApp
class MedicationsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
}
