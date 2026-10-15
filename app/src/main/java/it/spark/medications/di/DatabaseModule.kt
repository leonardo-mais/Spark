package it.spark.medications.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.spark.medications.data.local.dao.DoseLogDao
import it.spark.medications.data.local.dao.DoseScheduleDao
import it.spark.medications.data.local.dao.MedicineDao
import it.spark.medications.data.local.database.MedicationsDatabase
import it.spark.medications.data.local.prefs.SettingsPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MedicationsDatabase {
        return Room.databaseBuilder(
            context,
            MedicationsDatabase::class.java,
            "medications.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideMedicineDao(db: MedicationsDatabase): MedicineDao = db.medicineDao()

    @Provides
    fun provideDoseScheduleDao(db: MedicationsDatabase): DoseScheduleDao = db.doseScheduleDao()

    @Provides
    fun provideDoseLogDao(db: MedicationsDatabase): DoseLogDao = db.doseLogDao()

    @Provides
    @Singleton
    fun provideSettingsPreferences(@ApplicationContext context: Context): SettingsPreferences {
        return SettingsPreferences(context)
    }
}
