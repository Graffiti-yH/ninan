package com.denser.june.presentation.utils

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.denser.june.R
import com.denser.june.core.domain.enums.TagCategory

object TagUtils {

    const val TAG_CHARACTER_LIMIT = 25

    data class CategoryUiSpec(
        val iconRes: Int,
        val filledIconRes: Int,
        val color: Color,
        val containerColor: Color,
        val emptyMessage: String,
        val description: String
    )

    fun getCategoryForTag(tag: String): TagCategory {
        return when {
            tag.startsWith("@") -> TagCategory.People
            tag.startsWith("#") -> TagCategory.Topics
            else -> TagCategory.Spaces
        }
    }

    fun getCleanTagName(tag: String): String {
        return tag.removePrefix("@").removePrefix("#")
    }

    fun appendPrefix(tag: String, category: TagCategory): String {
        return when (category) {
            TagCategory.People -> "@$tag"
            TagCategory.Topics -> "#$tag"
            TagCategory.Spaces -> tag
        }
    }

    fun filterTagsByCategory(tags: List<String>, category: TagCategory): List<String> {
        return tags.filter { tag -> getCategoryForTag(tag) == category }
    }

    @Composable
    fun getCategoryUiSpec(category: TagCategory): CategoryUiSpec {
        return when (category) {
            TagCategory.Spaces -> CategoryUiSpec(
                iconRes = R.drawable.view_cozy_24px,
                filledIconRes = R.drawable.view_cozy_24px_fill,
                color = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                emptyMessage = "Categorize this entry",
                description = "Broad categories like Work, Travel, or Dream."
            )

            TagCategory.People -> CategoryUiSpec(
                iconRes = R.drawable.person_24px,
                filledIconRes = R.drawable.person_24px_fill,
                color = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                emptyMessage = "Who was there? (@name)",
                description = "Track friends, family, or colleagues across entries."
            )

            TagCategory.Topics -> CategoryUiSpec(
                iconRes = R.drawable.cards_stack_24px,
                filledIconRes = R.drawable.cards_stack_24px_fill,
                color = MaterialTheme.colorScheme.tertiary,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                emptyMessage = "What is this about? (#topic)",
                description = "Specific subjects like #Fitness, #Ideas, or #Hobbies."
            )
        }
    }

    @Composable
    fun getTagBaseColors(tag: String): Pair<Color, Color> {
        val category = getCategoryForTag(tag)
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