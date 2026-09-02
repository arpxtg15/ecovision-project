package com.example.data.model

data class EcoProduct(
    val id: String,
    val name: String,
    val subtitle: String,
    val category: String,
    val priceUsd: Double,
    val ecoCreditsPrice: Int,
    val rating: Double,
    val reviewsCount: Int,
    val iconEmoji: String,
    val sustainabilityBadge: String,
    val impactStatement: String,
    val description: String,
    val features: List<String>,
    val material: String,
    val isFeatured: Boolean = false
)

object EcoShopData {
    val categories = listOf(
        "All",
        "Zero Waste",
        "Clean Energy",
        "Kitchen & Dining",
        "Personal Care",
        "Impact & Trees"
    )

    val products = listOf(
        EcoProduct(
            id = "prod_bamboo_set",
            name = "Bamboo Travel Cutlery Set",
            subtitle = "Knife, fork, spoon & straw in organic pouch",
            category = "Zero Waste",
            priceUsd = 12.99,
            ecoCreditsPrice = 250,
            rating = 4.9,
            reviewsCount = 428,
            iconEmoji = "🎋",
            sustainabilityBadge = "100% Compostable",
            impactStatement = "Eliminates ~450 single-use plastic utensils per year",
            description = "Crafted from organically grown Moso bamboo. Naturally antibacterial, lightweight, and long-lasting for travel, office lunch, and camping.",
            features = listOf(
                "Naturally antibacterial & chemical-free",
                "Includes organic cotton roll-up carry pouch",
                "Dishwasher safe & biodegradable"
            ),
            material = "Organic Bamboo & Cotton",
            isFeatured = true
        ),
        EcoProduct(
            id = "prod_solar_bank",
            name = "Solar Foldable Power Bank",
            subtitle = "20,000mAh Dual-USB with SunPower cells",
            category = "Clean Energy",
            priceUsd = 34.99,
            ecoCreditsPrice = 650,
            rating = 4.8,
            reviewsCount = 312,
            iconEmoji = "☀️",
            sustainabilityBadge = "Clean Solar Powered",
            impactStatement = "Saves ~18kg grid CO2 per year of mobile charging",
            description = "High-efficiency monocrystalline solar panels capture ambient daylight to recharge your mobile devices anywhere off-grid.",
            features = listOf(
                "20,000mAh capacity (charges phones up to 5x)",
                "IP67 waterproof and shockproof casing",
                "Integrated emergency LED flashlight"
            ),
            material = "Recycled Polymer & Monocrystalline Silicon",
            isFeatured = true
        ),
        EcoProduct(
            id = "prod_insulated_bottle",
            name = "Stainless Steel Eco Flask (750ml)",
            subtitle = "Triple-insulated thermal flask",
            category = "Kitchen & Dining",
            priceUsd = 22.50,
            ecoCreditsPrice = 400,
            rating = 4.9,
            reviewsCount = 680,
            iconEmoji = "🍶",
            sustainabilityBadge = "Zero-Plastic Daily",
            impactStatement = "Replaces ~1,460 disposable plastic bottles over 4 years",
            description = "Keeps drinks ice-cold for 24 hours or piping hot for 12 hours. Premium pro-grade 18/8 stainless steel prevents flavor transfer.",
            features = listOf(
                "BPA-free & condensation-proof exterior",
                "Wide mouth fits standard ice cubes",
                "Lifetime durability warranty"
            ),
            material = "18/8 Pro-grade Recycled Stainless Steel"
        ),
        EcoProduct(
            id = "prod_plant_tree",
            name = "Plant 5 Mangrove Trees Voucher",
            subtitle = "Verified coastal ecosystem restoration project",
            category = "Impact & Trees",
            priceUsd = 10.00,
            ecoCreditsPrice = 180,
            rating = 5.0,
            reviewsCount = 1240,
            iconEmoji = "🌳",
            sustainabilityBadge = "Certified Climate Offset",
            impactStatement = "Offsets ~60kg CO2 per tree over its lifecycle",
            description = "Directly funds the planting of 5 native mangrove saplings in coastal wetlands with GPS coordinate verification certificate.",
            features = listOf(
                "Official digital certificate with GPS planting zone",
                "Protects coastal marine biodiversity & fisheries",
                "Monitored for 5 years by certified forestry partners"
            ),
            material = "Real World Verified Environmental Offset",
            isFeatured = true
        ),
        EcoProduct(
            id = "prod_shampoo_bar",
            name = "Solid Shampoo & Conditioner Bar Duo",
            subtitle = "Organic botanical nourishing hair bars",
            category = "Personal Care",
            priceUsd = 14.50,
            ecoCreditsPrice = 280,
            rating = 4.7,
            reviewsCount = 295,
            iconEmoji = "🧼",
            sustainabilityBadge = "Waterless & Bottle-Free",
            impactStatement = "Saves 3 plastic shampoo bottles and 90% shipping water weight",
            description = "Plant-derived cleansing bars enriched with argan oil, shea butter, and peppermint essence. Free of sulfates and parabens.",
            features = listOf(
                "Equivalent to 3 standard liquid shampoo bottles",
                "Plastic-free recyclable kraft packaging",
                "pH balanced for all hair types"
            ),
            material = "Organic Plant Oils & Essential Herbs"
        ),
        EcoProduct(
            id = "prod_cotton_produce_bags",
            name = "Organic Cotton Mesh Produce Bags (Set of 6)",
            subtitle = "Washable tare-weight labeled grocery bags",
            category = "Zero Waste",
            priceUsd = 11.99,
            ecoCreditsPrice = 220,
            rating = 4.8,
            reviewsCount = 512,
            iconEmoji = "🛍️",
            sustainabilityBadge = "100% GOTS Certified Organic",
            impactStatement = "Saves over 300 thin plastic grocery bags annually",
            description = "Breathable mesh allows ethylene gas to escape, keeping fruits and vegetables fresher for longer in your pantry and fridge.",
            features = listOf(
                "Includes 2 Small, 2 Medium, 2 Large bags",
                "Color-coded tare weight tags for easy checkout",
                "Machine washable with reinforced double stitching"
            ),
            material = "100% GOTS Certified Organic Cotton"
        ),
        EcoProduct(
            id = "prod_beeswax_wraps",
            name = "Natural Beeswax Food Wraps (Pack of 4)",
            subtitle = "Reusable alternative to plastic cling wrap",
            category = "Kitchen & Dining",
            priceUsd = 16.00,
            ecoCreditsPrice = 300,
            rating = 4.8,
            reviewsCount = 388,
            iconEmoji = "🐝",
            sustainabilityBadge = "Plastic Wrap Replacement",
            impactStatement = "Replaces 100+ meters of non-recyclable plastic cling film",
            description = "Made with organic cotton infused with sustainably harvested beeswax, jojoba oil, and tree resin. Seals with the warmth of your hands.",
            features = listOf(
                "Reusable for 12+ months with simple cold water wash",
                "Naturally antibacterial keeps food fresh",
                "Compostable at the end of lifecycle"
            ),
            material = "Organic Cotton, Beeswax & Jojoba Oil"
        ),
        EcoProduct(
            id = "prod_compost_bin",
            name = "Countertop Odor-Free Compost Bin (4L)",
            subtitle = "Stainless steel with active charcoal air filter",
            category = "Kitchen & Dining",
            priceUsd = 28.00,
            ecoCreditsPrice = 520,
            rating = 4.9,
            reviewsCount = 440,
            iconEmoji = "🍂",
            sustainabilityBadge = "Organic Waste Diverter",
            impactStatement = "Diverts up to 250kg of food scraps from landfills annually",
            description = "Sleek kitchen countertop bin with dual-layer activated charcoal filter in the lid that completely traps organic food odors.",
            features = listOf(
                "Dual activated charcoal filters last up to 6 months",
                "Rust-resistant brushed stainless steel",
                "Comfortable carrying handle for transport to garden"
            ),
            material = "Brushed Stainless Steel & Carbon Filter"
        ),
        EcoProduct(
            id = "prod_seed_pencils",
            name = "Plantable Seed Pencils & Paper Pack (10 Pcs)",
            subtitle = "Sprouts herbs & flowers when finished",
            category = "Zero Waste",
            priceUsd = 9.99,
            ecoCreditsPrice = 190,
            rating = 4.9,
            reviewsCount = 610,
            iconEmoji = "✏️",
            sustainabilityBadge = "Zero-Waste Plantable",
            impactStatement = "Turns stationery into live basil, mint, and wildflowers",
            description = "When the pencil is too short to write with, simply plant the green capsule end into soil to grow organic herbs and vibrant pollinator flowers.",
            features = listOf(
                "Contains seeds for Basil, Mint, Sunflower & Daisy",
                "100% sustainable cedar wood body and graphite core",
                "Seed paper notepad included"
            ),
            material = "Sustainable Cedar Wood & Organic Seeds"
        )
    )
}
