package com.denser.june.presentation.screens.home.tags

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.denser.june.R
import com.denser.june.core.domain.enums.TagCategory
import com.denser.june.presentation.screens.home.components.EmptyPage
import com.denser.june.presentation.screens.home.components.JournalCard
import com.denser.june.presentation.screens.home.tags.components.EditTagDialog
import com.denser.june.presentation.screens.home.tags.components.FilterBottomSheet
import com.denser.june.presentation.screens.home.tags.components.FilterFab
import com.denser.june.presentation.utils.UiUtils
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TagsPage() {
    val viewModel: TagsVM = koinViewModel()

    val allTags by viewModel.allUniqueTags.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val primaryTags by viewModel.primaryTags.collectAsStateWithLifecycle()
    val selectedPrimaryTag by viewModel.selectedPrimaryTag.collectAsStateWithLifecycle()
    val tagCounts by viewModel.tagCounts.collectAsStateWithLifecycle()
    val journals by viewModel.journals.collectAsStateWithLifecycle()
    val availableFilters by viewModel.availableFilters.collectAsStateWithLifecycle()
    val selectedFilters by viewModel.selectedFilters.collectAsStateWithLifecycle()

    var showEditDialog by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val hasAvailableFilters = availableFilters.isNotEmpty() || selectedFilters.isNotEmpty()
    val activeFilterCount = selectedFilters.size
    val currentCategorySpec = UiUtils.getCategoryUiSpec(selectedCategory)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            PrimaryTabRow(
                selectedTabIndex = selectedCategory.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh) }
            ) {
                TagCategory.entries.forEach { category ->
                    val spec = UiUtils.getCategoryUiSpec(category)
                    val isSelected = selectedCategory == category

                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(category) },
                        icon = {
                            Icon(
                                painter = painterResource(spec.iconRes),
                                contentDescription = category.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        text = {
                            Text(
                                text = category.label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (primaryTags.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(primaryTags, key = { it }) { fullTag ->
                        val isSelected = selectedPrimaryTag == fullTag
                        val count = tagCounts[fullTag] ?: 0
                        val displayText = when (selectedCategory) {
                            TagCategory.People -> fullTag.removePrefix("@")
                            TagCategory.Themes -> fullTag.removePrefix("#")
                            else -> fullTag
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectPrimaryTag(fullTag) },
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(16.dp),
                            label = {
                                Text(
                                    text = displayText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            trailingIcon = {
                                if (isSelected) {
                                    IconButton(
                                        onClick = { showEditDialog = true },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.more_vert_24px),
                                            contentDescription = "Edit",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else if (count > 0) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .height(24.dp)
                                            .defaultMinSize(minWidth = 24.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Text(
                                                text = count.toString(),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = currentCategorySpec.containerColor,
                                selectedLabelColor = currentCategorySpec.color,
                                selectedTrailingIconColor = currentCategorySpec.color,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                borderWidth = 1.dp
                            )
                        )
                    }
                }
            } else {
                EmptyPage(
                    icon = currentCategorySpec.iconRes,
                    title = "No ${selectedCategory.label.lowercase()} yet",
                    subtitle = "Tag a journal entry with a ${selectedCategory.label.lowercase()} to get started."
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(journals, key = { it.id }) { journal ->
                    JournalCard(journal = journal, modifier = Modifier.animateItem())
                }
                if (journals.isEmpty() && primaryTags.isNotEmpty()) {
                    item {
                        EmptyPage(
                            icon = R.drawable.auto_stories_off_24px,
                            title = "No entries",
                            subtitle = "No journals match the current filters."
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = hasAvailableFilters,
            enter = scaleIn(spring()) + fadeIn(),
            exit = scaleOut(spring()) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = UiUtils.BOTTOM_BAR_PADDING + 24.dp)
        ) {
            FilterFab(
                activeCount = activeFilterCount,
                onClick = { showFilterSheet = true }
            )
        }

        if (showFilterSheet) {
            FilterBottomSheet(
                sheetState = filterSheetState,
                availableFilters = availableFilters,
                selectedFilters = selectedFilters,
                onToggle = { viewModel.toggleFilter(it) },
                onClearAll = { viewModel.clearFilters() },
                onDismiss = { showFilterSheet = false }
            )
        }

        if (showEditDialog && selectedPrimaryTag != null) {
            EditTagDialog(
                currentTagName = selectedPrimaryTag!!,
                category = selectedCategory,
                existingTags = allTags,
                onDismiss = { showEditDialog = false },
                onRename = { newName ->
                    viewModel.renameCurrentTag(newName); showEditDialog = false
                },
                onDelete = { viewModel.deleteCurrentTag(); showEditDialog = false }
            )
        }
    }
}