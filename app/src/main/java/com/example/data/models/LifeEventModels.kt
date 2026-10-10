package com.example.data.models

data class LifeEvent(
    val eventId: String = "event-${System.currentTimeMillis()}",
    val ownerId: String = "local_user",
    val category: String = "Career", // Career, Education, Relationship, Relocation, Business, Purchase, Other
    val customCategory: String = "",
    val title: String,
    val notes: String = "",
    val dateString: String,
    val precision: String = "YearOnly", // Exact, Approximate, MonthYear, YearOnly, Unknown
    val location: String = "",
    val source: String = "user_entered", // user_entered, imported_consent
    val confidence: Int = 5, // 1 to 5
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class RetrospectiveAnalysis(
    val analysisId: String,
    val eventId: String,
    val eventDatePrecision: String,
    val calculatedAstronomicalContext: String,
    val matchedRules: List<MatchedRuleInfo>,
    val sourcePassages: List<SourcePassageInfo>,
    val uncertainties: List<String>,
    val traditionalInterpretationNote: String,
    val generatedAt: Long = System.currentTimeMillis()
)

data class MatchedRuleInfo(
    val ruleId: String,
    val version: String,
    val tradition: String,
    val conditionDescription: String,
    val evidenceLabel: String // traditional_doctrine, modern_commentary, research_hypothesis
)

data class SourcePassageInfo(
    val sourceId: String,
    val title: String,
    val passageRef: String,
    val translationProvenance: String,
    val supportsInterpretation: Boolean
)

data class PredictionRecord(
    val predictionId: String = "pred-${System.currentTimeMillis()}",
    val userId: String = "local_user",
    val topic: String,
    val windowStart: String,
    val windowEnd: String,
    val timezone: String = "UTC",
    val tradition: String = "Vedic / Jyotisha",
    val calculationVersion: String = "v1.2.0-swiss-ephe",
    val matchedRules: List<MatchedRuleInfo> = emptyList(),
    val sourceReferences: List<SourcePassageInfo> = emptyList(),
    val plainInterpretation: String,
    val uncertaintyStatement: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isImmutable: Boolean = true
)

data class PredictionOutcome(
    val outcomeId: String = "outcome-${System.currentTimeMillis()}",
    val predictionId: String,
    val userId: String = "local_user",
    val outcomeStatus: String = "OccurredAsDescribed", // OccurredAsDescribed, PartlyOccurred, DidNotOccur, TooAmbiguous, PreferNotToAnswer
    val explanation: String = "",
    val reportedAt: Long = System.currentTimeMillis(),
    val provenance: String = "user_reported" // user_reported, independently_verified
)

data class EvaluationSummary(
    val eligiblePredictionsCount: Int = 0,
    val reportedOutcomesCount: Int = 0,
    val missingOutcomesCount: Int = 0,
    val matchCount: Int = 0,
    val partialMatchCount: Int = 0,
    val mismatchCount: Int = 0,
    val ambiguousCount: Int = 0,
    val responseRatePercentage: Int = 0,
    val baselineComparisonNote: String = "Compared against historical general category base rates. Insufficient sample size for formal calibration."
)
