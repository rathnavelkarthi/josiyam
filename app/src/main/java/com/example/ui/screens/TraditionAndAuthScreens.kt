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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AstrologyTradition
import com.example.data.models.CalendarConvention
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TraditionSelectionScreen(
    currentTradition: AstrologyTradition,
    currentCalendar: CalendarConvention,
    onTraditionSelected: (AstrologyTradition) -> Unit,
    onCalendarSelected: (CalendarConvention) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showExplainerDialog by remember { mutableStateOf(false) }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("tradition_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CelestialTextPrimary
                    )
                }

                TextButton(onClick = { showExplainerDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CelestialGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "What's the difference?",
                        fontSize = 13.sp,
                        color = CelestialGold,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Your Journey\nYour Tradition",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialTextPrimary,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Different systems. Unique perspectives. Choose what resonates with you.",
                    fontSize = 14.sp,
                    color = CelestialTextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tradition Options
                Text(
                    text = "ASTROLOGY SYSTEM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                AstrologyTradition.entries.forEach { tradition ->
                    val isSelected = currentTradition == tradition
                    TraditionOptionCard(
                        tradition = tradition,
                        isSelected = isSelected,
                        onClick = { onTraditionSelected(tradition) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Calendar convention options
                Text(
                    text = "CALENDAR DISPLAY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CelestialGold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                CalendarConvention.entries.forEach { cal ->
                    val isSelected = currentCalendar == cal
                    CalendarOptionCard(
                        calendar = cal,
                        isSelected = isSelected,
                        onClick = { onCalendarSelected(cal) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            CelestialButton(
                text = "Continue →",
                onClick = onContinue,
                testTag = "tradition_continue_button"
            )
        }
    }

    if (showExplainerDialog) {
        AlertDialog(
            onDismissRequest = { showExplainerDialog = false },
            containerColor = CelestialSurface2,
            title = {
                Text(
                    text = "Zodiac Systems Explained",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "• Sidereal Zodiac (Vedic): References the fixed observable constellations. It uses an offset known as Ayanamsha (e.g. Lahiri) to account for axial precession.",
                        fontSize = 13.sp,
                        color = CelestialTextPrimary,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "• Tropical Zodiac (Western): References Earth's seasons and the vernal equinox. Signs align to solar cycles rather than background stars.",
                        fontSize = 13.sp,
                        color = CelestialTextPrimary,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "• Birthplace coordinates calculate your exact local eastern horizon (Lagna/Ascendant). Your place of birth does not limit which astrology tradition you can study.",
                        fontSize = 13.sp,
                        color = CelestialSky,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showExplainerDialog = false }) {
                    Text("Understood", color = CelestialGold, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

@Composable
private fun TraditionOptionCard(
    tradition: AstrologyTradition,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) CelestialGold else CelestialBorder
    val bgColor = if (isSelected) CelestialGoldWash else CelestialSurface2

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isSelected) CelestialGold.copy(alpha = 0.2f) else CelestialSurface3),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (tradition) {
                    AstrologyTradition.VEDIC_SIDEREAL -> Icons.Default.TempleHindu
                    AstrologyTradition.WESTERN_TROPICAL -> Icons.Default.Public
                    AstrologyTradition.COMPARE_BOTH -> Icons.Default.Balance
                },
                contentDescription = null,
                tint = if (isSelected) CelestialGold else CelestialTextMuted,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tradition.label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestialTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = tradition.subtitle,
                fontSize = 12.sp,
                color = CelestialTextSecondary,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = CelestialGold,
                unselectedColor = CelestialTextMuted
            )
        )
    }
}

@Composable
private fun CalendarOptionCard(
    calendar: CalendarConvention,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) CelestialSurface3 else CelestialSurface2.copy(alpha = 0.5f))
            .border(1.dp, if (isSelected) CelestialGoldStrong else CelestialBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = calendar.label,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) CelestialGoldStrong else CelestialTextPrimary
            )
            Text(
                text = calendar.description,
                fontSize = 11.5.sp,
                color = CelestialTextMuted
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = CelestialGold,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun AuthScreen(
    initialIsSignUp: Boolean = true,
    onSignIn: (email: String, pass: String) -> Unit = { _, _ -> },
    onSignUp: (email: String, pass: String) -> Unit = { _, _ -> },
    onGoogleSignIn: (android.content.Context) -> Unit = {},
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSignUp by remember { mutableStateOf(initialIsSignUp) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(true) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailSent by remember { mutableStateOf(false) }

    CelestialBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("auth_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CelestialTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sacred icon header
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(CelestialGoldWash)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CelestialGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSignUp) "Create Your Account" else "Welcome Back",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = CelestialTextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isSignUp) "Start your journey with the universe." else "Your cosmic placements are waiting.",
                fontSize = 14.sp,
                color = CelestialTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Google Sign In (Highlighted)
            Button(
                onClick = {
                    onGoogleSignIn(context)
                    onAuthSuccess()
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1F1F1F)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("google_signin_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Google",
                    tint = Color(0xFF4285F4),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Continue with Google",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Apple Sign In button as depicted in designs
            Button(
                onClick = onAuthSuccess,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CelestialSurface3,
                    contentColor = CelestialTextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("apple_signin_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Apple",
                    tint = CelestialTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Continue with Apple",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = CelestialBorder)
                Text(
                    text = "  or email  ",
                    fontSize = 12.sp,
                    color = CelestialTextMuted
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = CelestialBorder)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Form Fields
            if (isSignUp) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name", color = CelestialTextMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = CelestialGold)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_name_input")
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address", color = CelestialTextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = CelestialGold)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = CelestialTextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = CelestialGold)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password",
                            tint = CelestialTextMuted
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CelestialTextPrimary,
                    unfocusedTextColor = CelestialTextPrimary,
                    focusedBorderColor = CelestialGold,
                    unfocusedBorderColor = CelestialBorder,
                    focusedContainerColor = CelestialSurface2,
                    unfocusedContainerColor = CelestialSurface2
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input")
            )

            if (!isSignUp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showForgotPasswordDialog = true }) {
                        Text(
                            text = "Forgot Password?",
                            fontSize = 12.5.sp,
                            color = CelestialGold
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreedToTerms,
                        onCheckedChange = { agreedToTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CelestialGold,
                            uncheckedColor = CelestialBorderStrong,
                            checkmarkColor = CelestialBg
                        )
                    )
                    Text(
                        text = "I agree to the Terms of Service & Privacy Policy",
                        fontSize = 12.sp,
                        color = CelestialTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            CelestialButton(
                text = if (isSignUp) "Create Account" else "Sign In",
                onClick = {
                    if (isSignUp) {
                        onSignUp(email, password)
                    } else {
                        onSignIn(email, password)
                    }
                    onAuthSuccess()
                },
                testTag = "auth_submit_button"
            )

            Spacer(modifier = Modifier.height(10.dp))

            CelestialOutlinedButton(
                text = "Continue as Guest",
                onClick = onAuthSuccess,
                testTag = "auth_guest_button"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Switch Mode Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSignUp) "Already have an account? " else "Don't have an account? ",
                    fontSize = 13.5.sp,
                    color = CelestialTextSecondary
                )
                Text(
                    text = if (isSignUp) "Sign In" else "Sign Up",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialGold,
                    modifier = Modifier
                        .clickable { isSignUp = !isSignUp }
                        .testTag("auth_switch_mode_button")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            DemoDisclaimerBadge(text = "Prototype demo authentication • Instant local state")
        }
    }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                resetEmailSent = false
            },
            containerColor = CelestialSurface2,
            title = {
                Text("Reset Password", color = CelestialGold, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            },
            text = {
                if (resetEmailSent) {
                    Text(
                        "A password reset link has been dispatched to $email. Please check your inbox.",
                        color = CelestialPositive,
                        fontSize = 13.5.sp
                    )
                } else {
                    Text(
                        "Enter your email address. We will send a secure link to reset your account credentials.",
                        color = CelestialTextPrimary,
                        fontSize = 13.5.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (resetEmailSent) {
                        showForgotPasswordDialog = false
                        resetEmailSent = false
                    } else {
                        resetEmailSent = true
                    }
                }) {
                    Text(
                        text = if (resetEmailSent) "Done" else "Send Link",
                        color = CelestialGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel", color = CelestialTextMuted)
                }
            }
        )
    }
}
