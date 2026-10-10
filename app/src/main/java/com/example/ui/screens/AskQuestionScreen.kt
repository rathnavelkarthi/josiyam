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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BirthProfile
import com.example.data.models.QuestionCategory
import com.example.data.models.ReadingReport
import com.example.ui.components.CelestialBackground
import com.example.ui.components.CelestialBadge
import com.example.ui.components.CelestialButton
import com.example.ui.components.CelestialCard
import com.example.ui.components.DemoDisclaimerBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AskQuestionScreen(
    profile: BirthProfile,
    creditBalance: Int,
    onGenerateReading: (QuestionCategory, String) -> ReadingReport,
    onReadingGenerated: (ReadingReport) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(QuestionCategory.CAREER) }
    var questionText by remember { mutableStateOf(QuestionCategory.CAREER.sampleQuestion) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Adaptive questionnaire answers
    var contextTimingPreference by remember { mutableStateOf("Next 12–18 Months") }
    var contextFocusArea by remember { mutableStateOf("Leadership & Advancement") }

    LaunchedEffect(isSubmitting) {
        if (isSubmitting) {
            delay(1400) // Realistic thoughtful synthesis duration
            val report = onGenerateReading(selectedCategory, questionText)
            onReadingGenerated(report)
        }
    }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "How can we guide you?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CelestialTextPrimary
                    )
                    Text(
                        text = "Choose a topic or ask anything with transparency",
                        fontSize = 12.5.sp,
                        color = CelestialTextMuted
                    )
                }
                CelestialBadge(text = "$creditBalance Credits", color = CelestialGold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isSubmitting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = CelestialGold, modifier = Modifier.size(46.dp))
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Synthesizing Planetary Sutras...",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CelestialTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Checking Parashari yogas, transit Gochara & classical references",
                            fontSize = 12.5.sp,
                            color = CelestialTextMuted
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Custom Question Composer
                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        placeholder = { Text("Type your question here...", color = CelestialTextMuted) },
                        trailingIcon = {
                            IconButton(onClick = { isSubmitting = true }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Submit",
                                    tint = CelestialGold
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CelestialTextPrimary,
                            unfocusedTextColor = CelestialTextPrimary,
                            focusedBorderColor = CelestialGold,
                            unfocusedBorderColor = CelestialBorderStrong,
                            focusedContainerColor = CelestialSurface2,
                            unfocusedContainerColor = CelestialSurface2
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ask_question_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "EXPLORE CATEGORIES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2-Column Category Grid
                    val categories = QuestionCategory.entries
                    val chunked = categories.chunked(2)

                    chunked.forEach { rowCategories ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowCategories.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) CelestialGoldWash else CelestialSurface2)
                                        .border(
                                            1.dp,
                                            if (isSelected) CelestialGold else CelestialBorder,
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable {
                                            selectedCategory = cat
                                            questionText = cat.sampleQuestion
                                        }
                                        .padding(14.dp)
                                        .testTag("category_${cat.name.lowercase()}"),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Column {
                                        Icon(
                                            imageVector = when (cat) {
                                                QuestionCategory.CAREER -> Icons.Default.Work
                                                QuestionCategory.RELATIONSHIPS -> Icons.Default.Favorite
                                                QuestionCategory.MONEY -> Icons.Default.Payments
                                                QuestionCategory.MARRIAGE -> Icons.Default.FavoriteBorder
                                                QuestionCategory.HEALTH -> Icons.Default.Spa
                                                QuestionCategory.EDUCATION -> Icons.Default.School
                                                QuestionCategory.HOME -> Icons.Default.Home
                                                QuestionCategory.TRAVEL -> Icons.Default.Flight
                                                QuestionCategory.SPIRITUALITY -> Icons.Default.AutoAwesome
                                                QuestionCategory.TIMING -> Icons.Default.Schedule
                                            },
                                            contentDescription = null,
                                            tint = if (isSelected) CelestialGold else CelestialTextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = cat.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) CelestialGoldStrong else CelestialTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Adaptive Contextual Questionnaire for selected topic
                    CelestialCard(backgroundColor = CelestialSurface2) {
                        Text(
                            text = "Contextual Clarification for ${selectedCategory.title}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CelestialGoldStrong
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Providing extra context enables more precise planetary transit correlation.",
                            fontSize = 12.sp,
                            color = CelestialTextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Primary Timing Window Focus", fontSize = 11.5.sp, color = CelestialTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Next 6 Months", "Next 12–18 Months", "Next 2–3 Years").forEach { option ->
                                val isSelected = contextTimingPreference == option
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CelestialGoldWash else CelestialSurface3)
                                        .border(0.75.dp, if (isSelected) CelestialGold else CelestialBorder, RoundedCornerShape(8.dp))
                                    .clickable { contextTimingPreference = option }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 11.sp,
                                        color = if (isSelected) CelestialGoldStrong else CelestialTextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Primary Focus Dimension", fontSize = 11.5.sp, color = CelestialTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Advancement", "Stability & Risk", "Transitions").forEach { option ->
                                val isSelected = contextFocusArea == option
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CelestialGoldWash else CelestialSurface3)
                                        .border(0.75.dp, if (isSelected) CelestialGold else CelestialBorder, RoundedCornerShape(8.dp))
                                        .clickable { contextFocusArea = option }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = option,
                                        fontSize = 11.sp,
                                        color = if (isSelected) CelestialGoldStrong else CelestialTextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selected Profile & Pricing Preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CelestialBgElevated)
                            .border(1.dp, CelestialBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("PROFILE SUBJECT", fontSize = 10.sp, color = CelestialTextMuted, fontWeight = FontWeight.Bold)
                            Text("${profile.name} (Thula Rasi)", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("INQUIRY COST", fontSize = 10.sp, color = CelestialTextMuted, fontWeight = FontWeight.Bold)
                            Text("1 Question Credit", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CelestialPositive)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    CelestialButton(
                        text = "Generate Reading Report",
                        onClick = { isSubmitting = true },
                        testTag = "generate_reading_button"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DemoDisclaimerBadge(text = "Prototype preview • No real currency charged")

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
