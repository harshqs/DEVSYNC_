package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.data.ErrorEventEntity
import com.example.data.PrivacyScrubberEngine
import com.example.ui.CrashScenarioPreset
import com.example.ui.components.BrutalistBadge
import com.example.ui.components.BrutalistButton
import com.example.ui.components.BrutalistCard
import com.example.ui.theme.DevSyncAcidLime
import com.example.ui.theme.DevSyncBridgeLavender
import com.example.ui.theme.DevSyncCobalt
import com.example.ui.theme.DevSyncInkBlack
import com.example.ui.theme.DevSyncPaperSurface
import com.example.ui.theme.DevSyncScrubPink
import com.example.ui.theme.DevSyncStoneMuted
import com.example.ui.theme.DevSyncTerminalBg
import com.example.ui.theme.JetBrainsMonoFontFamily

@Composable
fun DashboardScreen(
    events: List<ErrorEventEntity>,
    selectedEventId: Int?,
    selectedPipelineStage: Int,
    isSimulatingLoop: Boolean,
    airplaneModeProof: Boolean,
    crashPresets: List<CrashScenarioPreset>,
    onSelectEvent: (Int) -> Unit,
    onSelectPipelineStage: (Int) -> Unit,
    onTriggerPreset: (CrashScenarioPreset) -> Unit,
    onApproveFix: (ErrorEventEntity, String) -> Unit,
    onSaveAsFixCard: (ErrorEventEntity) -> Unit,
    onAnalyzeCustomLog: (String, String) -> Unit,
    onToggleAirplaneMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeEvent = remember(events, selectedEventId) {
        events.find { it.id == selectedEventId } ?: events.firstOrNull()
    }
    var showRawDiff by remember { mutableStateOf(false) }
    var showApprovalHandoffSheet by remember { mutableStateOf(false) }
    var showCrashSimulatorDrawer by remember { mutableStateOf(false) }
    var showCustomLogInput by remember { mutableStateOf(false) }
    var taskFilter by remember { mutableStateOf("ALL") }

    var customCmd by remember { mutableStateOf("npm run build") }
    var customRawLog by remember {
        mutableStateOf(
            "ERROR Cannot find module 'zod' imported from /Users/dev/secret-app/src/schema.ts\nSTRIPE_SECRET_KEY=sk-live-51Nz998877665544332211"
        )
    }
    val clipboardManager = LocalClipboardManager.current

    val filteredQueue = remember(events, taskFilter) {
        when (taskFilter) {
            "PENDING" -> events.filter { it.status == "PENDING_APPROVAL" }
            "RESOLVED" -> events.filter { it.status != "PENDING_APPROVAL" }
            else -> events
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Connected Laptop Watcher & Offline Status Bar
        BrutalistCard(
            backgroundColor = DevSyncTerminalBg,
            shadowColor = DevSyncCobalt,
            shadowOffset = 4.dp,
            contentPadding = PaddingValues(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(DevSyncAcidLime)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "MacBook-Pro.local | $ devsync watch",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        )
                        Text(
                            text = if (airplaneModeProof) {
                                "Airplane-Mode Ready | Local Wi-Fi Bridge | 0 B Cloud"
                            } else {
                                "Local WebSocket Connected | 0 B Cloud"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB0BEC5))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = DevSyncAcidLime,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { showCrashSimulatorDrawer = !showCrashSimulatorDrawer }
                        .testTag("toggle_crash_simulator_button")
                ) {
                    Text(
                        text = if (showCrashSimulatorDrawer) "CLOSE SIM" else "TEST CRASH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DevSyncInkBlack,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            AnimatedVisibility(visible = showCrashSimulatorDrawer) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF2D3142))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "FIRE A SIMULATED LAPTOP BUILD FAILURE OVER LOCAL WI-FI:",
                        style = MaterialTheme.typography.labelSmall.copy(color = DevSyncAcidLime)
                    )
                    crashPresets.forEach { preset ->
                        Surface(
                            color = Color(0xFF1E2029),
                            border = BorderStroke(1.dp, Color(0xFF3E4356)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .clickable {
                                    onTriggerPreset(preset)
                                    showCrashSimulatorDrawer = false
                                }
                                .testTag("preset_crash_${preset.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = preset.label,
                                        style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
                                    )
                                    Text(
                                        text = "$ ${preset.command}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = DevSyncAcidLime)
                                    )
                                }
                                BrutalistBadge(
                                    text = "SEND ->",
                                    backgroundColor = DevSyncAcidLime,
                                    textColor = DevSyncInkBlack
                                )
                            }
                        }
                    }

                    BrutalistButton(
                        text = if (showCustomLogInput) "HIDE CUSTOM LOG" else "+ PASTE CUSTOM TERMINAL LOG",
                        onClick = { showCustomLogInput = !showCustomLogInput },
                        backgroundColor = DevSyncPaperSurface,
                        textColor = DevSyncInkBlack,
                        shadowColor = DevSyncCobalt,
                        testTag = "toggle_custom_crash_button"
                    )

                    AnimatedVisibility(visible = showCustomLogInput) {
                        Surface(
                            color = DevSyncPaperSurface,
                            border = BorderStroke(1.5.dp, DevSyncInkBlack),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = customCmd,
                                    onValueChange = { customCmd = it },
                                    label = { Text("Command") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_command_input")
                                )
                                OutlinedTextField(
                                    value = customRawLog,
                                    onValueChange = { customRawLog = it },
                                    label = { Text("Raw Log with Secrets / Paths") },
                                    minLines = 3,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_log_input")
                                )
                                BrutalistButton(
                                    text = "SEND TO PHONE DEBUGGER ->",
                                    onClick = {
                                        onAnalyzeCustomLog(customCmd, customRawLog)
                                        showCustomLogInput = false
                                        showCrashSimulatorDrawer = false
                                    },
                                    backgroundColor = DevSyncAcidLime,
                                    textColor = DevSyncInkBlack,
                                    shadowColor = DevSyncInkBlack,
                                    testTag = "submit_custom_crash_button"
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Primary Active Error Resolution Card
        if (activeEvent != null) {
            val detailedScrub = remember(activeEvent.rawLog) {
                PrivacyScrubberEngine.scrub(activeEvent.rawLog)
            }

            BrutalistCard(
                backgroundColor = DevSyncBridgeLavender,
                shadowColor = DevSyncInkBlack,
                shadowOffset = 5.dp,
                cornerRadius = 8.dp,
                contentPadding = PaddingValues(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "DEVSYNC / ERROR RECEIVED",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                color = DevSyncInkBlack
                            )
                        )
                        Text(
                            text = "$ ${activeEvent.rawCommand} | ${activeEvent.stackTag}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DevSyncCobalt,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    BrutalistBadge(
                        text = when (activeEvent.status) {
                            "APPROVED" -> "[STAGED ON LAPTOP]"
                            "SAVED_AS_CARD" -> "[SAVED FIX CARD]"
                            else -> "NEEDS APPROVAL"
                        },
                        backgroundColor = when (activeEvent.status) {
                            "APPROVED", "SAVED_AS_CARD" -> DevSyncAcidLime
                            else -> DevSyncPaperSurface
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                PipelineStageStrip(
                    selectedStage = selectedPipelineStage,
                    isSimulating = isSimulatingLoop,
                    activeEvent = activeEvent,
                    onSelectStage = onSelectPipelineStage
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = activeEvent.errorTitle,
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = DevSyncInkBlack,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = activeEvent.plainEnglishSummary,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        color = DevSyncInkBlack
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // SUGGESTED FIX white box
                Surface(
                    color = DevSyncPaperSurface,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SUGGESTED FIX",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF555555)
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(activeEvent.suggestedFixCommand))
                                    }
                                    .padding(4.dp)
                                    .testTag("copy_fix_command_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy suggested fix command",
                                    modifier = Modifier.size(14.dp),
                                    tint = DevSyncCobalt
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "COPY CMD",
                                    style = MaterialTheme.typography.labelSmall.copy(color = DevSyncCobalt)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = activeEvent.suggestedFixCommand,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = DevSyncInkBlack
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = activeEvent.confidenceNote.replace("•", "|"),
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF4A4A4A))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dual Telemetry Boxes: [ 2 secrets scrubbed ] | [ 0 B sent to cloud ]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = DevSyncAcidLime,
                        border = BorderStroke(1.5.dp, DevSyncInkBlack),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clickable { showRawDiff = !showRawDiff }
                            .testTag("toggle_privacy_diff_button")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${activeEvent.secretsScrubbedCount}",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = if (showRawDiff) "[HIDE DIFF]" else "[VIEW DIFF]",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                                )
                            }
                            Text(
                                text = "secrets scrubbed (+${activeEvent.pathsMaskedCount} path)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Surface(
                        color = DevSyncStoneMuted,
                        border = BorderStroke(1.5.dp, DevSyncInkBlack),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clickable { onToggleAirplaneMode() }
                            .testTag("toggle_airplane_proof_button")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "0 B",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "sent to cloud (Offline AI)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = showRawDiff) {
                    Surface(
                        color = DevSyncScrubPink,
                        border = BorderStroke(1.5.dp, DevSyncInkBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "PRIVACY GATE REDACTION LOG (REMOVED BEFORE AI INFERENCE)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            detailedScrub.detectedItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "- ${item.category} (${item.maskedOriginal})",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "-> ${item.replacementToken}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Surface(
                                color = DevSyncInkBlack,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = activeEvent.sanitizedLog,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        color = DevSyncAcidLime
                                    ),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BrutalistButton(
                        text = if (activeEvent.status == "APPROVED") "STAGED ON LAPTOP" else "APPROVE MANUALLY ->",
                        onClick = { showApprovalHandoffSheet = !showApprovalHandoffSheet },
                        backgroundColor = DevSyncInkBlack,
                        textColor = Color.White,
                        shadowColor = DevSyncCobalt,
                        testTag = "approve_manually_button",
                        modifier = Modifier.weight(1.15f)
                    )
                    BrutalistButton(
                        text = "SAVE FIX CARD",
                        onClick = { onSaveAsFixCard(activeEvent) },
                        backgroundColor = DevSyncPaperSurface,
                        textColor = DevSyncInkBlack,
                        shadowColor = DevSyncInkBlack,
                        testTag = "save_fix_card_button",
                        modifier = Modifier.weight(0.85f)
                    )
                }

                AnimatedVisibility(visible = showApprovalHandoffSheet) {
                    Surface(
                        color = DevSyncPaperSurface,
                        border = BorderStroke(1.5.dp, DevSyncInkBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "HUMAN CONTROL // CHOOSE HOW TO APPLY THIS FIX:",
                                style = MaterialTheme.typography.labelLarge.copy(color = DevSyncCobalt)
                            )
                            Text(
                                text = "DevSync never silently executes commands on your laptop.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            BrutalistButton(
                                text = "1. STAGE IN MACBOOK TERMINAL BUFFER (PRESS ENTER TO RUN)",
                                onClick = {
                                    onApproveFix(activeEvent, "TERMINAL_BUFFER")
                                    showApprovalHandoffSheet = false
                                },
                                backgroundColor = DevSyncAcidLime,
                                textColor = DevSyncInkBlack,
                                shadowColor = DevSyncInkBlack,
                                testTag = "handoff_terminal_buffer_btn"
                            )
                            BrutalistButton(
                                text = "2. SEND TO LAPTOP CLIPBOARD (iQOO OFFICE KIT)",
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(activeEvent.suggestedFixCommand))
                                    onApproveFix(activeEvent, "OFFICE_KIT")
                                    showApprovalHandoffSheet = false
                                },
                                backgroundColor = DevSyncBridgeLavender,
                                textColor = DevSyncInkBlack,
                                shadowColor = DevSyncInkBlack,
                                testTag = "handoff_office_kit_btn"
                            )
                        }
                    }
                }
            }
        }

        // 3. Developer Task Queue
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
                    text = "SESSION ERROR TASK QUEUE (${events.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ALL" to "All", "PENDING" to "Open", "RESOLVED" to "Done").forEach { (key, label) ->
                        val selected = taskFilter == key
                        Surface(
                            color = if (selected) DevSyncCobalt else DevSyncStoneMuted,
                            border = BorderStroke(1.dp, DevSyncInkBlack),
                            modifier = Modifier
                                .clickable { taskFilter = key }
                                .testTag("task_queue_filter_$key")
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selected) Color.White else DevSyncInkBlack
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredQueue.forEach { item ->
                    val isSelected = item.id == activeEvent?.id
                    Surface(
                        color = if (isSelected) DevSyncAcidLime else DevSyncStoneMuted,
                        border = BorderStroke(if (isSelected) 2.dp else 1.5.dp, DevSyncInkBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .clickable { onSelectEvent(item.id) }
                            .testTag("event_queue_card_${item.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    BrutalistBadge(
                                        text = item.stackTag,
                                        backgroundColor = DevSyncPaperSurface
                                    )
                                    Text(
                                        text = item.errorTitle,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = "$ ${item.rawCommand} | ${item.secretsScrubbedCount} secrets scrubbed",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            BrutalistBadge(
                                text = when (item.status) {
                                    "APPROVED" -> "APPROVED"
                                    "SAVED_AS_CARD" -> "SAVED"
                                    else -> "OPEN"
                                },
                                backgroundColor = if (item.status == "PENDING_APPROVAL") {
                                    DevSyncPaperSurface
                                } else {
                                    DevSyncBridgeLavender
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun PipelineStageStrip(
    selectedStage: Int,
    isSimulating: Boolean,
    activeEvent: ErrorEventEntity,
    onSelectStage: (Int) -> Unit
) {
    val stages = listOf(
        1 to "1.Catch",
        2 to "2.Alert",
        3 to "3.Scrub",
        4 to "4.Explain",
        5 to "5.Approve"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            stages.forEach { (num, label) ->
                val isSelected = selectedStage == num
                val bgColor = when {
                    isSelected -> DevSyncCobalt
                    num == 3 -> DevSyncScrubPink
                    num == 4 -> DevSyncAcidLime
                    else -> DevSyncPaperSurface
                }
                Surface(
                    color = bgColor,
                    border = BorderStroke(1.2.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectStage(num) }
                        .testTag("pipeline_stage_$num")
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else DevSyncInkBlack
                        ),
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
                    )
                }
            }
        }

        val stageTrace = when (selectedStage) {
            1 -> "CATCH: Watcher caught non-zero exit on `$ ${activeEvent.rawCommand}`."
            2 -> "ALERT: Sent over local Wi-Fi (ws://192.168.1.42:8765) in 12ms."
            3 -> "SCRUB: Redacted ${activeEvent.secretsScrubbedCount} secrets & ${activeEvent.pathsMaskedCount} path(s) before AI."
            4 -> "EXPLAIN: Local 4-bit model diagnosed '${activeEvent.errorTitle}' offline."
            else -> "APPROVE: Ready for manual developer confirmation (`${activeEvent.suggestedFixCommand}`)."
        }

        Surface(
            color = DevSyncInkBlack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isSimulating) "[LIVE] Receiving and scrubbing crash from laptop..." else stageTrace,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    color = DevSyncAcidLime,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}
