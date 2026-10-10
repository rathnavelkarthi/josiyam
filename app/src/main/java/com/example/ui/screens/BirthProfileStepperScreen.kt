package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BirthProfile
import com.example.data.models.BirthTimeConfidence
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.BirthProfileSetupViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun BirthProfileStepperScreen(
    onProfileSaved: (BirthProfile) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    setupViewModel: BirthProfileSetupViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by setupViewModel.uiState.collectAsState()

    var step by remember { mutableIntStateOf(1) } // 1, 2, or 3
    var name by remember { mutableStateOf(uiState.name) }
    var dateOfBirth by remember { mutableStateOf(uiState.dateOfBirth) }
    var timeOfBirth by remember { mutableStateOf(uiState.timeOfBirth) }
    var confidence by remember { mutableStateOf(uiState.confidence) }

    var placeQuery by remember { mutableStateOf(uiState.placeName) }
    var latitude by remember { mutableDoubleStateOf(uiState.latitude) }
    var longitude by remember { mutableDoubleStateOf(uiState.longitude) }
    var timezoneId by remember { mutableStateOf(uiState.timezoneId) }

    var isDefault by remember { mutableStateOf(uiState.isDefault) }
    var isCalculating by remember { mutableStateOf(false) }

    val citySuggestions = listOf(
        Triple("Chennai, Tamil Nadu, India", 13.0827 to 80.2707, "Asia/Kolkata (GMT+05:30)"),
        Triple("Madurai, Tamil Nadu, India", 9.9252 to 78.1198, "Asia/Kolkata (GMT+05:30)"),
        Triple("Coimbatore, Tamil Nadu, India", 11.0168 to 76.9558, "Asia/Kolkata (GMT+05:30)"),
        Triple("Bengaluru, Karnataka, India", 12.9716 to 77.5946, "Asia/Kolkata (GMT+05:30)"),
        Triple("Singapore", 1.3521 to 103.8198, "Asia/Singapore (GMT+08:00)"),
        Triple("London, United Kingdom", 51.5074 to -0.1278, "Europe/London (GMT+00:00)"),
        Triple("New York, United States", 40.7128 to -74.0060, "America/New_York (GMT-05:00)")
    )

    LaunchedEffect(isCalculating) {
        if (isCalculating) {
            setupViewModel.updateName(name)
            setupViewModel.updateDateOfBirth(dateOfBirth)
            setupViewModel.updateTimeOfBirth(timeOfBirth)
            setupViewModel.updateConfidence(confidence)
            setupViewModel.updateLocation(placeQuery, latitude, longitude, timezoneId)

            setupViewModel.saveProfile(context) { savedProfile ->
                onProfileSaved(savedProfile)
            }
        }
    }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp, vertical = 16.dp)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        if (step > 1) step-- else onBack()
                    },
                    modifier = Modifier.testTag("birth_profile_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CelestialTextPrimary
                    )
                }

                Text(
                    text = "Step $step of 3",
                    fontSize = 13.sp,
                    color = CelestialGold,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Step Progress Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (s in 1..3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (s <= step) CelestialGold else CelestialSurface3
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isCalculating) {
                // Calculation loading state
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
                        CircularProgressIndicator(
                            color = CelestialGold,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Calculating Celestial Placements...",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CelestialTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Applying Lahiri Ayanamsha (Chitrapaksha) & South Indian Kattam houses",
                            fontSize = 13.sp,
                            color = CelestialTextMuted,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (step) {
                        1 -> Step1Basics(
                            name = name,
                            onNameChange = { name = it },
                            dob = dateOfBirth,
                            onDobChange = { dateOfBirth = it },
                            tob = timeOfBirth,
                            onTobChange = { timeOfBirth = it },
                            confidence = confidence,
                            onConfidenceChange = { confidence = it }
                        )
                        2 -> Step2Location(
                            placeQuery = placeQuery,
                            onPlaceQueryChange = { placeQuery = it },
                            latitude = latitude,
                            longitude = longitude,
                            timezone = timezoneId,
                            citySuggestions = citySuggestions,
                            onSelectCity = { city, coords, tz ->
                                placeQuery = city
                                latitude = coords.first
                                longitude = coords.second
                                timezoneId = tz
                            }
                        )
                        3 -> Step3Confirm(
                            name = name,
                            dob = dateOfBirth,
                            tob = timeOfBirth,
                            confidence = confidence,
                            place = placeQuery,
                            timezone = timezoneId,
                            isDefault = isDefault,
                            onDefaultChange = { isDefault = it },
                            onEditStep = { step = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CelestialButton(
                    text = if (step < 3) "Continue →" else "Save and Calculate Chart",
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            isCalculating = true
                        }
                    },
                    testTag = "birth_profile_continue"
                )
            }
        }
    }
}

