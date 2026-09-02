package com.example.data.util

import com.example.data.model.SustainabilityFact
import java.util.Calendar

object SustainabilityFactProvider {

    val curatedFacts: List<SustainabilityFact> = listOf(
        SustainabilityFact(
            id = "fact_plastic_bottle_decomposition",
            topic = "Plastic & Marine Life",
            categoryTag = "Circular Economy",
            fact = "A single plastic bottle takes over 450 years to decompose in nature. Around 8 million metric tons of plastic enter our oceans annually, breaking down into microplastics that enter the marine food web.",
            globalImpact = "Recycling 1 ton of plastic saves ~5,774 kWh of energy and 16.3 barrels of oil.",
            takeawayAction = "Carry a reusable stainless steel flask everywhere to eliminate single-use PET bottles.",
            source = "UN Environment Programme (UNEP)",
            iconEmoji = "🌊"
        ),
        SustainabilityFact(
            id = "fact_aluminum_infinite_loop",
            topic = "Metals & Infinite Recycling",
            categoryTag = "Zero Waste",
            fact = "Aluminum is 100% infinitely recyclable without losing its structural properties. Nearly 75% of all aluminum ever produced in human history is still in productive use today!",
            globalImpact = "Recycling aluminum requires 95% less energy than extracting virgin aluminum from bauxite ore.",
            takeawayAction = "Rinse and crush soda cans before putting them in the scrap metals bin.",
            source = "The Aluminum Association & EPA",
            iconEmoji = "🥫"
        ),
        SustainabilityFact(
            id = "fact_food_waste_methane",
            topic = "Food Systems & Composting",
            categoryTag = "Organic Living",
            fact = "If global food waste were a country, it would be the third-largest greenhouse gas emitter in the world behind the USA and China, generating massive methane in landfills.",
            globalImpact = "Composting food scraps eliminates anaerobic methane emissions while regenerating topsoil.",
            takeawayAction = "Start a countertop compost caddy for vegetable peels and coffee grounds.",
            source = "Food and Agriculture Organization (FAO)",
            iconEmoji = "🌱"
        ),
        SustainabilityFact(
            id = "fact_led_lighting_revolution",
            topic = "Clean Energy & Efficiency",
            categoryTag = "Energy Saving",
            fact = "Residential LED bulbs use at least 75% less energy and last up to 25 times longer than traditional incandescent lighting, containing zero hazardous mercury vapor.",
            globalImpact = "Widespread adoption of LEDs could save over 569 TWh of electricity globally each year.",
            takeawayAction = "Switch standard halogen and incandescent bulbs to Energy Star rated LEDs.",
            source = "U.S. Department of Energy (DOE)",
            iconEmoji = "💡"
        ),
        SustainabilityFact(
            id = "fact_fast_fashion_water",
            topic = "Textiles & Fast Fashion",
            categoryTag = "Conscious Consumerism",
            fact = "It takes approximately 2,700 liters of fresh water to manufacture just one single cotton t-shirt — enough drinking water for one person for 900 days.",
            globalImpact = "Extending the life of clothes by just 9 months reduces carbon, waste, and water footprints by ~20-30%.",
            takeawayAction = "Prioritize timeless durable garments, thrift stores, and clothing repair workshops.",
            source = "World Wildlife Fund (WWF)",
            iconEmoji = "👕"
        ),
        SustainabilityFact(
            id = "fact_tree_carbon_capture",
            topic = "Forests & Biodiversity",
            categoryTag = "Nature Restoration",
            fact = "A mature hardwood tree absorbs up to 22 kilograms (48 lbs) of carbon dioxide each year from the atmosphere, while releasing enough clean oxygen for two human beings.",
            globalImpact = "Protecting and reforesting tropical canopies can deliver over 30% of global climate mitigation targets.",
            takeawayAction = "Plant native tree saplings or support urban greening initiatives in your city.",
            source = "NASA Earth Observatory & Arbor Day Foundation",
            iconEmoji = "🌳"
        ),
        SustainabilityFact(
            id = "fact_water_tap_leak",
            topic = "Water Conservation",
            categoryTag = "Resource Management",
            fact = "A faucet leaking just one single drop per second wastes more than 3,000 gallons (11,350 liters) of fresh drinking water every year — equivalent to over 180 showers.",
            globalImpact = "Household water efficiency measures can cut municipal water stress in cities by up to 25%.",
            takeawayAction = "Fix dripping washers immediately and use aerators on all bathroom and kitchen faucets.",
            source = "EPA WaterSense",
            iconEmoji = "💧"
        ),
        SustainabilityFact(
            id = "fact_ewaste_gold_ore",
            topic = "E-Waste & Critical Minerals",
            categoryTag = "Urban Mining",
            fact = "One metric ton of recycled smartphones and circuit boards contains up to 100 times more pure gold and 50 times more silver than one ton of raw mined geological ore.",
            globalImpact = "Recycling electronic waste prevents toxic lead, cadmium, and lithium contamination in groundwater.",
            takeawayAction = "Never dispose of old batteries or cables in household trash; drop them at certified e-waste hubs.",
            source = "Global E-Waste Monitor & UNU",
            iconEmoji = "📱"
        ),
        SustainabilityFact(
            id = "fact_glass_circularity",
            topic = "Glass & Packaging",
            categoryTag = "Zero Waste",
            fact = "Glass is made of all-natural non-porous silica sand and can be remelted endlessly without chemical leaching or quality loss. 1 ton of recycled glass saves 1.2 tons of virgin raw materials.",
            globalImpact = "Cullet (crushed recycled glass) melts at lower temperatures, cutting furnace energy and emissions by 20%.",
            takeawayAction = "Clean and repurpose glass jars for spice, tea, and bulk cereal storage.",
            source = "European Container Glass Federation (FEVE)",
            iconEmoji = "🫙"
        ),
        SustainabilityFact(
            id = "fact_bicycle_transit_urban",
            topic = "Clean Mobility",
            categoryTag = "Sustainable Transit",
            fact = "Cycling produces zero tailpipe emissions and uses about 20 times less energy than driving an electric car and 50 times less than a petrol vehicle for short urban trips under 5 km.",
            globalImpact = "Replacing one car commute per week with a bicycle saves ~0.5 metric tons of CO₂ annually per rider.",
            takeawayAction = "Walk or cycle for short neighborhood errands within 2 kilometers.",
            source = "Institute for Transportation and Development Policy (ITDP)",
            iconEmoji = "🚲"
        ),
        SustainabilityFact(
            id = "fact_solar_energy_potential",
            topic = "Solar Power & Clean Grid",
            categoryTag = "Renewable Tech",
            fact = "More solar energy strikes the Earth's surface in just one single hour (430 quintillion Joules) than the entire global human population consumes in a full year.",
            globalImpact = "Solar photovoltaic energy costs have plummeted over 85% in the last decade, making it the cheapest electricity source.",
            takeawayAction = "Explore rooftop solar panels or choose a utility provider offering renewable tariffs.",
            source = "International Renewable Energy Agency (IRENA)",
            iconEmoji = "☀️"
        ),
        SustainabilityFact(
            id = "fact_cardboard_recycling_trees",
            topic = "Paper & Pulp Conservation",
            categoryTag = "Circular Economy",
            fact = "Recycling 1 ton of corrugated cardboard saves 17 mature trees, 7,000 gallons of clean water, 4,000 kW of electricity, and 3 cubic yards of landfill space.",
            globalImpact = "Corrugated cardboard fibers can be pulped and reformed up to 5 to 7 times into new packaging boxes.",
            takeawayAction = "Flatten and keep shipping boxes dry before placing them in the cardboard recycling stream.",
            source = "American Forest & Paper Association",
            iconEmoji = "📦"
        )
    )

