package com.denser.june.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denser.june.core.domain.JournalRepo
import com.denser.june.core.domain.data_classes.Journal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SearchUiState(
    val isIdle: Boolean = true,
    val isLoading: Boolean = false,
    val journals: List<Journal> = emptyList()
)

private data class InternalSearchState(
    val query: String,
    val isDebouncing: Boolean,
    val bookmarked: Boolean,
    val draft: Boolean,
    val hasLocation: Boolean,
    val hasSong: Boolean,
    val hasMedia: Boolean
)

class SearchVM(
    private val repo: JournalRepo
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked = _isBookmarked.asStateFlow()

    private val _isDraft = MutableStateFlow(false)
    val isDraft = _isDraft.asStateFlow()

    private val _hasLocation = MutableStateFlow(false)
    val hasLocation = _hasLocation.asStateFlow()

    private val _hasSong = MutableStateFlow(false)
    val hasSong = _hasSong.asStateFlow()

    private val _hasMedia = MutableStateFlow(false)
    val hasMedia = _hasMedia.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults = combine(
        listOf(
            _searchQuery,
            _searchQuery.debounce(500),
            _isBookmarked,
            _isDraft,
            _hasLocation,
            _hasSong,
            _hasMedia
        )
    ) { array: Array<Any> ->
        val immediateQuery = array[0] as String
        val debouncedQuery = array[1] as String

        InternalSearchState(
            query = debouncedQuery,
            isDebouncing = immediateQuery != debouncedQuery,
            bookmarked = array[2] as Boolean,
            draft = array[3] as Boolean,
            hasLocation = array[4] as Boolean,
            hasSong = array[5] as Boolean,
            hasMedia = array[6] as Boolean
        )
    }.flatMapLatest { state ->
        val isIdle = state.query.isEmpty() && !state.bookmarked && !state.draft &&
                !state.hasLocation && !state.hasSong && !state.hasMedia

        if (state.isDebouncing) {
            kotlinx.coroutines.flow.flowOf(
                SearchUiState(
                    isIdle = false,
                    isLoading = true,
                    journals = emptyList()
                )
            )
        } else if (isIdle) {
            kotlinx.coroutines.flow.flowOf(SearchUiState(isIdle = true))
        } else {
            repo.getFilteredJournals(
                query = state.query,
                isBookmarked = if (state.bookmarked) true else null,
                isDraft = if (state.draft) true else null,
                hasLocation = if (state.hasLocation) true else null,
                hasSong = if (state.hasSong) true else null,
                hasMedia = if (state.hasMedia) true else null
            ).map { journals ->
                SearchUiState(isIdle = false, isLoading = false, journals = journals)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState(isIdle = true)
    )

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleBookmarkFilter() { _isBookmarked.value = !_isBookmarked.value }
    fun toggleDraftFilter() { _isDraft.value = !_isDraft.value }
    fun toggleLocationFilter() { _hasLocation.value = !_hasLocation.value }
    fun toggleSongFilter() { _hasSong.value = !_hasSong.value }
    fun toggleMediaFilter() { _hasMedia.value = !_hasMedia.value }
}