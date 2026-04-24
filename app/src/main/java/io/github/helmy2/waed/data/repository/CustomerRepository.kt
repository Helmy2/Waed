package io.github.helmy2.waed.data.repository

import io.github.helmy2.waed.data.local.dao.CustomerDao
import io.github.helmy2.waed.data.local.entity.CustomerRecord
import kotlinx.coroutines.flow.Flow

class CustomerRepository(
    private val customerDao: CustomerDao
) {

    fun getAllCustomers(): Flow<List<CustomerRecord>> = customerDao.getAllCustomers()

    fun searchByPageNumberFlow(pageNumber: Int): Flow<CustomerRecord?> = 
        customerDao.searchByPageNumberFlow(pageNumber)

    fun searchByNameFlow(query: String): Flow<List<CustomerRecord>> = 
        customerDao.searchByNameFlow(query)

    suspend fun isPageNumberTaken(pageNumber: Int, excludeId: Long = 0): Boolean {
        return customerDao.isPageNumberTaken(pageNumber, excludeId)
    }

    suspend fun insertCustomer(customer: CustomerRecord): Long {
        return customerDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: CustomerRecord) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(id: Long) {
        customerDao.deleteCustomer(id)
    }

    fun getCustomerByIdFlow(id: Long): Flow<CustomerRecord?> = 
        customerDao.getCustomerByIdFlow(id)
}