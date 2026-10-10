package com.example.data.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(
    val code: String,
    val englishName: String,
    val nativeName: String
) {
    ENGLISH("en", "English", "English"),
    TAMIL("ta", "Tamil", "தமிழ்"),
    HINDI("hi", "Hindi", "हिन्दी"),
    TELUGU("te", "Telugu", "తెలుగు");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.ENGLISH }

object JyotishaStrings {

    fun getAppName(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "ஜோதிஷம் (Jyotisha)"
        AppLanguage.HINDI -> "ज्योतिष (Jyotisha)"
        AppLanguage.TELUGU -> "జ్యోతిషం (Jyotisha)"
        AppLanguage.ENGLISH -> "Jyotisha"
    }

    fun getGreeting(lang: AppLanguage, name: String): String = when (lang) {
        AppLanguage.TAMIL -> "வணக்கம், $name 👋"
        AppLanguage.HINDI -> "नमस्ते, $name 👋"
        AppLanguage.TELUGU -> "నమస్కారం, $name 👋"
        AppLanguage.ENGLISH -> "Vanakkam, $name 👋"
    }

    fun getSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "உங்கள் வேத அண்ட வரைபடம் தயாராக உள்ளது"
        AppLanguage.HINDI -> "आपका वैदिक आकाशीय खाका तैयार है"
        AppLanguage.TELUGU -> "మీ వేద ఖగోళ బ్లూప్రింట్ సిద్ధంగా ఉంది"
        AppLanguage.ENGLISH -> "Your cosmic blueprint is ready"
    }

    // Navigation Labels
    fun navHome(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "முகப்பு"
        AppLanguage.HINDI -> "मुख्य"
        AppLanguage.TELUGU -> "హోమ్"
        AppLanguage.ENGLISH -> "Home"
    }

    fun navChart(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "ராசி கட்டம்"
        AppLanguage.HINDI -> "कुंडली"
        AppLanguage.TELUGU -> "కుండలి"
        AppLanguage.ENGLISH -> "Chart"
    }

    fun navPanchangam(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "பஞ்சாங்கம்"
        AppLanguage.HINDI -> "पंचांग"
        AppLanguage.TELUGU -> "పంచాంగం"
        AppLanguage.ENGLISH -> "Panchangam"
    }

    fun navAsk(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "சித்தர் AI"
        AppLanguage.HINDI -> "सिद्ध एआई"
        AppLanguage.TELUGU -> "సిద్ధ ఏఐ"
        AppLanguage.ENGLISH -> "Siddha AI"
    }

    fun navLibrary(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "நூலகம்"
        AppLanguage.HINDI -> "ग्रंथालय"
        AppLanguage.TELUGU -> "గ్రంథాలయం"
        AppLanguage.ENGLISH -> "Library"
    }

    fun navProfile(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "சுயவிவரம்"
        AppLanguage.HINDI -> "प्रोफ़ाइल"
        AppLanguage.TELUGU -> "ప్రొఫైల్"
        AppLanguage.ENGLISH -> "Profile"
    }

    // Common Buttons
    fun btnContinue(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "தொடர்க →"
        AppLanguage.HINDI -> "जारी रखें →"
        AppLanguage.TELUGU -> "కొనసాగించు →"
        AppLanguage.ENGLISH -> "Continue →"
    }

    fun btnSave(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "சேமிக்கவும்"
        AppLanguage.HINDI -> "सहेजें"
        AppLanguage.TELUGU -> "భద్రపరచు"
        AppLanguage.ENGLISH -> "Save"
    }

    fun btnSignOut(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "வெளியேறு"
        AppLanguage.HINDI -> "साइन आउट"
        AppLanguage.TELUGU -> "సైన్ అవుట్"
        AppLanguage.ENGLISH -> "Sign Out"
    }

    fun btnSignIn(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "உள்நுழைக"
        AppLanguage.HINDI -> "साइन इन करें"
        AppLanguage.TELUGU -> "సైన్ ఇన్"
        AppLanguage.ENGLISH -> "Sign In"
    }

    // Panchangam Limb Titles
    fun tithiLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "திதி"
        AppLanguage.HINDI -> "तिथि"
        AppLanguage.TELUGU -> "తిథి"
        AppLanguage.ENGLISH -> "Tithi"
    }

    fun nakshatraLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "நட்சத்திரம்"
        AppLanguage.HINDI -> "नक्षत्र"
        AppLanguage.TELUGU -> "నక్షత్రం"
        AppLanguage.ENGLISH -> "Nakshatra"
    }

    fun yogaLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "யோகம்"
        AppLanguage.HINDI -> "योग"
        AppLanguage.TELUGU -> "యోగం"
        AppLanguage.ENGLISH -> "Yoga"
    }

    fun karanaLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "கரணம்"
        AppLanguage.HINDI -> "करण"
        AppLanguage.TELUGU -> "కరణం"
        AppLanguage.ENGLISH -> "Karana"
    }

    fun rahuKalamLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "இராகு காலம்"
        AppLanguage.HINDI -> "राहु काल"
        AppLanguage.TELUGU -> "రాహు కాలం"
        AppLanguage.ENGLISH -> "Rahu Kalam"
    }

    fun yamagandamLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.TAMIL -> "எமகண்டம்"
        AppLanguage.HINDI -> "यमगंड"
        AppLanguage.TELUGU -> "యమగండం"
        AppLanguage.ENGLISH -> "Yamagandam"
    }

    // Sign Translations
    fun getSignName(lang: AppLanguage, sanskrit: String): String {
        return when (sanskrit.lowercase()) {
            "mesha", "aries" -> when (lang) {
                AppLanguage.TAMIL -> "மேஷம்"
                AppLanguage.HINDI -> "मेष"
                AppLanguage.TELUGU -> "మేషం"
                AppLanguage.ENGLISH -> "Aries (Mesha)"
            }
            "rishabha", "taurus" -> when (lang) {
                AppLanguage.TAMIL -> "ரிஷபம்"
                AppLanguage.HINDI -> "वृषभ"
                AppLanguage.TELUGU -> "వృషభం"
                AppLanguage.ENGLISH -> "Taurus (Rishabha)"
            }
            "mithuna", "gemini" -> when (lang) {
                AppLanguage.TAMIL -> "மிதுனம்"
                AppLanguage.HINDI -> "मिथुन"
                AppLanguage.TELUGU -> "మిథునం"
                AppLanguage.ENGLISH -> "Gemini (Mithuna)"
            }
            "kataka", "cancer" -> when (lang) {
                AppLanguage.TAMIL -> "கடகம்"
                AppLanguage.HINDI -> "कर्क"
                AppLanguage.TELUGU -> "కర్కాటకం"
                AppLanguage.ENGLISH -> "Cancer (Kataka)"
            }
            "simha", "leo" -> when (lang) {
                AppLanguage.TAMIL -> "சிம்மம்"
                AppLanguage.HINDI -> "सिंह"
                AppLanguage.TELUGU -> "సింహం"
                AppLanguage.ENGLISH -> "Leo (Simha)"
            }
            "kanya", "virgo" -> when (lang) {
                AppLanguage.TAMIL -> "கன்னி"
                AppLanguage.HINDI -> "कन्या"
                AppLanguage.TELUGU -> "కన్య"
                AppLanguage.ENGLISH -> "Virgo (Kanya)"
            }
            "tula", "thula", "libra" -> when (lang) {
                AppLanguage.TAMIL -> "துலாம்"
                AppLanguage.HINDI -> "तुला"
                AppLanguage.TELUGU -> "తుల"
                AppLanguage.ENGLISH -> "Libra (Tula)"
            }
            "vrischika", "scorpio" -> when (lang) {
                AppLanguage.TAMIL -> "விருச்சிகம்"
                AppLanguage.HINDI -> "वृश्चिक"
                AppLanguage.TELUGU -> "వృశ్చికం"
                AppLanguage.ENGLISH -> "Scorpio (Vrischika)"
            }
            "dhanu", "sagittarius" -> when (lang) {
                AppLanguage.TAMIL -> "தனுசு"
                AppLanguage.HINDI -> "धनु"
                AppLanguage.TELUGU -> "ధనుస్సు"
                AppLanguage.ENGLISH -> "Sagittarius (Dhanu)"
            }
            "makara", "capricorn" -> when (lang) {
                AppLanguage.TAMIL -> "மகரம்"
                AppLanguage.HINDI -> "मकर"
                AppLanguage.TELUGU -> "మకరం"
                AppLanguage.ENGLISH -> "Capricorn (Makara)"
            }
            "kumbha", "aquarius" -> when (lang) {
                AppLanguage.TAMIL -> "கும்பம்"
                AppLanguage.HINDI -> "कुंभ"
                AppLanguage.TELUGU -> "కుంభం"
                AppLanguage.ENGLISH -> "Aquarius (Kumbha)"
            }
            "meena", "pisces" -> when (lang) {
                AppLanguage.TAMIL -> "மீனம்"
                AppLanguage.HINDI -> "मीन"
                AppLanguage.TELUGU -> "మీనం"
                AppLanguage.ENGLISH -> "Pisces (Meena)"
            }
            else -> sanskrit
        }
    }
}
