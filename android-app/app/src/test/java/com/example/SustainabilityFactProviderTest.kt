package com.example

import com.example.data.util.SustainabilityFactProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SustainabilityFactProviderTest {

    @Test
    fun getDailyFact_returnsValidCuratedFact() {
        val fact = SustainabilityFactProvider.getDailyFact(dayOfYear = 100)
        assertNotNull(fact)
        assertTrue(fact.fact.isNotBlank())
        assertTrue(fact.topic.isNotBlank())
        assertTrue(fact.globalImpact.isNotBlank())
        assertTrue(fact.takeawayAction.isNotBlank())
        assertTrue(fact.source.isNotBlank())
    }

    @Test
    fun getNextFact_returnsDifferentFactWhenAvailable() {
        val firstFact = SustainabilityFactProvider.getDailyFact(dayOfYear = 1)
        val nextFact = SustainabilityFactProvider.getNextFact(firstFact.id)
        assertNotNull(nextFact)
        // With multiple facts in the curated list, next fact should not match current ID
        assertFalse(firstFact.id == nextFact.id)
    }

    @Test
    fun getAllFacts_returnsComprehensiveList() {
        val facts = SustainabilityFactProvider.getAllFacts()
        assertTrue(facts.size >= 10)
    }
}
