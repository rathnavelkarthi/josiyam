package com.example.data.models

enum class QuestionCategory(
    val title: String,
    val iconName: String,
    val sampleQuestion: String
) {
    CAREER("Career", "work", "Will I find a favorable career opportunity or promotion this year?"),
    RELATIONSHIPS("Relationships", "favorite", "What does my planetary timing suggest for finding alignment?"),
    MONEY("Money & Wealth", "payments", "How are my 2nd and 11th houses positioned for financial stability?"),
    MARRIAGE("Marriage", "favorite_border", "What are the traditional indicators for marriage timing?"),
    HEALTH("Health & Wellbeing", "spa", "How does my chart reflect natural vital energy and balance?"),
    EDUCATION("Education", "school", "Is this a favorable period for higher studies or competitive exams?"),
    HOME("Home & Property", "home", "Will I buy a house or vehicle in the next 2 years?"),
    TRAVEL("Travel & Relocation", "flight", "Are there indications of foreign relocation or pilgrimage?"),
    SPIRITUALITY("Spiritual Practice", "auto_awesome", "Which Ishta Devata and Dhyana practices suit my Lagna?"),
    TIMING("Auspicious Timing", "schedule", "When is the most supportive window for a new enterprise?")
}

data class ReadingReport(
    val id: String,
    val title: String,
    val dateIssued: String,
    val category: QuestionCategory,
    val questionText: String,
    val summary: String,
    val timingWindow: String,
    val chartFactors: List<String>,
    val whyInterpretation: String,
    val scripturalCanon: String,
    val sourcePassageLocator: String, // e.g. "Brihat Parashara Hora Shastra, Ch. 84, V. 12"
    val practicalGuidance: List<String>,
    val traditionalRemedies: List<RemedyItem>
)

data class RemedyItem(
    val id: String,
    val title: String,
    val deity: String,
    val practiceType: String, // "Mantra", "Temple Visit", "Charity", "Meditation"
    val scriptureCitation: String,
    val description: String,
    val mantraText: String = "",
    val suggestedTempleName: String = "",
    val suggestedTempleLocation: String = "",
    val practicalAlternative: String = "",
    val safetyNotice: String = "Traditional spiritual practice only. Non-coercive. Not a substitute for medical or professional counsel."
)
