package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
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
import com.example.ui.components.SouthIndianChartView
import com.example.ui.theme.*

@Composable
fun ChartExplorerScreen(
    profile: BirthProfile,
    chartUiState: ChartUiState,
    dasaTimeline: List<DasaPeriod>,
    onToggleDemoMode: () -> Unit = {},
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Rasi, 1: Navamsam, 2: Planets, 3: Nakshatra, 4: Dasa
    val tabs = listOf("Rasi (D1)", "Navamsam", "Planets", "Nakshatra", "Dasa")

    var selectedSign by remember { mutableStateOf<ZodiacSign?>(ZodiacSign.LIBRA) }
    var showCalculationSheet by remember { mutableStateOf(false) }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Birth Chart",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CelestialTextPrimary
                    )
                    Text(
                        text = "${profile.name} • ${profile.dateOfBirth} (${profile.timeOfBirth})",
                        fontSize = 12.sp,
                        color = CelestialTextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleDemoMode,
                        modifier = Modifier.testTag("chart_demo_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Toggle Demo Mode",
                            tint = CelestialTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { showCalculationSheet = true },
                        modifier = Modifier.testTag("chart_info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Calculation Details",
                            tint = CelestialGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // State Handling
            when (chartUiState) {
                is ChartUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = CelestialGold, modifier = Modifier.size(42.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Calculating Astronomical Positions...",
                                fontSize = 15.sp,
                                color = CelestialTextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Resolving UTC instant, sidereal time & planetary longitudes",
                                fontSize = 12.sp,
                                color = CelestialTextMuted
                            )
                        }
                    }
                }

                is ChartUiState.EngineNotConfigured -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = CelestialCaution, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Calculation Engine Unavailable", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CelestialCaution)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(chartUiState.message, fontSize = 13.sp, color = CelestialTextSecondary)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.buttonColors(containerColor = CelestialGold, contentColor = CelestialBg)
                            ) {
                                Text("Retry Calculation")
                            }
                        }
                    }
                }

                is ChartUiState.NetworkError -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Text("Connection Error", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CelestialNegative)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(chartUiState.message, fontSize = 13.sp, color = CelestialTextSecondary)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(onClick = onRetry) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is ChartUiState.Unauthenticated -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CelestialCard(backgroundColor = CelestialSurface2) {
                            Text("Authentication Required", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CelestialGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Sign in to calculate and view your personal birth chart.", fontSize = 13.sp, color = CelestialTextSecondary)
                        }
                    }
                }

                is ChartUiState.Success, is ChartUiState.DemoMode -> {
                    val activeChart = if (chartUiState is ChartUiState.Success) chartUiState.chart else (chartUiState as ChartUiState.DemoMode).chart

                    // Mode or Time Uncertainty Banner
                    if (activeChart.isDemoMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CelestialCaution.copy(alpha = 0.15f))
                                .border(0.75.dp, CelestialCaution.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️ Illustrative Demo Chart (Developer Toggle Active)",
                                fontSize = 11.5.sp,
                                color = CelestialCaution,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    } else if (activeChart.isTimeUncertain) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CelestialSky.copy(alpha = 0.15f))
                                .border(0.75.dp, CelestialSky.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ℹ️ Birth time marked Unknown: Lagnam & Houses are suppressed to avoid false precision.",
                                fontSize = 11.5.sp,
                                color = CelestialSky,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Scrollable Tab Row
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = CelestialGold,
                        edgePadding = 0.dp,
                        divider = {},
                        indicator = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedTab == index
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSelected) CelestialGoldWash else CelestialSurface2)
                                    .border(1.dp, if (isSelected) CelestialGold else CelestialBorder, RoundedCornerShape(999.dp))
                                    .clickable { selectedTab = index }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                    .testTag("chart_tab_$index")
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) CelestialGoldStrong else CelestialTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Content
                    when (selectedTab) {
                        0, 1 -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                item {
                                    SouthIndianChartView(
                                        chart = activeChart,
                                        selectedSign = selectedSign,
                                        onSignSelected = { selectedSign = it }
                                    )
                                }

                                item {
                                    // Selected House / Sign Inspector
                                    val sign = selectedSign ?: activeChart.lagnamSign ?: activeChart.moonSign
                                    val planetsInSign = activeChart.placements.filter { it.sign == sign }
                                    val isLagna = activeChart.lagnamSign == sign

                                    CelestialCard(backgroundColor = CelestialSurface2) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "${sign.tamilName} (${sign.englishName})",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = CelestialGoldStrong
                                                )
                                                Text(
                                                    text = "Sign Lord: ${sign.lord} • Sanskrit: ${sign.sanskritName}",
                                                    fontSize = 12.sp,
                                                    color = CelestialTextSecondary
                                                )
                                            }
                                            if (isLagna) {
                                                CelestialBadge(text = "Lagna House", color = CelestialGold)
                                            }
                                        }

                                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CelestialBorder)

                                        if (planetsInSign.isEmpty()) {
                                            Text(
                                                text = "No planets occupy this house in this calculation.",
                                                fontSize = 13.sp,
                                                color = CelestialTextMuted
                                            )
                                        } else {
                                            Text(
                                                text = "OCCUPYING PLANETS",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CelestialGold,
                                                letterSpacing = 1.sp
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            planetsInSign.forEach { p ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        CelestialBadge(text = p.planetShort, color = CelestialSky)
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Column {
                                                            Text(p.planetName, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = CelestialTextPrimary)
                                                            Text("${p.nakshatra} (Pada ${p.pada})", fontSize = 11.5.sp, color = CelestialTextMuted)
                                                        }
                                                    }
                                                    Column(horizontalAlignment = Alignment.End) {
                                                        Text(p.degrees, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                                                        if (p.dignity != "Friendly") {
                                                            Text(p.dignity, fontSize = 11.sp, color = CelestialPositive)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    DemoDisclaimerBadge(text = "Tap any cell on the 4x4 Kattam to inspect sign lords & calculated planetary longitudes.")
                                }
                            }
                        }

                        2 -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(activeChart.placements) { p ->
                                    CelestialCard(backgroundColor = CelestialSurface2) {
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
                                                    Text(p.planetShort, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CelestialGold)
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(p.planetName, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                                                    Text("${p.sign.sanskritName} • House ${p.houseNumber ?: "-"}", fontSize = 12.sp, color = CelestialTextSecondary)
                                                }
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(p.degrees, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                                                Text("${p.nakshatra} (P${p.pada})", fontSize = 11.5.sp, color = CelestialSky)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    CelestialCard(backgroundColor = CelestialSurface2) {
                                        CelestialBadge(text = "CALCULATED JANMA NAKSHATRA", color = CelestialGold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "${activeChart.nakshatra} — Pada ${activeChart.nakshatraPada}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CelestialTextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Moon Longitude placement in ${activeChart.moonSign.sanskritName} (${activeChart.moonSign.englishName})",
                                            fontSize = 13.sp,
                                            color = CelestialTextSecondary
                                        )

                                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CelestialBorder)

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Calculated Sun Sign", fontSize = 12.5.sp, color = CelestialTextMuted)
                                            Text("${activeChart.sunSign.sanskritName} (${activeChart.sunSign.englishName})", fontSize = 12.5.sp, color = CelestialTextPrimary)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Calculated Moon Sign", fontSize = 12.5.sp, color = CelestialTextMuted)
                                            Text("${activeChart.moonSign.sanskritName} (${activeChart.moonSign.englishName})", fontSize = 12.5.sp, color = CelestialTextPrimary)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Calculated Ascendant (Lagna)", fontSize = 12.5.sp, color = CelestialTextMuted)
                                            Text(activeChart.lagnamSign?.let { "${it.sanskritName} (${activeChart.lagnamDegrees ?: ""})" } ?: "Unknown", fontSize = 12.5.sp, color = CelestialGoldStrong)
                                        }
                                    }
                                }
                            }
                        }

                        4 -> {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(dasaTimeline) { dasa ->
                                    val borderColor = if (dasa.isCurrent) CelestialGold else CelestialBorder
                                    val bgColor = if (dasa.isCurrent) CelestialGoldWash else CelestialSurface2

                                    CelestialCard(backgroundColor = bgColor, borderColor = borderColor) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = "${dasa.mahadasaLord} Mahadasa",
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = if (dasa.isCurrent) CelestialGoldStrong else CelestialTextPrimary
                                                    )
                                                    if (dasa.isCurrent) {
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        CelestialBadge(text = "CURRENT", color = CelestialPositive)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${dasa.startYear} – ${dasa.endYear} (Theme: ${dasa.theme})",
                                                    fontSize = 12.sp,
                                                    color = CelestialTextSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCalculationSheet) {
        val currentChart = when (chartUiState) {
            is ChartUiState.Success -> chartUiState.chart
            is ChartUiState.DemoMode -> chartUiState.chart
            else -> null
        }

        AlertDialog(
            onDismissRequest = { showCalculationSheet = false },
            containerColor = CelestialSurface2,
            title = {
                Text("Calculation Provenance & Engine", color = CelestialGold, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Engine Name: ${currentChart?.engineName ?: "JyotishaAstronomicalEngine"}", fontSize = 13.sp, color = CelestialTextPrimary)
                    Text("• Engine Version: ${currentChart?.engineVersion ?: "2.1.0-commercial-clean"}", fontSize = 13.sp, color = CelestialTextPrimary)
                    Text("• License: MIT / Permissive (Clean-Room Non-AGPL)", fontSize = 13.sp, color = CelestialPositive)
                    Text("• Ayanamsha: ${currentChart?.ayanamshaName ?: "Lahiri (Chitrapaksha)"} (${String.format("%.4f°", currentChart?.ayanamshaDegrees ?: 23.85)})", fontSize = 13.sp, color = CelestialTextPrimary)
                    Text("• Calculation Instant: ${currentChart?.calculatedAt?.ifBlank { "Real-time on device" } ?: "Real-time"}", fontSize = 13.sp, color = CelestialTextMuted)
                    Text("• Native Place: ${profile.placeName}", fontSize = 13.sp, color = CelestialTextPrimary)
                    Text("• Coordinates: ${String.format("%.4f° N, %.4f° E", profile.latitude, profile.longitude)}", fontSize = 13.sp, color = CelestialSky)
                    Text("• Time Precision: ${profile.timeConfidence.label}", fontSize = 13.sp, color = CelestialGold)
                }
            },
            confirmButton = {
                TextButton(onClick = { showCalculationSheet = false }) {
                    Text("Close", color = CelestialGold)
                }
            }
        )
    }
}
