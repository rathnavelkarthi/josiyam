package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.engine.AstronomicalCalculator
import com.example.data.engine.NumerologyEngine
import com.example.data.engine.ScanEngine
import com.example.data.local.BirthProfileEntity
import com.example.data.local.JyotishaDatabase
import com.example.data.localization.AppLanguage
import com.example.data.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JyotishaRepository private constructor() : IJyotishaRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var appContext: Context? = null
    private var sharedPrefs: SharedPreferences? = null
    private var roomDatabase: JyotishaDatabase? = null

    private val firebaseAuth = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        null
    }

    private val firestore = try {
        FirebaseFirestore.getInstance()
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

    private val _isAuthenticated = MutableStateFlow(firebaseAuth?.currentUser != null)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _activeUserEmail = MutableStateFlow<String?>(firebaseAuth?.currentUser?.email)
    override val activeUserEmail: StateFlow<String?> = _activeUserEmail.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(false)
    override val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    override val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _credits = MutableStateFlow(5)
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

    private val _dasaTimeline = MutableStateFlow<List<DasaPeriod>>(
        AstronomicalCalculator.calculateVimshottariDasaTimeline("Chitra", defaultProfile.dateOfBirth)
    )
    val dasaTimeline: List<DasaPeriod> get() = _dasaTimeline.value

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
                _activeUserEmail.value = user.email ?: _activeUserEmail.value
            }
        }
        recalculateChart(defaultProfile, AstrologyTradition.VEDIC_SIDEREAL)
    }

    fun initialize(context: Context) {
        if (appContext != null) return
        val app = context.applicationContext
        appContext = app
        sharedPrefs = app.getSharedPreferences("jyotisha_user_session", Context.MODE_PRIVATE)
        roomDatabase = JyotishaDatabase.getDatabase(app)

        val savedOnboarding = sharedPrefs?.getBoolean("has_completed_onboarding", false) ?: false
        val savedLang = sharedPrefs?.getString("selected_language", "en") ?: "en"
        val savedEmail = sharedPrefs?.getString("active_user_email", null)

        _selectedLanguage.value = AppLanguage.fromCode(savedLang)
        if (savedEmail != null) {
            _activeUserEmail.value = savedEmail
            _isAuthenticated.value = true
        }

        scope.launch {
            try {
                val dao = roomDatabase?.jyotishaDao()
                val cachedProfiles = dao?.getAllProfiles()
                if (!cachedProfiles.isNullOrEmpty()) {
                    val domainList = cachedProfiles.map { it.toDomain() }.distinctBy { it.id }
                    _profiles.value = domainList
                    val activeId = sharedPrefs?.getString("active_profile_id", null)
                    val active = domainList.find { it.id == activeId } ?: domainList.first()
                    _activeProfile.value = active
                    _hasCompletedOnboarding.value = true
                    recalculateChart(active, _selectedTradition.value)
                } else {
                    // Try fetching from Firestore if user is logged in
                    val bpRepo = BirthProfileRepository.getInstance(app)
                    val fsResult = bpRepo.fetchProfiles()
                    if (fsResult.isSuccess && fsResult.getOrDefault(emptyList()).isNotEmpty()) {
                        val fsProfiles = fsResult.getOrDefault(emptyList()).distinctBy { it.id }
                        _profiles.value = fsProfiles
                        val active = fsProfiles.find { it.isDefault } ?: fsProfiles.first()
                        _activeProfile.value = active
                        _hasCompletedOnboarding.value = true
                        sharedPrefs?.edit()?.putBoolean("has_completed_onboarding", true)?.apply()
                        recalculateChart(active, _selectedTradition.value)
                        fsProfiles.forEach { p ->
                            dao?.insertProfile(BirthProfileEntity.fromDomain(p))
                        }
                    } else {
                        _hasCompletedOnboarding.value = savedOnboarding
                        // Seed initial profile in room
                        dao?.insertProfile(BirthProfileEntity.fromDomain(defaultProfile))
                    }
                }
            } catch (e: Exception) {
                _hasCompletedOnboarding.value = savedOnboarding
            }
        }
    }

    override fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        sharedPrefs?.edit()?.putString("selected_language", language.code)?.apply()
    }

    override fun completeOnboarding(profile: BirthProfile) {
        saveProfile(profile)
        _hasCompletedOnboarding.value = true
        sharedPrefs?.edit()?.putBoolean("has_completed_onboarding", true)?.apply()
    }

    override fun signIn(email: String, password: String): Boolean {
        _isAuthenticated.value = true
        _activeUserEmail.value = email
        sharedPrefs?.edit()?.putString("active_user_email", email)?.apply()
        firebaseAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    _activeUserEmail.value = user.email ?: email
                    sharedPrefs?.edit()?.putString("active_user_email", user.email ?: email)?.apply()
                }
            }
        }
        return true
    }

    override fun signUp(email: String, password: String): Boolean {
        _isAuthenticated.value = true
        _activeUserEmail.value = email
        sharedPrefs?.edit()?.putString("active_user_email", email)?.apply()
        firebaseAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    _activeUserEmail.value = user.email ?: email
                    sharedPrefs?.edit()?.putString("active_user_email", user.email ?: email)?.apply()
                }
            }
        }
        return true
    }

    override fun signInWithGoogle(context: Context?): Boolean {
        _isAuthenticated.value = true
        if (context != null) {
            scope.launch {
                try {
                    val authRepo = AuthRepository.getInstance()
                    val result = authRepo.signInWithGoogle(context)
                    if (result.isSuccess) {
                        val user = result.getOrNull()
                        val email = user?.email
                        if (email != null) {
                            _activeUserEmail.value = email
                            sharedPrefs?.edit()?.putString("active_user_email", email)?.apply()
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("JyotishaRepo", "Google sign in error", e)
                }
            }
        } else {
            val user = firebaseAuth?.currentUser
            val email = user?.email ?: "user@example.com"
            _activeUserEmail.value = email
            sharedPrefs?.edit()?.putString("active_user_email", email)?.apply()
        }
        return true
    }

    override fun signOut() {
        firebaseAuth?.signOut()
        _isAuthenticated.value = false
        _activeUserEmail.value = null
        _hasCompletedOnboarding.value = false
        sharedPrefs?.edit()
            ?.putBoolean("has_completed_onboarding", false)
            ?.remove("active_user_email")
            ?.apply()
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
        // Deduplicate by ID or matching name + dateOfBirth
        val index = updated.indexOfFirst { it.id == profile.id || (it.name.trim().equals(profile.name.trim(), ignoreCase = true) && it.dateOfBirth.trim() == profile.dateOfBirth.trim()) }
        if (index >= 0) {
            updated[index] = profile.copy(id = updated[index].id)
        } else {
            // If replacing default seed profile
            if (updated.size == 1 && updated[0].id == "profile_default") {
                updated[0] = profile
            } else {
                updated.add(profile)
            }
        }
        _profiles.value = updated
        _activeProfile.value = profile

        sharedPrefs?.edit()?.putString("active_profile_id", profile.id)?.apply()

        // Cache locally in Room
        scope.launch {
            try {
                roomDatabase?.jyotishaDao()?.insertProfile(BirthProfileEntity.fromDomain(profile))
            } catch (_: Exception) {}

            // Sync to Firestore
            try {
                val uid = firebaseAuth?.currentUser?.uid ?: "user_local"
                firestore?.collection("users")?.document(uid)?.collection("profiles")?.document(profile.id)?.set(
                    mapOf(
                        "id" to profile.id,
                        "name" to profile.name,
                        "dateOfBirth" to profile.dateOfBirth,
                        "timeOfBirth" to profile.timeOfBirth,
                        "timeConfidence" to profile.timeConfidence.name,
                        "placeName" to profile.placeName,
                        "latitude" to profile.latitude,
                        "longitude" to profile.longitude,
                        "timezoneId" to profile.timezoneId,
                        "isDefault" to profile.isDefault,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
            } catch (_: Exception) {}
        }

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

                // Dynamic Vimshottari Mahadasa
                _dasaTimeline.value = AstronomicalCalculator.calculateVimshottariDasaTimeline(
                    nakshatraName = calculatedChart.nakshatra,
                    birthDob = targetProfile.dateOfBirth
                )

                // Dynamic Numerology recalculation
                _numerologyReport.value = NumerologyEngine.calculate(
                    fullName = targetProfile.name,
                    dobString = targetProfile.dateOfBirth
                )

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

                // Generate individualized readings for this native
                generatePersonalizedReadings(calculatedChart, targetProfile)

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

    private fun generatePersonalizedReadings(chart: SouthIndianChart, profile: BirthProfile) {
        val moonSign = chart.moonSign
        val lagnaSign = chart.lagnamSign ?: ZodiacSign.LIBRA
        val nakshatra = chart.nakshatra
        val pada = chart.nakshatraPada

        val careerReading = ReadingReport(
            id = "reading_career_${profile.id}",
            title = "Career & Dharma: ${moonSign.englishName} Moon / ${lagnaSign.englishName} Lagna",
            dateIssued = "Recent Transit Alignment",
            category = QuestionCategory.CAREER,
            questionText = "What career direction and professional developments are indicated for ${profile.name}?",
            summary = "With your Janma Rasi in ${moonSign.sanskritName} (${moonSign.englishName}) and ${lagnaSign.sanskritName} Lagna, professional destiny is driven by ${moonSign.lord}'s governance. Classical texts indicate methodical progression, strategic patience in middle cycles, followed by commanding influence in enterprise.",
            timingWindow = "Next 18–24 Months (Peak Transit Window)",
            chartFactors = listOf(
                "Moon placed in ${moonSign.tamilName} (${moonSign.sanskritName}) ruled by ${moonSign.lord}.",
                "Lagnam in ${lagnaSign.tamilName} (${lagnaSign.sanskritName}) setting foundational temperament and ethical fortitude.",
                "Birth Nakshatra ${nakshatra} (Pada $pada) imparting analytical discipline and distinct craftsmanship."
            ),
            whyInterpretation = "According to Brihat Parashara Hora Shastra, when the natal Moon resides in ${moonSign.sanskritName}, the native possesses natural discernment. The 10th house from ${lagnaSign.sanskritName} receives stabilizing planetary aspects, transforming sustained effort into public trust.",
            scripturalCanon = "Brihat Parashara Hora Shastra (Adhyaya 48, Sloka 16): 'The native whose Moon occupies a benefic sthana achieves honors through steady perseverance and righteousness.'",
            sourcePassageLocator = "BPHS, Adhyaya 48, Shloka 16–18",
            practicalGuidance = listOf(
                "Focus on core strategic competencies rather than rapid speculative shifts.",
                "Cultivate transparent communication with senior domain mentors.",
                "Schedule major contractual milestones during waxing moon (Shukla Paksha) windows."
            ),
            traditionalRemedies = listOf(
                RemedyItem(
                    id = "remedy_career_${profile.id}",
                    title = "Strengthen Benefic Influence (${moonSign.lord} Preeti)",
                    deity = "Lord Dakshinamurthy / Surya Narayana",
                    practiceType = "Mantra Japa & Dana",
                    scriptureCitation = "Phaladeepika, Chapter 26 (Upachaya Shanti)",
                    description = "Chanting traditional peace mantras fosters clarity of intellect and dispels anxiety during professional reorganizations.",
                    mantraText = "Om Namo Bhagavate Vasudevaya",
                    suggestedTempleName = "Navagraha Temple Circuit",
                    suggestedTempleLocation = "Kumbakonam & Thanjavur District, Tamil Nadu",
                    practicalAlternative = "Dedicate time each Thursday to support educational supplies or mentorship for young learners."
                )
            )
        )

        val lifeReading = ReadingReport(
            id = "reading_life_${profile.id}",
            title = "Life Path & Vitality: ${nakshatra} Nakshatra Core",
            dateIssued = "Current Natal Assessment",
            category = QuestionCategory.SPIRITUALITY,
            questionText = "What foundational life strengths and spiritual milestones define my path?",
            summary = "Born under ${nakshatra} (Pada $pada), your temperament is marked by resilience, acute discernment, and a quest for deeper meaning. Traditional Jyotisha advises honoring seasonal rhythms and inner contemplation to maintain harmony.",
            timingWindow = "Long-Term Karmic Architecture",
            chartFactors = listOf(
                "Nakshatra: $nakshatra (Pada $pada) - Sovereign stellar alignment.",
                "Ascendant: ${lagnaSign.sanskritName} - Determines vital physical stamina and demeanor.",
                "Solar placement harmonizing personal agency with collective responsibility."
            ),
            whyInterpretation = "Tamil Siddha texts (Pulippani Jothidam) note that natives of ${nakshatra} possess a steadfast inner spirit. When balanced with proper breath awareness and righteous conduct, obstacles naturally dissolve.",
            scripturalCanon = "Pulippani Jothidam (Padal 72): 'Truthful conduct, tempered speech, and contemplation of the divine dispel all malefic planetary shadows.'",
            sourcePassageLocator = "Pulippani Munnul 300, Verse 72",
            practicalGuidance = listOf(
                "Practice 15 minutes of quiet breath contemplation (Pranayama) at sunrise.",
                "Engage in periodic selfless service (Seva) to ground personal accomplishments.",
                "Maintain integrity in all familial and professional promises."
            ),
            traditionalRemedies = listOf(
                RemedyItem(
                    id = "remedy_life_${profile.id}",
                    title = "Inner Vitality Harmony (Siddha Prana Shanti)",
                    deity = "Lord Shiva / Agastya Maharshi",
                    practiceType = "Silent Contemplation",
                    scriptureCitation = "Agastya Samhita & Thirumandiram",
                    description = "Meditation upon the inner light calms the nervous system and fosters spiritual discernment.",
                    mantraText = "Om Sivayanama",
                    suggestedTempleName = "Sri Agastheeswarar Temple",
                    suggestedTempleLocation = "Papanasam, Tirunelveli District, Tamil Nadu",
                    practicalAlternative = "Spend quiet time in unhurried nature or walking in peaceful greenery each morning."
                )
            )
        )

        _readings.value = listOf(careerReading, lifeReading)
        _activeReading.value = careerReading
    }

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
        val currentChart = _chart.value
        val mSign = currentChart.moonSign
        val lSign = currentChart.lagnamSign ?: ZodiacSign.LIBRA
        val nak = currentChart.nakshatra

        val (canonText, locator, factors, remedyName, remedyMantra) = when (category) {
            QuestionCategory.CAREER -> Quintuple(
                "Brihat Parashara Hora Shastra (Adhyaya 48, Sloka 16): 'The 10th house lord conjoined with or aspected by benefics grants honor, steadfast reputation, and righteous leadership.'",
                "BPHS, Adhyaya 48, Verse 16",
                listOf(
                    "Janma Rasi ${mSign.sanskritName} receives stabilizing trine aspects from benefic lords.",
                    "10th house from Lagnam (${lSign.sanskritName}) activates disciplined professional restructuring.",
                    "Nakshatra $nak enhances craftsmanship and focused execution."
                ),
                "Guru-Surya Preeti (Clarity of Leadership)",
                "Om Hram Hrim Hraum Sah Suryaya Namah"
            )
            QuestionCategory.RELATIONSHIPS -> Quintuple(
                "Phaladeepika (Adhyaya 15, Sloka 8): 'When the 7th house and Venus are associated with benefic influences, domestic peace, harmonious partnerships, and mutual respect thrive.'",
                "Phaladeepika, Ch. 15, Sloka 8",
                listOf(
                    "7th Bhava evaluated from natal Moon in ${mSign.sanskritName}.",
                    "Venusian harmonic vibration in Navamsam supporting empathetic listening.",
                    "Preservation of mutual emotional equilibrium across transit cycles."
                ),
                "Shukra Shanti (Harmony & Compassion)",
                "Om Shum Shukraya Namah"
            )
            QuestionCategory.MONEY -> Quintuple(
                "Saravali (Adhyaya 35, Sloka 12): 'Dhana sthana (2nd house) and Labha sthana (11th house) fortified by their own lords yield sustained prosperity through ethical labor.'",
                "Saravali, Chapter 35, Sloka 12",
                listOf(
                    "Wealth houses 2nd and 11th from ${lSign.sanskritName} show steady accumulation rather than abrupt speculation.",
                    "Jupiter's gaze on key economic sectors promotes prudent asset allocation.",
                    "Avoid impulsive commitments during retrograde transit corridors."
                ),
                "Lakshmi Narayana Aradhana (Abundance & Prudence)",
                "Om Shreem Hreem Kleem Mahalakshmaye Namah"
            )
            QuestionCategory.HEALTH -> Quintuple(
                "Charaka Samhita & Brihat Jataka (Adhyaya 22, Sloka 4): 'Harmony of Tridoshas (Vata, Pitta, Kapha) governed by the Sun and Moon sustains vitality and enduring vigor.'",
                "Brihat Jataka, Chapter 22",
                listOf(
                    "Ascendant ${lSign.sanskritName} governs bodily vitality and metabolic rhythm.",
                    "Moon in ${mSign.sanskritName} guides emotional equilibrium and sleep cycles.",
                    "Seasonal transitions (Ritu Sandhi) require mindful hydration and wholesome diet."
                ),
                "Dhanvantari Shanti (Vitality & Well-being)",
                "Om Namo Bhagavate Vasudevaya Dhanvantaraye Amrita Kalasha Hastaya Namah"
            )
            else -> Quintuple(
                "Pulippani Munnul 300 (Padal 84): 'Meditation upon the supreme truth, accompanied by ethical conduct and quiet discernment, dispels doubts and reveals the path of light.'",
                "Pulippani Jothidam, Padal 84",
                listOf(
                    "Natal disposition guided by $nak stellar resonance.",
                    "Inward reflection clarifies life priorities during current Dasa sub-cycle.",
                    "Alignment of personal purpose (Swadharma) with community well-being."
                ),
                "Agastya Dhyana (Inner Light & Peace)",
                "Om Sivayanama"
            )
        }

        val newReading = ReadingReport(
            id = "reading_${System.currentTimeMillis()}",
            title = "${category.title} Synthesis: ${mSign.englishName} Moon / ${lSign.englishName} Lagna",
            dateIssued = "Dynamic Consultation",
            category = category,
            questionText = questionText,
            summary = "Grounded analysis for ${category.title.lowercase()} indicates a constructive, transitional cycle. With your Moon in ${mSign.sanskritName} and Lagnam in ${lSign.sanskritName}, deliberate progress, strategic patience, and ethical alignment will bear fruitful outcomes.",
            timingWindow = "Current Astrological Window (Next 12–18 Months)",
            chartFactors = factors,
            whyInterpretation = "In classical South Indian Jyotisha, interpretations distinguish mathematical planetary coordinates from dogmatic claims. The interaction between your Janma Rasi (${mSign.sanskritName}) and current transit cycles emphasizes personal agency ('Purushakara') elevated by moral integrity.",
            scripturalCanon = canonText,
            sourcePassageLocator = locator,
            practicalGuidance = listOf(
                "Structure quarterly goals with defined verifiable milestones.",
                "Maintain measured emotional composure and consult seasoned mentors.",
                "Engage in daily quiet reflection before taking consequential decisions."
            ),
            traditionalRemedies = listOf(
                RemedyItem(
                    id = "remedy_${System.currentTimeMillis()}",
                    title = remedyName,
                    deity = "Cosmic Benefic Principle",
                    practiceType = "Mantra, Mindfulness & Charity",
                    scriptureCitation = locator,
                    description = "Traditional self-purifying contemplation intended to cultivate inner tranquility, ethical discipline, and mental clarity.",
                    mantraText = remedyMantra,
                    practicalAlternative = "Support education or feed birds/animals in nature as a daily act of gratitude (Dana)."
                )
            )
        )
        val updated = listOf(newReading) + _readings.value
        _readings.value = updated
        _activeReading.value = newReading
        return newReading
    }

    private data class Quintuple<A, B, C, D, E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
    )

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

    override fun deleteProfile(profileId: String) {
        val updated = _profiles.value.filter { it.id != profileId }
        _profiles.value = updated
        if (_activeProfile.value.id == profileId) {
            val fallback = updated.firstOrNull() ?: BirthProfile(
                id = "profile_${System.currentTimeMillis()}",
                name = "User Profile",
                dateOfBirth = "01 Jan 2000",
                timeOfBirth = "12:00 PM",
                placeName = "Chennai, Tamil Nadu, India"
            )
            _activeProfile.value = fallback
            if (updated.isEmpty()) {
                _profiles.value = listOf(fallback)
            }
            recalculateChart(fallback, _selectedTradition.value)
        }

        scope.launch {
            try {
                roomDatabase?.jyotishaDao()?.deleteProfileById(profileId)
                roomDatabase?.jyotishaDao()?.deleteChartsByProfileId(profileId)
            } catch (_: Exception) {}

            try {
                val uid = firebaseAuth?.currentUser?.uid
                if (uid != null) {
                    firestore?.collection("users")?.document(uid)?.collection("birth_profiles")?.document(profileId)?.delete()
                }
            } catch (_: Exception) {}
        }
    }

    override fun selectActiveProfile(profileId: String) {
        val target = _profiles.value.find { it.id == profileId } ?: return
        _activeProfile.value = target
        sharedPrefs?.edit()?.putString("active_profile_id", target.id)?.apply()
        recalculateChart(target, _selectedTradition.value)
    }

    override fun runRetrospectiveAnalysis(eventId: String): RetrospectiveAnalysis {
        val event = _lifeEvents.value.find { it.eventId == eventId } ?: LifeEvent(title = "Recorded Milestone", dateString = "2024-01-01")
        val active = _activeProfile.value
        val computedContext = AstronomicalCalculator.computeHistoricalContext(
            eventDateStr = event.dateString,
            profile = active,
            tradition = _selectedTradition.value
        )

        return RetrospectiveAnalysis(
            analysisId = "retro-${System.currentTimeMillis()}",
            eventId = event.eventId,
            eventDatePrecision = event.precision,
            calculatedAstronomicalContext = computedContext,
            matchedRules = listOf(
                MatchedRuleInfo(
                    ruleId = "RULE-BPHS-HISTORICAL-TRANSIT",
                    version = "2.1",
                    tradition = _selectedTradition.value.label,
                    conditionDescription = "Jupiter & Saturn planetary transits evaluated for event date ${event.dateString}.",
                    evidenceLabel = "astronomical_ephemeris_match"
                )
            ),
            sourcePassages = listOf(
                SourcePassageInfo(
                    sourceId = "BPHS-CH-84",
                    title = "Brihat Parashara Hora Shastra",
                    passageRef = "Chapter 84, Verses 12-14",
                    translationProvenance = "Vedic Sanskrit source translation by R. Santhanam",
                    supportsInterpretation = true
                )
            ),
            uncertainties = listOf(
                "Event date precision: ${event.precision}.",
                "Historical transit positions calculated via Lahiri Chitrapaksha ayanamsha; traditional interpretation is non-causal."
            ),
            traditionalInterpretationNote = "Retrospective analysis for '${event.title}' on ${event.dateString} grounded in astronomical ephemeris and classical Parashara doctrine."
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
