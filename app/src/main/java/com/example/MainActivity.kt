package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.*
import com.example.data.repository.JyotishaRepository
import com.example.ui.components.AppDestination
import com.example.ui.components.JyotishaBottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.CelestialBg
import com.example.ui.theme.MyApplicationTheme

sealed interface Screen {
    data object Splash : Screen
    data object Welcome : Screen
    data object TraditionSelect : Screen
    data object Auth : Screen
    data object BirthProfileStepper : Screen
    data class Main(val destination: AppDestination = AppDestination.HOME) : Screen
    data class ReadingDetail(val reading: ReadingReport) : Screen
    data class RemediesDetail(val reading: ReadingReport) : Screen
    data object BodyPatterns : Screen
    data object PanchangamDetail : Screen
    data object SiddhaAi : Screen
    data object NumerologyAndScan : Screen
    data class CameraCapture(val scanType: String, val targetSide: String) : Screen
    data object LifeTimeline : Screen
    data class RetrospectiveDetail(val analysis: RetrospectiveAnalysis) : Screen
    data object PredictionLedger : Screen
    data object EvaluationSummaryRoute : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val firebaseApp = com.google.firebase.FirebaseApp.initializeApp(this)
            if (firebaseApp != null) {
                val firebaseAppCheck = com.google.firebase.appcheck.FirebaseAppCheck.getInstance()
                firebaseAppCheck.installAppCheckProviderFactory(
                    com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory.getInstance()
                )
            }
        } catch (e: Exception) {
            // App Check initialization fallback for offline or test mode
        }
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                JyotishaApp()
            }
        }
    }
}

