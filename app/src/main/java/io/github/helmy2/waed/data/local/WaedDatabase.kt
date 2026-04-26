package io.github.helmy2.waed.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.helmy2.waed.data.local.dao.CustomerDao
import io.github.helmy2.waed.data.local.entity.CustomerRecord

@Database(
    entities = [CustomerRecord::class],
    version = 2,
    exportSchema = false
)
abstract class WaedDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
}