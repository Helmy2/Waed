package io.github.helmy2.waed.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.helmy2.waed.data.local.entity.CustomerRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM customer_record ORDER BY customerName ASC")
    fun getAllCustomers(): Flow<List<CustomerRecord>>

    @Query("SELECT * FROM customer_record WHERE pageNumber = :pageNumber LIMIT 1")
    fun searchByPageNumberFlow(pageNumber: Int): Flow<CustomerRecord?>

    @Query("SELECT * FROM customer_record WHERE pageNumber = :pageNumber AND id != :excludeId LIMIT 1")
    suspend fun checkPageNumberExists(pageNumber: Int, excludeId: Long = 0): CustomerRecord?

    @Query("""
        SELECT customer_record.* FROM customer_record
        JOIN customer_record_fts ON customer_record.customerName = customer_record_fts.customerName
        WHERE customer_record_fts MATCH :query || '*'
        ORDER BY customer_record.customerName ASC
    """)
    fun searchByNameFlow(query: String): Flow<List<CustomerRecord>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomer(customer: CustomerRecord): Long

    @Update
    suspend fun updateCustomer(customer: CustomerRecord)

    @Query("DELETE FROM customer_record WHERE id = :id")
    suspend fun deleteCustomer(id: Long)

    @Query("SELECT * FROM customer_record WHERE id = :id")
    fun getCustomerByIdFlow(id: Long): Flow<CustomerRecord?>


    @Query("SELECT EXISTS(SELECT 1 FROM customer_record WHERE pageNumber = :pageNumber AND id != :excludeId)")
    suspend fun isPageNumberTaken(pageNumber: Int, excludeId: Long): Boolean
}