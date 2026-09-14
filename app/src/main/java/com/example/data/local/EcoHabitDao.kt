package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoHabitDao {
    @Query("SELECT * FROM completed_habits ORDER BY timestamp DESC")
    fun getAllCompletedHabits(): Flow<List<CompletedHabitEntity>>

    @Query("SELECT * FROM completed_habits WHERE dateString = :dateString")
    fun getHabitsForDate(dateString: String): Flow<List<CompletedHabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedHabit(habit: CompletedHabitEntity): Long

    @Query("SELECT * FROM completed_challenges")
    fun getAllChallengeProgress(): Flow<List<CompletedChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChallengeProgress(challenge: CompletedChallengeEntity)

    @Query("SELECT * FROM user_eco_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserEcoProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserEcoProfileEntity)

    @Query("SELECT * FROM saved_hub_placements ORDER BY placedTimestamp DESC")
    fun getAllHubPlacements(): Flow<List<SavedHubPlacementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHubPlacement(placement: SavedHubPlacementEntity)

    @Query("DELETE FROM saved_hub_placements WHERE modelId = :modelId")
    suspend fun removeHubPlacement(modelId: String)
}
