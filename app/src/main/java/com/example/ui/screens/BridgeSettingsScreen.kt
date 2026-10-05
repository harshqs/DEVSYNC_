package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.BrutalistBadge
import com.example.ui.components.BrutalistButton
import com.example.ui.components.BrutalistCard
import com.example.ui.theme.DevSyncAcidLime
import com.example.ui.theme.DevSyncCobalt
import com.example.ui.theme.DevSyncInkBlack
import com.example.ui.theme.DevSyncPaperSurface
import com.example.ui.theme.DevSyncScrubPink
import com.example.ui.theme.DevSyncSuccessGreen
import com.example.ui.theme.JetBrainsMonoFontFamily

@Composable
fun BridgeSettingsScreen(
    laptopBridgeConnected: Boolean,
    officeKitConnected: Boolean,
    airplaneModeProof: Boolean,
    ruleScrubKeys: Boolean,
    ruleScrubEnv: Boolean,
    ruleMaskPaths: Boolean,
    totalSecretsScrubbed: Int,
    totalPathsMasked: Int,
    onToggleLaptopBridge: () -> Unit,
    onToggleOfficeKit: () -> Unit,
    onToggleAirplaneMode: () -> Unit,
    onToggleRuleKeys: () -> Unit,
    onToggleRuleEnv: () -> Unit,
    onToggleRulePaths: () -> Unit,
    onRelaunchOnboarding: () -> Unit,
    onOpenJudgeNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Paired Laptop Bridge Card
        BrutalistCard(
            backgroundColor = DevSyncPaperSurface,
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
                            .background(if (laptopBridgeConnected) DevSyncSuccessGreen else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "LAPTOP CLI BRIDGE",
                        style = MaterialTheme.typography.labelLarge.copy(color = DevSyncCobalt)
                    )
                }
                BrutalistBadge(
                    text = if (laptopBridgeConnected) "CONNECTED" else "PAUSED",
                    backgroundColor = if (laptopBridgeConnected) DevSyncAcidLime else DevSyncScrubPink
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "MacBook-Pro.local",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Endpoint: ws://192.168.1.42:8765 | Watcher: `$ devsync watch`",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = JetBrainsMonoFontFamily)
            )

            Spacer(modifier = Modifier.height(14.dp))

            SettingsToggleRow(
                title = "Local WebSocket CLI Watcher",
                subtitle = "Automatically receive non-zero build and test exits over Wi-Fi",
                checked = laptopBridgeConnected,
                onCheckedChange = onToggleLaptopBridge
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingsToggleRow(
                title = "iQOO Office Kit Handoff",
                subtitle = "Shared clipboard and Markdown issue handoff between phone and laptop",
                checked = officeKitConnected,
                onCheckedChange = onToggleOfficeKit
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingsToggleRow(
                title = "Strict Airplane-Mode Lockdown",
                subtitle = "Block all external network interfaces; allow local LAN bridge only",
                checked = airplaneModeProof,
                onCheckedChange = onToggleAirplaneMode
            )
        }

        // 2. On-Device 4-Bit Model Diagnostics
        BrutalistCard(
            backgroundColor = DevSyncAcidLime,
            contentPadding = PaddingValues(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrutalistBadge(
                    text = "ON-DEVICE AI ENGINE",
                    backgroundColor = DevSyncInkBlack,
                    textColor = Color.White
                )
                BrutalistBadge(
                    text = "INTERNET PERMISSION: NONE",
                    backgroundColor = DevSyncPaperSurface
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Qwen2.5-Coder-1.5B-Instruct (Q4_K_M)",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(label = "Avg Latency", value = "340 ms", modifier = Modifier.weight(1f))
                MetricTile(label = "Peak RAM", value = "612 MB", modifier = Modifier.weight(1f))
                MetricTile(label = "Cloud Traffic", value = "0 Bytes", modifier = Modifier.weight(1f))
            }
        }

        // 3. Deterministic Privacy Gate Configuration & Session Counters
        BrutalistCard(
            backgroundColor = DevSyncScrubPink,
            contentPadding = PaddingValues(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DETERMINISTIC PRIVACY GATE",
                    style = MaterialTheme.typography.labelLarge
                )
                BrutalistBadge(
                    text = "${totalSecretsScrubbed + totalPathsMasked} REDACTED",
                    backgroundColor = DevSyncPaperSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            SettingsToggleRow(
                title = "1 / Pattern-Based Secret Scrubber",
                subtitle = "Strips sk-*, AKIA*, ghp_*, and Bearer JWT tokens ($totalSecretsScrubbed scrubbed)",
                checked = ruleScrubKeys,
                onCheckedChange = onToggleRuleKeys
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingsToggleRow(
                title = "2 / .env & Database URI Redactor",
                subtitle = "Removes PASSWORD=, SECRET=, and DB connection strings",
                checked = ruleScrubEnv,
                onCheckedChange = onToggleRuleEnv
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingsToggleRow(
                title = "3 / Private Path & Identity Masker",
                subtitle = "Replaces /Users/<username>/... with /~/project/... ($totalPathsMasked masked)",
                checked = ruleMaskPaths,
                onCheckedChange = onToggleRulePaths
            )
        }

        // 4. Re-run Onboarding Setup or View Judge Prototype Notice
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BrutalistButton(
                text = "RE-RUN DEVICE SETUP & ONBOARDING FLOW ->",
                onClick = onRelaunchOnboarding,
                backgroundColor = DevSyncCobalt,
                textColor = Color.White,
                shadowColor = DevSyncInkBlack,
                testTag = "relaunch_onboarding_button"
            )

            BrutalistButton(
                text = "VIEW HACKATHON PROTOTYPE NOTICE ->",
                onClick = onOpenJudgeNotice,
                backgroundColor = DevSyncPaperSurface,
                textColor = DevSyncInkBlack,
                shadowColor = DevSyncInkBlack,
                testTag = "open_judge_notice_from_settings_btn"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SettingsToggleRow(
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
private fun MetricTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DevSyncPaperSurface,
        border = BorderStroke(1.5.dp, DevSyncInkBlack),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
