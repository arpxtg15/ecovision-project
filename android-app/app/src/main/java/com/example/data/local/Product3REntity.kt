package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_3r_catalog")
data class Product3REntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val brandOrType: String,
    val threeRClassification: String, // "REDUCE", "REUSE", "RECYCLE", "COMPOST", "SPECIAL"
    val category: String, // "PLASTIC", "GLASS", "METAL", "PAPER", "ORGANIC", "ELECTRONIC", "COMPOSITE", "FABRIC_WOOD"
    val categoryIcon: String,
    val resinCodeOrStandard: String,
    val dimensionsMm: String,
    val densityGcm3: Float,
    val decompositionTimeline: String,
    val carbonFootprintGrams: String,
    val recyclabilityScore: Int, // 0 - 100
    val recommendedBinOrStream: String,
    val howToReduce: String,
    val howToReuseUpcycle: String,
    val howToRecycle: String,
    val arTargetLabel: String,
    val arVisualBadge: String,
    val ecoSwapAlternative: String,
    val funFact: String
)
