package com.example.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.BuildConfig
import com.example.ar.ArHitMarker
import com.example.data.local.Product3REntity
import com.example.data.model.EcoActionType
import com.example.data.model.WasteCategory
import com.example.data.model.WasteItem
import com.example.data.remote.gemini.AiSustainabilityProfileResult
import com.example.data.remote.gemini.DetectedObjectAnalysis
import com.example.data.remote.gemini.DetectedProductComponent
import com.example.data.remote.gemini.GeminiClient
import com.example.data.remote.gemini.GeminiContent
import com.example.data.remote.gemini.GeminiGenerateRequest
import com.example.data.remote.gemini.GeminiGenerationConfig
import com.example.data.remote.gemini.GeminiInlineData
import com.example.data.remote.gemini.GeminiPart
import com.example.data.remote.gemini.GeminiSustainabilityAnalyzer
import com.example.data.remote.gemini.ThreeRGuidance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import android.util.Base64

private const val TAG = "GeminiSustainRepo"

/**
 * Interface contract defining sustainability and 3R analysis operations powered by Gemini AI.
 */
interface IGeminiSustainabilityRepository {
    val isApiKeyConfigured: Boolean
    val configuredModelName: String

    /**
     * Performs multimodal visual sustainability analysis using captured camera frame or uploaded image.
     */
    suspend fun analyzeProductFrame(
        bitmap: Bitmap?,
        detectedMarker: ArHitMarker? = null,
        barcode: String? = null
    ): Result<AiSustainabilityProfileResult>

    /**
     * Performs text-based sustainability analysis for a product name, material label, or barcode search.
     */
    suspend fun analyzeProductQuery(
        query: String
    ): Result<AiSustainabilityProfileResult>

    /**
     * Generates structured 3R (Reduce, Reuse, Recycle, Repair, Special Disposal) guidance.
     */
    suspend fun generate3RGuidance(
        productName: String,
        material: String
    ): Result<ThreeRGuidance>

    /**
     * Recommends sustainable, low-carbon circular alternatives for a given product and material.
     */
    suspend fun getEcoAlternatives(
        productName: String,
        currentMaterial: String
    ): Result<List<String>>

    /**
     * Performs a Lifecycle Assessment (LCA) estimation (manufacturing, usage, disposal, carbon footprint).
     */
    suspend fun analyzeLifeCycleImpact(
        productName: String,
        material: String
    ): Result<String>
}

/**
 * Repository implementation that interfaces with the Google Gemini API using the secure key from BuildConfig.
 * Provides fallback to deterministic local circular economy datasets when offline or unauthenticated.
 */
class GeminiSustainabilityRepository private constructor() : IGeminiSustainabilityRepository {

    companion object {
        @Volatile
        private var instance: GeminiSustainabilityRepository? = null

        fun getInstance(): GeminiSustainabilityRepository {
            return instance ?: synchronized(this) {
                instance ?: GeminiSustainabilityRepository().also { instance = it }
            }
        }
    }

    override val isApiKeyConfigured: Boolean
        get() {
            val key = getApiKey()
            return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        }

    override val configuredModelName: String = "gemini-3.5-flash"

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    override suspend fun analyzeProductFrame(
        bitmap: Bitmap?,
        detectedMarker: ArHitMarker?,
        barcode: String?
    ): Result<AiSustainabilityProfileResult> = withContext(Dispatchers.IO) {
        try {
            val profile = GeminiSustainabilityAnalyzer.analyzeFrame(
                bitmap = bitmap,
                detectedMarker = detectedMarker,
                barcodeInput = barcode
            )
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing product frame: ${e.message}", e)
            val fallback = GeminiSustainabilityAnalyzer.createFallbackSustainabilityProfile(
                bitmap = bitmap,
                detectedMarker = detectedMarker,
                barcodeInput = barcode
            )
            Result.success(fallback)
        }
    }

