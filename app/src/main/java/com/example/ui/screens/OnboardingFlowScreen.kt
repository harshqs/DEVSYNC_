package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrutalistBadge
import com.example.ui.components.BrutalistButton
import com.example.ui.components.BrutalistCard
import com.example.ui.components.TechnicalGridBackground
import com.example.ui.theme.DevSyncAcidLime
import com.example.ui.theme.DevSyncBridgeLavender
import com.example.ui.theme.DevSyncCobalt
import com.example.ui.theme.DevSyncInkBlack
import com.example.ui.theme.DevSyncPaperSurface
import com.example.ui.theme.DevSyncScrubPink
import com.example.ui.theme.DevSyncStoneMuted
import com.example.ui.theme.DevSyncSuccessGreen
import com.example.ui.theme.DevSyncTerminalBg
import com.example.ui.theme.JetBrainsMonoFontFamily

@Composable
fun OnboardingFlowScreen(
    currentStep: Int,
    onStepChange: (Int) -> Unit,
    onBackToJudgeNotice: () -> Unit,
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        if (currentStep > 0) {
            onStepChange(currentStep - 1)
        } else {
            onBackToJudgeNotice()
        }
    }

    var isLaptopPaired by remember { mutableStateOf(true) }
    var scrubApiKeys by remember { mutableStateOf(true) }
    var scrubEnvVars by remember { mutableStateOf(true) }
    var maskPrivatePaths by remember { mutableStateOf(true) }
    var selfTestPassed by remember { mutableStateOf(false) }
    var voiceWakeEnabled by remember { mutableStateOf(true) }
    var hapticAlertsEnabled by remember { mutableStateOf(true) }

    val stepLabels = listOf(
        "1. PAIR LAPTOP",
        "2. PRIVACY GATE",
        "3. LOCAL AI"
    )

    TechnicalGridBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Top App Setup Bar with Brand & Quick "Skip to Dashboard"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = DevSyncCobalt,
                        border = BorderStroke(1.5.dp, DevSyncInkBlack)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "DEVSYNC",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 3.dp, bottom = 3.dp)
                                    .width(10.dp)
                                    .height(3.dp)
                                    .background(DevSyncAcidLime)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    BrutalistBadge(
                        text = "SETUP ${currentStep + 1} OF 3",
                        backgroundColor = DevSyncPaperSurface
                    )
                }

                Surface(
                    color = DevSyncAcidLime,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onFinishOnboarding() }
                        .testTag("skip_onboarding_button")
                ) {
                    Text(
                        text = "SKIP TO DASHBOARD ->",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DevSyncInkBlack,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    )
                }
            }

            // 3-Step Interactive Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                stepLabels.forEachIndexed { index, label ->
                    val isCurrent = index == currentStep
                    val isCompleted = index < currentStep
                    Surface(
                        color = when {
                            isCurrent -> DevSyncCobalt
                            isCompleted -> DevSyncAcidLime
                            else -> DevSyncPaperSurface
                        },
                        border = BorderStroke(1.5.dp, DevSyncInkBlack),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clickable { onStepChange(index) }
                            .testTag("onboarding_step_pill_$index")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isCurrent) Color.White else DevSyncInkBlack,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = currentStep,
                label = "onboarding_product_flow"
            ) { step ->
                when (step) {
                    0 -> Step1PairLaptopBridge(
                        isLaptopPaired = isLaptopPaired,
                        onTogglePair = { isLaptopPaired = !isLaptopPaired }
                    )
                    1 -> Step2ConfigurePrivacyGate(
                        scrubApiKeys = scrubApiKeys,
                        onToggleApiKeys = { scrubApiKeys = !scrubApiKeys },
                        scrubEnvVars = scrubEnvVars,
                        onToggleEnvVars = { scrubEnvVars = !scrubEnvVars },
                        maskPrivatePaths = maskPrivatePaths,
                        onToggleMaskPaths = { maskPrivatePaths = !maskPrivatePaths },
                        selfTestPassed = selfTestPassed,
                        onRunSelfTest = { selfTestPassed = true }
                    )
                    else -> Step3LocalAiReadiness(
                        voiceWakeEnabled = voiceWakeEnabled,
                        onToggleVoiceWake = { voiceWakeEnabled = !voiceWakeEnabled },
                        hapticAlertsEnabled = hapticAlertsEnabled,
                        onToggleHaptics = { hapticAlertsEnabled = !hapticAlertsEnabled }
                    )
                }
            }

            // Bottom Navigation Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BrutalistButton(
                    text = if (currentStep > 0) "<- BACK" else "<- JUDGE NOTICE",
                    onClick = {
                        if (currentStep > 0) onStepChange(currentStep - 1)
                        else onBackToJudgeNotice()
                    },
                    backgroundColor = DevSyncPaperSurface,
                    textColor = DevSyncInkBlack,
                    shadowColor = DevSyncInkBlack,
                    testTag = "onboarding_back_btn",
                    modifier = Modifier.weight(0.4f)
                )

                if (currentStep < 2) {
                    BrutalistButton(
                        text = if (currentStep == 0) "NEXT: PRIVACY GATE ->" else "NEXT: LOCAL AI ->",
                        onClick = { onStepChange(currentStep + 1) },
                        backgroundColor = DevSyncCobalt,
                        textColor = Color.White,
                        shadowColor = DevSyncInkBlack,
                        testTag = "onboarding_next_btn",
                        modifier = Modifier.weight(0.6f)
                    )
                } else {
                    BrutalistButton(
                        text = "OPEN PRIMARY DASHBOARD ->",
                        onClick = onFinishOnboarding,
                        backgroundColor = DevSyncAcidLime,
                        textColor = DevSyncInkBlack,
                        shadowColor = DevSyncInkBlack,
                        testTag = "onboarding_launch_dashboard_btn",
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun Step1PairLaptopBridge(
    isLaptopPaired: Boolean,
    onTogglePair: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val cliCommand = "npx devsync watch --pair 842-910"

    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        BrutalistCard(
            backgroundColor = DevSyncCobalt,
            shadowColor = DevSyncInkBlack,
            shadowOffset = 5.dp,
            contentPadding = PaddingValues(22.dp)
        ) {
            BrutalistBadge(
                text = "STEP 1 // LOCAL WI-FI BRIDGE",
                backgroundColor = DevSyncAcidLime,
                textColor = DevSyncInkBlack
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Pair your laptop terminal.",
                style = MaterialTheme.typography.displaySmall.copy(color = Color.White)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "DevSync watches build and test exits on your laptop and sends failures directly to your phone over local Wi-Fi—with zero cloud servers.",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.92f))
            )
        }

        BrutalistCard(
            backgroundColor = DevSyncPaperSurface,
            contentPadding = PaddingValues(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1. RUN IN YOUR LAPTOP TERMINAL",
                    style = MaterialTheme.typography.labelLarge
                )
                BrutalistBadge(
                    text = "PIN: 842-910",
                    backgroundColor = DevSyncBridgeLavender
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = DevSyncTerminalBg,
                border = BorderStroke(1.5.dp, DevSyncInkBlack),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$ $cliCommand",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            color = DevSyncAcidLime,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Surface(
                        color = DevSyncPaperSurface,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clickable { clipboard.setText(AnnotatedString(cliCommand)) }
                            .testTag("copy_pair_cmd_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy pairing command",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "COPY", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        BrutalistCard(
            backgroundColor = if (isLaptopPaired) DevSyncBridgeLavender else DevSyncPaperSurface,
            contentPadding = PaddingValues(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isLaptopPaired) DevSyncSuccessGreen else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isLaptopPaired) "LAPTOP PAIRED OVER LOCAL WI-FI" else "DISCOVERED ON LAN (WLAN0)",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                BrutalistBadge(
                    text = if (isLaptopPaired) "CONNECTED" else "TAP TO PAIR",
                    backgroundColor = if (isLaptopPaired) DevSyncAcidLime else DevSyncStoneMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = DevSyncPaperSurface,
                border = BorderStroke(1.5.dp, DevSyncInkBlack),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .clickable { onTogglePair() }
                    .testTag("pair_laptop_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = "Laptop icon",
                            tint = DevSyncCobalt,
                            modifier = Modifier.size(28.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "MacBook-Pro.local",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "ws://192.168.1.42:8765 | iQOO Office Kit Ready",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Switch(
                        checked = isLaptopPaired,
                        onCheckedChange = { onTogglePair() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DevSyncInkBlack,
                            checkedTrackColor = DevSyncAcidLime
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun Step2ConfigurePrivacyGate(
    scrubApiKeys: Boolean,
    onToggleApiKeys: () -> Unit,
    scrubEnvVars: Boolean,
    onToggleEnvVars: () -> Unit,
    maskPrivatePaths: Boolean,
    onToggleMaskPaths: () -> Unit,
    selfTestPassed: Boolean,
    onRunSelfTest: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        BrutalistCard(
            backgroundColor = DevSyncScrubPink,
            contentPadding = PaddingValues(22.dp)
        ) {
            BrutalistBadge(
                text = "STEP 2 // TRUST BOUNDARY",
                backgroundColor = DevSyncPaperSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Configure your Privacy Gate.",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Deterministic regex rules strip secrets and mask private paths BEFORE the on-device AI model ever sees your log.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        BrutalistCard(
            backgroundColor = DevSyncPaperSurface,
            contentPadding = PaddingValues(20.dp)
        ) {
            Text(
                text = "ACTIVE SANITIZATION RULES",
                style = MaterialTheme.typography.labelLarge.copy(color = DevSyncCobalt)
            )
            Spacer(modifier = Modifier.height(12.dp))

            PrivacyRuleToggleRow(
                title = "Strip API Keys & Bearer Tokens",
                subtitle = "Redacts sk-*, AKIA*, ghp_*, AIza*, and JWT headers",
                checked = scrubApiKeys,
                onCheckedChange = onToggleApiKeys
            )
            Spacer(modifier = Modifier.height(10.dp))
            PrivacyRuleToggleRow(
                title = "Scrub .env & Database Passwords",
                subtitle = "Redacts PASSWORD=, SECRET=, and postgres:// credentials",
                checked = scrubEnvVars,
                onCheckedChange = onToggleEnvVars
            )
            Spacer(modifier = Modifier.height(10.dp))
            PrivacyRuleToggleRow(
                title = "Mask Private Filesystem Paths & IPs",
                subtitle = "Converts /Users/<you>/... to /~/project/... and masks LAN IPs",
                checked = maskPrivatePaths,
                onCheckedChange = onToggleMaskPaths
            )
        }

        BrutalistCard(
            backgroundColor = DevSyncBridgeLavender,
            contentPadding = PaddingValues(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE PRIVACY GATE PREVIEW",
                    style = MaterialTheme.typography.labelLarge
                )
                BrutalistBadge(
                    text = if (selfTestPassed) "[VERIFIED: 3/3 RULES]" else "0 B TO CLOUD",
                    backgroundColor = DevSyncAcidLime
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            val previewKey = if (scrubApiKeys) "[REDACTED_API_KEY]" else "sk-live-98a7bc654321"
            val previewEnv = if (scrubEnvVars) "[REDACTED_DB_PASS]" else "Sup3rS3cret99"
            val previewPath = if (maskPrivatePaths) "/~/project/api/client.ts" else "/Users/abhijith/corp/src/api/client.ts"

            Surface(
                color = DevSyncTerminalBg,
                border = BorderStroke(1.5.dp, DevSyncInkBlack),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ERROR Cannot find module 'axios'\n" +
                        "  at $previewPath:14:6\n" +
                        "  OPENAI_API_KEY=$previewKey\n" +
                        "  DB_URL=postgres://admin:$previewEnv@[MASKED_HOST]",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        color = DevSyncAcidLime
                    ),
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            BrutalistButton(
                text = if (selfTestPassed) "[OK] PRIVACY GATE SELF-TEST PASSED" else "RUN DETERMINISTIC SCRUBBER TEST",
                onClick = onRunSelfTest,
                backgroundColor = if (selfTestPassed) DevSyncAcidLime else DevSyncInkBlack,
                textColor = if (selfTestPassed) DevSyncInkBlack else Color.White,
                shadowColor = DevSyncCobalt,
                testTag = "run_privacy_selftest_btn"
            )
        }
    }
}

@Composable
private fun PrivacyRuleToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = DevSyncInkBlack,
                checkedTrackColor = DevSyncAcidLime
            )
        )
    }
}

@Composable
private fun Step3LocalAiReadiness(
    voiceWakeEnabled: Boolean,
    onToggleVoiceWake: () -> Unit,
    hapticAlertsEnabled: Boolean,
    onToggleHaptics: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        BrutalistCard(
            backgroundColor = DevSyncAcidLime,
            contentPadding = PaddingValues(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrutalistBadge(
                    text = "STEP 3 // ON-DEVICE 4-BIT AI",
                    backgroundColor = DevSyncInkBlack,
                    textColor = Color.White
                )
                BrutalistBadge(
                    text = "AIRPLANE-MODE READY",
                    backgroundColor = DevSyncPaperSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Local AI Engine Loaded.",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "DevSync runs a quantized 4-bit coding model directly on your phone. The AI module has zero internet permissions and never auto-executes commands.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        BrutalistCard(
            backgroundColor = DevSyncPaperSurface,
            contentPadding = PaddingValues(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Qwen2.5-Coder-1.5B-Instruct (Q4_K_M)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Quantized 4-Bit | Avg Latency: 340 ms | Peak RAM: 612 MB",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                BrutalistBadge(
                    text = "READY",
                    backgroundColor = DevSyncAcidLime
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = DevSyncBridgeLavender,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "0 B", style = MaterialTheme.typography.headlineMedium)
                        Text(text = "Cloud Data Sent", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Surface(
                    color = DevSyncScrubPink,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "100%", style = MaterialTheme.typography.headlineMedium)
                        Text(text = "Manual Approval", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        BrutalistCard(
            backgroundColor = DevSyncPaperSurface,
            contentPadding = PaddingValues(20.dp)
        ) {
            PrivacyRuleToggleRow(
                title = "Instant Haptic Build Crash Alert",
                subtitle = "Vibrate phone immediately when laptop build exits non-zero",
                checked = hapticAlertsEnabled,
                onCheckedChange = onToggleHaptics
            )
            Spacer(modifier = Modifier.height(10.dp))
            PrivacyRuleToggleRow(
                title = "Offline Voice Wake (\"DevSync, explain this error\")",
                subtitle = "Hands-free explanation while your hands stay on the keyboard",
                checked = voiceWakeEnabled,
                onCheckedChange = onToggleVoiceWake
            )
        }
    }
}
