package com.example.data.models

data class PanchangamSnapshot(
    val dateDisplay: String, // e.g. "Sunday, 10 Oct 2026"
    val tamilDate: String, // e.g. "Purattasi 24, Subhakritu Varusha"
    val tithi: String, // e.g. "Shukla Tritiya"
    val tithiEnd: String, // e.g. "Until 04:12 PM"
    val nakshatra: String, // e.g. "Chitra (Pada 3)"
    val nakshatraEnd: String, // e.g. "Until 08:45 PM"
    val moonPhase: String, // e.g. "Waxing Crescent (28%)"
    val rahuKalam: String, // e.g. "10:45 AM – 12:15 PM"
    val yamagandam: String, // e.g. "03:15 PM – 04:45 PM"
    val gulikaKalam: String, // e.g. "07:45 AM – 09:15 AM"
    val shubhaMuhurtham: String, // e.g. "09:20 AM – 10:35 AM"
    val yoga: String, // e.g. "Ayushman"
    val karana: String, // e.g. "Taitila"
    val sunrise: String, // e.g. "06:04 AM"
    val sunset: String, // e.g. "05:58 PM"
    val location: String = "Chennai, Tamil Nadu"
)

data class AstronomicalEvent(
    val id: String,
    val title: String,
    val category: String, // e.g. "Eclipse", "Planetary Transit", "Lunar Phase"
    val dateDisplay: String,
    val astronomicalFact: String,
    val traditionalInterpretation: String,
    val visibility: String,
    val sourceCitation: String
)
