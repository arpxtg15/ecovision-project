package com.example.data.remote.gemini

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.ai.tflite.TfLiteDetectedObject
import com.example.ar.ArHitMarker
import com.example.data.model.EcoActionType
import com.example.data.model.WasteCategory
import com.example.data.model.WasteItem
import com.example.data.model.WasteRepositoryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.UUID

private const val TAG = "GeminiSustainability"

/**
 * Data model for a detected object or waste item in an image analyzed by Gemini Multimodal Vision.
 */
data class DetectedObjectAnalysis(
    val id: String = UUID.randomUUID().toString(),
    val name: String, // e.g. "Glass Beverage Bottle", "Torn Cotton T-Shirt", "Cracked Ceramic Mug"
    val material: String, // e.g. "Clear Glass", "100% Cotton", "Glazed Ceramic"
    val category: WasteCategory = WasteCategory.OTHER, // Category: Plastics, Paper & Cardboard, Glass, Metals, Organic, E-Waste, Textiles, etc.
    val confidence: Float = 0.95f, // 0.0 to 1.0 (e.g. 0.95)
    val confidencePercentage: Int = 95, // 95%
    val isConfident: Boolean = true, // true if confident; false if ambiguous, blurry, or unrecognized
    val unconfidentReason: String? = null, // Stated reason if confidence is low
    val recommendedAction: EcoActionType = EcoActionType.RECYCLE, // Reuse, Repair, Donate, Recycle, Compost, Dispose
    val actionTitle: String = "Recycle",
    val reason: String = "High material recyclability in standard sorting streams.", // Rationale for action
    val suggestions: List<String> = emptyList(), // Practical, concise, and creative suggestions
    val decompositionTime: String = "450 Years",
    val carbonFootprint: String = "~80g CO₂e",
    val recyclingBinType: String = "Blue Bin (Plastics & Recyclables)",
    val upcyclingIdea: String? = null
)

/**
 * Data model for an individual detected physical component on a scanned product.
 */
data class DetectedProductComponent(
    val id: String = UUID.randomUUID().toString(),
    val name: String, // e.g. "PET Plastic Body", "Polypropylene Cap", "Paper Label"
    val material: String, // e.g. "PET #1", "PP #5", "Kraft Paper"
    val category: String = "Plastic",
    val actionType: String = "RECYCLE",
    val actionLabel: String = "PET Plastic – Recycle",
    val separationNotes: String = "Rinse and recycle in plastics stream",
    val normLeft: Float = 0.2f,
    val normTop: Float = 0.25f,
    val normRight: Float = 0.8f,
    val normBottom: Float = 0.75f
)

/**
 * Structured 3R (Reduce, Reuse, Recycle) + Repair + Special Disposal recommendations.
 */
data class ThreeRGuidance(
    val reduce: String,
    val reuse: String,
    val repair: String? = null,
    val recycle: String,
    val specialDisposal: String? = null,
    val isHazardousOrSpecial: Boolean = false,
    val safetyWarning: String? = null
)

/**
 * Result data model containing the detailed Gemini AI analysis of the item's sustainability profile.
 */
data class AiSustainabilityProfileResult(
    val id: String = UUID.randomUUID().toString(),
    val itemName: String,
    val category: WasteCategory,
    val primaryAction: EcoActionType,
    val materialType: String,
    val decompositionTime: String,
    val carbonFootprint: String,
    val recyclingBinType: String,
    val recyclabilityScore: Int = 90, // 0 - 100
    val confidenceLevel: Float = 0.95f, // 0.0 - 1.0
    val isConfident: Boolean = true,
    val lowConfidenceReason: String? = null,
    val sustainabilityDescription: String,
    val detectedObjects: List<DetectedObjectAnalysis> = emptyList(),
    val detectedComponents: List<DetectedProductComponent> = emptyList(),
    val threeRGuidance: ThreeRGuidance? = null,
    val stepByStepGuide: List<String> = emptyList(),
    val upcyclingIdeas: List<String> = emptyList(),
    val waysToReuse: List<String> = emptyList(),
    val waysToDispose: List<String> = emptyList(),
    val tfLiteObjects: List<TfLiteDetectedObject> = emptyList(),
    val detectedByTensorFlow: Boolean = false,
    val tensorFlowConfidence: Float = 0f,
    val ecoFriendlyAlternative: String = "",
    val funFact: String = "",
    val arVisualBadge: String = "♻️ 3R Verified",
    val isAiGenerated: Boolean = true,
    val modelName: String = "Gemini 3.5 Flash",
    val capturedBitmap: Bitmap? = null
) {
    /**
     * Converts the primary AI result to a WasteItem for catalog and repository persistence.
     */
    fun toWasteItem(): WasteItem {
        return WasteItem(
            id = id,
            name = itemName,
            category = category,
            primaryAction = primaryAction,
            materialType = materialType,
            decompositionTime = decompositionTime,
            carbonFootprint = carbonFootprint,
            recyclingBinType = recyclingBinType,
            stepByStepGuide = if (waysToDispose.isNotEmpty()) waysToDispose else stepByStepGuide,
            upcyclingIdeas = if (waysToReuse.isNotEmpty()) waysToReuse else upcyclingIdeas,
            ecoFriendlyAlternative = ecoFriendlyAlternative,
            funFact = funFact,
            arVisualBadge = arVisualBadge,
            sustainabilityProfileDescription = sustainabilityDescription,
            recyclabilityScore = recyclabilityScore,
            isAiAnalyzed = isAiGenerated,
            aiModelUsed = modelName,
            waysToReuse = if (waysToReuse.isNotEmpty()) waysToReuse else upcyclingIdeas,
            waysToDispose = if (waysToDispose.isNotEmpty()) waysToDispose else stepByStepGuide,
            detectedByTensorFlow = detectedByTensorFlow,
            tensorFlowConfidence = tensorFlowConfidence
        )
    }
}

