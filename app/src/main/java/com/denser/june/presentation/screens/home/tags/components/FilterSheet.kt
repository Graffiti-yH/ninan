package com.denser.june.presentation.screens.home.tags.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.denser.june.R
import com.denser.june.core.domain.enums.TagCategory
import com.denser.june.presentation.utils.UiUtils

@Composable
fun FilterFab(
    activeCount: Int,
    onClick: () -> Unit
) {
    val hasActive = activeCount > 0

    ExtendedFloatingActionButton(
        onClick = onClick,
        containerColor = if (hasActive) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (hasActive) MaterialTheme.colorScheme.onSecondaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(20.dp),
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
        icon = {
            Icon(
                painter = painterResource(R.drawable.filter_list_24px),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        text = {
            AnimatedContent(
                targetState = activeCount,
                label = "fab_count",
                transitionSpec = {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                }
            ) { count ->
                Text(
                    text = if (count > 0) "$count active" else "Filter",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    availableFilters: List<String>,
    selectedFilters: Set<String>,
    onToggle: (String) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val grouped = remember(availableFilters) {
        TagCategory.entries.associateWith { category ->
            availableFilters.filter { tag ->
                when (category) {
                    TagCategory.People -> tag.startsWith("@")
                    TagCategory.Themes -> tag.startsWith("#")
                    TagCategory.Spaces -> !tag.startsWith("@") && !tag.startsWith("#")
                }
            }
        }.filter { it.value.isNotEmpty() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(top = 8.dp)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )

                if (selectedFilters.isNotEmpty()) {
                    InputChip(
                        selected = true,
                        onClick = onClearAll,
                        label = {
                            Text(
                                text = "${selectedFilters.size}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.close_24px),
                                contentDescription = "Clear filters",
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                alpha = 0.6f
                            ),
                            selectedLabelColor = MaterialTheme.colorScheme.error,
                            selectedTrailingIconColor = MaterialTheme.colorScheme.error
                        ),
                        border = null,
                        shape = CircleShape,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (availableFilters.isEmpty()) {
                    Text(
                        "No tags found in this space.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp)
                    )
                } else {
                    grouped.entries.forEach { entry ->
                        FilterCategorySection(
                            category = entry.key,
                            tags = entry.value,
                            selectedFilters = selectedFilters,
                            onToggle = onToggle
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterCategorySection(
    category: TagCategory,
    tags: List<String>,
    selectedFilters: Set<String>,
    onToggle: (String) -> Unit
) {
    val spec = UiUtils.getCategoryUiSpec(category)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(spec.iconRes),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = spec.color
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = category.label,
                style = MaterialTheme.typography.labelLarge,
                color = spec.color
            )

            val activeCount = tags.count { it in selectedFilters }
            if (activeCount > 0) {
                Text(
                    text = " • $activeCount",
                    style = MaterialTheme.typography.labelLarge,
                    color = spec.color,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                val isActive = tag in selectedFilters
                val chipLabel =
                    if (category.prefix != null) tag.removePrefix(category.prefix) else tag

                FilterChip(
                    selected = isActive,
                    onClick = { onToggle(tag) },
                    label = { Text(chipLabel) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = spec.containerColor,
                        selectedLabelColor = spec.color,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = null,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}