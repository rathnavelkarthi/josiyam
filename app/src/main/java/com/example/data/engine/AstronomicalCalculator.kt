package com.example.data.engine

import com.example.data.models.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

object AstronomicalCalculator {
    const val ENGINE_NAME = "JyotishaAstronomicalEngine"
    const val ENGINE_VERSION = "2.1.0-commercial-clean"

    private val NAKSHATRAS = listOf(
        "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra",
        "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni", "Uttara Phalguni",
        "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha",
        "Moola", "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha",
        "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
    )

    private val PLANET_DETAILS = mapOf(
        "Su" to Triple("Sun (Surya)", "Surya", "Suriyan"),
        "Mo" to Triple("Moon (Chandra)", "Chandra", "Chandran"),
        "Ma" to Triple("Mars (Mangala)", "Mangala", "Sevvai"),
        "Me" to Triple("Mercury (Budha)", "Budha", "Budhan"),
        "Ju" to Triple("Jupiter (Guru)", "Brihaspati", "Guru"),
        "Ve" to Triple("Venus (Shukra)", "Shukra", "Sukran"),
        "Sa" to Triple("Saturn (Shani)", "Shani", "Sani"),
        "Ra" to Triple("North Node (Rahu)", "Rahu", "Raghu"),
        "Ke" to Triple("South Node (Ketu)", "Ketu", "Kethu")
    )

    fun calculateNatalChart(
        profile: BirthProfile,
        tradition: AstrologyTradition
    ): SouthIndianChart {
        val isUncertain = profile.timeConfidence == BirthTimeConfidence.UNKNOWN

        // Parse date and time into UTC
        val (jd, t) = computeJulianDayAndCenturies(profile)

        // Lahiri Ayanamsha (Chitrapaksha)
        val ayanamsha = if (tradition == AstrologyTradition.WESTERN_TROPICAL) {
            0.0
        } else {
            23.85308 + (1.397 * t)
        }

        fun toTradition(tropicalLong: Double): Double {
            return (tropicalLong - ayanamsha + 720.0) % 360.0
        }

        // Calculate Planetary Longitudes
        val rawSun = calculateSun(t)
        val rawMoon = calculateMoon(t)
        val rawMars = calculateMars(t, rawSun)
        val rawMercury = calculateMercury(t, rawSun)
        val rawJupiter = calculateJupiter(t, rawSun)
        val rawVenus = calculateVenus(t, rawSun)
        val rawSaturn = calculateSaturn(t, rawSun)
        val (rawRahu, rawKetu) = calculateNodes(t)

        val sunLong = toTradition(rawSun)
        val moonLong = toTradition(rawMoon)
        val marsLong = toTradition(rawMars)
        val mercLong = toTradition(rawMercury)
        val jupLong = toTradition(rawJupiter)
        val venLong = toTradition(rawVenus)
        val satLong = toTradition(rawSaturn)
        val rahuLong = toTradition(rawRahu)
        val ketuLong = toTradition(rawKetu)

        // Ascendant (Lagnam)
        val lagnaSign: ZodiacSign?
        val lagnaDegreesFormatted: String?
        val lagnaSignIdx: Int?

        if (!isUncertain) {
            val rawAsc = calculateAscendant(jd, profile.latitude, profile.longitude, t)
            val ascLong = toTradition(rawAsc)
            val sIdx = (ascLong / 30.0).toInt() % 12
            lagnaSignIdx = sIdx
            lagnaSign = getSignByIndex(sIdx)
            val degInSign = ascLong % 30.0
            lagnaDegreesFormatted = String.format("%02d°%02d'", degInSign.toInt(), ((degInSign % 1.0) * 60).toInt())
        } else {
            lagnaSign = null
            lagnaDegreesFormatted = null
            lagnaSignIdx = null
        }

        // Build Placements List
        val placements = mutableListOf<PlanetPlacement>()

        if (lagnaSign != null && lagnaDegreesFormatted != null) {
            placements.add(
                PlanetPlacement(
                    planetShort = "Lag",
                    planetName = "Ascendant (Lagna)",
                    planetSanskrit = "Lagna",
                    planetTamil = "Lagnam",
                    sign = lagnaSign,
                    houseNumber = 1,
                    degrees = lagnaDegreesFormatted,
                    nakshatra = getNakshatraName(toTradition(calculateAscendant(jd, profile.latitude, profile.longitude, t))),
                    pada = getNakshatraPada(toTradition(calculateAscendant(jd, profile.latitude, profile.longitude, t)))
                )
            )
        }

        val planetRawList = listOf(
            "Su" to sunLong,
            "Mo" to moonLong,
            "Ma" to marsLong,
            "Me" to mercLong,
            "Ju" to jupLong,
            "Ve" to venLong,
            "Sa" to satLong,
            "Ra" to rahuLong,
            "Ke" to ketuLong
        )

        planetRawList.forEach { (key, longitude) ->
            val details = PLANET_DETAILS[key] ?: Triple(key, key, key)
            val signIdx = (longitude / 30.0).toInt() % 12
            val sign = getSignByIndex(signIdx)
            val house = if (lagnaSignIdx != null) ((signIdx - lagnaSignIdx + 12) % 12) + 1 else 1
            val degInSign = longitude % 30.0
            val degFormatted = String.format("%02d°%02d'", degInSign.toInt(), ((degInSign % 1.0) * 60).toInt())
            val nakName = getNakshatraName(longitude)
            val nakPada = getNakshatraPada(longitude)

            val dignity = when {
                key == "Ma" && sign == ZodiacSign.CAPRICORN -> "Exalted"
                key == "Ju" && sign == ZodiacSign.CANCER -> "Exalted"
                key == "Ve" && sign == ZodiacSign.PISCES -> "Exalted"
                key == "Sa" && sign == ZodiacSign.LIBRA -> "Exalted"
                key == "Su" && sign == ZodiacSign.ARIES -> "Exalted"
                key == "Mo" && sign == ZodiacSign.TAURUS -> "Exalted"
                key == "Me" && sign == ZodiacSign.VIRGO -> "Exalted"
                else -> "Friendly"
            }

            placements.add(
                PlanetPlacement(
                    planetShort = key,
                    planetName = details.first,
                    planetSanskrit = details.second,
                    planetTamil = details.third,
                    sign = sign,
                    houseNumber = house,
                    degrees = degFormatted,
                    nakshatra = nakName,
                    pada = nakPada,
                    isRetrograde = key in listOf("Ra", "Ke"),
                    dignity = dignity
                )
            )
        }

        val moonSign = getSignByIndex((moonLong / 30.0).toInt() % 12)
        val sunSign = getSignByIndex((sunLong / 30.0).toInt() % 12)
        val birthNakshatra = getNakshatraName(moonLong)
        val birthPada = getNakshatraPada(moonLong)

        val calDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        calDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return SouthIndianChart(
            title = "Rasi (D1) Natal Chart",
            tradition = tradition,
            lagnamSign = lagnaSign,
            moonSign = moonSign,
            sunSign = sunSign,
            nakshatra = birthNakshatra,
            nakshatraPada = birthPada,
            placements = placements,
            calculationNote = if (tradition == AstrologyTradition.WESTERN_TROPICAL)
                "Calculated with Tropical Solar Zodiac (Equinox Reference)"
            else
                "Calculated with Lahiri Ayanamsha (Chitrapaksha: ${String.format("%.2f°", ayanamsha)})",
            engineName = ENGINE_NAME,
            engineVersion = ENGINE_VERSION,
            calculatedAt = calDateFormat.format(Date()),
            ayanamshaName = if (tradition == AstrologyTradition.WESTERN_TROPICAL) "None (Tropical)" else "Lahiri (Chitrapaksha)",
            ayanamshaDegrees = ayanamsha,
            isTimeUncertain = isUncertain,
            isDemoMode = false,
            lagnamDegrees = lagnaDegreesFormatted
        )
    }

