package com.denser.june.core.domain.preferences

import com.denser.june.core.domain.model.enums.ThemeMode
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.Flow

interface ThemePreferences {
    suspend fun resetAppTheme()

    fun getThemeMode(): Flow<ThemeMode>
    suspend fun updateThemeMode(pref: ThemeMode)

    fun getSeedColorFlow(): Flow<Int>
    suspend fun updateSeedColor(newCardContent: Int)

    fun getAmoledPrefFlow(): Flow<Boolean>
    suspend fun updateAmoledPref(amoled: Boolean)

    fun getPaletteStyle(): Flow<PaletteStyle>
    suspend fun updatePaletteStyle(style: PaletteStyle)

    fun getMaterialYouFlow(): Flow<Boolean>
    suspend fun updateMaterialTheme(pref: Boolean)
}
