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
    // Authentic 4x4 South Indian Kattam Grid with fixed clockwise sign positions
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(18.dp))
            .background(CelestialSurface2)
            .border(1.5.dp, CelestialBorderStrong, RoundedCornerShape(18.dp))
            .padding(6.dp)
            .testTag("south_indian_chart")
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val cellW = maxWidth / 4f
            val cellH = maxHeight / 4f

            // Render 12 Zodiac Sign cells
            ZodiacSign.entries.forEach { sign ->
                val isSelected = selectedSign == sign
                val planetsInSign = chart.placements.filter { it.sign == sign }
                val isLagnaHouse = chart.lagnamSign == sign

                Box(
                    modifier = Modifier
                        .offset(x = cellW * sign.gridCol, y = cellH * sign.gridRow)
                        .size(cellW, cellH)
                        .padding(2.dp)
                ) {
                    ChartSignCell(
                        sign = sign,
                        planets = planetsInSign,
                        isLagnaHouse = isLagnaHouse,
                        isSelected = isSelected,
                        onClick = { onSignSelected(sign) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Render central 2x2 Hub (occupies columns 1..2 and rows 1..2)
            Box(
                modifier = Modifier
                    .offset(x = cellW, y = cellH)
                    .size(cellW * 2f, cellH * 2f)
                    .padding(2.dp)
            ) {
                ChartCenterHub(
                    chart = chart,
                    modifier = Modifier.fillMaxSize()
                )
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
