package com.example.data.engine

import com.example.data.models.NumerologyReport

object NumerologyEngine {

    // Chaldean letter values (1 to 8, 9 is sacred and usually omitted from names directly unless compound sums to it)
    private val chaldeanMap = mapOf(
        'A' to 1, 'I' to 1, 'J' to 1, 'Q' to 1, 'Y' to 1,
        'B' to 2, 'K' to 2, 'R' to 2,
        'C' to 3, 'G' to 3, 'L' to 3, 'S' to 3,
        'D' to 4, 'M' to 4, 'T' to 4,
        'E' to 5, 'H' to 5, 'N' to 5, 'X' to 5,
        'U' to 6, 'V' to 6, 'W' to 6,
        'O' to 7, 'Z' to 7,
        'F' to 8, 'P' to 8
    )

    // Pythagorean letter values (1 to 9)
    private val pythagoreanMap = mapOf(
        'A' to 1, 'J' to 1, 'S' to 1,
        'B' to 2, 'K' to 2, 'T' to 2,
        'C' to 3, 'L' to 3, 'U' to 3,
        'D' to 4, 'M' to 4, 'V' to 4,
        'E' to 5, 'N' to 5, 'W' to 5,
        'F' to 6, 'O' to 6, 'X' to 6,
        'G' to 7, 'P' to 7, 'Y' to 7,
        'H' to 8, 'Q' to 8, 'Z' to 8,
        'I' to 9, 'R' to 9
    )

    fun calculate(fullName: String, dobString: String): NumerologyReport {
        val cleanName = fullName.uppercase().filter { it in 'A'..'Z' }
        
        // 1. Chaldean calculation
        var chaldeanCompound = 0
        for (char in cleanName) {
            chaldeanCompound += chaldeanMap[char] ?: 0
        }
        if (chaldeanCompound == 0) chaldeanCompound = 15 // default fallback
        
        var chaldeanSingle = reduceNumber(chaldeanCompound)

        // 2. Pythagorean calculation
        var pythSum = 0
        for (char in cleanName) {
            pythSum += pythagoreanMap[char] ?: 0
        }
        if (pythSum == 0) pythSum = 16
        var pythSingle = reduceNumber(pythSum)

        // 3. Birth Number (Day of birth) & 4. Life Path Number
        var birthDay = 24
        var birthMonth = 12
        var birthYear = 1997

        val dateFormats = listOf("dd MMM yyyy", "yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy", "MMM dd, yyyy", "yyyy/MM/dd")
        var dateParsed = false
        val cal = java.util.Calendar.getInstance()
        for (pattern in dateFormats) {
            try {
                val df = java.text.SimpleDateFormat(pattern, java.util.Locale.US)
                df.isLenient = false
                val parsed = df.parse(dobString.trim())
                if (parsed != null) {
                    cal.time = parsed
                    birthDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
                    birthMonth = cal.get(java.util.Calendar.MONTH) + 1
                    birthYear = cal.get(java.util.Calendar.YEAR)
                    dateParsed = true
                    break
                }
            } catch (_: Exception) {}
        }

        if (!dateParsed) {
            // Fallback parsing from digits
            val parts = dobString.trim().split(" ", "-", "/", ".")
            if (parts.size >= 3) {
                birthDay = parts[0].toIntOrNull() ?: 15
                birthYear = parts.find { it.length == 4 }?.toIntOrNull() ?: 1997
            }
        }

        val birthNumber = reduceToSingle(birthDay)

        // Life Path Number (Sum of all digits of day + month + year)
        val fullDigits = "$birthDay$birthMonth$birthYear"
        val fullDobSum = fullDigits.sumOf { it.digitToIntOrNull() ?: 0 }
        val lifePathNumber = if (fullDobSum == 11 || fullDobSum == 22 || fullDobSum == 33) {
            fullDobSum
        } else {
            reduceToSingle(fullDobSum)
        }

        return NumerologyReport(
            fullName = fullName.ifEmpty { "Aaditya Sharma" },
            dateOfBirth = dobString,
            chaldeanNameNumber = chaldeanSingle,
            chaldeanCompound = chaldeanCompound,
            chaldeanMeaning = getChaldeanMeaning(chaldeanSingle),
            pythagoreanExpressionNumber = pythSingle,
            pythagoreanMeaning = getPythagoreanMeaning(pythSingle),
            birthNumber = birthNumber,
            birthNumberMeaning = getBirthNumberMeaning(birthNumber),
            lifePathNumber = lifePathNumber,
            lifePathMeaning = getLifePathMeaning(lifePathNumber),
            tamilTransliterationNote = "Tamil script transliteration follows standard phoneme matching (e.g., Ka=1/2, Cha=3, Ta=4, Na=5, Pa=8, Ya=1).",
            compatibleNumbers = listOf((birthNumber % 9) + 1, ((birthNumber + 3) % 9) + 1)
        )
    }

