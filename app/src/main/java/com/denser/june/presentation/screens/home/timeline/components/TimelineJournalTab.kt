package com.denser.june.presentation.screens.home.timeline.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denser.june.core.domain.data_classes.Journal
import com.denser.june.core.utils.toDayOfMonth
import com.denser.june.core.utils.toShortMonth
import com.denser.june.presentation.navigation.AppNavigator
import com.denser.june.presentation.navigation.Route
import org.koin.compose.koinInject

import com.denser.june.core.R

@Composable
fun TimelineJournalTab(
    journals: List<Journal>,
    bottomPadding: Dp
) {
    val navigator = koinInject<AppNavigator>()

    if (journals.isEmpty()) {
        EmptyStateMessage("No journals this month.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomPadding + 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(journals, key = { it.id }) { journal ->
                TimelineJournalTile(
                    journal = journal,
                    onClick = {
                        navigator.navigateTo(Route.Editor(journal.id), isSingleTop = true)
                    }
                )
            }
        }
    }
}
@Composable
fun TimelineJournalTile(
    journal: Journal,
    onClick: () -> Unit
) {
    val wordCount = remember(journal.content) {
        val trimmed = journal.content.trim()
        if (trimmed.isEmpty()) 0
        else trimmed.split("\\s+".toRegex()).size
    }
    val wordText = if (wordCount == 1) "word" else "words"
    val mediaCount = remember(journal.images) { journal.images.size }
    val hasMusic = remember(journal.songDetails) { journal.songDetails != null }
    val hasLocation = remember(journal.location) { journal.location != null }
    val tagCount = remember(journal.tags) { journal.tags.size }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(48.dp)
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = journal.dateTime.toDayOfMonth(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = journal.dateTime.toShortMonth(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val titleText = journal.title.ifBlank { "Untitled Entry" }
                        val contentText = journal.content.ifBlank { "No content" }

                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = contentText,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    val emoji = journal.emoji
                    if (emoji != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = emoji,
                            fontSize = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$wordCount $wordText",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetadataBadge(
                            show = mediaCount > 0,
                            icon = R.drawable.photo_24px,
                            label = if (mediaCount > 1) "$mediaCount" else null
                        )
                        MetadataBadge(
                            show = hasMusic,
                            icon = R.drawable.music_note_24px
                        )
                        MetadataBadge(
                            show = hasLocation,
                            icon = R.drawable.location_on_24px
                        )
                        MetadataBadge(
                            show = tagCount > 0,
                            icon = R.drawable.sell_24px,
                            label = "$tagCount"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataBadge(
    show: Boolean,
    icon: Int,
    label: String? = null
) {
    if (!show) return

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}