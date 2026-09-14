package com.example.data.model

data class QuickEcoAction(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val description: String,
    val plasticSavedKg: Double = 0.0,
    val waterSavedLiters: Double = 0.0,
    val co2OffsetKg: Double = 0.0
)

data class ClimateQuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val category: String
)

data class EcoBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val progressLabel: String
)

data class DayActivity(
    val dayLabel: String,
    val activityCount: Int,
    val isToday: Boolean = false
)

object EcoHomeData {
    val quickActions = listOf(
        QuickEcoAction(
            id = "reusable_bottle",
            title = "Refilled Reusable Bottle",
            iconEmoji = "🚰",
            description = "Avoided a single-use plastic water bottle",
            plasticSavedKg = 0.04,
            waterSavedLiters = 3.0,
            co2OffsetKg = 0.15
        ),
        QuickEcoAction(
            id = "canvas_bag",
            title = "Used Cloth Shopping Bag",
            iconEmoji = "🛍️",
            description = "Diverted plastic grocery carrier bag",
            plasticSavedKg = 0.06,
            co2OffsetKg = 0.20
        ),
        QuickEcoAction(
            id = "green_transit",
            title = "Biked, Walked or Transit",
            iconEmoji = "🚲",
            description = "Chose low-carbon commuting over private car",
            co2OffsetKg = 1.60
        ),
        QuickEcoAction(
            id = "meatless_meal",
            title = "Plant-Based / Meatless Meal",
            iconEmoji = "🥗",
            description = "Reduced agricultural emissions and water footprint",
            waterSavedLiters = 45.0,
            co2OffsetKg = 1.20
        ),
        QuickEcoAction(
            id = "air_dry_laundry",
            title = "Air-Dried Clothes",
            iconEmoji = "🧺",
            description = "Skipped heat dryer cycle to save electrical grid load",
            co2OffsetKg = 0.85
        ),
        QuickEcoAction(
            id = "unplug_standby",
            title = "Unplugged Idle Electronics",
            iconEmoji = "🔌",
            description = "Prevented phantom electrical power drain",
            co2OffsetKg = 0.35
        ),
        QuickEcoAction(
            id = "composted_food",
            title = "Composted Kitchen Scraps",
            iconEmoji = "🍏",
            description = "Diverted organics from methane-producing landfills",
            co2OffsetKg = 0.50
        )
    )

    val sampleQuizzes = listOf(
        ClimateQuizQuestion(
            id = "quiz_plastic_decomp",
            question = "How long does a standard plastic bottle take to decompose in nature?",
            options = listOf("10–20 years", "50–100 years", "450–1,000 years", "Forever without fragmenting"),
            correctIndex = 2,
            explanation = "PET plastic bottles take approximately 450 to 1,000 years to break down, breaking into microplastics that contaminate soil and marine food chains.",
            category = "Materials & Waste"
        ),
        ClimateQuizQuestion(
            id = "quiz_food_waste",
            question = "If global food waste were a country, where would its greenhouse emissions rank?",
            options = listOf("1st in the world", "3rd behind USA and China", "10th globally", "25th globally"),
            correctIndex = 1,
            explanation = "According to the UN FAO, food loss and waste generates roughly 8-10% of global emissions, ranking 3rd after China and the USA.",
            category = "Food Systems"
        ),
        ClimateQuizQuestion(
            id = "quiz_led_savings",
            question = "How much less energy do modern LED bulbs consume compared to traditional incandescent bulbs?",
            options = listOf("Up to 25%", "Up to 50%", "Up to 80-90%", "Virtually identical"),
            correctIndex = 2,
            explanation = "LED lighting uses at least 75-90% less energy and lasts up to 25 times longer than traditional incandescent lighting.",
            category = "Clean Energy"
        ),
        ClimateQuizQuestion(
            id = "quiz_aluminum_recycle",
            question = "Recycling an aluminum can saves what percentage of energy compared to creating a new one from ore?",
            options = listOf("30%", "60%", "95%", "10%"),
            correctIndex = 2,
            explanation = "Recycling aluminum requires 95% less energy than mining and refining bauxite ore, and aluminum can be infinitely recycled without quality loss!",
            category = "Circular Economy"
        ),
        ClimateQuizQuestion(
            id = "quiz_water_dripping",
            question = "A single dripping faucet leaking 1 drop per second can waste how many liters per year?",
            options = listOf("500 liters", "1,200 liters", "Over 11,000 liters", "100 liters"),
            correctIndex = 2,
            explanation = "A tap leaking at just one drip per second can waste more than 3,000 gallons (11,350+ liters) of clean water per year.",
            category = "Water Conservation"
        )
    )
}
