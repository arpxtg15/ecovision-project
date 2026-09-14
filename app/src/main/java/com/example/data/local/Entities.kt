package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: String,
    val itemName: String,
    val category: String,
    val actionType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "completed_habits")
data class CompletedHabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitKey: String,
    val habitTitle: String,
    val dateString: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "completed_challenges")
data class CompletedChallengeEntity(
    @PrimaryKey
    val challengeId: String,
    val completedDays: Int,
    val isFullyCompleted: Boolean,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_eco_profile")
data class UserEcoProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalScansCount: Int = 4,
    val totalPlasticSavedKg: Double = 1.45,
    val totalWaterSavedLiters: Double = 84.0,
    val totalCo2OffsetKg: Double = 6.2,
    val userName: String = "Eco Hero",
    val hasCompletedOnboarding: Boolean = false
)

@Entity(tableName = "saved_hub_placements")
data class SavedHubPlacementEntity(
    @PrimaryKey
    val modelId: String,
    val title: String,
    val roomType: String, // e.g. "Living Room", "Balcony", "Rooftop"
    val scaleFactor: Float,
    val positionX: Float,
    val positionY: Float,
    val placedTimestamp: Long = System.currentTimeMillis()
)
