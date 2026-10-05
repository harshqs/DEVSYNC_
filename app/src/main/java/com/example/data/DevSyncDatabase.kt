package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Entity(tableName = "fix_cards")
data class FixCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardCode: String,
    val title: String,
    val cause: String,
    val fixCommand: String,
    val evidence: String,
    val scrubbedSummary: String,
    val scope: String,
    val stackTag: String,
    val categoryTag: String,
    val isVerified: Boolean = true,
    val isSharedWithTeam: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "error_events")
data class ErrorEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceType: String, // LAPTOP_CLI, CAMERA_OCR, VOICE_TRIGGER
    val rawCommand: String,
    val rawLog: String,
    val sanitizedLog: String,
    val errorTitle: String,
    val plainEnglishSummary: String,
    val likelyRootCause: String,
    val suggestedFixCommand: String,
    val secondaryStep: String,
    val confidenceNote: String,
    val secretsScrubbedCount: Int,
    val pathsMaskedCount: Int,
    val stackTag: String,
    val categoryTag: String,
    val status: String, // PENDING_APPROVAL, APPROVED, SAVED_AS_CARD
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface DevSyncDao {
    @Query("SELECT * FROM fix_cards ORDER BY timestamp DESC")
    fun getAllFixCards(): Flow<List<FixCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFixCard(card: FixCardEntity): Long

    @Update
    suspend fun updateFixCard(card: FixCardEntity)

    @Query("DELETE FROM fix_cards WHERE id = :id")
    suspend fun deleteFixCardById(id: Int)

    @Query("SELECT * FROM error_events ORDER BY timestamp DESC")
    fun getAllErrorEvents(): Flow<List<ErrorEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertErrorEvent(event: ErrorEventEntity): Long

    @Update
    suspend fun updateErrorEvent(event: ErrorEventEntity)
}

