package it.spark.medications.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.spark.medications.data.local.entity.DoseLogEntity
import it.spark.medications.data.local.entity.DoseScheduleEntity
import it.spark.medications.data.local.entity.MedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines WHERE isArchived = 0 ORDER BY name ASC")
    fun getAllActive(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE isArchived = 0 ORDER BY name ASC")
    suspend fun getAllActiveList(): List<MedicineEntity>

    @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MedicineEntity?

    @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
    fun getByIdFlow(id: Long): Flow<MedicineEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicine: MedicineEntity): Long

    @Update
    suspend fun update(medicine: MedicineEntity)

    @Delete
    suspend fun delete(medicine: MedicineEntity)

    @Query("UPDATE medicines SET totalUnits = totalUnits - :units WHERE id = :id AND totalUnits >= :units")
    suspend fun decrementStock(id: Long, units: Int): Int

    @Query("UPDATE medicines SET boxes = boxes + :boxesToAdd, totalUnits = totalUnits + (:boxesToAdd * unitsPerBox) WHERE id = :id")
    suspend fun refillBoxes(id: Long, boxesToAdd: Int)

    @Query("DELETE FROM medicines")
    suspend fun clearAll()
}

@Dao
interface DoseScheduleDao {
    @Query("SELECT * FROM dose_schedules WHERE medicineId = :medicineId ORDER BY timeMinutes ASC")
    fun getSchedulesForMedicine(medicineId: Long): Flow<List<DoseScheduleEntity>>

    @Query("SELECT * FROM dose_schedules WHERE medicineId = :medicineId ORDER BY timeMinutes ASC")
    suspend fun getSchedulesForMedicineList(medicineId: Long): List<DoseScheduleEntity>

    @Query("SELECT * FROM dose_schedules ORDER BY timeMinutes ASC")
    suspend fun getAllSchedulesList(): List<DoseScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(schedules: List<DoseScheduleEntity>)

    @Query("DELETE FROM dose_schedules WHERE medicineId = :medicineId")
    suspend fun deleteForMedicine(medicineId: Long)

    @Query("DELETE FROM dose_schedules")
    suspend fun clearAll()
}

@Dao
interface DoseLogDao {
    @Query("SELECT * FROM dose_logs WHERE scheduledDate = :dateString")
    fun getLogsForDate(dateString: String): Flow<List<DoseLogEntity>>

    @Query("SELECT * FROM dose_logs WHERE scheduledDate = :dateString")
    suspend fun getLogsForDateList(dateString: String): List<DoseLogEntity>

    @Query("SELECT * FROM dose_logs WHERE medicineId = :medicineId AND scheduleId = :scheduleId AND scheduledDate = :dateString LIMIT 1")
    suspend fun getLog(medicineId: Long, scheduleId: Long, dateString: String): DoseLogEntity?

    @Query("SELECT * FROM dose_logs WHERE scheduledDate LIKE :yearMonthPrefix || '%'")
    fun getLogsForMonth(yearMonthPrefix: String): Flow<List<DoseLogEntity>>

    @Query("SELECT * FROM dose_logs ORDER BY scheduledDate DESC, scheduledTimeMinutes DESC")
    suspend fun getAllLogsList(): List<DoseLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: DoseLogEntity): Long

    @Query("DELETE FROM dose_logs")
    suspend fun clearAll()
}
