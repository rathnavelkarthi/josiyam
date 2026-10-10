package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*
import kotlin.random.Random

private data class Star(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val alphaBase: Float,
    val color: Color
)

@Composable
fun CelestialBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Generate deterministic star positions
    val stars = remember {
        val rand = Random(42)
        List(48) {
            val color = when (rand.nextInt(4)) {
                0 -> CelestialGold.copy(alpha = 0.6f)
                1 -> CelestialSky.copy(alpha = 0.5f)
                2 -> CelestialViolet.copy(alpha = 0.4f)
                else -> Color.White.copy(alpha = 0.7f)
            }
            Star(
                xRatio = rand.nextFloat(),
                yRatio = rand.nextFloat(),
                radius = rand.nextFloat() * 1.6f + 0.8f,
                alphaBase = rand.nextFloat() * 0.4f + 0.3f,
                color = color
            )
        }
    }

    // Subtle breathing animation for ambient starlight
    val infiniteTransition = rememberInfiniteTransition(label = "starlight")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CelestialBg)
    ) {
        // Deep obsidian background with celestial radial vignette
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Cosmic ambient radial gradient
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CelestialBgElevated,
                        CelestialBg,
                        Color(0xFF030509)
                    ),
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.15f),
                    radius = canvasWidth * 1.2f
                )
            )

            // Faint orbital rings in the cosmic distance
            val center = Offset(canvasWidth * 0.5f, canvasHeight * 0.28f)
            drawCircle(
                color = CelestialBorder,
                radius = canvasWidth * 0.65f,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.75f)
            )
            drawCircle(
                color = CelestialBorder.copy(alpha = 0.05f),
                radius = canvasWidth * 0.95f,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 0.5f)
            )

            // Draw calm starry night nodes
            stars.forEach { star ->
                val starX = star.xRatio * canvasWidth
                val starY = star.yRatio * canvasHeight
                val currentAlpha = (star.alphaBase * pulse).coerceIn(0.15f, 0.85f)
                drawCircle(
                    color = star.color.copy(alpha = currentAlpha),
                    radius = star.radius,
                    center = Offset(starX, starY)
                )
            }
        }

        // Foreground content with transparent spacing
        content()
    }
}
