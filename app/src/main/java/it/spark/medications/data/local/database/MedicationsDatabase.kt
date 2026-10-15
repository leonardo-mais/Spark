package it.spark.medications.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import it.spark.medications.data.local.dao.DoseLogDao
import it.spark.medications.data.local.dao.DoseScheduleDao
import it.spark.medications.data.local.dao.MedicineDao
import it.spark.medications.data.local.entity.DoseLogEntity
import it.spark.medications.data.local.entity.DoseScheduleEntity
import it.spark.medications.data.local.entity.MedicineEntity

@Database(
    entities = [
        MedicineEntity::class,
        DoseScheduleEntity::class,
        DoseLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MedicationsDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun doseScheduleDao(): DoseScheduleDao
    abstract fun doseLogDao(): DoseLogDao
}
