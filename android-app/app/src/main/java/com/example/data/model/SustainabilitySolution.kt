package com.example.data.model

data class ActionStep(
    val title: String,
    val description: String,
    val impactRating: String, // e.g. "High Impact (-50kg CO2/yr)"
    val difficulty: String // "Easy", "Medium", "Advanced"
)

data class SustainabilitySolution(
    val id: String,
    val title: String,
    val subtitle: String,
    val categoryIcon: String,
    val problemStatement: String,
    val alarmingStat: String,
    val globalTarget: String,
    val keyActionSteps: List<ActionStep>,
    val practicalAlternatives: List<Pair<String, String>>, // Bad vs Green alternative
    val communityImpactTip: String,
    val calculatorMetric: String,
    val savingsPerUnit: Double, // e.g. liters saved per min, or kg plastic avoided
    val unitName: String
)

object SustainabilitySolutionsData {
    val solutionsList = listOf(
        SustainabilitySolution(
            id = "plastic_pollution",
            title = "Plastic Pollution",
            subtitle = "Breaking free from single-use plastics & ocean microplastics",
            categoryIcon = "🧴",
            problemStatement = "Over 300 million tons of plastic waste is produced globally every single year. Approximately 85% ends up in landfills or polluting marine ecosystems, breaking down into microplastics that enter food chains.",
            alarmingStat = "1 Million plastic bottles are purchased around the world every minute, taking 450+ years to decompose.",
            globalTarget = "UN Global Plastic Treaty target: 80% reduction in plastic pollution by 2040.",
            keyActionSteps = listOf(
                ActionStep("Carry the Green Trio", "Always keep a reusable water flask, foldable tote bag, and stainless steel cutlery set in your everyday backpack.", "Saves ~180 items/yr", "Easy"),
                ActionStep("Audit Bathroom Plastics", "Switch shampoo, conditioner, and body wash bottles to solid concentrated bar soaps and bamboo toothbrushes.", "Saves 12-15 bottles/yr", "Easy"),
                ActionStep("Join Bulk Buying & Zero-Packaging Markets", "Purchase grains, nuts, and household liquids from refill stations using your own jars.", "Saves 60kg packaging/yr", "Medium"),
                ActionStep("Support Extended Producer Responsibility (EPR)", "Buy from brands that offer take-back schemes and 100% post-consumer recycled (PCR) packaging.", "Systemic Shift", "Medium")
            ),
            practicalAlternatives = listOf(
                "Single-Use Plastic Bag" to "Organic Cotton Canvas Tote or Net Bag",
                "Plastic Bottled Water" to "Insulated Stainless Steel Vacuum Flask",
                "Plastic Cling Wrap" to "Natural Beeswax Wraps or Silicone Stretch Lids",
                "Disposable Plastic Cutlery" to "Pocket Bamboo / Metal Travel Cutlery",
                "Plastic Straws" to "Strawless Drinking or Stainless Steel Straw"
            ),
            communityImpactTip = "Organize a weekend neighborhood clean-up or initiate an eco-brick plastic packing drive at school/work!",
            calculatorMetric = "Single-Use Plastic Items Avoided per Day",
            savingsPerUnit = 0.04, // 0.04 kg plastic saved per item avoided
            unitName = "items avoided/day"
        ),
        SustainabilitySolution(
            id = "water_scarcity",
            title = "Water Scarcity & Conservation",
            subtitle = "Safeguarding fresh water reserves & preventing urban drought",
            categoryIcon = "💧",
            problemStatement = "Freshwater makes up less than 3% of the world's water supply, and two-thirds of it is tucked away in frozen glaciers. Over 2 billion people currently live in water-stressed countries.",
            alarmingStat = "A single 10-minute shower consumes up to 100 liters of potable water, and a leaky faucet can waste 3,000+ gallons per year.",
            globalTarget = "SDG 6: Ensure availability and sustainable management of water and sanitation for all by 2030.",
            keyActionSteps = listOf(
                ActionStep("Install Low-Flow Tap Aerators", "Screw inexpensive aerators onto sink faucets and showerheads to cut water flow by 50% without dropping pressure.", "Saves ~4,000L/yr", "Easy"),
                ActionStep("Smart 4-Minute Showers", "Keep shower routines under 4 minutes and turn off water while soaping or shampooing.", "Saves ~50L/day", "Easy"),
                ActionStep("Rooftop Rainwater Catchment", "Channel rainfall from gutters into barrels for watering plants, washing balconies, and garden irrigation.", "Saves ~20,000L/yr", "Medium"),
                ActionStep("Kitchen Greywater Diverter", "Collect water used for washing vegetables and rice to nourish balcony plants.", "Saves ~10L/day", "Easy")
            ),
            practicalAlternatives = listOf(
                "Running Tap While Brushing Teeth" to "Using a Single Rinse Tumbler (Saves 6L/min)",
                "Full-Blast Hose Pipe Car Wash" to "Bucket & Microfiber Cloth System",
                "Sprinklers in Midday Sun" to "Drip Irrigation at Dawn/Dusk to minimize evaporation",
                "Throwing Away Rice Wash Water" to "Reusing nutrient-rich rice rinse for garden plants"
            ),
            communityImpactTip = "Report leaking public municipal pipes through city apps and advocate for permeable pavements in residential colonies.",
            calculatorMetric = "Minutes Saved in Daily Shower Time",
            savingsPerUnit = 9.5, // Liters per minute
            unitName = "minutes reduced"
        ),
        SustainabilitySolution(
            id = "deforestation",
            title = "Deforestation & Flora Protection",
            subtitle = "Restoring native biodiversity and planting carbon-sink urban forests",
            categoryIcon = "🌳",
            problemStatement = "Forests cover 31% of the land area on our planet. They produce vital oxygen and provide homes for people and wildlife. Every year, 10 million hectares of forest are lost due to agricultural expansion and logging.",
            alarmingStat = "15 Billion trees are chopped down every year worldwide. Forest loss accounts for nearly 10% of all global greenhouse emissions.",
            globalTarget = "Bonn Challenge: Restore 350 million hectares of degraded and deforested landscapes by 2030.",
            keyActionSteps = listOf(
                ActionStep("Switch to 100% Tree-Free / FSC Paper", "Use certified recycled paper or bamboo/bagasse (sugarcane fiber) paper products.", "Protects mature trees", "Easy"),
                ActionStep("Digital First Routine", "Opt out of paper bills, receipts, flyers, and physical bank statements in favor of cloud archiving.", "Saves 10kg paper/yr", "Easy"),
                ActionStep("Adopt Native Trees & Seed Balls", "Plant indigenous drought-resistant species (Neem, Peepal, Banyan, Oak) in community open spaces.", "Offsets 22kg CO2/tree/yr", "Medium"),
                ActionStep("Support Regenerative Agroforestry", "Purchase shade-grown, Fairtrade, and rainforest-alliance certified coffee, tea, and cocoa.", "Combats clear-cutting", "Medium")
            ),
            practicalAlternatives = listOf(
                "Virgin Wood Pulp Paper Towels" to "Washable Microfiber / Organic Swedish Dishcloths",
                "Physical Notebooks for Scratch Notes" to "Digital Stylus Tablets or Reusable Wipeable Notebooks",
                "Exotic High-Water Ornamental Plants" to "Native Pollinator-Friendly Indigenous Flora",
                "Conventional Timber Furniture" to "Reclaimed Wood or Fast-Growing Moso Bamboo"
            ),
            communityImpactTip = "Create seed balls with clay and compost with children and distribute them in open green patches before the monsoon season!",
            calculatorMetric = "Sheets of Paper Saved per Day (Digital & Double-Sided)",
            savingsPerUnit = 0.05, // kg of wood / carbon equivalent
            unitName = "sheets saved/day"
        ),
        SustainabilitySolution(
            id = "energy_transition",
            title = "Clean Energy & Carbon Reduction",
            subtitle = "Decarbonizing daily power usage and eliminating phantom drain",
            categoryIcon = "⚡",
            problemStatement = "Fossil-fuel generated electricity and heat contribute to over 70% of total global greenhouse gas emissions. Transitioning to renewable decentralized solar and eliminating energy waste is essential.",
            alarmingStat = "Standby 'phantom power' consumed by plugged-in electronics accounts for 5% to 10% of residential electric consumption.",
            globalTarget = "Tripling global renewable energy capacity to 11,000 GW by 2030 (COP28 agreement).",
            keyActionSteps = listOf(
                ActionStep("Smart Power Strips for Vampire Draw", "Plug TVs, consoles, and computer desks into master switches to eliminate standby load at night.", "Cuts 100kWh/yr", "Easy"),
                ActionStep("Smart AC Temperature Calibration", "Set air conditioners to 24°C-26°C with ceiling fans; every 1°C higher saves 6% cooling energy.", "Cuts 250kWh/yr", "Easy"),
                ActionStep("Rooftop Distributed Solar Grid", "Install rooftop solar photovoltaic panels to generate clean self-sufficient solar watts.", "Cuts 1.5 tons CO2/yr", "Advanced"),
                ActionStep("Induction Cooktops over LPG", "Shift cooking appliances from fossil gas to high-efficiency induction magnetic cooktops.", "Zero indoor fumes", "Medium")
            ),
            practicalAlternatives = listOf(
                "Leaving Chargers Plugged Overnight" to "Using Smart Timer Plugs",
                "Incandescent Halogen Bulbs" to "Energy-Star Certified LED Bulbs",
                "Private Car Solo Commute" to "Public Metro, Carpooling, or Electric Bicycles",
                "Tumble Dryer for Clothes" to "Natural Solar Air-Drying Clothesline"
            ),
            communityImpactTip = "Advocate for solar street lighting and LED retrofit programs in your housing society or school campus!",
            calculatorMetric = "Hours of AC / High-Draw Appliances Reduced per Day",
            savingsPerUnit = 1.2, // kWh saved per hour
            unitName = "hours reduced/day"
        ),
        SustainabilitySolution(
            id = "food_waste_soil",
            title = "Zero Food Waste & Soil Regeneration",
            subtitle = "Turning kitchen scraps into nutrient-rich organic black gold",
            categoryIcon = "🥕",
            problemStatement = "Roughly one-third of all food produced globally for human consumption is lost or wasted (~1.3 billion tons/yr). If food waste were a country, it would be the third-largest emitter of greenhouse gases.",
            alarmingStat = "Food rotting anaerobically in landfills produces methane gas, which is 28 to 36 times more potent than carbon dioxide over a 100-year cycle.",
            globalTarget = "SDG 12.3: Halve per capita global food waste at retail and consumer levels by 2030.",
            keyActionSteps = listOf(
                ActionStep("Smart Meal Planning & 'First-In, First-Out'", "Organize refrigerator shelves so older groceries are visible and cooked first.", "Cuts waste by 70%", "Easy"),
                ActionStep("Start Bokashi or Aerobic Composting", "Compost fruit peels, coffee grounds, and tea leaves to create organic fertilizer for soil.", "Cuts 150kg waste/yr", "Easy"),
                ActionStep("Vegetable Scrap Stock & Regrowing", "Freeze clean onion skins, carrot ends, and celery stalks to simmer aromatic soup broths.", "Free nutritious food", "Easy"),
                ActionStep("Eat Lower on the Food Chain", "Incorporate plant-forward pulses, lentils, and seasonal greens into weekly meal rotations.", "Cuts food footprint 40%", "Medium")
            ),
            practicalAlternatives = listOf(
                "Throwing Out Spotty Brown Bananas" to "Baking Delicious Banana Bread or Freezing for Smoothies",
                "Over-Buying Perishable Groceries" to "Weekly Meal Plan Shopping Checklist",
                "Discarding Coffee Grounds in Trash" to "Adding directly to Soil as Nitrogen-Rich Acidic Plant Food",
                "Single-Use Plastic Clingfilm" to "Silicone Food Huggers and Airtight Pyrex Glass Bowls"
            ),
            communityImpactTip = "Start a school or apartment community compost tumblers where residents can drop off raw kitchen scraps for shared gardens!",
            calculatorMetric = "Grams of Kitchen Food Scraps Composted per Day",
            savingsPerUnit = 0.0019, // kg CO2e methane prevented per gram
            unitName = "grams composted/day"
        )
    )
}
