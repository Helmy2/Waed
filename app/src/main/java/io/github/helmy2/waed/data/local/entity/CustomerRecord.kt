package io.github.helmy2.waed.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customer_record",
    indices = [Index(value = ["pageNumber"], unique = true)]
)
data class CustomerRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pageNumber: Int,
    val customerName: String,
    val debit: Double
)