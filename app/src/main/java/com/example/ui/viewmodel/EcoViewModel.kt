package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.tflite.TfLiteDetectedObject
import com.example.ai.tflite.TfLiteObjectDetector
import com.example.ar.ArHitMarker
import com.example.ar.ArObjectDetectionService
import com.example.data.local.EcoDatabase
import com.example.data.local.Product3REntity
import com.example.data.local.SavedHubPlacementEntity
import com.example.data.local.ScanHistoryEntity
import com.example.data.local.UserEcoProfileEntity
import com.example.data.model.ClimateQuizQuestion
import com.example.data.model.DayActivity
import com.example.data.model.EcoActionType
import com.example.data.model.EcoBadge
import com.example.data.model.EcoChallenge
import com.example.data.model.EcoHomeData
import com.example.data.model.EcoTip
import com.example.data.model.QuickEcoAction
import com.example.data.model.SustainabilityFact
import com.example.data.model.SustainabilitySolution
import com.example.data.model.TipTimeContext
import com.example.data.model.VirtualHubModel
import com.example.data.model.WasteCategory
import com.example.data.model.WasteItem
import com.example.data.remote.gemini.AiSustainabilityProfileResult
import com.example.data.remote.gemini.GeminiSustainabilityAnalyzer
import com.example.data.repository.EcoRepository
import com.example.ml.teachablemachine.TeachableMachineClassificationResult
import com.example.ml.teachablemachine.TeachableMachineEngine
import com.example.ml.teachablemachine.TeachableMachineModelInfo
import com.example.ml.teachablemachine.TeachableMachinePresetCatalog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

data class ScannerUiState(
    val isScanningActive: Boolean = false,
    val isAnalyzing: Boolean = false,
    val selectedItem: WasteItem? = null,
    val currentAiProfile: AiSustainabilityProfileResult? = null,
    val capturedFrameBitmap: Bitmap? = null,
    val tfLiteDetectedObjects: List<TfLiteDetectedObject> = emptyList(),
    val detectedItemsInFrame: List<WasteItem> = emptyList(),
    val scanSearchQuery: String = "",
    val activeFilterCategory: WasteCategory? = null,
    val showDetailDialog: Boolean = false,
    val scanSuccessMessage: String? = null,
    val scanErrorMessage: String? = null,
    val arOverlayIntensity: Float = 1.0f
)

data class HubUiState(
    val selectedModel: VirtualHubModel? = null,
    val placedModels: List<SavedHubPlacementEntity> = emptyList(),
    val isArCameraPlacementActive: Boolean = false,
    val selectedRoomEnvironment: String = "Eco Living Room",
    val simulationParameterValue: Float = 0.5f,
    val showSpecsDialog: Boolean = false,
    val toastFeedback: String? = null
)

data class SolutionsUiState(
    val selectedSolution: SustainabilitySolution? = null,
    val searchQuery: String = "",
    val calculatorInputValue: Double = 3.0,
    val calculatedAnnualSavings: Double = 0.0,
    val showDetailDialog: Boolean = false
)

data class TipsAndHabitsUiState(
    val selectedContext: TipTimeContext = TipTimeContext.ALL_DAY,
    val activeChallengeId: String? = null,
    val feedbackMessage: String? = null
)

data class HomeQuizUiState(
    val currentQuestion: ClimateQuizQuestion = EcoHomeData.sampleQuizzes.first(),
    val selectedOptionIndex: Int? = null,
    val isSubmitted: Boolean = false,
    val isCorrect: Boolean = false,
    val showExplanation: Boolean = false
)

data class ShopUiState(
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val selectedProduct: com.example.data.model.EcoProduct? = null,
    val showDetailDialog: Boolean = false,
    val showOrderSuccessDialog: Boolean = false,
    val lastOrderedProduct: com.example.data.model.EcoProduct? = null,
    val purchasedProductIds: Set<String> = emptySet(),
    val spentEcoCredits: Int = 0,
    val feedbackMessage: String? = null
)

