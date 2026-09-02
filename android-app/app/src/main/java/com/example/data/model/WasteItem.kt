package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.OceanTeal
import com.example.ui.theme.SunGold

enum class EcoActionType(val title: String, val badgeColorHex: Long) {
    RECYCLE("Recycle", 0xFF2E7D32),
    REUSE("Reuse", 0xFF0288D1),
    REPAIR("Repair", 0xFF7B1FA2),
    DONATE("Donate", 0xFFE65100),
    COMPOST("Compost", 0xFF558B2F),
    DISPOSE("Dispose", 0xFF757575),
    REDUCE("Reduce", 0xFFFF8F00),
    REPLACE("Replace", 0xFF6A1B9A),
    SPECIAL_DISPOSAL("Special Disposal", 0xFFC62828);

    companion object {
        fun fromString(action: String): EcoActionType {
            val upper = action.trim().uppercase()
            return when {
                upper.contains("REPAIR") -> REPAIR
                upper.contains("DONATE") -> DONATE
                upper.contains("REUSE") -> REUSE
                upper.contains("COMPOST") -> COMPOST
                upper.contains("DISPOSE") || upper.contains("LANDFILL") || upper.contains("TRASH") -> DISPOSE
                upper.contains("REDUCE") -> REDUCE
                upper.contains("REPLACE") -> REPLACE
                upper.contains("SPECIAL") || upper.contains("HAZARD") || upper.contains("BATTERY") || upper.contains("E-WASTE") -> SPECIAL_DISPOSAL
                else -> RECYCLE
            }
        }
    }
}

enum class WasteCategory(val displayName: String) {
    PLASTIC("Plastics"),
    PAPER_CARDBOARD("Paper & Cardboard"),
    GLASS("Glass"),
    METALS("Metals & Cans"),
    ORGANIC("Organic Waste"),
    ELECTRONIC("E-Waste & Batteries"),
    TEXTILES("Textiles & Clothing"),
    COMPOSITE("Cartons & Composites"),
    OTHER("Other / Mixed");

    companion object {
        fun fromString(cat: String): WasteCategory {
            val upper = cat.trim().uppercase()
            return when {
                upper.contains("PLASTIC") -> PLASTIC
                upper.contains("PAPER") || upper.contains("CARDBOARD") -> PAPER_CARDBOARD
                upper.contains("GLASS") || upper.contains("CERAMIC") -> GLASS
                upper.contains("METAL") || upper.contains("CAN") || upper.contains("ALUMINUM") || upper.contains("STEEL") -> METALS
                upper.contains("ORGANIC") || upper.contains("FOOD") || upper.contains("COMPOST") -> ORGANIC
                upper.contains("ELECTRONIC") || upper.contains("BATTERY") || upper.contains("E-WASTE") -> ELECTRONIC
                upper.contains("TEXTILE") || upper.contains("CLOTH") || upper.contains("FABRIC") || upper.contains("COTTON") -> TEXTILES
                upper.contains("COMPOSITE") || upper.contains("TETRA") || upper.contains("CARTON") -> COMPOSITE
                else -> OTHER
            }
        }
    }
}

data class WasteItem(
    val id: String,
    val name: String,
    val category: WasteCategory,
    val primaryAction: EcoActionType,
    val materialType: String,
    val decompositionTime: String,
    val carbonFootprint: String,
    val recyclingBinType: String,
    val stepByStepGuide: List<String>,
    val upcyclingIdeas: List<String>,
    val ecoFriendlyAlternative: String,
    val funFact: String,
    val arVisualBadge: String,
    val sustainabilityProfileDescription: String? = null,
    val recyclabilityScore: Int = 90,
    val isAiAnalyzed: Boolean = false,
    val aiModelUsed: String? = null
)

