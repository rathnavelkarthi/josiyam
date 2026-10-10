package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.BirthProfile
import com.example.data.models.BirthTimeConfidence
import com.example.data.repository.BirthProfileRepository
import com.example.data.repository.JyotishaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BirthProfileSetupUiState(
    val name: String = "",
    val dateOfBirth: String = "",
    val timeOfBirth: String = "",
    val confidence: BirthTimeConfidence = BirthTimeConfidence.EXACT,
    val placeName: String = "Chennai, Tamil Nadu, India",
    val latitude: Double = 13.0827,
    val longitude: Double = 80.2707,
    val timezoneId: String = "Asia/Kolkata (GMT+05:30)",
    val isDefault: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class BirthProfileSetupViewModel(
    private val birthProfileRepo: BirthProfileRepository? = null,
    private val mainRepo: JyotishaRepository = JyotishaRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BirthProfileSetupUiState())
    val uiState: StateFlow<BirthProfileSetupUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun updateDateOfBirth(dob: String) {
        _uiState.value = _uiState.value.copy(dateOfBirth = dob, errorMessage = null)
    }

    fun updateTimeOfBirth(tob: String) {
        _uiState.value = _uiState.value.copy(timeOfBirth = tob, errorMessage = null)
    }

    fun updateConfidence(conf: BirthTimeConfidence) {
        _uiState.value = _uiState.value.copy(confidence = conf)
    }

    fun updateLocation(placeName: String, lat: Double, lng: Double, tzId: String) {
        _uiState.value = _uiState.value.copy(
            placeName = placeName,
            latitude = lat,
            longitude = lng,
            timezoneId = tzId
        )
    }

    fun fetchAndCacheUserProfiles(context: Context, onProfilesLoaded: (List<BirthProfile>) -> Unit) {
        viewModelScope.launch {
            val repo = birthProfileRepo ?: BirthProfileRepository.getInstance(context)
            val result = repo.fetchProfiles()
            if (result.isSuccess) {
                val profiles = result.getOrDefault(emptyList())
                if (profiles.isNotEmpty()) {
                    profiles.forEach { profile ->
                        mainRepo.saveProfile(profile)
                    }
                    onProfilesLoaded(profiles)
                }
            }
        }
    }

    fun saveProfile(context: Context, onComplete: (BirthProfile) -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please enter native profile name.")
            return
        }

        _uiState.value = state.copy(isSaving = true, errorMessage = null)

        val newProfile = BirthProfile(
            id = "profile_${System.currentTimeMillis()}",
            name = state.name.trim(),
            dateOfBirth = if (state.dateOfBirth.isBlank()) "01 Jan 2000" else state.dateOfBirth.trim(),
            timeOfBirth = if (state.timeOfBirth.isBlank()) "12:00 PM" else state.timeOfBirth.trim(),
            timeConfidence = state.confidence,
            placeName = state.placeName,
            latitude = state.latitude,
            longitude = state.longitude,
            timezoneId = state.timezoneId,
            isDefault = state.isDefault
        )

        viewModelScope.launch {
            // Save in main repository (local Room cache + state flow)
            mainRepo.completeOnboarding(newProfile)

            // Save in Firestore if repository available
            val repo = birthProfileRepo ?: BirthProfileRepository.getInstance(context)
            repo.saveProfile(newProfile)

            _uiState.value = _uiState.value.copy(isSaving = false, isSuccess = true)
            onComplete(newProfile)
        }
    }
}