data class ThreeRDatabaseUiState(
    val searchQuery: String = "",
    val activeClassification: String = "ALL", // "ALL", "RECYCLE", "REUSE", "REDUCE", "COMPOST", "SPECIAL"
    val selectedProduct: Product3REntity? = null,
    val showProductDetailDialog: Boolean = false,
    val isArTargetActive: Boolean = false,
    val toastFeedback: String? = null
)

class EcoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EcoRepository
    val teachableMachineEngine: TeachableMachineEngine = TeachableMachineEngine.getInstance()
    private val tfLiteObjectDetector: TfLiteObjectDetector

    val tmClassificationState: StateFlow<TeachableMachineClassificationResult?> = teachableMachineEngine.classificationState
    val tmActiveModel: StateFlow<TeachableMachineModelInfo> = teachableMachineEngine.activeModel
    val tmIsModelLoading: StateFlow<Boolean> = teachableMachineEngine.isModelLoading
    val tmIsInferencing: StateFlow<Boolean> = teachableMachineEngine.isInferencing
    val tmIsModelReady: StateFlow<Boolean> = teachableMachineEngine.isModelReady

    init {
        val db = EcoDatabase.getDatabase(application)
        repository = EcoRepository(db)
        teachableMachineEngine.initialize(application)
        tfLiteObjectDetector = TfLiteObjectDetector(application)
        viewModelScope.launch {
            repository.ensure3RDatabaseSeeded()
        }
    }

    val all3RProducts: StateFlow<List<Product3REntity>> = repository.all3RProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userProfile: StateFlow<UserEcoProfileEntity> = repository.userProfile
        .map { it ?: UserEcoProfileEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserEcoProfileEntity()
        )

    val scanHistory: StateFlow<List<ScanHistoryEntity>> = repository.allScans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedHubPlacements: StateFlow<List<SavedHubPlacementEntity>> = repository.savedPlacements
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI States
    private val _scannerState = MutableStateFlow(ScannerUiState())
    val scannerState: StateFlow<ScannerUiState> = _scannerState.asStateFlow()

    private val _hubState = MutableStateFlow(
        HubUiState(selectedModel = repository.getAllHubModels().firstOrNull())
    )
    val hubState: StateFlow<HubUiState> = _hubState.asStateFlow()

    private val _solutionsState = MutableStateFlow(
        SolutionsUiState(selectedSolution = repository.getAllSolutions().firstOrNull())
    )
    val solutionsState: StateFlow<SolutionsUiState> = _solutionsState.asStateFlow()

    private val _tipsState = MutableStateFlow(TipsAndHabitsUiState())
    val tipsState: StateFlow<TipsAndHabitsUiState> = _tipsState.asStateFlow()

    private val _dailyFact = MutableStateFlow(repository.getDailySustainabilityFact())
    val dailyFact: StateFlow<SustainabilityFact> = _dailyFact.asStateFlow()

    private val _quizState = MutableStateFlow(HomeQuizUiState())
    val quizState: StateFlow<HomeQuizUiState> = _quizState.asStateFlow()

    private val _homeFeedback = MutableStateFlow<String?>(null)
    val homeFeedback: StateFlow<String?> = _homeFeedback.asStateFlow()

    private val _shopState = MutableStateFlow(ShopUiState())
    val shopState: StateFlow<ShopUiState> = _shopState.asStateFlow()

    private val _threeRState = MutableStateFlow(ThreeRDatabaseUiState())
    val threeRState: StateFlow<ThreeRDatabaseUiState> = _threeRState.asStateFlow()

    // Repository Data Accessors
    fun getWasteItems(): List<WasteItem> {
        val query = _scannerState.value.scanSearchQuery
        val category = _scannerState.value.activeFilterCategory
        var items = if (query.isNotBlank()) repository.searchWasteItems(query) else repository.getAllWasteItems()
        if (category != null) {
            items = items.filter { it.category == category }
        }
        return items
    }

    fun getAllSolutions(): List<SustainabilitySolution> = repository.getAllSolutions()
    fun getAllHubModels(): List<VirtualHubModel> = repository.getAllHubModels()
    fun getAllEcoTips(): List<EcoTip> {
        val context = _tipsState.value.selectedContext
        val all = repository.getAllEcoTips()
        return if (context == TipTimeContext.ALL_DAY) all else all.filter { it.context == context || it.context == TipTimeContext.ALL_DAY }
    }
    fun getAllChallenges(): List<EcoChallenge> = repository.getAllChallenges()

    // Waste Scanner Actions
    fun onScannerSearchQueryChanged(newQuery: String) {
        _scannerState.update { it.copy(scanSearchQuery = newQuery) }
    }

    fun onSelectWasteCategoryFilter(category: WasteCategory?) {
        _scannerState.update { it.copy(activeFilterCategory = category) }
    }

    fun analyzeDetectedObjectFrame(
        bitmap: Bitmap?,
        detectedMarker: ArHitMarker? = null
    ) {
        if (bitmap == null) {
            _scannerState.update {
                it.copy(
                    isAnalyzing = false,
                    scanErrorMessage = "No image frame received. Please point your camera at an object or select an image to upload."
                )
            }
            return
        }

        _scannerState.update {
            it.copy(
                isScanningActive = true,
                isAnalyzing = true,
                capturedFrameBitmap = bitmap,
                scanErrorMessage = null,
                scanSuccessMessage = null
            )
        }
        viewModelScope.launch {
            try {
                // 1. Run local on-device TensorFlow Lite object detection
                val tfObjects = tfLiteObjectDetector.detectObjects(bitmap)

                // 2. Pass TensorFlow detected objects directly into Gemini to formulate tailored reuse and disposal guidance
                val result = repository.geminiRepository.analyzeProductFrame(
                    bitmap = bitmap,
                    detectedMarker = detectedMarker,
                    tfDetectedObjects = tfObjects
                )
                val aiResult = result.getOrElse {
                    GeminiSustainabilityAnalyzer.createFallbackSustainabilityProfile(
                        bitmap = bitmap,
                        detectedMarker = detectedMarker,
                        tfDetectedObjects = tfObjects
                    )
                }
                val wasteItem = aiResult.toWasteItem()

                // Project detected physical components into AR overlay space
                if (aiResult.detectedComponents.isNotEmpty()) {
                    ArObjectDetectionService.getInstance().setDetectedProductComponents(
                        components = aiResult.detectedComponents,
                        productName = aiResult.itemName,
                        confidence = aiResult.confidenceLevel
                    )
                }

                val detectedCount = aiResult.detectedObjects.size
                val statusMsg = if (tfObjects.isNotEmpty()) {
                    "TensorFlow detected '${tfObjects.first().label}' • Gemini suggested Reuse & Disposal"
                } else if (detectedCount > 1) {
                    "Identified $detectedCount objects with TensorFlow & Gemini"
                } else {
                    "Identified: ${aiResult.itemName}"
                }

                _scannerState.update {
                    it.copy(
                        isAnalyzing = false,
                        selectedItem = wasteItem,
                        currentAiProfile = aiResult,
                        tfLiteDetectedObjects = tfObjects,
                        showDetailDialog = true,
                        scanSuccessMessage = statusMsg,
                        scanErrorMessage = null
                    )
                }
                repository.recordScan(
                    item = wasteItem,
                    userNotes = "TensorFlow Lite & Gemini Vision Sustainability Analysis (${aiResult.detectedObjects.size} objects detected)"
                )
            } catch (e: Exception) {
                _scannerState.update {
                    it.copy(
                        isAnalyzing = false,
                        scanErrorMessage = "AI Vision Analysis failed: ${e.localizedMessage ?: "Please try again with clear lighting."}"
                    )
                }
            }
        }
    }

    fun clearScanError() {
        _scannerState.update { it.copy(scanErrorMessage = null) }
    }

    /**
     * Re-queries Gemini specifically to formulate new creative ways to reuse or dispose
     * of the currently selected or TensorFlow-detected item.
     */
    fun requestGeminiReuseAndDisposalRefresh(tfObject: TfLiteDetectedObject? = null) {
        val currentItem = _scannerState.value.selectedItem ?: return
        viewModelScope.launch {
            _scannerState.update { it.copy(isAnalyzing = true) }
            val res = repository.geminiRepository.suggestWaysToReuseOrDispose(currentItem, tfObject)
            res.onSuccess { (reuses, disposes) ->
                val updatedItem = currentItem.copy(
                    waysToReuse = reuses,
                    waysToDispose = disposes,
                    upcyclingIdeas = reuses,
                    stepByStepGuide = disposes
                )
                _scannerState.update {
                    it.copy(
                        isAnalyzing = false,
                        selectedItem = updatedItem,
                        scanSuccessMessage = "Gemini generated tailored reuse & disposal suggestions!"
                    )
                }
            }.onFailure { e ->
                _scannerState.update {
                    it.copy(
                        isAnalyzing = false,
                        scanErrorMessage = "Failed to refresh Gemini suggestions: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun retryLastScan() {
        val lastBitmap = _scannerState.value.capturedFrameBitmap
        if (lastBitmap != null) {
            analyzeDetectedObjectFrame(lastBitmap)
        }
    }

    fun classifyFrameWithTeachableMachine(bitmap: Bitmap, onResult: ((TeachableMachineClassificationResult) -> Unit)? = null) {
        teachableMachineEngine.classifyBitmap(bitmap, onResult)
    }

    fun selectTeachableMachinePresetModel(model: TeachableMachineModelInfo) {
        teachableMachineEngine.loadModel(model)
    }

    fun setCustomTeachableMachineModelUrl(url: String, title: String = "Custom Teachable Machine Model") {
        val customModel = TeachableMachineModelInfo(
            id = "custom_url_${System.currentTimeMillis()}",
            name = title,
            description = "User configured Google Teachable Machine model URL",
            modelUrl = url,
            classes = listOf("Class 1", "Class 2", "Class 3", "Class 4"),
            isBuiltIn = false
        )
        teachableMachineEngine.loadModel(customModel)
    }

    fun selectItemForScanning(item: WasteItem) {
        _scannerState.update {
            it.copy(
                selectedItem = item,
                isScanningActive = true,
                isAnalyzing = true
            )
        }
        viewModelScope.launch {
            val result = repository.geminiRepository.analyzeProductQuery(item.name)
            val aiResult = result.getOrNull()
            val enhancedItem = item.copy(
                sustainabilityProfileDescription = item.sustainabilityProfileDescription 
                    ?: aiResult?.sustainabilityDescription
                    ?: "Sustainability Profile for ${item.name}:\n\n• Material & Lifecycle Impact: Composed of ${item.materialType}. In natural open environments, this item takes approximately ${item.decompositionTime} to decompose, contributing to micro-particle pollution if landfilled.\n\n• Carbon Footprint & Energy: Manufacturing this item generates ${item.carbonFootprint}. Diverting it to certified circular sorting loops reduces greenhouse gas emissions by up to 85% compared to virgin raw material refining.\n\n• Circular Economy Value: Highly recoverable when sorted correctly in the ${item.recyclingBinType}. ${item.funFact}",
                recyclabilityScore = aiResult?.recyclabilityScore ?: item.recyclabilityScore,
                isAiAnalyzed = true,
                aiModelUsed = aiResult?.modelName ?: "Gemini 3.5 Flash"
            )
            _scannerState.update {
                it.copy(
                    selectedItem = enhancedItem,
                    currentAiProfile = aiResult,
                    isAnalyzing = false,
                    showDetailDialog = true
                )
            }
            repository.recordScan(enhancedItem)
        }
    }

    fun triggerQuickArScanSimulation(bitmap: Bitmap? = null, marker: ArHitMarker? = null) {
        analyzeDetectedObjectFrame(bitmap, marker)
    }

    fun dismissDetailDialog() {
        _scannerState.update { it.copy(showDetailDialog = false) }
    }

    fun resetScanner() {
        _scannerState.update {
            it.copy(
                isScanningActive = false,
                isAnalyzing = false,
                selectedItem = null,
                showDetailDialog = false
            )
        }
    }

    // Solutions Actions
    fun selectSolution(solution: SustainabilitySolution) {
        val calculated = solution.savingsPerUnit * _solutionsState.value.calculatorInputValue * 365.0
        _solutionsState.update {
            it.copy(
                selectedSolution = solution,
                calculatedAnnualSavings = calculated,
                showDetailDialog = true
            )
        }
    }

    fun updateCalculatorInput(value: Double) {
        val sol = _solutionsState.value.selectedSolution
        val rate = sol?.savingsPerUnit ?: 1.0
        _solutionsState.update {
            it.copy(
                calculatorInputValue = value,
                calculatedAnnualSavings = value * rate * 365.0
            )
        }
    }

    fun dismissSolutionDialog() {
        _solutionsState.update { it.copy(showDetailDialog = false) }
    }

    // Virtual Hub Actions
    fun selectHubModel(model: VirtualHubModel) {
        _hubState.update { it.copy(selectedModel = model) }
    }

    fun toggleArPlacementMode(active: Boolean) {
        _hubState.update { it.copy(isArCameraPlacementActive = active) }
    }

    fun updateEnvironment(environment: String) {
        _hubState.update { it.copy(selectedRoomEnvironment = environment) }
    }

    fun updateSimulationParameter(value: Float) {
        _hubState.update { it.copy(simulationParameterValue = value) }
    }

    fun toggleSpecsDialog(show: Boolean) {
        _hubState.update { it.copy(showSpecsDialog = show) }
    }

    fun saveCurrentModelPlacement() {
        val model = _hubState.value.selectedModel ?: return
        viewModelScope.launch {
            repository.saveHubSpacePlacement(
                model = model,
                room = _hubState.value.selectedRoomEnvironment,
                scale = model.realisticScale,
                x = 0.5f,
                y = 0.5f
            )
            _hubState.update {
                it.copy(toastFeedback = "✨ Placed ${model.title} in your ${_hubState.value.selectedRoomEnvironment}!")
            }
            delay(2500)
            _hubState.update { it.copy(toastFeedback = null) }
        }
    }

    fun removePlacement(modelId: String) {
        viewModelScope.launch {
            repository.removeHubSpacePlacement(modelId)
        }
    }

    // Tips and Challenges
    fun selectTipContext(context: TipTimeContext) {
        _tipsState.update { it.copy(selectedContext = context) }
    }

    fun completeEcoTip(tip: EcoTip) {
        viewModelScope.launch {
            repository.recordTipCompleted(tip)
            _tipsState.update {
                it.copy(feedbackMessage = "🌱 Awesome! Completed '${tip.title}'")
            }
            delay(2500)
            _tipsState.update { it.copy(feedbackMessage = null) }
        }
    }

    fun advanceChallenge(challengeId: String) {
        viewModelScope.launch {
            repository.updateChallengeProgress(challengeId, incrementDay = true)
            _tipsState.update {
                it.copy(feedbackMessage = "🎯 Checkpoint completed! Great progress on your green habit sprint!")
            }
            delay(2500)
            _tipsState.update { it.copy(feedbackMessage = null) }
        }
    }

    // Sustainability Facts Utility Actions
    fun cycleNextDailyFact() {
        val current = _dailyFact.value
        val next = repository.getNextSustainabilityFact(current.id)
        _dailyFact.value = next
    }

    fun refreshDailyFact() {
        _dailyFact.value = repository.getDailySustainabilityFact()
    }

    // Enhanced Home Actions
    fun getQuickEcoActions(): List<QuickEcoAction> = repository.getQuickEcoActions()

    fun logQuickEcoAction(action: QuickEcoAction) {
        viewModelScope.launch {
            repository.recordQuickAction(action)
            _homeFeedback.update { "✨ Logged: ${action.title}" }
            delay(3000)
            _homeFeedback.update { null }
        }
    }

    fun submitQuizAnswer(optionIndex: Int) {
        val current = _quizState.value.currentQuestion
        val isCorrect = optionIndex == current.correctIndex
        _quizState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isSubmitted = true,
                isCorrect = isCorrect,
                showExplanation = true
            )
        }
        viewModelScope.launch {
            if (isCorrect) {
                repository.recordQuizCompleted(current)
                _homeFeedback.update { "🎉 Correct! Great environmental insight!" }
            } else {
                _homeFeedback.update { "🌱 Keep learning! Explanatory insight unlocked." }
            }
            delay(3000)
            _homeFeedback.update { null }
        }
    }

    fun cycleNextQuiz() {
        val quizzes = repository.getClimateQuizQuestions()
        val currentId = _quizState.value.currentQuestion.id
        val currentIndex = quizzes.indexOfFirst { it.id == currentId }
        val nextIndex = if (currentIndex != -1 && currentIndex + 1 < quizzes.size) currentIndex + 1 else 0
        _quizState.update {
            HomeQuizUiState(
                currentQuestion = quizzes[nextIndex],
                selectedOptionIndex = null,
                isSubmitted = false,
                isCorrect = false,
                showExplanation = false
            )
        }
    }

    fun getBadges(profile: UserEcoProfileEntity, scansCount: Int): List<EcoBadge> {
        val scans = scansCount
        val plastic = profile.totalPlasticSavedKg
        val water = profile.totalWaterSavedLiters
        val co2 = profile.totalCo2OffsetKg

        return listOf(
            EcoBadge(
                id = "badge_first_scan",
                title = "AR Pioneer",
                description = "Scan your first waste item with AR",
                iconEmoji = "🎯",
                isUnlocked = scans >= 1,
                progressLabel = if (scans >= 1) "Unlocked" else "$scans/1 Scan"
            ),
            EcoBadge(
                id = "badge_action_hero",
                title = "Habit Sprinter",
                description = "Complete 3+ waste classifications",
                iconEmoji = "⚡",
                isUnlocked = scans >= 3,
                progressLabel = if (scans >= 3) "Unlocked" else "$scans/3 Scans"
            ),
            EcoBadge(
                id = "badge_plastic_saver",
                title = "Plastic Buster",
                description = "Prevent over 1.0 kg of plastic waste",
                iconEmoji = "🚯",
                isUnlocked = plastic >= 1.0,
                progressLabel = String.format(java.util.Locale.US, "%.1f/1.0 kg", plastic)
            ),
            EcoBadge(
                id = "badge_water_hero",
                title = "Aqua Guardian",
                description = "Conserve 50+ Liters of fresh water",
                iconEmoji = "💧",
                isUnlocked = water >= 50.0,
                progressLabel = String.format(java.util.Locale.US, "%.0f/50 L", water)
            ),
            EcoBadge(
                id = "badge_co2_crusher",
                title = "Carbon Neutralizer",
                description = "Offset 5.0+ kg of CO2 equivalent",
                iconEmoji = "🍃",
                isUnlocked = co2 >= 5.0,
                progressLabel = String.format(java.util.Locale.US, "%.1f/5.0 kg", co2)
            ),
            EcoBadge(
                id = "badge_master",
                title = "Eco Master",
                description = "Complete 10+ waste classifications",
                iconEmoji = "🌍",
                isUnlocked = scans >= 10,
                progressLabel = if (scans >= 10) "Unlocked" else "$scans/10 Scans"
            )
        )
    }

    fun getWeeklyActivity(): List<DayActivity> {
        return listOf(
            DayActivity("Mon", 45),
            DayActivity("Tue", 60),
            DayActivity("Wed", 80),
            DayActivity("Thu", 50),
            DayActivity("Fri", 95),
            DayActivity("Sat", 110),
            DayActivity("Sun", 85, isToday = true)
        )
    }

    // Eco Shop Actions
    fun getAvailableEcoCredits(profile: UserEcoProfileEntity, scansCount: Int): Int {
        val earnedCredits = 250 + (scansCount * 60) + (profile.totalPlasticSavedKg * 80).toInt() + (profile.totalCo2OffsetKg * 20).toInt()
        return (earnedCredits - _shopState.value.spentEcoCredits).coerceAtLeast(0)
    }

    fun onShopCategorySelected(category: String) {
        _shopState.update { it.copy(selectedCategory = category) }
    }

    fun onShopSearchQueryChanged(query: String) {
        _shopState.update { it.copy(searchQuery = query) }
    }

    fun onSelectShopProduct(product: com.example.data.model.EcoProduct) {
        _shopState.update { it.copy(selectedProduct = product, showDetailDialog = true) }
    }

    fun onDismissShopDialog() {
        _shopState.update { it.copy(showDetailDialog = false) }
    }

    fun onDismissOrderSuccessDialog() {
        _shopState.update { it.copy(showOrderSuccessDialog = false) }
    }

    fun redeemProductWithCredits(product: com.example.data.model.EcoProduct, availableCredits: Int) {
        if (availableCredits >= product.ecoCreditsPrice) {
            _shopState.update {
                it.copy(
                    spentEcoCredits = it.spentEcoCredits + product.ecoCreditsPrice,
                    purchasedProductIds = it.purchasedProductIds + product.id,
                    showDetailDialog = false,
                    showOrderSuccessDialog = true,
                    lastOrderedProduct = product,
                    feedbackMessage = "🎉 Successfully redeemed ${product.name} with ${product.ecoCreditsPrice} Eco Credits!"
                )
            }
            viewModelScope.launch {
                delay(3500)
                _shopState.update { it.copy(feedbackMessage = null) }
            }
        } else {
            val needed = product.ecoCreditsPrice - availableCredits
            _shopState.update {
                it.copy(feedbackMessage = "⚠️ You need $needed more Eco Credits! Scan recyclable items to earn more.")
            }
            viewModelScope.launch {
                delay(3500)
                _shopState.update { it.copy(feedbackMessage = null) }
            }
        }
    }

    fun purchaseProductWithDirectOrder(product: com.example.data.model.EcoProduct) {
        _shopState.update {
            it.copy(
                purchasedProductIds = it.purchasedProductIds + product.id,
                showDetailDialog = false,
                showOrderSuccessDialog = true,
                lastOrderedProduct = product,
                feedbackMessage = "🌱 Order confirmed for ${product.name}!"
            )
        }
        viewModelScope.launch {
            delay(3500)
            _shopState.update { it.copy(feedbackMessage = null) }
        }
    }

    // -------------------------------------------------------------
    // 3R Circular Products Database & AR Integration
    // -------------------------------------------------------------

    fun on3RSearchQueryChanged(query: String) {
        _threeRState.update { it.copy(searchQuery = query) }
    }

    fun onSelect3RClassificationFilter(classification: String) {
        _threeRState.update { it.copy(activeClassification = classification) }
    }

    fun select3RProduct(product: Product3REntity) {
        _threeRState.update {
            it.copy(
                selectedProduct = product,
                showProductDetailDialog = true
            )
        }
    }

    fun dismiss3RProductDialog() {
        _threeRState.update { it.copy(showProductDetailDialog = false) }
    }

    fun launchArScanFor3RProduct(product: Product3REntity) {
        // Lock this 3R product into ARCore detector with its exact resin code and dimensions
        val marker = ArObjectDetectionService.getInstance().lockTarget3RProduct(
            name = product.name,
            category = "${product.threeRClassification} • ${product.category}",
            resinCode = product.resinCodeOrStandard,
            dimensionsMm = product.dimensionsMm,
            densityGcm3 = product.densityGcm3,
            threeRClassification = product.threeRClassification,
            reduceTip = product.howToReduce,
            reuseTip = product.howToReuseUpcycle,
            recycleTip = product.howToRecycle,
            productId = product.id
        )

        // Find or map matching WasteItem if available
        val matchedWasteItem = repository.getAllWasteItems().find {
            it.name.contains(product.name.take(6), ignoreCase = true) ||
            it.materialType.contains(product.category, ignoreCase = true)
        } ?: repository.getAllWasteItems().first()

        _scannerState.update {
            it.copy(
                selectedItem = matchedWasteItem,
                showDetailDialog = false,
                scanSuccessMessage = "🎯 AR Target Locked on ${product.name}! Crosshair calibrated."
            )
        }

        _threeRState.update {
            it.copy(
                selectedProduct = product,
                showProductDetailDialog = false,
                isArTargetActive = true,
                toastFeedback = "Target locked: ${product.name} (${product.dimensionsMm})"
            )
        }

        viewModelScope.launch {
            delay(4000)
            _scannerState.update { it.copy(scanSuccessMessage = null) }
            _threeRState.update { it.copy(toastFeedback = null) }
        }
    }
}
