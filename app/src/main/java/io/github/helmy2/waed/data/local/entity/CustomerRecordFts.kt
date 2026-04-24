package io.github.helmy2.waed.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "customer_record_fts")
@Fts4(contentEntity = CustomerRecord::class)
data class CustomerRecordFts(
    val customerName: String
)