@Composable
private fun Step1Basics(
    name: String,
    onNameChange: (String) -> Unit,
    dob: String,
    onDobChange: (String) -> Unit,
    tob: String,
    onTobChange: (String) -> Unit,
    confidence: BirthTimeConfidence,
    onConfidenceChange: (BirthTimeConfidence) -> Unit
) {
    Text(
        text = "Your Birth Details",
        fontSize = 26.sp,
        fontWeight = FontWeight.SemiBold,
        color = CelestialTextPrimary
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Accurate birth details reveal your precise eastern horizon (Lagnam) and Dasa cycles.",
        fontSize = 13.5.sp,
        color = CelestialTextSecondary
    )

    Spacer(modifier = Modifier.height(22.dp))

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Profile Label / Native Name", color = CelestialTextMuted) },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CelestialGold) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = CelestialTextPrimary,
            unfocusedTextColor = CelestialTextPrimary,
            focusedBorderColor = CelestialGold,
            unfocusedBorderColor = CelestialBorder,
            focusedContainerColor = CelestialSurface2,
            unfocusedContainerColor = CelestialSurface2
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
    )

    Spacer(modifier = Modifier.height(14.dp))

    val context = LocalContext.current

    // Date Picker launcher
    val openDatePicker = {
        val cal = Calendar.getInstance()
        try {
            val df = SimpleDateFormat("dd MMM yyyy", Locale.US)
            val parsed = df.parse(dob.trim())
            if (parsed != null) cal.time = parsed
        } catch (_: Exception) {}

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(year, month, dayOfMonth)
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.US)
                onDobChange(formatter.format(selectedCal.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Time Picker launcher
    val openTimePicker = {
        val cal = Calendar.getInstance()
        try {
            val tf = SimpleDateFormat("hh:mm a", Locale.US)
            val parsed = tf.parse(tob.trim())
            if (parsed != null) {
                val tCal = Calendar.getInstance()
                tCal.time = parsed
                cal.set(Calendar.HOUR_OF_DAY, tCal.get(Calendar.HOUR_OF_DAY))
                cal.set(Calendar.MINUTE, tCal.get(Calendar.MINUTE))
            }
        } catch (_: Exception) {}

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCal.set(Calendar.MINUTE, minute)
                val formatter = SimpleDateFormat("hh:mm a", Locale.US)
                onTobChange(formatter.format(selectedCal.time))
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false // 12 hour with AM/PM
        ).show()
    }

    OutlinedTextField(
        value = dob,
        onValueChange = onDobChange,
        label = { Text("Date of Birth (Tap icon for Calendar)", color = CelestialTextMuted) },
        leadingIcon = {
            IconButton(onClick = openDatePicker) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = CelestialGold)
            }
        },
        trailingIcon = {
            TextButton(onClick = openDatePicker) {
                Text("Pick", color = CelestialGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = CelestialTextPrimary,
            unfocusedTextColor = CelestialTextPrimary,
            focusedBorderColor = CelestialGold,
            unfocusedBorderColor = CelestialBorder,
            focusedContainerColor = CelestialSurface2,
            unfocusedContainerColor = CelestialSurface2
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag("profile_dob_input")
    )

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
        value = tob,
        onValueChange = onTobChange,
        label = { Text("Time of Birth (Tap icon for Clock)", color = CelestialTextMuted) },
        leadingIcon = {
            IconButton(onClick = openTimePicker) {
                Icon(Icons.Default.Schedule, contentDescription = "Pick Time", tint = CelestialGold)
            }
        },
        trailingIcon = {
            TextButton(onClick = openTimePicker) {
                Text("Pick", color = CelestialGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = CelestialTextPrimary,
            unfocusedTextColor = CelestialTextPrimary,
            focusedBorderColor = CelestialGold,
            unfocusedBorderColor = CelestialBorder,
            focusedContainerColor = CelestialSurface2,
            unfocusedContainerColor = CelestialSurface2
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag("profile_tob_input")
    )

    // Quick presets for time
    Spacer(modifier = Modifier.height(6.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf("06:00 AM" to "Dawn", "12:00 PM" to "Noon", "04:30 PM" to "Evening", "11:30 PM" to "Night").forEach { (timeStr, label) ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (tob == timeStr) CelestialGoldWash else CelestialSurface3)
                    .border(1.dp, if (tob == timeStr) CelestialGold else CelestialBorder, RoundedCornerShape(8.dp))
                    .clickable { onTobChange(timeStr) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = if (tob == timeStr) CelestialGoldStrong else CelestialTextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "BIRTH-TIME CONFIDENCE",
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        color = CelestialGold,
        letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    BirthTimeConfidence.entries.forEach { conf ->
        val isSelected = confidence == conf
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) CelestialSurface3 else CelestialSurface2.copy(alpha = 0.5f))
                .border(1.dp, if (isSelected) CelestialGold else CelestialBorder, RoundedCornerShape(12.dp))
                .clickable { onConfidenceChange(conf) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { onConfidenceChange(conf) },
                colors = RadioButtonDefaults.colors(selectedColor = CelestialGold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = conf.label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) CelestialGoldStrong else CelestialTextPrimary
                )
                Text(
                    text = conf.description,
                    fontSize = 11.5.sp,
                    color = CelestialTextMuted
                )
            }
        }
    }
}

@Composable
private fun Step2Location(
    placeQuery: String,
    onPlaceQueryChange: (String) -> Unit,
    latitude: Double,
    longitude: Double,
    timezone: String,
    citySuggestions: List<Triple<String, Pair<Double, Double>, String>>,
    onSelectCity: (String, Pair<Double, Double>, String) -> Unit
) {
    Text(
        text = "Birth Location",
        fontSize = 26.sp,
        fontWeight = FontWeight.SemiBold,
        color = CelestialTextPrimary
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Geographical coordinates and historical timezone determine exact horizon calculation.",
        fontSize = 13.5.sp,
        color = CelestialTextSecondary
    )

    Spacer(modifier = Modifier.height(22.dp))

    OutlinedTextField(
        value = placeQuery,
        onValueChange = onPlaceQueryChange,
        label = { Text("Search Birth City, State, Country", color = CelestialTextMuted) },
        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = CelestialGold) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = CelestialTextPrimary,
            unfocusedTextColor = CelestialTextPrimary,
            focusedBorderColor = CelestialGold,
            unfocusedBorderColor = CelestialBorder,
            focusedContainerColor = CelestialSurface2,
            unfocusedContainerColor = CelestialSurface2
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().testTag("profile_place_input")
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Resolved Coordinates Card
    CelestialCard(backgroundColor = CelestialSurface2) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("COORDINATES", fontSize = 10.sp, color = CelestialTextMuted, fontWeight = FontWeight.Bold)
                Text(
                    "${String.format("%.4f", latitude)}° N, ${String.format("%.4f", longitude)}° E",
                    fontSize = 13.sp,
                    color = CelestialTextPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("TIMEZONE", fontSize = 10.sp, color = CelestialTextMuted, fontWeight = FontWeight.Bold)
                Text(timezone, fontSize = 13.sp, color = CelestialSky, fontWeight = FontWeight.Medium)
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Text(
        text = "COMMON SUGGESTIONS",
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        color = CelestialGold,
        letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    citySuggestions.forEach { (city, coords, tz) ->
        val isCurrent = placeQuery == city
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isCurrent) CelestialSurface3 else CelestialSurface2.copy(alpha = 0.4f))
                .border(0.75.dp, if (isCurrent) CelestialGold else CelestialBorder, RoundedCornerShape(10.dp))
                .clickable { onSelectCity(city, coords, tz) }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = if (isCurrent) CelestialGold else CelestialTextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = city,
                    fontSize = 13.sp,
                    color = if (isCurrent) CelestialGoldStrong else CelestialTextPrimary
                )
            }
            if (isCurrent) {
                Icon(Icons.Default.Check, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun Step3Confirm(
    name: String,
    dob: String,
    tob: String,
    confidence: BirthTimeConfidence,
    place: String,
    timezone: String,
    isDefault: Boolean,
    onDefaultChange: (Boolean) -> Unit,
    onEditStep: (Int) -> Unit
) {
    Text(
        text = "Confirm Details",
        fontSize = 26.sp,
        fontWeight = FontWeight.SemiBold,
        color = CelestialTextPrimary
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Review your parameters before we calculate your South Indian Kattam and Dasa timeline.",
        fontSize = 13.5.sp,
        color = CelestialTextSecondary
    )

    Spacer(modifier = Modifier.height(20.dp))

    CelestialCard(backgroundColor = CelestialSurface2) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("NAME & BASICS", fontSize = 10.5.sp, color = CelestialGold, fontWeight = FontWeight.Bold)
                Text(name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CelestialTextPrimary)
                Text("$dob at $tob", fontSize = 13.5.sp, color = CelestialTextSecondary)
                Text("Time Precision: ${confidence.label}", fontSize = 12.sp, color = CelestialSky)
            }
            TextButton(onClick = { onEditStep(1) }) {
                Text("Edit", color = CelestialGold, fontSize = 13.sp)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CelestialBorder)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("LOCATION & TIMEZONE", fontSize = 10.5.sp, color = CelestialGold, fontWeight = FontWeight.Bold)
                Text(place, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CelestialTextPrimary)
                Text(timezone, fontSize = 12.5.sp, color = CelestialTextSecondary)
            }
            TextButton(onClick = { onEditStep(2) }) {
                Text("Edit", color = CelestialGold, fontSize = 13.sp)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CelestialSurface2)
            .border(1.dp, CelestialBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isDefault,
            onCheckedChange = onDefaultChange,
            colors = CheckboxDefaults.colors(
                checkedColor = CelestialGold,
                uncheckedColor = CelestialBorderStrong,
                checkmarkColor = CelestialBg
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = "Save as default profile",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestialTextPrimary
            )
            Text(
                text = "Used automatically for all questions and daily Panchangam.",
                fontSize = 11.5.sp,
                color = CelestialTextMuted
            )
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    DemoDisclaimerBadge(text = "Ephemeral demonstration calculations • No external storage")
}
