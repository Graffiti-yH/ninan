package com.denser.june.presentation.utils

import android.content.Context
import com.denser.june.core.domain.preferences.PrivacyPreferences
import com.denser.june.presentation.screens.settings.components.VersionEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import org.json.JSONArray

class StartupManager(
    private val context: Context,
    private val privacyPrefs: PrivacyPreferences
) {
    private val _pendingWhatsChanged = MutableStateFlow<VersionEntry?>(null)
    val pendingWhatsChanged: Flow<VersionEntry?> = _pendingWhatsChanged

    suspend fun checkStartupFlows() {
        val latestEntry = try {
            val jsonString = context.assets.open("changelog.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            if (jsonArray.length() > 0) {
                val obj = jsonArray.getJSONObject(0)
                val version = obj.getString("version")
                val changesArray = obj.getJSONArray("changes")
                val changesList = mutableListOf<String>()
                for (j in 0 until changesArray.length()) {
                    changesList.add(changesArray.getString(j))
                }
                VersionEntry(version, changesList)
            } else null
        } catch (e: Exception) {
            null
        }

        if (latestEntry != null) {
            val lastShown = privacyPrefs.getLastChangelogShownFlow().first()
            if (lastShown != latestEntry.version) {
                _pendingWhatsChanged.value = latestEntry
            }
        }
    }

    suspend fun dismissWhatsChanged(version: String) {
        privacyPrefs.updateLastChangelogShown(version)
        _pendingWhatsChanged.value = null
    }
}
