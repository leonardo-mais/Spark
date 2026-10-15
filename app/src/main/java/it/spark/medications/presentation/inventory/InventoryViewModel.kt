package it.spark.medications.presentation.inventory

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spark.medications.domain.model.Medicine
import it.spark.medications.domain.repository.MedicationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class InventoryUiState(
    val medicines: List<Medicine> = emptyList(),
    val filteredMedicines: List<Medicine> = emptyList(),
    val searchQuery: String = "",
    val filterLowStockOnly: Boolean = false,
    val refillMedicineTarget: Medicine? = null,
    val refillBoxesInput: String = "1",
    val isLoading: Boolean = false
)

sealed interface InventoryIntent {
    data class SearchQueryChanged(val query: String) : InventoryIntent
    data class ToggleLowStockFilter(val active: Boolean) : InventoryIntent
    data class OpenRefillDialog(val medicine: Medicine) : InventoryIntent
    data class RefillBoxesInputChanged(val input: String) : InventoryIntent
    object ConfirmRefill : InventoryIntent
    object DismissRefillDialog : InventoryIntent
    data class EditMedicine(val medicineId: Long) : InventoryIntent
    object AddMedicine : InventoryIntent
}

sealed interface InventoryEffect {
    data class NavigateToEditor(val medicineId: Long?) : InventoryEffect
    data class ShowSnackbar(val message: String) : InventoryEffect
}

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: MedicationRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterLowStock = MutableStateFlow(false)
    private val _refillTarget = MutableStateFlow<Medicine?>(null)
    private val _refillBoxes = MutableStateFlow("1")

    private val _effects = Channel<InventoryEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<InventoryUiState> = combine(
        repository.getActiveMedicines(),
        _searchQuery,
        _filterLowStock,
        _refillTarget,
        _refillBoxes
    ) { medicines, query, lowStockOnly, refillTarget, refillBoxes ->
        val filtered = medicines.filter { med ->
            val matchesQuery = query.isBlank() || med.name.contains(query, ignoreCase = true) || med.dosage.contains(query, ignoreCase = true)
            val matchesStock = !lowStockOnly || med.isLowStock
            matchesQuery && matchesStock
        }
        InventoryUiState(
            medicines = medicines,
            filteredMedicines = filtered,
            searchQuery = query,
            filterLowStockOnly = lowStockOnly,
            refillMedicineTarget = refillTarget,
            refillBoxesInput = refillBoxes,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InventoryUiState()
    )

    fun onIntent(intent: InventoryIntent) {
        when (intent) {
            is InventoryIntent.SearchQueryChanged -> _searchQuery.value = intent.query
            is InventoryIntent.ToggleLowStockFilter -> _filterLowStock.value = intent.active
            is InventoryIntent.OpenRefillDialog -> {
                _refillTarget.value = intent.medicine
                _refillBoxes.value = "1"
            }
            is InventoryIntent.RefillBoxesInputChanged -> {
                _refillBoxes.value = intent.input.filter { it.isDigit() }
            }
            InventoryIntent.ConfirmRefill -> {
                val target = _refillTarget.value ?: return
                val boxes = _refillBoxes.value.toIntOrNull() ?: 1
                viewModelScope.launch {
                    repository.refillStock(target.id, boxes)
                    _refillTarget.value = null
                    _refillBoxes.value = "1"
                }
            }
            InventoryIntent.DismissRefillDialog -> {
                _refillTarget.value = null
                _refillBoxes.value = "1"
            }
            is InventoryIntent.EditMedicine -> {
                viewModelScope.launch {
                    _effects.send(InventoryEffect.NavigateToEditor(intent.medicineId))
                }
            }
            InventoryIntent.AddMedicine -> {
                viewModelScope.launch {
                    _effects.send(InventoryEffect.NavigateToEditor(null))
                }
            }
        }
    }
}
