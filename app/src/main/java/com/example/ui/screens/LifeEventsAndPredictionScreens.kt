package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.components.CelestialBackground
import com.example.ui.components.CelestialBadge
import com.example.ui.components.CelestialCard
import com.example.ui.components.DemoDisclaimerBadge
import com.example.ui.theme.*

@Composable
fun LifeTimelineScreen(
    lifeEvents: List<LifeEvent>,
    onAddEvent: (LifeEvent) -> Unit,
    onDeleteEvent: (String) -> Unit,
    onRunRetrospective: (String) -> Unit,
    onOpenPredictions: () -> Unit,
    onOpenEvaluation: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("Career") }
    var dateInput by remember { mutableStateOf("2024-01") }
    var precisionInput by remember { mutableStateOf("MonthYear") }
    var notesInput by remember { mutableStateOf("") }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("timeline_back_btn")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CelestialTextPrimary)
                }
                Text("Life Timeline & Retrospective", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                IconButton(onClick = { showAddDialog = true }, modifier = Modifier.testTag("add_event_btn")) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event", tint = CelestialGold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-nav buttons for predictions and evaluation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenPredictions,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CelestialGold),
                    border = BorderStroke(1.dp, CelestialGold)
                ) {
                    Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Prediction Ledger", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onOpenEvaluation,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CelestialSky),
                    border = BorderStroke(1.dp, CelestialSky)
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Evaluation Summary", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (lifeEvents.isEmpty()) {
                    CelestialCard(backgroundColor = CelestialSurface2) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.EventNote, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No Life Events Recorded", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CelestialTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Record your education, career, or relocation milestones to examine historical traditional interpretations.", fontSize = 12.sp, color = CelestialTextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                } else {
                    lifeEvents.forEach { event ->
                        CelestialCard(backgroundColor = CelestialSurface) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CelestialBadge(text = event.category, color = CelestialGold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(event.dateString, fontSize = 12.sp, color = CelestialSky)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(event.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CelestialTextPrimary)
                                    if (event.notes.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(event.notes, fontSize = 13.sp, color = CelestialTextSecondary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Precision: ${event.precision} • User-owned record", fontSize = 11.sp, color = CelestialTextMuted)
                                }
                                IconButton(onClick = { onDeleteEvent(event.eventId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CelestialTextMuted)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onRunRetrospective(event.eventId) },
                                colors = ButtonDefaults.buttonColors(containerColor = CelestialSurface2),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Run Retrospective Traditional Analysis", color = CelestialGoldStrong, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                DemoDisclaimerBadge(text = "Events are user-owned and private. Retrospective analysis is non-causal traditional interpretation.")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Life Milestone", color = CelestialTextPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Event Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = categoryInput,
                            onValueChange = { categoryInput = it },
                            label = { Text("Category (Career, Education, etc.)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = dateInput,
                            onValueChange = { dateInput = it },
                            label = { Text("Date (YYYY-MM-DD or YYYY)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Private Notes (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (titleInput.isNotBlank()) {
                                onAddEvent(
                                    LifeEvent(
                                        title = titleInput,
                                        category = categoryInput,
                                        dateString = dateInput,
                                        notes = notesInput,
                                        precision = if (dateInput.length > 4) "Exact" else "YearOnly"
                                    )
                                )
                                titleInput = ""
                                notesInput = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CelestialGold)
                    ) {
                        Text("Save Milestone", color = CelestialBg)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel", color = CelestialTextSecondary)
                    }
                },
                containerColor = CelestialSurface2
            )
        }
    }
}

@Composable
fun RetrospectiveAnalysisScreen(
    analysis: RetrospectiveAnalysis,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CelestialTextPrimary)
                }
                Text("Retrospective Analysis", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                Box(modifier = Modifier.size(40.dp))
            }

            CelestialCard(backgroundColor = CelestialSurface2) {
                Text("Traditional Interpretation Summary", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                Spacer(modifier = Modifier.height(6.dp))
                Text(analysis.traditionalInterpretationNote, fontSize = 13.sp, color = CelestialTextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Date Precision: ${analysis.eventDatePrecision}", fontSize = 12.sp, color = CelestialSky)
            }

            CelestialCard(backgroundColor = CelestialSurface) {
                Text("Calculated Astronomical Context", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialGold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(analysis.calculatedAstronomicalContext, fontSize = 13.sp, color = CelestialTextSecondary)
            }

            Text("Matched Versioned Rules", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CelestialTextPrimary)
            analysis.matchedRules.forEach { rule ->
                CelestialCard(backgroundColor = CelestialSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Rule ID: ${rule.ruleId}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CelestialGoldStrong)
                        CelestialBadge(text = rule.evidenceLabel, color = CelestialPositive)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(rule.conditionDescription, fontSize = 12.5.sp, color = CelestialTextSecondary)
                }
            }

            Text("Retrieved Source Passages", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CelestialTextPrimary)
            analysis.sourcePassages.forEach { src ->
                CelestialCard(backgroundColor = CelestialSurface) {
                    Text(src.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CelestialTextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Ref: ${src.passageRef}", fontSize = 12.sp, color = CelestialSky)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(src.translationProvenance, fontSize = 12.sp, color = CelestialTextSecondary)
                }
            }

            CelestialCard(backgroundColor = CelestialSurface2) {
                Text("Uncertainties & Limitations", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CelestialGold)
                Spacer(modifier = Modifier.height(4.dp))
                analysis.uncertainties.forEach { unc ->
                    Text("• $unc", fontSize = 12.sp, color = CelestialTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PredictionLedgerScreen(
    predictions: List<PredictionRecord>,
    onCreatePrediction: (String, String, String, String) -> Unit,
    onSubmitOutcome: (String, String, String) -> Unit,
    onOpenEvaluation: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var topicInput by remember { mutableStateOf("") }
    var startInput by remember { mutableStateOf("2026-01-01") }
    var endInput by remember { mutableStateOf("2026-06-30") }

    var selectedPredForOutcome by remember { mutableStateOf<PredictionRecord?>(null) }
    var outcomeStatus by remember { mutableStateOf("OccurredAsDescribed") }
    var outcomeNotes by remember { mutableStateOf("") }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CelestialTextPrimary)
                }
                Text("Prediction Ledger & Outcomes", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "New Prediction", tint = CelestialGold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                predictions.forEach { pred ->
                    CelestialCard(backgroundColor = CelestialSurface2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CelestialBadge(text = pred.tradition, color = CelestialGold)
                            Text("Window: ${pred.windowStart} to ${pred.windowEnd}", fontSize = 11.sp, color = CelestialSky)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(pred.topic, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CelestialTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(pred.plainInterpretation, fontSize = 13.sp, color = CelestialTextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("🔒 Immutable server-recorded timestamp prior to outcome", fontSize = 11.sp, color = CelestialPositive)

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { selectedPredForOutcome = pred },
                            colors = ButtonDefaults.buttonColors(containerColor = CelestialGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Report Follow-up Outcome", color = CelestialBg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                DemoDisclaimerBadge(text = "Prediction ledger stores forward-looking readings immutably. Outcomes are user-reported.")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("New Prospective Reading", color = CelestialTextPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = topicInput,
                            onValueChange = { topicInput = it },
                            label = { Text("Question or Focus Topic") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = startInput,
                            onValueChange = { startInput = it },
                            label = { Text("Window Start (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = endInput,
                            onValueChange = { endInput = it },
                            label = { Text("Window End (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (topicInput.isNotBlank()) {
                                onCreatePrediction(topicInput, startInput, endInput, "Vedic / Jyotisha")
                                topicInput = ""
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CelestialGold)
                    ) {
                        Text("Save Immutable Prediction", color = CelestialBg)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel", color = CelestialTextSecondary)
                    }
                },
                containerColor = CelestialSurface2
            )
        }

        if (selectedPredForOutcome != null) {
            AlertDialog(
                onDismissRequest = { selectedPredForOutcome = null },
                title = { Text("Submit Outcome Follow-up", color = CelestialTextPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select actual outcome for: ${selectedPredForOutcome?.topic}", fontSize = 12.sp, color = CelestialTextSecondary)
                        listOf("OccurredAsDescribed", "PartlyOccurred", "DidNotOccur", "TooAmbiguous").forEach { status ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { outcomeStatus = status }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = outcomeStatus == status, onClick = { outcomeStatus = status })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(status, fontSize = 13.sp, color = CelestialTextPrimary)
                            }
                        }
                        OutlinedTextField(
                            value = outcomeNotes,
                            onValueChange = { outcomeNotes = it },
                            label = { Text("Explanation (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedPredForOutcome?.let {
                                onSubmitOutcome(it.predictionId, outcomeStatus, outcomeNotes)
                            }
                            selectedPredForOutcome = null
                            outcomeNotes = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CelestialGold)
                    ) {
                        Text("Submit Outcome", color = CelestialBg)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedPredForOutcome = null }) {
                        Text("Cancel", color = CelestialTextSecondary)
                    }
                },
                containerColor = CelestialSurface2
            )
        }
    }
}

@Composable
fun EvaluationSummaryScreen(
    summary: EvaluationSummary,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CelestialTextPrimary)
                }
                Text("Research & Evaluation Summary", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                Box(modifier = Modifier.size(40.dp))
            }

            CelestialCard(backgroundColor = CelestialSurface2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Response Rate", fontSize = 12.sp, color = CelestialTextMuted)
                        Text("${summary.responseRatePercentage}%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CelestialGoldStrong)
                    }
                    CelestialBadge(text = "${summary.reportedOutcomesCount} / ${summary.eligiblePredictionsCount} Reported", color = CelestialPositive)
                }
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { summary.responseRatePercentage / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)),
                    color = CelestialGold,
                    trackColor = CelestialSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CelestialCard(backgroundColor = CelestialSurface, modifier = Modifier.weight(1f)) {
                    Text("Matches", fontSize = 11.sp, color = CelestialTextMuted)
                    Text("${summary.matchCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CelestialPositive)
                }
                CelestialCard(backgroundColor = CelestialSurface, modifier = Modifier.weight(1f)) {
                    Text("Partial", fontSize = 11.sp, color = CelestialTextMuted)
                    Text("${summary.partialMatchCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CelestialSky)
                }
                CelestialCard(backgroundColor = CelestialSurface, modifier = Modifier.weight(1f)) {
                    Text("Mismatches", fontSize = 11.sp, color = CelestialTextMuted)
                    Text("${summary.mismatchCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE89838))
                }
            }

            CelestialCard(backgroundColor = CelestialSurface) {
                Text("Baseline Comparison & Methodology", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialGold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(summary.baselineComparisonNote, fontSize = 13.sp, color = CelestialTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Missing outcomes and ambiguous responses are tracked separately to prevent inflation of success metrics.", fontSize = 12.sp, color = CelestialTextMuted)
            }

            DemoDisclaimerBadge(text = "Transparent outcome tracking framework. Distinguishes user-reported outcomes from verified facts.")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
