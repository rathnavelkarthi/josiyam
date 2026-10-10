package com.example.data.models

data class NumerologyReport(
    val fullName: String,
    val dateOfBirth: String,
    val chaldeanNameNumber: Int,
    val chaldeanCompound: Int,
    val chaldeanMeaning: String,
    val pythagoreanExpressionNumber: Int,
    val pythagoreanMeaning: String,
    val birthNumber: Int, // Day of month reduced (1-9)
    val birthNumberMeaning: String,
    val lifePathNumber: Int, // Full DOB reduced (1-9, 11, 22)
    val lifePathMeaning: String,
    val tamilTransliterationNote: String,
    val compatibleNumbers: List<Int>
)

data class PalmScanResult(
    val scanId: String,
    val handSide: String, // "Left Hand (Innate Potential)" or "Right Hand (Manifested Action)"
    val qualityScore: Int, // e.g. 94%
    val lineConfidenceHeart: Int, // 92%
    val lineConfidenceHead: Int, // 89%
    val lineConfidenceLife: Int, // 95%
    val lineConfidenceFate: Int, // 86%
    val mountAnalyses: List<MountAnalysis>,
    val traditionalSymbolism: String,
    val disclaimer: String
)

data class MountAnalysis(
    val mountName: String, // e.g. "Mount of Jupiter (Guru)", "Mount of Venus (Shukra)"
    val developmentLevel: String, // "Balanced & Prominent", "Moderate", "Subtle"
    val astrologicalSignificance: String
)

data class FaceAnalysisResult(
    val analysisId: String,
    val faceShape: String, // e.g. "Oval (Chandra-Surya Balance)"
    val symmetryScore: Int, // e.g. 91%
    val facialLandmarksDetected: Int, // 468 standard MediaPipe mesh points
    val features: List<FacialFeatureMetric>,
    val samudrikaTraditionNotes: String,
    val disclaimer: String
)

data class FacialFeatureMetric(
    val featureName: String,
    val measurementDescription: String,
    val traditionalReading: String
)