@Composable
fun JyotishaApp() {
    val repository = remember { JyotishaRepository.getInstance() }

    val activeProfile by repository.activeProfile.collectAsStateWithLifecycle()
    val allProfiles by repository.profiles.collectAsStateWithLifecycle()
    val tradition by repository.selectedTradition.collectAsStateWithLifecycle()
    val calendar by repository.selectedCalendar.collectAsStateWithLifecycle()
    val chartUiState by repository.chartUiState.collectAsStateWithLifecycle()
    val panchangam by repository.panchangam.collectAsStateWithLifecycle()
    val readings by repository.readings.collectAsStateWithLifecycle()
    val activeReading by repository.activeReading.collectAsStateWithLifecycle()
    val milestones by repository.milestones.collectAsStateWithLifecycle()
    val bodyAssessment by repository.bodyAssessment.collectAsStateWithLifecycle()
    val numerologyReport by repository.numerologyReport.collectAsStateWithLifecycle()
    val palmScanResult by repository.palmScanResult.collectAsStateWithLifecycle()
    val faceAnalysisResult by repository.faceAnalysisResult.collectAsStateWithLifecycle()
    val chatMessages by repository.chatMessages.collectAsStateWithLifecycle()
    val credits by repository.credits.collectAsStateWithLifecycle()
    val lifeEvents by repository.lifeEvents.collectAsStateWithLifecycle()
    val predictions by repository.predictions.collectAsStateWithLifecycle()
    val evaluationSummary by repository.evaluationSummary.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var currentMainDestination by remember { mutableStateOf(AppDestination.HOME) }

    // Handle Back Press across screens
    BackHandler(enabled = currentScreen !is Screen.Splash && currentScreen !is Screen.Main) {
        currentScreen = when (currentScreen) {
            is Screen.Welcome -> Screen.Splash
            is Screen.TraditionSelect -> Screen.Welcome
            is Screen.Auth -> Screen.TraditionSelect
            is Screen.BirthProfileStepper -> Screen.Auth
            is Screen.ReadingDetail -> Screen.Main(currentMainDestination)
            is Screen.RemediesDetail -> activeReading?.let { Screen.ReadingDetail(it) } ?: Screen.Main(currentMainDestination)
            is Screen.BodyPatterns -> Screen.Main(currentMainDestination)
            is Screen.PanchangamDetail -> Screen.Main(currentMainDestination)
            is Screen.SiddhaAi -> Screen.Main(currentMainDestination)
            is Screen.NumerologyAndScan -> Screen.Main(currentMainDestination)
            else -> Screen.Main(AppDestination.HOME)
        }
    }

    // Determine whether to show Bottom Navigation Bar
    val isBottomBarVisible = currentScreen is Screen.Main

    Scaffold(
        containerColor = CelestialBg,
        bottomBar = {
            if (isBottomBarVisible) {
                JyotishaBottomNavBar(
                    currentDestination = currentMainDestination,
                    onNavigate = { dest ->
                        currentMainDestination = dest
                        currentScreen = Screen.Main(dest)
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Splash -> {
                    SplashScreen(
                        onContinue = { currentScreen = Screen.Welcome }
                    )
                }

                is Screen.Welcome -> {
                    WelcomeScreen(
                        onBeginJourney = { currentScreen = Screen.TraditionSelect },
                        onAlreadyHaveAccount = { currentScreen = Screen.Auth }
                    )
                }

                is Screen.TraditionSelect -> {
                    TraditionSelectionScreen(
                        currentTradition = tradition,
                        currentCalendar = calendar,
                        onTraditionSelected = { repository.updateTradition(it) },
                        onCalendarSelected = { repository.updateCalendar(it) },
                        onContinue = { currentScreen = Screen.Auth },
                        onBack = { currentScreen = Screen.Welcome }
                    )
                }

                is Screen.Auth -> {
                    AuthScreen(
                        onAuthSuccess = {
                            // Route to birth profile setup if first time, or directly to Dashboard
                            currentScreen = Screen.BirthProfileStepper
                        },
                        onBack = { currentScreen = Screen.TraditionSelect }
                    )
                }

                is Screen.BirthProfileStepper -> {
                    BirthProfileStepperScreen(
                        onProfileSaved = { newProfile ->
                            repository.saveProfile(newProfile)
                            currentMainDestination = AppDestination.HOME
                            currentScreen = Screen.Main(AppDestination.HOME)
                        },
                        onBack = { currentScreen = Screen.Auth }
                    )
                }

                is Screen.Main -> {
                    when (screen.destination) {
                        AppDestination.HOME -> {
                            val activeChart = when (val state = chartUiState) {
                                is ChartUiState.Success -> state.chart
                                is ChartUiState.DemoMode -> state.chart
                                else -> com.example.data.engine.AstronomicalCalculator.calculateNatalChart(activeProfile, tradition)
                            }
                            HomeScreen(
                                profile = activeProfile,
                                chart = activeChart,
                                panchangam = panchangam,
                                recentReadings = readings,
                                onOpenChart = {
                                    currentMainDestination = AppDestination.CHART
                                    currentScreen = Screen.Main(AppDestination.CHART)
                                },
                                onOpenAsk = {
                                    currentMainDestination = AppDestination.ASK
                                    currentScreen = Screen.Main(AppDestination.ASK)
                                },
                                onOpenPanchangam = { currentScreen = Screen.PanchangamDetail },
                                onOpenSiddhaAi = { currentScreen = Screen.SiddhaAi },
                                onOpenBodyPatterns = { currentScreen = Screen.BodyPatterns },
                                onOpenNumerologyAndScan = { currentScreen = Screen.NumerologyAndScan },
                                onOpenLifeTimeline = { currentScreen = Screen.LifeTimeline },
                                onOpenReading = { r ->
                                    repository.setActiveReading(r)
                                    currentScreen = Screen.ReadingDetail(r)
                                },
                                onOpenProfile = {
                                    currentMainDestination = AppDestination.PROFILE
                                    currentScreen = Screen.Main(AppDestination.PROFILE)
                                }
                            )
                        }

                        AppDestination.CHART -> {
                            ChartExplorerScreen(
                                profile = activeProfile,
                                chartUiState = chartUiState,
                                dasaTimeline = repository.dasaTimeline,
                                onToggleDemoMode = { repository.toggleDemoMode() },
                                onRetry = { repository.recalculateChart() }
                            )
                        }

                        AppDestination.ASK -> {
                            AskQuestionScreen(
                                profile = activeProfile,
                                creditBalance = credits,
                                onGenerateReading = { cat, qText ->
                                    repository.askQuestion(cat, qText)
                                },
                                onReadingGenerated = { r ->
                                    repository.setActiveReading(r)
                                    currentScreen = Screen.ReadingDetail(r)
                                }
                            )
                        }

                        AppDestination.LIBRARY -> {
                            LibraryScreen(
                                sourceWorks = repository.sourceWorks,
                                savedReadings = readings,
                                milestones = milestones,
                                onOpenReading = { r ->
                                    repository.setActiveReading(r)
                                    currentScreen = Screen.ReadingDetail(r)
                                },
                                onAddMilestone = { repository.addMilestone(it) }
                            )
                        }

                        AppDestination.PROFILE -> {
                            ProfileScreen(
                                profile = activeProfile,
                                allProfiles = allProfiles,
                                tradition = tradition,
                                calendar = calendar,
                                credits = credits,
                                onEditProfile = { currentScreen = Screen.BirthProfileStepper },
                                onAddProfile = { currentScreen = Screen.BirthProfileStepper },
                                onChangeTradition = { currentScreen = Screen.TraditionSelect },
                                onRechargeCredits = {
                                    // Demo credit recharge
                                    repeat(5) { repository.saveProfile(activeProfile) }
                                }
                            )
                        }
                    }
                }

                is Screen.ReadingDetail -> {
                    ReadingReportScreen(
                        reading = screen.reading,
                        onBack = { currentScreen = Screen.Main(currentMainDestination) },
                        onOpenRemedies = { r -> currentScreen = Screen.RemediesDetail(r) },
                        onAskFollowUp = { currentScreen = Screen.SiddhaAi }
                    )
                }

                is Screen.RemediesDetail -> {
                    RemediesScreen(
                        reading = screen.reading,
                        onBack = { currentScreen = Screen.ReadingDetail(screen.reading) }
                    )
                }

                is Screen.BodyPatterns -> {
                    BodyPatternsScreen(
                        bodyAssessment = bodyAssessment,
                        samudrikaLandmarks = repository.samudrikaLandmarks,
                        onBack = { currentScreen = Screen.Main(currentMainDestination) }
                    )
                }

                is Screen.PanchangamDetail -> {
                    PanchangamScreen(
                        panchangam = panchangam,
                        astronomicalEvents = repository.astronomicalEvents,
                        onBack = { currentScreen = Screen.Main(currentMainDestination) }
                    )
                }

                is Screen.SiddhaAi -> {
                    SiddhaAiScreen(
                        profile = activeProfile,
                        messages = chatMessages,
                        onSendMessage = { txt -> repository.sendChatMessage(txt) }
                    )
                }

                is Screen.NumerologyAndScan -> {
                    NumerologyAndScanScreen(
                        numerology = numerologyReport,
                        palmScan = palmScanResult,
                        faceAnalysis = faceAnalysisResult,
                        onRunPalmScan = { side -> repository.runPalmScan(side) },
                        onRunFaceAnalysis = { repository.runFaceAnalysis() },
                        onOpenCameraController = { scanType, side -> currentScreen = Screen.CameraCapture(scanType, side) },
                        onRecalculateNumerology = { name, dob -> repository.calculateNumerology(name, dob) },
                        onBack = { currentScreen = Screen.Main(currentMainDestination) }
                    )
                }

                is Screen.CameraCapture -> {
                    CameraCaptureScreen(
                        scanType = screen.scanType,
                        targetSide = screen.targetSide,
                        onImageCaptured = { bitmap, quality ->
                            if (screen.scanType == "PALM") {
                                repository.runPalmScan(screen.targetSide)
                            } else {
                                repository.runFaceAnalysis()
                            }
                            currentScreen = Screen.NumerologyAndScan
                        },
                        onBack = { currentScreen = Screen.NumerologyAndScan }
                    )
                }

                is Screen.LifeTimeline -> {
                    LifeTimelineScreen(
                        lifeEvents = lifeEvents,
                        onAddEvent = { event -> repository.addLifeEvent(event) },
                        onDeleteEvent = { eventId -> repository.deleteLifeEvent(eventId) },
                        onRunRetrospective = { eventId ->
                            val analysis = repository.runRetrospectiveAnalysis(eventId)
                            currentScreen = Screen.RetrospectiveDetail(analysis)
                        },
                        onOpenPredictions = { currentScreen = Screen.PredictionLedger },
                        onOpenEvaluation = { currentScreen = Screen.EvaluationSummaryRoute },
                        onBack = { currentScreen = Screen.Main(currentMainDestination) }
                    )
                }

                is Screen.RetrospectiveDetail -> {
                    RetrospectiveAnalysisScreen(
                        analysis = screen.analysis,
                        onBack = { currentScreen = Screen.LifeTimeline }
                    )
                }

                is Screen.PredictionLedger -> {
                    PredictionLedgerScreen(
                        predictions = predictions,
                        onCreatePrediction = { topic, start, end, trad ->
                            repository.createPrediction(topic, start, end, trad)
                        },
                        onSubmitOutcome = { predId, status, expl ->
                            repository.submitPredictionOutcome(predId, status, expl)
                        },
                        onOpenEvaluation = { currentScreen = Screen.EvaluationSummaryRoute },
                        onBack = { currentScreen = Screen.LifeTimeline }
                    )
                }

                is Screen.EvaluationSummaryRoute -> {
                    EvaluationSummaryScreen(
                        summary = evaluationSummary,
                        onBack = { currentScreen = Screen.LifeTimeline }
                    )
                }
            }
        }
    }
}
