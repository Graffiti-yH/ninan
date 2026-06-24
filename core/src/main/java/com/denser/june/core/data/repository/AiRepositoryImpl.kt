package com.denser.june.core.data.repository

import com.denser.june.core.data.remote.ChatCompletionRequest
import com.denser.june.core.data.remote.ChatCompletionResponse
import com.denser.june.core.data.remote.ChatMessage
import com.denser.june.core.domain.model.Journal
import com.denser.june.core.domain.model.ai.AiAnalysisResult
import com.denser.june.core.domain.preferences.AiPreferences
import com.denser.june.core.domain.repository.AiRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.UnknownHostException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AiRepositoryImpl(
    private val okHttpClient: OkHttpClient,
    private val aiPreferences: AiPreferences,
    private val json: Json
) : AiRepository {

    companion object {
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()

        private val SYSTEM_PROMPT = buildString {
            appendLine("You are an empathetic psychological analysis assistant specializing in CBT (Cognitive Behavioral Therapy).")
            appendLine()
            appendLine("Analyze the user's diary/journal entries and provide structured feedback.")
            appendLine()
            appendLine("## Core Rules")
            appendLine("- Be warm, empathetic, supportive, and non-judgmental")
            appendLine("- Focus on recurring patterns, not isolated events")
            appendLine("- Identify cognitive distortions when evident (catastrophizing, black-and-white thinking, overgeneralization, personalization, should-statements, emotional reasoning, labeling, mind-reading)")
            appendLine("- Offer constructive, actionable suggestions grounded in CBT principles")
            appendLine("- Never diagnose medical conditions — use tentative, supportive language")
            appendLine("- If you detect signs of serious distress (self-harm, suicidal ideation), set riskWarning")
            appendLine("- ALWAYS respond in the SAME LANGUAGE as the diary entries (e.g. Chinese entries → respond in Chinese)")
            appendLine()
            appendLine("## CRITICAL: Output ONLY valid JSON. No markdown, no explanation, no code fences.")
            appendLine("Use this exact JSON structure (no trailing commas):")
            appendLine("""{
  "overallMood": "Brief mood summary in 2-5 words",
  "moodTrend": "improving",
  "emotionSummary": "2-3 paragraph detailed emotional analysis",
  "keyThemes": ["theme1", "theme2"],
  "emotionalPatterns": ["pattern1", "pattern2"],
  "cognitiveDistortions": ["distortion1", "distortion2"],
  "suggestions": ["suggestion1", "suggestion2"],
  "positiveHighlights": ["positive1", "positive2"],
  "riskWarning": null
}""")
            appendLine()
            appendLine("moodTrend must be exactly one of: \"improving\", \"stable\", \"declining\"")
            appendLine("Set riskWarning to null if no serious concerns. Only populate it when you detect signs of self-harm, suicidal ideation, or severe distress.")
            appendLine("Return PURE JSON only — no markdown formatting, no code fences, no extra text.")
        }
    }

    override suspend fun analyzeJournals(
        journals: List<Journal>,
        languageHint: String
    ): Result<AiAnalysisResult> {
        if (journals.isEmpty()) {
            return Result.failure(IllegalArgumentException("No journal entries to analyze"))
        }

        return try {
            val prefs = loadPreferences()
            if (prefs.apiKey.isBlank()) {
                return Result.failure(IllegalStateException(
                    "AI API key is not configured. Please set it in Settings → AI."
                ))
            }

            val userMessage = buildUserMessage(journals)

            val requestBody = ChatCompletionRequest(
                model = prefs.model,
                messages = listOf(
                    ChatMessage(role = "system", content = SYSTEM_PROMPT),
                    ChatMessage(role = "user", content = userMessage)
                )
            )

            val bodyJson = json.encodeToString(ChatCompletionRequest.serializer(), requestBody)

            val httpRequest = Request.Builder()
                .url("${prefs.apiUrl.trimEnd('/')}/chat/completions")
                .addHeader("Authorization", "Bearer ${prefs.apiKey}")
                .addHeader("Content-Type", "application/json")
                .post(bodyJson.toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = okHttpClient.newCall(httpRequest).await()
            val responseBody = response.body?.string()
                ?: return Result.failure(Exception("Empty response from AI API"))

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception("AI API error (${response.code}): ${responseBody.take(200)}")
                )
            }

            // Parse the OpenAI-style response first
            val chatResponse = json.decodeFromString(
                ChatCompletionResponse.serializer(),
                responseBody
            )

            val content = chatResponse.choices.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("No choices in AI response"))

            // Parse the inner JSON structure into AiAnalysisResult
            val analysisResult = json.decodeFromString(
                AiAnalysisResult.serializer(),
                content
            )

            Result.success(analysisResult)

        } catch (e: kotlinx.serialization.SerializationException) {
            Result.failure(Exception("Failed to parse AI response: ${e.message}", e))
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Network error: Unable to reach the AI service. Check your API URL and internet connection.", e))
        } catch (e: Exception) {
            Result.failure(Exception("AI analysis failed: ${e.message}", e))
        }
    }

    private data class AiPrefsSnapshot(
        val apiUrl: String,
        val apiKey: String,
        val model: String
    )

    private suspend fun loadPreferences(): AiPrefsSnapshot {
        return AiPrefsSnapshot(
            apiUrl = aiPreferences.getApiUrl().first(),
            apiKey = aiPreferences.getApiKey().first(),
            model = aiPreferences.getModel().first()
        )
    }

    private fun buildUserMessage(journals: List<Journal>): String {
        val sb = StringBuilder()
        sb.appendLine("Please analyze the following diary entries and provide the structured analysis in the specified JSON format.")
        sb.appendLine()

        // Sort by date
        val sorted = journals.sortedBy { it.dateTime }
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault())

        for ((index, journal) in sorted.withIndex()) {
            val dateStr = try {
                dateFormatter.format(Instant.ofEpochMilli(journal.dateTime))
            } catch (_: Exception) {
                "unknown-date"
            }

            sb.appendLine("--- Entry ${index + 1} ($dateStr) ---")
            if (journal.title.isNotBlank()) {
                sb.appendLine("Title: ${journal.title}")
            }
            if (journal.emoji != null) {
                sb.appendLine("Mood: ${journal.emoji}")
            }
            sb.appendLine(journal.content)
            sb.appendLine()
        }

        return sb.toString().trim()
    }
}

private suspend fun okhttp3.Call.await(): Response {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: Response) {
                continuation.resume(value = response)
            }

            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                if (continuation.isCancelled) return
                continuation.resumeWithException(e)
            }
        })
        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (_: Exception) {
                // Ignore
            }
        }
    }
}
