package it.spark.medications.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import it.spark.medications.domain.model.DoseLog
import it.spark.medications.domain.model.DoseSchedule
import it.spark.medications.domain.model.DoseStatus
import it.spark.medications.domain.model.Medicine
import it.spark.medications.domain.model.MedicineForm
import it.spark.medications.domain.model.NotificationPriority

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val form: String, // Stored as enum name
    val daysOfWeekMask: Int, // Bitmask: bit 1 = Monday ... bit 7 = Sunday
    val priority: String, // Stored as enum name
    val instructions: String,
    val boxes: Int,
    val unitsPerBox: Int,
    val totalUnits: Int,
    val alertBelowUnits: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
) {
    fun toDomain(schedules: List<DoseSchedule> = emptyList()): Medicine {
        val days = mutableSetOf<Int>()
        for (i in 1..7) {
            if ((daysOfWeekMask and (1 shl i)) != 0) {
                days.add(i)
            }
        }
        return Medicine(
            id = id,
            name = name,
            dosage = dosage,
            form = runCatching { MedicineForm.valueOf(form) }.getOrDefault(MedicineForm.PILL),
            daysOfWeek = days,
            priority = runCatching { NotificationPriority.valueOf(priority) }.getOrDefault(NotificationPriority.MEDIUM),
            instructions = instructions,
            boxes = boxes,
            unitsPerBox = unitsPerBox,
            totalUnits = totalUnits,
            alertBelowUnits = alertBelowUnits,
            createdAt = createdAt,
            isArchived = isArchived,
            schedules = schedules
        )
    }

    companion object {
        fun fromDomain(m: Medicine): MedicineEntity {
            var mask = 0
            for (day in m.daysOfWeek) {
                mask = mask or (1 shl day)
            }
            return MedicineEntity(
                id = m.id,
                name = m.name,
                dosage = m.dosage,
                form = m.form.name,
                daysOfWeekMask = mask,
                priority = m.priority.name,
                instructions = m.instructions,
                boxes = m.boxes,
                unitsPerBox = m.unitsPerBox,
                totalUnits = m.totalUnits,
                alertBelowUnits = m.alertBelowUnits,
                createdAt = m.createdAt,
                isArchived = m.isArchived
            )
        }
    }
}

@Entity(
    tableName = "dose_schedules",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicineId")]
)
data class DoseScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineId: Long,
    val timeMinutes: Int,
    val doseUnits: Int = 1
) {
    fun toDomain() = DoseSchedule(
        id = id,
        medicineId = medicineId,
        timeMinutes = timeMinutes,
        doseUnits = doseUnits
    )

    companion object {
        fun fromDomain(s: DoseSchedule, medId: Long = s.medicineId) = DoseScheduleEntity(
            id = s.id,
            medicineId = medId,
            timeMinutes = s.timeMinutes,
            doseUnits = s.doseUnits
        )
    }
}

@Entity(
    tableName = "dose_logs",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicineId"), Index("scheduledDate")]
)
data class DoseLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineId: Long,
    val scheduleId: Long,
    val scheduledDate: String, // YYYY-MM-DD
    val scheduledTimeMinutes: Int,
    val status: String,
    val actionTimestamp: Long? = null,
    val repeatCountTriggered: Int = 0
) {
    fun toDomain() = DoseLog(
        id = id,
        medicineId = medicineId,
        scheduleId = scheduleId,
        scheduledDate = scheduledDate,
        scheduledTimeMinutes = scheduledTimeMinutes,
        status = runCatching { DoseStatus.valueOf(status) }.getOrDefault(DoseStatus.PENDING),
        actionTimestamp = actionTimestamp,
        repeatCountTriggered = repeatCountTriggered
    )

    companion object {
        fun fromDomain(log: DoseLog) = DoseLogEntity(
            id = log.id,
            medicineId = log.medicineId,
            scheduleId = log.scheduleId,
            scheduledDate = log.scheduledDate,
            scheduledTimeMinutes = log.scheduledTimeMinutes,
            status = log.status.name,
            actionTimestamp = log.actionTimestamp,
            repeatCountTriggered = log.repeatCountTriggered
        )
    }
}
