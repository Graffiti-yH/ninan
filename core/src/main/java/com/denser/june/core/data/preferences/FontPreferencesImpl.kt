package com.denser.june.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.denser.june.core.domain.preferences.FontPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FontPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : FontPreferences {

    companion object {
        private val appFont = stringPreferencesKey("app_font")
        private const val DEFAULT_FONT = "Google Sans Flex"
    }

    override fun getAppFont(): Flow<String> = dataStore.data.map { it[appFont] ?: DEFAULT_FONT }

    override suspend fun updateAppFont(fontName: String) {
        dataStore.edit { it[appFont] = fontName }
    }
}
