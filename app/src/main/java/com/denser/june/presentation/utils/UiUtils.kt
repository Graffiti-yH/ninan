package com.denser.june.presentation.utils

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object UiUtils {
    @Composable
    fun getTransparentTextFieldColors() = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface
    )

    @Composable
    fun getTagBaseColors(tag: String): Pair<Color, Color> {
        return when {
            tag.startsWith("@") -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
            tag.startsWith("#") -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        }
    }

    @Composable
    fun getTagInputChipColors(tag: String): SelectableChipColors {
        val (container, content) = getTagBaseColors(tag)
        return InputChipDefaults.inputChipColors(
            containerColor = container,
            labelColor = content,
            leadingIconColor = content,
            trailingIconColor = content,
            selectedContainerColor = container,
            selectedLabelColor = content,
            selectedLeadingIconColor = content,
            selectedTrailingIconColor = content
        )
    }

    @Composable
    fun getTagSuggestionChipColors(tag: String): ChipColors {
        val (container, content) = getTagBaseColors(tag)

        return SuggestionChipDefaults.suggestionChipColors(
            containerColor = container,
            labelColor = content,
            iconContentColor = content
        )
    }
}