package com.example.data.repository

import com.example.data.models.*
import kotlinx.coroutines.flow.StateFlow

interface IJyotishaRepository {
    val activeProfile: StateFlow<BirthProfile>
    val profiles: StateFlow<List<BirthProfile>>
    val chartUiState: StateFlow<ChartUiState>
    val selectedTradition: StateFlow<AstrologyTradition>
    val selectedCalendar: StateFlow<CalendarConvention>
    val isAuthenticated: StateFlow<Boolean>
    val activeUserEmail: StateFlow<String?>
    val panchangam: StateFlow<PanchangamSnapshot>
    val readings: StateFlow<List<ReadingReport>>
    val activeReading: StateFlow<ReadingReport?>
    val credits: StateFlow<Int>
    val sourceWorks: List<SourceWork>
    val milestones: StateFlow<List<LifeMilestone>>
    val bodyAssessment: StateFlow<BodyAssessment>
    val samudrikaLandmarks: List<SamudrikaLandmark>
    val numerologyReport: StateFlow<NumerologyReport>
    val palmScanResult: StateFlow<PalmScanResult>
    val faceAnalysisResult: StateFlow<FaceAnalysisResult>
    val astronomicalEvents: List<AstronomicalEvent>
    val chatMessages: StateFlow<List<ChatMessage>>
    val lifeEvents: StateFlow<List<LifeEvent>>
    val predictions: StateFlow<List<PredictionRecord>>
    val evaluationSummary: StateFlow<EvaluationSummary>

    fun signIn(email: String, password: String): Boolean
    fun signInWithGoogle(): Boolean
    fun signOut()
    fun updateTradition(tradition: AstrologyTradition)
    fun updateCalendar(convention: CalendarConvention)
    fun saveProfile(profile: BirthProfile)
    fun recalculateChart(profile: BirthProfile? = null, tradition: AstrologyTradition? = null)
    fun toggleDemoMode()
    fun setActiveReading(reading: ReadingReport)
    fun askQuestion(category: QuestionCategory, questionText: String): ReadingReport
    fun addMilestone(milestone: LifeMilestone)
    fun deleteMilestone(id: String)
    fun sendChatMessage(userText: String)
    fun runPalmScan(handSide: String): PalmScanResult
    fun runFaceAnalysis(): FaceAnalysisResult
    fun calculateNumerology(name: String, dob: String): NumerologyReport
    fun addLifeEvent(event: LifeEvent)
    fun updateLifeEvent(event: LifeEvent)
    fun deleteLifeEvent(eventId: String)
    fun runRetrospectiveAnalysis(eventId: String): RetrospectiveAnalysis
    fun createPrediction(topic: String, windowStart: String, windowEnd: String, tradition: String): PredictionRecord
    fun submitPredictionOutcome(predictionId: String, status: String, explanation: String)
}
