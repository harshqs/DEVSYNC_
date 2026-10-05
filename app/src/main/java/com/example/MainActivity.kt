package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppEntryStage
import com.example.ui.DevSyncViewModel
import com.example.ui.PrimaryTab
import com.example.ui.components.TechnicalGridBackground
import com.example.ui.screens.BridgeSettingsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FixCardsScreen
import com.example.ui.screens.JudgePrototypeNoticeScreen
import com.example.ui.screens.OnboardingFlowScreen
import com.example.ui.screens.PhoneInputsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.DevSyncAcidLime
import com.example.ui.theme.DevSyncBridgeLavender
import com.example.ui.theme.DevSyncCobalt
import com.example.ui.theme.DevSyncInkBlack
import com.example.ui.theme.DevSyncPaperSurface
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DevSyncApp()
            }
        }
    }
}

private data class NavDestination(
    val tab: PrimaryTab,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun DevSyncApp(viewModel: DevSyncViewModel = viewModel()) {
    val entryStage by viewModel.entryStage.collectAsStateWithLifecycle()
    val onboardingStep by viewModel.onboardingStep.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val events by viewModel.errorEvents.collectAsStateWithLifecycle()
    val fixCards by viewModel.fixCards.collectAsStateWithLifecycle()
    val selectedEventId by viewModel.selectedEventId.collectAsStateWithLifecycle()
    val selectedPipelineStage by viewModel.selectedPipelineStage.collectAsStateWithLifecycle()
    val isSimulatingLoop by viewModel.isSimulatingLoop.collectAsStateWithLifecycle()
    val airplaneModeProof by viewModel.airplaneModeProof.collectAsStateWithLifecycle()
    val laptopBridgeConnected by viewModel.laptopBridgeConnected.collectAsStateWithLifecycle()
    val officeKitConnected by viewModel.officeKitConnected.collectAsStateWithLifecycle()
    val ruleScrubKeys by viewModel.ruleScrubKeys.collectAsStateWithLifecycle()
    val ruleScrubEnv by viewModel.ruleScrubEnv.collectAsStateWithLifecycle()
    val ruleMaskPaths by viewModel.ruleMaskPaths.collectAsStateWithLifecycle()
    val liveScrubberInput by viewModel.liveScrubberInput.collectAsStateWithLifecycle()
    val liveScrubResult by viewModel.liveScrubResult.collectAsStateWithLifecycle()
    val lastGeneratedMarkdown by viewModel.lastGeneratedMarkdown.collectAsStateWithLifecycle()
    val voiceTranscript by viewModel.voiceCommandTranscript.collectAsStateWithLifecycle()
    val voiceResponse by viewModel.voiceResponseSummary.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    val activeEvent = events.find { it.id == selectedEventId } ?: events.firstOrNull()
    val totalSecretsScrubbed = remember(events) { events.sumOf { it.secretsScrubbedCount } }
    val totalPathsMasked = remember(events) { events.sumOf { it.pathsMaskedCount } }

    // 1. Splash Screen Stage -> 2. Judge Prototype Notice Stage -> 3. Onboarding Flow -> 4. Main App
    when (entryStage) {
        AppEntryStage.SPLASH -> {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                SplashScreen(
                    onSplashFinished = viewModel::advanceFromSplashToJudgeNotice,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            return
        }

        AppEntryStage.JUDGE_NOTICE -> {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                JudgePrototypeNoticeScreen(
                    onStartOnboarding = { viewModel.openOnboarding(0) },
                    onSkipToDashboard = viewModel::completeOnboarding,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            return
        }

        AppEntryStage.ONBOARDING -> {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                OnboardingFlowScreen(
                    currentStep = onboardingStep,
                    onStepChange = viewModel::setOnboardingStep,
                    onBackToJudgeNotice = viewModel::openJudgeNotice,
                    onFinishOnboarding = viewModel::completeOnboarding,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            return
        }

        AppEntryStage.MAIN_APP -> {
            // Proceed to Main App Scaffold below
        }
    }

    val destinations = listOf(
        NavDestination(PrimaryTab.DASHBOARD, "Dashboard", Icons.Default.Dashboard, "nav_tab_dashboard"),
        NavDestination(PrimaryTab.PHONE_INPUTS, "Scan & Voice", Icons.Default.CameraAlt, "nav_tab_inputs"),
        NavDestination(PrimaryTab.FIX_CARDS, "Fix Cards (${fixCards.size})", Icons.Default.BookmarkBorder, "nav_tab_fix_cards"),
        NavDestination(PrimaryTab.BRIDGE_AI, "Bridge & AI", Icons.Default.Memory, "nav_tab_bridge")
    )

    if (currentTab != PrimaryTab.DASHBOARD) {
        BackHandler {
            viewModel.selectTab(PrimaryTab.DASHBOARD)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                DevSyncTopHeaderBar(
                    onOpenJudgeNotice = viewModel::openJudgeNotice,
                    onOpenOnboarding = { viewModel.openOnboarding(0) },
                    statusBanner = statusBanner,
                    onDismissBanner = viewModel::dismissBanner
                )
            },
            bottomBar = {
                if (!isExpandedScreen) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(DevSyncInkBlack)
                        )
                        NavigationBar(
                            containerColor = DevSyncPaperSurface,
                            tonalElevation = 0.dp
                        ) {
                            destinations.forEach { dest ->
                                val selected = currentTab == dest.tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.selectTab(dest.tab) },
                                    icon = {
                                        Icon(
                                            imageVector = dest.icon,
                                            contentDescription = dest.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = dest.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = DevSyncInkBlack,
                                        selectedTextColor = DevSyncInkBlack,
                                        indicatorColor = DevSyncAcidLime,
                                        unselectedIconColor = Color(0xFF555555),
                                        unselectedTextColor = Color(0xFF555555)
                                    ),
                                    modifier = Modifier.testTag(dest.testTag)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            TechnicalGridBackground(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (isExpandedScreen) {
                        NavigationRail(
                            containerColor = DevSyncPaperSurface
                        ) {
                            destinations.forEach { dest ->
                                val selected = currentTab == dest.tab
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { viewModel.selectTab(dest.tab) },
                                    icon = {
                                        Icon(
                                            imageVector = dest.icon,
                                            contentDescription = dest.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = dest.label,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = DevSyncInkBlack,
                                        selectedTextColor = DevSyncInkBlack,
                                        indicatorColor = DevSyncAcidLime
                                    ),
                                    modifier = Modifier.testTag(dest.testTag)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.5.dp)
                                .background(DevSyncInkBlack)
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (currentTab) {
                            PrimaryTab.DASHBOARD -> DashboardScreen(
                                events = events,
                                selectedEventId = selectedEventId,
                                selectedPipelineStage = selectedPipelineStage,
                                isSimulatingLoop = isSimulatingLoop,
                                airplaneModeProof = airplaneModeProof,
                                crashPresets = viewModel.crashPresets,
                                onSelectEvent = viewModel::selectEvent,
                                onSelectPipelineStage = viewModel::selectPipelineStage,
                                onTriggerPreset = viewModel::triggerCrashPreset,
                                onApproveFix = viewModel::approveEventFix,
                                onSaveAsFixCard = viewModel::saveEventAsFixCard,
                                onAnalyzeCustomLog = { cmd, log ->
                                    viewModel.analyzeCustomLogToDashboard(cmd, log, "LAPTOP_CLI")
                                },
                                onToggleAirplaneMode = viewModel::toggleAirplaneModeProof
                            )
                            PrimaryTab.PHONE_INPUTS -> PhoneInputsScreen(
                                crashPresets = viewModel.crashPresets,
                                activeEvent = activeEvent,
                                liveScrubberInput = liveScrubberInput,
                                liveScrubResult = liveScrubResult,
                                lastGeneratedMarkdown = lastGeneratedMarkdown,
                                voiceCommandTranscript = voiceTranscript,
                                voiceResponseSummary = voiceResponse,
                                officeKitConnected = officeKitConnected,
                                onUpdateScrubberInput = viewModel::updateLiveScrubberInput,
                                onRunCameraOcr = viewModel::runCameraOcrCapture,
                                onSendScrubbedToDashboard = viewModel::analyzeCustomLogToDashboard,
                                onTriggerVoiceCommand = { phrase ->
                                    viewModel.triggerOfflineVoiceCommand(phrase, activeEvent)
                                },
                                onToggleOfficeKit = viewModel::toggleOfficeKitConnection
                            )
                            PrimaryTab.FIX_CARDS -> FixCardsScreen(
                                fixCards = fixCards,
                                onToggleShareCard = viewModel::toggleShareFixCard,
                                onDeleteCard = viewModel::deleteFixCard
                            )
                            PrimaryTab.BRIDGE_AI -> BridgeSettingsScreen(
                                laptopBridgeConnected = laptopBridgeConnected,
                                officeKitConnected = officeKitConnected,
                                airplaneModeProof = airplaneModeProof,
                                ruleScrubKeys = ruleScrubKeys,
                                ruleScrubEnv = ruleScrubEnv,
                                ruleMaskPaths = ruleMaskPaths,
                                totalSecretsScrubbed = totalSecretsScrubbed,
                                totalPathsMasked = totalPathsMasked,
                                onToggleLaptopBridge = viewModel::toggleLaptopBridge,
                                onToggleOfficeKit = viewModel::toggleOfficeKitConnection,
                                onToggleAirplaneMode = viewModel::toggleAirplaneModeProof,
                                onToggleRuleKeys = viewModel::toggleRuleScrubKeys,
                                onToggleRuleEnv = viewModel::toggleRuleScrubEnv,
                                onToggleRulePaths = viewModel::toggleRuleMaskPaths,
                                onRelaunchOnboarding = { viewModel.openOnboarding(0) },
                                onOpenJudgeNotice = viewModel::openJudgeNotice
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DevSyncTopHeaderBar(
    onOpenJudgeNotice: () -> Unit,
    onOpenOnboarding: () -> Unit,
    statusBanner: String?,
    onDismissBanner: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DevSyncPaperSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
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
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "PRIVATE DEBUGGER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DevSyncInkBlack
                        )
                    )
                    Text(
                        text = "OFFLINE | 0 B CLOUD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = DevSyncCobalt
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = DevSyncBridgeLavender,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onOpenJudgeNotice() }
                        .testTag("open_judge_notice_button")
                ) {
                    Text(
                        text = "JUDGE NOTICE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DevSyncInkBlack,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }

                Surface(
                    color = DevSyncAcidLime,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onOpenOnboarding() }
                        .testTag("open_onboarding_flow_button")
                ) {
                    Text(
                        text = "SETUP FLOW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DevSyncInkBlack,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(DevSyncInkBlack)
        )

        AnimatedVisibility(visible = statusBanner != null) {
            statusBanner?.let { msg ->
                Surface(
                    color = DevSyncInkBlack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "> $msg",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                color = DevSyncAcidLime
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onDismissBanner,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss notification banner",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