    /**
     * Retrieves the deterministic fact for today based on the day of the year.
     * Ensures every user sees a refreshed fact each day without needing an active internet connection.
     */
    fun getDailyFact(dayOfYear: Int = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)): SustainabilityFact {
        if (curatedFacts.isEmpty()) {
            return SustainabilityFact(
                id = "default_fact",
                topic = "Sustainability",
                categoryTag = "Eco Habit",
                fact = "Small daily eco-friendly actions collectively create monumental planetary impact.",
                globalImpact = "Every single scan and habit drives community sustainability.",
                takeawayAction = "Practice the 4Rs: Recycle, Reuse, Reduce, Replace.",
                source = "EcoVision Science Team",
                iconEmoji = "🌍"
            )
        }
        val index = (dayOfYear.coerceAtLeast(0)) % curatedFacts.size
        return curatedFacts[index]
    }

    /**
     * Gets a random or next fact different from the current one for manual interactive exploration.
     */
    fun getNextFact(currentId: String?): SustainabilityFact {
        val filtered = curatedFacts.filter { it.id != currentId }
        return if (filtered.isNotEmpty()) filtered.random() else getDailyFact()
    }

    /**
     * Returns all curated facts.
     */
    fun getAllFacts(): List<SustainabilityFact> = curatedFacts

    /**
     * Utility method to simulate network API fetch or fallback to local curated knowledge.
     * Allows seamless expansion to remote REST endpoints while ensuring instant offline reliability.
     */
    suspend fun fetchDailyFactFromApiOrLocal(): SustainabilityFact {
        return getDailyFact()
    }
}
