package com.denser.june.core.domain.repository

import com.denser.june.core.domain.model.Journal
import com.denser.june.core.domain.model.ai.AiAnalysisResult

interface AiRepository {
    /**
     * Analyze journal entries and return a structured psychological analysis.
     * @param journals list of journal entries to analyze
     * @param languageHint preferred response language ("auto" detects from content)
     */
    suspend fun analyzeJournals(
        journals: List<Journal>,
        languageHint: String = "auto"
    ): Result<AiAnalysisResult>
}
