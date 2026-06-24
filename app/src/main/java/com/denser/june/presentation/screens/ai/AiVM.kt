package com.denser.june.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denser.june.core.domain.model.ai.AiAnalysisResult
import com.denser.june.core.domain.repository.AiRepository
import com.denser.june.core.domain.repository.JournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

data class AiUiState(
    val analysisState: AnalysisState = AnalysisState.Idle,
    val selectedRange: AiDateRange = AiDateRange.THIS_MONTH,
    val journalCount: Int = 0,
    val errorMessage: String? = null
)

sealed interface AnalysisState {
    data object Idle : AnalysisState
    data object Loading : AnalysisState
    data class Success(val result: AiAnalysisResult) : AnalysisState
    data class Error(val message: String) : AnalysisState
}

enum class AiDateRange(val labelRes: Int) {
    THIS_WEEK(com.denser.june.core.R.string.ai_range_this_week),
    THIS_MONTH(com.denser.june.core.R.string.ai_range_this_month),
    LAST_3_MONTHS(com.denser.june.core.R.string.ai_range_last_3_months),
    ALL(com.denser.june.core.R.string.ai_range_all)
}

class AiVM(
    private val journalRepository: JournalRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AiUiState())
    val state: StateFlow<AiUiState> = _state.asStateFlow()

    fun onSelectRange(range: AiDateRange) {
        _state.update { it.copy(selectedRange = range) }
    }

    fun analyze() {
        val range = _state.value.selectedRange
        val (startDate, endDate) = getDateRange(range)

        _state.update {
            it.copy(
                analysisState = AnalysisState.Loading,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val journals = withContext(Dispatchers.IO) {
                    if (startDate != null && endDate != null) {
                        journalRepository.getJournalsByDateRange(startDate, endDate).first()
                    } else {
                        journalRepository.getAllJournals()
                    }
                }

                if (journals.isEmpty()) {
                    _state.update {
                        it.copy(
                            analysisState = AnalysisState.Error(
                                "No journal entries found in the selected date range."
                            ),
                            journalCount = 0
                        )
                    }
                    return@launch
                }

                _state.update { it.copy(journalCount = journals.size) }

                val result = withContext(Dispatchers.IO) {
                    aiRepository.analyzeJournals(journals)
                }

                result.fold(
                    onSuccess = { analysis ->
                        _state.update {
                            it.copy(analysisState = AnalysisState.Success(analysis))
                        }
                    },
                    onFailure = { error ->
                        _state.update {
                            it.copy(
                                analysisState = AnalysisState.Error(
                                    error.message ?: "Unknown error occurred"
                                )
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        analysisState = AnalysisState.Error(
                            e.message ?: "An unexpected error occurred"
                        )
                    )
                }
            }
        }
    }

    fun reset() {
        _state.update {
            it.copy(
                analysisState = AnalysisState.Idle,
                journalCount = 0,
                errorMessage = null
            )
        }
    }

    private fun getDateRange(range: AiDateRange): Pair<Long?, Long?> {
        val today = LocalDate.now(ZoneId.systemDefault())
        val startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
        val endOfToday = startOfToday + 86400000L

        return when (range) {
            AiDateRange.THIS_WEEK -> {
                val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val start = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
                start to endOfToday
            }
            AiDateRange.THIS_MONTH -> {
                val startOfMonth = today.withDayOfMonth(1)
                val start = startOfMonth.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
                start to endOfToday
            }
            AiDateRange.LAST_3_MONTHS -> {
                val threeMonthsAgo = today.minusMonths(3)
                val start = threeMonthsAgo.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000
                start to endOfToday
            }
            AiDateRange.ALL -> null to null
        }
    }
}
