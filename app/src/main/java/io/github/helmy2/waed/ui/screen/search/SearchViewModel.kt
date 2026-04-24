package io.github.helmy2.waed.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.helmy2.waed.data.local.entity.CustomerRecord
import io.github.helmy2.waed.data.repository.CustomerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SearchUiState {
    data object Loading : SearchUiState()
    data class Success(val customers: List<CustomerRecord>) : SearchUiState()
    data object Empty : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _dialogCustomer = MutableStateFlow<CustomerRecord?>(null)
    val dialogCustomer: StateFlow<CustomerRecord?> = _dialogCustomer.asStateFlow()

    private val _dialogError = MutableStateFlow<String?>(null)
    val dialogError: StateFlow<String?> = _dialogError.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SearchUiState> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllCustomers()
                    .map { customers ->
                        if (customers.isEmpty()) {
                            SearchUiState.Empty
                        } else {
                            SearchUiState.Success(customers)
                        }
                    }
            } else {
                val trimmedQuery = query.trim()
                val isDigitsOnly = trimmedQuery.all { it.isDigit() }

                if (isDigitsOnly) {
                    val pageNumber = trimmedQuery.toIntOrNull()
                    if (pageNumber != null) {
                        repository.searchByPageNumberFlow(pageNumber)
                            .map { customer ->
                                if (customer != null) {
                                    SearchUiState.Success(listOf(customer))
                                } else {
                                    SearchUiState.Empty
                                }
                            }
                    } else {
                        kotlinx.coroutines.flow.flowOf(SearchUiState.Empty)
                    }
                } else {
                    repository.searchByNameFlow(trimmedQuery)
                        .map { customers ->
                            if (customers.isEmpty()) {
                                SearchUiState.Empty
                            } else {
                                SearchUiState.Success(customers)
                            }
                        }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchUiState.Loading
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun showAddDialog() {
        _dialogCustomer.value = null
        _showDialog.value = true
    }

    fun dismissDialog() {
        _showDialog.value = false
        _dialogCustomer.value = null
        _dialogError.value = null
    }

    fun saveCustomer(customer: CustomerRecord) {
        viewModelScope.launch {
            try {
                val isPageTaken = repository.isPageNumberTaken(customer.pageNumber, customer.id)
                if (isPageTaken) {
                    _dialogError.value = "Page number ${customer.pageNumber} is already in use"
                    return@launch
                }

                if (customer.id == 0L) {
                    repository.insertCustomer(customer)
                } else {
                    repository.updateCustomer(customer)
                }
                dismissDialog()
            } catch (e: Exception) {
                _dialogError.value = e.message ?: "Failed to save customer"
            }
        }
    }

    fun deleteCustomerById(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteCustomer(id)
            } catch (e: Exception) {
                // Error is handled by Flow
            }
        }
    }

    fun getCustomerByIdFlow(id: Long) = repository.getCustomerByIdFlow(id)
}