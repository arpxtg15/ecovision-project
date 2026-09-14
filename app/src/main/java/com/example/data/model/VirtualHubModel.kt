package com.example.data.model

data class HubModelSpecs(
    val dimensions: String,
    val idealPlacement: String,
    val estimatedAnnualSavings: String,
    val carbonOffsetPerYear: String,
    val maintenanceLevel: String,
    val keyBenefits: List<String>,
    val installationTips: List<String>
)

data class VirtualHubModel(
    val id: String,
    val title: String,
    val category: String,
    val iconEmoji: String,
    val shortDescription: String,
    val realisticScale: Float, // default scale multiplier
    val modelColorHex: Long,
    val secondaryColorHex: Long,
    val specs: HubModelSpecs,
    val interactiveFeatures: List<String>
)

object VirtualHubData {
    val models = listOf(
        VirtualHubModel(
            id = "solar_panel_array",
            title = "Rooftop & Balcony Solar Array",
            category = "Clean Energy",
            iconEmoji = "☀️",
            shortDescription = "High-efficiency monocrystalline solar photovoltaic panel array with micro-inverter.",
            realisticScale = 1.0f,
            modelColorHex = 0xFF1565C0,
            secondaryColorHex = 0xFFFFB300,
            specs = HubModelSpecs(
                dimensions = "1.7m x 1.0m (Standard 400W Panel)",
                idealPlacement = "South or South-West facing rooftop, terrace, or sunlit balcony railing (tilt 25°-30°)",
                estimatedAnnualSavings = "$180 - $260 / yr on electric bills (approx. 550 kWh/panel)",
                carbonOffsetPerYear = "390 kg CO₂e offset / panel / year (equivalent to planting 18 trees)",
                maintenanceLevel = "Low (Rinse dust with water once a month)",
                keyBenefits = listOf(
                    "Generates clean silent electricity from everyday sunlight",
                    "Reduces reliance on coal and gas-powered electric grids",
                    "Increases real estate value and long-term energy independence",
                    "25-year performance warranty with minimal moving parts"
                ),
                installationTips = listOf(
                    "Check shadow clearance from neighboring trees or chimneys between 9 AM and 4 PM",
                    "Ensure secure mounting brackets capable of withstanding local wind speeds",
                    "Connect through a certified bi-directional net meter"
                )
            ),
            interactiveFeatures = listOf(
                "Simulate daily sun-angle trajectory and peak power output (Watts)",
                "Adjust panel tilt from 15° to 45° to optimize solar absorption",
                "Calculate total household grid offset with multi-panel array"
            )
        ),
        VirtualHubModel(
            id = "indoor_composter",
            title = "Smart Odorless Kitchen Composter",
            category = "Soil & Waste",
            iconEmoji = "🍂",
            shortDescription = "Aerobic bokashi fermentation composter with activated carbon filter for odorless indoor or balcony use.",
            realisticScale = 0.8f,
            modelColorHex = 0xFF4E342E,
            secondaryColorHex = 0xFF66BB6A,
            specs = HubModelSpecs(
                dimensions = "38cm x 28cm x 45cm (20L Capacity)",
                idealPlacement = "Under kitchen sink, balcony corner, pantry countertop, or utility area",
                estimatedAnnualSavings = "Diverts 180kg of organic waste from municipal landfills / year",
                carbonOffsetPerYear = "115 kg CO₂e methane emissions prevented / year",
                maintenanceLevel = "Low (Add bran/inoculant with daily scraps, empty every 3-4 weeks)",
                keyBenefits = listOf(
                    "100% odorless decomposition with double-sealed lid & carbon filter",
                    "Produces nutrient-rich liquid compost tea for indoor house plants",
                    "Creates pre-compost humus that restores soil microbiome",
                    "Eliminates kitchen garbage bin odors and messy plastic trash bags"
                ),
                installationTips = listOf(
                    "Keep out of direct blazing sunlight to maintain optimal microbial temperature (18°C-28°C)",
                    "Chop food waste into 1-2 inch chunks for faster microbial breakdown",
                    "Drain compost liquid tea weekly using the bottom spigot"
                )
            ),
            interactiveFeatures = listOf(
                "Toggle breakdown animation from food scraps to rich dark compost",
                "Microbiome temperature & moisture health gauge simulator",
                "Nutrient liquid compost tea tap simulation"
            )
        ),
        VirtualHubModel(
            id = "urban_micro_garden",
            title = "Air-Purifying Urban Micro-Forest",
            category = "Flora & Biodiversity",
            iconEmoji = "🌿",
            shortDescription = "Modular self-watering terracotta vertical planter tower with NASA-approved air-purifying plants.",
            realisticScale = 0.9f,
            modelColorHex = 0xFF2E7D32,
            secondaryColorHex = 0xFF81C784,
            specs = HubModelSpecs(
                dimensions = "50cm diameter x 140cm height (Vertical 6-Tier Tower)",
                idealPlacement = "Living room corner near window, balcony, study desk, or classroom",
                estimatedAnnualSavings = "Filters indoor VOCs, removes 87% of toxins in 24 hours, provides fresh herbs",
                carbonOffsetPerYear = "Absorbs 45 kg CO₂ and generates 120 liters of fresh oxygen / day",
                maintenanceLevel = "Easy (Sub-irrigation wicking reservoir requires filling only once every 14 days)",
                keyBenefits = listOf(
                    "Includes Snake Plant, Spider Plant, Peace Lily, and Basil/Mint herbs",
                    "Natural biophilic stress reducer and concentration booster",
                    "Passively regulates indoor air humidity and cools ambient room temperature by 1-2°C",
                    "Zero soil mess with leca clay pebbles and coconut coir medium"
                ),
                installationTips = listOf(
                    "Place in bright indirect sunlight for lush foliage growth",
                    "Prune outer leaves occasionally to encourage fresh central sprouts",
                    "Wipe broad leaves with a damp cloth every month to maximize photosynthesis"
                )
            ),
            interactiveFeatures = listOf(
                "Simulate indoor Air Quality Index (AQI) drop & oxygen release",
                "Visualize sub-surface wicking water level reservoir",
                "Switch plant varieties (Herbs, Succulents, Air Purifiers)"
            )
        ),
        VirtualHubModel(
            id = "rainwater_harvesting_barrel",
            title = "Eco Rainwater Harvesting Collector",
            category = "Water Conservation",
            iconEmoji = "🌧️",
            shortDescription = "Compact UV-stabilized 200L rain barrel with leaf diverter filter, brass spigot, and overflow hose.",
            realisticScale = 1.1f,
            modelColorHex = 0xFF00695C,
            secondaryColorHex = 0xFF4DD0E1,
            specs = HubModelSpecs(
                dimensions = "60cm diameter x 95cm height (200 Liter / 53 Gallon)",
                idealPlacement = "Directly beneath outdoor roof gutter downspout in garden, courtyard, or terrace",
                estimatedAnnualSavings = "Collects up to 5,000 Liters of soft, chlorine-free rainwater per monsoon season",
                carbonOffsetPerYear = "Saves 35 kWh of municipal water pumping and purification energy",
                maintenanceLevel = "Low (Clean inlet debris mesh screen before the rainy season)",
                keyBenefits = listOf(
                    "Provides soft naturally acidic rainwater ideal for gardens and car washing",
                    "Reduces stormwater runoff and mitigates urban localized street flooding",
                    "Free emergency backup water supply for non-potable household uses",
                    "Child-safe sealed lid and fine mesh prevents mosquito breeding"
                ),
                installationTips = listOf(
                    "Place on level concrete pavers or cinder blocks to elevate spigot for bucket clearance",
                    "Connect overflow pipe to direct excess water to garden swales or soak-pits",
                    "Disconnect or drain before freezing winter months in sub-zero climates"
                )
            ),
            interactiveFeatures = listOf(
                "Rainfall simulator with real-time water fill level gauge",
                "Calculate water harvest based on local roof square-footage",
                "Simulated hose connection for garden irrigation"
            )
        ),
        VirtualHubModel(
            id = "smart_eco_aerator",
            title = "Smart Faucet Micro-Aerator & Sensor",
            category = "Water Conservation",
            iconEmoji = "🚰",
            shortDescription = "Dual-spray laminar flow water-saving aerator with touchless infrared proximity sensor.",
            realisticScale = 0.6f,
            modelColorHex = 0xFF0288D1,
            secondaryColorHex = 0xFFB3E5FC,
            specs = HubModelSpecs(
                dimensions = "Universal M24 / M22 thread (4.5cm length)",
                idealPlacement = "Kitchen sink, bathroom vanity, and utility washbasins",
                estimatedAnnualSavings = "Saves 12,000 Liters of water per sink / year (50% reduction)",
                carbonOffsetPerYear = "Saves 85 kWh of water heating gas/electricity per year",
                maintenanceLevel = "Zero (Self-cleaning silicone anti-limescale nozzles)",
                keyBenefits = listOf(
                    "Infuses air bubbles into water stream to maintain rich rinsing pressure with half the water",
                    "Touchless auto-shutoff stops wasteful running water while brushing or soaping hands",
                    "Swivel 360-degree head reaches every corner of the sink basin",
                    "DIY installation in under 60 seconds without tools"
                ),
                installationTips = listOf(
                    "Unscrew old factory aerator counter-clockwise using hand grip",
                    "Ensure rubber washer is seated properly to prevent dripping",
                    "Toggle between soft aerated bubble stream and strong rain rinse mode"
                )
            ),
            interactiveFeatures = listOf(
                "Toggle water stream simulation (Traditional 10 L/min vs Eco 4.5 L/min)",
                "Interactive hands-free proximity sensor trigger",
                "Real-time water savings meter"
            )
        )
    )
}
