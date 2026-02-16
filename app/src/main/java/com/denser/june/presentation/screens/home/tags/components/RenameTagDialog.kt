package com.denser.june.presentation.screens.home.tags.components

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
fun RenameTagDialog(
    currentTagName: String,
    category: TagCategory,
    existingTags: List<String>,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentTagName) }

    val targetTag = newName.trim()
    val cleanName = targetTag.removePrefix("@").removePrefix("#")

    val targetCategory = when {
        targetTag.startsWith("@") -> TagCategory.People
        targetTag.startsWith("#") -> TagCategory.Themes
        else -> TagCategory.Spaces
    }

    val isChanged = targetTag != currentTagName
    val isConflict = isChanged && existingTags.contains(targetTag)
    val isValid = cleanName.isNotEmpty() && isChanged

    val warningState = when {
        isConflict && isValid -> WarningType.Merge
        targetCategory != category && isValid -> WarningType.Switch
        else -> WarningType.None
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painterResource(R.drawable.edit_24px),
                null
            )
        },
        title = { Text("Rename Tag") },
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
                        isError = isConflict,
                        leadingIcon = {
                            val spec = UiUtils.getCategoryUiSpec(targetCategory)
                            Icon(
                                painter = painterResource(spec.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (isConflict) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }

                when (warningState) {
                    WarningType.Switch -> {
                        WarningCard(
                            iconRes = R.drawable.swap_horiz_24px,
                            text = "This will move to the ${targetCategory.label} tab.",
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    WarningType.Merge -> {
                        WarningCard(
                            iconRes = R.drawable.merge_type_24px,
                            text = "Already exists in ${targetCategory.label}. This will merge all entries.",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    WarningType.None -> {}
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (isValid) onRename(targetTag) },
                enabled = isValid,
                colors = if (isConflict) ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) else ButtonDefaults.buttonColors()
            ) {
                Text(if (isConflict) "Merge & Save" else "Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
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