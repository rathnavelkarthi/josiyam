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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.models.*
import com.example.ui.components.CelestialBackground
import com.example.ui.components.CelestialBadge
import com.example.ui.components.CelestialButton
import com.example.ui.components.CelestialOutlinedButton
import com.example.ui.components.CelestialCard
import com.example.ui.components.DemoDisclaimerBadge
import com.example.ui.theme.*

@Composable
fun LibraryScreen(
    sourceWorks: List<SourceWork>,
    savedReadings: List<ReadingReport>,
    milestones: List<LifeMilestone>,
    onOpenReading: (ReadingReport) -> Unit,
    onAddMilestone: (LifeMilestone) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Grantha Library, 1: Saved Readings, 2: Life Timeline
    val tabs = listOf("Grantha Library", "Saved Readings", "Life Timeline")

    var selectedSource by remember { mutableStateOf<SourceWork?>(null) }
    var showAddMilestoneDialog by remember { mutableStateOf(false) }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Library & Heritage",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestialTextPrimary
            )
            Text(
                text = "Authentic classical manuscripts, readings & life milestones",
                fontSize = 12.5.sp,
                color = CelestialTextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

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

            when (selectedTab) {
                0 -> {
                    // Grantha Classical Sources
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sourceWorks) { work ->
                            CelestialCard(
                                backgroundColor = CelestialSurface2,
                                onClick = { selectedSource = work }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(work.sanskritTitle, fontSize = 12.sp, color = CelestialGold, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(work.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                                        Text("By ${work.authorOrTradition} • ${work.period}", fontSize = 12.sp, color = CelestialTextSecondary)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(work.chapter, fontSize = 11.5.sp, color = CelestialSky)
                                    }
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = CelestialGold)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Saved Readings
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (savedReadings.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No saved readings yet. Ask a question to generate one.", color = CelestialTextMuted)
                                }
                            }
                        } else {
                            items(savedReadings) { r ->
                                CelestialCard(
                                    backgroundColor = CelestialSurface2,
                                    onClick = { onOpenReading(r) }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                CelestialBadge(text = r.category.title, color = CelestialSky)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(r.dateIssued, fontSize = 11.sp, color = CelestialTextMuted)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(r.title, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                                            Text(r.summary, fontSize = 12.sp, color = CelestialTextSecondary, maxLines = 2)
                                        }
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = CelestialGold)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Life Timeline
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Button(
                                onClick = { showAddMilestoneDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CelestialGold, contentColor = CelestialBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Log New Life Milestone", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        items(milestones) { m ->
                            CelestialCard(backgroundColor = CelestialSurface2) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CelestialBadge(text = m.category, color = CelestialGold)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(m.dateDisplay, fontSize = 11.5.sp, color = CelestialTextMuted)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(m.title, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                                        Text(m.notes, fontSize = 12.5.sp, color = CelestialTextSecondary)
                                    }
                                    if (m.activeDasaAtEvent.isNotEmpty()) {
                                        CelestialBadge(text = m.activeDasaAtEvent, color = CelestialSky)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Source Inspector Dialog
    if (selectedSource != null) {
        val src = selectedSource!!
        AlertDialog(
            onDismissRequest = { selectedSource = null },
            containerColor = CelestialSurface2,
            title = {
                Column {
                    Text(src.sanskritTitle, color = CelestialGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(src.title, color = CelestialTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("${src.authorOrTradition} • ${src.verseLocator}", color = CelestialTextMuted, fontSize = 11.5.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CelestialBgElevated)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("ORIGINAL VERSE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CelestialGold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(src.originalVerse, fontSize = 14.sp, color = CelestialTextPrimary, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(src.transliteration, fontSize = 12.sp, color = CelestialSky)
                        }
                    }

                    Text("ENGLISH TRANSLATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CelestialGold)
                    Text(src.translation, fontSize = 13.sp, color = CelestialTextPrimary, lineHeight = 18.sp)

                    Text("TRADITIONAL COMMENTARY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CelestialGold)
                    Text(src.commentary, fontSize = 12.5.sp, color = CelestialTextSecondary, lineHeight = 17.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedSource = null }) {
                    Text("Close", color = CelestialGold)
                }
            }
        )
    }

    // Add Milestone Dialog
    if (showAddMilestoneDialog) {
        var mTitle by remember { mutableStateOf("") }
        var mDate by remember { mutableStateOf("") }
        var mCat by remember { mutableStateOf("Career") }
        var mNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMilestoneDialog = false },
            containerColor = CelestialSurface2,
            title = {
                Text("Log Life Milestone", color = CelestialGold, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = mTitle,
                        onValueChange = { mTitle = it },
                        label = { Text("Milestone Title", color = CelestialTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CelestialTextPrimary,
                            unfocusedTextColor = CelestialTextPrimary,
                            focusedBorderColor = CelestialGold,
                            unfocusedBorderColor = CelestialBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = mDate,
                        onValueChange = { mDate = it },
                        label = { Text("Date (e.g. Oct 2025)", color = CelestialTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CelestialTextPrimary,
                            unfocusedTextColor = CelestialTextPrimary,
                            focusedBorderColor = CelestialGold,
                            unfocusedBorderColor = CelestialBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = mNotes,
                        onValueChange = { mNotes = it },
                        label = { Text("Notes / Reflections", color = CelestialTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CelestialTextPrimary,
                            unfocusedTextColor = CelestialTextPrimary,
                            focusedBorderColor = CelestialGold,
                            unfocusedBorderColor = CelestialBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (mTitle.isNotBlank()) {
                        onAddMilestone(
                            LifeMilestone(
                                id = "m_${System.currentTimeMillis()}",
                                title = mTitle,
                                dateDisplay = if (mDate.isNotBlank()) mDate else "Recent",
                                category = mCat,
                                notes = mNotes,
                                activeDasaAtEvent = "Jupiter Dasa"
                            )
                        )
                        showAddMilestoneDialog = false
                    }
                }) {
                    Text("Save Milestone", color = CelestialGold, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMilestoneDialog = false }) {
                    Text("Cancel", color = CelestialTextMuted)
                }
            }
        )
    }
}

@Composable
fun ProfileScreen(
    profile: BirthProfile,
    allProfiles: List<BirthProfile>,
    tradition: AstrologyTradition,
    calendar: CalendarConvention,
    credits: Int,
    userEmail: String? = null,
    currentLanguage: com.example.data.localization.AppLanguage = com.example.data.localization.AppLanguage.ENGLISH,
    onChangeLanguage: (com.example.data.localization.AppLanguage) -> Unit = {},
    onSelectProfile: (BirthProfile) -> Unit = {},
    onEditProfile: () -> Unit,
    onAddProfile: () -> Unit,
    onDeleteProfile: (String) -> Unit = {},
    onChangeTradition: () -> Unit,
    onRechargeCredits: () -> Unit,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showExportDialog by remember { mutableStateOf(false) }
    var profileToDelete by remember { mutableStateOf<BirthProfile?>(null) }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Profile & Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestialTextPrimary
            )
            Text(
                text = "Manage your birth data, traditions & privacy controls",
                fontSize = 12.5.sp,
                color = CelestialTextMuted
            )

            Spacer(modifier = Modifier.height(18.dp))

            // User Identity Card
            CelestialCard(backgroundColor = CelestialSurface2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(CelestialGoldWash)
                                .border(1.5.dp, CelestialGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(profile.name.take(1), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CelestialGoldStrong)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(profile.name, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                            Text(userEmail ?: "Verified User", fontSize = 12.5.sp, color = CelestialTextSecondary)
                            Text("Active Profile • Verified Calculation", fontSize = 11.5.sp, color = CelestialSky)
                        }
                    }
                    TextButton(onClick = onEditProfile) {
                        Text("Edit", color = CelestialGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Language Selection Card
            Text(
                text = "APP LANGUAGE / மொழி",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CelestialGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                com.example.data.localization.AppLanguage.entries.forEach { lang ->
                    val isSelected = currentLanguage == lang
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CelestialGoldWash else CelestialSurface2)
                            .border(1.dp, if (isSelected) CelestialGold else CelestialBorder, RoundedCornerShape(12.dp))
                            .clickable { onChangeLanguage(lang) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = lang.nativeName,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CelestialGoldStrong else CelestialTextPrimary
                            )
                            Text(
                                text = lang.englishName,
                                fontSize = 10.sp,
                                color = CelestialTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Saved Birth Profiles
            Text(
                text = "BIRTH PROFILES (${allProfiles.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CelestialGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            allProfiles.forEach { p ->
                val isActive = p.id == profile.id
                CelestialCard(
                    backgroundColor = if (isActive) CelestialSurface3 else CelestialSurface2,
                    borderColor = if (isActive) CelestialGold else CelestialBorder,
                    onClick = { onSelectProfile(p) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(p.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                                if (isActive) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CelestialBadge(text = "ACTIVE", color = CelestialGold)
                                } else if (p.isDefault) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CelestialBadge(text = "DEFAULT", color = CelestialPositive)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${p.dateOfBirth} at ${p.timeOfBirth}", fontSize = 12.5.sp, color = CelestialTextSecondary)
                            Text(p.placeName, fontSize = 11.5.sp, color = CelestialTextMuted)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onEditProfile) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CelestialTextMuted, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { profileToDelete = p },
                                modifier = Modifier.testTag("delete_profile_${p.id}")
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Profile", tint = CelestialNegative, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = onAddProfile,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CelestialSurface3, contentColor = CelestialTextPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Family Member Chart", fontSize = 13.5.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Astrology Settings
            Text(
                text = "ASTROLOGY CONVENTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CelestialGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            CelestialCard(backgroundColor = CelestialSurface2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Tradition", fontSize = 12.sp, color = CelestialTextMuted)
                        Text(tradition.label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                        Text(tradition.subtitle, fontSize = 11.5.sp, color = CelestialTextSecondary)
                    }
                    TextButton(onClick = onChangeTradition) {
                        Text("Change", color = CelestialGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Question Credits Ledger Card
            CelestialCard(backgroundColor = CelestialSurface2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("QUESTION CREDITS", fontSize = 10.5.sp, color = CelestialTextMuted, fontWeight = FontWeight.Bold)
                        Text("$credits Available", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = CelestialGoldStrong)
                        Text("1 credit per inquiry report", fontSize = 12.sp, color = CelestialTextSecondary)
                    }
                    Button(
                        onClick = onRechargeCredits,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CelestialGold, contentColor = CelestialBg)
                    ) {
                        Text("+5 Credits (Demo)", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Privacy & Export
            Text(
                text = "PRIVACY & SECURITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CelestialGold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            CelestialCard(backgroundColor = CelestialSurface2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showExportDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Export Chart & Readings Data", fontSize = 13.5.sp, color = CelestialTextPrimary)
                    Icon(Icons.Default.Download, contentDescription = null, tint = CelestialGold)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CelestialBorder)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Clear Local Biometric Scans", fontSize = 13.5.sp, color = CelestialNegative)
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = CelestialNegative)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            CelestialOutlinedButton(
                text = "Sign Out of Account",
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                testTag = "profile_sign_out_button"
            )

            Spacer(modifier = Modifier.height(16.dp))

            DemoDisclaimerBadge(text = "Ephemeral local storage • Zero telemetry • Private by design")

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = CelestialSurface2,
            title = {
                Text("Export Profile Data", color = CelestialGold, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            },
            text = {
                Text(
                    "Your birth profile parameters (${profile.name}, ${profile.dateOfBirth}, ${profile.placeName}) and reading history can be exported as a standard JSON bundle.",
                    color = CelestialTextPrimary,
                    fontSize = 13.5.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Download JSON", color = CelestialGold, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close", color = CelestialTextMuted)
                }
            }
        )
    }

    profileToDelete?.let { targetProfile ->
        AlertDialog(
            onDismissRequest = { profileToDelete = null },
            containerColor = CelestialSurface2,
            title = {
                Text("Delete Profile?", color = CelestialNegative, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Are you sure you want to permanently delete \"${targetProfile.name}\" and all its calculated charts, numerology analyses, and associated readings? This cannot be undone.",
                    color = CelestialTextPrimary,
                    fontSize = 13.5.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = targetProfile.id
                        profileToDelete = null
                        onDeleteProfile(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CelestialNegative),
                    modifier = Modifier.testTag("confirm_delete_profile_btn")
                ) {
                    Text("Delete Permanently", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { profileToDelete = null }) {
                    Text("Cancel", color = CelestialTextSecondary)
                }
            }
        )
    }
}
