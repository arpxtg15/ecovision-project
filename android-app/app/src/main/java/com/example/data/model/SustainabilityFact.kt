package com.example.data.model

data class SustainabilityFact(
    val id: String,
    val topic: String,
    val categoryTag: String,
    val fact: String,
    val globalImpact: String,
    val takeawayAction: String,
    val source: String,
    val iconEmoji: String
)
