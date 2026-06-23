package com.denser.june.presentation.screens.settings.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import com.denser.june.core.R
import com.denser.june.core.domain.model.enums.LockType
import com.denser.june.core.domain.model.enums.ThemeMode
import com.denser.june.presentation.screens.settings.SettingsState
import com.denser.june.presentation.screens.settings.tiles.*
import java.time.format.TextStyle
import com.denser.june.core.domain.model.enums.TimeFormat

data class SettingsTriggers(
    val onDeleteAllJournals: () -> Unit = {},
    val onColorPickerClick: () -> Unit = {},
    val onLicenseClick: () -> Unit = {},
    val onMapAttributionsClick: () -> Unit = {},
    val onAboutLibrariesClick: () -> Unit = {},
    val onChangelogClick: () -> Unit = {},
    val onCheckForUpdatesClick: () -> Unit = {},
    val onAboutHeaderClick: () -> Unit = {}
)

val LocalSettingsTriggers = staticCompositionLocalOf { SettingsTriggers() }

data class SettingTile(
    val key: String,
    val title: String,
    val subtitle: (Context, SettingsState) -> String?,
    val category: String,
    val keywords: List<String> = emptyList(),
    val content: @Composable () -> Unit
)

object SettingsTileRegistry {
    @Composable
    fun getTiles(): List<SettingTile> {
        val appThemeTitle = stringResource(R.string.app_theme)
        val amoledTitle = stringResource(R.string.amoled)
        val amoledDesc = stringResource(R.string.amoled_desc)
        val materialThemeTitle = stringResource(R.string.material_theme)
        val materialThemeDesc = stringResource(R.string.material_theme_desc)
        val seedColorTitle = stringResource(R.string.seed_color)
        val seedColorDesc = stringResource(R.string.seed_color_desc)
        val aboutLibrariesTitle = stringResource(R.string.about_libraries)

        val remindersTitle = stringResource(R.string.reminders)
        val remindersDesc = stringResource(R.string.reminders_desc)
        val includeTimeTitle = stringResource(R.string.include_time)
        val includeTimeDesc = stringResource(R.string.include_time_desc)
        val timeFormatTitle = stringResource(R.string.time_format)
        val timeFormat12h = stringResource(R.string.time_format_12h)
        val timeFormat24h = stringResource(R.string.time_format_24h)
        val mapSettingsTitle = stringResource(R.string.map_settings)
        val mapSettingsDesc = stringResource(R.string.map_settings_desc)
        val markdownEditorTitle = stringResource(R.string.markdown_editor)
        val markdownEnabled = stringResource(R.string.markdown_enabled)
        val markdownDisabled = stringResource(R.string.markdown_disabled)
        val startOfWeekTitle = stringResource(R.string.start_of_week)
        val deleteAllJournalsTitle = stringResource(R.string.delete_all_journals)
        val deleteAllJournalsDesc = stringResource(R.string.delete_all_journals_desc)
        val appFontTitle = stringResource(R.string.app_font)
        val paletteSelectionTitle = stringResource(R.string.palette_selection)
        val paletteSelectionDesc = stringResource(R.string.palette_selection_desc)
        val appLockTitle = stringResource(R.string.app_lock)
        val appLockCustomPin = stringResource(R.string.app_lock_custom_pin)
        val appLockScreenLock = stringResource(R.string.app_lock_screen_lock)
        val appLockNone = stringResource(R.string.app_lock_none)
        val screenPrivacyTitle = stringResource(R.string.screen_privacy)
        val screenPrivacyDesc = stringResource(R.string.screen_privacy_desc)
        val permissionsTitle = stringResource(R.string.permissions)
        val permissionsDesc = stringResource(R.string.permissions_desc)
        val cloudSyncTitle = stringResource(R.string.cloud_sync)
        val cloudSyncDesc = stringResource(R.string.cloud_sync_desc)
        val localBackupTitle = stringResource(R.string.local_backup)
        val localBackupDesc = stringResource(R.string.local_backup_desc)
        val aboutJuneTitle = stringResource(R.string.about_june)
        val aboutJuneDesc = stringResource(R.string.about_june_desc)
        val developerProfileTitle = stringResource(R.string.developer_profile)
        val developerName = stringResource(R.string.developer_name)
        val licenseTitle = stringResource(R.string.license)
        val licenseDesc = stringResource(R.string.license_desc)
        val mapCreditsTitle = stringResource(R.string.map_credits)
        val mapCreditsDesc = stringResource(R.string.map_credits_desc)
        val changelogTitle = stringResource(R.string.changelog)
        val changelogDesc = stringResource(R.string.changelog_desc)
        val checkForUpdatesTitle = stringResource(R.string.check_for_updates)
        val checkForUpdatesDesc = stringResource(R.string.check_for_updates_desc)
        val categoryGeneral = stringResource(R.string.category_general)
        val categoryAppearance = stringResource(R.string.category_appearance)
        val categoryPrivacySecurity = stringResource(R.string.category_privacy_security)
        val categorySyncBackup = stringResource(R.string.category_sync_backup)
        val categoryAbout = stringResource(R.string.category_about)

        return remember(
            appThemeTitle, amoledTitle, amoledDesc, materialThemeTitle,
            materialThemeDesc, seedColorTitle, seedColorDesc, aboutLibrariesTitle,
            remindersTitle, remindersDesc, includeTimeTitle, includeTimeDesc,
            timeFormatTitle, timeFormat12h, timeFormat24h,
            mapSettingsTitle, mapSettingsDesc, markdownEditorTitle, markdownEnabled, markdownDisabled,
            startOfWeekTitle, deleteAllJournalsTitle, deleteAllJournalsDesc,
            appFontTitle, paletteSelectionTitle, paletteSelectionDesc,
            appLockTitle, appLockCustomPin, appLockScreenLock, appLockNone,
            screenPrivacyTitle, screenPrivacyDesc, permissionsTitle, permissionsDesc,
            cloudSyncTitle, cloudSyncDesc, localBackupTitle, localBackupDesc,
            aboutJuneTitle, aboutJuneDesc, developerProfileTitle, developerName,
            licenseTitle, licenseDesc, mapCreditsTitle, mapCreditsDesc,
            changelogTitle, changelogDesc, checkForUpdatesTitle, checkForUpdatesDesc,
            categoryGeneral, categoryAppearance, categoryPrivacySecurity, categorySyncBackup, categoryAbout
        ) {
            listOf(
                SettingTile(
                    key = "REMINDERS",
                    title = remindersTitle,
                    subtitle = { _, _ -> remindersDesc },
                    category = categoryGeneral,
                    keywords = listOf("reminder", "notification", "schedule", "alert"),
                    content = { RemindersTile() }
                ),
                SettingTile(
                    key = "INCLUDE_TIME",
                    title = includeTimeTitle,
                    subtitle = { _, _ -> includeTimeDesc },
                    category = categoryGeneral,
                    keywords = listOf("time", "include", "journal", "auto"),
                    content = { IncludeTimeTile() }
                ),
                SettingTile(
                    key = "TIME_FORMAT",
                    title = timeFormatTitle,
                    subtitle = { _, state -> if (state.timeFormat == TimeFormat.TWELVE_HOUR) timeFormat12h else timeFormat24h },
                    category = categoryGeneral,
                    keywords = listOf("time", "clock", "hour", "format", "12", "24"),
                    content = { TimeFormatTile() }
                ),
                SettingTile(
                    key = "MAP_SETTINGS",
                    title = mapSettingsTitle,
                    subtitle = { _, _ -> mapSettingsDesc },
                    category = categoryGeneral,
                    keywords = listOf("map", "maptiler", "stadia", "mapbox", "key", "style", "settings", "maptiler", "mapbox", "stadia", "carto"),
                    content = { MapSettingsTile() }
                ),
                SettingTile(
                    key = "MARKDOWN_EDITOR",
                    title = markdownEditorTitle,
                    subtitle = { _, state -> if (state.isMarkdownEnabled) markdownEnabled else markdownDisabled },
                    category = categoryGeneral,
                    keywords = listOf("markdown", "editor", "rich", "text", "format", "plain"),
                    content = { MarkdownEditorTile() }
                ),
                SettingTile(
                    key = "START_OF_WEEK",
                    title = startOfWeekTitle,
                    subtitle = { _, state -> state.startOfWeek.getDisplayName(TextStyle.FULL, java.util.Locale.getDefault()) },
                    category = categoryGeneral,
                    keywords = listOf("start", "week", "day", "calendar", "sunday", "monday"),
                    content = { StartOfWeekTile() }
                ),
                SettingTile(
                    key = "DELETE_ALL_JOURNALS",
                    title = deleteAllJournalsTitle,
                    subtitle = { _, _ -> deleteAllJournalsDesc },
                    category = categoryGeneral,
                    keywords = listOf("delete", "remove", "erase", "all", "journals", "clear"),
                    content = { DeleteAllJournalsTile() }
                ),
                SettingTile(
                    key = "APP_THEME",
                    title = appThemeTitle,
                    subtitle = { context, state -> context.getString(state.appTheme.themeMode.stringRes) },
                    category = categoryAppearance,
                    keywords = listOf("theme", "dark", "light", "mode", "amoled", "color"),
                    content = { AppThemeTile() }
                ),
                SettingTile(
                    key = "APP_FONT",
                    title = appFontTitle,
                    subtitle = { _, state -> state.appTheme.appFont },
                    category = categoryAppearance,
                    keywords = listOf("font", "typography", "text", "style", "size"),
                    content = { AppFontTile() }
                ),
                SettingTile(
                    key = "AMOLED",
                    title = amoledTitle,
                    subtitle = { _, _ -> amoledDesc },
                    category = categoryAppearance,
                    keywords = listOf("amoled", "black", "dark", "oled", "battery"),
                    content = { AmoledTile() }
                ),
                SettingTile(
                    key = "MATERIAL_THEME",
                    title = materialThemeTitle,
                    subtitle = { _, _ -> materialThemeDesc },
                    category = categoryAppearance,
                    keywords = listOf("material", "you", "dynamic", "color", "wallpaper"),
                    content = { MaterialThemeTile() }
                ),
                SettingTile(
                    key = "SEED_COLOR",
                    title = seedColorTitle,
                    subtitle = { _, _ -> seedColorDesc },
                    category = categoryAppearance,
                    keywords = listOf("seed", "color", "picker", "accent", "custom"),
                    content = { SeedColorTile() }
                ),
                SettingTile(
                    key = "PALETTE_SELECTION",
                    title = paletteSelectionTitle,
                    subtitle = { _, _ -> paletteSelectionDesc },
                    category = categoryAppearance,
                    keywords = listOf("palette", "style", "theme", "tonal", "scheme"),
                    content = { PaletteSelectionSettingsItem() }
                ),
                SettingTile(
                    key = "APP_LOCK",
                    title = appLockTitle,
                    subtitle = { _, state ->
                        if (state.isAppLockEnabled) {
                            if (state.lockType == LockType.PIN) appLockCustomPin else appLockScreenLock
                        } else {
                            appLockNone
                        }
                    },
                    category = categoryPrivacySecurity,
                    keywords = listOf("lock", "security", "pin", "biometric", "password", "privacy"),
                    content = { AppLockTile() }
                ),
                SettingTile(
                    key = "SCREEN_PRIVACY",
                    title = screenPrivacyTitle,
                    subtitle = { _, _ -> screenPrivacyDesc },
                    category = categoryPrivacySecurity,
                    keywords = listOf("screenshot", "privacy", "screen", "recents", "secure"),
                    content = { ScreenPrivacyTile() }
                ),
                SettingTile(
                    key = "PERMISSIONS",
                    title = permissionsTitle,
                    subtitle = { _, _ -> permissionsDesc },
                    category = categoryPrivacySecurity,
                    keywords = listOf("permission", "location", "notification", "internet", "gps"),
                    content = { PermissionsTile() }
                ),
                SettingTile(
                    key = "CLOUD_SYNC",
                    title = cloudSyncTitle,
                    subtitle = { _, _ -> cloudSyncDesc },
                    category = categorySyncBackup,
                    keywords = listOf("cloud", "sync", "google", "drive", "backup", "webdav"),
                    content = { CloudSyncTile() }
                ),
                SettingTile(
                    key = "LOCAL_BACKUP",
                    title = localBackupTitle,
                    subtitle = { _, _ -> localBackupDesc },
                    category = categorySyncBackup,
                    keywords = listOf("backup", "restore", "export", "import", "local", "json"),
                    content = { LocalBackupTile() }
                ),
                SettingTile(
                    key = "ABOUT_HEADER",
                    title = aboutJuneTitle,
                    subtitle = { _, _ -> aboutJuneDesc },
                    category = categoryAbout,
                    keywords = listOf("about", "version", "github", "developer", "author"),
                    content = { AboutHeaderTile() }
                ),
                SettingTile(
                    key = "DEVELOPER",
                    title = developerProfileTitle,
                    subtitle = { _, _ -> developerName },
                    category = categoryAbout,
                    keywords = listOf("developer", "author", "meerkat", "denser", "github", "email"),
                    content = { DeveloperTile() }
                ),
                SettingTile(
                    key = "LICENSE",
                    title = licenseTitle,
                    subtitle = { _, _ -> licenseDesc },
                    category = categoryAbout,
                    keywords = listOf("license", "gpl", "open", "source", "terms"),
                    content = { LicenseTile() }
                ),
                SettingTile(
                    key = "ABOUT_LIBRARIES",
                    title = aboutLibrariesTitle,
                    subtitle = { _, _ -> aboutLibrariesTitle },
                    category = categoryAbout,
                    keywords = listOf("libraries", "licenses", "open", "source", "dependency"),
                    content = { AboutLibrariesTile() }
                ),
                SettingTile(
                    key = "MAP_CREDITS",
                    title = mapCreditsTitle,
                    subtitle = { _, _ -> mapCreditsDesc },
                    category = categoryAbout,
                    keywords = listOf("map", "attributions", "licenses", "credits", "osm", "maptiler", "mapbox", "stadia", "carto"),
                    content = { MapCreditsTile() }
                ),
                SettingTile(
                    key = "CHANGELOG",
                    title = changelogTitle,
                    subtitle = { _, _ -> changelogDesc },
                    category = categoryAbout,
                    keywords = listOf("changelog", "release", "history", "notes", "version", "updates"),
                    content = { ChangelogTile() }
                ),
                SettingTile(
                    key = "CHECK_FOR_UPDATES",
                    title = checkForUpdatesTitle,
                    subtitle = { _, _ -> checkForUpdatesDesc },
                    category = categoryAbout,
                    keywords = listOf("update", "check", "version", "github", "playstore", "latest"),
                    content = { CheckForUpdatesTile() }
                ),
            )
        }
    }

    @Composable
    fun getTilesForCategory(category: String): List<SettingTile> {
        return getTiles().filter { it.category == category }
    }

    @Composable
    fun getTile(key: String): SettingTile? {
        return getTiles().find { it.key == key }
    }
}
