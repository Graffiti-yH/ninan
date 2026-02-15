package com.denser.june.presentation.screens.editor.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.denser.june.R
import com.denser.june.presentation.components.JuneAppBarType
import com.denser.june.presentation.components.JuneTopAppBar
import com.denser.june.presentation.utils.UiUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JournalTagsSheet(
    tags: List<String>,
    suggestions: List<String>,
    isEditMode: Boolean,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onSearchTags: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var tagInput by remember { mutableStateOf("") }
    var showInfoDialog by remember { mutableStateOf(false) }

    fun onInsertPrefix(prefix: String) {
        tagInput = prefix
        onSearchTags(prefix)
    }

    if (showInfoDialog) {
        TagInfoDialog(onDismiss = { showInfoDialog = false })
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                JuneTopAppBar(
                    type = JuneAppBarType.CenterAligned,
                    title = {
                        Text(
                            text = if (isEditMode) "Manage Tags" else "Tags",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        FilledIconButton(
                            onClick = onDismiss,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                            ),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close_24px),
                                contentDescription = "Close",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showInfoDialog = true }) {
                            Icon(
                                painter = painterResource(R.drawable.info_24px),
                                contentDescription = "Tag Help",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (isEditMode) {
                    TagInputArea(
                        tagInput = tagInput,
                        onInputChange = {
                            tagInput = it
                            onSearchTags(it)
                        },
                        suggestions = suggestions,
                        onAddTag = {
                            onAddTag(it)
                            tagInput = ""
                            onSearchTags("")
                        },
                        onInsertPrefix = ::onInsertPrefix
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                TagSectionCard(
                    title = "People",
                    prefix = "@",
                    iconRes = R.drawable.person_24px,
                    tags = tags.filter { it.startsWith("@") },
                    isEditMode = isEditMode,
                    onRemove = onRemoveTag,
                    emptyMessage = "Who was there? (@name)",
                    tintColor = MaterialTheme.colorScheme.tertiary
                )

                TagSectionCard(
                    title = "Themes",
                    prefix = "#",
                    iconRes = R.drawable.flare_24px,
                    tags = tags.filter { it.startsWith("#") },
                    isEditMode = isEditMode,
                    onRemove = onRemoveTag,
                    emptyMessage = "What is this about? (#theme)",
                    tintColor = MaterialTheme.colorScheme.primary
                )

                TagSectionCard(
                    title = "Labels",
                    prefix = null,
                    iconRes = R.drawable.label_24px,
                    tags = tags.filter { !it.startsWith("@") && !it.startsWith("#") },
                    isEditMode = isEditMode,
                    onRemove = onRemoveTag,
                    emptyMessage = "Categorize this entry",
                    tintColor = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TagSectionCard(
    title: String,
    prefix: String?,
    iconRes: Int,
    tags: List<String>,
    isEditMode: Boolean,
    onRemove: (String) -> Unit,
    emptyMessage: String,
    tintColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = MaterialShapes.Cookie4Sided.toShape(),
                    color = tintColor.copy(alpha = 0.12f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = tintColor
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                if (prefix != null) {
                    Text(
                        text = prefix,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = tintColor
                    )
                    Spacer(Modifier.width(8.dp))
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (tags.isEmpty()) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        if (isEditMode) {
                            InputChip(
                                selected = true,
                                onClick = { onRemove(tag) },
                                label = { Text(tag) },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.close_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = UiUtils.getTagInputChipColors(tag),
                                border = null
                            )
                        } else {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(tag) },
                                shape = RoundedCornerShape(12.dp),
                                colors = UiUtils.getTagSuggestionChipColors(tag),
                                border = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagInputArea(
    tagInput: String,
    onInputChange: (String) -> Unit,
    suggestions: List<String>,
    onAddTag: (String) -> Unit,
    onInsertPrefix: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        tonalElevation = 6.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
        ) {
            val showSuggestions = suggestions.isNotEmpty() && tagInput.isNotBlank()

            if (showSuggestions) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(suggestions) { suggestion ->
                        SuggestionChip(
                            onClick = { onAddTag(suggestion) },
                            label = { Text(suggestion) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            shape = CircleShape
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
            else if (tagInput.isBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = { onInsertPrefix("@") },
                        label = { Text("Person") },
                        icon = { Text("@", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        border = null,
                        shape = CircleShape
                    )
                    SuggestionChip(
                        onClick = { onInsertPrefix("#") },
                        label = { Text("Theme") },
                        icon = { Text("#", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = null,
                        shape = CircleShape
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = tagInput,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            "Add tag...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = UiUtils.getTransparentTextFieldColors().copy(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (tagInput.isNotBlank()) onAddTag(tagInput)
                    }),
                    trailingIcon = {
                        Box(
                            modifier = Modifier.padding(end = 8.dp),
                        ) {
                            FilledIconButton(
                                onClick = { onAddTag(tagInput) },
                                enabled = tagInput.isNotBlank(),
                                modifier = Modifier.size(40.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.12f
                                    ),
                                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.38f
                                    )
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_upward_24px),
                                    contentDescription = "Add Tag",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TagInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(horizontal = 24.dp),
        confirmButton = {
            OutlinedButton(onClick = onDismiss) { Text("Got it") }
        },
        icon = {
            Icon(
                painterResource(R.drawable.sell_24px),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                "How Tags Work",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Tag entries by people, themes, and labels to find them later.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TagInfoRow(
                        iconRes = R.drawable.person_24px,
                        prefix = "@",
                        title = "People",
                        description = "Tag people to track who appears across entries.",
                        color = MaterialTheme.colorScheme.tertiary,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    TagInfoRow(
                        iconRes = R.drawable.flare_24px,
                        prefix = "#",
                        title = "Themes",
                        description = "Themes or projects to connect related thoughts.",
                        color = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    TagInfoRow(
                        iconRes = R.drawable.label_24px,
                        title = "Labels",
                        description = "Broad categories like Work, Travel or Dream.",
                        color = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TagInfoRow(
    prefix: String? = null,
    iconRes: Int? = null,
    title: String,
    description: String,
    color: Color,
    containerColor: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor.copy(alpha = 0.2f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = MaterialShapes.Cookie4Sided.toShape(),
                color = containerColor,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (iconRes != null) {
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = color
                        )
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (prefix != null) {
                        Text(
                            text = prefix,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodySmall.fontSize * 1.4
                )
            }
        }
    }
}