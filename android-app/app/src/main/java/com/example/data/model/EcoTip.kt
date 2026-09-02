package com.example.data.model

enum class TipTimeContext(val label: String, val iconEmoji: String) {
    ALL_DAY("Daily Habit", "☀️"),
    MORNING("Morning Routine", "🌅"),
    KITCHEN("Kitchen & Food", "🍳"),
    COMMUTE("Commute & Travel", "🚲"),
    OFFICE_SCHOOL("Study & Work", "💻"),
    SHOPPING("Eco Shopping", "🛍️"),
    NIGHT("Night & Power", "🌙")
}

data class EcoTip(
    val id: String,
    val title: String,
    val description: String,
    val context: TipTimeContext,
    val impactSnippet: String,
    val category: String,
    val actionableButtonText: String
)

object EcoTipsData {
    val sampleTips = listOf(
        EcoTip(
            id = "tip_morning_tumbler",
            title = "The One-Glass Toothbrush Rule",
            description = "Leaving the tap running while brushing your teeth wastes up to 6 liters of water per minute. Fill a single cup to rinse instead!",
            context = TipTimeContext.MORNING,
            impactSnippet = "Saves ~12 Liters every single morning",
            category = "Water Conservation",
            actionableButtonText = "I Did This Today"
        ),
        EcoTip(
            id = "tip_kitchen_rice_water",
            title = "Feed Balcony Plants with Rice & Veggie Water",
            description = "Instead of dumping the cloudy water after rinsing rice, lentils, or steamed vegetables down the drain, let it cool and pour it over potted plants. It is packed with starch and minerals!",
            context = TipTimeContext.KITCHEN,
            impactSnippet = "Zero fertilizer cost + saves 5L water",
            category = "Organic Living",
            actionableButtonText = "Nourished My Plants"
        ),
        EcoTip(
            id = "tip_commute_tire_pressure",
            title = "Proper Bicycle / Car Tire Pressure",
            description = "Riding bikes or driving cars with properly inflated tires reduces rolling resistance, saving muscle energy or up to 3% in fuel efficiency.",
            context = TipTimeContext.COMMUTE,
            impactSnippet = "Smooth transit & less carbon",
            category = "Clean Commute",
            actionableButtonText = "Checked Pressure"
        ),
        EcoTip(
            id = "tip_office_screen_dark_mode",
            title = "OLED Dark Mode & Brightness Calibration",
            description = "Using dark mode on OLED screens and capping monitor brightness at 70% can reduce display energy consumption by up to 39% while easing eye strain.",
            context = TipTimeContext.OFFICE_SCHOOL,
            impactSnippet = "Extends battery & reduces grid draw",
            category = "Digital Energy",
            actionableButtonText = "Switched to Dark Mode"
        ),
        EcoTip(
            id = "tip_shopping_mesh_bags",
            title = "Ditch Thin Produce Bags at Supermarket",
            description = "Loose apples, oranges, and bananas do not need individual plastic bags. Carry washable mesh net bags or place produce directly in your basket.",
            context = TipTimeContext.SHOPPING,
            impactSnippet = "Stops 4-6 flimsy plastic bags per trip",
            category = "Zero Plastic",
            actionableButtonText = "Avoided Single-Use"
        ),
        EcoTip(
            id = "tip_night_unplug",
            title = "Unplug Phantom Chargers Before Sleep",
            description = "Chargers left plugged into wall outlets continue drawing trickle current (phantom vampire load) even when no phone is connected.",
            context = TipTimeContext.NIGHT,
            impactSnippet = "Prevents 40 kWh of ghost electricity/yr",
            category = "Energy Saving",
            actionableButtonText = "Unplugged Wall Sockets"
        )
    )
}