    private fun computeJulianDayAndCenturies(profile: BirthProfile): Pair<Double, Double> {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        
        // Parse "24 Dec 1997" format
        try {
            val df = SimpleDateFormat("dd MMM yyyy", Locale.US)
            val parsedDate = df.parse(profile.dateOfBirth)
            if (parsedDate != null) {
                cal.time = parsedDate
            }
        } catch (_: Exception) {}

        var hour = 12
        var min = 0
        if (profile.timeConfidence != BirthTimeConfidence.UNKNOWN && profile.timeOfBirth.isNotBlank()) {
            try {
                val tf = SimpleDateFormat("hh:mm a", Locale.US)
                val parsedTime = tf.parse(profile.timeOfBirth)
                if (parsedTime != null) {
                    val tCal = Calendar.getInstance()
                    tCal.time = parsedTime
                    hour = tCal.get(Calendar.HOUR_OF_DAY)
                    min = tCal.get(Calendar.MINUTE)
                }
            } catch (_: Exception) {}
        }

        // Apply timezone offset adjustment
        val tzOffsetHours = if (profile.timezoneId.contains("GMT+05:30") || profile.timezoneId.contains("Kolkata")) {
            5.5
        } else if (profile.timezoneId.contains("GMT+08:00") || profile.timezoneId.contains("Singapore")) {
            8.0
        } else if (profile.timezoneId.contains("GMT-05:00") || profile.timezoneId.contains("New_York")) {
            -5.0
        } else {
            0.0
        }

        val utcDecimalHour = (hour + min / 60.0) - tzOffsetHours

        var y = cal.get(Calendar.YEAR)
        var m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)

        if (m <= 2) {
            y -= 1
            m += 12
        }

        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        var jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + d + b - 1524.5
        jd += (utcDecimalHour / 24.0)

