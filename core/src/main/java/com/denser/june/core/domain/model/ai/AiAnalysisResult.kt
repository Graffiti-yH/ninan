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
    val riskWarning: String? = null,
    /** Per-entry mood timeline for chart display */
    val moodTimeline: List<MoodEntry> = emptyList(),
    /** Inferred MBTI personality type, e.g. "INFP", "ENFJ" */
    val personalityType: String = "",
    /** Personality analysis summary */
    val personalitySummary: String = "",
    /** Jungian cognitive function scores (荣格八维) for radar chart */
    val personalityDimensions: List<JungianDimension> = emptyList()
)

/**
 * Mood entry for a single diary entry, used to render the mood line chart.
 */
@Serializable
data class MoodEntry(
    /** Date string in ISO format, e.g. "2024-01-15" */
    val date: String,
    /** Mood label, e.g. "高兴", "焦虑", "平静", "低落" */
    val mood: String,
    /** Numeric score 1-10 (1=worst, 10=best) for chart rendering */
    val score: Int,
    /** Emoji associated with the entry, if any */
    val emoji: String? = null
)

/**
 * A single dimension of the Jungian cognitive function (荣格八维).
 * Score ranges from 1-10 indicating how prominently this function manifests.
 */
@Serializable
data class JungianDimension(
    /** Function abbreviation, e.g. "Fi", "Fe", "Ti", "Te", "Ni", "Ne", "Si", "Se" */
    val key: String,
    /** Display name in the response language, e.g. "内倾情感", "Introverted Feeling" */
    val name: String,
    /** Score 1-10 indicating prominence */
    val score: Int,
    /** Brief explanation of how this function manifests in the user */
    val description: String = ""
)
