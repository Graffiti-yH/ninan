package com.denser.june.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.denser.june.core.domain.preferences.ThemePreferences
import com.denser.june.core.domain.model.enums.ThemeMode
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : ThemePreferences {

    override suspend fun resetAppTheme() {
        dataStore.edit { preferences ->
            preferences[seedColor] = -1
            preferences[amoledPref] = false
            preferences[paletteStyle] = PaletteStyle.TonalSpot.name
            preferences[materialTheme] = false
        }
    }

    companion object {
        private val seedColor = intPreferencesKey("seed_color")
        private val appTheme = stringPreferencesKey("app_theme")
        private val amoledPref = booleanPreferencesKey("with_amoled")
        private val paletteStyle = stringPreferencesKey("palette_style")
        private val materialTheme = booleanPreferencesKey("material_theme")
    }

    override fun getThemeMode(): Flow<ThemeMode> = dataStore.data
        .map { preferences ->
            val theme = preferences[appTheme] ?: ThemeMode.SYSTEM.name
            ThemeMode.valueOf(theme)
        }

    override suspend fun updateThemeMode(pref: ThemeMode) {
        dataStore.edit {
            it[appTheme] = pref.name
        }
    }

    override fun getSeedColorFlow(): Flow<Int> = dataStore.data
        .map { preferences -> preferences[seedColor] ?: -1 }

    override suspend fun updateSeedColor(newCardContent: Int) {
        dataStore.edit { settings ->
            settings[seedColor] = newCardContent
        }
    }

    override fun getAmoledPrefFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[amoledPref] == true }

    override suspend fun updateAmoledPref(amoled: Boolean) {
        dataStore.edit { settings ->
            settings[amoledPref] = amoled
        }
    }

    override fun getPaletteStyle(): Flow<PaletteStyle> = dataStore.data
        .map { preferences ->
            PaletteStyle.valueOf(preferences[paletteStyle] ?: PaletteStyle.TonalSpot.name)
        }

    override suspend fun updatePaletteStyle(style: PaletteStyle) {
        dataStore.edit { settings ->
            settings[paletteStyle] = style.name
        }
    }

    override fun getMaterialYouFlow(): Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[materialTheme] == true }

    override suspend fun updateMaterialTheme(pref: Boolean) {
        dataStore.edit { settings ->
            settings[materialTheme] = pref
        }
    }
}