    private fun reduceNumber(num: Int): Int {
        var n = num
        while (n > 9 && n != 11 && n != 22 && n != 33) {
            n = n.toString().sumOf { it.digitToIntOrNull() ?: 0 }
        }
        return if (n > 9 && n != 11 && n != 22 && n != 33) reduceNumber(n) else n
    }

    private fun reduceToSingle(num: Int): Int {
        var n = num
        while (n > 9) {
            n = n.toString().sumOf { it.digitToIntOrNull() ?: 0 }
        }
        return n
    }

    private fun getChaldeanMeaning(n: Int): String = when (n) {
        1 -> "Number of leadership, independence, and pioneering initiative (ruled by Surya)."
        2 -> "Number of diplomacy, intuition, cooperation, and artistic harmony (ruled by Chandra)."
        3 -> "Number of expression, optimism, creative wisdom, and Jupiterian expansion (ruled by Guru)."
        4 -> "Number of stability, meticulous organization, endurance, and structural discipline (ruled by Rahu/Ketu)."
        5 -> "Number of versatility, freedom, communication, and dynamic adaptability (ruled by Budha)."
        6 -> "Number of love, nurturing responsibility, domestic harmony, and aesthetic beauty (ruled by Shukra)."
        7 -> "Number of introspection, spiritual inquiry, analytical depth, and mystical wisdom (ruled by Ketu)."
        8 -> "Number of executive authority, karma, material manifestation, and disciplined leadership (ruled by Shani)."
        9 -> "Number of universal compassion, humanitarian vision, and completion (ruled by Mangala)."
        else -> "Harmonious vibration associated with spiritual and material balance."
    }

    private fun getPythagoreanMeaning(n: Int): String = when (n) {
        1 -> "The Independent Creator: Bold ambition and self-reliance."
        2 -> "The Peacemaker: Empathetic mediator and collaborative partner."
        3 -> "The Expressive Communicator: Artistic flair and inspiring social energy."
        4 -> "The Builder: Practical foundation-layer and dependable architect."
        5 -> "The Adventurer: Dynamic seeker of experience and progressive change."
        6 -> "The Caretaker: Compassionate guardian of home, family, and community."
        7 -> "The Seeker of Truth: Deep philosophical analyst and contemplative scholar."
        8 -> "The Powerhouse: Master of enterprise, financial acumen, and authority."
        9 -> "The Humanitarian: Generous benefactor devoted to universal upliftment."
        else -> "Balanced expression of inner talents and outward potential."
    }

    private fun getBirthNumberMeaning(n: Int): String = when (n) {
        1 -> "Core traits: Authoritative, determined, proud, and naturally magnetic."
        2 -> "Core traits: Gentle, perceptive, sensitive, and cooperative."
        3 -> "Core traits: Cheerful, articulate, creative, and enthusiastic."
        4 -> "Core traits: Diligent, methodical, reliable, and steadfast."
        5 -> "Core traits: Quick-witted, adventurous, inquiring, and adaptable."
        6 -> "Core traits: Warm-hearted, protective, artistic, and responsible."
        7 -> "Core traits: Contemplative, intuitive, scholarly, and refined."
        8 -> "Core traits: Ambitious, resilient, managerial, and structured."
        9 -> "Core traits: Courageous, idealistic, passionate, and broad-minded."
        else -> "Balanced intrinsic disposition."
    }

    private fun getLifePathMeaning(n: Int): String = when (n) {
        1 -> "Life Purpose: To pioneer new paths, develop sovereign self-confidence, and lead with integrity."
        2 -> "Life Purpose: To foster peace, mediate understanding, and cultivate deep partnerships."
        3 -> "Life Purpose: To uplift others through creative expression, writing, and inspired communication."
        4 -> "Life Purpose: To build enduring structures, establish order, and master practical skills."
        5 -> "Life Purpose: To champion freedom, embrace dynamic changes, and explore diverse perspectives."
        6 -> "Life Purpose: To serve family and community with devotion, healing, and aesthetic stewardship."
        7 -> "Life Purpose: To uncover profound spiritual truths, conduct research, and mentor seekers."
        8 -> "Life Purpose: To master material resources ethically, manage large enterprises, and exercise fair authority."
        9 -> "Life Purpose: To offer selfless service, practice universal empathy, and inspire global betterment."
        11 -> "Master Life Path 11: Intuitive spiritual illuminator and visionary inspiration for humanity."
        22 -> "Master Life Path 22: Master builder capable of manifesting grand visions into concrete reality."
        else -> "Life Purpose: Harmonious personal evolution and fulfillment of potential."
    }
}
