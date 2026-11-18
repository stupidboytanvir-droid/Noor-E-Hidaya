package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        TasbihEntity::class,
        BookmarkEntity::class,
        AmalEntity::class,
        HadithBookmarkEntity::class,
        DebtLedgerEntity::class,
        HadithEntity::class,
        MonthlyWorshipEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tasbihDao(): TasbihDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun amalDao(): AmalDao
    abstract fun hadithBookmarkDao(): HadithBookmarkDao
    abstract fun debtLedgerDao(): DebtLedgerDao
    abstract fun hadithDao(): HadithDao
    abstract fun monthlyWorshipDao(): MonthlyWorshipDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `monthly_worship` (
                        `yearMonth` TEXT NOT NULL,
                        `quranMinutes` INTEGER NOT NULL DEFAULT 0,
                        `duaCount` INTEGER NOT NULL DEFAULT 0,
                        `tasbihCount` INTEGER NOT NULL DEFAULT 0,
                        `duroodCount` INTEGER NOT NULL DEFAULT 0,
                        `hadithMinutes` INTEGER NOT NULL DEFAULT 0,
                        `hadithReadCount` INTEGER NOT NULL DEFAULT 0,
                        `updatedAt` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`yearMonth`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noor_e_hidayah.db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
