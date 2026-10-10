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
import com.example.data.models.BodyAssessment
import com.example.data.models.ReadingReport
import com.example.data.models.RemedyItem
import com.example.data.models.SamudrikaLandmark
import com.example.ui.components.CelestialBackground
import com.example.ui.components.CelestialBadge
import com.example.ui.components.CelestialCard
import com.example.ui.components.DemoDisclaimerBadge
import com.example.ui.theme.*

@Composable
fun RemediesScreen(
    reading: ReadingReport?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: Traditional, 1: Practical, 2: Lifestyle
    val filters = listOf("Traditional", "Practical", "Lifestyle")

    val remedies = reading?.traditionalRemedies ?: emptyList()

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("remedies_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CelestialTextPrimary
                    )
                }
                Text(
                    text = "Remedies & Guidance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialTextPrimary
                )
                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-filters row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEachIndexed { index, name ->
                    val isSel = selectedFilter == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isSel) CelestialGoldWash else CelestialSurface2)
                            .border(1.dp, if (isSel) CelestialGold else CelestialBorder, RoundedCornerShape(999.dp))
                            .clickable { selectedFilter = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            fontSize = 13.sp,
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
                remedies.forEach { remedy ->
                    CelestialCard(backgroundColor = CelestialSurface2) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CelestialGoldWash),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TempleHindu, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(remedy.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                                Text("Deity: ${remedy.deity} • ${remedy.practiceType}", fontSize = 12.sp, color = CelestialSky)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        CelestialBadge(text = remedy.scriptureCitation, color = CelestialGold)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = remedy.description,
                            fontSize = 13.5.sp,
                            color = CelestialTextPrimary,
                            lineHeight = 19.sp
                        )

                        if (remedy.mantraText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CelestialBgElevated)
                                    .border(0.75.dp, CelestialBorder, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("SACRED MANTRA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CelestialGold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "\"${remedy.mantraText}\"",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CelestialTextPrimary
                                    )
                                }
                            }
                        }

                        if (remedy.suggestedTempleName.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("SUGGESTED TEMPLE PILGRIMAGE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = CelestialGold, letterSpacing = 0.8.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CelestialSurface3)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(remedy.suggestedTempleName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                                    Text(remedy.suggestedTempleLocation, fontSize = 11.5.sp, color = CelestialTextMuted)
                                }
                            }
                        }

                        if (remedy.practicalAlternative.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("PRACTICAL ETHICAL ALTERNATIVE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = CelestialPositive, letterSpacing = 0.8.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = remedy.practicalAlternative,
                                fontSize = 12.5.sp,
                                color = CelestialTextSecondary
                            )
                        }
                    }
                }

                DemoDisclaimerBadge(text = "Traditional pariharas are optional practices of reverence. No claims of disease cure or guarantees.")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun BodyPatternsScreen(
    bodyAssessment: BodyAssessment,
    samudrikaLandmarks: List<SamudrikaLandmark>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Constitution, 2: Samudrika Marks
    val tabs = listOf("Overview", "Constitution", "Samudrika Marks")

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("body_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CelestialTextPrimary
                    )
                }
                Text(
                    text = "Body & Health Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialTextPrimary
                )
                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            fontSize = 12.sp,
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
                // Tridosha Visualizer Card
                CelestialCard(backgroundColor = CelestialSurface2) {
                    Text(
                        text = "Ayurvedic Tridosha Balance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CelestialTextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Three Tridosha Bubbles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DoshaBubble(name = "Vata", percent = bodyAssessment.vataPercentage, color = CelestialSky)
                        DoshaBubble(name = "Pitta", percent = bodyAssessment.pittaPercentage, color = Color(0xFFE89838), isDominant = true)
                        DoshaBubble(name = "Kapha", percent = bodyAssessment.kaphaPercentage, color = CelestialPositive)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = bodyAssessment.constitutionType,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CelestialGoldStrong
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = bodyAssessment.traditionalDescription,
                        fontSize = 13.sp,
                        color = CelestialTextSecondary,
                        lineHeight = 18.sp
                    )
                }

                // Care Recommendations
                CelestialCard(backgroundColor = CelestialSurface) {
                    Text("Daily Lifestyle Harmony (Dinacharya)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialGold)
                    Spacer(modifier = Modifier.height(10.dp))
                    bodyAssessment.careRecommendations.forEach { rec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Spa, contentDescription = null, tint = CelestialPositive, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(rec, fontSize = 13.sp, color = CelestialTextPrimary, lineHeight = 18.sp)
                        }
                    }
                }

                // Samudrika Shastra Landmarks Card
                CelestialCard(backgroundColor = CelestialSurface2) {
                    Text("Samudrika Shastra Symbolic Markings", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Traditional South Indian palmistry and bodily marks from ancient palm-leaf treatises:",
                        fontSize = 12.sp,
                        color = CelestialTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    samudrikaLandmarks.forEach { mark ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(mark.name, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = CelestialTextPrimary)
                                Text("${mark.location} • ${mark.traditionalSymbolism}", fontSize = 11.5.sp, color = CelestialTextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            CelestialBadge(text = "${mark.scorePercent}% Match", color = CelestialSky)
                        }
                        HorizontalDivider(color = CelestialBorder, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }

                DemoDisclaimerBadge(text = "Non-diagnostic educational assessment. Does not diagnose illness or provide clinical treatment.")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DoshaBubble(
    name: String,
    percent: Int,
    color: Color,
    isDominant: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .border(if (isDominant) 2.dp else 1.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = color)
                Text("$percent%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CelestialTextPrimary)
            }
        }
        if (isDominant) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Dominant", fontSize = 10.sp, color = CelestialGold, fontWeight = FontWeight.Bold)
        }
    }
}