object WasteRepositoryData {
    val sampleItems = listOf(
        WasteItem(
            id = "plastic_bottle",
            name = "Plastic Water Bottle (PET #1)",
            category = WasteCategory.PLASTIC,
            primaryAction = EcoActionType.RECYCLE,
            materialType = "Polyethylene Terephthalate (PETE #1)",
            decompositionTime = "450 Years",
            carbonFootprint = "82.8g CO₂e per 500ml bottle",
            recyclingBinType = "Blue Bin (Plastics & Dry Recyclables)",
            stepByStepGuide = listOf(
                "1. Empty remaining liquids completely",
                "2. Rinse with greywater if sticky or dirty",
                "3. Remove plastic sleeve/label if separate",
                "4. Crush bottle flat to save 75% bin volume",
                "5. Screw cap back on (caps are now recycled together)"
            ),
            upcyclingIdeas = listOf(
                "Self-watering indoor planter for herbs",
                "Bird feeder for balcony or garden",
                "Drip irrigation cone for garden potted plants"
            ),
            ecoFriendlyAlternative = "Switch to a food-grade Stainless Steel or Borosilicate Glass reusable flask to save ~167 plastic bottles/year.",
            funFact = "Recycling 1 plastic bottle saves enough electrical energy to power a 60W lightbulb for 6 hours!",
            arVisualBadge = "♻️ PET #1 High Recyclability"
        ),
        WasteItem(
            id = "snack_wrapper",
            name = "Multi-Layer Snack / Chip Wrapper",
            category = WasteCategory.COMPOSITE,
            primaryAction = EcoActionType.REDUCE,
            materialType = "Metallized Plastic Polymer (BOPP/Aluminum)",
            decompositionTime = "100 - 300 Years",
            carbonFootprint = "45g CO₂e per packet",
            recyclingBinType = "Special Flexible Plastics Drop-off / Eco-Brick",
            stepByStepGuide = listOf(
                "1. Wipe clean of food oils and crumbs",
                "2. Check if local supermarket has flexible film recycling bins",
                "3. If unavailable locally, pack tightly into an Eco-Brick plastic bottle for community building",
                "4. Do NOT toss in standard paper/metal curbside bins"
            ),
            upcyclingIdeas = listOf(
                "Pack tightly inside empty bottles to make insulated Eco-Bricks",
                "Use metallized side for heat-reflective outdoor crafts",
                "Upcycled woven waterproof decorative mats"
            ),
            ecoFriendlyAlternative = "Buy bulk trail mix in reusable cloth bags or bake homemade snacks in silicone bags.",
            funFact = "Multi-layer plastics contain up to 7 bonded micro-layers, making traditional municipal thermal sorting challenging.",
            arVisualBadge = "⚠️ Multi-Layer Foil Film"
        ),
        WasteItem(
            id = "tetra_pak",
            name = "Tetra Pak Juice / Milk Carton",
            category = WasteCategory.COMPOSITE,
            primaryAction = EcoActionType.RECYCLE,
            materialType = "75% Paperboard, 20% Polyethylene, 5% Aluminum",
            decompositionTime = "5 Years (Cardboard) / 200 Years (Foil Layer)",
            carbonFootprint = "32g CO₂e per 250ml carton",
            recyclingBinType = "Dedicated Carton Recycling / Dry Waste",
            stepByStepGuide = listOf(
                "1. Push the plastic straw inside or remove screw cap",
                "2. Unfold top flaps and flatten completely",
                "3. Rinse out milk/juice residue to avoid bacterial growth",
                "4. Place in aseptic carton recycling stream for fiber pulping"
            ),
            upcyclingIdeas = listOf(
                "Waterproof seedling starter pots for kitchen garden",
                "Desk drawer stationery organizing trays",
                "Child safe waterproof building blocks"
            ),
            ecoFriendlyAlternative = "Buy milk/juice in refillable returnable glass bottles or make fresh squeezed juices.",
            funFact = "Recycled aseptic cartons are hydrapulped into high-grade paper and poly-aluminum roofing sheets for low-cost eco housing.",
            arVisualBadge = "📦 Aseptic Composite Carton"
        ),
        WasteItem(
            id = "cfl_tube_light",
            name = "CFL Bulb & Fluorescent Tube",
            category = WasteCategory.ELECTRONIC,
            primaryAction = EcoActionType.SPECIAL_DISPOSAL,
            materialType = "Phosphor Coated Glass with Mercury Vapor",
            decompositionTime = "Indefinite (Glass) / Mercury toxicity permanent",
            carbonFootprint = "High environmental hazard if sent to landfill",
            recyclingBinType = "E-Waste / Hazardous Drop-Off Depot",
            stepByStepGuide = listOf(
                "1. Handle with extreme care — do not crush or break",
                "2. If cracked, ventilate room for 15 minutes immediately",
                "3. Wrap carefully in newspaper or original cardboard sleeve",
                "4. Hand over directly to certified E-Waste takeback program"
            ),
            upcyclingIdeas = listOf(
                "Never upcycle mercury-containing bulbs due to health risks; always direct to certified recyclers."
            ),
            ecoFriendlyAlternative = "Upgrade to Solid-State Energy Star LED bulbs which use 85% less electricity and contain 0% mercury.",
            funFact = "Over 95% of the glass, metal pins, and mercury in CFLs can be safely extracted and purified for industrial reuse.",
            arVisualBadge = "☣️ Hazardous Mercury Safe-Handling"
        ),
        WasteItem(
            id = "glass_jar",
            name = "Glass Jar & Food Container",
            category = WasteCategory.GLASS,
            primaryAction = EcoActionType.REUSE,
            materialType = "100% Infinitely Recyclable Silica Glass",
            decompositionTime = "1 Million+ Years (Inert)",
            carbonFootprint = "110g CO₂e per 500g container",
            recyclingBinType = "Green/Clear Glass Recycling Stream",
            stepByStepGuide = listOf(
                "1. Soak jar in warm soapy water to peel off adhesive label",
                "2. Rinse thoroughly and let air dry",
                "3. Separate metal lid (recycle lid with scrap metals)",
                "4. Reuse at home for pantry storage or place in glass container bin"
            ),
            upcyclingIdeas = listOf(
                "Aesthetic pantry spice & pulse bulk storage container",
                "Fairy light lantern or aromatic DIY candle holder",
                "Fermentation vessel for sourdough starter or kimchi"
            ),
            ecoFriendlyAlternative = "Glass is already one of the cleanest packaging materials! Maximize lifetime by reusing 10+ times.",
            funFact = "Glass can be melted down and re-molded infinite times without any degradation in purity, transparency, or quality!",
            arVisualBadge = "✨ 100% Infinite Recyclability"
        ),
        WasteItem(
            id = "aluminum_can",
            name = "Aluminum Beverage Can",
            category = WasteCategory.METALS,
            primaryAction = EcoActionType.RECYCLE,
            materialType = "Pure Aluminum Alloy",
            decompositionTime = "80 - 200 Years",
            carbonFootprint = "170g CO₂e (Virgin) vs 8g CO₂e (Recycled)",
            recyclingBinType = "Metals Recycling Bin",
            stepByStepGuide = listOf(
                "1. Pour out all soda/juice contents",
                "2. Quick rinse with water",
                "3. Keep pull-tab attached to the can body",
                "4. Lightly crush vertically and drop in metal bin"
            ),
            upcyclingIdeas = listOf(
                "Balcony vertical hanging succulent planters",
                "Desk pen holder with custom decoupage artwork",
                "Campfire mini survival stove"
            ),
            ecoFriendlyAlternative = "Opt for loose leaf drinks brewed in thermal containers instead of single-serving canned beverages.",
            funFact = "An aluminum can can be recycled, melted, rolled, filled, and back on a store shelf in as little as 60 days!",
            arVisualBadge = "🥫 Closed-Loop Circular Metal"
        ),
        WasteItem(
            id = "coffee_cup",
            name = "Disposable Takeaway Coffee Cup",
            category = WasteCategory.COMPOSITE,
            primaryAction = EcoActionType.REPLACE,
            materialType = "Paperboard lined with Polyethylene (PE) coating",
            decompositionTime = "20 - 30 Years",
            carbonFootprint = "60g CO₂e per 350ml cup",
            recyclingBinType = "Specialized Cup Recycling or General Landfill",
            stepByStepGuide = listOf(
                "1. Remove plastic lid and recycle if #5 PP plastic",
                "2. Remove cardboard heat sleeve and place in Paper bin",
                "3. Because of the inner plastic liner, standard paper mills cannot pulp these cups",
                "4. Check if coffee shop has dedicated cup collection bin"
            ),
            upcyclingIdeas = listOf(
                "Indoor seedling nursery cups (poke drainage hole)",
                "Paint and craft mixing vessels"
            ),
            ecoFriendlyAlternative = "Bring your own silicone or stainless steel KeepCup to cafes. Many cafes offer discounts for BYO mugs!",
            funFact = "Over 16 billion single-use disposable coffee cups are used annually worldwide, with less than 1% currently recycled.",
            arVisualBadge = "☕ Lined Paper Composite"
        ),
        WasteItem(
            id = "cardboard_box",
            name = "Corrugated Shipping Cardboard Box",
            category = WasteCategory.PAPER_CARDBOARD,
            primaryAction = EcoActionType.RECYCLE,
            materialType = "100% Unbleached Kraft Paper Fiber",
            decompositionTime = "2 - 3 Months",
            carbonFootprint = "40g CO₂e per box",
            recyclingBinType = "Blue/Yellow Paper & Cardboard Bin",
            stepByStepGuide = listOf(
                "1. Peel off plastic packing tape and shipping label stickers",
                "2. Flatten box completely by cutting bottom and top seams",
                "3. Keep dry (wet cardboard clogs pulping machinery)",
                "4. Stack neatly in the paper recycling bin"
            ),
            upcyclingIdeas = listOf(
                "Sheet mulching and weed barrier for garden beds",
                "Biodegradable brown carbon layer for home compost bin",
                "Custom drawer dividers and closet organizers"
            ),
            ecoFriendlyAlternative = "Select grouped shipping and minimalist eco-packaging options at checkout.",
            funFact = "Recycling 1 ton of cardboard saves 17 mature trees, 7,000 gallons of clean water, and 4,000 kW of electricity.",
            arVisualBadge = "📦 High Grade Kraft Fiber"
        ),
        WasteItem(
            id = "organic_scraps",
            name = "Kitchen Fruit & Vegetable Scraps",
            category = WasteCategory.ORGANIC,
            primaryAction = EcoActionType.COMPOST,
            materialType = "100% Biodegradable Nitrogen/Carbon Biomass",
            decompositionTime = "2 - 6 Weeks (in composter)",
            carbonFootprint = "Landfilling generates Methane (25x worse than CO₂)",
            recyclingBinType = "Green Wet Organic Waste / Home Compost",
            stepByStepGuide = listOf(
                "1. Collect in an aerated countertop compost caddy",
                "2. Balance 1 part green wet scraps (nitrogen) with 2 parts dry leaves/cardboard (carbon)",
                "3. Aerate or turn compost weekly",
                "4. Harvest rich dark 'black gold' humus for house plants"
            ),
            upcyclingIdeas = listOf(
                "Boil vegetable peelings with water to make rich organic broth",
                "Regrow scallions, celery, and leeks in shallow water saucers",
                "Citrus peel vinegar all-purpose natural surface cleaner"
            ),
            ecoFriendlyAlternative = "Adopt meal planning, freeze excess produce, and regrow kitchen scraps.",
            funFact = "Composting food waste prevents harmful anaerobic methane emissions in landfills while replenishing essential soil microbes.",
            arVisualBadge = "🌱 100% Organic Soil Nutrient"
        ),
        WasteItem(
            id = "battery_li",
            name = "Lithium-Ion / Alkaline AA Battery",
            category = WasteCategory.ELECTRONIC,
            primaryAction = EcoActionType.SPECIAL_DISPOSAL,
            materialType = "Lithium, Cobalt, Nickel, Manganese, Heavy Metals",
            decompositionTime = "100+ Years (Leaches toxic heavy metals)",
            carbonFootprint = "High extraction carbon & water depletion",
            recyclingBinType = "Battery Collection Bin at Electronics / Retail Hub",
            stepByStepGuide = listOf(
                "1. Cover positive (+) and negative (-) terminals with clear tape to prevent short circuits",
                "2. Store in a cool dry plastic container away from flammable goods",
                "3. Never throw in household trash or incinerator",
                "4. Drop off at local battery collection point"
            ),
            upcyclingIdeas = listOf(
                "Do not dismantle batteries at home; hand over for chemical smelting."
            ),
            ecoFriendlyAlternative = "Switch to USB rechargeable lithium batteries that can be recharged 1000+ times.",
            funFact = "Recovering cobalt and lithium from recycled batteries requires 80% less energy than mining raw ore from the earth.",
            arVisualBadge = "⚡ Heavy Metal Closed-Loop"
        )
    )
}
