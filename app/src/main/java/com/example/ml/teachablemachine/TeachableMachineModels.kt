package com.example.ml.teachablemachine

import java.util.UUID

/**
 * An individual class prediction output by the Teachable Machine TensorFlow.js vision model.
 */
data class TeachableMachinePrediction(
    val className: String,
    val probability: Float, // 0.0 to 1.0
    val category: String = "Recyclable Material",
    val defaultAction: String = "RECYCLE",
    val iconEmoji: String = "📦"
)

/**
 * Result of a Teachable Machine visual classification pass.
 */
data class TeachableMachineClassificationResult(
    val id: String = UUID.randomUUID().toString(),
    val topPrediction: TeachableMachinePrediction,
    val allPredictions: List<TeachableMachinePrediction>,
    val isConfident: Boolean = true,
    val confidenceThreshold: Float = 0.65f,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUrl: String? = null,
    val inferenceTimeMs: Long = 45L
)

/**
 * Metadata about an active or available Teachable Machine model.
 */
data class TeachableMachineModelInfo(
    val id: String,
    val name: String,
    val description: String,
    val modelUrl: String?,
    val classes: List<String>,
    val isBuiltIn: Boolean = false
)

/**
 * Standard default Teachable Machine 3R Product Classification Classes.
 */
object TeachableMachinePresetCatalog {

    val DEFAULT_CLASSES = listOf(
        TeachableMachinePrediction(
            className = "Plastic Bottle (PET #1)",
            probability = 0.0f,
            category = "Plastics",
            defaultAction = "RECYCLE",
            iconEmoji = "🥤"
        ),
        TeachableMachinePrediction(
            className = "Aluminum Beverage Can",
            probability = 0.0f,
            category = "Metals & Cans",
            defaultAction = "RECYCLE",
            iconEmoji = "🥫"
        ),
        TeachableMachinePrediction(
            className = "Glass Bottle / Jar",
            probability = 0.0f,
            category = "Glass",
            defaultAction = "REUSE",
            iconEmoji = "🫙"
        ),
        TeachableMachinePrediction(
            className = "Cardboard Box / Paper",
            probability = 0.0f,
            category = "Paper & Cardboard",
            defaultAction = "RECYCLE",
            iconEmoji = "📦"
        ),
        TeachableMachinePrediction(
            className = "HDPE Milk / Detergent Jug",
            probability = 0.0f,
            category = "Plastics (HDPE #2)",
            defaultAction = "RECYCLE",
            iconEmoji = "🧴"
        ),
        TeachableMachinePrediction(
            className = "Battery / E-Waste",
            probability = 0.0f,
            category = "E-Waste & Batteries",
            defaultAction = "SPECIAL_DISPOSAL",
            iconEmoji = "🔋"
        ),
        TeachableMachinePrediction(
            className = "Organic Food Waste / Scraps",
            probability = 0.0f,
            category = "Organic Waste",
            defaultAction = "COMPOST",
            iconEmoji = "🍎"
        ),
        TeachableMachinePrediction(
            className = "Tetra Pak / Beverage Carton",
            probability = 0.0f,
            category = "Cartons & Composites",
            defaultAction = "RECYCLE",
            iconEmoji = "🧃"
        ),
        TeachableMachinePrediction(
            className = "Styrofoam / Polystyrene Foam",
            probability = 0.0f,
            category = "Polystyrene (PS #6)",
            defaultAction = "REDUCE",
            iconEmoji = "🥡"
        ),
        TeachableMachinePrediction(
            className = "Reusable Flask / Steel Bottle",
            probability = 0.0f,
            category = "Reusable Sustainable",
            defaultAction = "REUSE",
            iconEmoji = "🧊"
        ),
        TeachableMachinePrediction(
            className = "Textile / Clothing Fabric",
            probability = 0.0f,
            category = "Fabrics & Textiles",
            defaultAction = "REUSE",
            iconEmoji = "👕"
        )
    )

    val BUILT_IN_MODELS = listOf(
        TeachableMachineModelInfo(
            id = "tm_sustainability_3r_v1",
            name = "Teachable Machine 3R Product Classifier",
            description = "Trained with TensorFlow.js on 11 product packaging and waste streams (PET, HDPE, Aluminum, Glass, Paper, E-Waste, Organics).",
            modelUrl = "https://teachablemachine.withgoogle.com/models/v1-sustainability-waste/",
            classes = DEFAULT_CLASSES.map { it.className },
            isBuiltIn = true
        ),
        TeachableMachineModelInfo(
            id = "tm_plastics_resin_v2",
            name = "Teachable Machine Resin Code Classifier",
            description = "Specialized on polymer identification: PET #1, HDPE #2, PVC #3, LDPE #4, PP #5, PS #6, Other #7.",
            modelUrl = "https://teachablemachine.withgoogle.com/models/v2-plastics-resin/",
            classes = listOf("PET #1", "HDPE #2", "PVC #3", "LDPE #4", "PP #5", "PS #6", "Other #7"),
            isBuiltIn = false
        ),
        TeachableMachineModelInfo(
            id = "tm_ewaste_hazard_v1",
            name = "Teachable Machine E-Waste & Hazard Detector",
            description = "Specialized on batteries, circuit boards, light bulbs, cables, and hazardous household items.",
            modelUrl = "https://teachablemachine.withgoogle.com/models/v1-ewaste-hazard/",
            classes = listOf("Lithium Battery", "Alkaline Cell", "Circuit Board", "LED Bulb", "Fluorescent Tube", "Power Cord"),
            isBuiltIn = false
        )
    )
}
