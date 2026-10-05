package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PrivacyScrubberEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DevSync", appName)
    }

    @Test
    fun `deterministic privacy scrubber removes secrets and private paths`() {
        val rawLog = """
            $ npm run build
            ERROR Cannot find module 'axios' imported from /Users/abhijith/iqoo-hackathon/src/api.ts
            OPENAI_API_KEY=sk-live-98a7bc654321fedcba987654
            AUTH_HEADER: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.payload.sig
        """.trimIndent()

        val result = PrivacyScrubberEngine.scrub(rawLog)
        assertEquals(2, result.secretsScrubbedCount)
        assertEquals(1, result.pathsMaskedCount)
        assertEquals(0, result.bytesSentToCloud)
        assertFalse(result.sanitizedText.contains("sk-live-98a7bc654321fedcba987654"))
        assertFalse(result.sanitizedText.contains("/Users/abhijith"))
        assertTrue(result.sanitizedText.contains("/~/project"))
    }
}
