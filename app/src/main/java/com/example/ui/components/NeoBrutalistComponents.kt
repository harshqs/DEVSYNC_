package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DevSyncAcidLime
import com.example.ui.theme.DevSyncAlertOrange
import com.example.ui.theme.DevSyncCobalt
import com.example.ui.theme.DevSyncErrorRed
import com.example.ui.theme.DevSyncGridLine
import com.example.ui.theme.DevSyncInkBlack
import com.example.ui.theme.DevSyncPaperGrid
import com.example.ui.theme.DevSyncPaperSurface
import com.example.ui.theme.DevSyncTerminalBg
import com.example.ui.theme.JetBrainsMonoFontFamily

@Composable
fun TechnicalGridBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DevSyncPaperGrid,
    gridLineColor: Color = DevSyncGridLine,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .drawBehind {
                val stepPx = 28.dp.toPx()
                val strokePx = 1.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = gridLineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = strokePx
                    )
                    x += stepPx
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokePx
                    )
                    y += stepPx
                }
            },
        content = content
    )
}

@Composable
fun BrutalistCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DevSyncPaperSurface,
    shadowColor: Color = DevSyncInkBlack,
    shadowOffset: Dp = 4.dp,
    cornerRadius: Dp = 4.dp,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(modifier = modifier.padding(end = shadowOffset, bottom = shadowOffset)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor, shape)
        )
        Surface(
            color = backgroundColor,
            shape = shape,
            border = BorderStroke(1.5.dp, DevSyncInkBlack),
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    }
                )
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    }
}

@Composable
fun BrutalistBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = DevSyncAcidLime,
    textColor: Color = DevSyncInkBlack,
    tiltDegrees: Float = 0f,
    hasHardShadow: Boolean = false
) {
    Box(
        modifier = modifier
            .rotate(tiltDegrees)
            .then(
                if (hasHardShadow) {
                    Modifier.padding(end = 2.dp, bottom = 2.dp)
                } else {
                    Modifier
                }
            )
    ) {
        if (hasHardShadow) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 2.dp, y = 2.dp)
                    .background(DevSyncInkBlack)
            )
        }
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .border(1.5.dp, DevSyncInkBlack)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    letterSpacing = 0.8.sp
                )
            )
        }
    }
}

@Composable
fun BrutalistButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "brutalist_button",
    backgroundColor: Color = DevSyncInkBlack,
    textColor: Color = Color.White,
    shadowColor: Color = DevSyncCobalt,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .padding(end = 3.dp, bottom = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.dp, y = 3.dp)
                .background(if (enabled) shadowColor else Color.Gray, RoundedCornerShape(2.dp))
        )
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = textColor,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.DarkGray
            ),
            border = BorderStroke(1.5.dp, DevSyncInkBlack),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
        }
    }
}

@Composable
fun TerminalWindowCard(
    command: String,
    errorLine: String,
    bridgeStatusLine: String,
    fixReadyCommand: String,
    modifier: Modifier = Modifier,
    accentShadowColor: Color = DevSyncAlertOrange
) {
    BrutalistCard(
        modifier = modifier,
        backgroundColor = DevSyncTerminalBg,
        shadowColor = accentShadowColor,
        shadowOffset = 5.dp,
        cornerRadius = 4.dp,
        contentPadding = PaddingValues(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(DevSyncErrorRed)
                )
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFCA28))
                )
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(DevSyncAcidLime)
                )
            }
            Text(
                text = "CLI WATCHER // LOCAL WS",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9EA4B8)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF2D3142))
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "$ $command",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = JetBrainsMonoFontFamily,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = "ERROR ",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    color = DevSyncErrorRed,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = errorLine,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    color = Color(0xFFFF8A80)
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "> $bridgeStatusLine",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = JetBrainsMonoFontFamily,
                color = Color(0xFFB0BEC5)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "FIX READY ",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    color = DevSyncAcidLime,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = fixReadyCommand,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
