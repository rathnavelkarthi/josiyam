package com.example.data.models

data class SourceWork(
    val id: String,
    val title: String,
    val sanskritTitle: String,
    val authorOrTradition: String,
    val period: String,
    val chapter: String,
    val verseLocator: String,
    val originalVerse: String,
    val transliteration: String,
    val translation: String,
    val commentary: String,
    val rightsStatus: String = "Public Domain / Classical Heritage"
)

data class LifeMilestone(
    val id: String,
    val title: String,
    val dateDisplay: String,
    val category: String,
    val notes: String,
    val activeDasaAtEvent: String = ""
)

data class BodyAssessment(
    val vataPercentage: Int = 30,
    val pittaPercentage: Int = 40,
    val kaphaPercentage: Int = 30,
    val constitutionType: String = "Pitta-Vata (Pitta Dominant)",
    val traditionalDescription: String = "You have a Pitta-dominant constitution with balanced Vata and Kapha qualities. In traditional Ayurveda, this reflects strong digestion (Agni) and focused determination.",
    val careRecommendations: List<String> = listOf(
        "Favour cooling herbs, coconut water, and sweet/bitter fruits.",
        "Maintain regular meal routines; avoid skipping meals during high sun hours.",
        "Incorporate cooling Pranayama such as Sheetali or Nadi Shodhana."
    ),
    val nonDiagnosticNotice: String = "Ayurvedic constitution assessment is a traditional wellness model. It does not diagnose, treat, or prevent medical diseases."
)

data class SamudrikaLandmark(
    val name: String,
    val location: String,
    val traditionalSymbolism: String,
    val scorePercent: Int
)

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "siddha"
    val text: String,
    val timestamp: String,
    val citations: List<String> = emptyList(),
    val tag: String? = null // "Calculation", "Traditional interpretation", "Scriptural Canon"
)
