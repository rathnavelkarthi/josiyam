package com.example.data.models

enum class AstrologyTradition(val label: String, val subtitle: String) {
    VEDIC_SIDEREAL(
        label = "Vedic (South Indian)",
        subtitle = "Based on sidereal zodiac & Lahiri Ayanamsha. Most widely used in India."
    ),
    WESTERN_TROPICAL(
        label = "Western (Tropical)",
        subtitle = "Based on solar equinox zodiac. Commonly used worldwide."
    ),
    COMPARE_BOTH(
        label = "Compare Both",
        subtitle = "Calculates Vedic and Western placements side-by-side."
    )
}

enum class CalendarConvention(val label: String, val description: String) {
    TAMIL_SOLAR("Tamil Solar Calendar", "Sauramana month reckoning (Chithirai, Vaikasi...)"),
    PANCHANGAM_LUNAR("Panchangam Lunar", "Chandramana tithis and nakshatras"),
    BOTH("Combined View", "Displays both solar day and lunar panchangam limbs")
}

enum class BirthTimeConfidence(val label: String, val description: String) {
    EXACT("Exact", "Accurate to the minute from hospital record or birth certificate"),
    APPROXIMATE("Approximate", "Known within 30–60 minutes window (e.g., afternoon)"),
    UNKNOWN("Unknown", "Exact time unknown; Lagnam and houses marked with caution")
}

data class BirthProfile(
    val id: String,
    val name: String,
    val dateOfBirth: String, // e.g. "24 Dec 1997"
    val timeOfBirth: String, // e.g. "04:30 PM"
    val timeConfidence: BirthTimeConfidence = BirthTimeConfidence.EXACT,
    val placeName: String, // e.g. "Chennai, Tamil Nadu, India"
    val latitude: Double = 13.0827,
    val longitude: Double = 80.2707,
    val timezoneId: String = "Asia/Kolkata (GMT+05:30)",
    val isDefault: Boolean = true
)

enum class ZodiacSign(
    val code: String,
    val englishName: String,
    val sanskritName: String,
    val tamilName: String,
    val gridRow: Int, // 0 to 3 in South Indian 4x4 Kattam
    val gridCol: Int, // 0 to 3 in South Indian 4x4 Kattam
    val lord: String
) {
    // Clockwise fixed layout in South Indian Kattam:
    // Row 0: Meena (0,0), Mesha (0,1), Rishabha (0,2), Mithuna (0,3)
    // Row 1: Kumbha (1,0), [blank 1,1], [blank 1,2], Kataka (1,3)
    // Row 2: Makara (2,0), [blank 2,1], [blank 2,2], Simha (2,3)
    // Row 3: Dhanu (3,0), Vrischika (3,1), Tula (3,2), Kanya (3,3)
    PISCES("Pisces", "Pisces", "Meena", "Meenam", 0, 0, "Guru"),
    ARIES("Aries", "Aries", "Mesha", "Mesham", 0, 1, "Kuja"),
    TAURUS("Taurus", "Taurus", "Rishabha", "Rishabham", 0, 2, "Shukra"),
    GEMINI("Gemini", "Gemini", "Mithuna", "Mithunam", 0, 3, "Budha"),
    CANCER("Cancer", "Cancer", "Kataka", "Kadagam", 1, 3, "Chandra"),
    LEO("Leo", "Leo", "Simha", "Simham", 2, 3, "Surya"),
    VIRGO("Virgo", "Virgo", "Kanya", "Kanni", 3, 3, "Budha"),
    LIBRA("Libra", "Libra", "Tula", "Thulam", 3, 2, "Shukra"),
    SCORPIO("Scorpio", "Scorpio", "Vrischika", "Viruchigam", 3, 1, "Kuja"),
    SAGITTARIUS("Sagittarius", "Sagittarius", "Dhanu", "Dhanusu", 3, 0, "Guru"),
    CAPRICORN("Capricorn", "Capricorn", "Makara", "Magaram", 2, 0, "Shani"),
    AQUARIUS("Aquarius", "Aquarius", "Kumbha", "Kumbham", 1, 0, "Shani");

    companion object {
        fun at(row: Int, col: Int): ZodiacSign? {
            return entries.find { it.gridRow == row && it.gridCol == col }
        }
    }
}

data class PlanetPlacement(
    val planetShort: String, // e.g. "Su", "Mo", "Ju", "Sa", "Ve", "Me", "Ma", "Ra", "Ke", "Lag"
    val planetName: String,
    val planetSanskrit: String,
    val planetTamil: String,
    val sign: ZodiacSign,
    val houseNumber: Int,
    val degrees: String,
    val nakshatra: String,
    val pada: Int,
    val isRetrograde: Boolean = false,
    val isCombust: Boolean = false,
    val dignity: String = "Friendly" // Own, Exalted, Debilitated, Moolatrikona, Friendly, Enemy
)

data class SouthIndianChart(
    val title: String,
    val tradition: AstrologyTradition,
    val lagnamSign: ZodiacSign?,
    val moonSign: ZodiacSign,
    val sunSign: ZodiacSign,
    val nakshatra: String,
    val nakshatraPada: Int,
    val placements: List<PlanetPlacement>,
    val calculationNote: String = "Astronomical calculation with Lahiri Ayanamsha (Chitrapaksha)",
    val engineName: String = "JyotishaAstronomicalEngine",
    val engineVersion: String = "2.1.0-commercial-clean",
    val calculatedAt: String = "",
    val ayanamshaName: String = "Lahiri (Chitrapaksha)",
    val ayanamshaDegrees: Double = 23.85,
    val isTimeUncertain: Boolean = false,
    val isDemoMode: Boolean = false,
    val lagnamDegrees: String? = null
)

sealed interface ChartUiState {
    data object Loading : ChartUiState
    data class Success(val chart: SouthIndianChart) : ChartUiState
    data class EngineNotConfigured(val message: String) : ChartUiState
    data class NetworkError(val message: String) : ChartUiState
    data class Unauthenticated(val message: String) : ChartUiState
    data class DemoMode(val chart: SouthIndianChart) : ChartUiState
}

data class DasaPeriod(
    val mahadasaLord: String,
    val antardasaLord: String,
    val startYear: String,
    val endYear: String,
    val isCurrent: Boolean,
    val theme: String
)
