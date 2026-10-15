package it.spark.medications

import it.spark.medications.domain.model.DoseSchedule
import it.spark.medications.domain.model.DoseStatus
import it.spark.medications.domain.model.Medicine
import it.spark.medications.domain.model.MedicineForm
import it.spark.medications.domain.model.NotificationPriority
import it.spark.medications.presentation.today.TodayUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MedicineCalculationsTest {

    @Test
    fun testStockRefillCalculation() {
        val medicine = Medicine(
            id = 1L,
            name = "Paracetamolo",
            dosage = "500 mg",
            form = MedicineForm.PILL,
            daysOfWeek = setOf(1, 2, 3, 4, 5, 6, 7),
            priority = NotificationPriority.MEDIUM,
            instructions = "Dopo i pasti",
            boxes = 1,
            unitsPerBox = 20,
            totalUnits = 20,
            alertBelowUnits = 5
        )

        assertFalse(medicine.isLowStock)

        // Simulate usage: 16 units taken -> 4 remaining
        val lowStockMedicine = medicine.copy(totalUnits = 4)
        assertTrue(lowStockMedicine.isLowStock)

        // Refill 2 boxes
        val refilledMedicine = lowStockMedicine.copy(
            boxes = lowStockMedicine.boxes + 2,
            totalUnits = lowStockMedicine.totalUnits + (2 * lowStockMedicine.unitsPerBox)
        )
        assertEquals(3, refilledMedicine.boxes)
        assertEquals(44, refilledMedicine.totalUnits)
        assertFalse(refilledMedicine.isLowStock)
    }

    @Test
    fun testTodayAdherenceProgress() {
        val emptyState = TodayUiState(takenCount = 0, totalCount = 0)
        assertEquals(0f, emptyState.progress, 0.001f)

        val halfState = TodayUiState(takenCount = 2, totalCount = 4)
        assertEquals(0.5f, halfState.progress, 0.001f)

        val completeState = TodayUiState(takenCount = 3, totalCount = 3)
        assertEquals(1.0f, completeState.progress, 0.001f)
    }

    @Test
    fun testDoseScheduleFormattedTime() {
        val scheduleMorning = DoseSchedule(id = 1, medicineId = 1, timeMinutes = 8 * 60 + 5) // 08:05
        assertEquals("08:05", scheduleMorning.formattedTime)

        val scheduleEvening = DoseSchedule(id = 2, medicineId = 1, timeMinutes = 20 * 60 + 30) // 20:30
        assertEquals("20:30", scheduleEvening.formattedTime)
    }
}
