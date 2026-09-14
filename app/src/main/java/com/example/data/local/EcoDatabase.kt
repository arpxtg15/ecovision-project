package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ScanHistoryEntity::class,
        CompletedHabitEntity::class,
        CompletedChallengeEntity::class,
        UserEcoProfileEntity::class,
        SavedHubPlacementEntity::class,
        Product3REntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class EcoDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun ecoHabitDao(): EcoHabitDao
    abstract fun product3RDao(): Product3RDao

    companion object {
        @Volatile
        private var INSTANCE: EcoDatabase? = null

        fun getDatabase(context: Context): EcoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EcoDatabase::class.java,
                    "ecovision_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
