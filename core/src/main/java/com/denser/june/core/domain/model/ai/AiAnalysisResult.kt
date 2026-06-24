package com.denser.june.core.domain.model.ai

import kotlinx.serialization.Serializable

/**
 * Structured result from AI analysis of journal entries.
 * Designed around CBT (Cognitive Behavioral Therapy) principles.
 */
@Serializable
data class AiAnalysisResult(
    /** Overall mood summary phrase, e.g. "Anxious but hopeful" */
    val overallMood: String = "",
    /** Trend direction: "improving", "stable", "declining" */
    val moodTrend: String = "stable",
    /** Detailed emotional state summary (1-3 paragraphs) */
    val emotionSummary: String = "",
    /** Recurring themes or topics found in the entries */
    val keyThemes: List<String> = emptyList(),
    /** Observed emotional or behavioral patterns */
    val emotionalPatterns: List<String> = emptyList(),
    /** Identified cognitive distortions (e.g. catastrophizing, black-and-white thinking) */
    val cognitiveDistortions: List<String> = emptyList(),
    /** Actionable suggestions for improvement */
    val suggestions: List<String> = emptyList(),
    /** Positive aspects or strengths noticed */
    val positiveHighlights: List<String> = emptyList(),
    /** Risk warning (e.g. signs of depression, anxiety) — null if no concern */
    val riskWarning: String? = null
)
