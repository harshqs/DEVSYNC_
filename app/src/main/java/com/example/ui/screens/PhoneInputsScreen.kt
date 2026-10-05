package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ErrorEventEntity
import com.example.data.ScrubResult
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
fun PhoneInputsScreen(
    crashPresets: List<CrashScenarioPreset>,
    activeEvent: ErrorEventEntity?,
    liveScrubberInput: String,
    liveScrubResult: ScrubResult,
    lastGeneratedMarkdown: String?,
    voiceCommandTranscript: String,
    voiceResponseSummary: String,
    officeKitConnected: Boolean,
    onUpdateScrubberInput: (String) -> Unit,
    onRunCameraOcr: (CrashScenarioPreset) -> Unit,
    onSendScrubbedToDashboard: (String, String, String) -> Unit,
    onTriggerVoiceCommand: (String) -> Unit,
    onToggleOfficeKit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedToolTab by remember { mutableIntStateOf(0) }
    var selectedOcrPresetIndex by remember { mutableIntStateOf(0) }
    val selectedOcrPreset = crashPresets.getOrElse(selectedOcrPresetIndex) { crashPresets.first() }
    val clipboardManager = LocalClipboardManager.current

    val toolTabs = listOf(
        "CAMERA OCR",
        "VOICE TRIGGER",
        "SCRUB LAB"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Segmented Tool Mode Switcher (No Emojis)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            toolTabs.forEachIndexed { idx, label ->
                val isSelected = selectedToolTab == idx
                Surface(
                    color = if (isSelected) DevSyncCobalt else DevSyncPaperSurface,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clickable { selectedToolTab = idx }
                        .testTag("input_mode_tab_$idx")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) Color.White else DevSyncInkBlack,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        AnimatedContent(
            targetState = selectedToolTab,
            label = "phone_input_tool_content"
        ) { mode ->
            when (mode) {
                0 -> CameraOcrToolSection(
                    crashPresets = crashPresets,
                    selectedOcrPresetIndex = selectedOcrPresetIndex,
                    onSelectPresetIndex = { selectedOcrPresetIndex = it },
                    selectedOcrPreset = selectedOcrPreset,
                    lastGeneratedMarkdown = lastGeneratedMarkdown,
                    onRunCameraOcr = onRunCameraOcr,
                    onSendScrubbedToDashboard = onSendScrubbedToDashboard,
                    onCopyMarkdown = { md -> clipboardManager.setText(AnnotatedString(md)) }
                )
                1 -> VoiceAssistantToolSection(
                    activeEvent = activeEvent,
                    voiceCommandTranscript = voiceCommandTranscript,
                    voiceResponseSummary = voiceResponseSummary,
                    onTriggerVoiceCommand = onTriggerVoiceCommand
                )
                else -> ScrubberLabToolSection(
                    liveScrubberInput = liveScrubberInput,
                    liveScrubResult = liveScrubResult,
                    onUpdateScrubberInput = onUpdateScrubberInput,
                    onSendScrubbedToDashboard = onSendScrubbedToDashboard
                )
            }
        }

        // iQOO Office Kit Handoff Status Strip
        BrutalistCard(
            backgroundColor = DevSyncPaperSurface,
            contentPadding = PaddingValues(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "iQOO OFFICE KIT HANDOFF",
                        style = MaterialTheme.typography.labelLarge.copy(color = DevSyncCobalt)
                    )
                    Text(
                        text = "Instant Markdown issue and clipboard sync with MacBook-Pro.local",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onToggleOfficeKit() }
                        .testTag("toggle_office_kit_button")
                ) {
                    BrutalistBadge(
                        text = if (officeKitConnected) "PHONE <-> LAPTOP" else "RECONNECT",
                        backgroundColor = DevSyncAcidLime,
                        hasHardShadow = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun CameraOcrToolSection(
    crashPresets: List<CrashScenarioPreset>,
    selectedOcrPresetIndex: Int,
    onSelectPresetIndex: (Int) -> Unit,
    selectedOcrPreset: CrashScenarioPreset,
    lastGeneratedMarkdown: String?,
    onRunCameraOcr: (CrashScenarioPreset) -> Unit,
    onSendScrubbedToDashboard: (String, String, String) -> Unit,
    onCopyMarkdown: (String) -> Unit
) {
    BrutalistCard(
        backgroundColor = DevSyncPaperSurface,
        contentPadding = PaddingValues(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Terminal Camera OCR Scanner",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Point at an external monitor. Reads text on-device and scrubs secrets.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            BrutalistBadge(text = "ON-DEVICE OCR", backgroundColor = DevSyncAcidLime)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "CHOOSE MONITOR FRAME IN VIEWFINDER:",
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            crashPresets.take(3).forEachIndexed { idx, preset ->
                val isSelected = idx == selectedOcrPresetIndex
                Surface(
                    color = if (isSelected) DevSyncCobalt else DevSyncStoneMuted,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clickable { onSelectPresetIndex(idx) }
                        .testTag("ocr_target_tab_$idx")
                ) {
                    Text(
                        text = preset.command.take(14),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) Color.White else DevSyncInkBlack
                        ),
                        maxLines = 1,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Simulated Camera Viewfinder
        Surface(
            color = DevSyncTerminalBg,
            border = BorderStroke(2.dp, DevSyncAcidLime),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "[LIVE OCR VIEWFINDER]",
                        style = MaterialTheme.typography.labelSmall.copy(color = DevSyncAcidLime)
                    )
                    Text(
                        text = "PRIVACY GATE: ACTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = selectedOcrPreset.rawLog,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        color = Color(0xFFE0E0E0)
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "[DETECTED SECRETS WILL BE REDACTED BEFORE EXPORT]",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DevSyncScrubPink,
                        fontSize = 9.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BrutalistButton(
                text = "CAPTURE -> MARKDOWN",
                onClick = { onRunCameraOcr(selectedOcrPreset) },
                backgroundColor = DevSyncInkBlack,
                textColor = Color.White,
                shadowColor = DevSyncCobalt,
                testTag = "run_camera_ocr_button",
                modifier = Modifier.weight(1f)
            )
            BrutalistButton(
                text = "DIAGNOSE ON DASHBOARD",
                onClick = {
                    onSendScrubbedToDashboard(
                        selectedOcrPreset.command,
                        selectedOcrPreset.rawLog,
                        "CAMERA_OCR"
                    )
                },
                backgroundColor = DevSyncAcidLime,
                textColor = DevSyncInkBlack,
                shadowColor = DevSyncInkBlack,
                testTag = "ocr_to_dashboard_button",
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedVisibility(visible = lastGeneratedMarkdown != null) {
            lastGeneratedMarkdown?.let { md ->
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BrutalistBadge(
                            text = "SANITIZED MARKDOWN ISSUE",
                            backgroundColor = DevSyncBridgeLavender
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clickable { onCopyMarkdown(md) }
                                .padding(6.dp)
                                .testTag("copy_markdown_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Markdown issue",
                                modifier = Modifier.size(14.dp),
                                tint = DevSyncCobalt
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COPY .MD",
                                style = MaterialTheme.typography.labelSmall.copy(color = DevSyncCobalt)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = DevSyncStoneMuted,
                        border = BorderStroke(1.5.dp, DevSyncInkBlack),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = md,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = JetBrainsMonoFontFamily
                            ),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VoiceAssistantToolSection(
    activeEvent: ErrorEventEntity?,
    voiceCommandTranscript: String,
    voiceResponseSummary: String,
    onTriggerVoiceCommand: (String) -> Unit
) {
    BrutalistCard(
        backgroundColor = DevSyncScrubPink,
        contentPadding = PaddingValues(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Hands-Free Offline Voice Trigger",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Keep your hands on the laptop keyboard while asking DevSync.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            BrutalistBadge(text = "OFFLINE VOICE PACK", backgroundColor = DevSyncPaperSurface)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            color = DevSyncPaperSurface,
            border = BorderStroke(1.5.dp, DevSyncInkBlack),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "LISTENING FOR ACTIVE TASK CONTEXT:",
                    style = MaterialTheme.typography.labelSmall.copy(color = DevSyncCobalt)
                )
                Text(
                    text = "${activeEvent?.errorTitle ?: "Missing dependency"} ($ ${activeEvent?.rawCommand ?: "npm run build"})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "TAP A VOICE PHRASE TO SPEAK:",
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        val voiceCommands = listOf(
            "DevSync, explain this error",
            "DevSync, stage safe fix command on my laptop",
            "DevSync, scrub and save Fix Card"
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            voiceCommands.forEachIndexed { index, phrase ->
                Surface(
                    color = DevSyncPaperSurface,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .clickable { onTriggerVoiceCommand(phrase) }
                        .testTag("voice_trigger_button_$index")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\"$phrase\"",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        BrutalistBadge(
                            text = "SPEAK",
                            backgroundColor = DevSyncAcidLime
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            color = DevSyncInkBlack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "RECOGNIZED OFFLINE: $voiceCommandTranscript",
                    style = MaterialTheme.typography.labelSmall.copy(color = DevSyncAcidLime)
                )
                Text(
                    text = voiceResponseSummary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                )
            }
        }
    }
}

@Composable
private fun ScrubberLabToolSection(
    liveScrubberInput: String,
    liveScrubResult: ScrubResult,
    onUpdateScrubberInput: (String) -> Unit,
    onSendScrubbedToDashboard: (String, String, String) -> Unit
) {
    BrutalistCard(
        backgroundColor = DevSyncBridgeLavender,
        contentPadding = PaddingValues(20.dp)
    ) {
        Text(
            text = "Deterministic Privacy Gate Inspector",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Paste any raw terminal log to verify what gets redacted before local AI inference.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = liveScrubberInput,
            onValueChange = onUpdateScrubberInput,
            label = { Text("Raw Log Input (Try sk-..., PASSWORD=..., /Users/...)") },
            minLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DevSyncPaperSurface,
                unfocusedContainerColor = DevSyncPaperSurface,
                focusedBorderColor = DevSyncCobalt,
                unfocusedBorderColor = DevSyncInkBlack
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_scrubber_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BrutalistBadge(
                text = "${liveScrubResult.secretsScrubbedCount} SECRETS SCRUBBED",
                backgroundColor = DevSyncAcidLime
            )
            BrutalistBadge(
                text = "${liveScrubResult.pathsMaskedCount} PATHS MASKED",
                backgroundColor = DevSyncScrubPink
            )
            BrutalistBadge(
                text = "0 B CLOUD",
                backgroundColor = DevSyncPaperSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = DevSyncInkBlack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "SANITIZED PAYLOAD (PASSED TO LOCAL MODEL):",
                    style = MaterialTheme.typography.labelSmall.copy(color = DevSyncAcidLime)
                )
                Text(
                    text = liveScrubResult.sanitizedText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontFamily = JetBrainsMonoFontFamily
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        BrutalistButton(
            text = "DIAGNOSE THIS SANITIZED LOG ON DASHBOARD ->",
            onClick = {
                onSendScrubbedToDashboard(
                    "devsync watch --custom",
                    liveScrubberInput,
                    "LAPTOP_CLI"
                )
            },
            backgroundColor = DevSyncCobalt,
            textColor = Color.White,
            shadowColor = DevSyncInkBlack,
            testTag = "send_scrubbed_to_dashboard_button"
        )
    }
}
