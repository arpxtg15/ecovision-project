package com.example.data.repository

import com.example.data.local.CompletedChallengeEntity
import com.example.data.local.CompletedHabitEntity
import com.example.data.local.EcoDatabase
import com.example.data.local.Product3REntity
import com.example.data.local.SavedHubPlacementEntity
import com.example.data.local.ScanHistoryEntity
import com.example.data.local.UserEcoProfileEntity
import com.example.data.model.EcoChallenge
import com.example.data.model.EcoChallengeData
import com.example.data.model.EcoTip
import com.example.data.model.EcoTipsData
import com.example.data.model.Product3RSeedData
import com.example.data.model.SustainabilityFact
import com.example.data.model.SustainabilitySolution
import com.example.data.model.SustainabilitySolutionsData
import com.example.data.model.VirtualHubData
import com.example.data.model.VirtualHubModel
import com.example.data.model.WasteCategory
import com.example.data.model.WasteItem
import com.example.data.model.WasteRepositoryData
import com.example.data.util.SustainabilityFactProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EcoRepository(
    private val database: EcoDatabase,
    val geminiRepository: IGeminiSustainabilityRepository = GeminiSustainabilityRepository.getInstance()
) {

    private val scanDao = database.scanHistoryDao()
    private val habitDao = database.ecoHabitDao()
    private val product3RDao = database.product3RDao()

    val allScans: Flow<List<ScanHistoryEntity>> = scanDao.getAllScans()
    val recentScans: Flow<List<ScanHistoryEntity>> = scanDao.getRecentScans()
    val userProfile: Flow<UserEcoProfileEntity?> = habitDao.getUserProfile()
    val completedHabits: Flow<List<CompletedHabitEntity>> = habitDao.getAllCompletedHabits()
    val challengeProgress: Flow<List<CompletedChallengeEntity>> = habitDao.getAllChallengeProgress()
    val savedPlacements: Flow<List<SavedHubPlacementEntity>> = habitDao.getAllHubPlacements()
    val all3RProducts: Flow<List<Product3REntity>> = product3RDao.getAll3RProducts()

    suspend fun ensure3RDatabaseSeeded() {
        val count = product3RDao.countProducts()
        if (count == 0) {
            product3RDao.insertAll(Product3RSeedData.initialProducts)
        }
    }

    fun search3RProducts(query: String): Flow<List<Product3REntity>> {
        return if (query.isBlank()) {
            product3RDao.getAll3RProducts()
        } else {
            product3RDao.searchProducts(query.trim())
        }
    }

    fun get3RProductsByClassification(classification: String): Flow<List<Product3REntity>> {
        return if (classification.equals("ALL", ignoreCase = true)) {
            product3RDao.getAll3RProducts()
        } else {
            product3RDao.getProductsByClassification(classification)
        }
    }

    fun get3RProductById(id: String): Flow<Product3REntity?> {
        return product3RDao.getProductById(id)
    }

    fun getAllWasteItems(): List<WasteItem> = WasteRepositoryData.sampleItems

    fun getWasteItemById(id: String): WasteItem? {
        return WasteRepositoryData.sampleItems.firstOrNull { it.id == id }
    }

    fun getWasteItemsByCategory(category: WasteCategory): List<WasteItem> {
        return WasteRepositoryData.sampleItems.filter { it.category == category }
    }

    fun searchWasteItems(query: String): List<WasteItem> {
        if (query.isBlank()) return WasteRepositoryData.sampleItems
        val cleanQuery = query.trim().lowercase()
        return WasteRepositoryData.sampleItems.filter {
            it.name.lowercase().contains(cleanQuery) ||
            it.materialType.lowercase().contains(cleanQuery) ||
            it.category.displayName.lowercase().contains(cleanQuery) ||
            it.primaryAction.name.lowercase().contains(cleanQuery)
        }
    }

    fun getAllSolutions(): List<SustainabilitySolution> = SustainabilitySolutionsData.solutionsList

    fun getSolutionById(id: String): SustainabilitySolution? {
        return SustainabilitySolutionsData.solutionsList.firstOrNull { it.id == id }
    }

    fun getAllHubModels(): List<VirtualHubModel> = VirtualHubData.models

    fun getHubModelById(id: String): VirtualHubModel? {
        return VirtualHubData.models.firstOrNull { it.id == id }
    }

    fun getAllEcoTips(): List<EcoTip> = EcoTipsData.sampleTips

    fun getAllChallenges(): List<EcoChallenge> = EcoChallengeData.activeChallenges

    fun getDailySustainabilityFact(): SustainabilityFact = SustainabilityFactProvider.getDailyFact()

    fun getNextSustainabilityFact(currentId: String?): SustainabilityFact = SustainabilityFactProvider.getNextFact(currentId)

    fun getAllSustainabilityFacts(): List<SustainabilityFact> = SustainabilityFactProvider.getAllFacts()

    fun getQuickEcoActions(): List<com.example.data.model.QuickEcoAction> = com.example.data.model.EcoHomeData.quickActions

    fun getClimateQuizQuestions(): List<com.example.data.model.ClimateQuizQuestion> = com.example.data.model.EcoHomeData.sampleQuizzes

    suspend fun recordQuickAction(action: com.example.data.model.QuickEcoAction): Long {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val habit = CompletedHabitEntity(
            habitKey = action.id,
            habitTitle = action.title,
            dateString = todayStr
        )
        val id = habitDao.insertCompletedHabit(habit)

        val currentProfile = userProfile.firstOrNull() ?: UserEcoProfileEntity()
        val updatedProfile = currentProfile.copy(
            totalPlasticSavedKg = currentProfile.totalPlasticSavedKg + action.plasticSavedKg,
            totalWaterSavedLiters = currentProfile.totalWaterSavedLiters + action.waterSavedLiters,
            totalCo2OffsetKg = currentProfile.totalCo2OffsetKg + action.co2OffsetKg
        )
        habitDao.saveUserProfile(updatedProfile)
        return id
    }

    suspend fun recordQuizCompleted(quiz: com.example.data.model.ClimateQuizQuestion) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val habit = CompletedHabitEntity(
            habitKey = quiz.id,
            habitTitle = "Quiz: ${quiz.question.take(20)}...",
            dateString = todayStr
        )
        habitDao.insertCompletedHabit(habit)

        val currentProfile = userProfile.firstOrNull() ?: UserEcoProfileEntity()
        val updatedProfile = currentProfile.copy(
            totalCo2OffsetKg = currentProfile.totalCo2OffsetKg + 0.10
        )
        habitDao.saveUserProfile(updatedProfile)
    }

    suspend fun recordScan(item: WasteItem, userNotes: String = ""): Long {
        val scan = ScanHistoryEntity(
            itemId = item.id,
            itemName = item.name,
            category = item.category.displayName,
            actionType = item.primaryAction.title,
            notes = userNotes
        )
        val id = scanDao.insertScan(scan)

        // Update profile environmental impact stats
        val currentProfile = userProfile.firstOrNull() ?: UserEcoProfileEntity()
        val updatedProfile = currentProfile.copy(
            totalScansCount = currentProfile.totalScansCount + 1,
            totalPlasticSavedKg = currentProfile.totalPlasticSavedKg + 0.12,
            totalCo2OffsetKg = currentProfile.totalCo2OffsetKg + 0.45
        )
        habitDao.saveUserProfile(updatedProfile)
        return id
    }

    suspend fun recordTipCompleted(tip: EcoTip): Long {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val completedHabit = CompletedHabitEntity(
            habitKey = tip.id,
            habitTitle = tip.title,
            dateString = todayStr
        )
        val id = habitDao.insertCompletedHabit(completedHabit)

        val currentProfile = userProfile.firstOrNull() ?: UserEcoProfileEntity()
        val updatedProfile = currentProfile.copy(
            totalWaterSavedLiters = currentProfile.totalWaterSavedLiters + 12.0
        )
        habitDao.saveUserProfile(updatedProfile)
        return id
    }

    suspend fun updateChallengeProgress(challengeId: String, incrementDay: Boolean = true) {
        val challenge = getAllChallenges().firstOrNull { it.id == challengeId } ?: return
        val currentList = habitDao.getAllChallengeProgress().firstOrNull() ?: emptyList()
        val existing = currentList.firstOrNull { it.challengeId == challengeId }
        val currentDays = existing?.completedDays ?: 0
        val nextDays = if (incrementDay) (currentDays + 1).coerceAtMost(challenge.durationDays) else currentDays
        val isFinished = nextDays >= challenge.durationDays

        val updated = CompletedChallengeEntity(
            challengeId = challengeId,
            completedDays = nextDays,
            isFullyCompleted = isFinished
        )
        habitDao.saveChallengeProgress(updated)

        if (isFinished && existing?.isFullyCompleted != true) {
            val currentProfile = userProfile.firstOrNull() ?: UserEcoProfileEntity()
            habitDao.saveUserProfile(
                currentProfile.copy(
                    totalCo2OffsetKg = currentProfile.totalCo2OffsetKg + 1.5
                )
            )
        }
    }

    suspend fun saveHubSpacePlacement(model: VirtualHubModel, room: String, scale: Float, x: Float, y: Float) {
        val placement = SavedHubPlacementEntity(
            modelId = model.id,
            title = model.title,
            roomType = room,
            scaleFactor = scale,
            positionX = x,
            positionY = y
        )
        habitDao.saveHubPlacement(placement)
    }

    suspend fun removeHubSpacePlacement(modelId: String) {
        habitDao.removeHubPlacement(modelId)
    }

    suspend fun clearHistory() {
        scanDao.clearAll()
    }
}
