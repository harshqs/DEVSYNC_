package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DevSyncColorScheme = lightColorScheme(
    primary = DevSyncCobalt,
    onPrimary = Color.White,
    primaryContainer = DevSyncBridgeLavender,
    onPrimaryContainer = DevSyncInkBlack,
    secondary = DevSyncAcidLime,
    onSecondary = DevSyncInkBlack,
    secondaryContainer = DevSyncAcidLime,
    onSecondaryContainer = DevSyncInkBlack,
    tertiary = DevSyncScrubPink,
    onTertiary = DevSyncInkBlack,
    tertiaryContainer = DevSyncScrubPink,
    onTertiaryContainer = DevSyncInkBlack,
    background = DevSyncPaperGrid,
    onBackground = DevSyncInkBlack,
    surface = DevSyncPaperSurface,
    onSurface = DevSyncInkBlack,
    surfaceVariant = DevSyncStoneMuted,
    onSurfaceVariant = DevSyncInkMuted,
    outline = DevSyncInkBlack,
    error = DevSyncErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // We intentionally use the signature DevSync Neo-Brutalist palette from the pitch deck
    // so the prototype matches the hackathon deck's Cobalt + Acid Lime + Technical Grid identity.
    MaterialTheme(
        colorScheme = DevSyncColorScheme,
        typography = Typography,
        content = content
    )
}
