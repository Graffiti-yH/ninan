package com.denser.june.core.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
@SerialName("Journal")
data class Journal(
    @Serializable(with = StringIdSerializer::class)
    val id: String,
    val title: String,
    val content: String,
    val emoji: String? = null,
    val images: List<String> = emptyList(),
    val location: JournalLocation? = null,
    val songDetails: SongDetails? = null,
    val tags: List<String> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long?,
    val dateTime: Long,
    val isBookmarked: Boolean = false,
    val isArchived: Boolean = false,
    val isDraft: Boolean = true,
    val deletedAt: Long? = null,
    val syncedAt: Long? = null,
    val cloudId: String? = null,
) {
    val isDeleted: Boolean get() = deletedAt != null

    fun isContentEqualTo(other: Journal): Boolean {
        return this.title == other.title &&
               this.content == other.content &&
               this.emoji == other.emoji &&
               this.images.map { File(it).name } == other.images.map { File(it).name } &&
               this.location == other.location &&
               this.songDetails == other.songDetails &&
               this.tags == other.tags &&
               this.isBookmarked == other.isBookmarked &&
               this.isArchived == other.isArchived &&
               this.isDraft == other.isDraft &&
               this.deletedAt == other.deletedAt
    }
}

@Serializable
data class JournalLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val name: String? = null,
    val locality: String? = null
)
