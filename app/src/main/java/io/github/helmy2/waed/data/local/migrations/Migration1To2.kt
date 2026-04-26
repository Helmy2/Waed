package io.github.helmy2.waed.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Drop the FTS virtual table that was removed from the schema
        db.execSQL("DROP TABLE IF EXISTS `customer_record_fts`")
        db.execSQL("DROP TABLE IF EXISTS `customer_record_fts_content`")
        db.execSQL("DROP TABLE IF EXISTS `customer_record_fts_segdir`")
        db.execSQL("DROP TABLE IF EXISTS `customer_record_fts_segments`")
        db.execSQL("DROP TABLE IF EXISTS `customer_record_fts_stat`")
        db.execSQL("DROP TABLE IF EXISTS `customer_record_fts_docsize`")
    }
}
