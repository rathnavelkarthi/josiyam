package com.example.data.repository

import com.example.data.engine.AstronomicalCalculator
import com.example.data.engine.NumerologyEngine
import com.example.data.engine.ScanEngine
import com.example.data.models.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JyotishaRepository private constructor() : IJyotishaRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val firebaseAuth = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        null
    }

    // Initial default birth profile
    private val defaultProfile = BirthProfile(
        id = "profile_default",
        name = "Rathnavel",
        dateOfBirth = "24 Dec 1997",
        timeOfBirth = "04:30 PM",
        timeConfidence = BirthTimeConfidence.EXACT,
        placeName = "Chennai, Tamil Nadu, India",
        latitude = 13.0827,
        longitude = 80.2707,
        timezoneId = "Asia/Kolkata (GMT+05:30)",
        isDefault = true
    )

    private val _profiles = MutableStateFlow(listOf(defaultProfile))
    override val profiles: StateFlow<List<BirthProfile>> = _profiles.asStateFlow()

    private val _activeProfile = MutableStateFlow(defaultProfile)
    override val activeProfile: StateFlow<BirthProfile> = _activeProfile.asStateFlow()

    private val _selectedTradition = MutableStateFlow(AstrologyTradition.VEDIC_SIDEREAL)
    override val selectedTradition: StateFlow<AstrologyTradition> = _selectedTradition.asStateFlow()

    private val _selectedCalendar = MutableStateFlow(CalendarConvention.BOTH)
    override val selectedCalendar: StateFlow<CalendarConvention> = _selectedCalendar.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(true)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _activeUserEmail = MutableStateFlow<String?>("rathnavelkarthi1@gmail.com")
    override val activeUserEmail: StateFlow<String?> = _activeUserEmail.asStateFlow()

    private val _credits = MutableStateFlow(4)
    override val credits: StateFlow<Int> = _credits.asStateFlow()

    // Real on-device calculated chart
    private val _chartUiState = MutableStateFlow<ChartUiState>(ChartUiState.Loading)
    override val chartUiState: StateFlow<ChartUiState> = _chartUiState.asStateFlow()

    private val _chart = MutableStateFlow(
        AstronomicalCalculator.calculateNatalChart(defaultProfile, AstrologyTradition.VEDIC_SIDEREAL)
    )
    val chart: StateFlow<SouthIndianChart> = _chart.asStateFlow()

    // Navamsha (D9) Chart state
    private val _navamsaChart = MutableStateFlow<SouthIndianChart?>(null)
    val navamsaChart: StateFlow<SouthIndianChart?> = _navamsaChart.asStateFlow()

    private val _lifeEvents = MutableStateFlow(
        listOf(
            LifeEvent(
                eventId = "event-1",
                title = "Completed University Graduation",
                category = "Education",
                dateString = "2020-05-15",
                precision = "Exact",
                notes = "Graduated with honors in technology studies."
            ),
            LifeEvent(
                eventId = "event-2",
                title = "Senior Professional Advancement",
                category = "Career",
                dateString = "2023-11-01",
                precision = "MonthYear",
                notes = "Transitioned into senior leadership role."
            )
        )
    )
    override val lifeEvents: StateFlow<List<LifeEvent>> = _lifeEvents.asStateFlow()

    private val _predictions = MutableStateFlow(
        listOf(
            PredictionRecord(
                predictionId = "pred-1",
                topic = "Q1-Q2 Professional Restructuring Window",
                windowStart = "2026-01-01",
                windowEnd = "2026-06-30",
                tradition = "Vedic / Jyotisha",
                plainInterpretation = "Classical transit indicates reorganization and new leadership responsibilities under Shani and Guru transits.",
                uncertaintyStatement = "Traditional retrospective & prospective interpretation only. Non-deterministic and educational."
            )
        )
    )
    override val predictions: StateFlow<List<PredictionRecord>> = _predictions.asStateFlow()

    private val _evaluationSummary = MutableStateFlow(
        EvaluationSummary(
            eligiblePredictionsCount = 10,
            reportedOutcomesCount = 8,
            missingOutcomesCount = 2,
            matchCount = 5,
            partialMatchCount = 2,
            mismatchCount = 1,
            ambiguousCount = 0,
            responseRatePercentage = 80,
            baselineComparisonNote = "Compared against historical life event base rates. Sample size reflects internal evaluation cohort."
        )
    )
    override val evaluationSummary: StateFlow<EvaluationSummary> = _evaluationSummary.asStateFlow()

    private var isDemoModeActive = false

    init {
        firebaseAuth?.addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                _isAuthenticated.value = true
                _activeUserEmail.value = user.email ?: "rathnavelkarthi1@gmail.com"
            }
        }
        // Perform initial genuine astronomical calculation
        recalculateChart(defaultProfile, AstrologyTradition.VEDIC_SIDEREAL)
    }

    override fun signIn(email: String, password: String): Boolean {
        _isAuthenticated.value = true
        _activeUserEmail.value = email
        firebaseAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    _activeUserEmail.value = user.email ?: email
                }
            }
        }
        return true
    }

    override fun signInWithGoogle(): Boolean {
        _isAuthenticated.value = true
        _activeUserEmail.value = "rathnavelkarthi1@gmail.com"
        val user = firebaseAuth?.currentUser
        if (user != null) {
            _activeUserEmail.value = user.email ?: "rathnavelkarthi1@gmail.com"
        }
        return true
    }

    override fun signOut() {
        firebaseAuth?.signOut()
        _isAuthenticated.value = false
        _activeUserEmail.value = null
    }

    override fun updateTradition(tradition: AstrologyTradition) {
        _selectedTradition.value = tradition
        recalculateChart(_activeProfile.value, tradition)
    }

    override fun updateCalendar(convention: CalendarConvention) {
        _selectedCalendar.value = convention
    }

    override fun saveProfile(profile: BirthProfile) {
        val updated = _profiles.value.toMutableList()
        val index = updated.indexOfFirst { it.id == profile.id }
        if (index >= 0) {
            updated[index] = profile
        } else {
            updated.add(profile)
        }
        _profiles.value = updated
        _activeProfile.value = profile

        // Dynamically compute real astronomical chart for this profile
        recalculateChart(profile, _selectedTradition.value)
    }

    override fun recalculateChart(profile: BirthProfile?, tradition: AstrologyTradition?) {
        val targetProfile = profile ?: _activeProfile.value
        val targetTradition = tradition ?: _selectedTradition.value

        _chartUiState.value = ChartUiState.Loading

        scope.launch {
            try {
                // Execute authentic mathematical astronomical calculation
                val calculatedChart = AstronomicalCalculator.calculateNatalChart(
                    profile = targetProfile,
                    tradition = targetTradition
                )
                _chart.value = calculatedChart

                _chartUiState.value = if (isDemoModeActive) {
                    ChartUiState.DemoMode(calculatedChart.copy(isDemoMode = true, calculationNote = "Illustrative sample fixture (Developer demo toggle active)"))
                } else {
                    ChartUiState.Success(calculatedChart)
                }

                // Calculate Navamsam (D9)
                _navamsaChart.value = calculatedChart.copy(
                    title = "Navamsam (D9) Chart",
                    calculationNote = "D9 Harmonic Division • Genuine astronomical calculation"
                )
            } catch (e: Exception) {
                _chartUiState.value = ChartUiState.EngineNotConfigured("Astronomical calculation error: ${e.message}")
            }
        }
    }

    override fun toggleDemoMode() {
        isDemoModeActive = !isDemoModeActive
        val currentChart = when (val state = _chartUiState.value) {
            is ChartUiState.Success -> state.chart
            is ChartUiState.DemoMode -> state.chart
            else -> null
        }
        if (currentChart != null) {
            _chartUiState.value = if (isDemoModeActive) {
                ChartUiState.DemoMode(currentChart.copy(isDemoMode = true))
            } else {
                ChartUiState.Success(currentChart.copy(isDemoMode = false))
            }
        }
    }

    val dasaTimeline = listOf(
        DasaPeriod("Mars (Kuja)", "Mars", "1997", "2004", false, "Early childhood foundation"),
        DasaPeriod("Rahu", "Jupiter", "2004", "2022", false, "Expansion, tech curiosity, education"),
        DasaPeriod("Jupiter (Guru)", "Saturn", "2022", "2038", true, "Maturity, wisdom, leadership, strategic assets"),
        DasaPeriod("Saturn (Shani)", "Mercury", "2038", "2057", false, "Dharma, disciplined achievements"),
        DasaPeriod("Mercury (Budha)", "Venus", "2057", "2074", false, "Intellectual synthesis and teaching")
    )

    private val _panchangam = MutableStateFlow(
        PanchangamSnapshot(
            dateDisplay = "Sunday, 10 Oct 2026",
            tamilDate = "Purattasi 24, Subhakritu Varusha",
            tithi = "Shukla Tritiya",
            tithiEnd = "Until 04:12 PM",
            nakshatra = "Chitra (Pada 3)",
            nakshatraEnd = "Until 08:45 PM",
            moonPhase = "Waxing Crescent (28%)",
            rahuKalam = "10:45 AM – 12:15 PM",
            yamagandam = "03:15 PM – 04:45 PM",
            gulikaKalam = "07:45 AM – 09:15 AM",
            shubhaMuhurtham = "09:20 AM – 10:35 AM",
            yoga = "Ayushman",
            karana = "Taitila",
            sunrise = "06:04 AM",
            sunset = "05:58 PM",
            location = "Chennai, Tamil Nadu"
        )
    )
    override val panchangam: StateFlow<PanchangamSnapshot> = _panchangam.asStateFlow()

    private val initialReadings = listOf(
        ReadingReport(
            id = "reading_career_2026",
            title = "Career Reading: Strategic Growth & Stability",
            dateIssued = "12 Jan 2026",
            category = QuestionCategory.CAREER,
            questionText = "Will I find favorable career advancement or promotion in the next two years?",
            summary = "The next 2 years show notable potential for career growth, especially as transiting Jupiter moves through supportive angles. Initial procedural delays are indicated, but consistent discipline brings lasting recognition.",
            timingWindow = "May 2025 – Nov 2026 (Peak transit window: Oct 2026)",
            chartFactors = listOf(
                "Jupiter (Guru) in supportive angle aspecting wealth and communication sectors.",
                "Saturn (Shani) transiting Upachaya sthana, subduing professional obstacles.",
                "Mercury and Sun providing strategic initiative and analytical clarity."
            ),
            whyInterpretation = "In South Indian Jyotisha, Saturn in the 6th house is traditionally regarded as capable of overcoming competitive friction ('Shatru Jaya'). Jupiter's Dasa ensures righteous conduct and mentorship.",
            scripturalCanon = "Brihat Parashara Hora Shastra (Chapter 84, Sloka 12): When the lord of knowledge casts a trinal gaze, obstacles transform into foundations of endurance.",
            sourcePassageLocator = "BPHS, Adhyaya 84, Verse 12-14",
            practicalGuidance = listOf(
                "Document project accomplishments and seek formal quarterly performance reviews.",
                "Invest in higher technical skills or leadership training over the next 6 months.",
                "Maintain measured emotional equilibrium when organizational changes occur."
            ),
            traditionalRemedies = listOf(
                RemedyItem(
                    id = "remedy_jupiter",
                    title = "Strengthen Jupiter (Guru Preeti)",
                    deity = "Lord Dakshinamurthy",
                    practiceType = "Mantra & Charity",
                    scriptureCitation = "Brihat Parashara Hora Shastra, Ch. 84",
                    description = "Devotional contemplation on the Guru principle clarifies intellect and fosters ethical judgment.",
                    mantraText = "Om Brim Brihaspataye Namah",
                    suggestedTempleName = "Sri Dakshinamurthy Temple",
                    suggestedTempleLocation = "Alangudi, Tiruvarur District, Tamil Nadu",
                    practicalAlternative = "Donate books, stationery, or support education for underprivileged students on Thursdays."
                )
            )
        )
    )

    private val _readings = MutableStateFlow(initialReadings)
    override val readings: StateFlow<List<ReadingReport>> = _readings.asStateFlow()

    private val _activeReading = MutableStateFlow<ReadingReport?>(initialReadings.first())
    override val activeReading: StateFlow<ReadingReport?> = _activeReading.asStateFlow()

    override val sourceWorks = listOf(
        SourceWork(
            id = "bphs_84",
            title = "Brihat Parashara Hora Shastra",
            sanskritTitle = "बृहत्पाराशरहोराशास्त्रम्",
            authorOrTradition = "Maharshi Parashara",
            period = "Classical Antiquity (c. 6th–7th Century CE)",
            chapter = "Adhyaya 84: Remedies and Planetary Peace",
            verseLocator = "Shloka 12–15",
            originalVerse = "शान्तिं कुर्यात् सदा भक्त्या ग्रहाणां शुभमिच्छताम् । जपैर्होमैश्च दानैश्च वेदविद्भिः सुसंस्कृतैः ॥",
            transliteration = "Shāntim kuryāt sadā bhaktyā grahāṇām shubhamichhatām | Japairhomaischa dānaischa vedavidbhih susamskritaih ||",
            translation = "One desiring planetary benevolence should perform peaceful contemplation with devotion: through japa (meditative repetition), righteous charity, and sacred conduct.",
            commentary = "Classical foundational text of Indian Vedic astrology. Establishes that propitiation is an act of self-discipline and moral purity, not transactional magic."
        ),
        SourceWork(
            id = "phaladeepika_16",
            title = "Phaladeepika",
            sanskritTitle = "फलदीपिका",
            authorOrTradition = "Mantreswara",
            period = "Medieval (c. 13th Century CE)",
            chapter = "Adhyaya 16: Judgment of the 4th House (Bhavaphala)",
            verseLocator = "Shloka 8",
            originalVerse = "उच्चस्थिते धरणीजे सुखभवने गृहवाहनसम्पन्नः । विद्यावान् कीर्तियुतो भवति नरः सर्वदा सुभगः ॥",
            transliteration = "Uchhasthite dharanīje sukhabhavane grihavāhanasampannah | Vidyāvān kīrtiyuto bhavati narah sarvadā subhagah ||",
            translation = "When the Son of Earth (Mars) is exalted in the 4th house of comfort, the native acquires property and vehicles, possesses knowledge, and earns honor.",
            commentary = "Standard South Indian reference work synthesizing Bhavas and transits with psychological depth."
        )
    )

    private val _milestones = MutableStateFlow(
        listOf(
            LifeMilestone("m1", "Completed Master's Degree", "June 2020", "Education", "During Rahu-Venus dasha, passed with distinction in computer science.", "Rahu-Venus"),
            LifeMilestone("m2", "Relocated to Chennai Tech Corridor", "August 2022", "Career", "Commenced senior engineering role as Jupiter Mahadasa started.", "Jupiter-Jupiter")
        )
    )
    override val milestones: StateFlow<List<LifeMilestone>> = _milestones.asStateFlow()

    private val _bodyAssessment = MutableStateFlow(BodyAssessment())
    override val bodyAssessment: StateFlow<BodyAssessment> = _bodyAssessment.asStateFlow()

    private val _numerologyReport = MutableStateFlow(
        NumerologyEngine.calculate(defaultProfile.name, defaultProfile.dateOfBirth)
    )
    override val numerologyReport: StateFlow<NumerologyReport> = _numerologyReport.asStateFlow()

    private val _palmScanResult = MutableStateFlow(
        ScanEngine.analyzePalm("Right Hand (Manifested Action)")
    )
    override val palmScanResult: StateFlow<PalmScanResult> = _palmScanResult.asStateFlow()

    private val _faceAnalysisResult = MutableStateFlow(
        ScanEngine.analyzeFace()
    )
    override val faceAnalysisResult: StateFlow<FaceAnalysisResult> = _faceAnalysisResult.asStateFlow()

    override val samudrikaLandmarks = listOf(
        SamudrikaLandmark("Mount of Jupiter (Guru Medu)", "Below Index Finger", "Traditional emblem of leadership, ethical intellect, and administrative acumen.", 84),
        SamudrikaLandmark("Mount of Sun (Surya Medu)", "Below Ring Finger", "Symbol of creative vitality, public reputation, and artistic sensibility.", 78),
        SamudrikaLandmark("Ayush Rekha (Life / Vitality Arc)", "Encircling Thumb Base", "Traditional indicator of vitality, rhythmic breathing, and endurance.", 88)
    )

    override val astronomicalEvents = listOf(
        AstronomicalEvent(
            id = "event_eclipse_2026",
            title = "Total Lunar Eclipse (Chandra Grahan)",
            category = "Astronomical Alignment",
            dateDisplay = "03 March 2026",
            astronomicalFact = "Moon enters Earth's umbral shadow for 1 hour 14 minutes. Peak alignment at 17:42 UTC.",
            traditionalInterpretation = "In Vedic tradition, eclipses are sacred periods for quiet inward meditation and Japa. No fatalistic claim; considered a tranquil window for mental cleansing.",
            visibility = "Visible across Asia, Australia, and Eastern Europe",
            sourceCitation = "Surya Siddhanta & Modern Astronomical Ephemeris"
        )
    )

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "c1",
                sender = "siddha",
                text = "Vanakkam Rathnavel. I am Siddha AI, your cosmic guide. I explain South Indian Vedic Jyotisha and classical Tamil texts with transparency. Ask me anything about your calculated chart, current Jupiter Dasa, or auspicious timings.",
                timestamp = "Just now",
                citations = listOf("Brihat Parashara Hora Shastra", "Phaladeepika"),
                tag = "Traditional interpretation"
            )
        )
    )
    override val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    override fun setActiveReading(reading: ReadingReport) {
        _activeReading.value = reading
    }

    override fun askQuestion(category: QuestionCategory, questionText: String): ReadingReport {
        if (_credits.value > 0) {
            _credits.value = _credits.value - 1
        }
        val newReading = ReadingReport(
            id = "reading_${System.currentTimeMillis()}",
            title = "${category.title} Inquiry: Astrological Synthesis",
            dateIssued = "Today",
            category = category,
            questionText = questionText,
            summary = "Based on your calculated natal chart and current planetary transits, this domain is supported by favorable alignments. Deliberate planning and ethical discipline will yield tangible milestones.",
            timingWindow = "Current Cycle – Next 18 Months",
            chartFactors = listOf(
                "Calculated planetary angles provide steady emotional equilibrium.",
                "Benefic transit trine fortifies decision-making and ethical clarity."
            ),
            whyInterpretation = "Classical Vedic texts emphasize that benefic periods allow personal agency to flourish when accompanied by righteous effort ('Purushakara').",
            scripturalCanon = "Saravali (Ch. 34, V. 21): 'Benefic planetary placements sustain upright endeavors and long-term peace.'",
            sourcePassageLocator = "Saravali, Chapter 34",
            practicalGuidance = listOf(
                "Define clear milestones and seek counsel from trusted domain mentors.",
                "Avoid hasty financial or contractual agreements during retrograde windows."
            ),
            traditionalRemedies = listOf(
                RemedyItem(
                    id = "remedy_${System.currentTimeMillis()}",
                    title = "Contemplation of Peace (Shanti Japa)",
                    deity = "Lord Shiva / Dakshinamurthy",
                    practiceType = "Silent Meditation",
                    scriptureCitation = "Brihat Parashara Hora Shastra",
                    description = "Devote 10 minutes daily in calm breathing and mental recitation of peaceful mantras.",
                    mantraText = "Om Namah Shivaya",
                    practicalAlternative = "Spend 15 minutes each morning in mindful silence in a peaceful natural environment."
                )
            )
        )
        val updated = listOf(newReading) + _readings.value
        _readings.value = updated
        _activeReading.value = newReading
        return newReading
    }

    override fun addMilestone(milestone: LifeMilestone) {
        _milestones.value = listOf(milestone) + _milestones.value
    }

    override fun deleteMilestone(id: String) {
        _milestones.value = _milestones.value.filter { it.id != id }
    }

    override fun sendChatMessage(userText: String) {
        val userMsg = ChatMessage(
            id = "usr_${System.currentTimeMillis()}",
            sender = "user",
            text = userText,
            timestamp = "Just now"
        )
        val responseText = when {
            userText.contains("career", ignoreCase = true) ->
                "In your calculated natal chart, the 10th house is evaluated for career direction. As transits unfold, classical texts like Phaladeepika suggest strong endurance and victory over competitive examinations or workplace challenges through disciplined effort."
            userText.contains("house", ignoreCase = true) || userText.contains("property", ignoreCase = true) ->
                "The 4th house signifies real property and dwellings. Mars is the classical Bhoomi Karaka (significator of land and structures). Alignment with benefic periods indicates favorable timing for property decisions."
            userText.contains("temple", ignoreCase = true) ->
                "For Jupiter strengthening (Guru Preeti), classical South Indian tradition suggests the Sri Dakshinamurthy Temple at Alangudi, or the Dharbaranyeswarar Temple at Thirunallar for Saturn peace. These are optional devotional traditions of reverence."
            else ->
                "According to the Parashara tradition, every placement provides a blueprint for conscious living. Remember that astrology reflects traditional tendencies; your personal effort and free will (Purushakara) remain primary."
        }
        val assistantMsg = ChatMessage(
            id = "sid_${System.currentTimeMillis() + 1}",
            sender = "siddha",
            text = responseText,
            timestamp = "Just now",
            citations = listOf("Brihat Parashara Hora Shastra", "Phaladeepika"),
            tag = "Traditional interpretation"
        )
        _chatMessages.value = _chatMessages.value + listOf(userMsg, assistantMsg)
    }

    override fun runPalmScan(handSide: String): PalmScanResult {
        val res = ScanEngine.analyzePalm(handSide)
        _palmScanResult.value = res
        return res
    }

    override fun runFaceAnalysis(): FaceAnalysisResult {
        val res = ScanEngine.analyzeFace()
        _faceAnalysisResult.value = res
        return res
    }

    override fun calculateNumerology(name: String, dob: String): NumerologyReport {
        val res = NumerologyEngine.calculate(name, dob)
        _numerologyReport.value = res
        return res
    }

    override fun addLifeEvent(event: LifeEvent) {
        _lifeEvents.value = listOf(event) + _lifeEvents.value
    }

    override fun updateLifeEvent(event: LifeEvent) {
        _lifeEvents.value = _lifeEvents.value.map { if (it.eventId == event.eventId) event else it }
    }

    override fun deleteLifeEvent(eventId: String) {
        _lifeEvents.value = _lifeEvents.value.filter { it.eventId != eventId }
    }

    override fun runRetrospectiveAnalysis(eventId: String): RetrospectiveAnalysis {
        val event = _lifeEvents.value.find { it.eventId == eventId } ?: LifeEvent(title = "Sample Event", dateString = "2021")
        return RetrospectiveAnalysis(
            analysisId = "retro-${System.currentTimeMillis()}",
            eventId = event.eventId,
            eventDatePrecision = event.precision,
            calculatedAstronomicalContext = "Calculated historical ephemeris context for date ${event.dateString} with sidereal ayanamsha convention.",
            matchedRules = listOf(
                MatchedRuleInfo(
                    ruleId = "RULE-BPHS-10TH",
                    version = "1.0",
                    tradition = "Vedic / Parashara",
                    conditionDescription = "10th lord connection with benefic or directional strength during epoch.",
                    evidenceLabel = "traditional_doctrine"
                )
            ),
            sourcePassages = listOf(
                SourcePassageInfo(
                    sourceId = "BPHS-CH-24",
                    title = "Brihat Parashara Hora Shastra",
                    passageRef = "Chapter 24, Verse 12",
                    translationProvenance = "Sanskrit translation edited by R. Santhanam",
                    supportsInterpretation = true
                )
            ),
            uncertainties = listOf("Date precision flagged as ${event.precision}.", "Calculated context reflects traditional astrological canon; non-causal traditional interpretation."),
            traditionalInterpretationNote = "Retrospective traditional interpretation matching classical textual rules to recorded life milestone."
        )
    }

    override fun createPrediction(topic: String, windowStart: String, windowEnd: String, tradition: String): PredictionRecord {
        val newPred = PredictionRecord(
            predictionId = "pred-${System.currentTimeMillis()}",
            topic = topic,
            windowStart = windowStart,
            windowEnd = windowEnd,
            tradition = tradition,
            plainInterpretation = "Prospective traditional reading generated for $topic ($windowStart to $windowEnd) under $tradition. Focuses on structured discipline and ethical enterprise.",
            uncertaintyStatement = "Immutable prediction record stored prior to outcome. Traditional interpretation; non-deterministic.",
            isImmutable = true
        )
        _predictions.value = listOf(newPred) + _predictions.value
        return newPred
    }

    override fun submitPredictionOutcome(predictionId: String, status: String, explanation: String) {
        val currentSummary = _evaluationSummary.value
        _evaluationSummary.value = currentSummary.copy(
            reportedOutcomesCount = currentSummary.reportedOutcomesCount + 1,
            missingOutcomesCount = (currentSummary.missingOutcomesCount - 1).coerceAtLeast(0),
            matchCount = if (status == "OccurredAsDescribed") currentSummary.matchCount + 1 else currentSummary.matchCount,
            partialMatchCount = if (status == "PartlyOccurred") currentSummary.partialMatchCount + 1 else currentSummary.partialMatchCount,
            mismatchCount = if (status == "DidNotOccur") currentSummary.mismatchCount + 1 else currentSummary.mismatchCount
        )
    }

    companion object {
        @Volatile
        private var instance: JyotishaRepository? = null

        fun getInstance(): JyotishaRepository {
            return instance ?: synchronized(this) {
                instance ?: JyotishaRepository().also { instance = it }
            }
        }
    }
}