    override suspend fun analyzeProductQuery(
        query: String
    ): Result<AiSustainabilityProfileResult> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (isApiKeyConfigured && query.isNotBlank()) {
            try {
                val prompt = """
                    You are an expert AI Sustainability & 3R Product Analyzer (Reduce, Reuse, Recycle, Repair, Special Disposal).
                    Analyze the following product or packaging: "$query"
                    
                    Identify its distinct components, material composition, decomposition timeline, carbon footprint, and circular 3R recommendations.
                    
                    Return ONLY valid JSON (no markdown formatting, no code fences):
                    {
                      "itemName": "$query",
                      "category": "Plastics / Paper & Cardboard / Glass / Metals & Cans / Organic Waste / E-Waste & Batteries / Cartons & Composites / Fabrics & Textiles",
                      "primaryAction": "RECYCLE / REUSE / REDUCE / REPAIR / SPECIAL_DISPOSAL",
                      "confidenceScore": 95,
                      "isConfident": true,
                      "lowConfidenceReason": "",
                      "materialType": "e.g. High Density Polyethylene (HDPE #2)",
                      "decompositionTime": "e.g. 100 Years",
                      "carbonFootprint": "e.g. 120g CO₂e",
                      "recyclingBinType": "Yellow / Mixed Recycling Bin",
                      "recyclabilityScore": 90,
                      "sustainabilityDescription": "Detailed lifecycle and circular recovery summary.",
                      "detectedComponents": [
                        {
                          "name": "Main Container",
                          "material": "HDPE #2",
                          "category": "Plastic",
                          "actionType": "RECYCLE",
                          "actionLabel": "HDPE #2 – Recycle",
                          "separationNotes": "Empty and rinse before placing in bin",
                          "normLeft": 0.2, "normTop": 0.2, "normRight": 0.8, "normBottom": 0.8
                        }
                      ],
                      "threeR": {
                        "reduce": "Choose bulk concentrates or refill stations to avoid repeat packaging.",
                        "reuse": "Rinse clean and repurpose for storage or garden watering.",
                        "repair": null,
                        "recycle": "Rinse clean, keep cap attached if matching material, and place into recycling bin.",
                        "specialDisposal": null,
                        "isHazardousOrSpecial": false,
                        "safetyWarning": ""
                      },
                      "stepByStepGuide": ["Empty all contents", "Rinse with water", "Sort into curbside bin"],
                      "upcyclingIdeas": ["Cut into a handy scoop or plant waterer"],
                      "ecoFriendlyAlternative": "Refill pouches or solid bar equivalent",
                      "funFact": "HDPE can be recycled up to 10 times without significant polymer degradation!",
                      "arVisualBadge": "♻️ HDPE #2 • 90% Recyclable"
                    }
                """.trimIndent()

                val request = GeminiGenerateRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt))
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.2f,
                        topP = 0.9f,
                        topK = 40,
                        maxOutputTokens = 2048
                    )
                )

                val response = GeminiClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    var cleanJson = text.trim()
                    if (cleanJson.startsWith("```json")) cleanJson = cleanJson.removePrefix("```json")
                    if (cleanJson.startsWith("```")) cleanJson = cleanJson.removePrefix("```")
                    if (cleanJson.endsWith("```")) cleanJson = cleanJson.removeSuffix("```")
                    cleanJson = cleanJson.trim()

                    val json = JSONObject(cleanJson)
                    val result = parseQueryJsonResponse(json, query)
                    return@withContext Result.success(result)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini query analysis failed: ${e.message}", e)
            }
        }

        // Fallback for query
        val fallback = GeminiSustainabilityAnalyzer.createFallbackSustainabilityProfile(
            bitmap = null,
            detectedMarker = null,
            barcodeInput = query
        )
        Result.success(fallback)
    }

    override suspend fun generate3RGuidance(
        productName: String,
        material: String
    ): Result<ThreeRGuidance> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (isApiKeyConfigured) {
            try {
                val prompt = """
                    Provide practical, accurate 3R sustainability guidance for "$productName" (Material: $material).
                    Return ONLY valid JSON (no markdown):
                    {
                      "reduce": "Actionable way to eliminate or reduce consumption of this product/packaging.",
                      "reuse": "Creative and safe ways to reuse or repurpose it.",
                      "repair": "Repair or maintenance tip if applicable, or null.",
                      "recycle": "Exact sorting stream, bin type, rinsing/separation instructions.",
                      "specialDisposal": "Hazardous/e-waste facility guidelines if applicable, or null.",
                      "isHazardousOrSpecial": false,
                      "safetyWarning": "Safety warning if hazardous, or null"
                    }
                """.trimIndent()

                val request = GeminiGenerateRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt))
                        )
                    )
                )

                val response = GeminiClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    var cleanJson = text.trim()
                    if (cleanJson.startsWith("```json")) cleanJson = cleanJson.removePrefix("```json")
                    if (cleanJson.startsWith("```")) cleanJson = cleanJson.removePrefix("```")
                    if (cleanJson.endsWith("```")) cleanJson = cleanJson.removeSuffix("```")
                    val json = JSONObject(cleanJson.trim())

                    val guidance = ThreeRGuidance(
                        reduce = json.optString("reduce", "Choose packaging-free or refillable options to cut down on waste."),
                        reuse = json.optString("reuse", "Clean and repurpose around the home or garden."),
                        repair = json.optString("repair").takeIf { it.isNotBlank() && it != "null" },
                        recycle = json.optString("recycle", "Check local recycling bin guidelines, rinse clean, and sort."),
                        specialDisposal = json.optString("specialDisposal").takeIf { it.isNotBlank() && it != "null" },
                        isHazardousOrSpecial = json.optBoolean("isHazardousOrSpecial", false),
                        safetyWarning = json.optString("safetyWarning").takeIf { it.isNotBlank() && it != "null" }
                    )
                    return@withContext Result.success(guidance)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini generate3RGuidance failed: ${e.message}", e)
            }
        }

        // Deterministic Fallback
        Result.success(
            ThreeRGuidance(
                reduce = "Opt for bulk purchases or reusable alternatives to minimize repetitive consumption.",
                reuse = "Clean thoroughly and repurpose for household storage, crafts, or gardening containers.",
                repair = null,
                recycle = "Rinse clean, separate distinct material layers, and place into municipal recycling stream.",
                specialDisposal = null,
                isHazardousOrSpecial = false,
                safetyWarning = null
            )
        )
    }

    override suspend fun getEcoAlternatives(
        productName: String,
        currentMaterial: String
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (isApiKeyConfigured) {
            try {
                val prompt = """
                    Give 3 concise, highly practical eco-friendly and reusable alternatives for "$productName" (made of $currentMaterial).
                    Return ONLY valid JSON array of strings (no markdown):
                    ["Alternative 1", "Alternative 2", "Alternative 3"]
                """.trimIndent()

                val request = GeminiGenerateRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt))
                        )
                    )
                )

                val response = GeminiClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    var cleanJson = text.trim()
                    if (cleanJson.startsWith("```json")) cleanJson = cleanJson.removePrefix("```json")
                    if (cleanJson.startsWith("```")) cleanJson = cleanJson.removePrefix("```")
                    if (cleanJson.endsWith("```")) cleanJson = cleanJson.removeSuffix("```")
                    val jsonArray = org.json.JSONArray(cleanJson.trim())
                    val list = mutableListOf<String>()
                    for (i in 0 until jsonArray.length()) {
                        list.add(jsonArray.getString(i))
                    }
                    if (list.isNotEmpty()) {
                        return@withContext Result.success(list)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini getEcoAlternatives failed: ${e.message}", e)
            }
        }

        Result.success(
            listOf(
                "Refillable stainless steel or glass containers",
                "Certified compostable plant-based alternatives",
                "Concentrated tablets or bulk refill stations"
            )
        )
    }

    override suspend fun analyzeLifeCycleImpact(
        productName: String,
        material: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (isApiKeyConfigured) {
            try {
                val prompt = """
                    Provide a concise, 3-paragraph Life Cycle Assessment (LCA) summary for "$productName" composed of "$material":
                    1. Raw Material Extraction & Manufacturing Carbon Footprint
                    2. Typical Use Phase & Longevity
                    3. Circular End-of-Life: Landfill vs Recycling vs Reuse impact.
                """.trimIndent()

                val request = GeminiGenerateRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = prompt))
                        )
                    )
                )

                val response = GeminiClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    return@withContext Result.success(text.trim())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini analyzeLifeCycleImpact failed: ${e.message}", e)
            }
        }

        Result.success(
            "Lifecycle Impact for $productName ($material):\n\n" +
            "• Raw Material Refining: Extracting and polymerizing virgin material accounts for ~70% of total lifecycle emissions.\n\n" +
            "• Usage Phase: Designed for standard utility; maximizing reuse cycles significantly offsets the embodied manufacturing energy.\n\n" +
            "• Circular Recovery: Recycling reduces energy demand by up to 80% compared to virgin material processing and prevents persistence in landfills."
        )
    }

    private fun parseQueryJsonResponse(json: JSONObject, originalQuery: String): AiSustainabilityProfileResult {
        val itemName = json.optString("itemName", originalQuery)
        val categoryStr = json.optString("category", "Plastics")
        val primaryActionStr = json.optString("primaryAction", "RECYCLE")
        val confidenceScore = json.optInt("confidenceScore", 92)
        val isConfident = json.optBoolean("isConfident", true)
        val lowConfidenceReason = json.optString("lowConfidenceReason").takeIf { it.isNotBlank() }
        val materialType = json.optString("materialType", "Recyclable Polymer")
        val decompositionTime = json.optString("decompositionTime", "100 Years")
        val carbonFootprint = json.optString("carbonFootprint", "85g CO₂e")
        val recyclingBinType = json.optString("recyclingBinType", "Blue Recycling Bin")
        val recyclabilityScore = json.optInt("recyclabilityScore", 85)
        val sustainabilityDescription = json.optString(
            "sustainabilityDescription",
            "Sustainability profile for $itemName composed of $materialType."
        )

        val detectedComponents = mutableListOf<DetectedProductComponent>()
        val componentsArray = json.optJSONArray("detectedComponents")
        if (componentsArray != null) {
            for (i in 0 until componentsArray.length()) {
                val compJson = componentsArray.getJSONObject(i)
                detectedComponents.add(
                    DetectedProductComponent(
                        name = compJson.optString("name", "Component"),
                        material = compJson.optString("material", materialType),
                        category = compJson.optString("category", "Plastic"),
                        actionType = compJson.optString("actionType", "RECYCLE"),
                        actionLabel = compJson.optString("actionLabel", "$materialType – Recycle"),
                        separationNotes = compJson.optString("separationNotes", "Separate and recycle"),
                        normLeft = compJson.optDouble("normLeft", 0.2).toFloat(),
                        normTop = compJson.optDouble("normTop", 0.2).toFloat(),
                        normRight = compJson.optDouble("normRight", 0.8).toFloat(),
                        normBottom = compJson.optDouble("normBottom", 0.8).toFloat()
                    )
                )
            }
        }

        val threeRJson = json.optJSONObject("threeR")
        val threeRGuidance = if (threeRJson != null) {
            ThreeRGuidance(
                reduce = threeRJson.optString("reduce", "Choose reusable alternatives."),
                reuse = threeRJson.optString("reuse", "Clean and repurpose for storage."),
                repair = threeRJson.optString("repair").takeIf { it.isNotBlank() && it != "null" },
                recycle = threeRJson.optString("recycle", "Rinse and place in $recyclingBinType."),
                specialDisposal = threeRJson.optString("specialDisposal").takeIf { it.isNotBlank() && it != "null" },
                isHazardousOrSpecial = threeRJson.optBoolean("isHazardousOrSpecial", false),
                safetyWarning = threeRJson.optString("safetyWarning").takeIf { it.isNotBlank() && it != "null" }
            )
        } else null

        val category = when {
            categoryStr.contains("Plastic", ignoreCase = true) -> WasteCategory.PLASTIC
            categoryStr.contains("Paper", ignoreCase = true) || categoryStr.contains("Cardboard", ignoreCase = true) -> WasteCategory.PAPER_CARDBOARD
            categoryStr.contains("Glass", ignoreCase = true) -> WasteCategory.GLASS
            categoryStr.contains("Metal", ignoreCase = true) -> WasteCategory.METALS
            categoryStr.contains("Organic", ignoreCase = true) -> WasteCategory.ORGANIC
            categoryStr.contains("Battery", ignoreCase = true) || categoryStr.contains("Electronic", ignoreCase = true) || categoryStr.contains("E-Waste", ignoreCase = true) -> WasteCategory.ELECTRONIC
            categoryStr.contains("Composite", ignoreCase = true) || categoryStr.contains("Carton", ignoreCase = true) -> WasteCategory.COMPOSITE
            else -> WasteCategory.PLASTIC
        }

        val primaryAction = when (primaryActionStr.uppercase()) {
            "REDUCE" -> EcoActionType.REDUCE
            "REUSE" -> EcoActionType.REUSE
            "COMPOST" -> EcoActionType.COMPOST
            "SPECIAL", "SPECIAL_DISPOSAL" -> EcoActionType.SPECIAL_DISPOSAL
            else -> EcoActionType.RECYCLE
        }

        val stepList = mutableListOf<String>()
        val stepArray = json.optJSONArray("stepByStepGuide")
        if (stepArray != null) {
            for (i in 0 until stepArray.length()) {
                stepList.add(stepArray.getString(i))
            }
        }

        val upcycleList = mutableListOf<String>()
        val upcycleArray = json.optJSONArray("upcyclingIdeas")
        if (upcycleArray != null) {
            for (i in 0 until upcycleArray.length()) {
                upcycleList.add(upcycleArray.getString(i))
            }
        }

        val detectedObj = DetectedObjectAnalysis(
            name = itemName,
            material = materialType,
            category = category,
            confidence = (confidenceScore / 100f).coerceIn(0.1f, 1.0f),
            confidencePercentage = confidenceScore,
            isConfident = isConfident,
            unconfidentReason = lowConfidenceReason,
            recommendedAction = primaryAction,
            actionTitle = primaryAction.title,
            reason = "Material identified for circular economy sorting.",
            suggestions = stepList.ifEmpty { listOf("Empty contents", "Rinse clean", "Sort into $recyclingBinType") },
            decompositionTime = decompositionTime,
            carbonFootprint = carbonFootprint,
            recyclingBinType = recyclingBinType,
            upcyclingIdea = upcycleList.firstOrNull()
        )

        return AiSustainabilityProfileResult(
            itemName = itemName,
            category = category,
            primaryAction = primaryAction,
            materialType = materialType,
            decompositionTime = decompositionTime,
            carbonFootprint = carbonFootprint,
            recyclingBinType = recyclingBinType,
            recyclabilityScore = recyclabilityScore,
            confidenceLevel = (confidenceScore / 100f).coerceIn(0.1f, 1.0f),
            isConfident = isConfident,
            lowConfidenceReason = lowConfidenceReason,
            sustainabilityDescription = sustainabilityDescription,
            detectedObjects = listOf(detectedObj),
            detectedComponents = detectedComponents,
            threeRGuidance = threeRGuidance,
            stepByStepGuide = if (stepList.isNotEmpty()) stepList else listOf("Empty contents", "Rinse clean", "Sort into $recyclingBinType"),
            upcyclingIdeas = if (upcycleList.isNotEmpty()) upcycleList else listOf("Repurpose for household organization or storage"),
            ecoFriendlyAlternative = json.optString("ecoFriendlyAlternative", "Reusable stainless steel or zero-waste refill"),
            funFact = json.optString("funFact", "Sorting circular materials prevents landfill greenhouse gas emissions!"),
            arVisualBadge = json.optString("arVisualBadge", "♻️ Verified Sustainability Profile"),
            isAiGenerated = true,
            modelName = "Gemini 3.5 Flash",
            capturedBitmap = null
        )
    }
}
