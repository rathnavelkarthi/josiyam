package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.models.BirthProfile
import com.example.data.models.BirthTimeConfidence

@Entity(tableName = "birth_profiles")
data class BirthProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val dateOfBirth: String,
    val timeOfBirth: String,
    val timeConfidence: String,
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String,
    val isDefault: Boolean,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): BirthProfile {
        val conf = when (timeConfidence.lowercase()) {
            "exact" -> BirthTimeConfidence.EXACT
            "approximate" -> BirthTimeConfidence.APPROXIMATE
            else -> BirthTimeConfidence.UNKNOWN
        }
        return BirthProfile(
            id = id,
            name = name,
            dateOfBirth = dateOfBirth,
            timeOfBirth = timeOfBirth,
            timeConfidence = conf,
            placeName = placeName,
            latitude = latitude,
            longitude = longitude,
            timezoneId = timezoneId,
            isDefault = isDefault
        )
    }

    companion object {
        fun fromDomain(profile: BirthProfile): BirthProfileEntity {
            return BirthProfileEntity(
                id = profile.id,
                name = profile.name,
                dateOfBirth = profile.dateOfBirth,
                timeOfBirth = profile.timeOfBirth,
                timeConfidence = profile.timeConfidence.name,
                placeName = profile.placeName,
                latitude = profile.latitude,
                longitude = profile.longitude,
                timezoneId = profile.timezoneId,
                isDefault = profile.isDefault
            )
        }
    }
}
