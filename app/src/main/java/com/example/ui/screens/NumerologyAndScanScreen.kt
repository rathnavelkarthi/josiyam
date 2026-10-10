package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.models.FaceAnalysisResult
import com.example.data.models.NumerologyReport
import com.example.data.models.PalmScanResult
import com.example.ui.components.CelestialBackground
import com.example.ui.components.CelestialBadge
import com.example.ui.components.CelestialCard
import com.example.ui.components.DemoDisclaimerBadge
import com.example.ui.theme.*

@Composable
fun NumerologyAndScanScreen(
    numerology: NumerologyReport,
    palmScan: PalmScanResult,
    faceAnalysis: FaceAnalysisResult,
    onRunPalmScan: (String) -> Unit,
    onRunFaceAnalysis: () -> Unit,
    onOpenCameraController: (String, String) -> Unit,
    onRecalculateNumerology: (String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Numerology, 1: Palm Scan, 2: Face Feature
    val tabs = listOf("Numerology Engine", "Palm Scanning", "Face Analysis")

    var nameInput by remember { mutableStateOf(numerology.fullName) }
    var dobInput by remember { mutableStateOf(numerology.dateOfBirth) }
    var selectedHand by remember { mutableStateOf("Right Hand (Manifested Action)") }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("scan_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CelestialTextPrimary
                    )
                }
                Text(
                    text = "Tri-Engine Personal Analysis",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialTextPrimary
                )
                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tabs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tabs.forEachIndexed { index, name ->
                    val isSel = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isSel) CelestialGoldWash else CelestialSurface2)
                            .border(1.dp, if (isSel) CelestialGold else CelestialBorder, RoundedCornerShape(999.dp))
                            .clickable { selectedTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSel) CelestialGoldStrong else CelestialTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Numerology Engine Tab
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Text("Chaldean & Pythagorean Numerology", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Deterministic name and birth number analysis with Tamil transliteration support.", fontSize = 12.sp, color = CelestialTextMuted)

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Full Name (English / Transliterated)") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CelestialGold,
                                    unfocusedBorderColor = CelestialBorder,
                                    focusedLabelColor = CelestialGold,
                                    unfocusedLabelColor = CelestialTextSecondary
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = dobInput,
                                onValueChange = { dobInput = it },
                                label = { Text("Date of Birth (DD MMM YYYY)") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CelestialGold,
                                    unfocusedBorderColor = CelestialBorder,
                                    focusedLabelColor = CelestialGold,
                                    unfocusedLabelColor = CelestialTextSecondary
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onRecalculateNumerology(nameInput, dobInput) },
                                colors = ButtonDefaults.buttonColors(containerColor = CelestialGold),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Calculate Numerology", color = CelestialBg, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Results Card
                        CelestialCard(backgroundColor = CelestialSurface) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Chaldean Name Number", fontSize = 12.sp, color = CelestialTextMuted)
                                    Text("${numerology.chaldeanNameNumber} (Compound: ${numerology.chaldeanCompound})", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CelestialGoldStrong)
                                }
                                CelestialBadge(text = "Surya / Guru Vibe", color = CelestialGold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(numerology.chaldeanMeaning, fontSize = 13.sp, color = CelestialTextPrimary)

                            HorizontalDivider(color = CelestialBorder, modifier = Modifier.padding(vertical = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Birth Number", fontSize = 11.sp, color = CelestialTextMuted)
                                    Text("${numerology.birthNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CelestialSky)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(numerology.birthNumberMeaning, fontSize = 12.sp, color = CelestialTextSecondary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Life Path Number", fontSize = 11.sp, color = CelestialTextMuted)
                                    Text("${numerology.lifePathNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CelestialPositive)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(numerology.lifePathMeaning, fontSize = 12.sp, color = CelestialTextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            CelestialBadge(text = numerology.tamilTransliterationNote, color = CelestialTextSecondary)
                        }
                    }

                    1 -> {
                        // Palm Scanning Tab
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Palm Landmarker & Crease Analysis", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                                CelestialBadge(text = "${palmScan.qualityScore}% Quality", color = CelestialPositive)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Using MediaPipe Hand Landmarker framework & Samudrika Shastra classical line correlation.", fontSize = 12.sp, color = CelestialTextMuted)

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Left Hand (Innate Potential)", "Right Hand (Manifested Action)").forEach { side ->
                                    val isSel = selectedHand == side
                                    OutlinedButton(
                                        onClick = {
                                            selectedHand = side
                                            onRunPalmScan(side)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isSel) CelestialGoldWash else Color.Transparent,
                                            contentColor = if (isSel) CelestialGoldStrong else CelestialTextSecondary
                                        ),
                                        border = BorderStroke(1.dp, if (isSel) CelestialGold else CelestialBorder)
                                    ) {
                                        Text(if (side.contains("Left")) "Left Hand" else "Right Hand", fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { onOpenCameraController("PALM", selectedHand) },
                                colors = ButtonDefaults.buttonColors(containerColor = CelestialGold),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CelestialBg, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open CameraX Palm Scanner", color = CelestialBg, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Scan Confidence bars
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                ScanConfidenceBar("Heart Line (Rishi Rekha)", palmScan.lineConfidenceHeart, CelestialPositive)
                                ScanConfidenceBar("Head Line (Buddhi Rekha)", palmScan.lineConfidenceHead, CelestialSky)
                                ScanConfidenceBar("Life Line (Ayush Rekha)", palmScan.lineConfidenceLife, CelestialGold)
                                ScanConfidenceBar("Fate Line (Karma Rekha)", palmScan.lineConfidenceFate, Color(0xFFE89838))
                            }
                        }

                        // Mount Analysis Cards
                        palmScan.mountAnalyses.forEach { mount ->
                            CelestialCard(backgroundColor = CelestialSurface) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Flare, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(mount.mountName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                                        Text("Development: ${mount.developmentLevel}", fontSize = 12.sp, color = CelestialSky)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(mount.astrologicalSignificance, fontSize = 13.sp, color = CelestialTextSecondary, lineHeight = 18.sp)
                            }
                        }

                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Text("Samudrika Shastra Context", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CelestialGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(palmScan.traditionalSymbolism, fontSize = 12.5.sp, color = CelestialTextPrimary, lineHeight = 18.sp)
                        }
                    }

                    2 -> {
                        // Face Analysis Tab
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Face Feature & Mesh Analysis", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                                CelestialBadge(text = "${faceAnalysis.symmetryScore}% Symmetry", color = CelestialPositive)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("MediaPipe 468 3D landmark mesh mapping combined with Samudrika physiognomy canons.", fontSize = 12.sp, color = CelestialTextMuted)

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { onRunFaceAnalysis() },
                                colors = ButtonDefaults.buttonColors(containerColor = CelestialGold),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CelestialBg, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Run Face Landmark Scan", color = CelestialBg, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { onOpenCameraController("FACE", "Face") },
                                colors = ButtonDefaults.buttonColors(containerColor = CelestialSurface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Open CameraX Face Mesh Scanner", color = CelestialGold, fontWeight = FontWeight.Bold)
                            }
                        }

                        faceAnalysis.features.forEach { feat ->
                            CelestialCard(backgroundColor = CelestialSurface) {
                                Text(feat.featureName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Measurement: ${feat.measurementDescription}", fontSize = 12.sp, color = CelestialSky)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(feat.traditionalReading, fontSize = 13.sp, color = CelestialTextPrimary, lineHeight = 18.sp)
                            }
                        }

                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Text("Physiognomy Tradition Notes", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CelestialGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(faceAnalysis.samudrikaTraditionNotes, fontSize = 12.5.sp, color = CelestialTextPrimary, lineHeight = 18.sp)
                        }
                    }
                }

                DemoDisclaimerBadge(text = "Educational and traditional self-reflection engines. Non-diagnostic.")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ScanConfidenceBar(label: String, score: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = CelestialTextPrimary)
            Text("$score%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = color,
            trackColor = CelestialSurface
        )
    }
}
