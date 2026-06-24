package com.denser.june.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.denser.june.core.domain.preferences.AiPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AiPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : AiPreferences {

    companion object {
        private val KEY_AI_API_URL = stringPreferencesKey("ai_api_url")
        private val KEY_AI_API_KEY = stringPreferencesKey("ai_api_key")
        private val KEY_AI_MODEL = stringPreferencesKey("ai_model")
    }

    override fun getApiUrl(): Flow<String> = dataStore.data
        .map { it[KEY_AI_API_URL] ?: DEFAULT_API_URL }

    override suspend fun setApiUrl(url: String) {
        dataStore.edit { it[KEY_AI_API_URL] = url }
    }

    override fun getApiKey(): Flow<String> = dataStore.data
        .map { it[KEY_AI_API_KEY] ?: "" }

    override suspend fun setApiKey(key: String) {
        dataStore.edit { it[KEY_AI_API_KEY] = key }
    }

    override fun getModel(): Flow<String> = dataStore.data
        .map { it[KEY_AI_MODEL] ?: DEFAULT_MODEL }

    override suspend fun setModel(model: String) {
        dataStore.edit { it[KEY_AI_MODEL] = model }
    }

    override suspend fun isConfigured(): Boolean {
        val prefs = dataStore.data.first()
        val url = prefs[KEY_AI_API_URL].orEmpty()
        val key = prefs[KEY_AI_API_KEY].orEmpty()
        return url.isNotBlank() && key.isNotBlank()
    }
}

const val DEFAULT_API_URL = "https://api.deepseek.com/v1"
const val DEFAULT_MODEL = "deepseek-chat"
