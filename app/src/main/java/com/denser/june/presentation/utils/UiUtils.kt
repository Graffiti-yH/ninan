package com.denser.june.presentation.utils

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.denser.june.R
import com.denser.june.core.domain.enums.TagCategory

object UiUtils {

    val BOTTOM_BAR_PADDING = 80.dp

    data class CategoryUiSpec(
        val iconRes: Int,
        val color: Color,
        val containerColor: Color,
        val emptyMessage: String,
        val description: String
    )

    @Composable
    fun getCategoryUiSpec(category: TagCategory): CategoryUiSpec {
        return when (category) {
            TagCategory.Spaces -> CategoryUiSpec(
                iconRes = R.drawable.view_cozy_24px,
                color = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                emptyMessage = "Categorize this entry",
                description = "Broad categories like Work, Travel or Dream."
            )

            TagCategory.People -> CategoryUiSpec(
                iconRes = R.drawable.person_24px,
                color = MaterialTheme.colorScheme.tertiary,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                emptyMessage = "Who was there? (@name)",
                description = "Tag people to track who appears across entries."
            )

            TagCategory.Themes -> CategoryUiSpec(
                iconRes = R.drawable.flare_24px,
                color = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                emptyMessage = "What is this about? (#theme)",
                description = "Themes or projects to connect related thoughts."
            )
        }
    }

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
        val category = when {
            tag.startsWith("@") -> TagCategory.People
            tag.startsWith("#") -> TagCategory.Themes
            else -> TagCategory.Spaces
        }
        val spec = getCategoryUiSpec(category)
        return spec.containerColor to spec.color
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