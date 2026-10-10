package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PlanetPlacement
import com.example.data.models.SouthIndianChart
import com.example.data.models.ZodiacSign
import com.example.ui.theme.*

@Composable
fun SouthIndianChartView(
    chart: SouthIndianChart,
    selectedSign: ZodiacSign?,
    onSignSelected: (ZodiacSign) -> Unit,
    modifier: Modifier = Modifier
) {
    // 4x4 South Indian Kattam Grid
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(18.dp))
            .background(CelestialSurface2)
            .border(1.5.dp, CelestialBorderStrong, RoundedCornerShape(18.dp))
            .padding(4.dp)
            .testTag("south_indian_chart")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0..3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    for (col in 0..3) {
                        val isCenter = (row == 1 || row == 2) && (col == 1 || col == 2)
                        if (isCenter) {
                            // The 2x2 center is rendered on row 1, col 1 spanning to col 2 and row 2
                            if (row == 1 && col == 1) {
                                ChartCenterHub(
                                    chart = chart,
                                    modifier = Modifier
                                        .weight(2f)
                                        .fillMaxHeight()
                                )
                            }
                            // col 2 in row 1 or col 1 & 2 in row 2 are handled by weight
                        } else {
                            val sign = ZodiacSign.at(row, col)
                            if (sign != null) {
                                val isSelected = selectedSign == sign
                                val planetsInSign = chart.placements.filter { it.sign == sign }
                                val isLagnaHouse = chart.lagnamSign == sign

                                ChartSignCell(
                                    sign = sign,
                                    planets = planetsInSign,
                                    isLagnaHouse = isLagnaHouse,
                                    isSelected = isSelected,
                                    onClick = { onSignSelected(sign) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartSignCell(
    sign: ZodiacSign,
    planets: List<PlanetPlacement>,
    isLagnaHouse: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = when {
        isSelected -> CelestialGoldWash
        isLagnaHouse -> CelestialVioletWash
        else -> Color.Transparent
    }
    val borderCol = if (isSelected) CelestialGold else CelestialBorder

    Box(
        modifier = modifier
            .padding(1.5.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(3.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Sign label & house indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sign.sanskritName.take(4),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) CelestialGold else CelestialTextMuted
                )
                if (isLagnaHouse) {
                    Text(
                        text = "Lag",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGoldStrong
                    )
                }
            }

            // Planets placed in this house
            if (planets.isNotEmpty()) {
                FlowRowPlanets(planets = planets)
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun FlowRowPlanets(planets: List<PlanetPlacement>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val chunked = planets.chunked(2)
        chunked.forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                pair.forEach { planet ->
                    val planetBg = when (planet.planetShort) {
                        "Su" -> Color(0xFFE89838) // Surya (Orange)
                        "Mo" -> Color(0xFF8CC7DA) // Chandra (Sky)
                        "Ju" -> CelestialGold // Guru (Gold)
                        "Ve" -> Color(0xFFE5C0EA) // Shukra (Light Violet)
                        "Ma" -> Color(0xFFE06D6D) // Mangala (Red)
                        "Sa" -> Color(0xFF7F8AA0) // Shani (Deep Blue-Grey)
                        "Me" -> Color(0xFF7ED6AD) // Budha (Emerald Green)
                        "Ra", "Ke" -> Color(0xFFA5A0F5) // Nodes (Violet)
                        else -> CelestialTextPrimary
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(planetBg.copy(alpha = 0.22f))
                            .border(0.5.dp, planetBg.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = planet.planetShort,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = planetBg
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartCenterHub(
    chart: SouthIndianChart,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CelestialBgElevated)
            .border(1.dp, CelestialBorderStrong, RoundedCornerShape(10.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = CelestialGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lagnam: ${chart.lagnamSign?.sanskritName ?: "Unknown"}",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = CelestialTextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = chart.lagnamSign?.let { "(${it.englishName})" } ?: "(Time Uncertain)",
                fontSize = 10.sp,
                color = CelestialTextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Janma Rasi: ${chart.moonSign.sanskritName}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = CelestialGoldStrong,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${chart.nakshatra} (P${chart.nakshatraPada})",
                fontSize = 9.sp,
                color = CelestialSky,
                textAlign = TextAlign.Center
            )
        }
    }
}
