package it.spark.medications.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import it.spark.medications.domain.model.MealPresets
import it.spark.medications.domain.model.PrioritySettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class SettingsPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("medications_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BREAKFAST = "breakfast_minutes"
        private const val KEY_LUNCH = "lunch_minutes"
        private const val KEY_DINNER = "dinner_minutes"

        private const val KEY_LOW_COUNT = "low_count"
        private const val KEY_LOW_INTERVAL = "low_interval"
        private const val KEY_MEDIUM_COUNT = "med_count"
        private const val KEY_MEDIUM_INTERVAL = "med_interval"
        private const val KEY_HIGH_COUNT = "high_count"
        private const val KEY_HIGH_INTERVAL = "high_interval"
    }

    fun getMealPresets(): MealPresets {
        return MealPresets(
            breakfastMinutes = prefs.getInt(KEY_BREAKFAST, 8 * 60),
            lunchMinutes = prefs.getInt(KEY_LUNCH, 13 * 60),
            dinnerMinutes = prefs.getInt(KEY_DINNER, 20 * 60)
        )
    }

    fun saveMealPresets(presets: MealPresets) {
        prefs.edit()
            .putInt(KEY_BREAKFAST, presets.breakfastMinutes)
            .putInt(KEY_LUNCH, presets.lunchMinutes)
            .putInt(KEY_DINNER, presets.dinnerMinutes)
            .apply()
    }

    fun getPrioritySettings(): PrioritySettings {
        return PrioritySettings(
            lowAlertCount = prefs.getInt(KEY_LOW_COUNT, 1),
            lowIntervalMinutes = prefs.getInt(KEY_LOW_INTERVAL, 0),
            mediumAlertCount = prefs.getInt(KEY_MEDIUM_COUNT, 3),
            mediumIntervalMinutes = prefs.getInt(KEY_MEDIUM_INTERVAL, 10),
            highAlertCount = prefs.getInt(KEY_HIGH_COUNT, 5),
            highIntervalMinutes = prefs.getInt(KEY_HIGH_INTERVAL, 5)
        )
    }

    fun savePrioritySettings(settings: PrioritySettings) {
        prefs.edit()
            .putInt(KEY_LOW_COUNT, settings.lowAlertCount)
            .putInt(KEY_LOW_INTERVAL, settings.lowIntervalMinutes)
            .putInt(KEY_MEDIUM_COUNT, settings.mediumAlertCount)
            .putInt(KEY_MEDIUM_INTERVAL, settings.mediumIntervalMinutes)
            .putInt(KEY_HIGH_COUNT, settings.highAlertCount)
            .putInt(KEY_HIGH_INTERVAL, settings.highIntervalMinutes)
            .apply()
    }

    val mealPresetsFlow: Flow<MealPresets> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_BREAKFAST || key == KEY_LUNCH || key == KEY_DINNER) {
                trySend(getMealPresets())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(getMealPresets())
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    val prioritySettingsFlow: Flow<PrioritySettings> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key?.startsWith("low_") == true || key?.startsWith("med_") == true || key?.startsWith("high_") == true) {
                trySend(getPrioritySettings())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(getPrioritySettings())
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()
}
