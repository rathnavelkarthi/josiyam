package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.models.AstronomicalEvent
import com.example.data.models.BirthProfile
import com.example.data.models.ChatMessage
import com.example.data.models.PanchangamSnapshot
import com.example.ui.components.CelestialBackground
import com.example.ui.components.CelestialBadge
import com.example.ui.components.CelestialCard
import com.example.ui.components.DemoDisclaimerBadge
import com.example.ui.theme.*

@Composable
fun PanchangamScreen(
    panchangam: PanchangamSnapshot,
    astronomicalEvents: List<AstronomicalEvent>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("panchangam_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CelestialTextPrimary
                    )
                }
                Text(
                    text = "Tamil Panchangam & Sky",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialTextPrimary
                )
                Box(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Primary Calendar Date Header Card
                CelestialCard(backgroundColor = CelestialSurface2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            CelestialBadge(text = "SOLAR & LUNAR PANCHANGAM", color = CelestialGold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = panchangam.tamilDate,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CelestialTextPrimary
                            )
                            Text(
                                text = "${panchangam.dateDisplay} • ${panchangam.location}",
                                fontSize = 12.5.sp,
                                color = CelestialTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(CelestialGoldWash),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("☀️", fontSize = 20.sp)
                        }
                    }
                }

                // 5 Limbs (Pancha-Angas) Card
                CelestialCard(backgroundColor = CelestialSurface) {
                    Text(
                        text = "The Five Limbs (Pancha-Angas)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CelestialGoldStrong
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LimbRow(title = "1. Tithi (Lunar Phase)", value = panchangam.tithi, timing = panchangam.tithiEnd)
                    LimbRow(title = "2. Vara (Solar Day)", value = "Bhanuvara (Sunday)", timing = "Sun-ruled day")
                    LimbRow(title = "3. Nakshatra (Star)", value = panchangam.nakshatra, timing = panchangam.nakshatraEnd)
                    LimbRow(title = "4. Yoga (Angular Sum)", value = panchangam.yoga, timing = "Auspicious duration")
                    LimbRow(title = "5. Karana (Half-Tithi)", value = panchangam.karana, timing = "Active phase")
                }

                // Auspicious and Inauspicious Times
                CelestialCard(backgroundColor = CelestialSurface2) {
                    Text(
                        text = "Day Timings & Muhurthams",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CelestialTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    TimingRow("Shubha Muhurtham", panchangam.shubhaMuhurtham, CelestialPositive, "Favourable window")
                    TimingRow("Brahma Muhurtham", "04:30 AM – 05:18 AM", CelestialGold, "Meditation & prayer")
                    TimingRow("Rahu Kalam", panchangam.rahuKalam, CelestialCaution, "Inauspicious")
                    TimingRow("Yamagandam", panchangam.yamagandam, CelestialCaution, "Inauspicious")
                    TimingRow("Gulika Kalam", panchangam.gulikaKalam, CelestialSky, "Neutral / routine tasks")
                }

                // Astronomical Ephemeris Events Card
                CelestialCard(backgroundColor = CelestialSurface) {
                    Text("Astronomical Alignments", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialSky)
                    Spacer(modifier = Modifier.height(10.dp))

                    astronomicalEvents.forEach { event ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(event.title, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                                CelestialBadge(text = event.dateDisplay, color = CelestialSky)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "ASTRONOMY: ${event.astronomicalFact}",
                                fontSize = 11.5.sp,
                                color = CelestialSky,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "VEDIC TRADITION: ${event.traditionalInterpretation}",
                                fontSize = 11.5.sp,
                                color = CelestialTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = CelestialBorder)
                    }
                }

                DemoDisclaimerBadge(text = "Astronomical calculations based on geocentric algorithms for Chennai, India.")
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LimbRow(title: String, value: String, timing: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontSize = 12.sp, color = CelestialTextMuted)
            Text(value, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = CelestialTextPrimary)
        }
        Text(timing, fontSize = 11.5.sp, color = CelestialGoldStrong)
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 3.dp), color = CelestialBorder)
}

@Composable
private fun TimingRow(label: String, time: String, color: Color, tag: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, color = CelestialTextPrimary)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(time, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = color)
            Spacer(modifier = Modifier.width(6.dp))
            Text("($tag)", fontSize = 10.5.sp, color = CelestialTextMuted)
        }
    }
}

@Composable
fun SiddhaAiScreen(
    profile: BirthProfile,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }

    val promptSuggestions = listOf(
        "When will I buy a house?",
        "Is this a good time to start a business?",
        "What are my career prospects?",
        "Which temple should I visit?",
        "Explain my Moon Rasi"
    )

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // AI Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CelestialGoldWash)
                            .border(1.dp, CelestialGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Siddha AI", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                        Text("Online • Grounded in classical Shastras", fontSize = 11.5.sp, color = CelestialPositive)
                    }
                }
                CelestialBadge(text = "Thula Lagna", color = CelestialGold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Context Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CelestialSurface2)
                    .border(0.75.dp, CelestialBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Context: ${profile.name} • Janma Rasi Thula • Jupiter Mahadasa active",
                    fontSize = 11.5.sp,
                    color = CelestialSky
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Message History List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    if (msg.sender == "user") {
                        // User message bubble
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CelestialSurface3)
                                    .border(1.dp, CelestialBorderStrong, RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    fontSize = 14.sp,
                                    color = CelestialTextPrimary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    } else {
                        // Siddha Assistant message card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(CelestialSurface2)
                                .border(1.dp, CelestialBorder, RoundedCornerShape(18.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Siddha AI", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CelestialGoldStrong)
                                    }
                                    if (msg.tag != null) {
                                        CelestialBadge(text = msg.tag, color = CelestialSky)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = msg.text,
                                    fontSize = 13.5.sp,
                                    color = CelestialTextPrimary,
                                    lineHeight = 20.sp
                                )

                                if (msg.citations.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CelestialBgElevated)
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "Classical Canon: ${msg.citations.joinToString(" • ")}",
                                            fontSize = 11.sp,
                                            color = CelestialGold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Suggestion Prompt Chips Scroll
            ScrollableTabRow(
                selectedTabIndex = 0,
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                divider = {},
                indicator = {}
            ) {
                promptSuggestions.forEach { prompt ->
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(CelestialSurface2)
                            .border(0.75.dp, CelestialBorder, RoundedCornerShape(999.dp))
                            .clickable {
                                onSendMessage(prompt)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(prompt, fontSize = 11.5.sp, color = CelestialTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Message Composer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask your question...", color = CelestialTextMuted, fontSize = 13.5.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CelestialTextPrimary,
                        unfocusedTextColor = CelestialTextPrimary,
                        focusedBorderColor = CelestialGold,
                        unfocusedBorderColor = CelestialBorder,
                        focusedContainerColor = CelestialSurface2,
                        unfocusedContainerColor = CelestialSurface2
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("siddha_chat_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CelestialGold)
                        .testTag("siddha_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = CelestialBg,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
