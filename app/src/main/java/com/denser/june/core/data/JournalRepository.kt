package com.denser.june.core.data

import com.denser.june.core.data.database.journal.JournalDao
import com.denser.june.core.data.database.journal.JournalTagCrossRef
import com.denser.june.core.data.database.journal.TagEntity
import com.denser.june.core.data.mappers.toEntity
import com.denser.june.core.data.mappers.toJournal
import com.denser.june.core.domain.JournalRepo
import com.denser.june.core.domain.data_classes.Journal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class JournalRepository(
    private val localDao: JournalDao
) : JournalRepo {

    override suspend fun insertJournal(journal: Journal): Long {
        return withContext(Dispatchers.IO) {
            val journalId = localDao.insertJournal(journal.toEntity())
            syncJournalTags(journalId, journal.tags)
            journalId
        }
    }

    override suspend fun updateJournal(journal: Journal) {
        withContext(Dispatchers.IO) {
            localDao.updateJournal(journal.toEntity())
            syncJournalTags(journal.id, journal.tags)
        }
    }

    override suspend fun deleteJournal(id: Long) {
        withContext(Dispatchers.IO) {
            localDao.deleteJournal(id)
            localDao.deleteTagsForJournal(id)
        }
    }

    override suspend fun deleteAllJournals() {
        withContext(Dispatchers.IO) {
            localDao.deleteAllJournals()
        }
    }

    private suspend fun syncJournalTags(journalId: Long, tags: List<String>) {
        localDao.deleteTagsForJournal(journalId)
        tags.forEach { tagName ->
            localDao.insertTag(TagEntity(name = tagName))
            localDao.getTagIdByName(tagName)?.let { tagId ->
                localDao.insertJournalTagCrossRef(JournalTagCrossRef(journalId, tagId))
            }
        }
    }

    override fun getJournals(): Flow<List<Journal>> =
        localDao.getAllJournals().map { entities -> entities.map { it.toJournal() } }

    override suspend fun getAllJournals(): List<Journal> =
        localDao.getAllJournals().first().map { it.toJournal() }

    override suspend fun getJournalById(id: Long): Journal? =
        withContext(Dispatchers.IO) { localDao.getJournalById(id)?.toJournal() }

    override suspend fun getLatestJournal(): Journal? =
        withContext(Dispatchers.IO) { localDao.getLatestJournal()?.toJournal() }

    override fun searchJournals(query: String): Flow<List<Journal>> =
        localDao.searchJournal(query).map { entities -> entities.map { it.toJournal() } }

    override fun getJournalsByTag(tagName: String): Flow<List<Journal>> =
        localDao.getJournalsByTagName(tagName).map { entities -> entities.map { it.toJournal() } }

    override fun getJournalsByDateRange(startDate: Long, endDate: Long): Flow<List<Journal>> =
        localDao.getJournalsByDateRange(startDate, endDate).map { entities -> entities.map { it.toJournal() } }

    override fun getFilteredJournals(
        query: String,
        isBookmarked: Boolean?,
        isDraft: Boolean?,
        hasLocation: Boolean?,
        hasSong: Boolean?
    ): Flow<List<Journal>> = localDao.getJournals(query, isBookmarked, isDraft, hasLocation, hasSong)
        .map { entities -> entities.map { it.toJournal() } }

    override fun getUniqueTags(): Flow<List<String>> = localDao.getAllUniqueTags()

    override fun getTagSuggestions(query: String): Flow<List<String>> = localDao.getTagSuggestions(query)

    override fun getTagCounts(): Flow<Map<String, Int>> {
        return localDao.getAllTagCounts()
            .map { list ->
                list.associate { it.name to it.count }
            }
    }

    override fun getJournalsByMultipleTags(tags: List<String>): Flow<List<Journal>> {
        if (tags.isEmpty()) {
            return getJournals()
        }
        return localDao.getJournalsWithAllTags(tags, tags.size)
            .map { entities -> entities.map { it.toJournal() } }
    }

    override suspend fun renameTag(oldName: String, newName: String) {
        withContext(Dispatchers.IO) {
            val existingTagId = localDao.getTagIdByName(newName)
            if (existingTagId != null) {
                val affectedJournals = localDao.getJournalsByTagNameSync(oldName)
                affectedJournals.forEach { entity ->
                    val journal = entity.toJournal()
                    val updatedTags = journal.tags.map { if (it == oldName) newName else it }.distinct()
                    updateJournal(journal.copy(tags = updatedTags))
                }
                localDao.deleteTag(oldName)

            } else {
                val affectedJournals = localDao.getJournalsByTagNameSync(oldName)
                localDao.updateTagName(oldName, newName)

                affectedJournals.forEach { entity ->
                    val journal = entity.toJournal()
                    val updatedTags = journal.tags.map { if (it == oldName) newName else it }
                    updateJournal(journal.copy(tags = updatedTags))
                }
            }
        }
    }
    override suspend fun deleteTag(tagName: String) {
        withContext(Dispatchers.IO) {
            val affectedJournals = localDao.getJournalsByTagNameSync(tagName)
            localDao.deleteTag(tagName)

            affectedJournals.forEach { entity ->
                val journal = entity.toJournal()
                if (journal.tags.contains(tagName)) {
                    val updatedTags = journal.tags.filter { it != tagName }
                    updateJournal(journal.copy(tags = updatedTags))
                }
            }
        }
    }
}