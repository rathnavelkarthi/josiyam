package com.example.ui.screens

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
import com.example.data.models.ReadingReport
import com.example.data.models.RemedyItem
import com.example.ui.components.CelestialBackground
import com.example.ui.components.CelestialBadge
import com.example.ui.components.CelestialButton
import com.example.ui.components.CelestialCard
import com.example.ui.components.CelestialOutlinedButton
import com.example.ui.components.DemoDisclaimerBadge
import com.example.ui.theme.*

@Composable
fun ReadingReportScreen(
    reading: ReadingReport,
    onBack: () -> Unit,
    onOpenRemedies: (ReadingReport) -> Unit,
    onAskFollowUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Overview, 1: Factors, 2: Reasoning, 3: Scripture, 4: Guidance
    val sectionTabs = listOf("Overview", "Factors", "Reasoning", "Scriptures", "Guidance")

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Top Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("report_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CelestialTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CelestialBadge(text = reading.category.title, color = CelestialSky)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = CelestialGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Report Title & Date
            Text(
                text = reading.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestialTextPrimary,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Issued on ${reading.dateIssued} • Based on Vedic Sidereal (Lahiri)",
                fontSize = 12.sp,
                color = CelestialTextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Segmented Scrollable Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedSection,
                containerColor = Color.Transparent,
                contentColor = CelestialGold,
                edgePadding = 0.dp,
                divider = {},
                indicator = {}
            ) {
                sectionTabs.forEachIndexed { idx, label ->
                    val isSel = selectedSection == idx
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isSel) CelestialGoldWash else CelestialSurface2)
                            .border(1.dp, if (isSel) CelestialGold else CelestialBorder, RoundedCornerShape(999.dp))
                            .clickable { selectedSection = idx }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("report_tab_$idx")
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSel) CelestialGoldStrong else CelestialTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedSection) {
                    0 -> {
                        // Overview Section
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CelestialPositive.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = CelestialPositive, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("SYNTHESIS SUMMARY", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = CelestialPositive)
                                    Text("Favourable Alignment with Measured Patience", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = reading.summary,
                                fontSize = 14.sp,
                                color = CelestialTextPrimary,
                                lineHeight = 21.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CelestialBgElevated)
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Primary Window: ${reading.timingWindow}",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CelestialGoldStrong
                                    )
                                }
                            }
                        }

                        // Traditional Remedies Quick Banner
                        CelestialCard(
                            backgroundColor = CelestialSurface,
                            borderColor = CelestialGold.copy(alpha = 0.4f),
                            onClick = { onOpenRemedies(reading) },
                            modifier = Modifier.testTag("report_remedies_banner")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(CelestialGoldWash),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.TempleHindu, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Traditional Remedies & Guidance", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                                        Text("${reading.traditionalRemedies.size} classical remedies & suggested temples", fontSize = 12.sp, color = CelestialTextSecondary)
                                    }
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CelestialGold)
                            }
                        }
                    }

                    1 -> {
                        // Calculated Chart Factors
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Text(
                                text = "What the Chart Indicates",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CelestialGoldStrong
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "These are concrete planetary positions and aspects computed from your birth data:",
                                fontSize = 12.5.sp,
                                color = CelestialTextMuted
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            reading.chartFactors.forEachIndexed { i, factor ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(CelestialGoldWash),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${i + 1}", fontSize = 11.sp, color = CelestialGold, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = factor,
                                        fontSize = 13.5.sp,
                                        color = CelestialTextPrimary,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // Why this Interpretation
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Text(
                                text = "Why this Interpretation",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CelestialGoldStrong
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = reading.whyInterpretation,
                                fontSize = 14.sp,
                                color = CelestialTextPrimary,
                                lineHeight = 21.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CelestialSurface3)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Transparent Methodology: Traditional Jyotisha synthesizes transit Gochara with Mahadasa lord dignities. Multiple classical schools (e.g. Parashari vs Jaimini) may evaluate sub-periods differently.",
                                    fontSize = 12.sp,
                                    color = CelestialSky,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    3 -> {
                        // Scriptural Canon
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Classical Scriptural Reference",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CelestialGoldStrong
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            CelestialBadge(text = reading.sourcePassageLocator, color = CelestialGold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = reading.scripturalCanon,
                                fontSize = 14.sp,
                                color = CelestialTextPrimary,
                                lineHeight = 21.sp
                            )
                        }
                    }

                    4 -> {
                        // Practical Guidance
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Text(
                                text = "Actionable Real-World Guidance",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CelestialGoldStrong
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Reversible, grounded recommendations that harmonize with your planetary rhythm:",
                                fontSize = 12.5.sp,
                                color = CelestialTextMuted
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            reading.practicalGuidance.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CelestialPositive, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = item,
                                        fontSize = 13.5.sp,
                                        color = CelestialTextPrimary,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }
                    }
                }

                DemoDisclaimerBadge(text = "Astrological analysis is an interpretive tradition. Not a substitute for professional legal or financial advice.")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CelestialOutlinedButton(
                    text = "Remedies",
                    onClick = { onOpenRemedies(reading) },
                    modifier = Modifier.weight(1f),
                    testTag = "report_bottom_remedies"
                )
                CelestialButton(
                    text = "Ask Siddha AI",
                    onClick = onAskFollowUp,
                    modifier = Modifier.weight(1f),
                    testTag = "report_bottom_ask_ai"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
