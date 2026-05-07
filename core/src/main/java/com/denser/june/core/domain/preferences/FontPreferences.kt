package com.denser.june.core.domain.preferences

import kotlinx.coroutines.flow.Flow

interface FontPreferences {
    fun getAppFont(): Flow<String>

    suspend fun updateAppFont(fontName: String)
}
