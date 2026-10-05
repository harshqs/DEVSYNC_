package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrutalistBadge
import com.example.ui.components.BrutalistButton
import com.example.ui.components.BrutalistCard
import com.example.ui.components.TechnicalGridBackground
import com.example.ui.components.TerminalWindowCard
import com.example.ui.theme.DevSyncAcidLime
import com.example.ui.theme.DevSyncBridgeLavender
import com.example.ui.theme.DevSyncCobalt
import com.example.ui.theme.DevSyncInkBlack
import com.example.ui.theme.DevSyncPaperSurface
import com.example.ui.theme.DevSyncScrubPink
import com.example.ui.theme.JetBrainsMonoFontFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(1700)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DevSyncCobalt)
            .clickable { onSplashFinished() }
            .padding(24.dp)
            .testTag("splash_screen_root")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "DEVELOPER TOOLS / ON-DEVICE AI / iQOO HACKATHON 2026",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White.copy(alpha = 0.88f),
                        letterSpacing = 1.sp
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "DEV\nSYNC",
                        style = MaterialTheme.typography.displayLarge.copy(
                            color = Color.White,
                            fontSize = 56.sp,
                            lineHeight = 54.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Box(
                        modifier = Modifier
                            .padding(start = 6.dp, bottom = 8.dp)
                            .width(34.dp)
                            .height(9.dp)
                            .background(DevSyncAcidLime)
                    )
                }

                Text(
                    text = "Your laptop’s errors, solved on your phone.\nOffline. Private.",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        lineHeight = 28.sp
                    )
                )

                BrutalistBadge(
                    text = "DEBUG ANYWHERE / LEAK NOTHING",
                    backgroundColor = DevSyncAcidLime,
                    textColor = DevSyncInkBlack,
                    tiltDegrees = -1.5f,
                    hasHardShadow = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                TerminalWindowCard(
                    command = "npm run build",
                    errorLine = "Cannot find module 'axios'",
                    bridgeStatusLine = "sent to phone over local Wi-Fi",
                    fixReadyCommand = "npm install axios"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DEVSYNC / 2026",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                )
                Text(
                    text = "INITIALIZING PROTOTYPE...",
                    style = MaterialTheme.typography.labelMedium.copy(color = DevSyncAcidLime)
                )
            }
        }
    }
}

@Composable
fun JudgePrototypeNoticeScreen(
    onStartOnboarding: () -> Unit,
    onSkipToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    TechnicalGridBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Top Header Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrutalistBadge(
                    text = "HACKATHON SUBMISSION // PROTOTYPE",
                    backgroundColor = DevSyncCobalt,
                    textColor = Color.White
                )
                BrutalistBadge(
                    text = "STAGE 1 MVP",
                    backgroundColor = DevSyncAcidLime,
                    textColor = DevSyncInkBlack
                )
            }

            // Primary Judge Notice Card
            BrutalistCard(
                backgroundColor = DevSyncPaperSurface,
                shadowColor = DevSyncCobalt,
                shadowOffset = 5.dp,
                contentPadding = PaddingValues(22.dp)
            ) {
                Text(
                    text = "Notice for Hackathon Judges",
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = DevSyncInkBlack,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "This application is an interactive MVP prototype showcase illustrating what we are going to build during the hackathon.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = DevSyncInkBlack,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "It demonstrates our end-to-end product workflow—specifically highlighting the User Onboarding Flow, the Deterministic Privacy Gate (which scrubs secrets locally), and the Primary Task Dashboard—so you can experience how DevSync operates in practice.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF333333)
                    )
                )
            }

            // What We Are Building (Roadmap & Scope Card)
            BrutalistCard(
                backgroundColor = DevSyncBridgeLavender,
                contentPadding = PaddingValues(22.dp)
            ) {
                Text(
                    text = "WHAT WE ARE BUILDING (CORE ARCHITECTURE & ROADMAP)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = DevSyncCobalt
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                JudgeScopeItem(
                    code = "01",
                    title = "Laptop CLI Watcher & Local Wi-Fi Bridge",
                    description = "A lightweight terminal watcher ($ devsync watch) detects non-zero build or test exits on the laptop and streams the failure over local WebSocket to the phone."
                )

                Spacer(modifier = Modifier.height(14.dp))

                JudgeScopeItem(
                    code = "02",
                    title = "Deterministic Privacy Gate (Before AI)",
                    description = "Strict pattern-based rules strip API keys, Bearer tokens, .env credentials, and private filesystem paths before any model inference occurs."
                )

                Spacer(modifier = Modifier.height(14.dp))

                JudgeScopeItem(
                    code = "03",
                    title = "On-Device 4-Bit Local AI & Phone Inputs",
                    description = "A compact quantized local model (with zero internet permission and 0 B sent to the cloud) explains the root cause in plain English, supported by Camera OCR and offline voice commands."
                )

                Spacer(modifier = Modifier.height(14.dp))

                JudgeScopeItem(
                    code = "04",
                    title = "Human Control & Offline Fix Cards",
                    description = "Every suggested command requires manual developer approval and can be saved on-device as a reusable Fix Card for teammates on the local network."
                )
            }

            // What is Interactive Right Now in this Prototype
            BrutalistCard(
                backgroundColor = DevSyncScrubPink,
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "HOW TO TEST THIS PROTOTYPE",
                    style = MaterialTheme.typography.labelLarge.copy(color = DevSyncInkBlack)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "1. Walk through the 3-step User Onboarding Flow (Laptop Pairing, Privacy Gate Rules, and Local 4-Bit Model Setup).\n\n" +
                        "2. Explore the Primary Task Dashboard: inspect scrubbed secrets, trigger simulated laptop build crashes, paste custom terminal logs, and manually approve fixes.\n\n" +
                        "3. Test the Camera OCR to Markdown scanner, Offline Voice triggers, and persistent local Fix Cards.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DevSyncInkBlack)
                )
            }

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BrutalistButton(
                    text = "START USER ONBOARDING FLOW ->",
                    onClick = onStartOnboarding,
                    backgroundColor = DevSyncCobalt,
                    textColor = Color.White,
                    shadowColor = DevSyncInkBlack,
                    testTag = "judge_notice_start_onboarding_btn"
                )

                BrutalistButton(
                    text = "JUMP STRAIGHT TO PRIMARY DASHBOARD ->",
                    onClick = onSkipToDashboard,
                    backgroundColor = DevSyncAcidLime,
                    textColor = DevSyncInkBlack,
                    shadowColor = DevSyncInkBlack,
                    testTag = "judge_notice_skip_dashboard_btn"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun JudgeScopeItem(
    code: String,
    title: String,
    description: String
) {
    Surface(
        color = DevSyncPaperSurface,
        border = BorderStroke(1.5.dp, DevSyncInkBlack),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrutalistBadge(
                    text = code,
                    backgroundColor = DevSyncAcidLime,
                    textColor = DevSyncInkBlack
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DevSyncInkBlack
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF2B2B2B)
                )
            )
        }
    }
}
