package com.denser.june.core.domain.preferences

import kotlinx.coroutines.flow.Flow

interface AiPreferences {
    /** OpenAI-compatible API base URL (e.g. https://api.openai.com/v1) */
    fun getApiUrl(): Flow<String>
    suspend fun setApiUrl(url: String)

    /** API key / token */
    fun getApiKey(): Flow<String>
    suspend fun setApiKey(key: String)

    /** Model name (e.g. gpt-4o-mini, claude-3-haiku) */
    fun getModel(): Flow<String>
    suspend fun setModel(model: String)

    /** Whether AI feature has been configured (has key + url) */
    suspend fun isConfigured(): Boolean
}
