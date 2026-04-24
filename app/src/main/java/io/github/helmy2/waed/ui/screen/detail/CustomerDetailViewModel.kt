package io.github.helmy2.waed.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.helmy2.waed.data.local.entity.CustomerRecord
import io.github.helmy2.waed.data.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SaveError {
    data class PageNumberTaken(val pageNumber: Int) : SaveError()
    data class SaveFailed(val message: String) : SaveError()
}

sealed class DeleteError {
    data class DeleteFailed(val message: String) : DeleteError()
}

class CustomerDetailViewModel(
    private val customerId: Long,
    private val repository: CustomerRepository
) : ViewModel() {
    val customer: StateFlow<CustomerRecord?> = repository.getCustomerByIdFlow(customerId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _saveError = MutableStateFlow<SaveError?>(null)
    val saveError: StateFlow<SaveError?> = _saveError.asStateFlow()

    private val _deleteError = MutableStateFlow<DeleteError?>(null)
    val deleteError: StateFlow<DeleteError?> = _deleteError.asStateFlow()

    fun showEditDialog() {
        _showEditDialog.value = true
    }

    fun hideEditDialog() {
        _showEditDialog.value = false
        _saveError.value = null
    }

    fun showDeleteConfirmation() {
        _showDeleteDialog.value = true
    }

    fun hideDeleteConfirmation() {
        _showDeleteDialog.value = false
    }

    fun saveCustomer(
        pageNumber: Int,
        customerName: String,
        debit: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val isPageTaken = repository.isPageNumberTaken(pageNumber, customerId)
                if (isPageTaken) {
                    _saveError.value = SaveError.PageNumberTaken(pageNumber)
                    return@launch
                }

                val updatedCustomer = CustomerRecord(
                    id = customerId,
                    pageNumber = pageNumber,
                    customerName = customerName.trim(),
                    debit = debit
                )
                repository.updateCustomer(updatedCustomer)
                _showEditDialog.value = false
                _saveError.value = null
                onSuccess()
            } catch (e: Exception) {
                _saveError.value = SaveError.SaveFailed(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteCustomer(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _showDeleteDialog.value = false
                repository.deleteCustomer(customerId)
                onSuccess()
            } catch (e: Exception) {
                _deleteError.value = DeleteError.DeleteFailed(e.message ?: "Unknown error")
            }
        }
    }

    fun clearSaveError() {
        _saveError.value = null
    }

    fun clearDeleteError() {
        _deleteError.value = null
    }
}