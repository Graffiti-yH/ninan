package com.denser.june.core.data.remote

import kotlinx.serialization.Serializable

/**
 * DTOs for the OpenAI-compatible chat completions API.
 * Requests are sent via OkHttp directly (base URL is user-configurable).
 */
@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 4096
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<Choice>,
    val usage: Usage? = null
)

@Serializable
data class Choice(
    val message: ChatMessage,
    val finish_reason: String? = null,
    val index: Int = 0
)

@Serializable
data class Usage(
    val prompt_tokens: Int = 0,
    val completion_tokens: Int = 0,
    val total_tokens: Int = 0
)