        val t = (jd - 2451545.0) / 36525.0
        return Pair(jd, t)
    }

    private fun calculateSun(t: Double): Double {
        val l0 = 280.46646 + 36000.76983 * t + 0.0003032 * (t * t)
        val m = 357.52911 + 35999.05029 * t - 0.0001537 * (t * t)
        val mRad = Math.toRadians(m % 360.0)
        val c = (1.914602 - 0.004817 * t) * sin(mRad) + (0.019993 - 0.000101 * t) * sin(2 * mRad)
        return (l0 + c + 720.0) % 360.0
    }

    private fun calculateMoon(t: Double): Double {
        val lPrime = 218.3164477 + 481267.88123421 * t
        val d = Math.toRadians((297.8501921 + 445267.1114034 * t) % 360.0)
        val m = Math.toRadians((357.5291092 + 35999.0502909 * t) % 360.0)
        val mPrime = Math.toRadians((134.9633964 + 477198.8675055 * t) % 360.0)

        val sigmaL = 6.288774 * sin(mPrime) + 1.274027 * sin(2 * d - mPrime) + 0.658314 * sin(2 * d) - 0.185116 * sin(m)
        return (lPrime + sigmaL + 720.0) % 360.0
    }

    private fun calculateMars(t: Double, sunLong: Double): Double {
        val l = (355.433 + 19140.299 * t) % 360.0
        val m = Math.toRadians((19.373 + 19139.858 * t) % 360.0)
        val helio = (l + 10.691 * sin(m) + 720.0) % 360.0
        val diff = Math.toRadians(helio - sunLong)
        val geo = (helio + Math.toDegrees(atan2(sin(diff), 1.524 - cos(diff))) + 720.0) % 360.0
        return geo
    }

    private fun calculateMercury(t: Double, sunLong: Double): Double {
        val m = Math.toRadians((174.794 + 149472.516 * t) % 360.0)
        val offset = (23.44 * sin(m) * 0.45) % 28.0
        return (sunLong + offset + 720.0) % 360.0
    }

    private fun calculateJupiter(t: Double, sunLong: Double): Double {
        val l = (34.351 + 3034.905 * t) % 360.0
        val m = Math.toRadians((19.895 + 3034.690 * t) % 360.0)
        val helio = (l + 5.555 * sin(m) + 720.0) % 360.0
        val diff = Math.toRadians(helio - sunLong)
        val geo = (helio + Math.toDegrees(atan2(sin(diff), 5.20 - cos(diff))) + 720.0) % 360.0
        return geo
    }

    private fun calculateVenus(t: Double, sunLong: Double): Double {
        val m = Math.toRadians((50.115 + 58517.803 * t) % 360.0)
        val offset = sin(m) * 44.0
        return (sunLong + offset + 720.0) % 360.0
    }

    private fun calculateSaturn(t: Double, sunLong: Double): Double {
        val l = (50.077 + 1222.113 * t) % 360.0
        val m = Math.toRadians((317.020 + 1221.551 * t) % 360.0)
        val helio = (l + 6.358 * sin(m) + 720.0) % 360.0
        val diff = Math.toRadians(helio - sunLong)
        val geo = (helio + Math.toDegrees(atan2(sin(diff), 9.54 - cos(diff))) + 720.0) % 360.0
        return geo
    }

    private fun calculateNodes(t: Double): Pair<Double, Double> {
        val rahu = (125.04452 - 1934.136261 * t + 72000.0) % 360.0
        val ketu = (rahu + 180.0) % 360.0
        return Pair(rahu, ketu)
    }

    private fun calculateAscendant(jd: Double, lat: Double, lon: Double, t: Double): Double {
        val jd0 = floor(jd - 0.5) + 0.5
        val t0 = (jd0 - 2451545.0) / 36525.0
        val gmst0 = 100.46061837 + 36000.770053608 * t0
        val utHours = ((jd + 0.5) % 1.0) * 24.0
        val gmst = (gmst0 + 360.98564736629 * (utHours / 24.0)) % 360.0
        val lst = Math.toRadians((gmst + lon + 720.0) % 360.0)
        val eps = Math.toRadians(23.4392911 - 0.0130042 * t)
        val phi = Math.toRadians(lat)

        val y = cos(lst)
        val x = -sin(lst) * cos(eps) - tan(phi) * sin(eps)
        return (Math.toDegrees(atan2(y, x)) + 90.0 + 720.0) % 360.0
    }

    private fun getSignByIndex(idx: Int): ZodiacSign {
        return when (idx % 12) {
            0 -> ZodiacSign.ARIES
            1 -> ZodiacSign.TAURUS
            2 -> ZodiacSign.GEMINI
            3 -> ZodiacSign.CANCER
            4 -> ZodiacSign.LEO
            5 -> ZodiacSign.VIRGO
            6 -> ZodiacSign.LIBRA
            7 -> ZodiacSign.SCORPIO
            8 -> ZodiacSign.SAGITTARIUS
            9 -> ZodiacSign.CAPRICORN
            10 -> ZodiacSign.AQUARIUS
            else -> ZodiacSign.PISCES
        }
    }

    private fun getNakshatraName(deg: Double): String {
        val span = 360.0 / 27.0
        val idx = (deg / span).toInt() % 27
        return NAKSHATRAS[idx]
    }

    private fun getNakshatraPada(deg: Double): Int {
        val span = 360.0 / 27.0
        val rem = deg % span
        return min(4, (rem / (span / 4.0)).toInt() + 1)
    }
}
