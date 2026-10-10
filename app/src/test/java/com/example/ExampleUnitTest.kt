package com.example

import com.example.data.engine.AstronomicalCalculator
import com.example.data.models.*
import com.example.data.repository.JyotishaRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun repository_initialState_isConsistent() {
        val repo = JyotishaRepository.getInstance()
        val profile = repo.activeProfile.value
        assertEquals("Rathnavel", profile.name)
        assertEquals("Chennai, Tamil Nadu, India", profile.placeName)
        assertEquals(AstrologyTradition.VEDIC_SIDEREAL, repo.selectedTradition.value)

        // Verify South Indian Kattam sign positions
        assertEquals(ZodiacSign.PISCES, ZodiacSign.at(0, 0))
        assertEquals(ZodiacSign.ARIES, ZodiacSign.at(0, 1))
        assertEquals(ZodiacSign.TAURUS, ZodiacSign.at(0, 2))
        assertEquals(ZodiacSign.GEMINI, ZodiacSign.at(0, 3))
        assertEquals(ZodiacSign.CANCER, ZodiacSign.at(1, 3))
        assertEquals(ZodiacSign.LEO, ZodiacSign.at(2, 3))
        assertEquals(ZodiacSign.VIRGO, ZodiacSign.at(3, 3))
        assertEquals(ZodiacSign.LIBRA, ZodiacSign.at(3, 2))
        assertEquals(ZodiacSign.SCORPIO, ZodiacSign.at(3, 1))
        assertEquals(ZodiacSign.SAGITTARIUS, ZodiacSign.at(3, 0))
        assertEquals(ZodiacSign.CAPRICORN, ZodiacSign.at(2, 0))
        assertEquals(ZodiacSign.AQUARIUS, ZodiacSign.at(1, 0))
    }

    @Test
    fun astronomicalCalculator_calculatesDynamicChartForDifferentDates() {
        val profile1 = BirthProfile(
            id = "p1",
            name = "Native One",
            dateOfBirth = "24 Dec 1997",
            timeOfBirth = "04:30 PM",
            timeConfidence = BirthTimeConfidence.EXACT,
            placeName = "Chennai, Tamil Nadu, India",
            latitude = 13.0827,
            longitude = 80.2707,
            timezoneId = "Asia/Kolkata (GMT+05:30)"
        )

        val profile2 = BirthProfile(
            id = "p2",
            name = "Native Two",
            dateOfBirth = "15 Aug 1947",
            timeOfBirth = "12:00 AM",
            timeConfidence = BirthTimeConfidence.EXACT,
            placeName = "New Delhi, India",
            latitude = 28.6139,
            longitude = 77.2090,
            timezoneId = "Asia/Kolkata (GMT+05:30)"
        )

        val chart1 = AstronomicalCalculator.calculateNatalChart(profile1, AstrologyTradition.VEDIC_SIDEREAL)
        val chart2 = AstronomicalCalculator.calculateNatalChart(profile2, AstrologyTradition.VEDIC_SIDEREAL)

        // Verifies calculation is genuinely dynamic and not hardcoded to one result
        assertNotEquals(chart1.sunSign, chart2.sunSign)
        assertEquals("JyotishaAstronomicalEngine", chart1.engineName)
        assertEquals("2.1.0-commercial-clean", chart1.engineVersion)
        assertTrue(chart1.placements.isNotEmpty())
        assertTrue(chart2.placements.isNotEmpty())
    }

    @Test
    fun astronomicalCalculator_unknownTime_suppressesLagna() {
        val unknownProfile = BirthProfile(
            id = "p_unk",
            name = "Unknown Native",
            dateOfBirth = "01 Jan 2000",
            timeOfBirth = "",
            timeConfidence = BirthTimeConfidence.UNKNOWN,
            placeName = "Chennai",
            latitude = 13.0827,
            longitude = 80.2707
        )

        val chart = AstronomicalCalculator.calculateNatalChart(unknownProfile, AstrologyTradition.VEDIC_SIDEREAL)

        assertTrue(chart.isTimeUncertain)
        assertNull(chart.lagnamSign)
        assertNull(chart.lagnamDegrees)
    }

    @Test
    fun astronomicalCalculator_tropicalVsSidereal_appliesAyanamsha() {
        val profile = BirthProfile(
            id = "p_test",
            name = "Test Native",
            dateOfBirth = "21 Mar 2020",
            timeOfBirth = "12:00 PM",
            timeConfidence = BirthTimeConfidence.EXACT,
            placeName = "Greenwich, UK",
            latitude = 51.48,
            longitude = 0.0,
            timezoneId = "UTC"
        )

        val siderealChart = AstronomicalCalculator.calculateNatalChart(profile, AstrologyTradition.VEDIC_SIDEREAL)
        val tropicalChart = AstronomicalCalculator.calculateNatalChart(profile, AstrologyTradition.WESTERN_TROPICAL)

        assertEquals(0.0, tropicalChart.ayanamshaDegrees, 0.001)
        assertTrue(siderealChart.ayanamshaDegrees > 23.0)
    }

    @Test
    fun repository_askQuestion_generatesStructuredReading() {
        val repo = JyotishaRepository.getInstance()
        val initialCount = repo.readings.value.size
        val newReading = repo.askQuestion(QuestionCategory.HOME, "Will I buy a house in 2027?")

        assertNotNull(newReading.id)
        assertEquals(QuestionCategory.HOME, newReading.category)
        assertTrue(newReading.chartFactors.isNotEmpty())
        assertTrue(newReading.traditionalRemedies.isNotEmpty())
        assertEquals(initialCount + 1, repo.readings.value.size)
    }
}