@Database(
    entities = [FixCardEntity::class, ErrorEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DevSyncDatabase : RoomDatabase() {
    abstract fun devSyncDao(): DevSyncDao

    companion object {
        @Volatile
        private var INSTANCE: DevSyncDatabase? = null

        fun getInstance(context: Context): DevSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DevSyncDatabase::class.java,
                    "devsync_local_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class DevSyncRepository(private val dao: DevSyncDao) {
    val allFixCards: Flow<List<FixCardEntity>> = dao.getAllFixCards()
    val allErrorEvents: Flow<List<ErrorEventEntity>> = dao.getAllErrorEvents()

    suspend fun insertFixCard(card: FixCardEntity): Long = dao.insertFixCard(card)
    suspend fun updateFixCard(card: FixCardEntity) = dao.updateFixCard(card)
    suspend fun deleteFixCard(id: Int) = dao.deleteFixCardById(id)

    suspend fun insertErrorEvent(event: ErrorEventEntity): Long = dao.insertErrorEvent(event)
    suspend fun updateErrorEvent(event: ErrorEventEntity) = dao.updateErrorEvent(event)

    suspend fun seedInitialDataIfNeeded() {
        val currentEvents = dao.getAllErrorEvents().first()
        if (currentEvents.isEmpty()) {
            val rawAxiosLog = """
                $ npm run build
                > vite build --mode production
                ERROR Cannot find module 'axios' imported from /Users/abhijith/iqoo-hackathon/devsync-web/src/api/client.ts
                ENV_LOADED: OPENAI_API_KEY=sk-live-98a7bc654321fedcba987654
                AUTH_HEADER: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.payload.sig
                Exit code: 1
            """.trimIndent()
            val axiosAnalysis = PrivacyScrubberEngine.analyzeLocally("npm run build", rawAxiosLog)

            val rawKotlinLog = """
                $ ./gradlew :app:compileDebugKotlin
                e: file:///Users/abhijith/android- internal/secret-payments/PaymentManager.kt:42:18 Unresolved reference ' AuthTokenSigner'
                AWS_SECRET_ACCESS_KEY=AKIAIOSFODNN7EXAMPLE99
                Connecting to build cache at 192.168.1.108:8080
                FAILURE: Build failed with an exception.
            """.trimIndent()
            val kotlinAnalysis = PrivacyScrubberEngine.analyzeLocally("./gradlew :app:compileDebugKotlin", rawKotlinLog)

            val rawDbLog = """
                $ pytest tests/integration/test_ledger.py
                psycopg2.OperationalError: connection to server at "10.24.8.19", port 5432 failed: Connection refused
                DATABASE_URL=postgres://admin_prod:Sup3rS3cretP@ss99@10.24.8.19:5432/fin_ledger
                File "/home/abhijith/fintech-core/db/session.py", line 88, in connect
            """.trimIndent()
            val dbAnalysis = PrivacyScrubberEngine.analyzeLocally("pytest tests/integration/test_ledger.py", rawDbLog)

            dao.insertErrorEvent(
                ErrorEventEntity(
                    sourceType = "LAPTOP_CLI",
                    rawCommand = "pytest tests/integration/test_ledger.py",
                    rawLog = rawDbLog,
                    sanitizedLog = dbAnalysis.scrubResult.sanitizedText,
                    errorTitle = dbAnalysis.errorTitle,
                    plainEnglishSummary = dbAnalysis.plainEnglishSummary,
                    likelyRootCause = dbAnalysis.likelyRootCause,
                    suggestedFixCommand = dbAnalysis.suggestedFixCommand,
                    secondaryStep = dbAnalysis.secondaryStep,
                    confidenceNote = dbAnalysis.confidenceNote,
                    secretsScrubbedCount = dbAnalysis.scrubResult.secretsScrubbedCount,
                    pathsMaskedCount = dbAnalysis.scrubResult.pathsMaskedCount,
                    stackTag = dbAnalysis.stackTag,
                    categoryTag = dbAnalysis.categoryTag,
                    status = "PENDING_APPROVAL",
                    timestamp = System.currentTimeMillis() - 180_000L
                )
            )

            dao.insertErrorEvent(
                ErrorEventEntity(
                    sourceType = "LAPTOP_CLI",
                    rawCommand = "./gradlew :app:compileDebugKotlin",
                    rawLog = rawKotlinLog,
                    sanitizedLog = kotlinAnalysis.scrubResult.sanitizedText,
                    errorTitle = kotlinAnalysis.errorTitle,
                    plainEnglishSummary = kotlinAnalysis.plainEnglishSummary,
                    likelyRootCause = kotlinAnalysis.likelyRootCause,
                    suggestedFixCommand = kotlinAnalysis.suggestedFixCommand,
                    secondaryStep = kotlinAnalysis.secondaryStep,
                    confidenceNote = kotlinAnalysis.confidenceNote,
                    secretsScrubbedCount = kotlinAnalysis.scrubResult.secretsScrubbedCount,
                    pathsMaskedCount = kotlinAnalysis.scrubResult.pathsMaskedCount,
                    stackTag = kotlinAnalysis.stackTag,
                    categoryTag = kotlinAnalysis.categoryTag,
                    status = "PENDING_APPROVAL",
                    timestamp = System.currentTimeMillis() - 90_000L
                )
            )

            // Primary featured error from Slide 1 & Slide 2 of the PDF
            dao.insertErrorEvent(
                ErrorEventEntity(
                    sourceType = "LAPTOP_CLI",
                    rawCommand = "npm run build",
                    rawLog = rawAxiosLog,
                    sanitizedLog = axiosAnalysis.scrubResult.sanitizedText,
                    errorTitle = "Missing dependency",
                    plainEnglishSummary = "Your project imports axios, but it is not installed.",
                    likelyRootCause = "The dependency is imported in src/api/client.ts but missing from package.json.",
                    suggestedFixCommand = "npm install axios",
                    secondaryStep = "Run npm install axios, then run the build again.",
                    confidenceNote = "98% confidence | On-device 4-bit model (0 B sent to cloud)",
                    secretsScrubbedCount = 2,
                    pathsMaskedCount = 1,
                    stackTag = "NODE.JS",
                    categoryTag = "DEPENDENCY",
                    status = "PENDING_APPROVAL",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        val currentCards = dao.getAllFixCards().first()
        if (currentCards.isEmpty()) {
            // Exact Fix Card #014 from Slide 8 of the PDF + 2 additional realistic team cards
            dao.insertFixCard(
                FixCardEntity(
                    cardCode = "FIX CARD / #012",
                    title = "PostgreSQL ECONNREFUSED on local test runner",
                    cause = "Docker container for local test DB was not started before running pytest suite.",
                    fixCommand = "docker compose up -d db && npx prisma migrate dev",
                    evidence = "psycopg2.OperationalError on port 5432 during integration test bootstrap.",
                    scrubbedSummary = "1 DB URI password and 2 internal hosts/paths.",
                    scope = "Available to this device or approved local teammates.",
                    stackTag = "POSTGRES",
                    categoryTag = "DATABASE",
                    isVerified = true,
                    isSharedWithTeam = true,
                    timestamp = System.currentTimeMillis() - 3600_000L
                )
            )
            dao.insertFixCard(
                FixCardEntity(
                    cardCode = "FIX CARD / #013",
                    title = "KSP plugin version mismatch in Android build",
                    cause = "KSP version in libs.versions.toml did not align with Kotlin compiler version.",
                    fixCommand = "./gradlew :app:kspDebugKotlin --rerun-tasks",
                    evidence = "Task :app:kspDebugKotlin failed during symbol processing phase.",
                    scrubbedSummary = "1 AWS key and 1 private developer path.",
                    scope = "Available to this device or approved local teammates.",
                    stackTag = "KOTLIN",
                    categoryTag = "BUILD",
                    isVerified = true,
                    isSharedWithTeam = false,
                    timestamp = System.currentTimeMillis() - 1800_000L
                )
            )
            dao.insertFixCard(
                FixCardEntity(
                    cardCode = "FIX CARD / #014",
                    title = "Cannot find module 'axios'",
                    cause = "The dependency is imported but missing from package.json.",
                    fixCommand = "npm install axios, then run the build again.",
                    evidence = "Node module resolution failed during the Vite build.",
                    scrubbedSummary = "2 secret-like values and 1 private path.",
                    scope = "Available to this device or approved local teammates.",
                    stackTag = "NODE.JS",
                    categoryTag = "DEPENDENCY",
                    isVerified = true,
                    isSharedWithTeam = true,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
