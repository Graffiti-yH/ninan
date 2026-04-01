package com.denser.june.core.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `tags` (
                    `tagId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `name` TEXT NOT NULL
                )
            """.trimIndent())
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_name` ON `tags` (`name`)")
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `journal_tag_cross_ref` (
                    `id` INTEGER NOT NULL, 
                    `tagId` INTEGER NOT NULL, 
                    PRIMARY KEY(`id`, `tagId`)
                )
            """.trimIndent())
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_journal_tag_cross_ref_tagId` ON `journal_tag_cross_ref` (`tagId`)")
            db.execSQL("ALTER TABLE `journals` ADD COLUMN `tags` TEXT NOT NULL DEFAULT ''")
        }
    }
}