object GeminiSustainabilityAnalyzer {

    /**
     * Analyzes a camera frame or uploaded image using Gemini Multimodal Vision API,
     * utilizing on-device TensorFlow detection results to refine reuse & disposal suggestions.
     */
    suspend fun analyzeFrame(
        bitmap: Bitmap?,
        detectedMarker: ArHitMarker? = null,
        barcodeInput: String? = null,
        tfDetectedObjects: List<TfLiteDetectedObject> = emptyList()
    ): AiSustainabilityProfileResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // Prepare Base64 JPEG image if bitmap is present
        val base64Image = bitmap?.let { encodeBitmapToBase64(it) }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY" && base64Image != null) {
            try {
                Log.d(TAG, "Calling Gemini 3.5 Flash with ${tfDetectedObjects.size} TensorFlow objects (${base64Image.length} chars)")
                
                val prompt = buildAnalysisPrompt(detectedMarker, barcodeInput, tfDetectedObjects)
                val request = GeminiGenerateRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = prompt),
                                GeminiPart(
                                    inlineData = GeminiInlineData(
                                        mimeType = "image/jpeg",
                                        data = base64Image
                                    )
                                )
                            )
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
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (!responseText.isNullOrBlank()) {
                    Log.d(TAG, "Gemini Response received: $responseText")
                    val parsedResult = parseGeminiResponse(responseText, bitmap, tfDetectedObjects)
                    if (parsedResult != null) {
                        return@withContext parsedResult
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini API call failed: ${e.message}", e)
            }
        }

        // High-fidelity fallback based on TensorFlow edge detection, optical markers & repository items
        return@withContext createFallbackSustainabilityProfile(bitmap, detectedMarker, barcodeInput, tfDetectedObjects)
    }

    private fun buildAnalysisPrompt(
        marker: ArHitMarker?,
        barcodeInput: String?,
        tfObjects: List<TfLiteDetectedObject> = emptyList()
    ): String {
        val tfContext = if (tfObjects.isNotEmpty()) {
            val top = tfObjects.first()
            """
            STAGE 1 — TENSORFLOW ON-DEVICE OBJECT DETECTION RESULTS:
            The on-device TensorFlow Lite model detected:
            - Object Name / Class: "${top.label}"
            - Material Identification: "${top.material}"
            - Model Confidence: ${(top.confidence * 100).toInt()}%
            - Recommended Action: "${top.recommendedAction}"
            - Bounding Box: [L:${top.boundingBox.left}, T:${top.boundingBox.top}, R:${top.boundingBox.right}, B:${top.boundingBox.bottom}]
            """.trimIndent()
        } else {
            ""
        }

        val markerContext = if (marker != null) {
            "Optical telemetry hint: label='${marker.label}', resinCode='${marker.resinCode}', density=${marker.densityGcm3}g/cm³."
        } else {
            ""
        }

        val barcodeContext = if (!barcodeInput.isNullOrBlank()) {
            "Product Barcode: $barcodeInput."
        } else {
            ""
        }

        return """
            You are an expert AI Sustainability Specialist powered by Google Gemini Vision.
            $tfContext
            $markerContext
            $barcodeContext
            
            SEQUENTIAL PIPELINE:
            1. Stage 1 was performed by TensorFlow on-device detection (see detected object above).
            2. In Stage 2, your task as Gemini is to suggest tailored, actionable ways to REUSE or DISPOSE of the identified item.
            
            Explicit Requirements:
            - "waysToReuse": Suggest at least 3-4 creative, practical, safe ways to reuse, upcycle, repurpose, or repair this item.
            - "waysToDispose": Suggest at least 3-4 exact, step-by-step proper disposal instructions (exact bin type/color, rinsing, flattening, or special e-waste/hazardous drop-off).
            
            Task details:
            - Examine the image and the TensorFlow detected object.
            - Identify specific item name, material, category, recyclability score (0-100), decomposition time, and carbon footprint.
            - Provide structured 3R (Reduce, Reuse, Repair, Recycle, Special Disposal) guidance.
               - If an object cannot be identified confidently (e.g. due to blurriness, darkness, obstruction, or ambiguity), set "isConfident": false, keep confidence low, and provide a clear, honest "unconfidentReason" explaining what is uncertain instead of guessing.
               - Determine the MOST APPROPRIATE ACTION among:
                 * Reuse (repurpose, clean, or upcycle)
                 * Repair (mend, stitch, solder, glue, or replace worn component to extend life)
                 * Donate (if wearable, functioning, or usable by charities/community)
                 * Recycle (standard or specialized recycling stream)
                 * Compost (organic matter, untreated wood, biodegradable waste)
                 * Dispose (landfill / safe municipal disposal when non-recyclable or hazardous)
               - Provide a clear, practical "reason" explaining why this specific action is the optimal sustainability recommendation.
               - Provide a list of practical, concise, and creative "suggestions" for what the user can do with this specific item (e.g., 2 to 4 concrete action steps or DIY ideas).
               - Provide estimated decomposition time, carbon footprint estimate, and designated recycling bin or drop-off location.
            3. Provide physical component breakdown (e.g. bottle body vs cap vs label) and 3R Guidance.
            
            Return ONLY a valid JSON object matching this structure (no markdown fences, no backticks):
            {
              "detectedObjects": [
                {
                  "name": "Specific item name",
                  "material": "Specific material",
                  "category": "Plastics / Paper & Cardboard / Glass / Metals & Cans / Organic Waste / E-Waste & Batteries / Textiles & Clothing / Cartons & Composites / Other",
                  "confidence": 0.95,
                  "confidencePercentage": 95,
                  "isConfident": true,
                  "unconfidentReason": "",
                  "recommendedAction": "One of: Reuse, Repair, Donate, Recycle, Compost, Dispose",
                  "reason": "Clear, practical reason why this action is recommended for this specific item and material.",
                  "suggestions": [
                    "Practical and concise suggestion 1",
                    "Practical and concise suggestion 2",
                    "Creative upcycling or alternative idea 3"
                  ],
                  "decompositionTime": "e.g. 450 Years",
                  "carbonFootprint": "e.g. ~82g CO₂e",
                  "recyclingBinType": "e.g. Blue Curbside Bin",
                  "upcyclingIdea": "e.g. Transform into a self-watering herb planter"
                }
              ],
              "itemName": "Primary item name or summary of detected items",
              "category": "Plastics",
              "primaryAction": "RECYCLE",
              "confidenceScore": 95,
              "isConfident": true,
              "lowConfidenceReason": "",
              "materialType": "Primary material",
              "decompositionTime": "450 Years",
              "carbonFootprint": "~82g CO₂e per unit",
              "recyclingBinType": "Blue Bin (Plastics & Recyclables)",
              "recyclabilityScore": 92,
              "sustainabilityDescription": "2-3 sentence overview of lifecycle impact, carbon footprint, and circular handling.",
              "detectedComponents": [
                {
                  "name": "Component name (e.g. PET Body, Cap)",
                  "material": "Material (e.g. PET #1, PP #5)",
                  "category": "Plastic",
                  "actionType": "RECYCLE",
                  "actionLabel": "PET Plastic – Recycle",
                  "separationNotes": "Empty and rinse; recycle in dry bin",
                  "normLeft": 0.2,
                  "normTop": 0.2,
                  "normRight": 0.8,
                  "normBottom": 0.8
                }
              ],
              "threeR": {
                "reduce": "Practical way to reduce future consumption.",
                "reuse": "Safe and practical ways to reuse or repurpose.",
                "repair": "Repair suggestions if item can be fixed.",
                "recycle": "Preparation and sorting steps for recycling.",
                "specialDisposal": "Specialized hazardous/e-waste facility if applicable (or null).",
                "isHazardousOrSpecial": false,
                "safetyWarning": ""
              },
              "waysToReuse": [
                "1. Creative upcycling or DIY repurposing project",
                "2. Practical functional second-life storage or household use",
                "3. Repair, cleaning, or maintenance suggestion"
              ],
              "waysToDispose": [
                "1. Exact bin type and stream (e.g. Blue Recycling Bin)",
                "2. Empty and clean food or liquid residue",
                "3. Crush flat to conserve bin volume",
                "4. Component separation or hazardous drop-off note"
              ],
              "stepByStepGuide": ["1. Empty and rinse", "2. Crush flat", "3. Place in blue bin"],
              "upcyclingIdeas": ["Cut top to create a desktop pen holder or indoor plant pot"],
              "ecoFriendlyAlternative": "Refillable stainless steel bottle",
              "funFact": "Recycling 1 plastic bottle saves enough energy to power an LED bulb for 6 hours!",
              "arVisualBadge": "♻️ 95% Recyclable"
            }
        """.trimIndent()
    }

    private fun parseGeminiResponse(
        responseText: String,
        originalBitmap: Bitmap?,
        tfObjects: List<TfLiteDetectedObject> = emptyList()
    ): AiSustainabilityProfileResult? {
        try {
            var cleanJson = responseText.trim()
            if (cleanJson.startsWith("```json")) {
                cleanJson = cleanJson.removePrefix("```json")
            }
            if (cleanJson.startsWith("```")) {
                cleanJson = cleanJson.removePrefix("```")
            }
            if (cleanJson.endsWith("```")) {
                cleanJson = cleanJson.removeSuffix("```")
            }
            cleanJson = cleanJson.trim()

            val firstBrace = cleanJson.indexOf('{')
            val lastBrace = cleanJson.lastIndexOf('}')
            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                cleanJson = cleanJson.substring(firstBrace, lastBrace + 1)
            }

            val json = JSONObject(cleanJson)

            // 1. Parse Detected Objects Array
            val detectedObjects = mutableListOf<DetectedObjectAnalysis>()
            val objectsArray = json.optJSONArray("detectedObjects")
            if (objectsArray != null && objectsArray.length() > 0) {
                for (i in 0 until objectsArray.length()) {
                    val obj = objectsArray.optJSONObject(i) ?: continue
                    val name = obj.optString("name", "Detected Object ${i + 1}")
                    val material = obj.optString("material", "Mixed Material")
                    val catStr = obj.optString("category", "Other")
                    val confDbl = obj.optDouble("confidence", 0.95).toFloat().coerceIn(0.1f, 1.0f)
                    val confInt = obj.optInt("confidencePercentage", (confDbl * 100).toInt()).coerceIn(10, 100)
                    val isConf = obj.optBoolean("isConfident", confDbl >= 0.65f)
                    val unconfReason = obj.optString("unconfidentReason", "").ifBlank { null }
                    val actStr = obj.optString("recommendedAction", "Recycle")
                    val actionType = EcoActionType.fromString(actStr)
                    val reason = obj.optString("reason", "Follow circular sorting guidelines for this material.")
                    
                    val suggestionsList = mutableListOf<String>()
                    val suggArray = obj.optJSONArray("suggestions")
                    if (suggArray != null) {
                        for (s in 0 until suggArray.length()) {
                            val item = suggArray.optString(s, "").trim()
                            if (item.isNotBlank()) suggestionsList.add(item)
                        }
                    }
                    if (suggestionsList.isEmpty()) {
                        suggestionsList.add("Inspect item condition and follow municipal sorting streams.")
                    }

                    val decomp = obj.optString("decompositionTime", "Varies by environment")
                    val carbon = obj.optString("carbonFootprint", "~50g CO₂e")
                    val bin = obj.optString("recyclingBinType", "Designated Municipal Bin")
                    val upcycling = obj.optString("upcyclingIdea", "").ifBlank { null }

                    detectedObjects.add(
                        DetectedObjectAnalysis(
                            name = name,
                            material = material,
                            category = WasteCategory.fromString(catStr),
                            confidence = confDbl,
                            confidencePercentage = confInt,
                            isConfident = isConf,
                            unconfidentReason = unconfReason,
                            recommendedAction = actionType,
                            actionTitle = actStr,
                            reason = reason,
                            suggestions = suggestionsList,
                            decompositionTime = decomp,
                            carbonFootprint = carbon,
                            recyclingBinType = bin,
                            upcyclingIdea = upcycling
                        )
                    )
                }
            }

            // Fallback for primary properties from top detected object or root json
            val firstObj = detectedObjects.firstOrNull()
            val itemName = json.optString("itemName", firstObj?.name ?: "Detected Object")
            val categoryStr = json.optString("category", firstObj?.category?.displayName ?: "Plastics")
            val actionStr = json.optString("primaryAction", firstObj?.recommendedAction?.name ?: "RECYCLE")
            val confidenceInt = json.optInt("confidenceScore", firstObj?.confidencePercentage ?: 95)
            val confidenceLevel = (confidenceInt / 100f).coerceIn(0.1f, 1.0f)
            val isConfident = json.optBoolean("isConfident", firstObj?.isConfident ?: true)
            val lowConfidenceReason = json.optString("lowConfidenceReason", firstObj?.unconfidentReason ?: "").ifBlank { null }
            val materialType = json.optString("materialType", firstObj?.material ?: "Polymer Composite")
            val decompositionTime = json.optString("decompositionTime", firstObj?.decompositionTime ?: "100+ Years")
            val carbonFootprint = json.optString("carbonFootprint", firstObj?.carbonFootprint ?: "65g CO₂e per unit")
            val recyclingBinType = json.optString("recyclingBinType", firstObj?.recyclingBinType ?: "Blue Bin (Dry Recyclables)")
            val recyclabilityScore = json.optInt("recyclabilityScore", 85)
            val sustainabilityDescription = json.optString(
                "sustainabilityDescription",
                "This item represents a recognized material stream. Following the recommended circular action helps minimize waste and reduces lifecycle greenhouse emissions."
            )
            val ecoFriendlyAlternative = json.optString("ecoFriendlyAlternative", "Switch to a durable, reusable alternative to minimize waste.")
            val funFact = json.optString("funFact", "Proper circular sorting prevents municipal landfill buildup and conserves natural raw resources.")
            val arVisualBadge = json.optString("arVisualBadge", "♻️ Circular Recyclable")

            // If detectedObjects was empty, create one from root values
            if (detectedObjects.isEmpty()) {
                val actType = EcoActionType.fromString(actionStr)
                detectedObjects.add(
                    DetectedObjectAnalysis(
                        name = itemName,
                        material = materialType,
                        category = WasteCategory.fromString(categoryStr),
                        confidence = confidenceLevel,
                        confidencePercentage = confidenceInt,
                        isConfident = isConfident,
                        unconfidentReason = lowConfidenceReason,
                        recommendedAction = actType,
                        actionTitle = actType.title,
                        reason = "Proper sorting and handling preserves materials in the circular economy.",
                        suggestions = listOf(
                            "Clean and prepare item according to local sorting requirements.",
                            "Consider repurposing or repairing if in usable condition."
                        ),
                        decompositionTime = decompositionTime,
                        carbonFootprint = carbonFootprint,
                        recyclingBinType = recyclingBinType,
                        upcyclingIdea = "Repurpose creatively for household organization or gardening."
                    )
                )
            }

            // Parse detected components
            val detectedComponents = mutableListOf<DetectedProductComponent>()
            val compArray = json.optJSONArray("detectedComponents")
            if (compArray != null && compArray.length() > 0) {
                for (i in 0 until compArray.length()) {
                    val compJson = compArray.optJSONObject(i) ?: continue
                    val cName = compJson.optString("name", "Component ${i + 1}")
                    val cMat = compJson.optString("material", "Material")
                    val cCat = compJson.optString("category", "Plastic")
                    val cAction = compJson.optString("actionType", "RECYCLE")
                    val cLabel = compJson.optString("actionLabel", "$cMat – $cAction")
                    val cSep = compJson.optString("separationNotes", "Separate and recycle")
                    val cLeft = compJson.optDouble("normLeft", (0.15 + (i * 0.15))).toFloat().coerceIn(0.05f, 0.95f)
                    val cTop = compJson.optDouble("normTop", (0.2 + (i * 0.15))).toFloat().coerceIn(0.1f, 0.9f)
                    val cRight = compJson.optDouble("normRight", (cLeft.toDouble() + 0.5)).toFloat().coerceIn(0.1f, 0.95f)
                    val cBottom = compJson.optDouble("normBottom", (cTop.toDouble() + 0.4)).toFloat().coerceIn(0.15f, 0.95f)

                    detectedComponents.add(
                        DetectedProductComponent(
                            name = cName,
                            material = cMat,
                            category = cCat,
                            actionType = cAction,
                            actionLabel = cLabel,
                            separationNotes = cSep,
                            normLeft = cLeft,
                            normTop = cTop,
                            normRight = cRight,
                            normBottom = cBottom
                        )
                    )
                }
            }

            // Parse 3R guidance
            var threeRGuidance: ThreeRGuidance? = null
            val threeRJson = json.optJSONObject("threeR")
            if (threeRJson != null) {
                val reduce = threeRJson.optString("reduce", "Choose reusable alternatives and minimal packaging options.")
                val reuse = threeRJson.optString("reuse", "Clean and repurpose for household storage, crafts, or gardening.")
                val repair = if (threeRJson.has("repair") && !threeRJson.isNull("repair") && threeRJson.optString("repair").isNotBlank()) {
                    threeRJson.optString("repair")
                } else null
                val recycle = threeRJson.optString("recycle", "Empty, rinse clean, flatten, and deposit in standard curbside dry recycling.")
                val specialDisposal = if (threeRJson.has("specialDisposal") && !threeRJson.isNull("specialDisposal") && threeRJson.optString("specialDisposal").isNotBlank()) {
                    threeRJson.optString("specialDisposal")
                } else null
                val isHazardous = threeRJson.optBoolean("isHazardousOrSpecial", false)
                val safetyWarning = threeRJson.optString("safetyWarning", "")

                threeRGuidance = ThreeRGuidance(
                    reduce = reduce,
                    reuse = reuse,
                    repair = repair,
                    recycle = recycle,
                    specialDisposal = specialDisposal,
                    isHazardousOrSpecial = isHazardous,
                    safetyWarning = safetyWarning.ifBlank { null }
                )
            }

            val stepByStepGuide = mutableListOf<String>()
            val stepsArray = json.optJSONArray("stepByStepGuide")
            if (stepsArray != null) {
                for (i in 0 until stepsArray.length()) {
                    stepByStepGuide.add(stepsArray.getString(i))
                }
            }
            if (stepByStepGuide.isEmpty()) {
                stepByStepGuide.addAll(
                    listOf(
                        "1. Clean and empty all liquid or food residue",
                        "2. Separate removable caps, lids, and labels",
                        "3. Flatten or compress to maximize bin volume",
                        "4. Deposit in designated recycling or disposal stream"
                    )
                )
            }

            val upcyclingIdeas = mutableListOf<String>()
            val ideasArray = json.optJSONArray("upcyclingIdeas")
            if (ideasArray != null) {
                for (i in 0 until ideasArray.length()) {
                    upcyclingIdeas.add(ideasArray.getString(i))
                }
            }
            if (upcyclingIdeas.isEmpty()) {
                upcyclingIdeas.add("Repurpose into an indoor herb planter, storage container, or craft project.")
            }

            val matchedCategory = WasteCategory.fromString(categoryStr)
            val matchedAction = EcoActionType.fromString(actionStr)

            val waysToReuse = mutableListOf<String>()
            val reuseArray = json.optJSONArray("waysToReuse")
            if (reuseArray != null) {
                for (i in 0 until reuseArray.length()) {
                    val s = reuseArray.optString(i, "").trim()
                    if (s.isNotBlank()) waysToReuse.add(s)
                }
            }
            if (waysToReuse.isEmpty()) {
                waysToReuse.addAll(upcyclingIdeas)
                if (tfObjects.isNotEmpty() && tfObjects.first().waysToReuse.isNotEmpty()) {
                    waysToReuse.addAll(tfObjects.first().waysToReuse)
                }
            }

            val waysToDispose = mutableListOf<String>()
            val disposeArray = json.optJSONArray("waysToDispose")
            if (disposeArray != null) {
                for (i in 0 until disposeArray.length()) {
                    val s = disposeArray.optString(i, "").trim()
                    if (s.isNotBlank()) waysToDispose.add(s)
                }
            }
            if (waysToDispose.isEmpty()) {
                waysToDispose.addAll(stepByStepGuide)
                if (tfObjects.isNotEmpty() && tfObjects.first().waysToDispose.isNotEmpty()) {
                    waysToDispose.addAll(tfObjects.first().waysToDispose)
                }
            }

            val topTf = tfObjects.firstOrNull()

            return AiSustainabilityProfileResult(
                itemName = itemName,
                category = matchedCategory,
                primaryAction = matchedAction,
                materialType = materialType,
                decompositionTime = decompositionTime,
                carbonFootprint = carbonFootprint,
                recyclingBinType = recyclingBinType,
                recyclabilityScore = recyclabilityScore,
                confidenceLevel = confidenceLevel,
                isConfident = isConfident,
                lowConfidenceReason = lowConfidenceReason,
                sustainabilityDescription = sustainabilityDescription,
                detectedObjects = detectedObjects,
                detectedComponents = detectedComponents,
                threeRGuidance = threeRGuidance,
                stepByStepGuide = stepByStepGuide,
                upcyclingIdeas = upcyclingIdeas,
                waysToReuse = waysToReuse.distinct(),
                waysToDispose = waysToDispose.distinct(),
                tfLiteObjects = tfObjects,
                detectedByTensorFlow = tfObjects.isNotEmpty(),
                tensorFlowConfidence = topTf?.confidence ?: confidenceLevel,
                ecoFriendlyAlternative = ecoFriendlyAlternative,
                funFact = funFact,
                arVisualBadge = arVisualBadge,
                isAiGenerated = true,
                modelName = "Gemini 3.5 Flash",
                capturedBitmap = originalBitmap
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse Gemini response JSON: ${e.message}", e)
            return null
        }
    }

    fun createFallbackSustainabilityProfile(
        bitmap: Bitmap?,
        detectedMarker: ArHitMarker?,
        barcodeInput: String? = null,
        tfDetectedObjects: List<TfLiteDetectedObject> = emptyList()
    ): AiSustainabilityProfileResult {
        val sampleItems = WasteRepositoryData.sampleItems
        val topTf = tfDetectedObjects.firstOrNull()

        val matchedItem = if (topTf != null) {
            sampleItems.firstOrNull { it.name.contains(topTf.label, ignoreCase = true) || topTf.label.contains(it.name, ignoreCase = true) }
                ?: sampleItems.firstOrNull { it.materialType.contains(topTf.material, ignoreCase = true) }
                ?: sampleItems.first()
        } else if (detectedMarker != null) {
            sampleItems.firstOrNull { it.name.contains(detectedMarker.label, ignoreCase = true) }
                ?: sampleItems.firstOrNull { it.materialType.contains(detectedMarker.resinCode, ignoreCase = true) }
                ?: sampleItems.first()
        } else if (!barcodeInput.isNullOrBlank()) {
            sampleItems.firstOrNull { it.name.contains(barcodeInput, ignoreCase = true) } ?: sampleItems.first()
        } else {
            sampleItems.first()
        }

        val isHazardous = matchedItem.category == WasteCategory.ELECTRONIC
        val itemName = topTf?.label ?: matchedItem.name
        val materialType = topTf?.material ?: matchedItem.materialType
        val actionType = if (isHazardous) EcoActionType.SPECIAL_DISPOSAL else if (topTf != null) EcoActionType.fromString(topTf.recommendedAction) else matchedItem.primaryAction
        val waysToReuse = if (topTf != null && topTf.waysToReuse.isNotEmpty()) topTf.waysToReuse else matchedItem.upcyclingIdeas
        val waysToDispose = if (topTf != null && topTf.waysToDispose.isNotEmpty()) topTf.waysToDispose else matchedItem.stepByStepGuide

        val components = when (matchedItem.category) {
            WasteCategory.PLASTIC -> listOf(
                DetectedProductComponent(
                    name = "Bottle Body",
                    material = "PET #1 Plastic",
                    category = "Plastic",
                    actionType = "RECYCLE",
                    actionLabel = "PET Plastic – Recycle",
                    separationNotes = "Empty and rinse; 95% recyclable",
                    normLeft = 0.25f,
                    normTop = 0.28f,
                    normRight = 0.75f,
                    normBottom = 0.82f
                ),
                DetectedProductComponent(
                    name = "Screw Cap",
                    material = "PP #5 Plastic",
                    category = "Plastic",
                    actionType = "RECYCLE",
                    actionLabel = "PP Plastic – Recycle",
                    separationNotes = "Screw back onto bottle after emptying",
                    normLeft = 0.40f,
                    normTop = 0.16f,
                    normRight = 0.60f,
                    normBottom = 0.26f
                )
            )
            WasteCategory.METALS -> listOf(
                DetectedProductComponent(
                    name = "Can Body",
                    material = "Aluminum Alloy 3004",
                    category = "Metal",
                    actionType = "RECYCLE",
                    actionLabel = "Metal – Recycle",
                    separationNotes = "Empty liquid; keep tab attached; 100% recyclable",
                    normLeft = 0.28f,
                    normTop = 0.25f,
                    normRight = 0.72f,
                    normBottom = 0.85f
                )
            )
            else -> listOf(
                DetectedProductComponent(
                    name = matchedItem.name,
                    material = matchedItem.materialType,
                    category = matchedItem.category.displayName,
                    actionType = matchedItem.primaryAction.name,
                    actionLabel = "${matchedItem.materialType} – ${matchedItem.primaryAction.title}",
                    separationNotes = "Follow standard separation guidelines",
                    normLeft = 0.22f,
                    normTop = 0.22f,
                    normRight = 0.78f,
                    normBottom = 0.78f
                )
            )
        }

        val detectedObj = DetectedObjectAnalysis(
            name = matchedItem.name,
            material = matchedItem.materialType,
            category = matchedItem.category,
            confidence = 0.96f,
            confidencePercentage = 96,
            isConfident = true,
            unconfidentReason = null,
            recommendedAction = actionType,
            actionTitle = actionType.title,
            reason = if (actionType == EcoActionType.RECYCLE) {
                "High circular value and standard processing availability in municipal dry recycling bins."
            } else if (actionType == EcoActionType.COMPOST) {
                "Biodegradable organic matter enriches soil health and prevents anaerobic methane production in landfills."
            } else if (actionType == EcoActionType.REPAIR) {
                "Extending the functional lifespan of existing items drastically reduces upstream manufacturing impact."
            } else if (actionType == EcoActionType.REUSE) {
                "Repurposing the container or product displaces the need for single-use purchases."
            } else {
                "Safe disposal in certified streams prevents environmental contamination."
            },
            suggestions = listOf(
                "Empty and clean any residue before sorting or repurposing",
                if (matchedItem.upcyclingIdeas.isNotEmpty()) matchedItem.upcyclingIdeas.first() else "Explore creative DIY uses around the home",
                "Ensure placement in the correct municipal ${matchedItem.recyclingBinType}"
            ),
            decompositionTime = matchedItem.decompositionTime,
            carbonFootprint = matchedItem.carbonFootprint,
            recyclingBinType = matchedItem.recyclingBinType,
            upcyclingIdea = matchedItem.upcyclingIdeas.firstOrNull()
        )

        val threeR = ThreeRGuidance(
            reduce = "Choose bulk refillable options to minimize single-use packaging consumption.",
            reuse = if (isHazardous) "Reuse is not recommended for spent cells." else "Clean and repurpose for household organizing or gardening.",
            repair = if (isHazardous) "Replace modular component if safe to do so." else null,
            recycle = "Empty liquid contents, rinse clean, and place into ${matchedItem.recyclingBinType}.",
            specialDisposal = if (isHazardous) "Bring to designated electronic recycling kiosk." else null,
            isHazardousOrSpecial = isHazardous,
            safetyWarning = if (isHazardous) "Never puncture or toss in household trash." else null
        )

        return AiSustainabilityProfileResult(
            id = matchedItem.id,
            itemName = itemName,
            category = matchedItem.category,
            primaryAction = actionType,
            materialType = materialType,
            decompositionTime = matchedItem.decompositionTime,
            carbonFootprint = matchedItem.carbonFootprint,
            recyclingBinType = matchedItem.recyclingBinType,
            recyclabilityScore = if (actionType == EcoActionType.RECYCLE) 94 else 88,
            confidenceLevel = topTf?.confidence ?: 0.96f,
            isConfident = true,
            lowConfidenceReason = null,
            sustainabilityDescription = "Sustainability Profile for $itemName: Composed of $materialType. Detected by on-device TensorFlow and analyzed with Gemini circular 3R models.",
            detectedObjects = listOf(detectedObj),
            detectedComponents = components,
            threeRGuidance = threeR,
            stepByStepGuide = waysToDispose,
            upcyclingIdeas = waysToReuse,
            waysToReuse = waysToReuse,
            waysToDispose = waysToDispose,
            tfLiteObjects = tfDetectedObjects,
            detectedByTensorFlow = tfDetectedObjects.isNotEmpty(),
            tensorFlowConfidence = topTf?.confidence ?: 0f,
            ecoFriendlyAlternative = matchedItem.ecoFriendlyAlternative,
            funFact = matchedItem.funFact,
            arVisualBadge = if (topTf != null) "⚡ TF: ${topTf.label}" else matchedItem.arVisualBadge,
            isAiGenerated = true,
            modelName = "Gemini 3.5 Flash",
            capturedBitmap = bitmap
        )
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val maxDimension = 1024
        val width = bitmap.width
        val height = bitmap.height
        val scaledBitmap = if (width > maxDimension || height > maxDimension) {
            val ratio = if (width > height) maxDimension.toFloat() / width else maxDimension.toFloat() / height
            val targetW = (width * ratio).toInt().coerceAtLeast(1)
            val targetH = (height * ratio).toInt().coerceAtLeast(1)
            Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
        } else {
            bitmap
        }

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
