package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FixCardEntity
import com.example.ui.components.BrutalistBadge
import com.example.ui.components.BrutalistCard
import com.example.ui.theme.DevSyncAcidLime
import com.example.ui.theme.DevSyncBridgeLavender
import com.example.ui.theme.DevSyncCobalt
import com.example.ui.theme.DevSyncInkBlack
import com.example.ui.theme.DevSyncPaperSurface
import com.example.ui.theme.DevSyncScrubPink
import com.example.ui.theme.DevSyncStoneMuted
import com.example.ui.theme.JetBrainsMonoFontFamily

@Composable
fun FixCardsScreen(
    fixCards: List<FixCardEntity>,
    onToggleShareCard: (FixCardEntity) -> Unit,
    onDeleteCard: (FixCardEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("ALL") }
    val clipboardManager = LocalClipboardManager.current

    val availableTags = remember(fixCards) {
        listOf("ALL") + fixCards.map { it.stackTag }.distinct()
    }

    val filteredCards = remember(fixCards, searchQuery, selectedTag) {
        fixCards.filter { card ->
            val matchesTag = selectedTag == "ALL" || card.stackTag.equals(selectedTag, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                card.title.contains(searchQuery, ignoreCase = true) ||
                card.fixCommand.contains(searchQuery, ignoreCase = true) ||
                card.cause.contains(searchQuery, ignoreCase = true)
            matchesTag && matchesQuery
        }
    }

    val sharedCount = remember(fixCards) { fixCards.count { it.isSharedWithTeam } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BrutalistCard(
                modifier = Modifier.weight(1f),
                backgroundColor = DevSyncPaperSurface,
                contentPadding = PaddingValues(14.dp)
            ) {
                Text(text = "${fixCards.size}", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Saved on Device", style = MaterialTheme.typography.labelSmall)
            }
            BrutalistCard(
                modifier = Modifier.weight(1f),
                backgroundColor = DevSyncAcidLime,
                contentPadding = PaddingValues(14.dp)
            ) {
                Text(text = "$sharedCount", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Shared on LAN", style = MaterialTheme.typography.labelSmall)
            }
            BrutalistCard(
                modifier = Modifier.weight(1f),
                backgroundColor = DevSyncScrubPink,
                contentPadding = PaddingValues(14.dp)
            ) {
                Text(text = "100%", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Sanitized", style = MaterialTheme.typography.labelSmall)
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Fix Cards"
                )
            },
            placeholder = { Text("Search offline Fix Cards (axios, postgres, ksp)...") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DevSyncPaperSurface,
                unfocusedContainerColor = DevSyncPaperSurface,
                focusedBorderColor = DevSyncCobalt,
                unfocusedBorderColor = DevSyncInkBlack
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_fix_cards_input")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            availableTags.forEach { tag ->
                val isSelected = tag == selectedTag
                Surface(
                    color = if (isSelected) DevSyncCobalt else DevSyncPaperSurface,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { selectedTag = tag }
                        .testTag("filter_tag_$tag")
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (isSelected) Color.White else DevSyncInkBlack,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        if (filteredCards.isEmpty()) {
            BrutalistCard(
                backgroundColor = DevSyncPaperSurface,
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "No matching Fix Cards found",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Approve and save an error on the Dashboard to add it to your offline Fix Card library.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else {
            filteredCards.forEach { card ->
                FixCardLibraryItem(
                    card = card,
                    onCopyFix = {
                        clipboardManager.setText(AnnotatedString(card.fixCommand))
                    },
                    onToggleShare = { onToggleShareCard(card) },
                    onDelete = { onDeleteCard(card) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun FixCardLibraryItem(
    card: FixCardEntity,
    onCopyFix: () -> Unit,
    onToggleShare: () -> Unit,
    onDelete: () -> Unit
) {
    BrutalistCard(
        backgroundColor = DevSyncPaperSurface,
        shadowColor = DevSyncCobalt,
        shadowOffset = 6.dp,
        cornerRadius = 2.dp,
        contentPadding = PaddingValues(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrutalistBadge(
                text = card.cardCode,
                backgroundColor = DevSyncCobalt,
                textColor = Color.White
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (card.isSharedWithTeam) "SHARED ON LAN" else "SAVED ON DEVICE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DevSyncInkBlack
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("delete_fix_card_${card.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Fix Card",
                        tint = DevSyncInkBlack,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(DevSyncInkBlack)
        )
        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = card.title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DevSyncInkBlack
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        FixCardTableRow(label = "Cause", value = card.cause)
        FixCardTableRow(label = "Fix", value = card.fixCommand, isBoldMono = true)
        FixCardTableRow(label = "Evidence", value = card.evidence)
        FixCardTableRow(label = "Scrubbed", value = card.scrubbedSummary)
        FixCardTableRow(label = "Scope", value = card.scope)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinePillTag(text = card.stackTag)
                OutlinePillTag(text = card.categoryTag)
                if (card.isVerified) {
                    OutlinePillTag(text = "VERIFIED")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = DevSyncStoneMuted,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onCopyFix() }
                        .testTag("copy_card_fix_${card.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy fix",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "COPY", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Surface(
                    color = if (card.isSharedWithTeam) DevSyncAcidLime else DevSyncBridgeLavender,
                    border = BorderStroke(1.5.dp, DevSyncInkBlack),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onToggleShare() }
                        .testTag("share_card_lan_${card.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Fix Card on local network",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (card.isSharedWithTeam) "SHARED" else "SHARE LAN",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FixCardTableRow(
    label: String,
    value: String,
    isBoldMono: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DevSyncInkBlack
                ),
                modifier = Modifier.width(84.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = if (isBoldMono) FontWeight.Bold else FontWeight.Normal,
                    color = DevSyncInkBlack
                ),
                modifier = Modifier.weight(1f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFD0CBC1))
        )
    }
}

@Composable
private fun OutlinePillTag(text: String) {
    Surface(
        color = DevSyncPaperSurface,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.2.dp, DevSyncInkBlack)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
