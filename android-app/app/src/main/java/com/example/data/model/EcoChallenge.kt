package com.example.data.model

data class EcoChallenge(
    val id: String,
    val title: String,
    val subtitle: String,
    val durationDays: Int,
    val iconEmoji: String,
    val targetGoal: String,
    val description: String,
    val dailyCheckpoints: List<String>
)

object EcoChallengeData {
    val activeChallenges = listOf(
        EcoChallenge(
            id = "zero_plastic_week",
            title = "7-Day Zero Single-Use Plastic Challenge",
            subtitle = "Eliminate disposable bottles, bags, straws, and cutlery for one full week",
            durationDays = 7,
            iconEmoji = "🛡️",
            targetGoal = "Zero disposable single-use plastics for 7 consecutive days",
            description = "Test your eco-lifestyle commitment! Carry your reusable bottle, cutlery, and tote bags everywhere.",
            dailyCheckpoints = listOf(
                "Day 1: Audit your everyday carry kit (flask, bag, spoon)",
                "Day 2: Say 'No straw please' when dining out",
                "Day 3: Buy fresh unpackaged fruits and vegetables",
                "Day 4: Pack your lunch in a reusable stainless box",
                "Day 5: Swap liquid body wash for a solid soap bar",
                "Day 6: Refuse plastic bags during evening errands",
                "Day 7: Inspect your trash bin and celebrate zero single-use plastics!"
            )
        ),
        EcoChallenge(
            id = "water_guardian_5day",
            title = "5-Day Water Guardian Sprint",
            subtitle = "Keep all showers strictly under 4 minutes and repurpose kitchen rinse water",
            durationDays = 5,
            iconEmoji = "💧",
            targetGoal = "Save 250+ Liters of fresh water this week",
            description = "Track your water mindfulness with quick showers, single tumbler brushing, and greywater reuse for plants.",
            dailyCheckpoints = listOf(
                "Day 1: Set a 4-minute shower timer on your phone",
                "Day 2: Use a single glass tumbler while brushing teeth",
                "Day 3: Collect vegetable wash water and hydrate outdoor plants",
                "Day 4: Check faucets and toilet tanks for silent leaks",
                "Day 5: Wash your bike or balcony with bucket and sponge instead of a hose"
            )
        ),
        EcoChallenge(
            id = "energy_vampire_hunter",
            title = "Weekend Energy Vampire Sweep",
            subtitle = "Hunt down and shut off phantom vampire power draws in your home or room",
            durationDays = 2,
            iconEmoji = "⚡",
            targetGoal = "Eliminate 100% of standby idle appliances",
            description = "Identify wall transformers, microwave clocks, idle TV consoles, and chargers that silently drain electricity.",
            dailyCheckpoints = listOf(
                "Day 1: Unplug all idle chargers and switch off computer peripheral power strips",
                "Day 2: Set room AC temperature to 25°C + ceiling fan for optimal comfort and lowest draw"
            )
        )
    )
}

data class TeamMember(
    val name: String,
    val role: String,
    val school: String,
    val email: String,
    val phone: String,
    val avatarInitials: String,
    val bio: String
)

object TeamData {
    val projectTeam = listOf(
        TeamMember(
            name = "Arpit Gupta",
            role = "Lead Co-Creator & Innovator (Grade XI-C)",
            school = "Khaitan Public School, Ghaziabad",
            email = "arpit.g@khaitanpublicschool.com",
            phone = "+91 9560904994",
            avatarInitials = "AG",
            bio = "Passionate student technologist focused on blending Augmented Reality, Computer Vision, and environmental science to empower communities with actionable sustainability habits."
        ),
        TeamMember(
            name = "Avasyu Bansal",
            role = "Lead Co-Creator & Innovator (Grade XI-C)",
            school = "Khaitan Public School, Ghaziabad",
            email = "av.bansal@khaitanpublicschool.com",
            phone = "+91 9560904994",
            avatarInitials = "AB",
            bio = "Student innovator dedicated to creating accessible green UI/UX experiences and educational AR tools that turn complex environmental challenges into simple everyday choices."
        )
    )
}
