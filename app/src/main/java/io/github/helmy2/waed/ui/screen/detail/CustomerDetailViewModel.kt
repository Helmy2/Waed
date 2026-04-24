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

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun startEditing() {
        _isEditing.value = true
    }

    fun cancelEditing() {
        _isEditing.value = false
        _error.value = null
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
                    _error.value = "Page number $pageNumber is already in use"
                    return@launch
                }

                val updatedCustomer = CustomerRecord(
                    id = customerId,
                    pageNumber = pageNumber,
                    customerName = customerName.trim(),
                    debit = debit
                )
                repository.updateCustomer(updatedCustomer)
                _isEditing.value = false
                _error.value = null
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to save customer"
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
                _error.value = e.message ?: "Failed to delete customer"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}