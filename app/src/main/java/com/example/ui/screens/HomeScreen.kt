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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BirthProfile
import com.example.data.models.PanchangamSnapshot
import com.example.data.models.ReadingReport
import com.example.data.models.SouthIndianChart
import com.example.ui.components.CelestialBackground
import com.example.ui.components.CelestialBadge
import com.example.ui.components.CelestialCard
import com.example.ui.components.DemoDisclaimerBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    profile: BirthProfile,
    chart: SouthIndianChart,
    panchangam: PanchangamSnapshot,
    recentReadings: List<ReadingReport>,
    onOpenChart: () -> Unit,
    onOpenAsk: () -> Unit,
    onOpenPanchangam: () -> Unit,
    onOpenSiddhaAi: () -> Unit,
    onOpenBodyPatterns: () -> Unit,
    onOpenNumerologyAndScan: () -> Unit,
    onOpenLifeTimeline: () -> Unit,
    onOpenReading: (ReadingReport) -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Top Header: User Greeting & Profile Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenProfile() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CelestialGoldWash)
                            .border(1.dp, CelestialGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.name.take(1),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialGoldStrong
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Vanakkam, ${profile.name} 👋",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CelestialTextPrimary
                        )
                        Text(
                            text = "Your cosmic blueprint is ready",
                            fontSize = 12.sp,
                            color = CelestialTextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onOpenProfile,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CelestialSurface2)
                        .border(1.dp, CelestialBorder, CircleShape)
                        .testTag("home_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Preferences",
                        tint = CelestialTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Hero Feature: "Your Vedic Rasi"
            CelestialCard(
                backgroundColor = CelestialSurface2,
                borderColor = CelestialBorderStrong,
                onClick = onOpenChart,
                modifier = Modifier.testTag("home_chart_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CelestialGoldWash),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "♎",
                                fontSize = 22.sp,
                                color = CelestialGoldStrong
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            CelestialBadge(text = "VEDIC SIDEREAL (LAHIRI)", color = CelestialGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${chart.moonSign.tamilName} (${chart.moonSign.englishName})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CelestialTextPrimary
                            )
                            Text(
                                text = "Lagnam: ${chart.lagnamSign?.sanskritName ?: "Unknown"} • ${chart.nakshatra} (P${chart.nakshatraPada})",
                                fontSize = 12.5.sp,
                                color = CelestialTextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Chart",
                        tint = CelestialGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Quick Feature Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionPill(
                    icon = Icons.Default.CalendarToday,
                    title = "Daily\nPanchangam",
                    onClick = onOpenPanchangam,
                    modifier = Modifier.weight(1f),
                    testTag = "home_quick_panchangam"
                )
                QuickActionPill(
                    icon = Icons.Default.AutoAwesome,
                    title = "Ask\nSiddha AI",
                    onClick = onOpenSiddhaAi,
                    modifier = Modifier.weight(1f),
                    testTag = "home_quick_siddha_ai"
                )
                QuickActionPill(
                    icon = Icons.Default.Spa,
                    title = "Body &\nSamudrika",
                    onClick = onOpenBodyPatterns,
                    modifier = Modifier.weight(1f),
                    testTag = "home_quick_body"
                )
                QuickActionPill(
                    icon = Icons.Default.Stars,
                    title = "Birth\nKundali",
                    onClick = onOpenChart,
                    modifier = Modifier.weight(1f),
                    testTag = "home_quick_chart"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionPill(
                    icon = Icons.Default.Timeline,
                    title = "Life Timeline & Prediction Ledger",
                    onClick = onOpenLifeTimeline,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "home_quick_timeline"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tri-Engine Personal Analysis Banner (Palm, Face & Numerology)
            CelestialCard(
                backgroundColor = CelestialSurface2,
                borderColor = CelestialGold,
                onClick = onOpenNumerologyAndScan,
                modifier = Modifier.testTag("home_tri_engine_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CelestialGoldWash),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            CelestialBadge(text = "TRI-ENGINE INTEGRATION", color = CelestialGold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Palm Scanning, Face & Numerology", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                            Text("Unified personal report powered by 3 distinct analytical engines", fontSize = 11.5.sp, color = CelestialTextSecondary)
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = CelestialGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Today's Cosmic Snapshot / Panchangam Card
            CelestialCard(
                backgroundColor = CelestialSurface,
                onClick = onOpenPanchangam,
                modifier = Modifier.testTag("home_panchangam_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Cosmic Snapshot",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CelestialTextPrimary
                    )
                    Text(
                        text = "View details →",
                        fontSize = 12.sp,
                        color = CelestialGold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                SnapshotRow(label = "Tithi", value = panchangam.tithi, subvalue = panchangam.tithiEnd)
                SnapshotRow(label = "Nakshatra", value = panchangam.nakshatra, subvalue = panchangam.nakshatraEnd)
                SnapshotRow(label = "Moon Phase", value = panchangam.moonPhase, subvalue = "Illuminated")
                SnapshotRow(label = "Rahu Kalam", value = panchangam.rahuKalam, subvalue = "Inauspicious", highlight = CelestialCaution)
                SnapshotRow(label = "Shubha Muhurtham", value = panchangam.shubhaMuhurtham, subvalue = "Favourable", highlight = CelestialPositive)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Call to Action: Explore a Question
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(CelestialBgElevated)
                    .border(1.dp, CelestialGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .clickable { onOpenAsk() }
                    .padding(18.dp)
                    .testTag("home_ask_cta")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Have a question about your chart?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CelestialGoldStrong
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Ask about career, home, relationships, or planetary timing.",
                            fontSize = 13.sp,
                            color = CelestialTextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CelestialGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Ask",
                            tint = CelestialBg,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recent Readings List
            if (recentReadings.isNotEmpty()) {
                Text(
                    text = "RECENT READINGS",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                recentReadings.forEach { reading ->
                    CelestialCard(
                        backgroundColor = CelestialSurface2,
                        onClick = { onOpenReading(reading) },
                        modifier = Modifier.padding(bottom = 10.dp).testTag("reading_item_${reading.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CelestialBadge(text = reading.category.title, color = CelestialSky)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = reading.dateIssued,
                                        fontSize = 11.5.sp,
                                        color = CelestialTextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = reading.title,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CelestialTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = reading.summary,
                                    fontSize = 12.5.sp,
                                    color = CelestialTextSecondary,
                                    maxLines = 2
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = CelestialTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            DemoDisclaimerBadge(text = "Panchangam coordinates: Chennai (13.08° N, 80.27° E) • Illustrative engine")

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CelestialSurface2)
            .border(0.75.dp, CelestialBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CelestialGold,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = CelestialTextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun SnapshotRow(
    label: String,
    value: String,
    subvalue: String,
    highlight: Color = CelestialTextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(CelestialGold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, color = CelestialTextSecondary)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = highlight)
            Spacer(modifier = Modifier.width(6.dp))
            Text("($subvalue)", fontSize = 11.sp, color = CelestialTextMuted)
        }
    }
}
