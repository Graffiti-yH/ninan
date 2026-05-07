package com.denser.june

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denser.june.core.domain.preferences.PrivacyPreferences
import com.denser.june.core.domain.preferences.ThemePreferences
import com.denser.june.core.domain.preferences.FontPreferences
import com.denser.june.core.domain.model.AppTheme
import com.denser.june.core.domain.preferences.SyncPreferences
import com.denser.june.core.domain.sync.SyncManager
import com.denser.june.core.domain.sync.SyncStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AppState(
    val appTheme: AppTheme = AppTheme(),
    val isAppLockEnabled: Boolean = false,
    val isLoading: Boolean = true,
    val syncStatus: SyncStatus = SyncStatus.Idle,
    val isSyncEnabled: Boolean = false,
    val isInternetAllowed: Boolean = true
)

class MainVM(
    themePrefs: ThemePreferences,
    privacyPrefs: PrivacyPreferences,
    fontPrefs: FontPreferences,
    syncManager: SyncManager,
    syncPrefs: SyncPreferences
) : ViewModel() {

    private val themeFlow = combine(
        themePrefs.getSeedColorFlow(),
        themePrefs.getThemeMode(),
        themePrefs.getAmoledPrefFlow(),
        themePrefs.getPaletteStyle(),
        themePrefs.getMaterialYouFlow()
    ) { seed, themeMode, amoled, style, matYou ->
        AppTheme(
            seedColor = seed,
            themeMode = themeMode,
            withAmoled = amoled,
            style = style,
            materialTheme = matYou
        )
    }

    val state = combine(
        themeFlow,
        fontPrefs.getAppFont(),
        privacyPrefs.getAppLockFlow(),
        syncManager.status,
        syncPrefs.getSyncEnabled(),
        privacyPrefs.getIsInternetAllowedFlow()
    ) { args: Array<Any?> ->
        val baseTheme = args[0] as AppTheme

        AppState(
            appTheme = baseTheme.copy(appFont = args[1] as String),
            isAppLockEnabled = args[2] as Boolean,
            isLoading = false,
            syncStatus = args[3] as SyncStatus,
            isSyncEnabled = args[4] as Boolean,
            isInternetAllowed = args[5] as Boolean
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppState()
    )
}