package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DevSyncDatabase
import com.example.data.DevSyncRepository
import com.example.data.ErrorEventEntity
import com.example.data.FixCardEntity
import com.example.data.LocalAnalysisResult
import com.example.data.PrivacyScrubberEngine
import com.example.data.ScrubResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppEntryStage {
    SPLASH,
    JUDGE_NOTICE,
    ONBOARDING,
    MAIN_APP
}

enum class PrimaryTab {
    DASHBOARD,
    PHONE_INPUTS,
    FIX_CARDS,
    BRIDGE_AI
}

data class CrashScenarioPreset(
    val id: String,
    val label: String,
    val sourceType: String,
    val command: String,
    val rawLog: String
)

class DevSyncViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DevSyncRepository

    val errorEvents: StateFlow<List<ErrorEventEntity>>
    val fixCards: StateFlow<List<FixCardEntity>>

    // Launches with Splash -> Judge Prototype Notice -> Onboarding Flow -> Primary Dashboard
    private val _entryStage = MutableStateFlow(AppEntryStage.SPLASH)
    val entryStage: StateFlow<AppEntryStage> = _entryStage.asStateFlow()

    private val _onboardingStep = MutableStateFlow(0)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    private val _currentTab = MutableStateFlow(PrimaryTab.DASHBOARD)
    val currentTab: StateFlow<PrimaryTab> = _currentTab.asStateFlow()

    private val _selectedEventId = MutableStateFlow<Int?>(null)
    val selectedEventId: StateFlow<Int?> = _selectedEventId.asStateFlow()

    private val _selectedPipelineStage = MutableStateFlow(4)
    val selectedPipelineStage: StateFlow<Int> = _selectedPipelineStage.asStateFlow()

    private val _isSimulatingLoop = MutableStateFlow(false)
    val isSimulatingLoop: StateFlow<Boolean> = _isSimulatingLoop.asStateFlow()

    private val _airplaneModeProof = MutableStateFlow(true)
    val airplaneModeProof: StateFlow<Boolean> = _airplaneModeProof.asStateFlow()

    private val _laptopBridgeConnected = MutableStateFlow(true)
    val laptopBridgeConnected: StateFlow<Boolean> = _laptopBridgeConnected.asStateFlow()

    private val _officeKitConnected = MutableStateFlow(true)
    val officeKitConnected: StateFlow<Boolean> = _officeKitConnected.asStateFlow()

    private val _ruleScrubKeys = MutableStateFlow(true)
    val ruleScrubKeys: StateFlow<Boolean> = _ruleScrubKeys.asStateFlow()

    private val _ruleScrubEnv = MutableStateFlow(true)
    val ruleScrubEnv: StateFlow<Boolean> = _ruleScrubEnv.asStateFlow()

    private val _ruleMaskPaths = MutableStateFlow(true)
    val ruleMaskPaths: StateFlow<Boolean> = _ruleMaskPaths.asStateFlow()

    private val _liveScrubberInput = MutableStateFlow(
        """
        $ npm run build
        ERROR Cannot find module 'axios' imported from /Users/abhijith/iqoo-hackathon/devsync-web/src/api/client.ts
        OPENAI_API_KEY=sk-live-98a7bc654321fedcba987654
        DATABASE_URL=postgres://admin:SecretPass123@192.168.1.42:5432/prod_db
        """.trimIndent()
    )
    val liveScrubberInput: StateFlow<String> = _liveScrubberInput.asStateFlow()

    private val _liveScrubResult = MutableStateFlow(
        PrivacyScrubberEngine.scrub(_liveScrubberInput.value)
    )
    val liveScrubResult: StateFlow<ScrubResult> = _liveScrubResult.asStateFlow()

    private val _lastGeneratedMarkdown = MutableStateFlow<String?>(null)
    val lastGeneratedMarkdown: StateFlow<String?> = _lastGeneratedMarkdown.asStateFlow()

    private val _voiceCommandTranscript = MutableStateFlow("\"DevSync, explain this error\"")
    val voiceCommandTranscript: StateFlow<String> = _voiceCommandTranscript.asStateFlow()

    private val _voiceResponseSummary = MutableStateFlow(
        "Your project imports 'axios' in src/api/client.ts, but it is missing from package.json. Safe fix: run `npm install axios`."
    )
    val voiceResponseSummary: StateFlow<String> = _voiceResponseSummary.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    val crashPresets = listOf(
        CrashScenarioPreset(
            id = "axios_missing",
            label = "Missing 'axios' + Leaked OpenAI Key",
            sourceType = "LAPTOP_CLI",
            command = "npm run build",
            rawLog = """
                $ npm run build
                > vite build --mode production
                ERROR Cannot find module 'axios' imported from /Users/abhijith/iqoo-hackathon/devsync-web/src/api/client.ts
                ENV_LOADED: OPENAI_API_KEY=sk-live-98a7bc654321fedcba987654
                AUTH_HEADER: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.payload.sig
                Exit code: 1
            """.trimIndent()
        ),
        CrashScenarioPreset(
            id = "kotlin_gradle",
            label = "Kotlin Unresolved Ref + AWS Secret",
            sourceType = "LAPTOP_CLI",
            command = "./gradlew :app:compileDebugKotlin",
            rawLog = """
                $ ./gradlew :app:compileDebugKotlin
                e: file:///Users/abhijith/android-internal/payments/PaymentManager.kt:42:18 Unresolved reference 'AuthTokenSigner'
                AWS_SECRET_ACCESS_KEY=AKIAIOSFODNN7EXAMPLE99
                Connecting to internal cache at 192.168.1.108:8080
                FAILURE: Build failed with an exception.
            """.trimIndent()
        ),
        CrashScenarioPreset(
            id = "postgres_conn",
            label = "Postgres Port 5432 Refused + DB Password",
            sourceType = "LAPTOP_CLI",
            command = "pytest tests/integration/test_ledger.py",
            rawLog = """
                $ pytest tests/integration/test_ledger.py
                psycopg2.OperationalError: connection to server at "10.24.8.19", port 5432 failed: Connection refused
                DATABASE_URL=postgres://admin_prod:Sup3rS3cretP@ss99@10.24.8.19:5432/fin_ledger
                File "/home/abhijith/fintech-core/db/session.py", line 88, in connect
            """.trimIndent()
        ),
        CrashScenarioPreset(
            id = "rust_borrow",
            label = "Rust Borrow Conflict (E0502) + GitHub Token",
            sourceType = "CAMERA_OCR",
            command = "cargo check --workspace",
            rawLog = """
                $ cargo check --workspace
                error[E0502]: cannot borrow `engine.state` as mutable because it is also borrowed as immutable
                 --> /Users/abhijith/rust-node/src/worker.rs:114:9
                GITHUB_TOKEN=ghp_1234567890abcdefABCDEF1234567890
            """.trimIndent()
        )
    )

    init {
        val dao = DevSyncDatabase.getInstance(application).devSyncDao()
        repository = DevSyncRepository(dao)

        errorEvents = repository.allErrorEvents.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        fixCards = repository.allFixCards.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun advanceFromSplashToJudgeNotice() {
        if (_entryStage.value == AppEntryStage.SPLASH) {
            _entryStage.value = AppEntryStage.JUDGE_NOTICE
        }
    }

    fun openJudgeNotice() {
        _entryStage.value = AppEntryStage.JUDGE_NOTICE
    }

    fun openOnboarding(step: Int = 0) {
        _onboardingStep.value = step.coerceIn(0, 2)
        _entryStage.value = AppEntryStage.ONBOARDING
    }

    fun setOnboardingStep(step: Int) {
        _onboardingStep.value = step.coerceIn(0, 2)
    }

    fun completeOnboarding() {
        _entryStage.value = AppEntryStage.MAIN_APP
        _currentTab.value = PrimaryTab.DASHBOARD
        showBanner("Connected to MacBook-Pro.local | Privacy Gate & 4-Bit Local Model active")
    }

    fun selectTab(tab: PrimaryTab) {
        _currentTab.value = tab
    }

    fun selectEvent(eventId: Int) {
        _selectedEventId.value = eventId
    }

    fun selectPipelineStage(stage: Int) {
        _selectedPipelineStage.value = stage.coerceIn(1, 5)
    }

    fun toggleAirplaneModeProof() {
        _airplaneModeProof.value = !_airplaneModeProof.value
        val modeText = if (_airplaneModeProof.value) {
            "Airplane-Mode Enabled | External network sockets blocked (Local Wi-Fi bridge only)"
        } else {
            "Local Wi-Fi Mode | 0 B sent to cloud"
        }
        showBanner(modeText)
    }

    fun toggleLaptopBridge() {
        _laptopBridgeConnected.value = !_laptopBridgeConnected.value
        showBanner(
            if (_laptopBridgeConnected.value) "Paired with MacBook-Pro.local (ws://192.168.1.42:8765)"
            else "Laptop CLI Watcher disconnected"
        )
    }

    fun toggleOfficeKitConnection() {
        _officeKitConnected.value = !_officeKitConnected.value
        showBanner(
            if (_officeKitConnected.value) "iQOO Office Kit Active (Shared Clipboard & Markdown Handoff)"
            else "iQOO Office Kit Paused"
        )
    }

    fun toggleRuleScrubKeys() {
        _ruleScrubKeys.value = !_ruleScrubKeys.value
    }

    fun toggleRuleScrubEnv() {
        _ruleScrubEnv.value = !_ruleScrubEnv.value
    }

    fun toggleRuleMaskPaths() {
        _ruleMaskPaths.value = !_ruleMaskPaths.value
    }

    fun triggerCrashPreset(preset: CrashScenarioPreset) {
        viewModelScope.launch {
            _isSimulatingLoop.value = true
            showBanner("[ALERT] Incoming crash from MacBook-Pro: $ ${preset.command}")
            for (stage in 1..4) {
                _selectedPipelineStage.value = stage
                delay(160)
            }
            val analysis: LocalAnalysisResult = PrivacyScrubberEngine.analyzeLocally(preset.command, preset.rawLog)
            val newId = repository.insertErrorEvent(
                ErrorEventEntity(
                    sourceType = preset.sourceType,
                    rawCommand = preset.command,
                    rawLog = preset.rawLog,
                    sanitizedLog = analysis.scrubResult.sanitizedText,
                    errorTitle = analysis.errorTitle,
                    plainEnglishSummary = analysis.plainEnglishSummary,
                    likelyRootCause = analysis.likelyRootCause,
                    suggestedFixCommand = analysis.suggestedFixCommand,
                    secondaryStep = analysis.secondaryStep,
                    confidenceNote = analysis.confidenceNote,
                    secretsScrubbedCount = analysis.scrubResult.secretsScrubbedCount,
                    pathsMaskedCount = analysis.scrubResult.pathsMaskedCount,
                    stackTag = analysis.stackTag,
                    categoryTag = analysis.categoryTag,
                    status = "PENDING_APPROVAL",
                    timestamp = System.currentTimeMillis()
                )
            )
            _selectedEventId.value = newId.toInt()
            _isSimulatingLoop.value = false
            showBanner(
                "Scrubbed ${analysis.scrubResult.secretsScrubbedCount} secrets & ${analysis.scrubResult.pathsMaskedCount} paths | Fix ready for approval"
            )
        }
    }

    fun approveEventFix(event: ErrorEventEntity, handoffMode: String = "TERMINAL_BUFFER") {
        viewModelScope.launch {
            val updated = event.copy(status = "APPROVED")
            repository.updateErrorEvent(updated)
            _selectedPipelineStage.value = 5
            val targetDesc = if (handoffMode == "OFFICE_KIT") {
                "Copied '${event.suggestedFixCommand}' to laptop via iQOO Office Kit"
            } else {
                "Staged '${event.suggestedFixCommand}' in MacBook-Pro terminal (awaiting Enter)"
            }
            showBanner("[APPROVED] $targetDesc")
        }
    }

    fun saveEventAsFixCard(event: ErrorEventEntity) {
        viewModelScope.launch {
            val nextNum = (fixCards.value.size + 15).toString().padStart(3, '0')
            val card = FixCardEntity(
                cardCode = "FIX CARD / #$nextNum",
                title = "${event.errorTitle} (${event.rawCommand})",
                cause = event.likelyRootCause,
                fixCommand = "${event.suggestedFixCommand}, then run the build again.",
                evidence = event.plainEnglishSummary,
                scrubbedSummary = "${event.secretsScrubbedCount} secret-like values and ${event.pathsMaskedCount} private path(s).",
                scope = "Available to this device or approved local teammates.",
                stackTag = event.stackTag,
                categoryTag = event.categoryTag,
                isVerified = true,
                isSharedWithTeam = false,
                timestamp = System.currentTimeMillis()
            )
            repository.insertFixCard(card)
            repository.updateErrorEvent(event.copy(status = "SAVED_AS_CARD"))
            showBanner("Saved ${card.cardCode} to offline Team Memory")
        }
    }

    fun updateLiveScrubberInput(newInput: String) {
        _liveScrubberInput.value = newInput
        _liveScrubResult.value = PrivacyScrubberEngine.scrub(newInput)
    }

    fun analyzeCustomLogToDashboard(command: String, rawLog: String, sourceType: String = "LAPTOP_CLI") {
        if (rawLog.isBlank()) return
        val preset = CrashScenarioPreset(
            id = "custom_${System.currentTimeMillis()}",
            label = "Custom Terminal Crash",
            sourceType = sourceType,
            command = command.ifBlank { "npm run build" },
            rawLog = rawLog
        )
        triggerCrashPreset(preset)
        _currentTab.value = PrimaryTab.DASHBOARD
    }

    fun runCameraOcrCapture(preset: CrashScenarioPreset) {
        val analysis = PrivacyScrubberEngine.analyzeLocally(preset.command, preset.rawLog)
        _lastGeneratedMarkdown.value = PrivacyScrubberEngine.generateMarkdownIssue(analysis)
        showBanner("OCR captured & scrubbed ${analysis.scrubResult.secretsScrubbedCount} secrets -> Clean Markdown ready")
    }

    fun triggerOfflineVoiceCommand(commandPhrase: String, activeEvent: ErrorEventEntity?) {
        _voiceCommandTranscript.value = "\"$commandPhrase\""
        val targetTitle = activeEvent?.errorTitle ?: "Missing dependency ('axios')"
        val targetFix = activeEvent?.suggestedFixCommand ?: "npm install axios"
        val targetCause = activeEvent?.plainEnglishSummary ?: "Your project imports axios, but it is not installed."

        _voiceResponseSummary.value = when {
            commandPhrase.contains("explain", ignoreCase = true) -> {
                "$targetTitle — $targetCause Suggested safe command: `$targetFix` (0 B sent to cloud)."
            }
            commandPhrase.contains("save", ignoreCase = true) -> {
                if (activeEvent != null) {
                    saveEventAsFixCard(activeEvent)
                }
                "Sanitized explanation saved as a reusable Fix Card on this phone."
            }
            else -> {
                if (activeEvent != null) {
                    approveEventFix(activeEvent)
                }
                "Staged `$targetFix` in your MacBook-Pro terminal buffer. Press Enter on your laptop to run."
            }
        }
        showBanner("Voice command processed offline: $commandPhrase")
    }

    fun toggleShareFixCard(card: FixCardEntity) {
        viewModelScope.launch {
            val updated = card.copy(isSharedWithTeam = !card.isSharedWithTeam)
            repository.updateFixCard(updated)
            val msg = if (updated.isSharedWithTeam) {
                "${card.cardCode} broadcasted to approved teammates on local Wi-Fi"
            } else {
                "${card.cardCode} set to Private (this device only)"
            }
            showBanner(msg)
        }
    }

    fun deleteFixCard(card: FixCardEntity) {
        viewModelScope.launch {
            repository.deleteFixCard(card.id)
            showBanner("Deleted ${card.cardCode}")
        }
    }

    fun dismissBanner() {
        _statusBannerMessage.value = null
    }

    private fun showBanner(message: String) {
        _statusBannerMessage.value = message
    }
}
