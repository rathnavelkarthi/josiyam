package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CelestialBackground
import com.example.ui.components.CelestialButton
import com.example.ui.components.CelestialOutlinedButton
import com.example.ui.theme.*

@Composable
fun SplashScreen(
    hasCompletedOnboarding: Boolean = false,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-transition to dashboard if session was already established and onboarding completed
    LaunchedEffect(hasCompletedOnboarding) {
        if (hasCompletedOnboarding) {
            kotlinx.coroutines.delay(1400)
            onContinue()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "halo_breathing")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_scale"
    )

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Central Sacred Celestial Lotus Emblem
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(haloScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer faint golden aura
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        CelestialGold.copy(alpha = 0.25f),
                                        CelestialGoldWash,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    // Inner orbit ring
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, CelestialGold.copy(alpha = 0.6f), CircleShape)
                            .background(CelestialBgElevated)
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Jyotisha Emblem",
                            tint = CelestialGoldStrong,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Jyotisha",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialTextPrimary,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ANCIENT WISDOM FOR A BRIGHTER TOMORROW",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = CelestialGold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "A clearer, source-grounded way to explore South Indian Vedic astrology & cosmic cycles.",
                    fontSize = 14.sp,
                    color = CelestialTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            // Bottom CTA
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CelestialButton(
                    text = if (hasCompletedOnboarding) "Enter Cosmic Dashboard →" else "Begin Experience",
                    onClick = onContinue,
                    testTag = "splash_continue_button"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SKY • SELF • TRADITION • PURPOSE",
                    fontSize = 10.sp,
                    color = CelestialTextMuted,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    onBeginJourney: () -> Unit,
    onAlreadyHaveAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(CelestialSurface2)
                        .border(1.dp, CelestialBorder, RoundedCornerShape(999.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "✧ South Indian Astrology Platform",
                        fontSize = 12.sp,
                        color = CelestialGold,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Central Hero Editorial Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "Understand your chart.\nExplore your path.",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialTextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Jyotisha connects traditional South Indian Sidereal charts, Tamil Panchangam, classical scriptures, and transparent insights into one calm, Apple-quality experience.",
                    fontSize = 15.sp,
                    color = CelestialTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Three core pillars
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeaturePillarRow(
                        title = "Authentic South Indian Kattam",
                        description = "Fixed-sign 4x4 Rasi grid, Navamsam & Dasa-Bhukti analysis."
                    )
                    FeaturePillarRow(
                        title = "Classical Scripture Citations",
                        description = "Every reading cites Brihat Parashara, Phaladeepika, or Pulippani."
                    )
                    FeaturePillarRow(
                        title = "No Fear, No Fatalism",
                        description = "Distinguishes calculated facts from traditional interpretations."
                    )
                }
            }

            // CTAs
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CelestialButton(
                    text = "Begin your journey",
                    onClick = onBeginJourney,
                    testTag = "welcome_begin_button"
                )

                CelestialOutlinedButton(
                    text = "I already have an account",
                    onClick = onAlreadyHaveAccount,
                    testTag = "welcome_signin_button"
                )
            }
        }
    }
}

@Composable
private fun FeaturePillarRow(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CelestialSurface2.copy(alpha = 0.6f))
            .border(0.75.dp, CelestialBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(CelestialGoldWash),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = CelestialGold,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestialTextPrimary
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = CelestialTextMuted,
                lineHeight = 16.sp
            )
        }
    }
}
