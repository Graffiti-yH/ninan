package com.denser.june.presentation.screens.home.tags.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denser.june.R
import com.denser.june.core.domain.enums.TagCategory
import com.denser.june.presentation.utils.UiUtils

private enum class WarningType {
    None, Switch, Merge
}

@Composable
fun EditTagDialog(
    currentTagName: String,
    category: TagCategory,
    existingTags: List<String>,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit
) {
    val initialFullName = remember(currentTagName, category) {
        when {
            currentTagName.startsWith("@") || currentTagName.startsWith("#") -> currentTagName
            category == TagCategory.People -> "@$currentTagName"
            category == TagCategory.Themes -> "#$currentTagName"
            else -> currentTagName
        }
    }

    var newName by remember { mutableStateOf(initialFullName) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val label = category.singularLabel

    val targetFullTag = remember(newName) {
        val trimmed = newName.trim()
        when {
            trimmed.startsWith("@") -> "@${trimmed.removePrefix("@")}"
            trimmed.startsWith("#") -> "#${trimmed.removePrefix("#")}"
            else -> trimmed
        }
    }

    val targetCleanName = remember(targetFullTag) {
        targetFullTag.removePrefix("@").removePrefix("#")
    }

    val isValid = remember(targetCleanName, targetFullTag, initialFullName) {
        newName.isNotBlank() &&
                targetCleanName.isNotEmpty() &&
                targetFullTag != initialFullName
    }

    val currentTargetCategory = remember(targetFullTag) {
        when {
            targetFullTag.startsWith("@") -> TagCategory.People
            targetFullTag.startsWith("#") -> TagCategory.Themes
            else -> TagCategory.Spaces
        }
    }

    val isMergeConflict = remember(targetFullTag, initialFullName) {
        targetFullTag != initialFullName && existingTags.contains(targetFullTag)
    }

    val isCategorySwitch = currentTargetCategory != category

    val warningState = remember(isMergeConflict, isCategorySwitch, isValid) {
        when {
            isMergeConflict && isValid -> WarningType.Merge
            isCategorySwitch && isValid -> WarningType.Switch
            else -> WarningType.None
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = {
                Icon(
                    painterResource(R.drawable.delete_24px),
                    null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete $label?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(R.drawable.info_24px),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "\"$currentTagName\" will be removed from all entries.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(painterResource(R.drawable.delete_24px), null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    painterResource(R.drawable.sell_24px),
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Edit Tag") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Name",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            isError = isMergeConflict,
                            leadingIcon = {
                                val spec = UiUtils.getCategoryUiSpec(currentTargetCategory)
                                Icon(
                                    painter = painterResource(spec.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (isMergeConflict) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                if (newName.isNotEmpty()) {
                                    IconButton(onClick = { newName = "" }) {
                                        Icon(painterResource(R.drawable.close_24px), "Clear")
                                    }
                                }
                            }
                        )
                    }

                    AnimatedContent(
                        targetState = warningState,
                        label = "Warning Animation",
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { state ->
                        when (state) {
                            WarningType.Switch -> {
                                WarningCard(
                                    iconRes = R.drawable.swap_horiz_24px,
                                    text = "This will move to the ${currentTargetCategory.label} tab.",
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                        alpha = 0.4f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            WarningType.Merge -> {
                                WarningCard(
                                    iconRes = R.drawable.merge_type_24px,
                                    text = "Already exists in ${currentTargetCategory.label}. This will merge all entries.",
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = 0.5f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            WarningType.None -> {}
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = { showDeleteConfirm = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Icon(painterResource(R.drawable.delete_24px), "Delete")
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = onDismiss) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = { if (isValid) onRename(newName) },
                            enabled = isValid,
                            colors = if (isMergeConflict) ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ) else ButtonDefaults.buttonColors()
                        ) {
                            Text(if (isMergeConflict) "Merge" else "Save")
                        }
                    }
                }
            },
            dismissButton = null
        )
    }
}

@Composable
private fun WarningCard(
    iconRes: Int,
    text: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor
            )
        }
    }
}