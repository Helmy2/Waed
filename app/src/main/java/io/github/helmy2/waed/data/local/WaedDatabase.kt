package io.github.helmy2.waed.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.helmy2.waed.data.local.dao.CustomerDao
import io.github.helmy2.waed.data.local.entity.CustomerRecord
import io.github.helmy2.waed.data.local.entity.CustomerRecordFts

@Database(
    entities = [CustomerRecord::class, CustomerRecordFts::class],
    version = 1,
    exportSchema = false
)
abstract class WaedDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
}